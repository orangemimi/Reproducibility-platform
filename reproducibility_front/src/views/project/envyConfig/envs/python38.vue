<template>
  <div class="python38">
    <div class="workDirectory">
      <h2>Work Directory</h2>

      <div class="toolbox">
        <!-- 按钮一 -->
        <el-tooltip
          content="点击上传代码、数据等文件"
          placement="top"
          :open-delay="500"
        >
          <el-button
            type="primary"
            :icon="Upload"
            circle
            @click="openFileInput"
            class="uploadButton"
          ></el-button>
        </el-tooltip>
        <input
          ref="hiddenFileInput"
          type="file"
          multiple
          style="display: none"
          @change="handleFileChange"
        />

        <!-- 按钮二 -->
        <el-tooltip
          content="识别代码生成默认依赖库"
          placement="top"
          :open-delay="500"
        >
          <el-button
            type="warning"
            :icon="SetUp"
            circle
            @click="createDependencyDialog = true"
            class="uploadButton"
          />
        </el-tooltip>
        <el-dialog
          title="Are you sure you want to create a dependency file?"
          v-model="createDependencyDialog"
          width="30%"
        >
          <span>
            Automatically identify potential installed dependency libraries
            based on the code you upload, and you can manually modify or add
            version information later
          </span>
          <template v-slot:footer>
            <span class="dialog-footer">
              <el-button type="primary" @click="confirmAction">
                Confirm
              </el-button>
              <el-button @click="createDependencyDialog = false">
                Cancel
              </el-button>
            </span>
          </template>
        </el-dialog>

        <!-- 按钮三 -->
        <!-- <el-tooltip
          content="查阅与编辑选中的文件"
          placement="top"
          :open-delay="500"
        >
          <el-button
            type="danger"
            :icon="Edit"
            circle
            @click="editorFile"
            :disabled="isEdit"
          />
        </el-tooltip> -->

        <!-- 按钮四 -->
        <el-tooltip content="保存编辑内容" placement="top" :open-delay="500">
          <el-button
            type="success"
            :icon="FolderChecked"
            circle
            :disabled="!isEdit"
            @click="saveEditedFile"
          />
        </el-tooltip>
      </div>

      <el-divider style="margin: 10px 0"></el-divider>

      <el-tree
        :data="treeData"
        :props="defaultProps"
        @node-click="handleNodeClick"
        node-key="label"
        :default-expanded-keys="['workDirectory']"
      >
        <template #default="{ node, data }">
          <el-icon v-if="data.directory"><Folder /></el-icon>
          <el-icon v-else><Tickets /></el-icon>
          <el-tooltip
            class="item"
            effect="light"
            :content="data.label"
            placement="top-start"
          >
            <span>{{ node.label }}</span>
          </el-tooltip>
        </template>
      </el-tree>
    </div>

    <div class="controller">
      <div class="terminal">
        <div class="terminalTools">
          <el-button
            :icon="ShoppingTrolley"
            type="info"
            link
            class="textButton"
            @click="filesToImage"
          >
            build image
          </el-button>
          <el-button
            :icon="Box"
            type="info"
            link
            class="textButton"
            @click="runTheContainer"
          >
            run container
          </el-button>
          <el-button
            :icon="FolderChecked"
            type="info"
            link
            class="textButton"
            @click="endExecution"
          >
            end of execution
          </el-button>
          <el-button
            :icon="FolderChecked"
            type="info"
            link
            class="textButton"
            @click="test"
          >
            test
          </el-button>
        </div>
        <div class="consoler">
          <codeEditor
            :language="'python'"
            :value="codeMirrorInstance"
            style="height: 100%"
            @changed="changeTextarea"
          ></codeEditor>
        </div>
      </div>
      <div class="operationRecord">operationRecord</div>
    </div>
  </div>
</template>

<script setup lang="ts">
// @ts-ignore
import {
  Upload,
  FolderChecked,
  SetUp,
  // Edit,
  Box,
  ShoppingTrolley,
} from "@element-plus/icons-vue";
import { Ref, onMounted, ref } from "vue";
import {
  getDirectory,
  uploadDataAndCode,
  createDependency,
  editUploadedFile,
  SaveFileContent,
  buildImage,
  runContainer,
  overReproTask,
  // @ts-ignore
} from "@/api/request.js";
import { ElMessage, ElMessageBox } from "element-plus";
// @ts-ignore
import codeEditor from "_com/codingOL/CommonEditor.vue";

/**
 * area-left
 * 左侧工作目录所需要包含的代码
 */

// 树形控件所需要的数据及对应设置
// 树形控件所需要的数据及对应设置
// 树形控件所需要的数据及对应设置
interface Tree {
  label: string;
  children: Tree[];
  generation: string[];
  url: string;
}

// interface DataItem {
//   name: string;
//   url: string;
// }

const defaultProps = {
  children: "children",
  label: "label",
};

const nodeSelected = ref();
// 点击节点触发的事件
const handleNodeClick = (data: Tree) => {
  nodeSelected.value = data;

  if (!nodeSelected.value.directory) {
    if (judgeEditState()) {
      ElMessage.warning("Please save the edited content first");
      return;
    }
    editorFile();
  }
  // console.log(nodeSelected.value);
};

const treeData: Ref<Tree[]> = ref([] as Tree[]);

// 从后端获取和格式化工作目录的方法
const getAndFormatDirectory = async () => {
  const response = await getDirectory();

  treeData.value = formatDirectory(response);
  console.log(treeData.value, "treeData.value");
};

// 格式化后端工作目录的方法
const formatDirectory = (data: any, n = []) => {
  return data
    .map((item: any) => {
      // 这里n和newArr都是为了将文件的层级以及在哪些文件夹目录下保留下来，方便读取和编辑
      if (item.directory == true && item.name != "workDirectory") {
        var newArr: any = [...n];
        newArr.push(item.name);
      }
      return {
        label: item.name,
        children: item.children ? formatDirectory(item.children, newArr) : [],
        directory: item.directory,
        generation: n,
        url: item.url,
      };
    })
    .sort((a: any, b: any) => {
      // 确保文件夹在前，文件在后
      if (a.directory && !b.directory) return -1;
      if (!a.directory && b.directory) return 1;
      return a.label.localeCompare(b.label);
    });
};

// button1、处理上传逻辑
// button1、处理上传逻辑
// button1、处理上传逻辑
const hiddenFileInput = ref(null);
const openFileInput = () => {
  if (judgeEditState()) {
    ElMessage.warning("Please save the edited content first");
    return;
  }

  if (nodeSelected.value && nodeSelected.value.directory) {
    if (hiddenFileInput.value) {
      (hiddenFileInput.value as HTMLInputElement).click();
    }
  } else {
    ElMessage.warning("Please select which folder to upload to");
    return;
  }
};

// 处理上传的文件，上传后给状态提示
const handleFileChange = async (event: Event) => {
  const files = (event.target as HTMLInputElement).files;
  if (!files?.length) return;

  const formData = new FormData();
  var filePath = getFileDirectory();

  formData.append("path", filePath);
  for (let i = 0; i < files.length; i++) {
    // if (filePath == "//data") {
    //   // incomplete 上传数据容器
    //   // 记录输入数据
    //   var data: DataItem = {
    //     name: files[i].name,
    //     url: "123",
    //   };

    //   dataFileListBefore.value.push(data);
    // }

    formData.append("files", files[i]);
  }

  try {
    const uploadDataResponse = await uploadDataAndCode(formData);
    treeData.value = formatDirectory(uploadDataResponse);
    ElMessage.success("Files uploaded successful");
  } catch (error: any) {
    ElMessage.error("Failed to upload files", error);
  }
};

//  button2、生成依赖库
//  button2、生成依赖库
//  button2、生成依赖库
const createDependencyDialog = ref(false);
const confirmAction = () => {
  if (judgeEditState()) {
    ElMessage.warning("Please save the edited content first");
    return;
  }
  // 在这里调用您的 API 并处理结果
  createDependency()
    .then((data: any) => {
      ElMessage.success("Dependency file created successfully");
      createDependencyDialog.value = false;
      treeData.value = formatDirectory(data);
    })
    .catch((error: any) => {
      codeMirrorInstance.value = error;
      ElMessage.error("Dependency file creation failed");
      createDependencyDialog.value = false;
    });
};

// button3、编辑文件
// button3、编辑文件
// button3、编辑文件
// 正在编辑的路径
const editingFilePath = ref("");
// 可修改的后缀集合
const supportFileList = ref([".py", "/requirements.txt"] as string[]);
const editorFile = async () => {
  if (nodeSelected.value == undefined) {
    ElMessage.error("Please select the file you want to edit first");
    return;
  }
  if (nodeSelected.value.directory) {
    ElMessage.error("Unable to edit folder");
    // ElMessage.error("请选择正确的文件进行编辑");
    return;
  }

  // 获取文件所在的目录
  var filePath = getFileDirectory();
  editingFilePath.value = filePath;
  var formData = new FormData();
  formData.append("filePath", filePath);

  codeMirrorInstance.value = (await editUploadedFile(
    formData
  )) as unknown as string;
  isEdit.value = true;

  // 做一个判断，只有特定后缀的文件才能修改
  if (
    !supportFileList.value.some((ext) => filePath.toLowerCase().endsWith(ext))
  ) {
    ElMessage.warning(
      "Only code can and requirement be modified, other files can only be viewed or copied"
    );
    isEdit.value = false;
    editingFilePath.value = "";
  }
};
// 判断是否还在编辑状态
const judgeEditState = () => {
  if (editingFilePath.value) {
    return true;
  }
};
// 得到工作目录中的完整路径
const getFileDirectory = () => {
  var filePath = "";
  nodeSelected.value.generation.forEach((element: string) => {
    filePath += "//" + element;
  });
  if (nodeSelected.value.label != "workDirectory") {
    filePath += "//" + nodeSelected.value.label;
  } else {
    return "";
  }
  return filePath;
};

// button4、保存编辑内容
// button4、保存编辑内容
// button4、保存编辑内容
const isEdit = ref(false);

const saveEditedFile = async () => {
  var formData = new FormData();
  // console.log(editingFilePath.value, codeMirrorInstance.value.length, "101");
  var tempCode = codeMirrorInstance.value;
  formData.append("filePath", editingFilePath.value);
  formData.append("fileContent", codeMirrorInstance.value);
  await SaveFileContent(formData);
  isEdit.value = false;
  // 清除选中的文件
  editingFilePath.value = "";
  codeMirrorInstance.value = tips.value;
  ElMessage.success("Save successful!");
  return tempCode;
};

/**
 * area-right-top
 * 右侧上方terminal区域
 */
// TEXT - BUTTON - AREA
// TEXT - BUTTON - AREA
// TEXT - BUTTON - AREA

// button5、生成镜像
// button5、生成镜像
// button5、生成镜像
const filesToImage = async () => {
  if (judgeEditState()) {
    ElMessage.warning("Please save the edited content first");
    return;
  }
  // 弹出确认框
  ElMessageBox.confirm(
    "Make sure you have uploaded all the code files and that the requirements. txt is correct",
    "Confirmation",
    {
      confirmButtonText: "Yes",
      cancelButtonText: "No",
      type: "warning",
    }
  )
    .then(async () => {
      codeMirrorInstance.value = (await buildImage()) as unknown as string;
      ElMessage.success("buildImage over");
    })
    .catch(() => {
      ElMessage.info("Cancel building build");
    });
};

// button6、运行容器
// button6、运行容器
// button6、运行容器
const runTheContainer = async () => {
  if (nodeSelected.value == undefined) {
    ElMessage.error("Please select the code you want to execute first");
    return;
  }

  if (
    nodeSelected.value.directory ||
    !nodeSelected.value.label.endsWith(".py")
  ) {
    ElMessage.error("Unable to execute this file");
    return;
  }

  // 弹出确认框
  ElMessageBox.confirm(
    "Are you sure to save and execute this code : " + nodeSelected.value.label,
    "Confirmation",
    {
      confirmButtonText: "Yes",
      cancelButtonText: "No",
      type: "warning",
    }
  )
    .then(async () => {
      // 保存编辑后的code，并返回代码
      var tempCode = await saveEditedFile();
      // 扫描data文件夹
      dataFileListBefore.value = scanDataFile();
      // 识别输入数据
      extractInputData(tempCode);

      // 获取文件所在的目录
      var filePath = nodeSelected.value.label;
      var formData = new FormData();
      formData.append("scriptName", filePath);
      var response = (await runContainer(formData)) as any;
      // 更新目录（输出的文件）
      treeData.value = formatDirectory(response.directoryList);
      // 更新输出内容
      codeMirrorInstance.value = response.output;
      ElMessage.success("runContainer over");

      // 再次扫描文件夹,得到新增的文件列表
      extractOutputData();
      // incomplete-将新增文件上传到数据容器
      console.log(
        inputDataName.value,
        outputDataName.value,
        "input and output"
      );

      //执行结束，清除选中目标
      nodeSelected.value = undefined;
    })
    .catch(() => {
      ElMessage.info("Please continue editing or change code execution");
    });
};

// 获取输入数据
const inputDataName = ref([] as string[]);
const extractInputData = (code: string) => {
  inputDataName.value = [];
  const lines = code.split("\n"); // 按行分割字符串
  const firstLine = lines[0]; // 读取第一行
  // 检查第一行是否包含@inputData()
  if (firstLine.includes("@inputData")) {
    // 使用正则表达式提取括号内的内容
    const match = firstLine.match(/@inputData\((.*)\)/);
    if (match && match[1]) {
      // 按逗号分割，并创建数组
      inputDataName.value = match[1].split(",").map((item) =>
        item
          .trim()
          .replace(/(^['"]+|['"]+$)/g, "")
          .trim()
      );
    }
  }
  console.log(inputDataName.value, "inputDataName");
};

// 提取多出来的文件，上传到数据容器，并作为输出文件
const outputDataName = ref([] as string[]);
const extractOutputData = () => {
  var dataFileListNow = scanDataFile();
  outputDataName.value = dataFileListNow.filter((item) => {
    return !dataFileListBefore.value.includes(item);
  });
};

// 扫描data文件夹，检查多出来的数据文件，即为输出文件
const dataFileListBefore = ref([] as string[]);
const scanDataFile = () => {
  var dataList: string[] = [];
  treeData.value[0].children.forEach((item) => {
    if (item.label == "data") {
      dataList = item.children.map((data) => {
        return data.label;
      });
    }
  });
  return dataList;
};

// button7、结束运行，清空工作目录，生成工作流
// button7、结束运行，清空工作目录，生成工作流
// button7、结束运行，清空工作目录，生成工作流
const endExecution = () => {
  if (judgeEditState()) {
    ElMessage.warning("Please save the edited content first");
    return;
  }
  // 弹出确认框
  ElMessageBox.confirm(
    "This action will delete the images and containers you have created and clear the working directory . Are you sure you want to end execution?",
    "Confirmation",
    {
      confirmButtonText: "Yes",
      cancelButtonText: "No",
      type: "warning",
    }
  )
    .then(async () => {
      await overReproTask();
      // getAndFormatDirectory();
      // treeData.value = ;
      // Incomplete: 直接跳转到envy界面
      // Incomplete: 直接跳转到envy界面
      // Incomplete: 直接跳转到envy界面
      ElMessage.success("endExecution over");
    })
    .catch(() => {
      ElMessage.info("End execution cancelled");
    });
};

// TERMINAL - AREA
const tips = ref(
  "tips：代码和依赖库只能存放于根目录中；部分文件只能查看无法编辑\n" +
    "请将所有数据文件（包括上传的初始数据和代码生成的输出数据）上传到data文件夹中，否则会影响工作流生成\n" +
    "1、点击蓝色按钮上传代码、数据（必选）和依赖库（可选）\n" +
    "2（可选）、点击黄色按钮生成默认依赖库\n" +
    "3（可选）、点击红色按钮查阅与修改文件，可修改代码、依赖库和部分数据\n" +
    "4（可选）、修改完后请先点击绿色按钮保存编辑内容再进行其他操作\n" +
    "5、点击build image生成镜像\n" +
    "6、选择要执行的代码文件，点击run container执行代码，可执行多次\n" +
    "7、点击红色按钮查阅和保存生成的文件\n" +
    "8、点击end of execution结束复现，根据复现步骤，生成工作流\n"
);
const codeMirrorInstance = ref(tips.value);
//codemirror更新事件
const changeTextarea = (val: any) => {
  codeMirrorInstance.value = val;
};

/**
 * area-right-bottom
 * 右侧下方operationRecord区域
 */

// 加载页面时，先读取一次工作目录
onMounted(async () => {
  getAndFormatDirectory();
});
const test = () => {
  dataFileListBefore.value = scanDataFile();
  extractOutputData();
  console.log(outputDataName.value, "0001");
};
</script>

<style scoped lang="scss">
.python38 {
  width: 60%;
  height: 60vh;
  margin: 20vh 20vw;
  display: flex;
  .workDirectory {
    width: 20%;
    height: 100%;
    overflow: hidden;
    margin-right: 0.5%;
    box-shadow: -3px 5px 12px 0 rgba(0, 0, 0, 0.3);
    border-radius: 5px;

    // border: 1px black solid;
    h2 {
      text-align: center;
      margin: 2.2vh 0;
    }
    .toolbox {
      display: flex;
      justify-content: center;
      height: 8%;
      width: 100%;
      padding: 2%;
      //   border: 1px black solid;
      .uploadButton {
        margin-right: 1vw;
      }
    }
  }
  .controller {
    width: 80%;
    height: 100%;
    box-shadow: 0 5px 12px 0 rgba(0, 0, 0, 0.3);
    border-radius: 5px;
    // border: 1px black solid;
    .terminal {
      width: 100%;
      height: 60%;
      background-color: #eee;
      .terminalTools {
        width: 100%;
        height: 10%;
        background-color: #2c3041;
        color: white;
        line-height: 1.6vh;
        font-size: 1.6vh;
        padding: 1vh;
        .textButton {
          color: white;
        }
      }
      .consoler {
        width: 100%;
        height: 90%;
        .CodeMirror {
          height: 100%;
        }
      }
    }
    .operationRecord {
      width: 100%;
      height: 40%;
      background-color: #ddd;
    }
  }
}
</style>
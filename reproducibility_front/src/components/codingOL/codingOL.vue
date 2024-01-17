<template>
  <div>
    <el-container style="width: 100%">
      <el-main>
        <CommonEditor
          :language="'python'"
          :value="codeMirrorInstance"
          style="height: 40vh; width: 100%"
          @input="changeTextarea"
        ></CommonEditor>
        <div class="operationArea">
          <el-button @click="codeRunning" type="primary">run code</el-button>
          <el-button @click="addNewModelVisible = true" type="success"
            >Save code</el-button
          >
          <el-button @click="downloadVisible = true" type="success"
            >Save Output to Local</el-button
          >
        </div>
        <!-- 填写存为new model 的表单 -->
        <el-dialog
          title="Save the code as a new model"
          v-model="addNewModelVisible"
          width="30%"
          @close="handleClose"
        >
          <el-row>
            <el-col :span="12">
              <el-input v-model="modelName" placeholder="model name" label="modelName"></el-input>
            </el-col>
            <el-col :span="10" :offset="2">
              <el-switch
                v-model="privacy"
                style="
                  --el-switch-on-color: #ff4949;
                  --el-switch-off-color: #13ce66;
                "
                inactive-text="public"
                active-text="private"
              ></el-switch>
            </el-col>
          </el-row>
          <br>
          <el-row>
            <el-col :span="24">
              <el-input
                v-model="modelDescription"
                placeholder="model description"
                type="textarea"
                rows="6"
              ></el-input>
            </el-col>
          </el-row>
          <template v-slot:footer>
            <span class="dialog-footer">
              <el-button @click="handleClose">Cancel</el-button>
              <el-button type="primary" @click="saveCode"> Save </el-button>
            </span>
          </template>
        </el-dialog>
        <!-- 收集保存文件的名称 -->
        <el-dialog
          title="Custom file name"
          v-model="downloadVisible"
          width="30%"
          @close="handleClose"
        >
          <el-input
            v-model="fileName"
            placeholder="Please enter a file name (including file extension)"
          >
          </el-input>

          <template v-slot:footer>
            <span class="dialog-footer">
              <el-button @click="handleClose">Cancel</el-button>
              <el-button type="primary" @click="saveWithCustomFileName">
                Save
              </el-button>
            </span>
          </template>
        </el-dialog>
      </el-main>
      <el-footer class="footer" height="315px">
        <div class="res">
          <el-row>
            <el-col :span="24" class="outputcol">
              <div class="outputbox">
                {{ responseOutput }}
              </div>
            </el-col>
          </el-row>
        </div>
      </el-footer>
    </el-container>
  </div>
</template>

<script>
import "codemirror/lib/codemirror.css"; // 引入 CodeMirror 的 CSS 文件
import "codemirror/mode/javascript/javascript"; // 引入 JavaScript 编辑器模式
import { codingPython } from "@/api/request";
import CommonEditor from "./CommonEditor.vue";
import {
  saveData,
  getFolders,
  getScenarioById,
  getProjectById,
  saveNewCodeModel,
  updateresourceCollection,
} from "@/api/request";
import { renderSize } from "@/utils/utils";

export default {
  components: {
    CommonEditor: CommonEditor,
  },
  props: {
    scenarioId: {
      type: String,
    },
    content: {
      type: String,
    },
  },
  watch: {
    content: {
      handler(val) {
        if(val){
          this.codeMirrorInstance = val;
        }
      },
      deep: true,
      immediate: true,
    },
  },
  data() {
    return {
      codeMirrorInstance: "print('Have a try now!')",
      responseOutput: "Output or error message will be displayed here !",
      pathError: false,
      downloadVisible: false,
      addNewModelVisible: false,
      fileName: "",
      modelName: "",
      privacy: "true",
      modelDescription: "",
      Folders: [],
      routerFolderId: "",
      scenario: {},
      projectId: this.$route.params.id, //projectId
    };
  },
  methods: {
    async saveCode() {
      let privacy
      if(this.privacy == true){
        privacy = 'private'
      }else{
        privacy = 'public'
      }
      let form = {}
      form.name = this.modelName;
      form.description = this.modelDescription;
      form.privacy = privacy;
      form.content = this.codeMirrorInstance;

      await saveNewCodeModel(form).then((result)=>{
        if(result == "Save Error."){
                  this.$message({
            message:'Save error',
            type:'error'
          })
        }else{
          this.$message({
            message:'Save success',
            type:'success'
          })
          this.scenario.resourceCollection.modelList.push(result)
        }
      })
      this.addNewModelVisible = false;

      this.scenario = await updateresourceCollection(
        this.scenario.id,
        {
          modelList: this.scenario.resourceCollection.modelList,
          dataList: this.scenario.resourceCollection.dataList,
        }
      )
    },
    //下载按钮
    saveOutputDialogue() {
      this.downloadVisible = true;
    },
    //取消下载
    handleClose() {
      this.downloadVisible = false;
      this.addNewModelVisible = false;
    },
    // 确定下载
    saveWithCustomFileName() {
      var fileName = this.fileName.trim();
      if (fileName) {
        // 用户输入了文件名
        this.saveOutput(fileName);
        this.downloadVisible = false; // 关闭对话框
      } else {
        // 用户未输入文件名，可以添加提示
        this.$message.error("文件名不能为空！");
      }
    },
    //下载事件
    saveOutput(fileName) {
      // 创建一个新的 Blob 对象，将数据包装在其中
      var blob = new Blob([this.responseOutput]);
      var link = document.createElement("a");
      link.href = window.URL.createObjectURL(blob);
      link.download = fileName;
      link.click();
    },

    //运行代码块
    async codeRunning() {
      var code = this.codeMirrorInstance;
      var codeLines = code.split("\n");
      var updatedCodeLines = [];
      for (var codeLine of codeLines) {
        var currentCodeLines = updatedCodeLines.slice();
        var updatedLine = await this.replaceDataInLine(
          codeLine,
          currentCodeLines
        );
        if (this.pathError) {
          this.pathError = false;
          this.responseOutput = updatedLine;
          return;
        }
        updatedCodeLines.push(updatedLine);
      }
      var codeWithReplacedData = updatedCodeLines.join("\n");
      var data = { code: codeWithReplacedData, folderId: this.routerFolderId };
      console.log(data, "docker-code");
      this.responseOutput = await codingPython(data);
    },
    // 识别所有的@GetData('路径')，并将其转化为对应的数据，如果路径不指向数据文件则报出具体的错误
    // 识别所有的@SaveData(data,'路径')，并将其删除，将对应的data以对应的格式存储到指定路径中
    async replaceDataInLine(line, currentCodeLines) {
      const regex = /@GetData\(['"](.*?)['"]\)/g;
      const regex2 = /@SaveData\(([^,]+),\s*['"]([^'"]+)['"]\)/g;
      let updatedLine = line;
      let match;
      let match2;

      // 先用url替换掉获取数据的标识符
      while ((match = regex.exec(line)) !== null) {
        var relativePath = match[1];
        var fileContent = this.changeData(relativePath);
        // 如果changeData发现这个路径不指向特定的数据文件，就在这里退出，并返回错误原因
        if (this.pathError) {
          return fileContent;
        }
        updatedLine = updatedLine.replace(
          match[0],
          JSON.stringify(fileContent)
        );
      }

      // 记录并删除保存数据的标识符
      while ((match2 = regex2.exec(line)) !== null) {
        var data = match2[1];
        var relativePath2 = match2[2];
        var fileContent2 = "";
        await this.SaveDataInFolder(data, relativePath2, currentCodeLines);
        updatedLine = updatedLine.replace(
          match2[0],
          JSON.stringify(fileContent2)
        );
        return updatedLine;
      }
      return updatedLine;
    },
    // 执行GetData逻辑，根据路径找到对应的数据文件，获取数据的url
    changeData(path) {
      var segments = path.split("/").filter((segment) => segment.trim() !== "");
      //空路径
      if (segments.length < 1) {
        this.pathError = true;
        return "路径为空";
      }
      var currentFolder;
      var projectName = this.project?.name + " --folder";
      this.Folders[0]?.children.forEach((child) => {
        if (child.name === projectName) {
          currentFolder = child;
          return;
        }
      });
      var scenarioName = this.scenario?.name + " --folder";
      currentFolder?.children.forEach((child) => {
        if (child.name === scenarioName) {
          currentFolder = child;
          return;
        }
      });
      // 处理前面的部分，每个部分都是文件夹
      for (let i = 0; i < segments.length - 1; i++) {
        if (segments[i] == "." || segments[i] == "..") {
          continue;
        }
        var folderName = segments[i];
        console.log(i, folderName, currentFolder, currentFolder.children);
        if (currentFolder.children) {
          var flag = false;
          currentFolder.children.some((child, i) => {
            // 要检查子文件是不是有level属性，没有的是数据文件，不能出现在路径中间
            if (child.name === folderName && child.level) {
              currentFolder = currentFolder.children[i];
              flag = true;
            }
          });
          if (!flag) {
            this.pathError = true;
            return (
              currentFolder.name +
              "文件中没有名为" +
              folderName +
              "的文件夹，请检查路径"
            );
          }
        } else {
          //文件夹没有文件或者路径文件夹错误
          this.pathError = true;
          return folderName + "该文件没有子文件夹或该文件不是文件夹";
        }
      }
      let fileName = segments[segments.length - 1];
      let url = null;
      // 访问数据文件,获取数据文件url
      if (currentFolder.children) {
        currentFolder.children.some((child, i) => {
          if (
            (child.name === fileName ||
              child.name + child.suffix === fileName) &&
            child.value
          ) {
            url = currentFolder.children[i].value;
            return;
          }
        });
      }
      if (url) {
        return url;
      } else {
        this.pathError = true;
        return fileName + "不是一个数据文件，请检查该数据文件是否存在"; // 数据文件不存在
      }
    },
    //捕获路径之后进行数据存储
    async SaveDataInFolder(data, path, currentCodeLines) {
      //需要针对path做进一步处理
      // if(segments[i]=='.'||segments[i]=='..'){
      //     continue;
      //   }
      var currentCodeBlock = currentCodeLines.join("\n");
      // console.log(data,path,'11');
      currentCodeBlock += '\nprint("start");';
      currentCodeBlock += "\nprint(" + data + ");";
      var code = { code: currentCodeBlock };
      var responseOutput = await codingPython(code);
      var lines = responseOutput.split("\n");

      // 找到打印的数据，从i = lines.length - 2开始，回避内容为空的情况
      var dataContent = "";
      for (let i = lines.length - 2; i >= 0; i--) {
        if (lines[i].trim() == "start") {
          for (let j = i + 1; j < lines.length; j++) {
            if (lines[j].trim() !== "") {
              dataContent += lines[j].trim();
              dataContent += "\n";
            }
          }
        }
      }
      // 先处理path，获取指定的文件拓展名
      var state = 0;
      var suffix;
      var fileName;
      var blob;
      var fullName;
      // 处理path，获得文件名和拓展名
      if (path != "") {
        state += 1;
        fullName = path.replace(/^.*[\\\/]/, "");
        let lastIndexOfDot = fullName.lastIndexOf(".");
        // 判断是否存在文件扩展名
        if (lastIndexOfDot !== -1) {
          fileName = fullName.substring(0, lastIndexOfDot); // 获取除去最后一个点之后的部分
        } else {
          fileName = fullName;
        }
        let match = path.match(/\.([^.]+)$/);
        suffix = match ? match[1] : "txt";
      }
      fullName = fileName + "." + suffix;
      // 根据path储存到对应的位置
      if (dataContent != "" && state == 1) {
        state += 1;
        blob = new Blob([dataContent], {
          type: `text/${suffix};charset=utf-8`,
        });
      }
      // 如果state为2，就可以执行文件存储操作了
      if (state == 2) {
        // 包装上传的文件
        let uploadFileForm = new FormData();
        uploadFileForm.append("file", blob, fullName);

        // 获取添加的文件夹
        var currentRow;
        var projectName = this.project?.name + " --folder";
        this.Folders[0]?.children.forEach((child) => {
          if (child.name === projectName) {
            currentRow = child;
            return;
          }
        });
        var scenarioName = this.scenario?.name + " --folder";
        currentRow?.children.forEach((child) => {
          if (child.name === scenarioName) {
            currentRow = child.id;
            return;
          }
        });
        let data = await saveData(
          uploadFileForm,
          renderSize(blob.size),
          currentRow
        );
        console.log(data, "all done");
      }
    },
    //codemirror更新事件
    changeTextarea(val) {
      this.codeMirrorInstance = val;
    },
  },
  async mounted() {
    // console.log(this.Folders,'11');
    this.Folders = await getFolders();
    this.scenario = await getScenarioById(this.scenarioId);
    for (let children of this.Folders[0].children) {
      if (children.tagId == this.projectId) {
        for (let child of children.children) {
          if (child.tagId == this.scenarioId) {
            this.routerFolderId = child.id;
          }
        }
      }
    }
    this.project = await getProjectById(this.projectId);
    if(this.content){
      this.codeMirrorInstance = this.content
    }
  },
  beforeUnmount() {},
};
</script>

<style lang="less" scoped>
.codingArea {
  width: 600px;
  padding: 10px;
}
.operationArea {
  margin: 10px;
  padding: 5px;
  float: right;
}
.CodeMirror-code {
  line-height: 1.6em;
  font-family: Menlo, Monaco, Consolas, "Courier New", monospace;
}
.footer {
  display: flex;
  flex-direction: column;
  align-content: space-between;
  justify-content: space-between;
  .res {
    height: 80%;
    width: 100%;
    border: 1px solid rgba(0, 0, 0, 0.15);
    box-shadow: 0 6px 12px rgba(0, 0, 0, 0.175);
    background-color: #444040;
    color: #fff;
    .outputcol {
      display: flex;
      justify-content: center;
      align-content: center;
      justify-items: center;
      align-items: center;
      border-right: 1ch solid #7c7777;
      .outputbox {
        white-space: pre-wrap;
        overflow-y: auto;
        height: 250px;
        width: 80%;
      }
      /*滚动条宽 长,滚动条整体部分，其中的属性有width,height,background,border等。*/
      ::-webkit-scrollbar {
        width: 7px;
      }
      /*滚动条的滑轨背景颜色,可以用display:none让其不显示，也可以添加背景图片，颜色改变显示效果。*/
      ::-webkit-scrollbar-track {
        background-color: #f5f5f5;
        -webkit-box-shadow: inset 0 0 3px rgba(0, 0, 0, 0.1);
        border-radius: 5px;
      }
      /*滑块颜色 */
      ::-webkit-scrollbar-thumb {
        background-color: rgba(0, 0, 0, 0.2);
        border-radius: 5px;
      }
      /*滚动条两端的按钮。可以用display:none让其不显示，也可以添加背景图片，颜色改变显示效果。*/
      ::-webkit-scrollbar-button {
        background-color: #eee;
        display: none;
      }
      /*横向滚动条和纵向滚动条相交处尖角的颜色 */
      ::-webkit-scrollbar-corner {
        background-color: rgb(117, 116, 116);
      }
    }
  }
}
</style>

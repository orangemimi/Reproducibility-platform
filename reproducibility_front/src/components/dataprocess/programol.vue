<template>
  <div class="common-layout">
    <el-container>
      <el-header class="header">
        <h2>{{ workspace.userName }}的工作空间</h2>
        <div>
          <span style="font-size: 18px; font-weight: 600"
            >项目选择:&nbsp;&nbsp;</span
          >
          <el-select
            v-model="workspace.projectSelId"
            class="m-2"
            placeholder="请选择您的项目"
            size="large"
            @change="projectChange"
            ref="projSel"
          >
            <el-option
              v-for="item in workspace.projectOption"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
          <el-button
            type="primary"
            plain
            style="margin-left: 10px"
            @click="createProject"
          >
            创建新项目
          </el-button>
          <el-button
            type="danger"
            plain
            style="margin-left: 10px"
            @click="ProjectDeletedata.projectDeleteDialogShow = true"
            :disabled="workspace.projectSelId == ''"
          >
            删除该项目
          </el-button>
        </div>
      </el-header>
      <el-container
        v-loading="workspace.loading"
        element-loading-text="项目开启中，请稍后..."
      >
        <el-aside
          width="250px"
          class="aside"
          :style="{
            boxShadow: '--el-box-shadow-lighter',
          }"
        >
          <div class="btnbox">
            <div class="left">
              <span>项目:{{ projectdata.projectName }}</span>
            </div>
            <div class="btns">
              <el-upload
                :show-file-list="false"
                class="upload-demo"
                action=""
                :limit="1"
                :before-upload="uploadfile"
                :auto-upload="true"
              >
                <el-icon size="21px" class="icon" title="上传文件"
                  ><Upload
                /></el-icon>
              </el-upload>
              <el-icon
                @click="downloadfile"
                size="21px"
                class="icon"
                title="下载代码"
                ><Download
              /></el-icon>
              <el-icon
                size="21px"
                class="icon"
                title="新建文件"
                @click="createfile"
                ><DocumentAdd
              /></el-icon>
              <el-icon
                size="21px"
                class="icon"
                title="新建文件夹"
                @click="createfolder"
                ><FolderAdd
              /></el-icon>
            </div>
          </div>
          <el-tree
            ref="tree"
            :data="projectdata.projectDirectory"
            :props="projectdata.defaultProps"
            @node-click="handleNodeClick"
            @node-contextmenu="handleNodeContext"
            node-key="filePath"
            :current-node-key="projectdata.currentnode"
          />
          <div
            id="contextmenu"
            v-show="menuContextdata.foldervisible"
            class="menu folder"
          >
            <div class="contextmenu__item" @click="openfile">打开</div>
            <div class="contextmenu__item" @click="createfolder">
              新建文件夹
            </div>
            <div class="contextmenu__item" @click="createfile">新建文件</div>
            <div class="contextmenu__item" @click="deletefile">删除</div>
            <el-upload
              :show-file-list="false"
              class="upload-demo contextmenu__item"
              action=""
              :limit="1"
              :before-upload="uploadfile"
              :auto-upload="true"
            >
              <div>上传文件</div>
            </el-upload>
            <div class="contextmenu__item" @click="downloadfile">下载</div>
            <div class="contextmenu__item" @click="renamefile">重命名</div>
          </div>
          <div
            id="contextmenu"
            v-show="menuContextdata.filevisible"
            class="menu file"
          >
            <div class="contextmenu__item" @click="openfile">打开</div>
            <div class="contextmenu__item" @click="deletefile">删除</div>
            <div class="contextmenu__item" @click="renamefile">重命名</div>
            <div class="contextmenu__item" @click="downloadfile">下载</div>
            <div class="contextmenu__item" @click="addToLab">
              添加数据至实验室
            </div>
          </div>
        </el-aside>
        <el-container>
          <el-main>
            <el-tabs
              v-model="Tabdata.editableTabsValue"
              type="card"
              class="demo-tabs"
              closable
              @tab-remove="removeTab"
              @tab-change="tabchange"
            >
              <el-tab-pane
                v-for="item in Tabdata.editableTabs"
                :key="item.name"
                :label="item.title"
                :name="item.name"
              >
              </el-tab-pane>
            </el-tabs>
            <div class="content">
              <CommonEditor
                :language="'python'"
                :value="codedata.code"
                style="height: 40vh"
                @input="changeTextarea"
              ></CommonEditor>
            </div>
          </el-main>
          <el-footer class="footer" height="315px">
            <div class="res">
              <el-row>
                <el-col :span="24" class="outputcol">
                  <div class="outputbox">
                    {{ Outputdata.output }}
                  </div>
                </el-col>
                <!-- <el-col :span="12"> <div id="mycanvas"></div></el-col> -->
              </el-row>

              <!-- If you want turtle graphics include a canvas -->
            </div>
            <div class="btnss">
              <el-button
                type="primary"
                plain
                @click="savedit"
                :disabled="Tabdata.editableTabsValue == ''"
                >保存编辑</el-button
              >
              <el-button
                @click="donwloadDep"
                type="primary"
                plain
                :disabled="
                  workspace.projectSelId == '' &&
                  !projectdata.isrunning &&
                  !projectdata.isdownloading
                "
                >下载依赖</el-button
              >
              <el-button
                type="success"
                plain
                @click="runit"
                :disabled="
                  workspace.projectSelId == '' &&
                  !projectdata.isrunning &&
                  !projectdata.isdownloading
                "
                >运行</el-button
              >
              <el-button
                type="warning"
                plain
                @click="stopit"
                :disabled="
                  (workspace.projectSelId == '' &&
                    !projectdata.isdownloading) ||
                  !projectdata.isrunning
                "
                >停止运行</el-button
              >
            </div>
          </el-footer>
        </el-container>
      </el-container>
    </el-container>
    <el-dialog
      :visible="FolderCreatedata.folderCreateDialogShow"
      title="输入文件夹名称"
      width="400px"
      :modal="false"
    >
      <el-input v-model="FolderCreatedata.folderName"></el-input>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="CreateFolderCancel">取消</el-button>
          <el-button type="primary" @click="CreateFolderConfirm">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
    <el-dialog
      :visible="FileCreatedata.fileCreateDialogShow"
      title="输入文件名称"
      width="400px"
      :modal="false"
    >
      <el-input v-model="FileCreatedata.fileName"></el-input>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="CreateFileCancel">取消</el-button>
          <el-button type="primary" @click="CreateFileConfirm">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
    <el-dialog
      :visible="projectCreatedata.projectCreateDialogShow"
      title="创建新项目"
      width="400px"
      :modal="false"
      @close="projectCreatedata.projectCreateDialogShow = false"
    >
      <span>项目名称</span>
      <el-input v-model="projectCreatedata.projectName"></el-input>
      <div>版本</div>
      <el-select
        v-model="projectCreatedata.pythonValue"
        class="m-2"
        placeholder="请选择python版本"
        size="large"
      >
        <el-option
          v-for="item in projectCreatedata.pythonOptions"
          :key="item.value"
          :label="item.label"
          :value="item.value"
        />
      </el-select>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="projectCreatedata.projectCreateDialogShow = false"
            >取消</el-button
          >
          <el-button type="primary" @click="projectCreateConfirm">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
    <el-dialog
      :visible="ProjectDeletedata.projectDeleteDialogShow"
      title="确认要删除该项目吗？"
      width="400px"
      :modal="false"
    >
      <span style="font-size: 20px">项目名称： </span>
      <span>{{ projectdata.projectName }}</span>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="ProjectDeletedata.projectDeleteDialogShow = false"
            >取消</el-button
          >
          <el-button type="primary" @click="deleteProject"> 确定 </el-button>
        </span>
      </template>
    </el-dialog>
    <el-dialog
      :visible="FileRenamedata.fileRenameDialogShow"
      title="输入新名称"
      width="400px"
      :modal="false"
    >
      <el-input v-model="FileRenamedata.fileName"></el-input>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="RenameFileCancel">取消</el-button>
          <el-button type="primary" @click="RenameFileConfirm">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import CommonEditor from "./CommonEditor.vue";
// import { codemirror } from "vue-codemirror";
// import "codemirror/addon/hint/show-hint.css";
// import "codemirror/addon/hint/show-hint.js";
// import "codemirror/addon/hint/javascript-hint";
// import "codemirror/addon/hint/xml-hint";
// import "codemirror/addon/hint/sql-hint";
// import "codemirror/addon/hint/anyword-hint";
// import "codemirror/theme/monokai.css";
// import "codemirror/mode/python/python.js";
// import {
//   DocumentAdd,
//   Download,
//   FolderAdd,
//   Upload,
// } from "@element-plus/icons-vue";
import {
  CMData,
  projectData,
  TabData,
  menuContextData,
  FolderCreateData,
  FileCreateData,
  NodeContextData,
  ProjectCreateData,
  ProjectDeleteData,
  FileRenameData,
  CodeData,
  OutputData,
  Project,
  WorkSpace,
  Createfile,
} from "../../utils/programol";
import Api from "../../api/programol.js";
// import { this.$message } from "element-plus";
export default {
  name: "programol",
  data() {
    return {
      api: new Api(),
      userInfo: localStorage.getItem("userInfo"),
      CMdata: new CMData(),
      projectdata: new projectData(),
      Tabdata: new TabData(),
      menuContextdata: new menuContextData(),
      FolderCreatedata: new FolderCreateData(),
      FileCreatedata: new FileCreateData(),
      NodeContextdata: new NodeContextData(),
      projectCreatedata: new ProjectCreateData(),
      ProjectDeletedata: new ProjectDeleteData(),
      FileRenamedata: new FileRenameData(),
      workspace: new WorkSpace(),
      codedata: new CodeData(),
      Outputdata: new OutputData(),
      tree: null,
      projSel: null,
      timer: null,
      userId: localStorage.getItem("userId"),
      userName: localStorage.getItem("name"),
    };
  },
  computed: {
    // extensions() {
    //   const result = [];
    //   result.push(this.CMdata.languages[this.CMdata.CMConfig.language]);
    //   if (this.CMdata.themes[this.CMdata.CMConfig.theme]) {
    //     result.push(this.CMdata.themes[this.CMdata.CMConfig.theme]);
    //   }
    //   return result;
    // },
  },
  methods: {
    getWorkSpaceData() {
      this.api.OpenWorkSpace(this.userId).then((res) => {
        if (res.data.code == 0) {
          this.workspace.userName = res.data.data.userName;
          this.workspace.projectOption = res.data.data.projectOption;
        }
      });
    },
    //打开某个项目，初始化文件夹列表
    openproject() {
      this.workspace.loading = true;
      this.api.OpenProject(this.workspace.projectSelId).then((res) => {
        console.log(res);
        if (res.data.code == 0) {
          this.$message({ message: "开启项目成功", type: "success" });
          this.projectdata.projectName = res.data.data.projectName;
          this.projectdata.projectDirectory = res.data.data.programFileDTO;
          this.NodeContextdata.currentNodeFolder =
            "E:/workspace/" + this.userId + "/" + this.projectdata.projectName;
          this.NodeContextdata.currentNodeId =
            this.projectdata.projectDirectory[0].id;
          this.projectdata.containerStatus = true;
          this.Tabdata.editableTabs = [];
          this.$set(this.codedata, "code", "");
          // this.codedata.code = "";
          this.workspace.loading = false;
          // startWatch();
        } else {
          this.$message({ message: "开启项目失败", type: "error" });
        }
      });
    },
    // startTimer() {
    //   this.timer = setTimeout(() => {
    //     if (this.projectdata.containerStatus) {
    //       console.log(123123);
    //       this.$message({ message: "长时间未操作关闭容器", type: "warning" });
    //       api.stopContainer(this.workspace.projectSelId).then((res) => {
    //         console.log(res);
    //         projectdata.containerStatus = false;
    //       });
    //     }
    //   }, 10 * 60 * 1000);
    // },
    // resetTimer() {
    //   // console.log(123456);
    //   if (timer) {
    //     clearTimeout(timer);
    //   }
    //   if (!this.projectdata.containerStatus) {
    //     this.openproject();
    //     this.projectdata.containerStatus = true;
    //   }
    //   startTimer();
    // },
    // startWatch() {
    //   // console.log(111111);
    //   document.addEventListener("mousedown", resetTimer);
    //   document.addEventListener("mousemove", resetTimer);
    //   document.addEventListener("keydown", resetTimer);
    //   document.addEventListener("scroll", resetTimer);
    //   startTimer();
    // },
    // onUnmounted(() => {
    //   document.removeEventListener("mousedown", resetTimer);
    //   document.removeEventListener("mousemove", resetTimer);
    //   document.removeEventListener("keydown", resetTimer);
    //   document.removeEventListener("scroll", resetTimer);
    //   if (timer) {
    //     clearTimeout(timer);
    //   }
    //   if (projectdata.containerStatus) {
    //     api.stopContainer(workspace.projectSelId);
    //   }
    // });
    handleReady(payload) {
      console.log("handleReady payload:", payload);
    },

    // const outf = (text) => {
    //   var mypre = document.getElementById("output");
    //   mypre.innerHTML = mypre.innerHTML + text;
    // };
    addTab(id, tablabel) {
      this.Tabdata.editableTabs.push({
        title: tablabel,
        name: id,
      });
      this.Tabdata.editableTabsValue = id;
    },
    removeTab(targetName) {
      const tabs = this.Tabdata.editableTabs;
      let activeName = this.Tabdata.editableTabsValue;
      if (activeName === targetName) {
        tabs.forEach((tab, index) => {
          if (tab.name === targetName) {
            const nextTab = tabs[index + 1] || tabs[index - 1];
            if (nextTab) {
              activeName = nextTab.name;
            } else {
              // this.codedata.code = "";
              this.$set(this.codedata, "code", "");
              activeName = "";
            }
          }
        });
      }

      this.Tabdata.editableTabsValue = activeName;
      this.Tabdata.editableTabs = tabs.filter((tab) => tab.name !== targetName);
    },
    updateCode(newCode) {
      this.$set(this.codedata, "code", newCode);
      // this.codedata.code = newCode; // 更新父组件中的数据属性
    },
    tabchange(TabPaneName) {
      // consoleLog(TabPaneName);
      this.$set(this.codedata, "code", this.codedata.codeMap.get(TabPaneName));
      // this.codedata.code = this.codedata.codeMap.get(TabPaneName);
    },
    //更换项目，也就是打开另一个项目
    projectChange() {
      console.log(this.workspace.projectSelId);
      this.openproject();
      console.log(this.projSel);
      this.Outputdata.output = "";
      // tree.value.setCurrentNode(node);
    },
    //文件夹目录点击事件
    handleNodeClick(data) {
      this.NodeContextdata.currentNodeId = data.id;
      if (data.type == "folder") {
        this.NodeContextdata.currentNodeFolder = data.filePath;
        this.NodeContextdata.currentNodeFile = null;
      } else if (data.type == "file") {
        this.NodeContextdata.currentNodeFile = data.filePath;
        this.NodeContextdata.currentNodeFolder = null;
      }
      // console.log(node);
      // tree.value.setCurrentNode(node, true);
    },
    //文件夹目录右键事件
    handleNodeContext(event, data) {
      this.NodeContextdata.nodeEvent = event;
      this.NodeContextdata.nodeData = data;
      this.NodeContextdata.nodeElement = null;
      this.NodeContextdata.currentNodeId = data.id;
      if (data.type == "folder") {
        this.NodeContextdata.currentNodeFolder = data.filePath;
        this.NodeContextdata.currentNodeFile = null;
        this.NodeContextdata.nodeElement = event.target.closest(
          ".el-tree-node__content"
        );
        this.menuContextdata.foldervisible = false; // 先把模态框关死，目的是 第二次或者第n次右键鼠标的时候 它默认的是true
        this.menuContextdata.foldervisible = true; // 显示模态窗口，跳出自定义菜单栏
        event.preventDefault(); //关闭浏览器右键默认事件
        let menu = document.querySelector(".folder");
        this.styleMenu(menu, event, data);
      } else {
        this.NodeContextdata.currentNodeFile = data.filePath;
        this.NodeContextdata.currentNodeFolder = null;
        this.menuContextdata.filevisible = false; // 先把模态框关死，目的是 第二次或者第n次右键鼠标的时候 它默认的是true
        this.menuContextdata.filevisible = true; // 显示模态窗口，跳出自定义菜单栏
        event.preventDefault(); //关闭浏览器右键默认事件
        let menu = document.querySelector(".file");
        this.styleMenu(menu, event, data);
      }
    },
    //设置contextmenu的位置
    styleMenu(menu, e, data) {
      if (data.type == "folder") {
        document.addEventListener("click", this.foofolder); // 给整个document新增监听鼠标事件，点击任何位置执行foo方法
        menu.style.top = e.layerY + 50 + "px";
        menu.style.left = e.layerX + "px";
      }
      if (data.type == "file") {
        document.addEventListener("click", this.foofile); // 给整个document新增监听鼠标事件，点击任何位置执行foo方法
        menu.style.top = e.layerY + 50 + "px";
        menu.style.left = e.layerX + "px";
      }
    },
    //点击别的地方，contextmenu就不显示
    foofolder() {
      // 取消鼠标监听事件 菜单栏
      this.menuContextdata.foldervisible = false;
      document.removeEventListener("click", this.foofolder); // 关掉监听，
    },
    //同上
    foofile() {
      // 取消鼠标监听事件 菜单栏
      this.menuContextdata.filevisible = false;
      document.removeEventListener("click", this.foofile); // 关掉监听，
    },
    //打开文件或者文件夹
    openfile() {
      if (this.NodeContextdata.nodeData.type == "folder") {
        this.NodeContextdata.nodeElement.click();
      } else {
        if (
          this.Tabdata.editableTabs.some(
            (item) => item.name == this.NodeContextdata.currentNodeId
          )
        ) {
          // this.codedata.code = this.codedata.codeMap.get(
          //   this.NodeContextdata.currentNodeId
          // );
          this.$set(
            this.codedata,
            "code",
            this.codedata.codeMap.get(this.NodeContextdata.currentNodeId)
          );

          this.Tabdata.editableTabsValue = this.NodeContextdata.currentNodeId;
        } else {
          this.api.openFile(this.NodeContextdata.currentNodeId).then((res) => {
            console.log(res);
            // code.value = res.data.data;
            this.codedata.codeMap.set(
              this.NodeContextdata.currentNodeId,
              res.data.data
            );
            this.$set(this.codedata, "code", res.data.data);
            // this.codedata.code = res.data.data;
            console.log(this.codedata.code);
            this.addTab(
              this.NodeContextdata.currentNodeId,
              this.NodeContextdata.nodeData.label
            );
            // addTab()
          });
        }

        console.log(this.NodeContextdata.nodeData.label);
      }
      this.closeMenu();
    },
    //创建项目
    createProject() {
      console.log(111);
      this.projectCreatedata.projectName = "";
      this.projectCreatedata.pythonValue = "";
      this.projectCreatedata.projectCreateDialogShow = true;
    },
    //确认创建
    projectCreateConfirm() {
      let project = new Project();
      project.projectName = this.projectCreatedata.projectName;
      project.pythonVersion = this.projectCreatedata.pythonValue;
      project.userId = this.userId;
      project.workspace = this.userId;
      this.api.CreateProject(project).then((res) => {
        console.log(res);
        if (res.code == 0) {
          this.projectdata.projectName = res.data.projectName;
          this.workspace.projectSelId = res.data.projectId;
          this.projectdata.projectDirectory = res.data.programFileDTO;
        }
        this.getWorkSpaceData();
      });
      this.projectCreatedata.projectCreateDialogShow = false;
    },
    //删除项目
    deleteProject() {
      this.api.DeleteProject(this.workspace.projectSelId).then((res) => {
        if (res.data.code == 0) {
          this.$message({ message: res.data.data, type: "success" });
          this.getWorkSpaceData();
          this.projectdata.projectDirectory = [];
          this.projectdata.projectName = "";
        } else {
          this.$message({ message: res.data, type: "error" });
        }
      });
      this.ProjectDeletedata.projectDeleteDialogShow = false;
    },
    //创建文件夹
    createfolder() {
      // 处理创建文件夹的逻辑
      if (this.workspace.projectSelId != "") {
        this.FolderCreatedata.folderName = "";
        this.FolderCreatedata.folderCreateDialogShow = true;
      } else {
        this.$message({ message: "请先创建或选择项目", type: "warning" });
      }

      this.closeMenu();
    },
    //取消创建
    CreateFolderCancel() {
      this.FolderCreatedata.folderName = "";
      this.FolderCreatedata.folderCreateDialogShow = false;
    },
    //确认创建
    CreateFolderConfirm() {
      if (
        this.NodeContextdata.currentNodeFolder == null ||
        this.NodeContextdata.currentNodeId == null
      ) {
        // NodeContextdata.currentNodeFolder =
        //   "W:/YangtzeDataStore/workspace/" +
        //   userInfo.id +
        //   "/" +
        //   projectdata.projectName;
        this.NodeContextdata.currentNodeFolder =
          "E:/workspace/" + this.userId + "/" + this.projectdata.projectName;
        this.NodeContextdata.currentNodeId =
          this.projectdata.projectDirectory[0].id;
      }
      let file = new Createfile();
      file.fileName = this.FolderCreatedata.folderName;
      file.filePath =
        this.NodeContextdata.currentNodeFolder +
        "/" +
        this.FolderCreatedata.folderName;
      file.isFolder = true;
      file.projectName = this.projectdata.projectName;
      file.userId = this.userId;
      // file.parentId=
      file.parentId = this.NodeContextdata.currentNodeId;
      this.api.CreateFile(file).then((res) => {
        console.log(res);
        if (res.code == 0) {
          this.$message({ message: res.data, type: "success" });
        } else {
          this.$message({ message: res.data, type: "error" });
        }
        this.openproject();
        this.FolderCreatedata.folderCreateDialogShow = false;
      });
    },
    //创建文件
    createfile() {
      // 处理创建文件的逻辑
      if (this.workspace.projectSelId != "") {
        this.FileCreatedata.fileName = "";
        this.FileCreatedata.fileCreateDialogShow = true;
      } else {
        this.$message({ message: "请先创建或选择项目", type: "warning" });
      }
      this.closeMenu();
    },
    //取消创建
    CreateFileCancel() {
      this.FileCreatedata.fileName = "";
      this.FileCreatedata.fileCreateDialogShow = false;
    },
    //确认创建
    CreateFileConfirm() {
      if (
        this.NodeContextdata.currentNodeFolder == null ||
        this.NodeContextdata.currentNodeId == null
      ) {
        // NodeContextdata.currentNodeFolder =
        //   "W:/YangtzeDataStore/workspace/" +
        //   userInfo.id +
        //   "/" +
        //   projectdata.projectName;
        this.NodeContextdata.currentNodeFolder =
          "E:/workspace/" + this.userId + "/" + this.projectdata.projectName;
        this.NodeContextdata.currentNodeId =
          this.projectdata.projectDirectory[0].id;
      }
      let file = new Createfile();
      file.fileName = this.FileCreatedata.fileName;
      file.filePath =
        this.NodeContextdata.currentNodeFolder +
        "/" +
        this.FileCreatedata.fileName;
      file.isFolder = false;
      file.projectName = this.projectdata.projectName;
      file.userId = this.userId;
      file.parentId = this.NodeContextdata.currentNodeId;
      this.api.CreateFile(file).then((res) => {
        // console.log(res);
        if (res.code == 0) {
          this.$message({ message: res.data, type: "success" });
        } else {
          this.$message({ message: res.data, type: "error" });
        }
        this.openproject();
        this.FileCreatedata.fileCreateDialogShow = false;
      });
    },
    //删除文件
    deletefile() {
      this.api.DeleteFile(this.NodeContextdata.currentNodeId).then((res) => {
        if (res.data.code == 0) {
          this.$message({ message: res.data.data, type: "success" });
        } else {
          this.$message({ message: res.data.data, type: "error" });
        }
        this.openproject();
        console.log(res);
      });
      this.closeMenu();
    },
    //重命名文件
    renamefile() {
      this.FileRenamedata.fileRenameDialogShow = true;
      this.FileRenamedata.fileName = this.NodeContextdata.nodeData.label;
      this.closeMenu();
    },
    //确认重命名
    RenameFileConfirm() {
      this.api
        .RenameFile(
          this.NodeContextdata.currentNodeId,
          this.FileRenamedata.fileName
        )
        .then((res) => {
          if (res.data.code == 0) {
            this.$message({ message: res.data.data, type: "success" });
          } else {
            this.$message({ message: res.data.data, type: "error" });
          }
          this.openproject();
          this.FileRenamedata.fileRenameDialogShow = false;
        });
    },
    //取消重命名
    RenameFileCancel() {
      this.FileRenamedata.fileName = "";
      this.FileRenamedata.fileRenameDialogShow = false;
    },
    //下载文件
    downloadfile() {
      if (this.workspace.projectSelId != "") {
        console.log(123123);
        this.api.downloadFile(this.NodeContextdata.currentNodeId);
      } else {
        this.$message({ message: "请先创建或选择项目", type: "warning" });
      }
      this.closeMenu();
    },

    uploadfile(rowfile) {
      if (this.workspace.projectSelId != "") {
        if (
          this.NodeContextdata.currentNodeFolder == null ||
          this.NodeContextdata.currentNodeId == null
        ) {
          // NodeContextdata.currentNodeFolder =
          //   "W:/YangtzeDataStore/workspace/" +
          //   userInfo.id +
          //   "/" +
          //   projectdata.projectName;
          this.NodeContextdata.currentNodeFolder =
            "E:/workspace/" + this.userId + "/" + this.projectdata.projectName;
          this.NodeContextdata.currentNodeId =
            this.projectdata.projectDirectory[0].id;
        }
        let form = new FormData();
        form.append("upFile", rowfile);
        this.api
          .uploadFile(this.NodeContextdata.currentNodeId, form)
          .then((res) => {
            if (res.code == 0) {
              this.$message({ message: res.data, type: "success" });
            } else {
              this.$message({ message: res.data, type: "error" });
            }
            this.openproject();
          });
      } else {
        this.$message({ message: "请先创建或选择项目", type: "warning" });
      }
      this.closeMenu();
    },
    closeMenu() {
      this.menuContextdata.filevisible = false;
      this.menuContextdata.foldervisible = false;
    },
    //将所有结果数据加载至实验室中
    addToLab() {
      console.log("正在添加");
    },
    //保存编辑
    savedit() {
      let form = new FormData();
      form.append("content", this.codedata.code);
      this.api.saveEdit(this.Tabdata.editableTabsValue, form).then((res) => {
        console.log(res);
        if (res.code == 0) {
          this.$message({ message: res.data, type: "success" });
        } else {
          this.$message({ message: res.data, type: "error" });
        }
      });
    },
    //下载依赖
    donwloadDep() {
      this.$message({ message: "开始下载", type: "success" });
      this.Outputdata.output = "";
      this.Outputdata.output += "正在下载依赖";
      this.projectdata.isdownloading = true;
      const ws = new WebSocket(
        "ws://localhost:8082/websocket/" + this.workspace.projectSelId
      );
      let that = this;
      ws.onmessage = function (event) {
        // 接收到来自服务器的消息
        var message = event.data;
        console.log("来消息了" + message); // 将消息打印到控制台
        that.Outputdata.output += " \n";
        that.Outputdata.output += message;
      };
      this.api.donwloadDep(this.workspace.projectSelId).then((res) => {
        console.log(res);
        if (res.data.code == 0) {
          this.$message({ message: res.data.data, type: "success" });
        } else {
          this.$message({ message: res.data.data, type: "error" });
        }
        this.projectdata.isdownloading = false;
        ws.close();
        // Outputdata.output += "运行结束";
      });
    },
    //run
    runit() {
      this.$message({ message: "开始运行", type: "success" });
      this.Outputdata.output = "";
      this.Outputdata.output += "正在运行";
      this.projectdata.isrunning = true;
      //ws
      const ws = new WebSocket(
        "ws://localhost:8082/websocket/" + this.workspace.projectSelId
      );
      let that = this;
      ws.onmessage = function (event) {
        // 接收到来自服务器的消息
        var message = event.data;
        console.log("来消息了" + message); // 将消息打印到控制台
        that.Outputdata.output += " \n";
        that.Outputdata.output += message;
      };
      this.api.runDemo(this.workspace.projectSelId).then((res) => {
        this.openproject();
        if (res.data.code == 0) {
          this.$message({ message: res.data.data, type: "success" });
        } else {
          this.$message({ message: res.data.data, type: "error" });
        }
        this.projectdata.isrunning = false;
        ws.close();
        this.Outputdata.output += "\n运行结束";
      });
    },
    //stop
    stopit() {
      this.api.stopDemo(this.workspace.projectSelId).then((res) => {
        console.log(res);
        this.projectdata.isrunning = false;
      });
    },
    changeTextarea(val) {
      this.$set(this.codedata, "code", val);
      // this.codedata.code = val;
    },
  },
  watch: {
    "workspace.projectSelId": function (newValue, oldValue) {
      console.log(
        `Previous project selected: ${oldValue}, new project selected: ${newValue}`
      );
      if (oldValue != "" && oldValue != null) {
        this.api.stopContainer(oldValue).then((res) => {
          console.log(res);
        });
      }
    },
  },
  components: {
    CommonEditor: CommonEditor,
  },
  mounted() {
    this.getWorkSpaceData();
  },
};

// let code = ref("print('hello world')");
//methods
//获取workspace数据包括项目列表、用户名等

//watch,如果项目变化了的话，就把前一个项目的容器给关了
</script>

<style scoped lang="less">
.content {
  width: 100%;
  // .codemirror .cm-editor {
  //   width: 30vw;
  //   height: 50vh;
  //   margin: 0 1rem;
  //   border: 1px solid #ddd;
  // }
}
.header {
  display: flex;
  justify-content: space-between;
}
.aside {
  // border: 1px solid #dcdcdc;
  // box-shadow: --el-box-shadow-lighter;
  box-shadow: 0px 0px 6px rgba(0, 0, 0, 0.12);
  position: relative;
  .contextmenu__item {
    display: block;
    line-height: 34px;
    text-align: center;
  }
  .contextmenu__item:not(:last-child) {
    border-bottom: 1px solid rgba(0, 0, 0, 0.1);
  }
  .menu {
    position: absolute;
    background-color: #fff;
    width: 100px;
    /*height: 106px;*/
    font-size: 12px;
    color: #444040;
    border-radius: 4px;
    -webkit-box-sizing: border-box;
    box-sizing: border-box;
    border-radius: 3px;
    border: 1px solid rgba(0, 0, 0, 0.15);
    box-shadow: 0 6px 12px rgba(0, 0, 0, 0.175);
    white-space: nowrap;
    z-index: 1000;
  }
  .contextmenu__item:hover {
    cursor: pointer;
    background: #66b1ff;
    border-color: #66b1ff;
    color: #fff;
  }
}
.btnbox {
  display: flex;
  padding: 5px;
  margin-bottom: 10px;
  margin-top: 10px;
  justify-content: space-between;
  align-content: center;
  align-items: center;
  // border: 1px solid #dcdcdc;
  height: 30px;
  // background: linear-gradient(0deg, rgba(0, 0, 0, 0.02), rgba(0, 0, 0, 0.02)),
  // #fafafa;
  .left {
    line-height: 30px;
    font-size: 15px;
    font-weight: 400;
  }
  .btns {
    width: 98px;
    display: flex;
    justify-content: space-between;
    align-content: center;
    align-items: center;
    line-height: 30px;
    height: 30px;
  }
  .upload-demo {
    display: flex;
    justify-content: space-between;
    align-content: center;
    align-items: center;
  }
  .icon:hover {
    color: rgb(28, 169, 224);
    cursor: pointer;
  }
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
      border-right: 1px solid #7c7777;
      .outputbox {
        white-space: pre-wrap;
        overflow-y: scroll;
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
  .btnss {
    height: 12%;
    width: 80%;
    margin-left: 22%;
  }
}
</style>
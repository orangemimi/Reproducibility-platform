<template>
  <div class="main">
    <div class="btnList" v-if="role == 'participant'">
      <!-- <div class="btnList" > -->
      <div v-if="!isAddFolder">
        <div class="btn">
          <el-upload
            action
            :auto-upload="true"
            :show-file-list="false"
            :on-change="handleUploadFileChange"
            ref="upload"
            :http-request="submitFile"
            :max-height="tableHeight"
            multiple
            title="upload data files"
          >
            <el-button size="default">
              <el-icon><UploadFilled /></el-icon>
            </el-button>
          </el-upload>
        </div>

        <div class="btn">
          <el-button size="default" @click="addFolderShow" title="add a folder">
            <el-icon><DocumentAdd /></el-icon>
          </el-button>
        </div>
      </div>
      <div v-else>
        <el-input v-model="folderName">
          <template #suffix>
            <el-icon @click="uploadFolder"><Check /></el-icon>
            <el-icon @click="closeAddFolder"><Close /></el-icon>
          </template>
        </el-input>
      </div>
    </div>
    <div class="row-style">
      <el-table
        ref="multipleTable"
        :data="folderList"
        :span-method="arraySpanMethod"
        tooltip-effect="dark"
        style="width: 100%"
        max-height="800"
        :row-style="{ height: '0' }"
        :cell-style="{
          color: '#666',
          fontFamily: 'Arial',
          fontSize: '10px',
          padding: '0',
        }"
        :header-cell-style="{
          fontFamily: 'Arial',
          fontSize: '14px',
        }"
        row-key="id"
        :tree-props="{ children: 'children' }"
        border
        default-expand-all
        @row-click="rowClick"
        highlight-current-row
      >
        <template v-slot:empty> Please upload a file </template>

        <el-table-column label="Name" show-overflow-tooltip width="200">
          <template #default="scope">
            <i class="collapse" :class="collapseClass(scope.row)"></i>
            {{ scope.row.name }}
            <span
              v-show="
                !Object.prototype.hasOwnProperty.call(scope.row, 'isFolder')
              "
              >{{ scope.row.suffix }}</span
            >
          </template>
        </el-table-column>
        <el-table-column label="Upload time" width="120" show-overflow-tooltip>
          <template #default="scope">{{ scope.row.createTime }}</template>
        </el-table-column>
        <!--TODO-zzy -->
      </el-table>
    </div>
  </div>
</template>

<script>
import { saveData, addFolder, getFolders } from "@/api/request";
// import dataUpload from './FileUpload'; //dialogcontent
import { renderSize } from "@/utils/utils";
import { mapState } from "vuex";

export default {
  data() {
    return {
      //upload data dialog
      uploadFileDialogShow: true,
      folderList: [],
      fileItemListFromResource: [],
      projectId: this.$route.params.id,
      modelItemList: [],
      checkAll: false,
      checkedFileItemList: [],
      isIndeterminate: false,
      //table
      multipleSelection: [],
      //add folder
      isAddFolder: false,
      folderName: "",
      currentRow: "",
      // fileItemListDirect: []

      fileList: [],
      editDataDialogShow: false,
      tableHeight: 0,
    };
  },

  props: {
    scenarioId: {
      type: String,
    },
  },
  computed: {
    ...mapState({
      role: (state) => state.permission.role,
    }),
  },
  methods: {
    closeAddFolder() {
      this.isAddFolder = false;
    },
    async uploadFolder() {
      if (!this.folderName) {
        this.$message({
          message: "Please enter a file name",
          type: "warning",
        });
        return;
      }
      let form = {
        name: this.folderName,
        parent: "0",
        level: 0,
        children: [],
      };
      if (this.currentRow != "") {
        form.parent = this.currentRow.id;
        form.level = this.currentRow.level + 1;
      }
      await addFolder(form);
      this.isAddFolder = false;
      await this.getFolders();
    },
    arraySpanMethod({ row }) {
      if (row.isFolder) {
        return [1, 3];
      }
    },

    //get all the data

    async getFolders() {
      let data = await getFolders();
      // console.log(data, "dara[]0");
      this.folderList = data;
      // let folderList = data[0].children.filter(
      //   (item) => (item.tagId = this.projectId)
      // )
      // this.folderList = folderList[0].children.filter(
      //   (item) => (item.tagId = this.scenarioId)
      // )
      // todo --只在project内展示所有project的
    },

    handleUploadFileChange(file, fileList) {
      this.fileList = fileList;
      // console.log("22222", this.fileList);
    },

    // handleCurrentChange(row) {
    //   this.currentRow = row;
    // },
    rowClick(row) {
      console.log(row);
      this.currentRow = row;
    },
    // cancleCurrentRow() {
    //   this.currentRow = "";
    // },

    addFolderShow() {
      this.folderName = "";
      this.isAddFolder = true;
    },

    //上传文件到服务器
    async submitFile(fileItem) {
      if (this.currentRow != "") {
        console.log(fileItem.file);
        let param = fileItem.file;
        let uploadFileForm = new FormData();
        uploadFileForm.append("file", param);
        // console.log( "datddd",uploadFileForm,
        //   renderSize(param.size) ,
        //   this.currentRow.id,)

        let data = await saveData(
          uploadFileForm,
          renderSize(param.size),
          this.currentRow.id
        );
        console.log(data);
      } else {
        this.$alert("Please select one folder to upload data", "Warning", {});
      }
    },

    collapseClass(params) {
      return params.isFolder === true ? "el-icon-folder" : "el-icon-document";
    },
    // cancleRow() {
    //   this.currentRow = "";
    // },
  },
  async mounted() {
    this.$nextTick(() => {
      this.tableHeight = window.innerHeight - 300;
      //后面的50：根据需求空出的高度，自行调整
    });
    await this.getFolders();
  },
};
</script>

<style lang="scss" scoped>
.main {
  padding: 0px 0px;
  height: 100%;
  width: 100%;
  .row-style {
    padding: 0 0px;
    height: 100%;
    width: 100%;
    // position: relative;
  }

  .btnList {
    width: 100%;
    // padding: 0 0 0 35%;
    float: right;
    margin-right: -2px;
    margin-top: -47px;
    .btn {
      float: right;
      margin-right: 12px;
    }
  }
  /* 用来设置当前页面element全局table 选中某行时的背景色*/
  .el-table__body tr.current-row > td {
    background-color: #69a8ea !important;
    color: #fff;
  }

  .contentBottom {
    height: 40px;
    line-height: 40px;
    margin-top: 10px;

    .selectFile {
      background-color: lightgoldenrodyellow;
      float: left;
      width: 250px;
      margin: 0 0 0 10px;
      border: 1px solid #dddddd;
      border-radius: 4px;
      // border-top: 5px solid #67c23a;
    }
    .submitBtn {
      float: right;
      // width: 30%;
    }
  }
}
</style>

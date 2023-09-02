<template>
  <div class="main">
    <div class="row-style">
      <el-table
        ref="multipleTable"
        :data="folderList"
        :span-method="arraySpanMethod"
        tooltip-effect="dark"
        style="width: 100%"
        max-height="350"
        :row-style="{ height: '0' }"
        :cell-style="{ padding: '4px' }"
        row-key="id"
        :tree-props="{ children: 'dataList' }"
        border
        default-expand-all
        @current-change="handleCurrentChange"
        highlight-current-row
      >
        <template slot="empty">
          Please upload a file
        </template>

        <el-table-column label="Name" show-overflow-tooltip>
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

        <el-table-column label="File size" width="180">
          <template #default="scope">{{ scope.row.fileSize }}</template>
        </el-table-column>
        <el-table-column label="Upload time" width="200" show-overflow-tooltip>
          <template #default="scope">{{ scope.row.createTime }}</template>
        </el-table-column>
        <!--TODO-zzy -->
        <el-table-column label="Operation" width="120" show-overflow-tooltip>
          <template #default="scope">
            <i
              class="el-icon-edit-outline"
              style="margin-right:10px"
              @click="cilckEditDialog"
            />

            <i class="el-icon-download" @click="download(scope.row.value)" />
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-col :span="12">
      <div class="contentBottom">
        <div class="selectFile" v-show="currentRow != ''">
          <!-- {{ currentRow }} -->
          <div style="float:left">
            {{ currentRow.name }}{{ currentRow.suffix }}
          </div>
          <i class="el-icon-error" style="float:right;" @click="cancleRow" />
        </div>
      </div>
    </el-col>
    <el-col :span="12">
      <div class="btnList">
        <div v-if="!isAddFolder">
          <div class="btn">
            <el-upload
              action
              :auto-upload="true"
              :show-file-list="false"
              :on-change="handleUploadFileChange"
              ref="upload"
              :http-request="submitFile"
              multiple
            >
              <el-button size="mini">
                <i class="el-icon-upload"></i>
                Upload
              </el-button>
            </el-upload>
          </div>
          <div class="btn"></div>
          <div class="btn">
            <el-button size="mini" @click="addFolderShow">Add folder</el-button>
          </div>
        </div>
        <div v-else>
          <el-input v-model="folderName">
            <template #suffix>
              <i class="el-input__icon el-icon-check" @click="uploadFolder"></i>
              <i
                class="el-input__icon el-icon-close"
                @click="closeAddFolder"
              ></i>
            </template>
          </el-input>
        </div>
      </div>
    </el-col>
    <el-dialog>
      <div class="contentBottom">
        <div v-show="editDataDialogShow != ''"></div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { saveData, addFolder, getFolders } from "@/api/request";
// import dataUpload from './FileUpload'; //dialogcontent
import { renderSize } from "@/utils/utils";
import { mapState } from "vuex";

export default {
  components: {},

  data() {
    return {
      uploadFileDialogShow: true, //upload data dialog
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
    };
  },
  computed: {
    ...mapState({
      role: (state) => state.permission.role,
    }),
  },

  methods: {
    arraySpanMethod({ row }) {
      if (row.isFolder) {
        return [1, 3];
      }
    },
    cilckEditDialog() {
      this.editDataDialogShow = true;
    },
    download(val) {
      console.log(val);
    },

    downloadFileResource(data) {
      window.open(data.address);
    },

    //get all the data
    async getFolders() {
      let data = await getFolders();
      this.folderList = data;
      // console.log(this.folderList, "folferlist");
      // this.fileItemListDirect = this.getFileItemListDirect();
      // await this.getSelectedFile();
    },

    handleUploadFileChange(file, fileList) {
      this.fileList = fileList;
      // console.log("22222", this.fileList);
    },

    handleCurrentChange(row) {
      this.currentRow = row;
    },
    cancleCurrentRow() {
      this.currentRow = "";
    },

    addFolderShow() {
      this.folderName = "";
      this.isAddFolder = true;
    },
    closeAddFolder() {
      this.isAddFolder = false;
    },

    async uploadFolder() {
      let form = {
        name: this.folderName,
        parent: "0",
        children: [],
      };
      if (this.currentRow != "") {
        form.parent = this.currentRow.id;
      }
      let data = await addFolder(form);
      console.log("addfolder", data);
      this.isAddFolder = false;
      await this.getFolders();
    },

    //上传文件到服务器
    async submitFile(fileItem) {
      // console.log(param, this.fileList);
      if (this.currentRow != "") {
        console.log(param);
        let param = fileItem.file;
        let uploadFileForm = new FormData();
        uploadFileForm.append("file", param);

        let data = await saveData(
          uploadFileForm,
          this.currentRow.id,
          renderSize(param.size)
        );
        console.log(data);
      } else {
        this.$alert("Please select one folder to upload data", "Warning", {});
      }
    },

    collapseClass(params) {
      return params.isFolder === true ? "el-icon-folder" : "el-icon-document";
    },
    cancleRow() {
      this.currentRow = "";
    },
  },
  async mounted() {
    await this.getFolders();
  },
};
</script>

<style lang="scss" scoped>
.main {
  padding: 0 10px;
  height: 100%;
  width: 100%;

  .row-style {
    padding: 0 10px;
    height: 100%;
    width: 100%;
    // position: relative;
  }

  .btnList {
    margin-top: 10px;
    width: 100%;
    // padding: 0 0 0 35%;
    float: right;

    .btn {
      float: right;
      margin-right: 10px;
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

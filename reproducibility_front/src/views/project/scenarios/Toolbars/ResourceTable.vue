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
        <template v-slot:empty> Please upload a file </template>

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
          <!-- <template #default="scope">
            <el-icon style="margin-right: 10px"
              ><el-icon-edit-outline
            /></el-icon>
            <el-icon><el-icon-download /></el-icon>
            还需要一个可视化的按钮 什么kk什么的
          </template> -->
        </el-table-column>
      </el-table>
    </div>

    <el-col :span="12">
      <div class="contentBottom">
        <div class="selectFile" v-show="currentRow != ''">
          <!-- {{ currentRow }} -->
          <div style="float: left">
            {{ currentRow.name }}{{ currentRow.suffix }}
          </div>
          <el-icon style="float: right"><el-icon-error /></el-icon>
        </div>
      </div>
    </el-col>
    <el-col :span="12">
      <div class="btnList">
        <div v-if="!isAddFolder">
          <div class="btn">
            <el-button size="mini" @click="submitDataToEvent">Submit</el-button>
          </div>
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
                <el-icon><el-icon-upload /></el-icon>
                Upload
              </el-button>
            </el-upload>
          </div>

          <div class="btn">
            <el-button size="mini" @click="addFolderShow">Add folder</el-button>
          </div>
        </div>
        <div v-else>
          <el-input v-model="folderName">
            <template #suffix>
              <el-icon class="el-input__icon"><el-icon-check /></el-icon>
              <el-icon class="el-input__icon"><el-icon-close /></el-icon>
            </template>
          </el-input>
        </div>
      </div>
    </el-col>
    <br /><br /><br />
    <el-divider></el-divider>

    <div class="row-style">
      <el-table
        ref="multipleTable"
        :data="boundData"
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
        <el-table-column label="Name" prop="name" show-overflow-tooltip>
        </el-table-column>

        <el-table-column label="Produced from" prop="pname" width="180">
        </el-table-column>
        <el-table-column
          label="Description"
          width="200"
          prop="description"
          show-overflow-tooltip
        >
        </el-table-column>
        <!--TODO-zzy -->
        <el-table-column label="Operation" width="120" show-overflow-tooltip>
          <template #default>
            <el-icon><el-icon-upload /></el-icon>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <el-dialog>
      <div class="contentBottom">
        <div v-show="editDataDialogShow != ''"></div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  // EditOutline as ElIconEditOutline,
  // Download as ElIconDownload,
  Error as ElIconError,
  Upload as ElIconUpload,
  Check as ElIconCheck,
  Close as ElIconClose,
} from '@element-plus/icons-vue'
import { $emit } from '../../../../utils/gogocodeTransfer'
import { saveData, addFolder, getFolders } from '@/api/request'
// import dataUpload from './FileUpload'; //dialogcontent
import { renderSize } from '@/utils/utils'
import { mapState } from 'vuex'

export default {
  components: {
    // ElIconEditOutline,
    // ElIconDownload,
    ElIconError,
    ElIconUpload,
    ElIconCheck,
    ElIconClose,
  },
  props: {
    boundData: {
      type: Array,
    },
  },
  data() {
    return {
      folderList: [],

      //add folder
      isAddFolder: false,
      folderName: '',
      currentRow: '',
      // fileItemListDirect: []

      fileList: [],
      editDataDialogShow: false,
    }
  },
  computed: {
    ...mapState({
      role: (state) => state.permission.role,
    }),
  },
  methods: {
    arraySpanMethod({ row }) {
      if (row.isFolder) {
        return [1, 3]
      }
    },
    cilckEditDialog() {
      this.editDataDialogShow = true
    },
    download(val) {
      console.log(val)
    },

    downloadFileResource(data) {
      window.open(data.address)
    },

    //get all the data
    async getFolders() {
      let data = await getFolders()
      this.folderList = data
      // console.log(this.folderList, "folferlist");
      // this.fileItemListDirect = this.getFileItemListDirect();
      // await this.getSelectedFile();
    },

    handleUploadFileChange(file, fileList) {
      this.fileList = fileList
      // console.log("22222", this.fileList);
    },

    handleCurrentChange(row) {
      this.currentRow = row
    },
    cancleCurrentRow() {
      this.currentRow = ''
    },

    addFolderShow() {
      this.folderName = ''
      this.isAddFolder = true
    },
    closeAddFolder() {
      this.isAddFolder = false
    },

    async uploadFolder() {
      let form = {
        name: this.folderName,
        parent: '0',
        children: [],
      }
      if (this.currentRow != '') {
        form.parent = this.currentRow.id
      }
      await addFolder(form)
      this.isAddFolder = false
      await this.getFolders()
    },

    //上传文件到服务器
    async submitFile(fileItem) {
      // console.log(param, this.fileList);
      if (this.currentRow != '') {
        console.log(param)
        let param = fileItem.file
        let uploadFileForm = new FormData()
        uploadFileForm.append('file', param)

        await saveData(
          uploadFileForm,

          renderSize(param.size),
          this.currentRow.id
        )
      } else {
        this.$alert('Please select one folder to upload data', 'Warning', {})
      }
    },

    collapseClass(params) {
      return params.isFolder === true ? 'el-icon-folder' : 'el-icon-document'
    },
    cancleRow() {
      this.currentRow = ''
    },
    submitDataToEvent() {
      console.log('this.currentRow', this.currentRow)
      if (this.currentRow != '') {
        $emit(this, 'submitDataToEvent', this.currentRow)
      }
    },
  },
  async mounted() {
    await this.getFolders()
  },
  emits: ['submitDataToEvent'],
}
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

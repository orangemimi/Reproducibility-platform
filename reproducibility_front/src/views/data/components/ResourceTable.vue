<template>
  <div class="main">
    <!-- <div class="btnList" v-if="role == 'participant'"> -->
      <div class="btnList" >
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
            <el-button size="default">
              <el-icon><el-icon-upload /></el-icon>
              Upload
            </el-button>
          </el-upload>
        </div>
        <div class="btn"></div>
        <div class="btn">
          <el-button size="default" @click="addFolderShow"> <el-icon><FolderAdd /></el-icon> Add folder</el-button>
        </div>
      </div>
      <div v-else>
        <el-input v-model="folderName">
          <template #suffix>
            <el-icon class="el-input__icon" @click="uploadFolder"><el-icon-check /></el-icon>
            <el-icon class="el-input__icon" @click="closeAddFolder"><el-icon-close /></el-icon>
          </template>
        </el-input>
      </div>
    </div>
    <div class="row-style">
      <el-table
        ref="multipleTable"
        :data="folderList"
        :span-method="spanMethod"
        tooltip-effect="dark"
        style="width: 100%"
        max-height="800"
        :row-style="{ height: '0' }"
        :cell-style="{ padding: '4px' }"
        row-key="id"
        :tree-props="{ children: 'children' }"
        border
        default-expand-all
        @row-click="rowClick"
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

        <el-table-column label="File size" width="180" show-overflow-tooltip>
          <template #default="scope">{{ scope.row.fileSize }}</template>
        </el-table-column>
        <el-table-column label="Upload time" width="200" show-overflow-tooltip>
          <template #default="scope">{{ scope.row.createTime }}</template>
        </el-table-column>
        <el-table-column label="Operation" width="120" show-overflow-tooltip>
          <template #default="scope">
            <el-icon @click="clickEditDialog(scope.row)" style="margin-right: 10px"><el-icon-edit-outline/></el-icon>
            <el-icon @click="downloadFileResource(scope.row.value)"><el-icon-download /></el-icon>
        </template>
        </el-table-column>
      </el-table>
    </div>
    <el-dialog
      title="edit"
      v-model="editDataDialogShow"
    >
      <div class="contentBottom">
        <div v-show="editDataDialogShow">还不知道写啥，插个眼</div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  Upload as ElIconUpload,
  Check as ElIconCheck,
  Close as ElIconClose,
  Edit as ElIconEditOutline,
  Download as ElIconDownload,
} from '@element-plus/icons-vue'
import { saveData, addFolder, getFolders } from '@/api/request'
// import dataUpload from './FileUpload'; //dialogcontent
import { renderSize } from '@/utils/utils'
import { mapState } from 'vuex'

export default {
  components: {
    ElIconUpload,
    ElIconCheck,
    ElIconClose,
    ElIconEditOutline,
    ElIconDownload,
  },
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
      // if (row.isFolder) {
      //   return [1, 3]
      // }
    },
    spanMethod({ row, column, rowIndex, columnIndex }) {
      if (rowIndex < this.folderList.length - 1) {
        if (row[column.property] === " ") {
          let rowspan = 1;
          for (let i = rowIndex + 1; i < this.folderList.length; i++) {
            if (this.folderList[i][column.property] === " ") {
              rowspan++;
            } else {
              break;
            }
          }
          return {
            rowspan,
            colspan: 1,
          };
        }
      }
    },
    clickEditDialog(val) {
      this.editDataDialogShow = true
    },
    download(val) {
      console.log(val)
    },

    downloadFileResource(data) {
      window.open(data)
    },

    //get all the data
    async getFolders() {
      let data = await getFolders()
      this.folderList = data

    },

    handleUploadFileChange(file, fileList) {
      this.fileList = fileList
    },

    // handleCurrentChange(row) {
    //   this.currentRow = row;
    // },
    rowClick(row) {
      console.log(row)
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
      if(!this.folderName){
        this.$message({
          message: 'Please enter a file name', 
          type: 'warning' 
        })
        return
      }
      let form = {
        name: this.folderName,
        parent: '0',
        level: 0,
        children: [],
      }
      if (this.currentRow != '') {
        form.parent = this.currentRow.id
        form.level = this.currentRow.level + 1
      }
      await addFolder(form)
      this.isAddFolder = false
      await this.getFolders()
    },

    //上传文件到服务器
    async submitFile(fileItem) {
      if (this.currentRow != '') {
        console.log(fileItem.file)
        let param = fileItem.file
        let uploadFileForm = new FormData()
        uploadFileForm.append('file', param)
        // console.log( "datddd",uploadFileForm,
        //   renderSize(param.size) ,
        //   this.currentRow.id,)

        let data = await saveData(
          uploadFileForm,
          renderSize(param.size),
          this.currentRow.id
        )
        console.log(data)
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
  },
  async mounted() {
    await this.getFolders()
  },
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

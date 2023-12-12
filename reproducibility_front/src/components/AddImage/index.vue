<template>
  <div class="upload-container">
    <el-upload
      ref="upload"
      list-type="picture-card"
      :on-change="onChange"
      :http-request="submitUpload"
      :file-list="fileList"
      action
      :auto-upload="true"
    >
      <template v-slot:default>
        <el-icon><el-icon-plus /></el-icon>
      </template>

      <template v-slot:file="{ file }">
        <div>
          <img class="el-upload-list__item-thumbnail" :src="file.url" alt="" />
          <span class="el-upload-list__item-actions">
            <span
              class="el-upload-list__item-preview"
              @click="handlePictureCardPreview(file)"
            >
              <el-icon><el-icon-zoom-in /></el-icon>
            </span>
            <span
              v-if="!disabled"
              class="el-upload-list__item-delete"
              @click="handleDownload(file)"
            >
              <el-icon><el-icon-download /></el-icon>
            </span>
            <span
              v-if="!disabled"
              class="el-upload-list__item-delete"
              @click="handleRemove(file)"
            >
              <el-icon><el-icon-delete /></el-icon>
            </span>
          </span>
        </div>
      </template>
    </el-upload>
    <el-dialog v-model="dialogVisible" style="z-index: 9999">
      <img width="100%" :src="dialogImageUrl" alt="" />
    </el-dialog>
  </div>
</template>

<script>
import {
  Plus as ElIconPlus,
  ZoomIn as ElIconZoomIn,
  Download as ElIconDownload,
  Delete as ElIconDelete,
} from '@element-plus/icons-vue'
import { $on, $off, $once, $emit } from '../../utils/gogocodeTransfer'
import { post } from '@/axios'

import { mapState } from 'vuex'
export default {
  components: {
    ElIconPlus,
    ElIconZoomIn,
    ElIconDownload,
    ElIconDelete,
  },
  props: {
    fileUrl: {
      type: String,
    },
    uploadPath: {
      type: String,
    },
  },
  watch: {
    fileUrl: {
      handler(val) {
        if (val) {
          this.fileList = [{ url: this.fileUrl }]
        }
      },
      deep: true,
    },
  },
  computed: {
    ...mapState({
      userId: (state) => state.user.userId,
    }),
  },
  data() {
    return {
      dialogImageUrl: '',
      dialogVisible: false,
      disabled: false,
      uploadFileForm: new FormData(), //上传文件的form
      fileList: [], //el-upload上传的文件列表,
      dataItemList: [],
      file: {},
      // ordinaryFileUrl:
    }
  },
  methods: {
    //上传文件发生改变时
    onChange(file) {
      this.file = file
      this.fileList = [file]
    },

    handleRemove(file) {
      this.$refs.upload.clearFiles()
      console.log(file)
    },

    handlePictureCardPreview(file) {
      this.dialogImageUrl = file.url
      this.dialogVisible = true
    },

    handleDownload(file) {
      console.log(file)
    },

    async submitUpload({ file }) {
      console.log(file)
      let form = new FormData()
      form.append('pictureFile', file)
      let fileName = await post(`${this.uploadPath}`, form)
      let url = `http://${window.location.host}/r/${this.userId}/projectPicture/${fileName}`
      console.log(url)
      this.file.staticUrl = url
      $emit(this, 'uploadImgResponse', url)
    },
    clear() {
      this.file = {}
      this.fileUrl = ''
    },
  },
  mounted() {
    // this.clear();
  },
  emits: ['uploadImgResponse'],
}
</script>

<style lang="scss" scoped>
.upload-container {
  :deep(.el-upload--picture-card) {
    width: 100px;
    height: 100px;
    line-height: 110px;
  }
  :deep(.el-upload-list__item) {
    width: 100px;
    height: 100px;
  }
}
</style>

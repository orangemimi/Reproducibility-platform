<template>
  <div>
    <el-upload
      class="avatar-uploader"
      action
      :auto-upload="false"
      :on-change="change"
      ref="upload"
      :multiple="false"
      list-type="picture-card"
    >
      <!-- <img v-if="imageUrl" :src="imageUrl" class="avatar" /> -->
      <!-- <i v-else class="el-icon-plus avatar-uploader-icon"></i> -->
      <template #default>
        <el-icon><el-icon-plus /></el-icon>
      </template>
      <template #file="{ file }">
        <div>
          <img class="el-upload-list__item-thumbnail" :src="file.url" alt="" />
          <span class="el-upload-list__item-actions">
            <span
              class="el-upload-list__item-preview"
              @click="handlePictureCardPreview(file)"
            >
              <el-icon><el-icon-zoom-in /></el-icon>
            </span>
            <span class="el-upload-list__item-delete" @click="handleRemove()">
              <el-icon><el-icon-delete /></el-icon>
            </span>
          </span>
        </div>
      </template>
    </el-upload>
    <el-dialog v-model="dialogVisible" :modal="false">
      <img width="100%" :src="imageUrl" alt="" />
    </el-dialog>
  </div>
</template>

<script>
import {
  Plus as ElIconPlus,
  ZoomIn as ElIconZoomIn,
  Delete as ElIconDelete,
} from '@element-plus/icons-vue'
import { $emit } from '../../utils/gogocodeTransfer'
export default {
  components: {
    ElIconPlus,
    ElIconZoomIn,
    ElIconDelete,
  },
  data() {
    return {
      imageUrl: '',
      dialogVisible: false,
    }
  },
  methods: {
    handlePictureCardPreview(file) {
      this.imageUrl = file.url
      this.dialogVisible = true
    },
    handleRemove() {
      this.$refs.upload.$data.uploadFiles = []
    },
    change(file, fileList) {
      if (fileList.length > 1) {
        fileList.splice(0, 1)
      }
      this.imageUrl = URL.createObjectURL(file.raw)
      $emit(this, 'getfile', file)
    },
  },
  emits: ['getfile'],
}
</script>

<style lang="scss" scoped>
/*// .avatar-uploader ::v-deep .el-upload*/
//  {
//   /*//   border: 1px dashed #d9d9d9;*/ /*//   border-radius: 6px;*/ /*//   cursor: pointer;*/ /*//   position: relative;*/ /*//   overflow: hidden;*/ /*//*/
// }
//  /*// .avatar-uploader ::v-deep .el-upload:hover*/
//  {
//   /*//   border-color: #409eff;*/ /*//*/
// } /*// .avatar-uploader-icon*/
//  {
//   /*//   font-size: 28px;*/ /*//   color: #8c939d;*/ /*//   width: 178px;*/ /*//   height: 178px;*/ /*//   line-height: 178px;*/ /*//   text-align: center;*/ /*//*/
// } /*// .avatar*/
//  {
//   /*//   width: 178px;*/ /*//   height: 178px;*/ /*//   display: block;*/ /*//*/
// }
</style>

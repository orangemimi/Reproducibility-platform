<template>
  <div>
    <el-button
      v-show="disabled && input.dataServiceId != undefined"
      plain
      type="warning"
      :icon="ElIconDownload"
      @click="download()"
    ></el-button>
    <el-upload
      v-show="!disabled"
      action="/api/data_service/addByFile"
      :limit="1"
      :on-success="onSuccess"
    >
      <el-button size="small" type="primary">点击上传</el-button>
    </el-upload>
  </div>
</template>

<script>
import { Download as ElIconDownload } from '@element-plus/icons-vue'
import config from '@/config'
export default {
  data() {
    return {
      ElIconDownload,
    }
  },
  props: {
    disabled: {
      type: Boolean,
      default: false,
    },
    input: {
      type: Object,
    },
  },
  methods: {
    onSuccess({ data }) {
      this.input.dataServiceId = data.id
    },
    download() {
      window.open(
        `${config.containerURL}/data_service/fetch/${this.input.dataServiceId}`
      )
    },
  },
}
</script>

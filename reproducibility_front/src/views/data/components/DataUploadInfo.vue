<template>
  <div class="main">
    <data-upload-form
      :formType="'file'"
      :initFormData="initFormData"
      @uploadSuccess="uploadSuccess"
    ></data-upload-form>
  </div>
</template>

<script>
import { $on, $off, $once, $emit } from '../../../utils/gogocodeTransfer'
import dataUploadForm from './DataUploadForm.vue'
export default {
  components: {
    dataUploadForm,
  },
  props: {
    initFormData: {
      type: Object,
    },
  },
  data() {
    return {
      activeName: 'file',
      // form: {}
    }
  },
  methods: {
    uploadSuccess(val) {
      $emit(this, 'uploadSuccess', val)
    },
  },
  mounted() {
    // console.log(this.initFormData)
  },
  emits: ['uploadSuccess'],
}
</script>

<style lang="scss" scoped>
.main {
  height: 100%;
  width: 100%;
}
</style>

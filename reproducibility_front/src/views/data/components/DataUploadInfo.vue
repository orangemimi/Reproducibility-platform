<!-- data upload information -->
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
import dataUploadForm from "./DataUploadForm.vue";
// import addImage from '_com/AddImage';

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
      activeName: "file",
      // form: {}
    };
  },

  methods: {
    uploadSuccess(val) {
      this.$emit("uploadSuccess", val);
    },
  },

  mounted() {
    // console.log(this.initFormData)
  },
};
</script>
<style lang="scss" scoped>
.main {
  height: 100%;
  width: 100%;
}
</style>

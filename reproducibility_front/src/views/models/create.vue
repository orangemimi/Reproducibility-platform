<template>
  <div class="main-contain">
    <el-row>
      <el-col :span="24">
        <el-form ref="form" :model="form" label-width="100px" size="small">
          <el-form-item label="Name">
            <el-input v-model="form.name" />
          </el-form-item>
          <el-form-item label="Description">
            <el-input v-model="form.description" />
          </el-form-item>
          <el-form-item label="md5">
            <el-input v-model="form.md5" />
          </el-form-item>
          <el-form-item label="Privacy">
            <el-radio-group v-model="form.privacy">
              <el-radio label="public">Public</el-radio>
              <el-radio label="private">Private</el-radio>
            </el-radio-group>
          </el-form-item>

          <el-form-item label="MDL">
            <el-input
              type="textarea"
              :autosize="{ minRows: 2, maxRows: 4 }"
              placeholder="Please enter the mdl"
              v-model="form.mdl"
            >
            </el-input>
          </el-form-item>
        </el-form>
      </el-col>
    </el-row>
    <div class="title"><el-button @click="addModel">Create</el-button></div>
  </div>
</template>

<script>
import { addModelByMD5Local } from '@/api/request'
export default {
  watch: {},

  computed: {},

  data() {
    return {
      form: {
        name: '',
        description: '',
        md5: '',
        privacy: '',
        tags: [],
        thumbnail: '',
        source: '',
        type: 'service',
      },
      inputTagValue: '',
    }
  },

  methods: {
    async addModel() {
      let data = await addModelByMD5Local(this.form)
      console.log(data)
      this.$notify({
        title: 'Success',
        message: 'You have add the model service successfully!',
        type: 'success',
      })
    },

    uploadImgResponse(val) {
      this.form.thumbnail = val
    },

    handleClose(val) {
      this.form.tags.splice(this.form.tags.indexOf(val), 1)
    },

    handleInputConfirm() {
      let inputTagValue = this.inputTagValue
      if (inputTagValue) {
        this.form.tags.push(inputTagValue)
      }
      this.inputTagValue = ''
    },
  },

  mounted() {},
}
</script>

<style lang="scss" scoped>
.main-contain {
  /*//   width: 100%;*/
  .title {
    text-align: center;
    // width: 100%;
  }
}
</style>

<template>
  <el-container>
    <el-card class="main">

      <el-upload
        ref="upload"
        class="upload-demo"
        :limit="1"
        :on-exceed="handleExceed"
        :auto-upload="false"
        :http-request="submitFile"
      >

        <template #trigger>
          <div>
            <el-button type="primary"  :icon="Plus" circle />
          </div>
        </template>
        <el-button type="warning" :icon="Aim" circle @click="submitUpload" />
        <el-button type="danger" :icon="Delete" circle @click="test" />
        <el-button type="success" :icon="Check" circle @click="test" />


        <template #tip>
          <div class="uploadTips">
            limit 1 file, new file will cover the old file
          </div>
        </template>
      </el-upload>

      <el-card class="text">
        <label for="userText">Type your text:</label><br />
        <el-input
          v-model="userText"
          :rows="24"
          type="textarea"
          placeholder="Please input"
          class="textarea"
        ></el-input>
      </el-card>
    </el-card>
  </el-container>
</template>

<script lang="ts">
import { defineComponent, ref } from "vue";
// import {pdfConvert} from '@/api/request';
import { Check, Plus, Delete, Aim } from "@element-plus/icons-vue";
import { genFileId,ElMessage } from 'element-plus'
import type { UploadInstance, UploadProps, UploadRawFile } from 'element-plus'
export default defineComponent({
  setup() {
    // upload
    const userText = ref("You can find your xml here");
    const upload = ref<UploadInstance>()

    const handleExceed: UploadProps['onExceed'] = (files) => {
      upload.value!.clearFiles()
      const file = files[0] as UploadRawFile
      file.uid = genFileId()
      upload.value!.handleStart(file)

    }
    const submitFile = async(fileItem: any) =>{
      let fileContent = fileItem.file;
      let formData = new FormData();
      formData.append('pdfFile', fileContent);
      console.log(fileContent,formData,'110');
      
      // await pdfConvert(formData)
    }
    const submitUpload = () => {
      if(1){
        upload.value!.submit()
      }else{
        ElMessage({
          message: 'Please upload a file',
          type: 'warning',
        })
      }
    }

    const test = ()=>{
      console.log('happy work')
      console.log(upload)
    }
    return {
      upload,
      userText,
      Check,
      Plus,
      Delete,
      Aim,
      handleExceed,
      submitUpload,
      test,
      submitFile,
    };
  },
});
</script>

<style scoped>
* {
  padding: 0;
  margin: 0;
  line-height: 1;
  /* font-size: 14px; */
}
.uploadTips{
  color: red;
  margin:10px 0px ;
  font-size: 10px;
}
.test {
  border: 1px solid black;
}
.main {
  padding: 20px;
  width: 60vw;
  height: 60vh;
  margin-left: 20%;
}
.text {
  min-height: 40vh;
  margin-top: 20px;
}
.textarea {
  margin-top: 20px;
}
:deep(.el-upload){
  margin: 0px 8px 8px 0px;
  padding-top:0px
}

</style>
<template>
  <div>
    <el-form ref="form" :model="form" label-width="150px" size="mini">
      <el-form-item label="Name">
        <el-input v-model="form.name"></el-input>
      </el-form-item>
      <el-form-item label="Description">
        <el-input
          type="textarea"
          v-model="form.description"
          resize="none"
        ></el-input>
      </el-form-item>
      <el-form-item label="Privacy">
        <el-switch
          v-model="form.privacy"
          active-value="public"
          inactive-value="private"
          active-color="#409EFF"
          inactive-color="#67C23A"
        >
        </el-switch>
        {{ form.privacy }}
      </el-form-item>
      <el-form-item label="Tags">
        <el-tag
          :key="tag"
          v-for="tag in dynamicTags"
          closable
          :disable-transitions="false"
          @close="handleClose(tag)"
        >
          {{ tag }}
        </el-tag>

        <el-input
          class="input-new-tag"
          v-if="inputVisible"
          v-model="inputValue"
          ref="saveTagInput"
          size="small"
          @keyup.enter.native="handleInputConfirm"
          @blur="handleInputConfirm"
        ></el-input>
        <el-button v-else class="button-new-tag" size="small" @click="showInput"
          >+ New Tag</el-button
        >
      </el-form-item>
      <el-form-item label="Picture">
        <add-image @getfile="getfile" class="picture" />
      </el-form-item>
    </el-form>

    <div class="btn">
      <!-- <el-button @click="cancel" size="small">Cancel</el-button> -->
      <el-button type="primary" @click="saveProject" size="small"
        >Create</el-button
      >
    </div>
  </div>
</template>

<script>
import { saveProject, postFile } from "@/api/request";
import addImage from "_com/AddImage/index1.vue";
export default {
  components: { addImage },
  data() {
    return {
      form: {
        name: "",
        description: "",
        privacy: "public",
      },
      dynamicTags: [],
      inputVisible: false,
      inputValue: "",
      pictureFile: "",
    };
  },
  methods: {
    handleClose(tag) {
      this.dynamicTags.splice(this.dynamicTags.indexOf(tag), 1);
    },
    handleInputConfirm() {
      let inputValue = this.inputValue;
      if (this.dynamicTags.indexOf(inputValue) != -1) {
        this.$notify({
          title: "warning",
          message: "The same label exists!",
          type: "warning",
        });
      } else {
        if (inputValue) {
          this.dynamicTags.push(inputValue);
        }
        this.inputVisible = false;
        this.inputValue = "";
      }
    },
    showInput() {
      this.inputVisible = true;
      this.$nextTick(() => {
        this.$refs.saveTagInput.$refs.input.focus();
      });
    },
    getfile(val) {
      this.pictureFile = val;
    },
    async saveProject() {
      let form = new FormData();
      let pictureData = "";
      form.append("name", this.form.name);

      if (this.pictureFile != null && this.pictureFile != "") {
        form.append("datafile", this.pictureFile.raw);
        let picData = await postFile(form);
        pictureData = "http://175.27.137.60:8083/data/" + picData.data.data.id;
      }

      let jsonData = {
        project: {
          name: this.form.name,
          description: this.form.description,
          privacy: this.form.privacy,
          tags: this.dynamicTags,
          picture: this.pictureFile == null ? null : pictureData,
        },
      };
      await saveProject(jsonData);
      this.cancel();
    },

    cancel() {
      this.$emit("dialogShow", false);
    },
  },
};
</script>

<style lang="scss" scoped>
.el-tag + .el-tag {
  margin-left: 10px;
}
.input-new-tag {
  width: 90px;
  margin-left: 10px;
  vertical-align: bottom;
}
.button-new-tag {
  margin-left: 10px;
  height: 32px;
  line-height: 30px;
  padding-top: 0;
  padding-bottom: 0;
}
.picture {
  margin-top: 10px;
  margin-left: 10px;
}
.btn {
  margin-top: 10px;
  text-align: center;
}
</style>

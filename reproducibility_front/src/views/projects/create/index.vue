<template>
  <div>
    <el-form ref="form" :model="form" label-width="150px" size="default">
      <el-form-item label="Project Name">
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
          @keyup.enter="handleInputConfirm"
          @blur="handleInputConfirm"
        ></el-input>
        <el-button v-else class="button-new-tag" size="small" @click="showInput"
          >+ New Tag</el-button
        >
      </el-form-item>
      <el-form-item label="Picture">
        <add-image @getfile="getfile" class="picture" />
      </el-form-item>
      <el-form-item label="Scenario Name">
        <el-input v-model="form.scenarioName"></el-input>
      </el-form-item>
      <el-form-item label="Scenario Type">
        <el-radio v-model="form.scenarioType" label="sequentModels"
          >Sequent models</el-radio
        >
        <el-radio v-model="form.scenarioType" label="integrateTask"
          >Integrate task</el-radio
        >
        <el-radio v-model="form.scenarioType" label="dockerScenario"
          >Docker scenario</el-radio
        >
      </el-form-item>
      <el-form-item
        v-if="form.scenarioType == 'dockerScenario'"
        label="Environment "
      >
        <el-radio v-model="form.env" label="python:3.9">python:3.9</el-radio>
        <el-radio v-model="form.env" label="python:3.6">python:3.6</el-radio>
        <el-radio v-model="form.env" label="python:2.7">python:2.7</el-radio>
        <el-radio v-model="form.env" label="R">R</el-radio>
      </el-form-item>
    </el-form>

    <div class="btn">
      <!-- <el-button @click="cancel" size="small">Cancel</el-button> -->
      <el-button type="primary" @click="saveProject" size="large"
        >Create</el-button
      >
    </div>
  </div>
</template>

<script>
import { $emit } from "../../../utils/gogocodeTransfer";
import {
  saveProject,
  postFile,
  saveScenario,
  createContainer,
  updateContainerId,
  updateProject,
} from "@/api/request";
import addImage from "_com/AddImage/index1.vue";
export default {
  components: { addImage },
  data() {
    return {
      form: {
        name: "",
        description: "",
        privacy: "public",
        scenarioName: "",
        scenarioType: "sequentModels",
        env: "python:3.9",
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
      if (
        !this.form.name ||
        !this.form.scenarioType ||
        !this.form.scenarioName
      ) {
        this.$message.error("Please fill in the required fields");
        return;
      }
      let form = new FormData();
      let pictureData = "";
      form.append("name", this.form.name);
      if (this.pictureFile != null && this.pictureFile != "") {
        form.append("datafile", this.pictureFile.raw);
        let picData = await postFile(form);
        pictureData = "http://112.4.132.6:8083/data/" + picData.data.data.id;
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
      let result = await saveProject(jsonData);
      console.log(result, 1011);

      await this.setScenario(result);
      this.cancel();
      this.$router.push({
        path: `/project/${result.id}/construction`,
      });
    },
    async setScenario(project) {
      if (this.form.scenarioType != "dockerScenario") {
        this.form.env = null;
      }
      let data = await saveScenario(
        {
          projectId: project.id,
          name: this.form.scenarioName,
          type: this.form.scenarioType,
          env: this.form.env,
          resourceCollection: {
            modelList: [],
          },
        },
        "initial"
      );
      // 如果是docker场景，就需要添加容器id到场景中
      if (this.form.scenarioType == "dockerScenario") {
        let ContainerIdFormData = new FormData();
        ContainerIdFormData.append("scenarioId", data.id);
        ContainerIdFormData.append("env", this.form.env);
        let containerId = await createContainer(ContainerIdFormData);

        ContainerIdFormData.append("containerId", containerId);
        updateContainerId(ContainerIdFormData);
      }

      project.scenarioList = [data.id];
      await updateProject(project.id, project);
    },
    cancel() {
      $emit(this, "dialogShow", false);
    },
  },
  emits: ["dialogShow"],
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

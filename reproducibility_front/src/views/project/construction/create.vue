<template>
  <div class="main">
    <el-col :span="18" :offset="2">
      <el-form ref="form" :model="form" label-width="150px">
        <el-form-item label="New Scenario Name">
          <el-input v-model="form.name"></el-input>
        </el-form-item>
        <br>
        <br>
        <el-form-item label="Scenerio Type">
          <el-radio v-model="form.type" label="sequentModels"
            >Sequent models</el-radio
          >
          <el-radio v-model="form.type" label="integrateTask"
            >Integrate task</el-radio
          >
        </el-form-item>
      </el-form>
      <div class="dialog-footer">
        <!-- <el-button @click="resourceCollectDialog = false" size="default"
          >Cancel</el-button
        > -->
        <el-button type="primary" @click="setScenario()" size="default"
          >Create</el-button
        >
      </div>
    </el-col>
  </div>
</template>

<script>
import { $emit } from "../../../utils/gogocodeTransfer";
import {
  getProjectById,
  getModelsByPrivacy,
  updateProject,
  // getScenarioById,
  saveScenario,
} from "@/api/request";
export default {
  data() {
    return {
      isOverOne: false,
      projectId: this.$route.params.id, //projectId
      project: {},
      form: {
        name: "",
        type: "sequentModels",
      },

      modelList: [],

      selectedModelsWithId: [],
      currentModelList: [],

      total: 0,
      keyword: "",
      isSearching: false,
    };
  },
  methods: {
    // transfer----------
    async currentModelChange(val) {
      await this.getPublicModels(val - 1);
    },
    async getPublicModels(val) {
      // console.log(val,'001');
      // let data = await getModelsByPrivacy("public", val, 20);

      // this.total = data.totalElements;
      // this.modelList = data.content;

      // var currentModelList = this.modelList.map((value) => {
      //   return {
      //     label: value.name,
      //     key: value.id,
      //     obj: value,
      //   };
      // });
      // // 初始化数据
      // this.currentModelList = currentModelList;
    },

    // clearSearch() {
    //   this.keyword = "";
    //   // this.init();
    //   this.isSearching = false;
    // },

    init() {
      this.getPublicModels(0);
    },

    selectChange(value, type) {
      if (type == "model") {
        this.modelList = value;
      }
    },

    async setScenario() {
      $emit(this, "createStatus", "success");
      let data = await saveScenario({
        projectId: this.projectId,
        name: this.form.name,
        type: this.form.type,
        resourceCollection: {
          modelList: this.selectedModelsWithId,
        },
      });
      this.project.scenario = data.id;
      await updateProject(this.projectId, this.project);
      if (this.isOverOne) {
        $emit(this, "createStatus", "success");
      } else {
        this.$router.push({ 
          path: `/project/${this.projectId}/scenarios`,
        });
      }
    },
  },
  async mounted() {
    this.project = await getProjectById(this.$route.params.id);

    // if (this.project.scenario != null && this.project.scenario != '') {
    //   this.$router.push({
    //     path: `/project/${this.projectId}/scenarios`,
    //   })
    // }
    // console.log(this.project);
    if (
      this.project.resourceCollection != null &&
      this.project.resourceCollection.modelList != null
    ) {
      this.selectedModelsWithId = this.project.resourceCollection.modelList;
    }
    this.init();
    // this.getData();
  },
  emits: ["createStatus"],
};
</script>

<style lang="scss" scoped>
.main {
  width: 100%;
  padding: 80px  5px;
  .scenarioCard{
    .el-card__body {
      min-height: calc(100vh - 240px);
    }
  }
  .createScenario {
    overflow: auto;
    :deep(.el-dialog__body) {
      height: 801px;
      overflow: auto;
    }
  }
}
.dialog-footer {
  margin-top: 200px;
  float: right;
}
.transfer {
  .mainContainer {
    :deep(.el-transfer-panel__body) {
      height: 480px;
    }
    :deep(.el-transfer-panel) {
      width: 250px;
    }
    :deep(.el-transfer-panel__list) {
      height: 450px;
    }
    :deep(.el-transfer-panel__list.is-filterable) {
      height: 400px;
    }
    :deep(.el-transfer__buttons .el-button) {
      display: block;
      margin-left: 0;
    }
  }
}
</style>

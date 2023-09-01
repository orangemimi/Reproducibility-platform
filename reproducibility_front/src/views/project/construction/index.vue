<!--  -->
<template>
  <div class="main">
    <el-col :span="16" :offset="2">
      <el-form ref="form" :model="form" label-width="200px">
        <el-form-item label="Name">
          <el-input v-model="form.name"></el-input>
        </el-form-item>
        <el-form-item label="Scenerio Type">
          <el-radio v-model="form.type" label="sequentModels"
            >Sequent models</el-radio
          >
          <el-radio v-model="form.type" label="integrateTask"
            >Integrate task</el-radio
          >
        </el-form-item>
        <el-form-item label="Model Resources">
          <el-row>
            <el-row>
              <el-col :span="10">
                <el-input
                  placeholder="Search by name"
                  v-model="keyword"
                  class="input-with-select"
                  size="small"
                >
                  <el-button
                    slot="append"
                    icon="el-icon-search"
                    @click="searchModelByName"
                    v-if="!isSearching"
                  ></el-button>
                  <el-button
                    slot="append"
                    icon="el-icon-close"
                    @click="clearSearch"
                    v-else
                  ></el-button>
                </el-input>
              </el-col>
            </el-row>

            <div class="transfer">
              <el-transfer
                class="mainContainer"
                v-model="selectedModelsWithId"
                @change="
                  (value) => {
                    selectChange(value, 'model');
                  }
                "
                :titles="['Public models', 'Selected models']"
                :data="currentModelList"
              >
              </el-transfer>
              <el-pagination
                layout="prev, pager, next"
                :total="total"
                small
                :pager-count="5"
                :page-size="20"
                :current-page="1"
                @current-change="currentModelChange"
                class="pageClass"
                v-show="!isSearching"
              ></el-pagination>
            </div>
          </el-row>
        </el-form-item>
      </el-form>
      <div class="dialog-footer">
        <!-- <el-button @click="resourceCollectDialog = false" size="mini"
                >Cancle</el-button
              > -->
        <el-button type="primary" @click="setScenario()" size="mini"
          >Create</el-button
        >
      </div>
    </el-col>
  </div>
</template>

<script>
import {
  getProjectById,
  getModelsByPrivacy,
  getPublicModelListByIgnoreName,
  updateProject,
  // getScenarioById,
  saveScenario,
} from "@/api/request";
export default {
  data() {
    return {
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
      let data = await getModelsByPrivacy("public", val, 20);

      this.total = data.totalElements;
      this.modelList = data.content;

      var currentModelList = this.modelList.map((value) => {
        return {
          label: value.name,
          key: value.id,
          obj: value,
        };
      });
      // 初始化数据
      this.currentModelList = currentModelList;
    },

    // 穿梭搜索
    async searchModelByName() {
      let data = await getPublicModelListByIgnoreName(this.keyword);
      this.modelList = data;
      var currentModelList = this.modelList.map((value) => {
        return {
          label: value.name,
          key: value.id,
          obj: value,
        };
      });
      // 初始化数据
      this.currentModelList = currentModelList;
      this.isSearching = true;
    },
    clearSearch() {
      this.keyword = "";
      this.init();
      this.isSearching = false;
    },

    init() {
      this.getPublicModels(0);
    },

    selectChange(value, type) {
      if (type == "model") {
        this.modelList = value;
      }
    },

    async setScenario() {
      console.log("try", {
        projectId: this.projectId,
        name: this.form.name,
        type: this.form.type,
        resourceCollection: {
          modelList: this.selectedModelsWithId,
        },
      });
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

      this.$router.push({
        path: `/project/${this.projectId}/scenarios`,
      });
    },
  },
  async mounted() {
    this.project = await getProjectById(this.$route.params.id);

    if (this.project.scenario != null && this.project.scenario != "") {
      this.$router.push({
        path: `/project/${this.projectId}/scenarios`,
      });
    }
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
};
</script>

<style lang="scss" scoped>
.main {
  width: 100%;
  padding: 20px;
}

.dialog-footer {
  float: right;

  /* margin-bottom: 10px; */
}

.transfer {
  .mainContainer {
    /deep/ .el-transfer-panel__body {
      height: 480px;
    }
    /deep/ .el-transfer-panel {
      width: 250px;
    }
    /deep/ .el-transfer-panel__list {
      height: 450px;
    }
    /deep/ .el-transfer-panel__list.is-filterable {
      height: 400px;
    }
    /deep/ .el-transfer__buttons .el-button {
      display: block;
      margin-left: 0;
    }
  }
}
</style>

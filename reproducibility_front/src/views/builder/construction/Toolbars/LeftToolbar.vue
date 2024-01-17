<template>
  <div class="mainLeft">
    <el-row>
      <el-button
        v-show="scenariosToolBarShow"
        size="default"
        @click="createNewScenario"
        title="add a scenario"
      >
        <el-icon><DocumentAdd /></el-icon>
      </el-button>

      <el-row style="float: right; margin-right: 10px">
        <div style="float: left; margin: 5px 15px">Binded to reviewer</div>
        <el-tooltip
          :content="
            chosenScenario.isBinded
              ? 'Is binded to reviewer: ' + chosenScenario.isBinded
              : 'Is binded to reviewer: false'
          "
          placement="top"
        >
          <el-switch
            v-model="chosenScenario.isBinded"
            active-color="#13ce66"
            inactive-color="#ff4949"
          >
          </el-switch>
        </el-tooltip>
      </el-row>
    </el-row>
    <el-row style="margin: 10px 0">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item @click="scenariosToolBarShow = true"
          >Scenarios</el-breadcrumb-item
        >
        <el-breadcrumb-item v-show="!scenariosToolBarShow">
          {{ chosenScenario.name }}</el-breadcrumb-item
        >
      </el-breadcrumb>
    </el-row>
    <el-row>
      <div v-if="scenariosToolBarShow">
        <div
          v-for="(item, key) in allScenarioList"
          :key="key"
          @click="chooseScenario(item)"
        >
          <scenario-card :secnarioForm="item"></scenario-card>
        </div>
      </div>
      <div v-else>
        <resource-toolbar
          :chosenScenario="chosenScenario"
          @selectModel="selectModel"
          @codingOl="codingOl"
        ></resource-toolbar>
      </div>
    </el-row>
    <div class="createScenario">
      <el-dialog
        v-model="createScenarioDialog"
        width="900px"
        style="height: 600px"
        title="Create a new scenario"
        :close-on-click-modal="false"
        :destroy-on-close="true"
      >
        <create-scenario
          :isOverOne="false"
          @createStatus="createStatus"
        ></create-scenario>
      </el-dialog>
    </div>
  </div>
</template>

<script>
import {
  getProjectById,
  getScenarioById,
  getScenariosByProjectId,
} from "@/api/request";

import scenarioCard from '_com/Cards/ScenarioListCard.vue'
import createScenario from '../create.vue'
// 场景和数据选择工具
import resourceToolbar from "./ResourceToolbar.vue";
export default {
  data() {
    return {
      //projectId
      projectId: this.$route.params.id,
      project: {},
      allScenarioList: [],
      chosenScenario: {},
      currentModel: {},
      scenariosToolBarShow: true,
      createScenarioDialog: false,
      //   resourceCollectionOb
      resourceCollection: {},
    };
  },
  components: {
    scenarioCard,
    resourceToolbar,
    createScenario,
    // SelectedScenario
  },
  watch: {},
  computed: {},
  methods: {
    async init() {
      this.project = await getProjectById(this.projectId);
      this.allScenarioList = await getScenariosByProjectId(this.projectId);
      // this.allScenarioList.forEach((element) => {
      //   if (this.project.scenarioList.include(element.id)) {
      //     element.isBinded = true;
      //   }
      // });
    },

    async chooseScenario(item) {
      this.scenariosToolBarShow = false;
      let data = await getScenarioById(item.id);
      this.chosenScenario = data;
      this.scenariosToolBarShow = false;
    },

    createNewScenario() {
      this.createScenarioDialog = true;
    },

    selectModel(val) {
      this.$emit("selectModel", val, this.chosenScenario.id);
      //   this.currentModel = val;
    },
    codingOl(val) {
      this.$emit("codingOl", val, this.chosenScenario.id);
    },
    createStatus(val) {
      console.log(val, "10101");
      if (val == "success") {
        this.createScenarioDialog = false;
        this.init();
      }
    },
  },
  mounted() {
    this.init();
  },
  emits: ["selectModel", "codingOl"],
};
</script>

<style lang="scss" scoped>
.mainLeft {
  width: 320px;
  float: left; /*//   padding: 5px 10px;*/
  // .scenario {
  //   // background-color: #6060601c;
  //   // width: 300px;
  // }

  .scenarioCard {
    margin-left: 10px;

    .el-card__body {
      min-height: calc(90vh);
    }
  }
  .createScenario {
    overflow: auto;
    :deep(.el-dialog__body) {
      height: 100%;
      overflow: auto;
    }
  }
  .selectResource {
    .el-dialog__body {
      height: 800px;
      overflow: auto;
    }
  }
}
</style>

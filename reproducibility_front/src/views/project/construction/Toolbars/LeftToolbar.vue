<!--  -->
<template>
  <div class="mainLeft">
    <el-row>
      <el-button
        v-show="scenariosToolBarShow"
        icon="el-icon-document-add"
        size="mini"
        @click="createNewScenario"
        title="add a scenario"
      ></el-button>

      <el-row style="float:right;margin-right:10px">
        <div style="float:left;margin-right:10px">Binded to reviewer</div>
        <el-tooltip
          :content="'Is binded to reviewer: ' + chosenScenario.isBinded"
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
        <el-breadcrumb-item @click.native="scenariosToolBarShow = true"
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
        ></resource-toolbar>

        <el-empty description="Please add the resources first"></el-empty>
      </div>
    </el-row>
    <div class="createScenario">
      <el-dialog
        :visible.sync="createScenarioDialog"
        width="1200px"
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
  getMyModels,
} from "@/api/request";

import scenarioCard from "_com/Cards/ScenarioListCard.vue";
import createScenario from "../create.vue";
import resourceToolbar from "./ResourceToolbar";
// import SelectedScenario from "_com/Cards/SelectedScenario.vue";
export default {
  components: {
    scenarioCard,
    resourceToolbar,
    createScenario,
    // SelectedScenario
  },

  watch: {},

  computed: {},

  data() {
    return {
      projectId: this.$route.params.id, //projectId
      project: {},
      // chosenScenario: {},
      allScenarioList: [],
      chosenScenario: {},
      currentModel: {},
      scenariosToolBarShow: true,
      createScenarioDialog: false,
    
      allModelsWithUser: [],
      resourceCollection: {},
      //   resourceCollectionOb
 
    };
  },

  methods: {
    async init() {
      this.project = await getProjectById(this.projectId);
      this.allScenarioList = await getScenariosByProjectId(this.projectId);
      this.allScenarioList.forEach((element) => {
        if (element.id == this.project.scenario) {
          element.isBinded = true;
        }
      });
      //get my models
      this.allModelsWithUser = await getMyModels();
      console.log("this.allModelsWithUser", this.allModelsWithUser);
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
    createStatus(val) {
      if (val == "success") {
        this.createScenarioDialog = false;
        this.init();
      }
    },
  },

  mounted() {
    this.init();
  },
};
</script>
<style lang="scss" scoped>
.mainLeft {
  width: 300px;
  float: left;
  //   padding: 5px 10px;
  /* min-height: calc(100vh - 240px); */

  .scenario {
    // background-color: #6060601c;
    // width: 300px;
  }

  .scenarioCard {
    margin-left: 10px;

    .el-card__body {
      min-height: calc(100vh - 240px);
    }
  }
  .createScenario {
    .el-dialog__body {
      height: 200px;
      overflow: auto;
    }
  }
  .selectResource {
    .el-dialog__body {
      height: 1200px;
      overflow: auto;
    }
  }
}
</style>

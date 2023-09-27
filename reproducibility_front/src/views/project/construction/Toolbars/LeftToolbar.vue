<!--  -->
<template>
  <div class="main">
    <el-col :span="5" class="scenario">
      <el-row>
        <el-button
          v-show="scenariosToolBarShow"
          icon="el-icon-document-add"
          size="mini"
          @click="createNewScenario"
          title="add a scenario"
        ></el-button>
        <el-button
          v-show="!scenariosToolBarShow"
          icon="el-icon-document-add"
          size="mini"
          @click="addModelInScenario"
          title="add a scenario"
        ></el-button>
        <el-button
          v-show="!scenariosToolBarShow"
          icon="el-icon-document-add"
          size="mini"
          @click="addDataInScenario"
          title="add a scenario"
        ></el-button>
      </el-row>
      <el-row>
        <el-breadcrumb separator="/">
          <el-breadcrumb-item @click="scenariosToolBarShow = true"
            >Scenarios</el-breadcrumb-item
          >
          <el-breadcrumb-item
            v-show="
              !scenariosToolBarShow &&
                Object.prototype.hasOwnProperty.call(chosenScenario, 'name')
            "
          >
            {{ chosenScenario.name }}</el-breadcrumb-item
          >
        </el-breadcrumb>
      </el-row>
      <el-row v-show="scenariosToolBarShow">
        <div
          v-for="(item, key) in allScenarioList"
          :key="key"
          @click="chooseScenario(item)"
        >
          <scenario-card :secnarioForm="item"></scenario-card>
        </div>
      </el-row>
      <el-row
        v-if="
          !scenariosToolBarShow &&
            Object.prototype.hasOwnProperty.call(
              chosenScenario.resourceCollection,
              'modelList'
            )
        "
      >
        <resource-toolbar
          :modelList="chosenScenario.resourceCollection.modelList"
          @selectModel="selectModel"
        ></resource-toolbar
      ></el-row>
    </el-col>

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

    <!-- </el-row> -->
  </div>
</template>

<script>
import {
  getProjectById,
  getScenarioById,
  getScenariosByProjectId,
} from "@/api/request";

import scenarioCard from "_com/Cards/ScenarioListCard.vue";
import createScenario from "./../../construction/create.vue";

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
    };
  },

  methods: {
    async init() {
      this.project = await getProjectById(this.projectId);

      this.allScenarioList = await getScenariosByProjectId(this.projectId);

      console.log(" this.allScenarioList ", this.allScenarioList);
      this.allScenarioList.forEach((element) => {
        if (element.id == this.project.scenario) {
          element.isBinded = true;
        }
      });
    },

    async getChosenScenario() {
      this.chosenScenario = await getScenarioById(this.project.scenario);
      this.chosenScenario.isBinded = true;
      if (
        Object.prototype.hasOwnProperty.call(
          this.chosenScenario.resourceCollection,
          "modelList"
        )
      ) {
        this.currentModel = this.chosenScenario.resourceCollection.modelList[0];
      } else {
        this.currentModel = {};
      }
    },

    async chooseScenario(item) {
      this.scenariosToolBarShow = false;
      this.chosenScenario = await getScenarioById(item.id);
      console.log(this.chosenScenario, "chosenScenario");
      // this.chosenScenario.isBinded = true;
    },

    createNewScenario() {
      this.createScenarioDialog = true;
    },

    selectModel(val) {
      this.currentModel = val;
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
<style lang="scss">
.main {
  width: 100%;
  padding: 5px 10px;
  /* min-height: calc(100vh - 240px); */

  .scenario {
    background-color: #3067d61c;
  }

  .scenarioCard {
    margin-left: 10px;

    .el-card__body {
      min-height: calc(100vh - 240px);
    }
  }
  .createScenario {
    .el-dialog__body {
      height: 1200px;
      overflow: auto;
    }
  }
}
</style>

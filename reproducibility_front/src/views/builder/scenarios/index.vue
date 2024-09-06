<template>
  <div class="main">
    <!-- <el-row :gutter="20"> -->

    <el-col :span="3" class="scenario">
      <el-row> </el-row>
      <el-row>
        <h3 style="text-align: center; margin-bottom: 10px">Scenario list</h3>
        <!-- TODO-ZZY ADD NEW SCENARIO -->
        <div
          v-for="(item, key) in allScenarioList"
          :key="key"
          @click="chooseScenario(item)"
        >
          <scenario-card :secnarioForm="item"></scenario-card>
        </div>
        <el-button @click="createNewScenario"> + New scienario </el-button>
      </el-row>
    </el-col>

    <el-col :span="21">
      <div class="scenarioCard">
        <el-card>
          <el-col :span="4">
            <h3 style="text-align: left; margin-bottom: 10px">Resources</h3>

            <resource-toolbar
              :modelList="chosenScenario.resourceCollection.modelList"
              @selectModel="selectModel"
            ></resource-toolbar>
          </el-col>
          <el-col :span="20">
            <el-row style="float: right; margin-right: 10px">
              <div style="float: left; margin-right: 10px">
                Binded to reviewer
              </div>
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
            <el-row>
              <model-content
                :currentModel="currentModel"
                :initialScenarioId="chosenScenario.id"
              ></model-content>
            </el-row>
          </el-col>
        </el-card>
      </div>
    </el-col>

    <div class="createScenario">
      <el-dialog
        v-model="createScenarioDialog"
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
import createScenario from "./../construction/index.vue";

import resourceToolbar from "./Toolbars/ResourceToolbar";
import modelContent from "_com/ModelContent/index";
// import SelectedScenario from "_com/Cards/SelectedScenario.vue";
export default {
  components: {
    scenarioCard,
    resourceToolbar,
    modelContent,
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
      createScenarioDialog: false,
    };
  },

  methods: {
    async init() {
      this.project = await getProjectById(this.projectId);

      this.chosenScenario = await getScenarioById(this.project.scenario);
      this.chosenScenario.isBinded = true;
      this.currentModel = this.chosenScenario.resourceCollection.modelList[0];

      this.allScenarioList = await getScenariosByProjectId(this.projectId);
      let binded = {};
      this.allScenarioList.forEach((element, index) => {
        if (element.id == this.project.scenario) {
          element.isBinded = true;
          binded = element;
          this.allScenarioList.splice(index, 1);
        }
      });

      this.allScenarioList.unshift(binded);
    },

    async chooseScenario(item) {
      this.chosenScenario = await getScenarioById(item.id);
      this.chosenScenario.isBinded = true;
    },

    createNewScenario() {
      console.log("in");
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
  width: 100%; /*// padding: 5px 10px;*/
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

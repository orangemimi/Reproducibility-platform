<!--  -->
<template>
  <div class="main" style="margin:10px">
    <!-- <el-row :gutter="20"> -->
    <el-col :span="3" class="scenario">
      <h3 style="text-align:center;margin-bottom: 10px;">Scenario list</h3>
      <!-- TODO-ZZY ADD NEW SCENARIO -->
      <div
        v-for="(item, key) in allScenarioList"
        :key="key"
        @click="chooseScenario(item)"
      >
        <scenario-card :secnarioForm="item"></scenario-card>
      </div>
    </el-col>

    <el-col :span="21">
      <div class="scenarioCard">
        <el-card>
          <el-col :span="4">
            <h3 style="text-align:left;margin-bottom: 10px;">
              Resources
            </h3>

            <resource-toolbar
              :modelList="chosenScenario.resourceCollection.modelList"
              @selectModel="selectModel"
            ></resource-toolbar>
          </el-col>
          <el-col :span="20">
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
            <el-row>
              {{ currentModel }}
            </el-row>
          </el-col>
        </el-card>
      </div>
    </el-col>
    <!-- </el-row> -->
  </div>
</template>

<script>
import {
  getProjectById,
  getScenarioById,
  getScenariosByProjectId,
} from "@/api/request";

import ScenarioCard from "_com/Cards/ScenarioListCard.vue";

import ResourceToolbar from "./Toolbars/ResourceToolbar";
// import SelectedScenario from "_com/Cards/SelectedScenario.vue";
export default {
  components: {
    ScenarioCard,
    ResourceToolbar,
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
    };
  },

  methods: {
    async init() {
      this.project = await getProjectById(this.projectId);

      this.chosenScenario = await getScenarioById(this.project.scenario);
      this.chosenScenario.isBinded = true;
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

    selectModel(val) {
      this.currentModel = val;
    },
  },

  mounted() {
    this.init();
  },
};
</script>
<style lang="scss">
.main {
  width: 98%;
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
}
</style>

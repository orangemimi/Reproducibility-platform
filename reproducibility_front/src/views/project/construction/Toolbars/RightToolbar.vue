<!--  -->
<template>
    <div class="main">
      <!-- <el-row :gutter="20"> -->
  
      <el-col :span="5" class="scenario">
        <left-toolbar></left-toolbar>
      </el-col>
  
      <el-col :span="19">
        <div class="scenarioCard">
          <el-card>
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
              <model-content
                :currentModel="currentModel"
                :scenarioId="chosenScenario.id"
              ></model-content>
            </el-row>
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
  import modelContent from "./Toolbars/ModelContent";
  import leftToolbar from "./Toolbars/LeftToolbar";
  // import SelectedScenario from "_com/Cards/SelectedScenario.vue";
  export default {
    components: {
      modelContent,
      leftToolbar,
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
  
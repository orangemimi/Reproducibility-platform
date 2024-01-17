<!--  -->
<template>
  <div>
    <el-tabs v-model="activeName" type="card" @tab-click="handleClick">
      <el-tab-pane label="Info" name="info">
        <workflow
          @chosenInstance="chosenInstance"
          :currentScenario="initialScenario"
          :invokingType="'initial_info'"
        ></workflow>
      </el-tab-pane>

      <el-tab-pane label="Reproduction" name="reproduce" v-show="reproduceShow">
        <el-row>
          <el-row class="title">
            <el-col>{{ selectedInstance.name }}</el-col>
          </el-row>
        </el-row>
        <el-row>
          <el-col :span="12" style="padding: 20px; background-color: aliceblue">
            <div style="font-size: 18px; font-weight: 600">
              Initial computational processes:
            </div>
            <el-row>
              <model-content
                :currentModel="selectedInstance"
                :invokingType="'initial'"
                :initialScenarioId="initialScenario.id"
                style="padding-right: 5px"
              ></model-content>
            </el-row>
            <br />
            <workflow
              :currentScenario="initialScenario"
              :invokingType="'initial'"
              style="padding-right: 5px"
            ></workflow>
          </el-col>

          <el-col
            :span="12"
            style="padding: 20px; background-color: rgb(255, 253, 240)"
          >
            <div style="font-size: 18px; font-weight: 600">
              Reproduced computational processes:
            </div>
            <el-row>
              <model-content
                :currentModel="currentModel"
                :invokingType="'reproduction'"
                :initialScenarioId="initialScenario.id"
                :reproducedScenarioId="reproducedScenario.id"
                style="padding-left: 5px"
              ></model-content>
            </el-row>
            <br />
            <workflow
              :currentScenario="reproducedScenario"
              :invokingType="'reproduction'"
              style="padding-left: 5px"
            ></workflow>
          </el-col>
        </el-row>
        <el-row>
          <el-row>
            <div>
              The assessment of computational reproducibility about scenario
            </div>
          </el-row>
        </el-row>
      </el-tab-pane>
    </el-tabs>

    <el-dialog v-model="centerDialogVisible" title="Warning" width="30%" center>
      <span>
        Are you sure to create a initialScenario to implemenatate reproduction ?
      </span>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="centerDialogVisible = false">Cancel</el-button>
          <el-button type="primary" @click="setScenario"> Confirm </el-button>
        </span>
      </template>
    </el-dialog>
    <!-- {{ initialScenario }} -->
    <el-drawer v-model="visiblePop" size="50%">
      <assessment
        :initialInstanceObjectList="initialScenario.instanceObjectList"
        :reproducedInstanceObjectList="reproducedScenario.instanceObjectList"
        :scenarioId="reproducedScenario.id"
      ></assessment>
    </el-drawer>
    <div class="affix">
      <div icon="Connection" class="circleBtn" @click="changeVisiblePop">
        <div class="circleText">
          <el-icon><Pointer /></el-icon>
        </div>
      </div>
    </div>

    <!-- <div class="affix">
      <el-popover :width="1200" :visible="visiblePop">
        <template #reference>
          <div icon="Connection" class="circleBtn" @click="changeVisiblePop">
            <div class="circleText">
              <el-icon><Pointer /></el-icon>
            </div>
          </div>
        </template>
        <template #default>
          <el-row>
            <div style="font-weight: 600; font-size: 18px">
              Assessment for computational reproducibility
            </div>
          </el-row>
          <br />
          <el-row>
            <assessment
              :initialInstanceObjectList="initialScenario.instanceObjectList"
              :reproducedInstanceObjectList="
                reproducedScenario.instanceObjectList
              "
              :scenarioId="reproducedScenario.id"
            ></assessment>
          </el-row>
        </template>
      </el-popover>
    </div> -->
  </div>
</template>

<script>
import workflow from "@/views/builder/workflow/index.vue";
import modelContent from "_com/ModelContent/index.vue";
import starRate from "./components/StarRate.vue";
import assessment from "./components/Assessment.vue";
import {
  getProjectById,
  getScenarioById,
  getModelById,
  saveScenario,
  getScenariosByScenarioId,
} from "@/api/request";

export default {
  components: {
    starRate,
    workflow,
    modelContent,
    assessment,
    // affixContent
    // SelectedScenario
  },

  data() {
    return {
      myRate: "",
      visiblePop: false,
      activeName: "info",
      reproduceShow: false,
      centerDialogVisible: false,
      selectedInstance: {},
      project: {},
      currentModel: {},
      initialScenario: {},
      reproducedScenario: {},

      PCorrelationCoefficient: "",
    };
  },

  methods: {
    //合并单元格

    changeVisiblePop() {
      this.visiblePop = !this.visiblePop;
      if (this.visiblePop) {
        // this.assessReproducibility();
      }
    },

    async getExpectedInstances() {
      this.project = await getProjectById(this.$route.params.id);
      this.initialScenario = await getScenarioById(
        this.project.scenarioList[0]
      );

      this.reproducedScenario = await getScenariosByScenarioId(
        this.initialScenario.id
      );
      if (this.reproducedScenario != null) {
        this.reproduceShow = true;
        if (
          this.reproducedScenario.instanceObjectList != undefined &&
          this.reproducedScenario.instanceObjectList.length != 0
        ) {
          this.chosenInstance(this.reproducedScenario.instanceObjectList[0]); //默认显示第一个instance
        }
      }
      // console.log(this.reproducedScenario, "reproducedScenario");
      // debugger;
    },

    async chosenInstance(selected) {
      this.selectedInstance = selected;
      if (this.reproducedScenario == null) {
        this.centerDialogVisible = true;
      } else {
        this.activeName = "reproduce";
        this.currentModel = await getModelById(this.selectedInstance.modelId);
        this.reproduceShow = true;
      }
    },
    async setScenario() {
      let data = await saveScenario(
        {
          projectId: this.$route.params.id,
          name: "Reproduction || " + this.initialScenario.name,
          type: this.initialScenario.type,
          resourceCollection: this.initialScenario.resourceCollection,
        },
        this.initialScenario.id
      );
      this.activeName = "reproduce";
      this.currentModel = await getModelById(this.selectedInstance.modelId);
      this.reproduceShow = true;
    },
  },

  created() {
    this.getExpectedInstances();
  },
};
</script>
<style lang="scss" scoped>
.affix {
  text-align: right;
  left: 35px;
  bottom: 35px;
  position: fixed;
  z-index: 100;
  .circleBtn {
    background: rgb(226, 127, 29);
    border-radius: 25px;

    width: 50px;
    height: 50px;
    .circleText {
      line-height: 50px;
      text-align: center;
      font-size: 30px;
      color: #ffffff;
    }
  }
}
</style>

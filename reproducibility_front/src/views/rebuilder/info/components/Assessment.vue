<!-- assessment table -->
<template>
  <div>
    <el-tabs
      v-model="activeName"
      type="card"
      class="demo-tabs"
      @tab-click="handleClick"
    >
      <el-tab-pane label="workflow" name="workflow">
        <el-row>
          <div style="font-size: 18px; font-weight: 600">
            Selected computational processes for assessment :
          </div>
          <br />
          <br />
          <el-col :span="22" :offset="1">
            <div class="slider-demo-block">
              <el-slider
                disabled
                v-model="sliderDataWhole"
                range
                :marks="marks"
                :max="reproducedInstanceObjectList.length - 1"
                :format-tooltip="formatTooltip"
              />
            </div>
          </el-col>
        </el-row>

        <br />
        <el-divider />
        <div style="font-size: 18px; font-weight: 600">
          Indicators of metrics :
        </div>
        <br />
        <wholeWorkflow :isWhole="true"></wholeWorkflow>
        <br />
        <el-divider />
        <div style="font-size: 18px; font-weight: 600">
          Calculation of effect size :
        </div>
        <br />
        <el-row style="width: 100%">
          <assessmentInstance
            :modelInstanceList="modelInstanceList"
            @refreshAssessInstance="refreshAssessInstance"
          ></assessmentInstance>
        </el-row>
        <el-divider />
        <el-row>
          <div style="font-size: 18px; font-weight: 600">
            Select effect size for calculation :
          </div>
          <model-content
            :currentModel="assessMethod"
            :reproducedScenarioId="scenarioId"
            :invokingType="'assessment'"
            :initialInstanceObjectList="initialInstanceObjectList"
            :reproducedInstanceObjectList="reproducedInstanceObjectList"
          ></model-content>
        </el-row>
      </el-tab-pane>
      <el-tab-pane label="individual" name="individual">
        <el-row>
          <div style="font-size: 18px; font-weight: 600">
            Select one or more computational processes for assessment :
          </div>
          <br />
          <br />
          <el-col :span="20">
            <div class="slider-demo-block">
              <el-slider
                v-model="sliderData"
                range
                :marks="marks"
                :max="reproducedInstanceObjectList.length - 1"
                :format-tooltip="formatTooltip"
              />
            </div>
          </el-col>
          <el-col :span="2" :offset="1">
            <el-button type="primary" @click="confirmIntancesSelection"
              >Confirm</el-button
            >
          </el-col>
        </el-row>
        <br />
        <el-divider />
        <wholeWorkflow :isWhole="false"></wholeWorkflow>
        <br />
        <el-divider />
        <div style="font-size: 18px; font-weight: 600">
          Calculation of effect size :
        </div>
        <br />
        <el-row style="width: 100%">
          <assessmentInstance
            :modelInstanceList="modelInstanceList"
            @refreshAssessInstance="refreshAssessInstance"
          ></assessmentInstance>
        </el-row>
        <el-divider />

        <el-row>
          <div style="font-size: 18px; font-weight: 600">
            Select effect size for calculation :
          </div>
          <model-content
            :currentModel="assessMethod"
            :reproducedScenarioId="scenarioId"
            :invokingType="'assessment'"
            :initialInstanceObjectList="selectedInitialInstances"
            :reproducedInstanceObjectList="selectedReproducedInstances"
          ></model-content>
        </el-row>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import { getAssessmentMethod } from "@/api/request";
import modelContent from "_com/ModelContent/assessment.vue";
import assessmentInstance from "./AssessmentInstance.vue";
import wholeWorkflow from "./WholeWorkflow.vue";
import {
  getAssessmentInstancesInScenario,
  getRecordofSingleModel,
} from "@/api/request";

export default {
  components: {
    modelContent,
    assessmentInstance,
    wholeWorkflow,
    // SelectedScenario
  },
  props: {
    initialInstanceObjectList: {
      type: Array,
    },
    reproducedInstanceObjectList: {
      type: Array,
    },
    scenarioId: {
      type: String,
    },
  },

  watch: {
    reproducedInstanceObjectList: {
      deep: true,
      immediate: true,
      handler(val) {
        if (val.length != 0) {
          this.getSliderData();
        }
      },
    },
    scenarioId: {
      deep: true,
      immediate: true,
      handler(val) {
        if (val != null) {
          this.refreshAssessInstance();
        }
      },
    },
  },
  data() {
    return {
      startLabel: "",
      endLabel: "",
      sliderDataWhole: [0, 5],
      sliderData: [0, 1],
      marks: {},
      selected4Assessment: [],
      assessmentMethodShow: false,
      modelInstanceList: [],
      assessMethod: {},
      initialInstances: [],
      reproducedInstances: [],
      initialOutcomes: [],
      reproducedOutcomes: [],
      selectedInitialOutput: {},
      selectedreproducedOutput: {},
      assessMethodForm: [
        {
          type: "",
          indicator: "",
          initial: {},
          initial_variable: "",
          reproduced_variable: "",
          reproduced: {},
          calculation: "",
        },
      ],

      activeName: "workflow",
      selectedInitialInstances: [],
      selectedReproducedInstances: [],
    };
  },

  methods: {
    async refreshAssessInstance() {
      await this.getAssessmentInstance();
      this.$forceUpdate();
    },
    async getAssessmentInstance() {
      this.modelInstanceList = await getAssessmentInstancesInScenario(
        this.scenarioId
      );
      //refresh the instances
      this.modelInstanceList.forEach(async (instance) => {
        if (instance.status == 0 || instance.status == 1) {
          let data = await getRecordofSingleModel(instance);
          if (data != undefined) {
            instance = data.data;
          }
        }
      });
      // this.instanceDialogShow = true;
    },
    formatTooltip(val) {
      return this.reproducedInstanceObjectList[val].name;
    },
    getSliderData() {
      this.reproducedInstanceObjectList.forEach((instance, index) => {
        this.sliderData.push(index);
        let obj = { label: "Step" + index };
        this.marks[index] = obj;
      });
    },

    getRowKeys(row) {
      return row.id;
    },

    async getAssessmentMethod() {
      this.assessMethod = await getAssessmentMethod();
    },
    confirmIntancesSelection() {
      let start = this.sliderData[0];
      let end = this.sliderData[1];
      let selectedInstances1 = [];
      let selectedInstances2 = [];
      this.reproducedInstanceObjectList.forEach((instance, index) => {
        if (index >= start && index <= end) {
          selectedInstances1.push(instance);
        }
      });
      this.initialInstanceObjectList.forEach((instance, index) => {
        if (index >= start && index <= end) {
          selectedInstances2.push(instance);
        }
      });
      this.selectedReproducedInstances = selectedInstances1;
      this.selectedInitialInstances = selectedInstances2;
      this.$forceUpdate();
    },
  },

  mounted() {
    this.getAssessmentMethod();
    // this.refreshAssessInstance();
  },
};
</script>
<style lang="scss" scoped>
.rate {
  :deep(.Rate__star) {
    padding: 0;
  }
}
.slider-demo-block {
  display: flex;
  align-items: center;
}
.slider-demo-block .el-slider {
  margin-top: 0;
  margin-left: 12px;
}
</style>

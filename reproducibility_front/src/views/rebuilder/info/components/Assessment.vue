<!-- assessment table -->
<template>
  <div>
    <el-tabs v-model="activeName" type="card" class="demo-tabs">
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
                :max="
                  reproducedInstanceObjectList
                    ? reproducedInstanceObjectList.length - 1
                    : 3
                "
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
          Calculation of similarity probability :
        </div>
        <br />
        <el-row style="width: 100%">
          <assessmentInstance
            :outputShow="outputShow"
            :modelInstanceList="modelInstanceList"
            @refreshAssessInstance="refreshAssessInstance"
          ></assessmentInstance>
          <img v-if="outputShow" src="./output2.png" />
          <img v-if="outputShow" src="./output1.png" />
        </el-row>
        <el-divider />
        <el-row>
          <div style="font-size: 18px; font-weight: 600">
            Select similarity probability for calculation :
          </div>
          <div class="container">
            <el-table
              :data="tableData"
              border
              :row-style="setRowStyle"
              style="width: 100%"
            >
              <el-table-column
                prop="event"
                label="Event Name"
              ></el-table-column>
              <el-table-column
                prop="description"
                label="Description"
              ></el-table-column>
              <el-table-column prop="type" label="Type"></el-table-column>
              <el-table-column label="value">
                <template v-slot="{ row, $index }">
                  <el-upload
                    v-if="$index === 0"
                    class="upload-demo"
                    action="https://jsonplaceholder.typicode.com/posts/"
                    :file-list="row.fileList"
                  >
                    <el-button size="small" type="primary">点击上传</el-button>
                  </el-upload>
                  <el-select
                    v-else-if="row.event === 'AssessmentMethod'"
                    v-model="row.value"
                    placeholder="Please enter here"
                  >
                    <el-option
                      v-for="method in assessmentMethods"
                      :key="method"
                      :label="method"
                      :value="method"
                    ></el-option>
                  </el-select>

                  <el-input
                    v-else-if="row.type === 'parameters'"
                    v-model="row.value"
                    placeholder="Please enter here"
                  ></el-input>

                  <span v-else>
                    <el-select
                      v-model="row.value"
                      placeholder="Please enter here"
                    >
                      <el-option
                        v-for="method in assessmentMethods"
                        :key="method"
                        :label="method"
                        :value="method"
                      ></el-option>
                    </el-select>
                  </span>
                </template>
              </el-table-column>
            </el-table>

            <el-button type="primary" @click="startAssessment"
              >invoke</el-button
            >
          </div>
          <!-- <model-content
            :currentModel="assessMethod"
            :reproducedScenarioId="scenarioId"
            :invokingType="'assessment'"
            :initialInstanceObjectList="initialInstanceObjectList"
            :reproducedInstanceObjectList="reproducedInstanceObjectList"
          ></model-content> -->
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
        <div style="font-size: 18px; font-weight: 600">
          Indicators of metrics :
        </div>
        <br />
        <div v-if="selectedReproducedInstances.length == 0">
          <el-empty description="Confirm computational processes to assess" />
        </div>
        <div
          v-show="selectedReproducedInstances.length != 0"
          style="margin: 20px 20px 0 20px"
        >
          <wholeWorkflow
            :isWhole="false"
            :reproducedInstanceObjectList="selectedReproducedInstances"
            :initialInstanceObjectList="selectedInitialInstances"
            :sliderData="sliderData"
            :scenarioId="scenarioId"
          ></wholeWorkflow>
        </div>
        <br />
        <el-divider />
        <div style="font-size: 18px; font-weight: 600">
          Calculation of similarity probability :
        </div>
        <br />
        <el-row style="width: 100%">
          <assessmentInstance
            :outputShow="outputShow"
            :modelInstanceList="modelInstanceList"
            @refreshAssessInstance="refreshAssessInstance"
          ></assessmentInstance>
        </el-row>

        <el-divider />

        <el-row>
          <div style="font-size: 18px; font-weight: 600">
            Select similarity probability for calculation :
          </div>
          <div class="container">
            <el-table
              :data="tableData"
              border
              :row-style="setRowStyle"
              style="width: 100%"
            >
              <el-table-column
                prop="event"
                label="Event Name"
              ></el-table-column>
              <el-table-column
                prop="description"
                label="Description"
              ></el-table-column>
              <el-table-column prop="type" label="Type"></el-table-column>
              <el-table-column label="value">
                <template v-slot="{ row, $index }">
                  <el-upload
                    v-if="$index === 0"
                    class="upload-demo"
                    action="https://jsonplaceholder.typicode.com/posts/"
                    :file-list="row.fileList"
                  >
                    <el-button size="small" type="primary">点击上传</el-button>
                  </el-upload>
                  <el-select
                    v-else-if="row.event === 'AssessmentMethod'"
                    v-model="row.value"
                    placeholder="Please enter here"
                  >
                    <el-option
                      v-for="method in assessmentMethods"
                      :key="method"
                      :label="method"
                      :value="method"
                    ></el-option>
                  </el-select>

                  <el-input
                    v-else-if="row.type === 'parameters'"
                    v-model="row.value"
                    placeholder="Please enter here"
                  ></el-input>

                  <span v-else>
                    <el-select
                      v-model="row.value"
                      placeholder="Please enter here"
                    >
                      <el-option
                        v-for="method in assessmentMethods"
                        :key="method"
                        :label="method"
                        :value="method"
                      ></el-option>
                    </el-select>
                  </span>
                </template>
              </el-table-column>
            </el-table>

            <el-button type="primary" @click="startAssessment"
              >invoke</el-button
            >
          </div>
          <!-- <model-content
            :currentModel="assessMethod"
            :reproducedScenarioId="scenarioId"
            :invokingType="'assessment'"
            :initialInstanceObjectList="selectedInitialInstances"
            :reproducedInstanceObjectList="selectedReproducedInstances"
          ></model-content> -->
        </el-row>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import { getAssessmentMethod, getInstancesByIds } from "@/api/request";
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
        console.log(val, 6158);

        if (val && val.length != 0) {
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
      outputShow: false,
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
      tableData: [
        {
          event: "InitialResource",
          description: "Initial resource for assessment",
          type: "inputs",
          value: "ExtrapolationResults.csv",
        },
        {
          event: "ReproducedResource",
          description: "Reproduced resource for assessment",
          type: "inputs",
          value: "ExtrapolationResults.csv",
        },
        {
          event: "SelectedAttributeInInitialResource",
          description: "Selected attribute for assessment",
          type: "parameters",
          value: "",
        },
        {
          event: "SelectedAttributeInReproducedResource",
          description: "Selected attribute for assessment",
          type: "parameters",
          value: "",
        },
        {
          event: "TypeOfInitialResource",
          description: "Type of initial resource, like .csv and .xlsx",
          type: "parameters",
          value: "",
        },
        {
          event: "TypeOfReproducedResource",
          description: "Type of reproduced resource, like .csv and .xlsx",
          type: "parameters",
          value: "",
        },
        // {
        //   event: "AssessmentMethod",
        //   description: "Choose a method for calculation",
        //   type: "parameters",
        //   value: "Pearson",
        // },
        {
          event: "OutputType",
          description:
            "Data types and organization of output data such as scattered/continuous field/points/lines",
          type: "parameters",
          value: "scattered points",
        },
      ],
      assessmentMethods: [
        "Cohen's d",
        "Hedges' g",
        "Cohen's w",
        "Pearson",
        "MSE",
        "MAE",
        "RMSE",
        "r²",
        "R_squared",
        "Eta-squared",
      ],
    };
  },

  methods: {
    delay(ms) {},
    async startAssessment() {
      setTimeout(() => {
        // 3 秒后修改数据
        this.outputShow = true;
      }, 1500);
    },
    setRowStyle({ rowIndex }) {
      // 为前两行设置背景色（索引 0 和 1）
      if (rowIndex < 2) {
        return {
          backgroundColor: "#f2f9ec",
          // 需要 !important 覆盖默认样式
          "--bg-color": "#f2f9ec !important",
        };
      }
      if (rowIndex > 5) {
        return {
          backgroundColor: "#fef6d5",
          // 需要 !important 覆盖默认样式
          "--bg-color": "#fef6d5 !important",
        };
      }
      return {};
    },
    async refreshAssessInstance() {
      await this.getAssessmentInstance();
      this.$forceUpdate();
    },
    async getAssessmentInstance() {
      console.log(this.scenarioId, "in");
      // this.modelInstanceList = await getAssessmentInstancesInScenario(
      //   this.scenarioId
      // );
      this.modelInstanceList = await getInstancesByIds(
        this.initialInstanceObjectList
      );
      console.log(this.modelInstanceList, 159);

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
      console.log(this.reproducedInstanceObjectList, 15952);
      this.sliderData = [];
      this.marks = {};
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
      console.log(this.sliderData, this.marks, 101);

      let start = this.sliderData[0];
      let end = this.sliderData[1];
      let selectedInstances1 = [];
      let selectedInstances2 = [];
      // this.reproducedInstanceObjectList.forEach((instance, index) => {
      //   if (index >= start && index <= end) {
      //     selectedInstances1.push(instance);
      //   }
      // });
      // this.initialInstanceObjectList.forEach((instance, index) => {
      //   if (index >= start && index <= end) {
      //     selectedInstances2.push(instance);
      //   }
      // });
      this.modelInstanceList.forEach((instance, index) => {
        if (index >= start && index <= end) {
          selectedInstances1.push(instance);
        }
      });

      this.selectedReproducedInstances = selectedInstances1;
      this.selectedInitialInstances = selectedInstances1;
      this.$forceUpdate();
    },
  },

  async mounted() {
    console.log(this.scenarioId, 15941);
    // this.modelInstanceList = await getAssessmentInstancesInScenario(
    //   this.scenarioId
    // );
    this.modelInstanceList = await getInstancesByIds(
      this.initialInstanceObjectList
    );
    console.log(this.modelInstanceList, 15942);

    await this.getAssessmentMethod();
    await this.refreshAssessInstance();
    console.log("Assessment Mounted");
  },
};
</script>
<style lang="scss" scoped>
.container {
  padding: 20px;
}

.info-box {
  margin-bottom: 15px;
  padding: 10px;
}

.el-button {
  margin-top: 10px;
}
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
 
<template>
  <div>
    <div v-show="isWhole">
      <el-table :data="workflowMetrics" border style="width: 1170px">
        <el-table-column prop="criteria" label="Dimension" width="180" />
        <el-table-column prop="level" label="Level" width="110">
          <template #default="scope">
            <tr>
              <div>
                <rate
                  :length="scope.row.level.length"
                  v-model="scope.row.selectLevel"
                  @after-rate="onAftereRate"
                  class="rate"
                />
              </div>
            </tr>
          </template>
        </el-table-column>
        <el-table-column prop="level" label="Note">
          <template #default="scope">
            <tr>
              <div
                v-for="(item, index) in scope.row.level"
                :key="index"
                v-show="index + 1 == scope.row.selectLevel"
              >
                {{ item.description }}
              </div>
            </tr>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <div v-show="!isWhole">
      <el-timeline>
        <el-timeline-item
          v-for="(step, index) in reproducedInstanceObjectList"
          :key="index"
          :timestamp="step.name"
          placement="top"
          color="#0bbd87"
          type="success"
          :hollow="true"
        >
          <el-row style="display: flex; flex-wrap: nowrap">
            <div style="float: left; width: 930px">
              <el-tag>{{ "Step: " + index }}</el-tag>
            </div>

            <el-button
              @click="saveIndividualIndicators(step, step.name)"
              style="float: right; margin-right: 2px"
              type="primary"
              plain
              size="small"
              :disabled="index > stepMaxSave"
              >Save</el-button
            >
          </el-row>
          <el-card>
            <!-- {{ filteredData(step, index) }} -->
            <el-table
              :ref="step.name"
              :data="filteredData(step, index)"
              border
              style="width: 1170px"
              :span-method="objectSpanMethod"
              :key="step.name"
            >
              <el-table-column prop="dimension" label="Dimension" width="180" />
              <el-table-column prop="criteria" label="Criteria" width="280" />
              <el-table-column prop="level" label="Level" width="110">
                <template #default="scope">
                  <tr>
                    <div>
                      <rate
                        @click="console.log(scope.row, index)"
                        :length="scope.row.level.length"
                        v-model="scope.row.selectLevel"
                        class="rate"
                      />
                    </div>
                  </tr>
                </template>
              </el-table-column>
              <el-table-column prop="level" label="Note">
                <template #default="scope">
                  <tr>
                    <div
                      v-for="(item, index) in scope.row.level"
                      :key="index"
                      v-show="index + 1 == scope.row.selectLevel"
                    >
                      {{ item.description }}
                    </div>
                  </tr>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-timeline-item>
      </el-timeline>
    </div>
  </div>
</template>

<script>
import {
  getAssessmentInstancesInScenario,
  getRecordofSingleModel,
  saveAssessment,
  getAssessment,
} from "@/api/request";
import { initSetTimeOut } from "@/utils/utils";
export default {
  components: {},
  props: {
    sliderData: {
      type: Array,
    },
    isWhole: {
      type: Boolean,
    },
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
    scenarioId: {
      deep: true,
      immediate: true,
      handler(val) {
        // await this.$nextTick(async () => {
        if (val != null && val != undefined) {
          this.getAssessmentMetrics();
        }
        // });
      },
    },

    // assessmentMetricsList: {
    //   deep: true,
    //   immediate: true,
    //   handler(val) {
    //     if (val != undefined && val.length != 0) {
    //       let data = val.slice(-1);
    //       this.currentAssessStep = data[0].step;
    //       this.assessReproducibilityIndividual();
    //     }
    //   },
    // },

    sliderData: {
      deep: true,
      immediate: true,
      handler(val) {
        if (val != undefined && val.length != 0) {
          this.assessReproducibility();
          // this.assessReproducibilityIndividual();
        }
      },
    },
  },

  computed: {
    getCurrentMetrics(index) {
      let data = this.assessmentMetricsList.filter(
        (ass) => ass.modelId == index.modelId
      );
      return data.outputs;
    },
  },

  data() {
    return {
      // filteredData:[],
      stepMaxSave: 0,
      assessmentMetricsList: [],
      currentAssessStep: 0,
      modelInstanceList: [],
      individualMetrics: [
        {
          dimension: "Re-performability",
          criteria: "Accessibility of resources",

          level: [
            {
              name: 0,
              description: "Unavailable ",
            },
            {
              name: 1,
              description:
                "Available through non-provided information in materials (such as request or expert experience)",
            },
            {
              name: 2,
              description:
                "Available through provided information in materials",
            },
          ],
          selectLevel: "",
        },
        {
          dimension: "Re-performability",
          criteria: "Usability of model resources",

          level: [
            {
              name: 0,
              description:
                "Inoperative of model resources (e.g., failure to execute owing to environment setting issues, or without enough resource elements or inadequate information to configure the data resources to inputs in model resources)",
            },
            {
              name: 1,
              description:
                "Operative of model resources through non-provided information in materials (such as request or expert experience)",
            },
            {
              name: 2,
              description:
                "Operative of model resources (including achieving related outcomes with test/example data)",
            },
          ],
          selectLevel: "",
        },
        {
          dimension: "Re-performability",
          criteria: "Availability of outcomes",
          level: [
            {
              name: 0,
              description: "Unable to achieve outputs",
            },
            {
              name: 1,
              description: "Able to achieve outputs",
            },
          ],
          selectLevel: 0,
        },

        ////consistency
        {
          dimension: "Consistency",
          criteria: "Consistency of resource preparation",
          level: [
            {
              name: 0,
              description:
                "Different (including same resources with different forms, such as in Python file or in service)",
            },
            {
              name: 1,
              description:
                "Similar resources with different forms (especially focus on model resources, such as in Python file or in service)",
            },
            {
              name: 2,
              description: "Identical resources",
            },
          ],
          selectLevel: 0,
        },
        {
          dimension: "Consistency",
          criteria: "Consistency of simulation calculation",
          level: [
            {
              name: 0,
              description: "Different calculation",
            },
            {
              name: 1,
              description:
                "Consistent calculation (including different configuration without impacting the outputs)",
            },
          ],
          selectLevel: "",
        },
        {
          dimension: "Consistency",
          criteria: "Consistency of output analysis",
          level: [
            {
              name: 0,
              description: "Unacceptable",
            },
            {
              name: 1,
              description: "Acceptable",
            },
          ],
          selectLevel: 0,
        },
      ],
      workflowMetrics: [
        {
          criteria: "Re-performability",
          level: [
            {
              name: 0,
              description: "Unavailabile to acahieve final outputs",
            },
            {
              name: 1,
              description:
                "Availabile to acahieve final outputs (With the usage of other resources)",
            },
            {
              name: 2,
              description:
                "Availabile to acahieve final outputs (With the usage of initial resources)",
            },
          ],
          selectLevel: 0,
        },
        {
          criteria: "Consistency",
          level: [
            {
              name: 0,
              description: "Unacceptable",
            },
            {
              name: 1,
              description: "Acceptable",
            },
          ],
          selectLevel: 0,
        },
      ],
    };
  },

  methods: {
    filteredData(step, index) {
      let individualMetrics = [
        {
          dimension: "Re-performability",
          criteria: "Accessibility of resources",

          level: [
            {
              name: 0,
              description: "Unavailable ",
            },
            {
              name: 1,
              description:
                "Available through non-provided information in materials (such as request or expert experience)",
            },
            {
              name: 2,
              description:
                "Available through provided information in materials",
            },
          ],
          selectLevel: "",
        },
        {
          dimension: "Re-performability",
          criteria: "Usability of model resources",

          level: [
            {
              name: 0,
              description:
                "Inoperative of model resources (e.g., failure to execute owing to environment setting issues, or without enough resource elements or inadequate information to configure the data resources to inputs in model resources)",
            },
            {
              name: 1,
              description:
                "Operative of model resources through non-provided information in materials (such as request or expert experience)",
            },
            {
              name: 2,
              description:
                "Operative of model resources (including achieving related outcomes with test/example data)",
            },
          ],
          selectLevel: "",
        },
        {
          dimension: "Re-performability",
          criteria: "Availability of outcomes",
          level: [
            {
              name: 0,
              description: "Unable to achieve outputs",
            },
            {
              name: 1,
              description: "Able to achieve outputs",
            },
          ],
          selectLevel: 0,
        },

        ////consistency
        {
          dimension: "Consistency",
          criteria: "Consistency of resource preparation",
          level: [
            {
              name: 0,
              description:
                "Different (including same resources with different forms, such as in Python file or in service)",
            },
            {
              name: 1,
              description:
                "Similar resources with different forms (especially focus on model resources, such as in Python file or in service)",
            },
            {
              name: 2,
              description: "Identical resources",
            },
          ],
          selectLevel: 0,
        },
        {
          dimension: "Consistency",
          criteria: "Consistency of simulation calculation",
          level: [
            {
              name: 0,
              description: "Different calculation",
            },
            {
              name: 1,
              description:
                "Consistent calculation (including different configuration without impacting the outputs)",
            },
          ],
          selectLevel: "",
        },
        {
          dimension: "Consistency",
          criteria: "Consistency of output analysis",
          level: [
            {
              name: 0,
              description: "Unacceptable",
            },
            {
              name: 1,
              description: "Acceptable",
            },
          ],
          selectLevel: 0,
        },
      ];

      let currentMetrics = [];
      // // this.tableKey = !this.tableKey;

      if (
        this.assessmentMetricsList != undefined &&
        this.assessmentMetricsList.length > 0
      ) {
        let maxtMetrics = this.assessmentMetricsList.slice(-1);
        this.currentAssessStep = maxtMetrics[0].step + 1;

        let currentStep = this.getModelStep(step); //
        if (currentStep == maxtMetrics[0].step + 1) {
          console.log(currentStep, step.name, "1");
          return this.assessReproducibilityIndividual();
        }
        if (currentStep > maxtMetrics[0].step + 1) {
          console.log(currentStep, step.name, "2");
          return individualMetrics;
        }

        //get metrics from the assessmentList
        let currentMetrics = this.assessmentMetricsList.filter(
          (me) => me.modelId == step.modelId
        );
        // console.log(currentMetrics[0]);
        if (currentMetrics.length != 0) {
          let out = currentMetrics[0].outputs;
          individualMetrics[0].selectLevel = out[0].selectLevel;
          individualMetrics[1].selectLevel = out[1].selectLevel;
          individualMetrics[2].selectLevel = out[2].selectLevel;
          individualMetrics[3].selectLevel = out[3].selectLevel;
          individualMetrics[4].selectLevel = out[4].selectLevel;
          individualMetrics[5].selectLevel = out[5].selectLevel;

          console.log(individualMetrics, index, "11");

          return individualMetrics;
        }
      } else {
        this.currentAssessStep = 0;
        // this.assessReproducibilityIndividual();
        console.log(22);

        return this.assessReproducibilityIndividual(index);
      }

      // if (this.currentMetrics != undefined && this.currentMetrics.length != 0) {
      //   individualMetrics[0].selectLevel = this.currentMetrics[0].selectLevel;
      //   individualMetrics[1].selectLevel = this.currentMetrics[1].selectLevel;
      //   individualMetrics[2].selectLevel = this.currentMetrics[2].selectLevel;
      //   individualMetrics[3].selectLevel = this.currentMetrics[3].selectLevel;
      //   individualMetrics[4].selectLevel = this.currentMetrics[4].selectLevel;
      //   individualMetrics[5].selectLevel = this.currentMetrics[5].selectLevel;

      //   return individualMetrics;
      // } else {
      //   return this.individualMetrics;
      // }
    },
    getCurrentStepMetrics(model) {
      let step = this.getModelStep(model);
      let current = this.assessmentMetricsList.filter(
        (assess) => assess.modelId == model.modelId
      );
      if (current != undefined && current.length > 0) {
        return current[0].outputs;
      } else {
        return [];
      }
    },
    async getAssessmentMetrics() {
      // console.log(this.scenarioId, "assessmentMetricsList");
      if (this.scenarioId != undefined && this.scenarioId != null) {
        let data = await getAssessment(this.scenarioId);
        // console.log(data);
        this.assessmentMetricsList = data;
        // console.log(this.assessmentMetricsList, "assessmentMetricsList");
        if (
          this.assessmentMetricsList.length == 0 ||
          this.assessmentMetricsList == null
        ) {
          this.stepMaxSave = 0;
        } else {
          this.stepMaxSave = this.assessmentMetricsList.length;
        }
      }
    },

    getModelStep(model) {
      let step = 0;
      this.reproducedInstanceObjectList.forEach((obj, index) => {
        if (obj.modelId == model.modelId) {
          step = index;
        }
      });
      return step;
    },

    async saveIndividualIndicators(model, tableName) {
      let step = this.getModelStep(model);
      let form = {
        reproducedScenarioId: this.scenarioId,
        // purpose: "consistency", //consistency
        object: "model",
        modelId: model.modelId,
        outputs: this.$refs[tableName][0].individualProcessMetricsIndividual,
        step: step,
      };
      console.log(form);
      if (
        this.assessmentMetricsList == null ||
        this.assessmentMetricsList == undefined ||
        this.assessmentMetricsList.length == 0
      ) {
        let data = await saveAssessment(form);
        console.log(data);
      } else {
      }
    },

    getAssessmentList() {},
    onAftereRate() {
      console.log(this.myRate);
    },

    objectSpanMethod({ rowIndex, columnIndex }) {
      if (columnIndex === 0) {
        // 里边的判断需要覆盖所有显示出来的单元格
        if (rowIndex === 0) {
          return {
            rowspan: 3, // 合并几列
            colspan: 1, // 合并几行
          };
        }
        if (rowIndex === 3) {
          return {
            rowspan: 3,
            colspan: 1,
          };
        } else {
          // 这个是处理不显示的单元格（也就是删除的单元格），将他们删除掉，也可以说是删除占位，不写这个会把这些元素挤到其他位置，造成布局错乱
          return {
            rowspan: 0,
            colspan: 0,
          };
        }
      }
    },

    assessReproducibility() {
      let initialInstanceObjects = this.initialInstanceObjectList;
      let reproducedInstanceObjects = this.reproducedInstanceObjectList;
      let reperformability = 0;
      let stepOrder = false;
      let lengthEqual =
        initialInstanceObjects.length == reproducedInstanceObjects.length
          ? true
          : false;
      if (lengthEqual) {
        reproducedInstanceObjects.filter((reInstance, index) => {
          stepOrder =
            reInstance.modelId == initialInstanceObjects[index].modelId
              ? true
              : false;
          if (stepOrder) {
            // console.log("consistent step order");
            reperformability = 2;
          } else {
            //judge the outputs are same
            if (
              initialInstanceObjects.some(
                (initInstance) => initInstance.id == reInstance.id
              )
            ) {
              reperformability = 0;
            }
          }
        });
      } else {
        stepOrder = false;
        reperformability = 0;
      }
      this.workflowMetrics.filter((data) => {
        if (data.criteria == "Re-performability") {
          data.selectLevel = reperformability + 1;
        }
      });
    },

    assessReproducibilityIndividual(step) {
      // let startProbe;
      // let endProbe;
      let assess = {};
      // if (this.sliderData.length == 2) {
      //   startProbe = this.sliderData[0];
      //   endProbe = this.sliderData[1];
      // }
      // let currentAssessStep = this.currentAssessStep;
      let currentAssessStep = step;
      // debugger;
      console.log(currentAssessStep, 33);
      if (currentAssessStep == undefined) {
        return;
      }

      assess = this.assessNextStep(currentAssessStep);

      this.individualMetrics.filter((data) => {
        if (data.criteria == "Consistency of resource preparation") {
          if (assess.inputDataConsistency && assess.parameterConsistency) {
            data.selectLevel = 3;
          } else {
            data.selectLevel = 0;
          }
        }
        if (data.criteria == "Consistency of simulation calculation") {
          if (assess.stepOrder) {
            data.selectLevel = 2;
          } else {
            data.selectLevel = 0;
          }
        }
        if (data.criteria == "Accessibility of resources") {
          data.selectLevel = assess.accessibleResource;
        }
        if (data.criteria == "Usability of model resources") {
          data.selectLevel = assess.usableResource;
        }
        if (data.criteria == "Availability of outcomes") {
          data.selectLevel = assess.availableOut;
        }
        if (data.criteria == "Consistency of output analysis") {
          data.selectLevel = 0;
        }
      });
      return this.individualMetrics;
    },
    assessNextStep(index) {
      let initialInstanceObjects = this.initialInstanceObjectList;
      let reproducedInstanceObjects = this.reproducedInstanceObjectList;
      let stepOrder = false;
      let inputDataConsistency;
      let parameterConsistency;
      let accessibleResource;
      let usableResource;
      let availableOut;
      //-----assess step1
      let reInstance = reproducedInstanceObjects[index];

      if (reInstance.status == 2) {
        accessibleResource = 3;
        usableResource = 3;
        availableOut = 2;
      }
      let initialInstanceCorresponding = initialInstanceObjects[index];

      stepOrder =
        reInstance.modelId == initialInstanceCorresponding.modelId
          ? true
          : false; // get the Consistency of simulation calculation of step 1

      inputDataConsistency = this.assessIntermediateConsistency(
        reInstance.behavior[0].inputs,
        initialInstanceCorresponding.behavior[0].inputs,
        "input"
      );

      parameterConsistency = this.assessIntermediateConsistency(
        reInstance.behavior[0].parameters,
        initialInstanceCorresponding.behavior[0].parameters,
        "parameter"
      );
      return {
        accessibleResource: accessibleResource,
        usableResource: usableResource,
        availableOut: availableOut,
        stepOrder: stepOrder,
        inputDataConsistency: inputDataConsistency,
        parameterConsistency: parameterConsistency,
      };
      //------assess step1
    },
    getIntermediateOutStep(dataId) {
      let outConsistency;
      // debugger;
      this.initialInstanceObjectList.forEach((instance) => {
        instance.behavior[0].outputs.forEach((out) => {
          if (out.dataId == dataId) {
            let data = this.assessmentMetricsList.filter(
              (ass) => ass.modelId == instance.modelId
            );
            outConsistency = data[0].outputs[5].selectLevel;
            //get Assessment
          }
        });
      });
      console.log(outConsistency);
      // debugger;
      return outConsistency;
    },

    assessIntermediateConsistency(initialList, reproducedList, type) {
      let flagConsistency = true;

      initialList.forEach((reinput) => {
        let data = reproducedList.filter(
          (input) => input.eventId == reinput.eventId
        );
        let iniInput = data[0];
        if (type == "input") {
          if (!iniInput.datasetItem.isIntermediate) {
            if (iniInput.dataId != reinput.dataId) {
              flagConsistency = false; //judge the Consistency of resource preparation
            }
          } else {
            // debugger;
            //如果是中间数据，应该比较上一个step的outputconsistency
            let data = this.getIntermediateOutStep(iniInput.dataId);
            if (data == 0) {
              flagConsistency = false;
            } else {
              flagConsistency = true;
            }
          }
        } else {
          if (
            false &&
            iniInput.datasetItem?.UdxDeclarationNew[0]?.parameterValue !=
              reinput.datasetItem?.UdxDeclarationNew[0]?.parameterValue
          ) {
            flagConsistency = false; //judge the Consistency of resource preparation
          }
        }
      });
      return flagConsistency;
    },
  },

  mounted() {
    // this.getAssessmentMetrics();
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

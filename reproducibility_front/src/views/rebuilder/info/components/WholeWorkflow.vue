<!--  -->
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
                  v-model="scope.row.level.selectLevel"
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
                v-show="index + 1 == scope.row.level.selectLevel"
              >
                {{ item.description }}
              </div>
            </tr>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <div v-show="!isWhole">
      <el-table
        :data="individualProcessMetricsIndividual"
        border
        style="width: 1170px"
        :span-method="objectSpanMethod"
      >
        <el-table-column prop="dimension" label="Dimension" width="180" />
        <el-table-column prop="criteria" label="Criteria" width="280" />
        <el-table-column prop="level" label="Level" width="110">
          <template #default="scope">
            <tr>
              <div>
                <rate
                  :length="scope.row.level.length"
                  v-model="scope.row.level.selectLevel"
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
                v-show="index + 1 == scope.row.level.selectLevel"
              >
                {{ item.description }}
              </div>
            </tr>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script>
export default {
  components: {},
  props: {
    isWhole: {
      type: Boolean,
    },
  },
  watch: {},

  computed: {},

  data() {
    return {
      modelInstanceList: [],
      individualProcessMetricsIndividual: [
        {
          dimension: "Re-performability",
          criteria: "Accessibility of resources",

          level: [
            {
              name: 0,
              description:
                "Unavailable (including available upon request or expert experience)",
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
          selectLevel: "",
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
          selectLevel: "",
        },
        {
          dimension: "Consistency",
          criteria: "Usability of model resources",
          level: [
            {
              name: 0,
              description:
                "Different (including same resources with different forms, such as in Python file or in service)",
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
          selectLevel: "",
        },

        ,
      ],
      workflowMetrics: [
        {
          criteria: "Re-performability",
          level: [
            {
              name: "[0]",
              description: "Unavailabile to acahieve final outputs",
            },
            {
              name: "[1]",
              description:
                "Availabile to acahieve final outputs (With the usage of other resources)",
            },
            {
              name: "[2]",
              description:
                "Availabile to acahieve final outputs (With the usage of initial resources)",
            },
          ],
          selectLevel: "",
        },
        {
          criteria: "Consistency",
          level: [
            {
              name: "[0]",
              description: "Unacceptable",
            },
            {
              name: "[1]",
              description: "Acceptable",
            },
          ],
          selectLevel: "",
        },
      ],
    };
  },

  methods: {
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
    // assessReproducibility() {
    //   let initialInstanceObjects = this.initialInstanceObjectList;
    //   let reproducedInstanceObjects = this.reproducedInstanceObjectList;
    //   let reperformability = "[0]";
    //   let stepOrder = false;
    //   let lengthEqual =
    //     initialInstanceObjects.length == reproducedInstanceObjects.length
    //       ? true
    //       : false;
    //   if (lengthEqual) {
    //     reproducedInstanceObjects.filter((reInstance, index) => {
    //       stepOrder =
    //         reInstance.modelId == initialInstanceObjects[index].modelId
    //           ? true
    //           : false;
    //       if (stepOrder) {
    //         console.log("consistent step order");
    //         reperformability = "[2]";
    //       } else {
    //         //judge the outputs are same
    //         if (
    //           initialInstanceObjects.some(
    //             (initInstance) => initInstance.id == reInstance.id
    //           )
    //         ) {
    //           reperformability = "[0]";
    //         }
    //       }
    //     });
    //   } else {
    //     stepOrder = false;
    //     reperformability = "[0]";
    //   }
    //   this.workflowMetrics.filter((data) => {
    //     if (data.criteria == "Re-performability") {
    //       data.selectLevel = reperformability;
    //     }
    //   });
    //   // console.log(this.workflowMetrics);
    // },
  },

  mounted() {},
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

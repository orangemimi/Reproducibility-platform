<!--  -->
<template>
  <div>
    <el-table
      :data="modelInstanceList"
      style="width: 100%"
      :row-class-name="tableRowClassNameofInstance"
      @row-click="rowClick"
    >
      <el-table-column label="name" prop="behavior" width="200">
        <template v-slot="scope">
          <!-- {{ scope.row.behavior.parameters }} -->
          <el-tag type="success" disable-transitions>
            {{ getAssessmentMethod(scope.row.behavior) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="createTime" prop="createTime" width="190">
      </el-table-column>
      <el-table-column label="statusEnum" prop="status" width="120">
        <template v-slot="scope">
          <el-tag
            :type="
              getStatus(scope.row.status) == 'initialized' ||
              getStatus(scope.row.status) == 'started'
                ? 'primary'
                : getStatus(scope.row.status)
            "
            disable-transitions
          >
            {{ getStatus(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="Value" prop="assessmentValue" width="120">
        <template v-slot="scope">
          {{ scope.row.behavior[0].outputs[0].datasetItem.assessmentValue }}
        </template>
      </el-table-column>
      <el-table-column label="Operation" prop="Operation" width="120">
        <template v-slot="scope">
          <el-button
            @click="getInstances"
            :disabled="
              scope.row.behavior[0].outputs[0].datasetItem.assessmentValue !=
                '' ||
              scope.row.behavior[0].outputs[0].datasetItem.assessmentValue !=
                undefined
            "
            >Refresh</el-button
          >
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
export default {
  props: {
    modelInstanceList: {
      type: Array,
    },
  },

  data() {
    return {
      modelItem: {},
      currentInstance: {},
    };
  },

  methods: {
    getInstances() {
      this.$emit("refreshInstance", true);
    },
    getAssessmentMethod(behavior) {
      return behavior[0].parameters[4].datasetItem.UdxDeclarationNew[0]
        .parameterValue;
    },
    filterCurrentAllEventsWithStates(state) {
      let array = [];
      this.expandRows = [];
      state.inputs.forEach((item) => {
        item.stateName = state.name;
        item.type = "inputs";
        array.push(item);
      });
      state.parameters.forEach((item) => {
        item.stateName = state.name;
        item.type = "parameters";
        // debugger;
        array.push(item);
        this.expandRows.push(item.eventId);
      });
      state.outputs.forEach((item) => {
        item.stateName = state.name;
        item.type = "outputs";
        array.push(item);
      });
    },

    getStatus(status) {
      let flag = "success";
      status == 2
        ? (flag = "success")
        : status == -1
        ? (flag = "error")
        : status == 0
        ? (flag = "initialized")
        : status == 1
        ? (flag = "started")
        : (flag = "error");
      return flag;
    },
    rowClick(row) {
      this.currentInstance = row;
    },
    closeInstanceDialog() {
      this.currentInstance = {};
    },
  },

  mounted() {},
};
</script>
<style lang="scss" scoped></style>

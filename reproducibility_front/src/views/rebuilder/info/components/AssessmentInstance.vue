<!--  -->
<template>
  <div>
    <el-row style="width: 100%">
      <el-col
        :span="8"
        v-for="(instance, index) in modelInstanceList"
        :key="index"
        style="padding: 5px"
      >
        <el-card>
          <el-form
            ref="instance"
            :model="instance"
            label-width="170px"
            label-position="left"
          >
            <el-form-item label="Effect size: ">
              <el-tag
                type="info"
                disable-transitions
                v-if="getAssessmentMethod(instance.behavior) == 'Pearson'"
                >Pearson r</el-tag
              >
              <el-tag type="success" disable-transitions v-else>
                {{ getAssessmentMethod(instance.behavior) }}
              </el-tag>
            </el-form-item>
            <el-form-item label="Calculation: ">
              <el-tag type="info" disable-transitions>
                {{
                  instance.behavior[0].outputs[0].datasetItem.assessmentValue
                }}
              </el-tag>
            </el-form-item>
            <el-form-item label="Initial Resource: ">
              <el-tag type="info" disable-transitions>
                {{ instance.behavior[0].inputs[0].datasetItem.dataName }}
              </el-tag>
            </el-form-item>
            <el-form-item label="Reproduced Resource: ">
              <el-tag type="info" disable-transitions>
                {{ instance.behavior[0].inputs[1].datasetItem.dataName }}
              </el-tag>
            </el-form-item>
            <el-form-item label="Status: ">
              <el-tag
                :type="
                  getStatus(instance.status) == 'initialized' ||
                  getStatus(instance.status) == 'started'
                    ? 'primary'
                    : getStatus(instance.status)
                "
                disable-transitions
              >
                {{ getStatus(instance.status) }}
              </el-tag>
            </el-form-item>
            <el-form-item label="Operation: ">
              <el-button
                size="small"
                @click="getInstances"
                :disabled="
                  Object.hasOwnProperty.call(
                    instance.behavior[0].outputs[0].datasetItem,
                    'assessmentValue'
                  )
                "
                >Refresh</el-button
              >
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
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

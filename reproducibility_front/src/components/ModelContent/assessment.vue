<template>
  <el-container style="width: 100%; height: 100%">
    <div class="main">
      <div
        style="
          position: absolute;
          float: top;
          margin: -75% 0px 0px 91%;
          z-index: 999;
        "
      >
        <el-button
          type="primary"
          size="normal"
          :disabled="!canInvoke"
          @click="initInvoke(invokingType)"
        >
          <el-icon><Setting /></el-icon>Invoke
        </el-button>
      </div>
      <el-row class="table">
        <el-col>
          <div v-for="(state, index) in modelItem.behavior" :key="index">
            <el-row>
              <el-col :span="24">
                <state-description :stateItem="state"></state-description>
              </el-col>
            </el-row>
            <el-table
              border
              :data="filterCurrentAllEventsWithStates(state)"
              :row-class-name="tableRowClassName"
              @expand-change="handleExpandChange"
              @row-click="rowClick"
              style="width: 100%"
              :row-key="(row) => row.eventId"
              :expand-row-keys="expandRows"
              ref="modelContentTable"
            >
              <el-table-column label="Event name" width="250">
                <template v-slot="scope">
                  <span class="event_name" :title="scope.row.name">
                    <span
                      v-show="scope.row.isOptional == false"
                      style="color: red"
                      >*</span
                    >
                    {{ scope.row.name }}
                  </span>
                </template>
              </el-table-column>
              <el-table-column
                prop="description"
                label="Description"
              ></el-table-column>
              <el-table-column
                width="150"
                prop="type"
                label="Type"
              ></el-table-column>
              <el-table-column label="Value" width="260">
                <template v-slot="scope">
                  <div v-if="scope.row.type == 'parameters'">
                    <div v-if="scope.row.name == 'AssessmentMethod'">
                      <el-select
                        v-model="
                          scope.row.datasetItem.UdxDeclarationNew[0]
                            .parameterValue
                        "
                        class="m-2"
                        placeholder="Select"
                      >
                        <el-option
                          v-for="item in indicatorMethods"
                          :key="item.name"
                          :label="item.label"
                          :value="item.name"
                        />
                      </el-select>
                    </div>
                    <div v-else>
                      <el-input
                        v-model="
                          scope.row.datasetItem.UdxDeclarationNew[0]
                            .parameterValue
                        "
                      ></el-input>
                    </div>
                  </div>
                  <div v-if="scope.row.type == 'inputs'">
                    <div v-if="invokingType == 'initial'">
                      <el-button round style="margin-left: 10px">
                        {{ scope.row.datasetItem.dataName }}
                        <el-icon circle><Download /></el-icon
                      ></el-button>
                    </div>
                    <div v-else>
                      <div v-if="scope.row.name == 'InitialResource'">
                        <assessOutputTreeVue
                          :treeEvent="scope.row"
                          :dataFolderList="initialInstanceObjectList"
                        ></assessOutputTreeVue>
                      </div>
                      <div v-else>
                        <assessOutputTreeVue
                          :treeEvent="scope.row"
                          :dataFolderList="reproducedInstanceObjectList"
                        ></assessOutputTreeVue>
                      </div>
                    </div>
                  </div>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-col>
      </el-row>
    </div>

    <el-dialog
      v-model="modelInvokeDialogShow"
      width="500px"
      title="Enter the instance name to invoke the model"
      :close-on-click-modal="false"
      :destroy-on-close="true"
    >
      <el-input
        v-model="modelInstanceName_input"
        placeholder="请输入内容"
      ></el-input>
      <template v-slot:footer>
        <span class="dialog-footer">
          <el-button @click="modelInvokeDialogShow = false">Cancel</el-button>
          <el-button type="primary" @click="startInvoke"
            >Start to invoke</el-button
          >
        </span>
      </template>
    </el-dialog>
  </el-container>
</template>

<script>
import ResourceTable from "@/views/builder/construction/Toolbars/DataTable.vue";
import StateDescription from "_com/Cards/StateDescription.vue";
import SelectTree from "_com/SelectTree/tree.vue";
import assessOutputTreeVue from "./components/assessOutputTree.vue";
import modelInstance from "./components/ModelInstance.vue";
import { mapState } from "vuex";
import { errorNotification, successNotification } from "@/utils/notification";

import {
  initTask,
  getRecordofSingleModel,
  getInstancesInScenario,
  bindScenario,
  getScenarioById,
  getFoldersByTagId,
  getAssessmentInstancesInScenario,
} from "@/api/request";
import {
  getInstanceName,
  downloadData,
  startInvokeModel,
} from "./configuration";

export default {
  components: {
    ResourceTable,
    StateDescription,
    SelectTree,
    assessOutputTreeVue,
    modelInstance,
  },
  props: {
    currentModel: {
      type: Object,
    },
    initialScenarioId: {
      type: String,
    },
    reproducedScenarioId: {
      type: String,
    },
    invokingType: {
      type: String,
    },
    initialInstanceObjectList: {
      type: Array,
    },
    reproducedInstanceObjectList: {
      type: Array,
    },
  },
  watch: {
    currentModel: {
      async handler(newVal) {
        this.modelItem = newVal;
        if (Object.hasOwnProperty.call(newVal, "md5")) {
          this.canInvoke = true;
          await this.initModelTask();
        }
      },
      deep: true,
      immediate: true,
    },
    // reproducedScenarioId: {
    //   async handler(newVal) {
    //     if (newVal != "") {
    //       await this.getAssessmentInstancesInScenario();
    //     }
    //   },
    //   deep: true,
    //   immediate: true,
    // },
  },
  computed: {
    ...mapState({
      userId: (state) => state.user.userId,
    }),
  },
  data() {
    return {
      indicatorMethods: [
        { name: "Eta-squared", label: "Eta-squared", description: "" },
        { name: "Cohen's d", label: "Cohen's d", description: "" },
        { name: "Hedges' g", label: "Hedges' g", description: "" },
        { name: "Cohen's w", label: "Cohen's w", description: "" },
        { name: "R_squared", label: "R_squared", description: "" },
        {
          label: "Pearson correlation coefficient & P-value",
          name: "Pearson",
          description: "",
        },
        { name: "MSE", label: "MSE", description: "" },
        { name: "RMSE", nalabelme: "RMSE", description: "" },
        { name: "MAE", label: "MAE", description: "" },
        { name: "r2", label: "r2", description: "" },
      ],
      currentInstance: {},
      expandRows: [],
      projectId: this.$route.params.id, //projectId
      modelInstanceName_input: "",
      modelInvokeDialogShow: false,

      boundInstances: [],
      boundInstanceObjects: [],
      selectedDataName: "",
      // selectedDataEvent: [],
      modelItem: this.currentEvent,
      modelInstance: {},
      modelInstanceList: [],
      instanceDialogShow: false,
      currentEvent: "",
      timer: {},
      refreshForm: {},
      boundData: [],
      canInvoke: true,
      // modelWithCurrentAllEvents: [],

      invokeForm: {
        ip: "",
        port: "",
        pid: "",
        username: "",
        inputs: [
          {
            statename: "",
            event: "",
            value: "",
            tag: "",
            dataId: "",
          },
        ],
        outputs: [
          {
            statename: "",
            event: "",
            template: {
              type: "", //id|none
              value: "", //if tyoe=none value=""
              dataId: "",
            },
          },
        ],
      },
    };
  },
  methods: {
   

    closeInstanceDialog() {
      this.currentInstance = {};
    },

    rowClick(row) {
      this.currentInstance = row;
    },

    //table related
    handleExpandChange(row, rows) {
      const isExpend = rows.some((r) => r.id === row.id); // 判断当前行展开状态
      if (isExpend) {
        // Do some things
      } else {
        return;
      }
    },

    tableRowClassName({ row, rowIndex }) {
      row.rowIndex = rowIndex;
      if (row.type == "parameters") {
        return "parameter-row";
      }
      if (row.type == "inputs") {
        return "input-row";
      }
      if (row.type == "outputs") {
        return "output-row";
      }
    },
    tableRowClassNameofInstance({ row }) {
      if (row.status == 2) {
        return "run-success";
      } else {
        return "run-loading";
      }
    },

    // 下载按钮
    download(data) {
      downloadData(data);
    },
    // 过滤方法，没有udxNode变量就加一个，暂时不知道udxNode是干啥的
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

      return array;
    },
    async refreshInstance() {
      await this.getInstances();
      this.$forceUpdate();
    },

    async getInstances() {
      this.modelInstanceList = await getAssessmentInstancesInScenario(
        this.reproducedScenarioId
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

    // 运行invoke
    initInvoke(invokingType) {
      this.modelInstanceName_input = getInstanceName(
        invokingType,
        this.modelItem.name
      );
      this.modelInvokeDialogShow = true;
    },
    // 开始invoke模型
    async startInvoke() {
      this.modelInvokeDialogShow = false;

      await startInvokeModel(
        this.modelItem,
        this.invokeForm,
        this.modelInstanceName_input,
        this.invokingType,
        this.initialScenarioId,
        this.reproducedScenarioId
      );
    },

    //在点击model的时候就初始化一个task，获取模型所在位置的ip，端口，md5值（模型的唯一标识符）
    async initModelTask() {
      //get task ip port ...
      let { data } = await initTask(this.modelItem.md5);
      if (data == undefined || data == "") {
        this.canInvoke = false;
        this.$message.error("This model cannot be executed now");
      } else {
        this.canInvoke = true;
        this.invokeForm.ip = data.host;
        this.invokeForm.port = data.port;
        this.invokeForm.pid = this.modelItem.md5; //md5
        this.invokeForm.username = this.userId;
      }
    },
  },

  async mounted() {
    await this.refreshInstance();
  },
};
</script>

<style lang="scss" scoped>
.selectData {
  :deep(.el-dialog) {
    height: 800px;
    overflow: auto;
  }
  :deep(.run-loading .el-table__expand-column .cell) {
    display: none;
  }
}
.main {
  width: 100%;
  height: 100%;
  .table {
    width: 100%;
    :deep(.el-table .parameter-row) {
      // background: oldlace;
      background: rgb(255, 255, 255);
    }

    :deep(.el-table .input-row) {
      background: #f0f9eb;
    }
    :deep(.el-table .output-row) {
      background: #f1f1f1;
    }

    :deep(.input-row .el-table__expand-column .cell) {
      display: none;
    }

    :deep(.output-row .el-table__expand-column .cell) {
      display: none;
    }
  }
}
</style>

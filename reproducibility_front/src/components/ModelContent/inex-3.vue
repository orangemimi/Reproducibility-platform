<template>
  <el-container style="width: 100%; height: 100%">
    <div class="main">
      <div v-show="invokingType == 'construction'">
        <el-row class="title">
          <el-col>{{ modelItem.name }}</el-col>
        </el-row>
      </div>
      <div style="min-height: 35px">
        <el-row
          v-show="
            invokingType == 'reproduction' || invokingType == 'construction'
          "
        >
          <el-col :span="17">
            <p class="description">{{ modelItem.description }}</p>
          </el-col>
          <el-col :span="7">
            <el-button
              type="success"
              size="normal"
              style="float: right"
              @click="getInstances"
            >
              <el-icon><Setting /></el-icon>Instances
            </el-button>
            <el-button
              type="primary"
              size="normal"
              :disabled="!canInvoke"
              @click="initInvoke(invokingType)"
              style="float: right; margin-right: 5px"
            >
              <el-icon><Setting /></el-icon>Invoke
            </el-button>
          </el-col>
        </el-row>
      </div>

      <el-row class="table">
        <!-- {{ modelItem.behavior }} -->
        <el-col>
          <div v-for="(state, index) in modelItem.behavior" :key="index">
            <el-row>
              <state-description :stateItem="state"></state-description>
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
              <el-table-column key="need" type="expand" width="30">
                <template v-slot="props">
                  <el-table
                    border
                    :data="props.row.datasetItem.UdxDeclarationNew"
                  >
                    <el-table-column prop="name" label="Event name" width="250">
                    </el-table-column>
                    <el-table-column
                      width="250"
                      prop="description"
                      label="Description"
                    ></el-table-column>
                    <el-table-column
                      width="150"
                      prop="type"
                      label="Type"
                    ></el-table-column>
                    <el-table-column prop="value" label="Value">
                      <template v-slot="scope">
                        <el-input v-model="scope.row.parameterValue"></el-input>
                      </template>
                    </el-table-column>
                  </el-table>
                </template>
              </el-table-column>
              <el-table-column label="Event name" width="180">
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
                    <el-icon><CaretBottom /></el-icon>
                  </div>
                  <div v-if="scope.row.type == 'inputs'">
                    <div v-if="invokingType == 'initial'">
                      <el-button round style="margin-left: 10px">
                        {{ scope.row.datasetItem.dataName }}
                        <el-icon circle><Download /></el-icon
                      ></el-button>
                    </div>
                    <div v-else>
                      <SelectTree
                        :treeData="dataFolderList"
                        :treeEvent="scope.row"
                        @refreshData="refreshData"
                      ></SelectTree>
                    </div>
                  </div>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-col>
      </el-row>
    </div>
    <div class="selectData">
      <!-- instance dialog -->
      <el-dialog
        v-model="instanceDialogShow"
        width="1200px"
        title="Instances"
        :close-on-click-modal="false"
        @close="closeInstanceDialog"
      >
        <el-table
          :data="modelInstanceList"
          style="width: 100%"
          :row-class-name="tableRowClassNameofInstance"
          @row-click="rowClick"
        >
          <el-table-column label="name" prop="name"> </el-table-column>
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
                <!-- {{ scope.row.status==2? 'Success' }}  Success -->
              </el-tag>
              <!-- <el-tag
                  v-else-if="scope.row.status == -1"
                  type="error"
                  disable-transitions
                  >Failure
                </el-tag>
                <el-tag
                  v-else-if="scope.row.status == 0"
                  type="primary"
                  disable-transitions
                  >initialized
                </el-tag>
                <el-tag
                  v-else-if="scope.row.status == 1"
                  type="primary"
                  disable-transitions
                  >started
                </el-tag>
                <el-tag v-else type="error" disable-transitions
                  >{{ scope.row.status }}
                </el-tag> -->
            </template>
          </el-table-column>
          <el-table-column label="Operation" width="120">
            <template v-slot="props">
              <el-button
                v-show="props.row.status == 2"
                :type="isBound(props.row) ? 'success' : 'primary'"
                round
                @click="toggleBindStatus(props.row)"
              >
                <el-icon><Check /></el-icon>
                {{ isBound(props.row) ? "bound" : "bind" }}
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <div v-if="currentInstance != null">
          <el-col>
            <h1>Model Configuration</h1>
            <br />
            <hr />
            <el-row class="table">
              <div
                v-for="(state, index) in currentInstance.behavior"
                :key="index"
              >
                <el-table
                  :data="filterCurrentAllEventsWithStates(state)"
                  style="width: 100%"
                  :row-class-name="tableRowClassName"
                >
                  <el-table-column width="50" type="expand" key="need">
                    <template #default="scope">
                      <el-table
                        border
                        :data="scope.row.datasetItem.UdxDeclarationNew"
                      >
                        <el-table-column
                          prop="name"
                          label="Event name"
                          width="200"
                        >
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
                        <el-table-column prop="parameterValue" label="Value">
                        </el-table-column>
                      </el-table>
                    </template>
                  </el-table-column>
                  <el-table-column label="Name" prop="name" width="250">
                  </el-table-column>
                  <el-table-column label="Type" prop="type" width="120">
                  </el-table-column>
                  <el-table-column
                    label="Description"
                    prop="description"
                    width="310"
                  >
                  </el-table-column>

                  <el-table-column
                    label="Data Download or Parameters Value"
                    width="430"
                  >
                    <template #default="props">
                      <div v-if="props.row.type == 'parameters'"></div>

                      <el-button
                        v-else
                        type="info"
                        round
                        @click="download(props.row, props.row)"
                      >
                        <el-icon><Download /></el-icon>Download</el-button
                      >
                    </template>
                  </el-table-column>
                </el-table>
              </div>
            </el-row>
          </el-col>
        </div>
      </el-dialog>
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
import { errorNotification, successNotification } from "@/utils/notification";
import {
  saveData,
  invokeSingleModel,
  saveInstance,
  initTask,
  updateInstance,
  getInstanceById,
  getRecordofSingleModel,
  getInstancesInScenario,
  bindScenario,
  getScenarioById,
  getFolders,
  getFoldersByTagId,
} from "@/api/request";
import { renderSize } from "@/utils/utils";
import { mapState } from "vuex";
import SelectTree from "_com/SelectTree/tree.vue";
import { dateFormat } from "@/lib/utils";

export default {
  components: {
    ResourceTable,
    StateDescription,
    SelectTree,
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
  },
  computed: {
    ...mapState({
      userId: (state) => state.user.userId,
    }),
  },
  data() {
    return {
      currentInstance: {},
      expandRows: [],
      projectId: this.$route.params.id, //projectId
      modelInstanceName_input: "",
      modelInvokeDialogShow: false,
      scenario: {},
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
      dataFolderList: [],
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
    closeInstanceDialog() {
      this.currentInstance = {};
    },

    refreshData(val) {
      if (val) {
        this.getFolders();
      }
    },
    rowClick(row) {
      this.currentInstance = row;
    },
    async getFolders() {
      let folderData = [];
      let dataFolderList = [];
      //将绑定所有实例的输出数据也添加到dataFolderList当中

      let OutputFolderList = {};
      let intermediateInitial = [];
      let intermediateReproduced = [];
      let dataInitial = [];
      let dataReproduce = [];

      intermediateInitial = await this.getIntermediateData(
        this.initialScenarioId,
        "construction"
      );
      if (this.invokingType == "construction") {
        folderData = await getFolders();
        dataFolderList = folderData[0].children.filter(
          (item) => item.tagId == this.projectId
        );
        dataInitial = dataFolderList[0].children.filter(
          (item) => item.tagId == this.initialScenarioId
        );
        this.dataFolderList = [...dataInitial, ...intermediateInitial];
      }
      if (this.invokingType == "reproduction") {
        intermediateReproduced = await this.getIntermediateData(
          this.reproducedScenarioId,
          "reproduction"
        );

        dataInitial = await getFoldersByTagId(this.initialScenarioId);
        dataReproduce = await getFoldersByTagId(this.reproducedScenarioId);
        this.dataFolderList = [
          ...dataInitial,
          ...intermediateInitial,
          ...dataReproduce,
          ...intermediateReproduced,
        ];
      }
    },

    async getIntermediateData(scenarioId, type) {
      let boundInstanceOutput = [];
      let intermediate = [];
      await this.getScenario(scenarioId);
      // debugger;
      console.log(this.scenario.instanceObjectLists);
      this.scenario.instanceObjectList.filter((obj) => {
        obj.behavior.filter((state) => {
          if (boundInstanceOutput.length == 0) {
            boundInstanceOutput = [...state.outputs];
          } else {
            boundInstanceOutput.push(...state.outputs);
          }
        });
      });
      console.log(intermediate, "intermediate");
      debugger;
      let outputFolderList = {
        name:
          "Output of bound instances of " +
          (type == "construction" ? "initial" : "reproduced") +
          " scenario",
        children: boundInstanceOutput,
      };
      intermediate.push(outputFolderList);

      return intermediate;
    },

    async selectDataDialog(event) {
      await this.getFolders();
      this.currentEvent = event;
    },

    getBoundData() {
      let dataArray = [];
      this.boundInstances.forEach((instance) =>
        instance.behavior.forEach((state) =>
          state.outputs.forEach((out) => {
            out.modelName = instance.modelName;
            out.instanceName = instance.name;
            dataArray.push(out);
          })
        )
      );
      this.boundData = dataArray;
    },

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
    isBound(row) {
      if (this.boundInstances != "undefined" && this.boundInstances != null) {
        return this.boundInstances.includes(row.id);
      }
      return false;
    },
    async toggleBindStatus(row) {
      // 切换按钮的状态
      if (this.isBound(row)) {
        // 执行解绑逻辑
        this.unBindInstanceId(row);
        successNotification("unbind", "instance");
      } else {
        // 执行绑定逻辑
        this.bindInstanceId(row);
        successNotification("bind", "instance");
      }
    },
    async bindInstanceId(row) {
      this.boundInstances.push(row.id);
      await this.refreshBoundInstancesInScenario();
    },

    async unBindInstanceId(row) {
      const index = this.boundInstances.indexOf(row.id);
      if (index !== -1) {
        this.boundInstances.splice(index, 1);
      }
      await this.refreshBoundInstancesInScenario();
      this.$forceUpdate();
    },

    async refreshBoundInstancesInScenario() {
      let resourceCollection = {
        dataList: "",
        modelList: this.scenario.resourceCollection.modelList,
      };
      this.modelInstanceList.forEach((instance) => {
        if (this.boundInstances.some((id) => id == instance.id)) {
          instance.behavior.forEach((state) => {
            state.inputs.forEach((input) => {
              //initial input

              if (resourceCollection.dataList == "") {
                resourceCollection.dataList = [input.dataId];
              } else {
                resourceCollection.dataList.push(input.dataId);
              }
            });
          });
        }
      });

      let update = {
        instances: this.boundInstances,
        resourceCollection: resourceCollection,
      };
      await bindScenario(this.judgeIsReproducedScenarioId(), update);
      //update the resourceCollection
    },

    judgeIsReproducedScenarioId() {
      return this.invokingType == "construction"
        ? this.initialScenarioId
        : this.reproducedScenarioId;
    },

    // 下载按钮
    download(data) {
      if (data.value) {
        let urls = data.value;
        // 创建一个链接元素
        const link = document.createElement("a");
        link.href = urls;
        link.target = "_blank"; // 在新窗口中打开链接
        link.download = data.datasetItem.dataName; // 设置下载的文件名
        // 模拟点击链接，触发下载
        link.click();
      } else {
        errorNotification("Data lost");
      }
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

    async getInstances() {
      //refresh instances to get binded insstances
      await this.getScenario(this.judgeIsReproducedScenarioId());
      //get all invoked instances
      this.modelInstanceList = await getInstancesInScenario(
        this.judgeIsReproducedScenarioId(),
        this.modelItem.id,
        this.invokingType == "reproduction" ? true : false
      );
      //refresh the instances
      this.modelInstanceList.forEach(async (instance) => {
        if (instance.status == 0 || instance.status == 1) {
          let { data } = await getRecordofSingleModel(instance);
          instance = data;
        }
      });
      this.instanceDialogShow = true;
    },

    async getScenario(scenarioId) {
      this.scenario = await getScenarioById(scenarioId);
      if (Object.prototype.hasOwnProperty.call(this.scenario, "instances")) {
        this.boundInstances = this.scenario.instances;
        // this.boundInstanceObjects = this.scenario.instanceObjectList;
      }
      console.log(
        this.scenario.instanceObjectList,
        " this.boundInstanceObjects"
      );
    },

    // 运行invoke
    initInvoke(invokingType) {
      if (invokingType == "construction") {
        this.modelInstanceName_input =
          this.modelItem.name + "  ||  " + dateFormat(new Date());
      } else {
        this.modelInstanceName_input =
          "Reproduction ||  " +
          this.modelItem.name +
          "  ||  " +
          dateFormat(new Date());
      }

      this.modelInvokeDialogShow = true;
    },
    // 开始invoke模型
    async startInvoke() {
      this.modelInvokeDialogShow = false;
      try {
        await this.createFilefromParam();
        await this.createInvokeForm();
        console.log(this.invokeForm, "invokeForm");
        let data = await invokeSingleModel(this.invokeForm);
        let refreshForm = {
          ip: this.invokeForm.ip,
          port: this.invokeForm.port,
        };

        if (data == null) {
          errorNotification("You have run the model failed");
          await this.emitInstance(-1, this.modelItem, refreshForm);
        } else {
          refreshForm.tid = data.data.tid;
          console.log("4");
          await this.emitInstance(0, this.modelItem, refreshForm);
        }
        successNotification("start", "calculation");
      } catch {
        errorNotification("You have run the model failed");
      }
    },
    // 创建运行时的instance
    async emitInstance(status, modelItem, refreshForm) {
      //在运行时 instance的创建
      if (status == 0 || status == -1) {
        let instanceTemp = {
          name: this.modelInstanceName_input,
          modelName: modelItem.name,
          behavior: modelItem.behavior,
          status: status,
          scenarioId: this.judgeIsReproducedScenarioId(),
          modelId: modelItem.id,
          refreshForm: refreshForm,
          isReproduced: this.invokingType == "reproduction" ? true : false,
        };

        await saveInstance(instanceTemp);
      } else {
        await this.updateInstance(status, modelItem.behavior, modelItem.id);
      }
    },

    async updateInstance(status, behavior, instanceId) {
      await updateInstance(instanceId, {
        behavior: behavior,
        status: status,
      });
    },

    // 创建一个invokeForm，清洗参数，将各数据放到invokeForm中
    createInvokeForm() {
      let stateList = this.modelItem.behavior;
      this.invokeForm.inputs = [];
      this.invokeForm.outputs = [];

      // debugger;
      for (let i = 0; i < stateList.length; i++) {
        let state = stateList[i];
        let allInputsWithPara = state.inputs.concat(state.parameters);
        allInputsWithPara.forEach((item) => {
          if (
            Object.hasOwnProperty.call(item, "value") &&
            item.value != "" &&
            item.value != null
          ) {
            let detail = {
              statename: state.name,
              event: item.name,
              tag: item.name,
              url: item.value,
            };

            this.invokeForm.inputs.push(detail);
          }
        });
        state.outputs.forEach((item) => {
          let template = {};

          let outputTemplate = item.datasetItem;
          if (outputTemplate.type === "external") {
            template = {
              type: "id",
              value: outputTemplate.externalId,
            };
          } else {
            template = {
              type: "none",
              value: "",
            };
          }
          let detail = {
            statename: state.name,
            event: item.name,
            template: template,
          };

          this.invokeForm.outputs.push(detail);
        });
      }

      console.log(this.invokeForm, "this.invokeForm");
    },
    // 点击state使其收缩的事件，感觉没啥用啊
    handleChange() {},

    // 将参数绑定为一个xml文件，上传，返回url绑定到mdl
    async createFilefromParam() {
      let stateList = this.modelItem.behavior;
      for (let i = 0; i < stateList.length; i++) {
        let events = stateList[i].parameters;
        if (events.length == 0) {
          return;
        }

        for (let j = 0; j < events.length; j++) {
          console.log("1.1", events);
          let content = "";
          // let uploadFileForm = new FormData();

          //这一块应该是重新梳理过的模型结构，是新改的，但是跟以前的不适配，暂时注释掉
          let udxNodeList = events[j].datasetItem.UdxDeclarationNew;
          console.log(udxNodeList);
          for (let k = 0; k < udxNodeList.length; k++) {
            if (Object.hasOwnProperty.call(udxNodeList[k], "parameterValue")) {
              content += `<XDO name="${events[j].name}" kernelType="string" value="${udxNodeList[k].parameterValue}" />`;
            }
          }
          if (content != "") {
            content = "<Dataset> " + content + " </Dataset>";
            let file = new File([content], events[j].name + ".xml", {
              type: "text/plain",
            });
            await this.submitUpload(i, j, file);
          }
        }
      }
    },
    // 上传文件，并保留url
    async submitUpload(stateIndex, eventIndex, fileItem) {
      let uploadFileForm = new FormData();
      uploadFileForm.append("file", fileItem);
      let data = await saveData(
        uploadFileForm,
        renderSize(fileItem.size),
        "intermediate"
      );
      this.modelItem.behavior[stateIndex].parameters[eventIndex]["value"] =
        data;
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
  beforeUnmount() {
    clearInterval(this.timer);
  },
  async mounted() {
    // this.getFolders();
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
      background: oldlace;
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

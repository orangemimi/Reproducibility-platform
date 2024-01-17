<template>
  <el-container style="width: 100%; height: 100%">
    <div v-if="codingOlShow" class="main">
      <coding-OL :scenarioId="scenarioId"></coding-OL>
    </div>
    <div v-else-if="modelItem" class="main">
      <el-row class="title">
        <el-col>{{ modelItem.name }}</el-col>
      </el-row>

      <el-row>
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
            @click="initInvoke"
            style="float: right; margin-right: 5px"
          >
            <el-icon><Setting /></el-icon>Invoke
          </el-button>
        </el-col>
      </el-row>
      <el-row class="table">
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
              style="width: 100%"
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
                    <SelectTree
                      :treeData="dataFolderList"
                      :treeEvent="scope.row"
                      @refreshData="refreshData"
                    ></SelectTree>
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
        width="900px"
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
                v-if="scope.row.status == '2'"
                type="success"
                disable-transitions
              >
                Success
              </el-tag>
              <el-tag
                v-else-if="scope.row.status == '-1'"
                type="error"
                disable-transitions
                >Failure
              </el-tag>
              <el-tag
                v-else-if="scope.row.status == '0'"
                type="primary"
                disable-transitions
                >initialized
              </el-tag>
              <el-tag
                v-else-if="scope.row.status == '1'"
                type="primary"
                disable-transitions
                >started
              </el-tag>
              <el-tag v-else type="error" disable-transitions
                >{{ scope.row.status }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="Operation" width="120">
            <template v-slot="props">
              <el-button
                v-show="props.row.status == '2'"
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
        <div v-show="currentInstance != null">
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
                        <el-table-column
                          width="150"
                          prop="parameterValue"
                          label="Value"
                        >
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
                    width="150"
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

    <div class="selectData">
      <el-dialog
        v-model="selectDataDialogShow"
        width="900px"
        title="Select data from resource center or upload"
        :close-on-click-modal="false"
        :destroy-on-close="true"
      >
        <resource-table
          @submitDataToEvent="submitDataToEvent"
          :boundData="boundData"
        ></resource-table>
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

import {
  saveData,
  invokeSingleModel,
  initTask,
  saveInstance,
  updateInstance,
  getInstanceById,
  getRecordofSingleModel,
  getInstancesInScenario,
  bindScenario,
  getScenarioById,
  getFolders,
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
    scenarioId: {
      type: String,
    },
    codingOlShow: {
      type: Boolean,
    },
  },
  watch: {
    currentModel: {
      async handler(newVal) {
        if (Object.hasOwnProperty.call(newVal, "md5")) {
          this.modelItem = newVal;
          this.canInvoke = true;
          await this.initTask();
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
    //把获取数据中的name字段分开
  },
  data() {
    return {
      currentInstance: {},
      parameterExpand: [],
      codingShow: false,
      projectId: this.$route.params.id, //projectId
      modelInstanceName_input: "",
      modelInvokeDialogShow: false,
      scenario: {},
      boundInstances: [],
      selectedDataName: "",
      // selectedDataEvent: [],
      modelItem: this.currentEvent,
      modelInstance: {},
      modelInstanceList: [],
      allModelInstanceListInScenario: [],
      instanceDialogShow: false,
      activeNames: 0,
      selectDataDialogShow: false,
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
          },
        ],
        outputs: [
          {
            statename: "",
            event: "",
            template: {
              type: "", //id|none
              value: "", //if tyoe=none value=""
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
    refreshData(val) {
      if (val) {
        this.getFolders();
      }
      // console.log("refresh");
    },
    rowClick(row) {
      console.log("row", row);
      this.currentInstance = row;
    },
    async getFolders() {
      let data = await getFolders();
      let dataFolderList = data[0].children.filter(
        (item) => item.tagId == this.projectId
      );
      this.dataFolderList = dataFolderList[0].children.filter(
        (item) => item.tagId == this.scenarioId
      );
      //将绑定所有实例的输出数据也添加到dataFolderList当中
      var boundInstanceOutput = [];
      var OutputFolderList = {};
      if (this.scenarioId) {
        this.scenario = await getScenarioById(this.scenarioId);
        this.boundInstances = this.scenario.instances;
        for (var boundInstance of this.boundInstances) {
          var instance = await getInstanceById(boundInstance);
          boundInstanceOutput.push(...instance.behavior[0].outputs);
        }
        OutputFolderList.name = "Output of bound instances";
        OutputFolderList.children = boundInstanceOutput;
        this.dataFolderList.push(OutputFolderList);
      }

      // todo --只在project内展示所有project的
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
      console.log(this.boundInstances, row, "row");
      if (
        this.boundInstances != "undefined" &&
        this.boundInstances != null &&
        this.boundInstances.length != 0
      ) {
        console.log(this.boundInstances.includes(row.id), "includes");
        return this.boundInstances.includes(row.id);
      }
      return false;
    },
    async toggleBindStatus(row) {
      // 切换按钮的状态
      if (this.isBound(row)) {
        // 执行解绑逻辑
        this.unBindInstanceId(row);
        this.$message({
          showClose: true,
          message: "Unbind successfully",
          type: "info",
        });
      } else {
        // 执行绑定逻辑
        // this.bindInstanceId(row);
        //   console.log("after", this.boundInstances);
        // this.boundInstances = [...boundInstances, row.id];
        // console.log("after", this.boundInstances);
        console.log(this.boundInstances, "initial");
        this.boundInstances.push(row.id);
        await this.refreshBoundInstancesInScenario();
        this.$message({
          showClose: true,
          message: "Binding successful",
          type: "success",
        });
      }
    },
    // async bindInstanceId(row) {

    // },

    async unBindInstanceId(row) {
      const index = this.boundInstances.indexOf(row.id);
      if (index !== -1) {
        this.boundInstances.splice(index, 1);
      }
      await this.refreshBoundInstancesInScenario();
    },

    async refreshBoundInstancesInScenario() {
      let update = { instances: this.boundInstances };
      await bindScenario(this.scenarioId, update);
    },
    // tableRowClassName({ row, rowIndex }) {
    //   row.rowIndex = rowIndex;
    // },
    // 下载按钮
    download(data) {
      if (data.value) {
        let urls = data.value;
        // 创建一个链接元素
        const link = document.createElement("a");
        link.href = urls;
        link.target = "_blank"; // 在新窗口中打开链接
        link.download = data.name; // 设置下载的文件名
        // 模拟点击链接，触发下载
        link.click();
      } else {
        this.$message({
          showClose: true,
          message: "Data lost",
          type: "warning",
        });
      }
    },
    // 过滤方法，没有udxNode变量就加一个，暂时不知道udxNode是干啥的
    filterCurrentAllEventsWithStates(state) {
      let array = [];
      state.inputs.forEach((item) => {
        item.stateName = state.name;
        item.type = "inputs";
        array.push(item);
      });
      state.parameters.forEach((item) => {
        item.stateName = state.name;
        item.type = "parameters";
        array.push(item);
      });
      state.outputs.forEach((item) => {
        item.stateName = state.name;
        item.type = "outputs";
        array.push(item);
      });

      return array;
    },

    // 选择数据后清除数据
    clearData(modelInEvent) {
      console.log(modelInEvent);
      modelInEvent.value = "";
      modelInEvent.valueName = "";
      this.$forceUpdate();
    },
    // 控制选择数据dialogue

    // 选择数据提交按钮的方法，数据只是缓存在浏览器中 1、提取并存储文件名 2、将数据的id和url存储起来
    submitDataToEvent(val) {
      this.currentEvent.value = val.value;
      this.currentEvent.valueName = val.name + val.suffix;

      this.selectDataDialogShow = false;
    },

    async getInstances() {
      await this.getScenario(this.scenarioId);

      this.modelInstanceList = await getInstancesInScenario(
        this.scenarioId,
        this.modelItem.id
      );
      await this.getInstanceStatus();
      this.instanceDialogShow = true;
    },

    async getScenario(scenarioId) {
      this.scenario = await getScenarioById(scenarioId);
      if (Object.prototype.hasOwnProperty.call(this.scenario, "instances")) {
        this.boundInstances = this.scenario.instances;
      }
    },
    getInstanceStatus() {
      this.modelInstanceList.forEach(async (instance) => {
        if (instance.status == 0) {
          await this.getOutputs(instance, instance.refreshForm);
          //表示正在运行
        }
      });
    },

    // 运行invoke
    initInvoke() {
      this.modelInstanceName_input =
        this.modelItem.name + "  ||  " + dateFormat(new Date());
      this.modelInvokeDialogShow = true;
    },
    // 开始invoke模型
    async startInvoke() {
      this.modelInvokeDialogShow = false;

      try {
        await this.createFilefromParam();
        await this.createInvokeForm();

        let data = await invokeSingleModel(this.invokeForm);
        console.log(data, "3");
        let refreshForm = {
          ip: this.invokeForm.ip,
          port: this.invokeForm.port,
        };
        if (data == null) {
          this.$message({
            message: "You have run the model failed",
            type: "error",
          });
          await this.emitInstance(-1, this.modelItem, refreshForm);
        } else {
          refreshForm.tid = data.data.tid;
          console.log("4");
          await this.emitInstance(0, this.modelItem, refreshForm);
        }
        this.$message({
          message: "Start calculation",
          type: "success",
        });
      } catch {
        this.$message({
          type: "error",
          message: "invoke failed",
        });
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
          scenarioId: this.scenarioId,
          modelId: modelItem.id,
          refreshForm: refreshForm,
          isReproduced: false,
        };
        console.log(instanceTemp, "instanceTemp");
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

    async getOutputs(instance, refreshForm) {
      if (instance.status != 2) {
        let { data } = await getRecordofSingleModel(refreshForm);
        if (data.status != instance.status) {
          instance.status = data.status;
          if (data.status == 2) {
            let isURLExist = true;
            data.outputs.forEach((out) => {
              if (out.url == "") {
                isURLExist = false;
              }
            });
            if (isURLExist) {
              let instanceTemp = this.getStateEventOut(instance, data);
              await this.updateInstance(
                instance.status,
                instanceTemp.behavior,
                instance.id
              );
            } else {
              await this.updateInstance(-1, instance.behavior, instance.id);
            }

            // let data3 = await updateInstance(this.scenarioId, stepResource);
          }
        }
      }
    },

    getStateEventOut(instance, record) {
      let stateList = instance.behavior;
      let outputUrl = record.outputs;
      outputUrl.forEach((el) => {
        stateList.forEach((state, index) => {
          if (state.name == el.statename) {
            state.outputs.forEach((event, eventIndex) => {
              if (el.event == event.name) {
                instance.behavior[index].outputs[eventIndex]["value"] = el.url;
              }
            });
          }
        });
      });

      return instance;
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
            // uploadFileForm.append("file", file);

            // this.createConfigFile();

            await this.submitUpload(i, j, file);
          }
        }
      }
    },
    // 上传文件，并保留url
    async submitUpload(stateIndex, eventIndex, fileItem) {
      // let param = fileItem.file;
      let uploadFileForm = new FormData();
      uploadFileForm.append("file", fileItem);
      let data = await saveData(
        uploadFileForm,
        renderSize(fileItem.size),
        "intermediate"
      );
      this.modelItem.behavior[stateIndex].parameters[eventIndex]["value"] =
        data;
      console.log("data", data);
    },
    //在点击model的时候就初始化一个task，获取模型所在位置的ip，端口，md5值（模型的唯一标识符）
    async initTask() {
      //get task ip port ...
      console.log(this.modelItem.md5, "md5");
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
    this.getFolders();
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
    // :deep(.el-table .input-row .cell .el-table__expand-icon) {
    //   display: none;
    // }

    :deep(.input-row .el-table__expand-column .cell) {
      display: none;
    }

    :deep(.output-row .el-table__expand-column .cell) {
      display: none;
    }
  }
}
</style>

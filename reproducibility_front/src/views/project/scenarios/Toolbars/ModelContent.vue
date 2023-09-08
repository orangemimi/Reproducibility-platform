<!--  -->
<template>
  <div>
    <div class="main">
      <el-row class="title">
        <el-col>{{ modelItem.name }}</el-col>
      </el-row>

      <el-row>
        <el-col :span="17">
          <p class="description">{{ modelItem.description }}</p>
        </el-col>
        <el-col :span="7">
          <el-button type="success" size="mini" style="float:right">
            <i class="el-icon-setting"></i>&nbsp;Instances
          </el-button>
          <el-button
            type="primary"
            size="mini"
            @click="invokeTest"
            style="float:right;margin-right:5px"
          >
            <i class="el-icon-setting"></i>&nbsp;Invoke
          </el-button>
        </el-col>
      </el-row>

      <el-collapse
        v-model="activeNames"
        @change="handleChange"
        v-for="(state, index) in modelItem.behavior"
        :key="index"
      >
        <el-collapse-item :title="state.name" :name="index">
          <!-- <el-divider></el-divider> -->
          <div>
            <div class="_params-group">
              <el-row v-if="state.inputs.length !== 0" class="stateTitle"
                >Inputs</el-row
              >
              <el-divider class="stateTitleDivider"></el-divider>
              <div class="events">
                <el-row
                  v-for="(modelInEvent, inEventIndex) in state.inputs"
                  :key="inEventIndex"
                  class="event"
                >
                  <el-row>
                    <el-col :span="17" class="_event-desc">
                      <span class="event_name" :title="modelInEvent.name">
                        <span
                          v-show="modelInEvent.isOptional == false"
                          style="color: red"
                          >*</span
                        >
                        {{ modelInEvent.name }}
                      </span>
                      <p class="event_desc" :title="modelInEvent.description">
                        {{ modelInEvent.description }}
                      </p>
                    </el-col>

                    <el-row>
                      <div
                        v-if="
                          modelInEvent.hasOwnProperty('url') &&
                            modelInEvent.url != '' &&
                            modelInEvent.urlName != ''
                        "
                      >
                        <div class="select-data select-data-line">
                          <div class="data-name">
                            {{ modelInEvent.urlName }}
                          </div>
                          <el-button
                            type="success"
                            icon="el-icon-download"
                            size="mini"
                            circle
                            @click="download(modelInEvent)"
                          ></el-button>
                          <el-button
                            type="warning"
                            icon="el-icon-close"
                            size="mini"
                            circle
                            @click="remove(modelInEvent)"
                          ></el-button>
                        </div>
                      </div>
                      <div v-else>
                        <el-button
                          type="primary"
                          plain
                          @click="selectDataDialog(modelInEvent)"
                        >
                          Select data
                        </el-button>
                      </div>
                    </el-row>
                  </el-row>
                  <el-row>
                    <el-divider class="eventDivider"></el-divider>
                  </el-row>
                </el-row>
              </div>
            </div>

            <div class="_params-group">
              <el-row v-if="state.parameters.length !== 0" class="stateTitle"
                >Parameters</el-row
              >
              <el-divider class="stateTitleDivider"></el-divider>
              <div class="events">
                <el-row
                  v-for="(modelInEvent, inEventIndex) in state.parameters"
                  :key="inEventIndex"
                  class="event"
                >
                  <el-row>
                    <el-col :span="17" class="_event-desc">
                      <span class="event_name" :title="modelInEvent.name">
                        <span
                          v-show="modelInEvent.isOptional == false"
                          style="color: red"
                          >*</span
                        >
                        {{ modelInEvent.name }}
                      </span>
                      <p class="event_desc" :title="modelInEvent.description">
                        {{ modelInEvent.description }}
                      </p>
                    </el-col>

                    <el-row
                      v-if="
                        Object.prototype.hasOwnProperty.call(
                          modelInEvent,
                          'datasetItem'
                        ) && modelInEvent.datasetItem.type == `internal`
                      "
                    >
                      <div v-if="filterUdxNode(modelInEvent)">
                        <el-table
                          border
                          :data="filterUdxNode(modelInEvent)[0].UdxNode"
                        >
                          <el-table-column
                            prop="name"
                            label="Parameter"
                            width="180"
                          ></el-table-column>
                          <el-table-column
                            prop="description"
                            label="Description"
                            width="180"
                          ></el-table-column>
                          <el-table-column
                            prop="type"
                            label="Type"
                          ></el-table-column>
                          <el-table-column label="Value">
                            <template slot-scope="scope">
                              <el-input v-model="scope.row.value"></el-input>
                            </template>
                          </el-table-column>
                        </el-table>
                      </div>
                      <div v-else>
                        <div
                          v-if="
                            modelInEvent.hasOwnProperty('url') &&
                              modelInEvent.url != '' &&
                              modelInEvent.urlName != ''
                          "
                        >
                          <div class="select-data select-data-line">
                            <div class="data-name">
                              {{ modelInEvent.urlName }}
                            </div>
                            <el-button
                              type="success"
                              icon="el-icon-download"
                              size="mini"
                              circle
                              @click="download(modelInEvent)"
                            ></el-button>
                            <el-button
                              type="warning"
                              icon="el-icon-close"
                              size="mini"
                              circle
                              @click="remove(modelInEvent)"
                            ></el-button>
                          </div>
                        </div>
                        <div v-else>
                          <el-button
                            type="primary"
                            plain
                            @click="selectDataDialog(modelInEvent)"
                          >
                            Select data
                          </el-button>
                        </div>
                      </div>
                    </el-row>
                    <el-row v-else>
                      <div
                        v-if="
                          modelInEvent.hasOwnProperty('url') &&
                            modelInEvent.url != '' &&
                            modelInEvent.urlName != ''
                        "
                      >
                        <div class="select-data select-data-line">
                          <div class="data-name">
                            {{ modelInEvent.urlName }}
                          </div>
                          <el-button
                            type="success"
                            icon="el-icon-download"
                            size="mini"
                            circle
                            @click="download(modelInEvent)"
                          ></el-button>
                          <el-button
                            type="warning"
                            icon="el-icon-close"
                            size="mini"
                            circle
                            @click="remove(modelInEvent)"
                          ></el-button>
                        </div>
                      </div>
                      <div v-else>
                        <el-button
                          type="primary"
                          plain
                          @click="selectDataDialog(modelInEvent)"
                        >
                          Select data
                        </el-button>
                      </div>
                    </el-row>
                  </el-row>
                  <el-row>
                    <el-divider class="eventDivider"></el-divider>
                  </el-row>
                </el-row>
              </div>
            </div>

            <div class="_params-group">
              <el-row v-if="state.outputs.length !== 0" class="stateTitle"
                >Output</el-row
              >
              <el-divider class="stateTitleDivider"></el-divider>
              <div class="events">
                <el-row
                  v-for="(modelOutEvent, outEventIndex) in state.outputs"
                  :key="outEventIndex"
                  class="event"
                >
                  <el-row>
                    <el-col :span="17" class="_event-desc">
                      <span class="event_name" :title="modelOutEvent.name">{{
                        modelOutEvent.name
                      }}</span>
                      <p class="event_desc" :title="modelOutEvent.description">
                        {{ modelOutEvent.description }}
                      </p>
                    </el-col>
                    <el-col :span="6" :offset="1">
                      <div class="_btn-group">
                        <el-button
                          plain
                          round
                          type="warning"
                          @click="download(modelOutEvent)"
                          v-if="
                            modelOutEvent.hasOwnProperty('url') &&
                              modelOutEvent.url != ''
                          "
                          >Download</el-button
                        >
                        <el-button
                          plain
                          round
                          type="warning"
                          @click="bind(modelOutEvent)"
                          :class="{ bindClass: modelOutEvent.bind }"
                          v-if="
                            modelOutEvent.hasOwnProperty('url') &&
                              modelOutEvent.url != ''
                          "
                          >Bind</el-button
                        >
                      </div>
                    </el-col>
                  </el-row>
                  <el-row>
                    <el-divider class="eventDivider"></el-divider>
                  </el-row>
                </el-row>
              </div>
            </div>
          </div>
        </el-collapse-item>
      </el-collapse>
    </div>
    <div class="selectData">
      <el-dialog
        :visible.sync="selectDataDialogShow"
        width="1000px"
        title="Select data from resource center or upload"
        :close-on-click-modal="false"
        :destroy-on-close="true"
      >
        <resource-table @submitDataToEvent="submitDataToEvent"></resource-table>
      </el-dialog>
    </div>
  </div>
</template>

<script>
import ResourceTable from "./ResourceTable.vue";
import {
  saveData,
  invokeSingleModel,
  initTask,
  saveInstance,
  updateInstance,
  getRecordofSingleModel,
  //   getInstancesInScenario,
  //   getInstanceById,
} from "@/api/request";
import { renderSize } from "@/utils/utils";
import { mapState } from "vuex";
export default {
  props: {
    currentModel: {
      type: Object,
    },
    scenarioId: {
      type: String,
    },
  },
  components: { ResourceTable },

  watch: {
    currentModel: {
      async handler(newVal) {
        if (Object.hasOwnProperty.call(newVal, "md5")) {
          this.modelItem = newVal;
          console.log(this.modelItem);
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
  },

  data() {
    return {
      modelItem: this.currentEvent,
      modelInstance: {},
      activeNames: 0,
      selectDataDialogShow: false,
      currentEvent: "",
      timer: {},
      record: {},
      refreshForm: {},
      invokeForm: {
        ip: "",
        port: "",
        pid: "",
        username: "",
        inputs: [
          {
            statename: "",
            event: "",
            url: "",
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
    filterUdxNode(event) {
      if (
        Object.prototype.hasOwnProperty.call(
          event.datasetItem,
          "UdxDeclaration"
        )
      ) {
        if (event.datasetItem.UdxDeclaration[0].UdxNode != "") {
          if (
            Object.prototype.hasOwnProperty.call(
              event.datasetItem.UdxDeclaration[0].UdxNode[0].UdxNode[0],
              "UdxNode"
            )
          ) {
            return false;
          } else {
            let udxNode = event.datasetItem.UdxDeclaration[0].UdxNode;
            return udxNode;
          }
        }
      }
    },
    selectDataDialog(event) {
      this.currentEvent = event;
      this.selectDataDialogShow = true;
    },
    // selectData(val) {
    //   let stateIndex = this.stateList.findIndex(
    //     (state) => state.name == this.currentEvent.stateName
    //   );

    //   let eventIndex = this.stateList[stateIndex].Event.findIndex(
    //     (event) => event.name == this.currentEvent.name
    //   );
    //   this.$set(
    //     this.stateList[stateIndex].Event[eventIndex],
    //     "url",
    //     val.pathURL
    //   );
    //   this.$set(
    //     this.stateList[stateIndex].Event[eventIndex],
    //     "urlName",
    //     val.name
    //   );

    //   this.selectDataDialogShow = false;
    // },

    submitDataToEvent(val) {
      this.currentEvent.value = val.value;
      this.currentEvent.dataId = val.id;
      this.selectDataDialogShow = false;
    },
    async invokeTest() {
      await this.createFilefromParam();
      this.createInvokeForm();
      //测试数据没有弄 直接运行 根据ip+id
      //invoke
      let data = await invokeSingleModel(this.invokeForm);
      debugger;

      this.refreshForm = {
        ip: this.invokeForm.ip,
        port: this.invokeForm.port,
        tid: data.data.tid,
      };
      let refreshForm = this.refreshForm;

      if (data == null) {
        this.$message({
          message: "You have run the model failed",
          type: "error",
        });
        let record = {
          status: -1, //0 calculating
          ...refreshForm,
        };
        await this.emitInstance(record);
        // this.status = false;
      } else {
        debugger;
        let record = {
          status: 0, //0 calculating
          ...refreshForm,
        };
        await this.emitInstance(record);
        this.getOutputs();
      }
    },
    async emitInstance(record) {
      //在运行时 instance的创建
      if (record.status == 0) {
        let instanceTemp = {
          model: this.modelItem,
          status: record.status,
          scenarioId: this.scenarioId,
        };

        let data = await saveInstance(instanceTemp);
        console.log(data);
        debugger;
        this.modelInstance = data;
      } else {
        let stepResource = {
          states: this.modelItem,
          status: record.status, //2 finish
        };
        let data = await updateInstance(this.scenarioId, stepResource);
        this.modelInstance = data;
      }
    },

    async getOutputs() {
      //获得结果
      this.record = {};
      this.timer = setInterval(async () => {
        if (this.record.status == 2) {
          clearInterval(this.timer);
          await this.getStateEventOut(this.record);
          return;
        } else {
          let { data } = await getRecordofSingleModel(this.refreshForm);
          this.record = data;
          console.log(data);
        }
      }, 2000);
    },

    async getStateEventOut(record) {
      let stateList = this.model.behavior;
      let outputUrl = record.outputs;
      outputUrl.forEach((el) => {
        stateList.forEach((state, index) => {
          if (state.name == el.statename) {
            state.outputs.forEach((event, eventIndex) => {
              if (el.event == event.name) {
                this.$set(
                  this.modelItem.behavior[index].outputs[eventIndex],
                  "value",
                  el.url
                );
              }
            });
          }
        });
      });
      await this.emitInstance(record);
    },

    createInvokeForm() {
      console.log(this.modelItem);
      let stateList = this.modelItem.behavior;
      let input = [];
      let output = [];

      for (let i = 0; i < stateList.length; i++) {
        let state = stateList[i];
        let allInputsWithPara = state.inputs.concat(state.parameters);
        let detail = {};
        for (let j = 0; j < allInputsWithPara.length; j++) {
          //判断数据类型 如果是input--对应url

          detail["statename"] = state.name;
          detail["event"] = allInputsWithPara[j].name;

          if (Object.hasOwnProperty.call(allInputsWithPara[j], "value")) {
            detail["tag"] = allInputsWithPara[j].name;
            detail["url"] = allInputsWithPara[j].value;
            input.push(detail);
          } else {
            continue;
          }
        }

        for (let j = 0; j < state.outputs.length; j++) {
          let template = {};
          let outputTemplate = state.outputs[j].datasetItem;
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
          detail["template"] = template;
          output.push(detail);
        }
      }

      this.invokeForm.inputs = input;
      this.invokeForm.outputs = output;
      console.log(this.invokeForm);
    },
    handleChange() {},

    async createFilefromParam() {
      console.log("111", this.modelItem);
      let stateList = this.modelItem.behavior;
      for (let i = 0; i < stateList.length; i++) {
        let events = stateList[i].parameters;
        for (let j = 0; j < events.length; j++) {
          //判断如果是参数的话，重新绑定成为一个文件 之后上传 返回url绑定到mdl中去

          let content = "";
          let uploadFileForm = new FormData();

          let udxNodeList =
            events[j].datasetItem.UdxDeclaration[0].UdxNode[0].UdxNode;
          for (let k = 0; k < udxNodeList.length; k++) {
            if (Object.hasOwnProperty.call(udxNodeList[k], "value")) {
              // content += `<XDO name="${udxNodeList[k].name}" kernelType="${udxNodeList[k].type}" value="${udxNodeList[k].value}" />`;
              content += `<XDO name="${udxNodeList[k].name}" kernelType="string" value="${udxNodeList[k].value}" />`;
            }
          }
          if (content != "") {
            content = "<Dataset> " + content + " </Dataset>";
            let file = new File([content], events[j].name + ".xml", {
              type: "text/plain",
            });
            uploadFileForm.append("file", file);

            // this.createConfigFile();
            await this.submitUpload(i, j, uploadFileForm);
          }
        }
      }
    },

    async submitUpload(stateIndex, eventIndex, fileItem) {
      let param = fileItem.file;
      let uploadFileForm = new FormData();
      uploadFileForm.append("file", param);

      let data = await saveData(uploadFileForm, "", renderSize(param.size));

      this.$set(
        this.modelItem.behavior[stateIndex].parameters[eventIndex],
        "url",
        data.value
      );
    },

    async initTask() {
      //get task ip port ...

      let { data } = await initTask(this.modelItem.md5);

      this.invokeForm.ip = data.host;
      this.invokeForm.port = data.port;
      this.invokeForm.pid = this.modelItem.md5; //md5
      this.invokeForm.username = this.userId;
      console.log("data2", this.invokeForm);
    },
  },

  beforeDestroy() {
    clearInterval(this.timer);
  },

  mounted() {},
};
</script>
<style lang="scss" scoped>
.selectData {
  /deep/.el-dialog {
    height: 800px;
  }
}
</style>

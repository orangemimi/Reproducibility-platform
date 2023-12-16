<!--  -->
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
<<<<<<< Updated upstream:reproducibility_front/src/views/project/scenarios/Toolbars/ModelContent.vue
            size="mini"
            style="float:right"
            @click="getInstances"
          >
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
=======
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
              @row-click="rowClick"
              style="width: 100%"
            >
              <el-table-column key="need" type="expand" width="30">
                <template v-slot="props">
                  <el-table
                    border
                    :data="props.row.datasetItem.UdxDeclarationNew"
                  >
                    <el-table-column prop="name" label="Event name" width="200">
                    </el-table-column>
                    <el-table-column
                      prop="description"
                      label="Description"
                    ></el-table-column>
                    <el-table-column
                      width="200"
                      prop="type"
                      label="Type"
                    ></el-table-column>
                    <el-table-column width="150" prop="value" label="Value">
                      <template v-slot="scope">
                        <el-input v-model="scope.row.value"></el-input>
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
                    <!-- <button @click="test(scope)">123</button> -->
                    <el-input
                      v-model="scope.row.datasetItem.parameterValue"
                    ></el-input>
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
>>>>>>> Stashed changes:reproducibility_front/src/components/ModelContent/index.vue
    </div>
    <div class="selectData">
      <el-dialog
        :visible.sync="instanceDialogShow"
        width="1000px"
        title="Insatnces"
        :close-on-click-modal="false"
        :destroy-on-close="true"
      >
<<<<<<< Updated upstream:reproducibility_front/src/views/project/scenarios/Toolbars/ModelContent.vue
        {{ modelInstanceList }}
=======
        <el-table
          :data="modelInstanceList"
          style="width: 100%"
          :row-class-name="tableRowClassNameofInstance"
          @expand-change="handleExpandChange"
        >
          <el-table-column type="expand">
            <template v-slot="props">
              <h1>Model Configuration</h1>
              <br />
              <hr />
              <!-- {{ props.row }} -->
              <el-row class="table">
                <div
                  v-for="(state, index) in modelInstanceList.behavior"
                  :key="index"
                >
                  <el-row>
                    <state-description :stateItem="state"></state-description>
                  </el-row>
                  <el-table
                    :data="filterCurrentAllEventsWithStates(state)"
                    style="width: 100%"
                    :row-class-name="tableRowClassName"
                    type="expand"
                  >
                    <el-table-column label="Name" prop="name">
                    </el-table-column>
                    <el-table-column label="Type" prop="type">
                    </el-table-column>
                    <el-table-column label="Description" prop="description">
                    </el-table-column>

                    <el-table-column label="Data Download or Parameters Value">
                      <template v-slot="scope">
                        <div v-if="scope.row.type == 'parameters'">
                          <!-- <template v-slot="props">
                              <el-table
                                border
                                :data="props.row.datasetItem.UdxDeclarationNew"
                              >
                                <el-table-column
                                  prop="name"
                                  label="Event name"
                                  width="180"
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
                                  prop="value"
                                  label="Value"
                                >
                                  <template v-slot="scope">
                                    <el-input
                                      v-model="scope.row.value"
                                    ></el-input>
                                  </template>
                                </el-table-column>
                              </el-table>
                            </template> -->
                        </div>
                        <el-button
                          v-else
                          type="info"
                          round
                          @click="download(scope.row, props.row)"
                        >
                          <el-icon><Download /></el-icon>Download</el-button
                        >
                      </template>
                    </el-table-column>
                  </el-table>
                </div>
              </el-row>
            </template>
          </el-table-column>
          <el-table-column label="name" prop="name"> </el-table-column>
          <el-table-column label="createTime" prop="createTime">
          </el-table-column>
          <el-table-column label="statusEnum" prop="status">
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
          <el-table-column label="Operation">
            <template v-slot="props">
              <el-button
                v-show="props.row.status === '2'"
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
        <!-- {{ modelInstanceList }} -->
>>>>>>> Stashed changes:reproducibility_front/src/components/ModelContent/index.vue
      </el-dialog>
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
<<<<<<< Updated upstream:reproducibility_front/src/views/project/scenarios/Toolbars/ModelContent.vue
  </div>
</template>

<script>
import ResourceTable from "./ResourceTable.vue";
=======

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

>>>>>>> Stashed changes:reproducibility_front/src/components/ModelContent/index.vue
import {
  saveData,
  invokeSingleModel,
  initTask,
  saveInstance,
  updateInstance,
  getInstanceById,
  getRecordofSingleModel,
  getInstancesInScenario,
<<<<<<< Updated upstream:reproducibility_front/src/views/project/scenarios/Toolbars/ModelContent.vue
  //   getInstanceById,
} from "@/api/request";
import { renderSize } from "@/utils/utils";
import { mapState } from "vuex";
import { dateFormat } from "@/lib/utils";
export default {
=======
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
>>>>>>> Stashed changes:reproducibility_front/src/components/ModelContent/index.vue
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
  components: { ResourceTable },

  watch: {
    currentModel: {
      async handler(newVal) {
        if (Object.hasOwnProperty.call(newVal, "md5")) {
          this.modelItem = newVal;
<<<<<<< Updated upstream:reproducibility_front/src/views/project/scenarios/Toolbars/ModelContent.vue
=======
          this.canInvoke = true;
>>>>>>> Stashed changes:reproducibility_front/src/components/ModelContent/index.vue
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
<<<<<<< Updated upstream:reproducibility_front/src/views/project/scenarios/Toolbars/ModelContent.vue

  data() {
    return {
      modelItem: this.currentEvent,
      modelInstance: {},
      modelInstanceList: [],
=======
  data() {
    return {
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
>>>>>>> Stashed changes:reproducibility_front/src/components/ModelContent/index.vue
      instanceDialogShow: false,
      activeNames: 0,
      selectDataDialogShow: false,
      currentEvent: "",
      timer: {},
<<<<<<< Updated upstream:reproducibility_front/src/views/project/scenarios/Toolbars/ModelContent.vue
      recordOutput: {},
      refreshForm: {},
=======
      refreshForm: {},
      boundData: [],
      canInvoke: true,
      // modelWithCurrentAllEvents: [],
      dataFolderList: [],
>>>>>>> Stashed changes:reproducibility_front/src/components/ModelContent/index.vue
      invokeForm: {
        ip: "",
        port: "",
        pid: "",
        username: "",
        inputs: [
          {
            statename: "",
            event: "",
<<<<<<< Updated upstream:reproducibility_front/src/views/project/scenarios/Toolbars/ModelContent.vue
            url: "",
=======
            value: "",
>>>>>>> Stashed changes:reproducibility_front/src/components/ModelContent/index.vue
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
<<<<<<< Updated upstream:reproducibility_front/src/views/project/scenarios/Toolbars/ModelContent.vue

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

    async getInstances() {
=======
  methods: {
    // parametersInput(scope, row) {
    //   for (let behavior of this.currentModel.behavior) {
    //     for (let parameter of behavior.parameters) {
    //       if (row.eventId === parameter.eventId) {
    //         parameter.value = row.inputValue;
    //       }
    //     }
    //   }
    // },
    refreshData(val) {
      if (val) {
        this.getFolders();
      }
      // console.log("refresh");
    },
    rowClick(row) {
      // console.log("row", row);
      this.currentRow = row;
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
        this.$message({
          showClose: true,
          message: "Unbind successfully",
          type: "info",
        });
      } else {
        // 执行绑定逻辑
        this.bindInstanceId(row);
        this.$message({
          showClose: true,
          message: "Binding successful",
          type: "success",
        });
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
>>>>>>> Stashed changes:reproducibility_front/src/components/ModelContent/index.vue
      this.instanceDialogShow = true;
      this.modelInstanceList = await getInstancesInScenario(
        this.scenarioId,
        this.modelItem.id
      );
      await this.getInstanceStatus();
    },
<<<<<<< Updated upstream:reproducibility_front/src/views/project/scenarios/Toolbars/ModelContent.vue
    async getInstanceStatus() {
      this.modelInstanceList.forEach((instance) => {
        if (instance.status == 0) {
          this.getOutputs(instance, instance.refreshForm);
=======

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
>>>>>>> Stashed changes:reproducibility_front/src/components/ModelContent/index.vue
          //表示正在运行
        }
      });
    },
    // selectData(val) {
    //   let stateIndex = this.stateList.findIndex(
    //     (state) => state.name == this.currentEvent.stateName
    //   );

<<<<<<< Updated upstream:reproducibility_front/src/views/project/scenarios/Toolbars/ModelContent.vue
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
      debugger;
      await this.createFilefromParam();
      this.createInvokeForm();
      //测试数据没有弄 直接运行 根据ip+id
      //invoke
      let data = await invokeSingleModel(this.invokeForm);
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
        console.log("refreshForm", "refreshForm", refreshForm);
        await this.emitInstance(0, this.modelItem, refreshForm);
=======
    // 运行invoke
    initInvoke() {
      this.modelInstanceName_input =
        this.modelItem.name + "  ||  " + dateFormat(new Date());
      this.modelInvokeDialogShow = true;
    },
    // 开始invoke模型
    async startInvoke() {
      this.modelInvokeDialogShow = false;
      // debugger;
      try {
        await this.createFilefromParam();
        await this.createInvokeForm();
        console.log(this.invokeForm, "invokeForm");
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
>>>>>>> Stashed changes:reproducibility_front/src/components/ModelContent/index.vue
      }
    },
    async emitInstance(status, modelItem, refreshForm) {
      //在运行时 instance的创建
      if (status == 0 || status == -1) {
        let instanceTemp = {
          name: modelItem.name + "||" + dateFormat(new Date()),
          behavior: modelItem.behavior,
          status: status,
          scenarioId: this.scenarioId,
          modelId: modelItem.id,
          refreshForm: refreshForm,
          isReproduced: false,
        };
<<<<<<< Updated upstream:reproducibility_front/src/views/project/scenarios/Toolbars/ModelContent.vue
        await saveInstance(instanceTemp);
      } else {
        let instanceTemp = {
          behavior: modelItem.behavior,
          status: status,
        };
        console.log(instanceTemp);

        let data = await updateInstance(modelItem.id, instanceTemp);
        console.log("over", data);
      }
    },

=======
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

>>>>>>> Stashed changes:reproducibility_front/src/components/ModelContent/index.vue
    async getOutputs(instance, refreshForm) {
      debugger;
      //获得结果
      if (instance.status != 2) {
        let { data } = await getRecordofSingleModel(refreshForm);
<<<<<<< Updated upstream:reproducibility_front/src/views/project/scenarios/Toolbars/ModelContent.vue
        console.log(data);
=======
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
>>>>>>> Stashed changes:reproducibility_front/src/components/ModelContent/index.vue

        if (data.status != instance.status) {
          instance.status = data.status;
          if (data.status == 2) {
            await this.getStateEventOut(instance, data);
            // let data3 = await updateInstance(this.scenarioId, stepResource);
          }
        }
      }
    },

<<<<<<< Updated upstream:reproducibility_front/src/views/project/scenarios/Toolbars/ModelContent.vue
    async getStateEventOut(instance, record) {
=======
    getStateEventOut(instance, record) {
>>>>>>> Stashed changes:reproducibility_front/src/components/ModelContent/index.vue
      let stateList = instance.behavior;
      let outputUrl = record.outputs;
      outputUrl.forEach((el) => {
        stateList.forEach((state, index) => {
          if (state.name == el.statename) {
            state.outputs.forEach((event, eventIndex) => {
              if (el.event == event.name) {
<<<<<<< Updated upstream:reproducibility_front/src/views/project/scenarios/Toolbars/ModelContent.vue
                this.$set(
                  instance.behavior[index].outputs[eventIndex],
                  "value",
                  el.url
                );
=======
                instance.behavior[index].outputs[eventIndex]["value"] = el.url;
>>>>>>> Stashed changes:reproducibility_front/src/components/ModelContent/index.vue
              }
            });
          }
        });
      });

<<<<<<< Updated upstream:reproducibility_front/src/views/project/scenarios/Toolbars/ModelContent.vue
      console.log(instance);
      await this.emitInstance(instance.status, instance, instance.refreshForm);
=======
      return instance;
>>>>>>> Stashed changes:reproducibility_front/src/components/ModelContent/index.vue
    },

    createInvokeForm() {
<<<<<<< Updated upstream:reproducibility_front/src/views/project/scenarios/Toolbars/ModelContent.vue
      console.log(this.modelItem);
      let stateList = this.modelItem.behavior;
      let input = [];
      let output = [];
=======
      let stateList = this.modelItem.behavior;
      this.invokeForm.inputs = [];
      this.invokeForm.outputs = [];
>>>>>>> Stashed changes:reproducibility_front/src/components/ModelContent/index.vue

      for (let i = 0; i < stateList.length; i++) {
        let state = stateList[i];
        let allInputsWithPara = state.inputs.concat(state.parameters);
<<<<<<< Updated upstream:reproducibility_front/src/views/project/scenarios/Toolbars/ModelContent.vue
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
=======
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
>>>>>>> Stashed changes:reproducibility_front/src/components/ModelContent/index.vue
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
<<<<<<< Updated upstream:reproducibility_front/src/views/project/scenarios/Toolbars/ModelContent.vue
          detail["template"] = template;
          output.push(detail);
        }
      }

      this.invokeForm.inputs = input;
      this.invokeForm.outputs = output;
      console.log(this.invokeForm);
=======
          let detail = {
            statename: state.name,
            event: item.name,
            template: template,
          };

          this.invokeForm.outputs.push(detail);
        });
      }

      console.log(this.invokeForm, "this.invokeForm");
>>>>>>> Stashed changes:reproducibility_front/src/components/ModelContent/index.vue
    },
    handleChange() {},

    async createFilefromParam() {
<<<<<<< Updated upstream:reproducibility_front/src/views/project/scenarios/Toolbars/ModelContent.vue
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
=======
      let stateList = this.modelItem.behavior;
      console.log(stateList, "0");
      for (let i = 0; i < stateList.length; i++) {
        let events = stateList[i].parameters;
        if (events.length == 0) {
          return;
        }
        console.log("1.1");
        for (let j = 0; j < events.length; j++) {
          let content = "";
          // let uploadFileForm = new FormData();

          let udxNodeList = events[j].datasetItem.UdxDeclarationNew;
          console.log(udxNodeList);
          for (let k = 0; k < udxNodeList.length; k++) {
            if (Object.hasOwnProperty.call(udxNodeList[k], "value")) {
              // content += `<XDO name="${udxNodeList[k].name}" kernelType="${udxNodeList[k].type}" value="${udxNodeList[k].value}" />`;
              content += `<XDO name="${events[j].name}" kernelType="string" value="${udxNodeList[k].value}" />`;
            }
          }

          if (content != "") {
            content = "<Dataset> " + content + " </Dataset>";
            let file = new File([content], events[j].name + ".xml", {
              type: "text/plain",
            });

            await this.submitUpload(i, j, file);
>>>>>>> Stashed changes:reproducibility_front/src/components/ModelContent/index.vue
          }
        }
      }
    },

    async submitUpload(stateIndex, eventIndex, fileItem) {
<<<<<<< Updated upstream:reproducibility_front/src/views/project/scenarios/Toolbars/ModelContent.vue
      let param = fileItem.file;
      let uploadFileForm = new FormData();
      uploadFileForm.append("file", param);

      let data = await saveData(uploadFileForm, "", renderSize(param.size));

      this.$set(
        this.modelItem.behavior[stateIndex].parameters[eventIndex],
        "url",
        data.value
      );
=======
      let uploadFileForm = new FormData();
      uploadFileForm.append("file", fileItem);
      let data = await saveData(
        uploadFileForm,
        renderSize(fileItem.size),
        "intermediate"
      );
      this.modelItem.behavior[stateIndex].parameters[eventIndex]["value"] =
        data;
>>>>>>> Stashed changes:reproducibility_front/src/components/ModelContent/index.vue
    },

    async initTask() {
      //get task ip port ...
<<<<<<< Updated upstream:reproducibility_front/src/views/project/scenarios/Toolbars/ModelContent.vue

      let { data } = await initTask(this.modelItem.md5);

      this.invokeForm.ip = data.host;
      this.invokeForm.port = data.port;
      this.invokeForm.pid = this.modelItem.md5; //md5
      this.invokeForm.username = this.userId;
    },
  },
=======
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
>>>>>>> Stashed changes:reproducibility_front/src/components/ModelContent/index.vue

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
<<<<<<< Updated upstream:reproducibility_front/src/views/project/scenarios/Toolbars/ModelContent.vue
=======
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
    // :deep(.el-table .input-row .cell .el-table__expand-icon) {
    //   display: none;
    // }

    :deep(.input-row .el-table__expand-column .cell) {
      display: none;
    }

    :deep(.output-row .el-table__expand-column .cell) {
      display: none;
    }
>>>>>>> Stashed changes:reproducibility_front/src/components/ModelContent/index.vue
  }
}
</style>

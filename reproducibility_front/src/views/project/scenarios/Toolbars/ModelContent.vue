<!--  -->
<template>
  <div>
    <div class="main">
      <el-row class="title">
        <el-col>{{ modelItem.name }}</el-col>
      </el-row>

      <el-row>
        <el-col :span="16">
          <p class="description">{{ modelItem.description }}</p>
        </el-col>
        <el-button
          type="primary"
          @click="invokeTest"
          style="float:right;width:110px;margin-right:15%"
        >
          <i class="el-icon-setting"></i>&nbsp;Invoke
        </el-button>
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
        <resource-table></resource-table>
      </el-dialog>
    </div>
  </div>
</template>

<script>
import ResourceTable from "./ResourceTable.vue";
export default {
  props: {
    modelItem: {
      type: Object,
    },
  },
  components: { ResourceTable },

  watch: {},

  computed: {},

  data() {
    return { activeNames: 0, selectDataDialogShow: false, currentEvent: "" };
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
    selectData(val) {
      let stateIndex = this.stateList.findIndex(
        (state) => state.name == this.currentEvent.stateName
      );

      let eventIndex = this.stateList[stateIndex].Event.findIndex(
        (event) => event.name == this.currentEvent.name
      );
      this.$set(
        this.stateList[stateIndex].Event[eventIndex],
        "url",
        val.pathURL
      );
      this.$set(
        this.stateList[stateIndex].Event[eventIndex],
        "urlName",
        val.name
      );

      this.selectDataDialogShow = false;
    },

    invokeTest() {},
    handleChange() {},
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

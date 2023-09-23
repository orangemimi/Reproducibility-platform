<template>
  <!-- reproduce invoke -->
  <div class="main">
    <el-col :span="24">
      <div class="group">
        <el-row class="title">Input</el-row>
        <div class="items" v-if="service.behavior">
          <el-row
            v-for="(input, inputIndex) in service.behavior.inputs"
            :key="inputIndex"
            class="item"
          >
            <el-row>
              <el-col :span="10">
                <span class="event-name">
                  <span v-show="!input.isOptional" style="color:red">*</span
                  >{{ input.name }}</span
                >
              </el-col>
              <el-col :span="13" :offset="1">
                <el-select
                  v-model="input.dataServiceId"
                  placeholder="Please bind"
                >
                  <el-option-group
                    key="originalDataServices"
                    label="Original input data resources"
                  >
                    <el-option
                      v-for="dataService in dataServices.originalDataServices"
                      :key="dataService.id"
                      :label="dataService.name"
                      :value="dataService.id"
                    >
                      <span style="float: left">{{ dataService.name }}</span>
                    </el-option>
                  </el-option-group>

                  <el-option-group
                    key="intermediateDataServices"
                    label="Initial intermediate output by authors"
                  >
                    <el-option
                      v-for="dataService in dataServices.intermediateDataServices"
                      :key="dataService.id"
                      :label="dataService.name"
                      :value="dataService.id"
                      v-show="!dataService.isReproduced"
                    >
                      <span style="float: left">{{ dataService.name }}</span>
                    </el-option>
                  </el-option-group>
                  <el-option-group
                    key="intermediateDataServicesReproduced"
                    label="Reproduced intermediate output"
                  >
                    <el-option
                      v-for="dataService in dataServices.intermediateDataServices"
                      :key="dataService.id"
                      :label="dataService.name"
                      :value="dataService.id"
                      v-show="dataService.isReproduced"
                    >
                      <span style="float: left">{{ dataService.name }}</span>
                    </el-option>
                  </el-option-group>
                </el-select>
              </el-col>
            </el-row>
            <el-row>{{ input.description }}</el-row>
          </el-row>
        </div>
      </div>

      <div class="group" v-show="service.behavior.parameters">
        <el-row class="title">params</el-row>
        <div class="items">
          <el-row
            v-for="(parameter, parameterIndex) in service.behavior.parameters"
            :key="parameterIndex"
            class="item"
          >
            <el-row>
              <el-col :span="10">
                <span class="event-name"> {{ parameter.name }}</span>
              </el-col>
              <el-col :span="13" :offset="1">
                <el-tooltip
                  :content="parameter.tooltip"
                  placement="top"
                  effect="light"
                >
                  <component
                    :is="typeMapping(parameter.type)"
                    :initParameter="parameter"
                  ></component>
                </el-tooltip>
              </el-col>
            </el-row>
            <el-row>{{ parameter.description }}</el-row>
          </el-row>
        </div>
      </div>

      <div class="group">
        <el-row class="title">Output</el-row>
        <div class="items">
          <el-row
            v-for="(output, outputIndex) in service.behavior.outputs"
            :key="outputIndex"
            class="item"
          >
            <el-row>
              <el-col :span="10">
                <span class="event-name"> {{ output.name }}</span>
              </el-col>
              <el-col :span="13" :offset="1">
                <div>
                  null
                </div>
              </el-col>
            </el-row>
            <el-row>{{ output.description }}</el-row>
          </el-row>
        </div>
      </div>
    </el-col>

    <div class="slider scrollbar">
      <div class="image">
        <img
          :src="imgPath(service.snapshot, service.name)"
          v-if="service.name != undefined"
        />
      </div>

      <h2 class="title">{{ service.name }}</h2>
      <h3 class="desc">Introduction</h3>
      <p>{{ service.description }}</p>
      <div v-if="service.agentInfo">
        <h3 class="desc">Author</h3>
        <p>{{ service.agentInfo[0].name }}</p>
        <!-- <p>{{ service.agentInfo[0].type }}</p> -->
      </div>

      <el-col :span="2" :offset="1">
        <el-row>
          <el-button
            class="btn"
            plain
            type="primary"
            @click="showDialogInvoke()"
          >
            Re-invoke</el-button
          >
        </el-row>
        <el-row>
          <el-button
            class="btn"
            plain
            type="primary"
            @click="showDialogInstances()"
          >
            instances</el-button
          >
        </el-row>
      </el-col>
    </div>

    <el-dialog title="运行实例" :visible.sync="dialogInvokeVisible">
      <el-input v-model="instanceName" autocomplete="off">
        <template slot="prepend">运行实例名称:</template></el-input
      >
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogInvokeVisible = false">取 消</el-button>
        <el-button type="primary" @click="invoke()">Invoke</el-button>
      </span>
    </el-dialog>

    <el-dialog
      title="Instances"
      :visible.sync="dialogInstancesVisible"
      width="80%"
    >
      <!-- {{ instancesData }} -->
      <el-table :data="instancesData" stripe style="width: 100%">
        <el-table-column type="expand">
          <template slot-scope="props">
            <h1>Model Configuration</h1>
            <el-divider></el-divider>
            <innerTable :behavior="props.row.service.behavior"></innerTable>
          </template>
        </el-table-column>

        <el-table-column prop="name" label="name" width="150"></el-table-column>
        <el-table-column
          prop="createTime"
          label="createTime"
          width="200"
        ></el-table-column>
        <el-table-column
          prop="statusEnum"
          label="statusEnum"
          :filters="[
            { text: 'success', value: 'FINISH' },
            { text: 'error', value: 'ERROR' },
            { text: 'run', value: 'RUN' },
          ]"
          :filter-method="filterTag"
          filter-placement="bottom-end"
        >
          <template slot-scope="scope">
            <el-tag
              :type="statusEnumTag(scope.row.statusEnum)"
              disable-transitions
              >{{ scope.row.statusEnum }}</el-tag
            >
          </template>
        </el-table-column>
        <el-table-column label="isReproduced" width="120">
          <template slot-scope="scope">
            {{ scope.row.isReproduced }}
          </template>
        </el-table-column>
        <el-table-column label="Operation">
          <template slot-scope="scope">
            <el-button
              size="small"
              round
              @click="handleBind(scope.row)"
              :disabled="!scope.row.isReproduced"
              >Bind</el-button
            >
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script>
import InputParameter from "_com/behaviorUI/InputParameter";
import RangeParameter from "_com/behaviorUI/RangeParameter";
import SelectParameter from "_com/behaviorUI/SelectParameter";
import SliderParameter from "_com/behaviorUI/SliderParameter";
import TableParameter from "_com/behaviorUI/TableParameter";
import { dateFormat } from "@/lib/utils";
import { get, post } from "@/axios";
import innerTable from "_com/innerTable";
import { imgBase64 } from "@/lib/utils";
import { mapState } from "vuex";
export default {
  // props: ["service", "type", "dataServices"],
  computed: {
    ...mapState(["user"]),
    // filterData(isReproduced) {
    //   // console.log(this.dataServices.intermediateDataServices);
    //   return this.dataServices.intermediateDataServices.filter(
    //     (el) => isReproduced == el.isReproduced
    //   );
    // },
  },
  data() {
    return {
      id: this.$route.params.id,
      serviceId: "",
      type: this.$route.params.type,
      service: {},
      dialogInvokeVisible: false,
      dialogInstancesVisible: false,
      instancesData: [],
      dataServices: [],
      instanceName: "",
      g2s: {},
    };
  },
  async mounted() {
    // console.log(this.$route.params.serviceId);
    let service;
    // let instanceId = this.$route.query.instanceId;

    if (this.type === "model") {
      service = await get("/model_service/{id}", null, { id: this.id });
      // console.log(service);
      // debugger;
    } else {
      service = await get("/data_process_service/{id}", null, {
        id: this.id,
      });
    }
    // console.log("service", service);
    if (
      service.behavior.parameters != undefined ||
      service.behavior.parameters != null ||
      service.behavior.parameters.length != 0
    ) {
      service.behavior.parameters.forEach((parameter) => {
        if (
          parameter.defaultValue != null ||
          parameter.defaultValue != undefined
        ) {
          parameter.value = parameter.defaultValue;
        }
      });
    }
    this.service = service;
    let pid = this.$route.params.projectId;
    console.log(this.service);

    let dataServices = await get(`/g2s/{pid}/dataServices`, null, {
      pid,
    });
    this.dataServices = dataServices;

    // console.log(this.$route.params);
    this.g2s = await get("/g2s/{id}/view", null, {
      id: this.$route.params.projectId,
    });
    this.serviceId = this.service.id;
    // this.dataServices = service.
  },

  methods: {
    imgPath(snapshot, name) {
      if (snapshot != undefined) {
        return snapshot;
      } else {
        return imgBase64(name);
      }
    },
    async handleBind(row) {
      console.log(row.id);
      await post(
        "/g2s/{id}/bind/{instanceId}?instanceEnum=" + row.instanceEnum,
        null,
        {
          id: this.$route.params.projectId,
          instanceId: row.id,
        }
      );

      this.$message({
        message: "绑定成功",
        type: "success",
      });
      this.dialogInstancesVisible = false;
    },
    statusEnumTag(status) {
      if (status === "FINISH") {
        return "success";
      } else if (status === "ERROR") {
        return "danger";
      } else {
        return "info";
      }
    },
    filterTag(value, row) {
      return row.statusEnum === value;
    },
    showDialogInvoke() {
      this.dialogInvokeVisible = true;
      this.instanceName =
        this.service.name +
        "_ReproduceOut_" +
        this.user.name +
        dateFormat(new Date());
    },
    showDialogInstances() {
      this.dialogInstancesVisible = true;
      this.refreshInstances();
    },

    async refreshInstances() {
      console.log({
        id: this.$route.params.projectId,
        type: "MODEL",
        serviceId: this.service.id,
      });
      this.instancesData = await get(
        "/g2s/{id}/getInstances/{type}/{serviceId}",
        null,
        {
          id: this.$route.params.projectId,
          type: "MODEL",
          serviceId: this.service.id,
        },
        false
      );
    },
    async invoke() {
      this.service.details = null;
      await post(
        "/g2s/{id}/invoke",
        {
          name: this.instanceName,
          statusEnum: "READY",
          instanceEnum: "MODEL",
          service: this.service,
          creator: this.$store.state.user.name,
          isReproduced: true,
        },
        {
          id: this.$route.params.projectId,
        }
      );
      this.dialogInvokeVisible = false;
    },
    typeMapping(type) {
      let vueType;
      switch (type) {
        case "input_parameter":
          {
            vueType = "InputParameter";
          }
          break;
        case "range_parameter":
          {
            vueType = "RangeParameter";
          }
          break;
        case "slider_parameter":
          {
            vueType = "SliderParameter";
          }
          break;
        case "select_parameter":
          {
            vueType = "SelectParameter";
          }
          break;
        case "table_parameter":
          {
            vueType = "TableParameter";
          }
          break;
      }
      return vueType;
    },
  },

  components: {
    InputParameter,
    RangeParameter,
    SelectParameter,
    SliderParameter,
    TableParameter,
    innerTable,
  },
};
</script>

<style>
.table-expand {
  font-size: 0;
}
.table-expand label {
  width: 90px;
  color: #99a9bf;
}
.table-expand .el-form-item {
  margin-right: 0;
  margin-bottom: 0;
  width: 50%;
}
.main {
  position: relative;
}

.slider {
  /* border-right: solid 2px rgb(156, 154, 154); */
  min-height: 800px;
  max-height: 800px;
  overflow-y: auto;
  overflow-x: hidden;
  word-wrap: break-word;
  padding: 10px 10px;
}
@media screen and (min-width: 1300px) {
  .main .slider {
    width: 300px;
  }
}

@media screen and (max-width: 1140px) {
  .main .slider {
    width: 20%;
  }
}

@media screen and (max-width: 975px) {
  .main .slider {
    width: 25%;
  }
}
.image {
  max-width: 80%;
  max-height: 45%;
  overflow: hidden;
}
.image > img {
  max-width: 100%;
  min-width: 100%;
  overflow: hidden;
}

.btn {
  margin-top: 20px;
}

.title {
  position: relative;
  font-size: 18px;
  padding: 0px 0px 30px 0px;
}
.title i {
  font-size: 30px !important;
}
.group {
  position: relative;
  padding-bottom: 30px;
}
.group > .title {
  font-style: italic;
  font-size: 12px;
  padding-bottom: 10px;
  border-bottom: solid 2px #999;
}
.group > .items {
  padding: 10px 0px 6px 50px;
}
.group > .items > .item {
  padding: 20px 10px 20px 0px;
  border-bottom: dotted 1px #999999;
  line-height: 2;
}
</style>

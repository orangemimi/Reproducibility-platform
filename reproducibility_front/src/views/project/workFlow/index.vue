<template>
  <div class="mainContent">
    <div style="position:relative">
      <h1 class="title" :title="this.scenario.name" v-if="this.scenario.name">
        {{ this.scenario.name }}
      </h1>
      <h1 class="title" :title="this.scenario.name" v-else>Scenario unnamed</h1>
      <el-button
        @click="folkVisible = true"
        style="position:absolute; right:65px;top:0px;"
        type="info"
        circle
        icon="el-icon-connection"
      ></el-button>
    </div>
    <el-row>
      <el-col :span="22" :offset="1">
        <el-row>
          <!-- <el-col :span="5">
              <el-card class="leftContent">
                <div class="single-info">
                  <div class="info-tag">
                    <i class="el-icon-user-solid" />
                    <strong style="margin-left:5px">Creator</strong>
                  </div>
                  <span>{{ this.g2s.creator }}</span>
                </div>
  
                <div class="single-info">
                  <div class="info-tag">
                    <i class="el-icon-time" />
                    <strong style="margin-left:5px">Create Time</strong>
                  </div>
                  <span>{{ this.g2s.createTime }}</span>
                </div>
  
                <el-collapse v-model="activeNamesLeft">
                  <el-collapse-item title="goals" name="1">
                    <div>{{ g2s.goals }}</div>
                  </el-collapse-item>
                  <el-collapse-item title="background" name="2">
                    <div>{{ g2s.background }}</div>
                  </el-collapse-item>
                </el-collapse>
              </el-card>
            </el-col> -->
          <!-- <el-col :span="22" :offset="1"> -->
          <div class="rightContent">
            <el-row class="rightContentCard">
              <el-card>
                <el-collapse v-model="activeNamesRight">
                  <!-- <el-collapse-item title="Context Definition" name="1">
                        <el-tabs v-model="activeContext" v-if="g2s.contextDefine">
                          <el-tab-pane label="Theme" name="theme">{{
                            g2s.contextDefine.theme
                          }}</el-tab-pane>
                          <el-tab-pane label="Geographic Object" name="object">{{
                            g2s.contextDefine.object
                          }}</el-tab-pane>
                          <el-tab-pane label="Boundary" name="boundary">{{
                            g2s.contextDefine.boundary
                          }}</el-tab-pane>
                        </el-tabs>
                      </el-collapse-item> -->
                  <el-collapse-item title="Resource Collection" name="2">
                    <el-tabs v-model="activeResource">
                      <el-tab-pane label="Data Services" name="data">
                        <el-table
                          :data="dataTable"
                          style="width: 100%;  font-size: 18px;"
                          max-height="400"
                        >
                          <el-table-column
                            prop="name"
                            label="Name"
                            width="300"
                          ></el-table-column>
                          <!-- <el-table-column
                              prop="createTime"
                              label="CreateTime"
                              width="200"
                            ></el-table-column> -->
                          <el-table-column
                            prop="description"
                            label="description"
                            width="600"
                          ></el-table-column>
                          <el-table-column fixed="right" label="operation">
                            <template slot-scope="scope">
                              <el-button
                                @click.native.prevent="download(scope.row)"
                                type="text"
                                icon="el-icon-download"
                                >Download</el-button
                              >
                            </template>
                            {{ url }}
                          </el-table-column>
                        </el-table>
                      </el-tab-pane>

                      <!-- <el-tab-pane label="Data Process Services" name="process">
                          <el-table :data="dataProcessTable" style="width: 100%">
                            <el-table-column
                              prop="name"
                              label="Name"
                              width="180"
                            ></el-table-column>
                            <el-table-column
                              prop="createTime"
                              label="CreateTime"
                              width="180"
                            ></el-table-column>
                            <el-table-column
                              prop="description"
                              label="description"
                            ></el-table-column>
                            <el-table-column fixed="right" label="operation">
                              <template slot-scope="scope">
                                <el-button
                                  @click.native.prevent="
                                    view(scope.row, 'process')
                                  "
                                  type="text"
                                  size="small"
                                  >view</el-button
                                >
  
                                <el-button
                                  @click.native.prevent="
                                    invoke(scope.row, 'process')
                                  "
                                  type="text"
                                  size="small"
                                  >invoke</el-button
                                >
                              </template>
                            </el-table-column>
                          </el-table>
                        </el-tab-pane> -->

                      <el-tab-pane label="Model Services" name="model">
                        <el-table
                          :data="modelTable"
                          style="width: 100%;  font-size: 18px;"
                          max-height="400"
                        >
                          <el-table-column
                            prop="modelName"
                            label="Name"
                            width="300"
                          ></el-table-column>
                          <el-table-column
                            prop="invokeTime"
                            label="Invoke Time"
                            width="350"
                          ></el-table-column>
                          <el-table-column
                            prop="modelDescription"
                            label="Description"
                            width="400"
                          ></el-table-column>
                          <!-- <el-table-column
                              prop="type"
                              label="Type"
                            ></el-table-column> -->

                          <el-table-column fixed="right" label="operation">
                            <template slot-scope="scope">
                              <el-button
                                @click.native.prevent="view(scope.row, 'model')"
                                type="text"
                                >view</el-button
                              >

                              <el-button
                                @click.native.prevent="
                                  invoke(scope.row, 'model')
                                "
                                type="text"
                                >invoke</el-button
                              >
                            </template>
                          </el-table-column>
                        </el-table>
                      </el-tab-pane>
                    </el-tabs>
                  </el-collapse-item>
                </el-collapse>
              </el-card>
            </el-row>

            <el-row class="rightContentCard">
              <el-card>
                <el-tabs v-model="activeExpected" class="instanceCard">
                  <el-tab-pane
                    label="Service Instances"
                    name="instance"
                    style="display: flex; flex-wrap: wrap;justify-content:space-around"
                  >
                    <el-col
                      :span="7"
                      :offset="0"
                      v-for="(instance, index) in instanceCard"
                      :key="index"
                      v-show="!instance.isReproduced"
                    >
                      <InstanceCard :cardData="instance"></InstanceCard>
                    </el-col>
                  </el-tab-pane>

                  <!-- <el-tab-pane label="Evaluation" name="evaluation">
                        <el-col
                          :span="7"
                          :offset="1"g2s.computation.serviceInstances
                          v-for="(evaluation, index) in evaluationCard"
                          :key="index"
                        >
                          <InstanceCard :cardData="evaluation"></InstanceCard>
                        </el-col>
                      </el-tab-pane> -->

                  <!-- <el-tab-pane label="Workflow" name="workflow">
                      <el-button
                        @click="fullScreen"
                        icon="el-icon-full-screen"
                        primary
                      ></el-button>
                      <workflow
                        id="fullScreenComponent"
                        :expectedInstances="g2s.computation.serviceInstances"
                      ></workflow>
                    </el-tab-pane> -->
                </el-tabs>
              </el-card>
            </el-row>
            <el-row class="rightContentCard">
              <el-card>
                <h1>Simulation Dataflow</h1>
                <mxgraph
                  id="fullScreenComponent"
                  :expectedInstances="instanceList"
                ></mxgraph>
              </el-card>
            </el-row>
          </div>
          <!-- </el-col> -->
        </el-row>
      </el-col>
    </el-row>

    <el-dialog title="Folk Geographic Simulation" :visible.sync="folkVisible">
      <el-form :model="g2s_folk">
        <el-form-item label="Name">
          <el-input v-model="g2s_folk.name" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="Description">
          <el-input
            type="textarea"
            :rows="2"
            v-model="g2s_folk.description"
            autocomplete="off"
          ></el-input>
        </el-form-item>
        <el-form-item label="Background">
          <el-input
            type="textarea"
            :rows="2"
            v-model="g2s_folk.background"
            autocomplete="off"
          ></el-input>
        </el-form-item>
        <el-form-item label="Goals">
          <el-input
            type="textarea"
            :rows="2"
            v-model="g2s_folk.goals"
            autocomplete="off"
          ></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="folk()">Create</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
// import workflow from "./components/workflow";
import mxgraph from "./components/MxGraph";
import { post } from "@/axios";
import InstanceCard from "_com/common/InstanceCard";
// import config from "@/config";
import {
  getProjectById,
  getScenarioById,
  getInstancesByIds,
  getUserInfoByUserId,
  getModelById,
} from "@/api/request";
export default {
  data() {
    return {
      scenario: [],
      project: [],
      userInfo: [],
      creator: "",
      ExpectedInstances: [],
      instanceTest: [],
      id: this.$route.params.id,
      folkVisible: false,
      g2s_folk: {},
      g2s: {
        contextDefine: {
          theme: {},
          object: {},
          boundary: {},
        },
        resourceCollect: {
          dataServices: [],
          modelServices: [],
          dataProcessServices: [],
        },
        simulationConceptGraph: {
          imgGraph: null,
        },
        computation: {
          serviceInstances: [],
        },
        evaluation: [],
      },
      activeNamesLeft: ["1", "2"],
      activeNamesRight: ["1", "2"],
      activeResource: "data",
      activeContext: "theme",
      activeExpected: "instance",
      fullscreenFlag: false,
      url: "",
    };
  },
  computed: {
    instanceList() {
      console.log(this.ExpectedInstances, "originData");
      return this.ExpectedInstances.map((item) => {
        let [name, createTime] = item.name.split("||");
        let {
          id,
          index,
          isReproduced,
          updateTime,
          status,
          modelId,
          modelDescription,
        } = item;
        let stateEnum =
          status === "2" ? "success" : status === "-1" ? "failed" : "running";
        let service = {};
        service.behavior = item.behavior[0];
        service.name = name;
        let instanceEnum = "MODEL";
        let creator = this.creator;
        return {
          createTime,
          creator,
          id,
          index,
          instanceEnum,
          isReproduced,
          name,
          service,
          stateEnum,
          updateTime,
          modelId,
          modelDescription,
        };
      });
    },
    instanceCard() {
      let arr = [];
      this.instanceList.forEach(
        ({
          id,
          name,
          createTime,
          creator,
          instanceEnum,
          service,
          isReproduced,
          modelDescription,
        }) => {
          let inner = {
            id,
            name,
            description: modelDescription,
            createTime,
            creator,
            serviceId: service.id,
            isReproduced,
          };
          if (instanceEnum === "MODEL") {
            inner.type = "model";
          } else {
            inner.type = "process";
          }
          // 检查新JSON对象的name属性是否与已有JSON对象相等
          let duplicate = arr.some(function(item) {
            return item.name === inner.name;
          });
          if (!duplicate) {
            arr.push(inner);
          }
          // arr.push(inner);
        }
      );
      return arr;
    },
    evaluationCard() {
      let arr = [];
      this.g2s.evaluation.forEach(
        ({ id, name, description, createTime, creator }) => {
          let inner = {
            serviceId: id,
            name,
            description,
            createTime,
            creator,
          };
          inner.type = "evaluation";
          arr.push(inner);
        }
      );
      return arr;
    },
    dataTable() {
      let arr = this.ExpectedInstances.map((item) => {
        let behavior = item.behavior[0];
        let { inputs, outputs, parameters } = behavior;
        return [...inputs, ...outputs, ...parameters];
      });
      let newArr = [];
      arr.forEach((item) => {
        newArr = [...newArr, ...item];
      });
      return newArr;
    },
    dataProcessTable() {
      let arr = [];
      if (this.g2s.resourceCollect.dataProcessServices == null) {
        return;
      }
      this.g2s.resourceCollect.dataProcessServices.forEach(
        ({ id, name, description, createTime }) => {
          let inner = {
            id,
            name,
            description,
            createTime,
            // type:"Simulation calculation"，
          };
          arr.push(inner);
        }
      );
      return arr;
    },
    modelTable() {
      // let arr = [];
      // this.g2s.resourceCollect.modelServices.forEach(
      //   ({ id, name, description, createTime }) => {
      //     let inner = {
      //       id,
      //       name,
      //       description,
      //       createTime,
      //       type: "Simulation calculation",
      //     };
      //     arr.push(inner);
      //   }
      // );
      return this.ExpectedInstances.map((item) => {
        let { modelDescription, modelId, name } = item;
        console.log(name, "101");
        let [modelName, invokeTime] = name.split("||");
        return {
          modelId,
          modelDescription,
          modelName,
          invokeTime,
        };
      });
    },
  },

  methods: {
    // 这个方法叫什么不重要，你只需要知道所有的数据，这个页面和子页面你需要的所有原始数据都在这里，就够了
    async getExpectedInstances() {
      // 先拿到对应的工程 所包含的场景id
      this.project = await getProjectById(this.$router.currentRoute.params.id);
      this.userInfo = await getUserInfoByUserId(this.project.creatorId);
      this.creator = this.userInfo.name;
      // 根据场景中instances获取对应的boundInstanceList
      this.scenario = await getScenarioById(this.project.scenario);
      console.log(this.scenario, "this.scenario");
      let boundInstanceList = await getInstancesByIds(this.scenario.instances);
      let newBoundInstanceList = boundInstanceList.map(async (item) => {
        let modelInfo = await getModelById(item.modelId);
        return {
          ...item,
          modelDescription: modelInfo.description,
        };
      });
      newBoundInstanceList = await Promise.all(newBoundInstanceList);
      console.log(newBoundInstanceList, "222");
      this.ExpectedInstances = newBoundInstanceList;
    },
    download(row) {
      let urls = row.value;
      // 创建一个链接元素
      const link = document.createElement("a");
      link.href = urls;
      link.target = "_blank"; // 在新窗口中打开链接
      link.download = "downloaded_file"; // 设置下载的文件名
      // 模拟点击链接，触发下载
      link.click();
      // this.url = `${config.containerURL}/data_service/fetch/${row.id}`;
      // console.log(this.url);
      // window.open(`${config.containerURL}/data_service/fetch/${row.id}`);
    },
    view(row, type) {
      this.$router.push({
        path: `/resource/${row.id}/${type}`,
      });
    },
    invoke(row, type) {
      this.$router.push({
        path: `/resource/${row.id}/${type}/invoke`,
      });
    },
    // fullScreen() {
    //   let element = document.getElementById("fullScreenComponent");
    //   element.requestFullscreen();
    //   this.fullscreenFlag = true;
    // },
    async folk() {
      this.g2s_folk.creator = this.$store.state.user.name;
      let { id } = await post(`/g2s/${this.id}/folk`, this.g2s_folk);
      this.$router.push({ path: `/g2s/${id}` });
      this.folkVisible = false;
    },
  },
  created() {
    document.addEventListener("keyup", (el) => {
      if (el.keyCode == 27) {
        this.fullscreenFlag = false;
      }
    });
  },
  async mounted() {
    this.getExpectedInstances();
    // this.g2s = await get("/g2s/{id}/view", null, {
    //   id: this.id,
    // });
  },
  components: {
    // workflow,
    InstanceCard,
    mxgraph,
  },
};
</script>

<style scoped>
.mainContent {
  /* background-color: rgba(116, 111, 111, 0.2); */
  /* height: 700px; */
  height: 100%;
}
.title {
  text-align: center;
  margin-top: 15px;
  margin-bottom: 20px;
  max-width: 800px;
  margin-left: auto;
  margin-right: auto;
  color: #0366d6;
}
.info {
  display: flex;
  align-items: center;
  margin-right: 10px;
}
.single-info {
  display: flex;
  align-items: flex-start;
  padding: 5px;
  /* height: 30px; */
  font-size: 12px;
  line-height: 15px;
}
.info-tag {
  display: flex;
  width: 120px;
  align-items: center;
}
.rightContent {
  /* margin-top: 20px; */
  flex: 1;
}
.leftContent /deep/.el-collapse-item__header {
  height: 40px;
  padding-left: 5px;
  font-weight: 600;
}
.rightContent /deep/.el-collapse-item__header {
  background-color: rgb(243, 243, 243);
  border-radius: 5px;
  border: 1px solid rgb(222, 222, 222);
  padding-left: 20px;
  height: 40px;
  font-weight: 600;
  font-size: 16px;
}
.rightContent /deep/.el-collapse-item__wrap {
  margin: 20px 20px;
  border-bottom: 0;
  line-height: 0;
}
.rightContent /deep/ .el-tabs__item {
  height: 35px;
}

.rightContent /deep/ .el-table th {
  background: rgb(243, 243, 243);
  height: 35px;
  border-radius: 5px;
  padding: 0;
}
.rightContentCard {
  margin-bottom: 20px;
  /* justify-content:space-between */
}

.instanceCard /deep/ .el-card:hover {
  background-color: #f2f6fc;
}
</style>

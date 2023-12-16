<template>
  <div class="mainContent">
    <div style="position: relative">
      <h1 class="title" :title="this.project.name" v-if="this.project.name">
        {{ this.project.name }}
      </h1>
      <h1 class="title" :title="this.project.name" v-else>unnamed</h1>
      <el-button
        @click="folkVisible = true"
        style="position: absolute; right: 65px; top: 0px"
        type="info"
        circle
        ><el-icon><Download /></el-icon
      ></el-button>
    </div>
    <el-row>
      <el-col :span="22" :offset="1">
        <el-row class="contentCard">
          <el-card class="container">
            <el-collapse v-model="activeNamesRight">
              <el-collapse-item title="Resource Collection" name="2">
                <el-tabs v-model="activeResource">
                  <el-tab-pane label="Data Services" name="data">
                    <el-table
                      :data="dataTable"
                      style="width: 100%; font-size: 18px"
                      max-height="400"
                    >
                      <el-table-column
                        prop="name"
                        label="Name"
                        width="300"
                      ></el-table-column>
                      <el-table-column
                        prop="suffix"
                        label="suffix"
                      ></el-table-column>
                      <el-table-column
                        fixed="right"
                        label="operation"
                        width="200"
                      >
                        <template v-slot="scope">
                          <el-button
                            @click.prevent="download(scope.row)"
                            type="text"
                            ><el-icon><Download /></el-icon>Download</el-button
                          >
                          <!-- <el-icon><Download /></el-icon> -->
                        </template>
                        <!-- {{ url }} -->
                      </el-table-column>
                    </el-table>
                  </el-tab-pane>

                  <el-tab-pane label="Model Services" name="model">
                    <el-table
                      :data="modelTable"
                      style="width: 100%; font-size: 18px"
                      max-height="400"
                    >
                      <el-table-column
                        prop="name"
                        label="Name"
                        width="300"
                      ></el-table-column>
                      <el-table-column
                        prop="createTime"
                        label="Invoke Time"
                        width="350"
                      ></el-table-column>
                      <el-table-column
                        prop="description"
                        label="Description"
                      ></el-table-column>
                      <el-table-column
                        fixed="right"
                        label="operation"
                        width="200"
                      >
                        <template v-slot="scope">
                          <el-button
                            @click.prevent="view(scope.row, 'model')"
                            type="text"
                            >view</el-button
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

        <el-row class="contentCard">
          <el-card class="container">
            <el-tabs v-model="activeExpected" class="instanceCard">
              <el-tab-pane
                label="Service Instances"
                name="instance"
                style="
                  display: flex;
                  flex-wrap: wrap;
                  justify-content: space-around;
                "
              >
                <el-col
                  :span="7"
                  :offset="0"
                  v-for="(instance, index) in instanceList"
                  :key="index"
                  v-show="!instance.isReproduced"
                >
                  <InstanceCard
                    :cardData="instance"
                    @click="chosenInstance(instance)"
                  ></InstanceCard>
                </el-col>
              </el-tab-pane>
            </el-tabs>
          </el-card>
        </el-row>
        <el-row class="contentCard">
          <el-card class="container">
            <h1>Simulation Dataflow</h1>
            <mxgraph
              id="fullScreenComponent"
              :expectedInstances="instanceList"
            ></mxgraph>
          </el-card>
        </el-row>
      </el-col>
    </el-row>
  </div>
</template>

<script>
// import workflow from "./components/workflow";
import mxgraph from "_com/MxGraph/MxGraph.vue";
import { post } from "@/axios";
import InstanceCard from "_com/common/InstanceCard.vue";
import { mapState } from "vuex";
// import config from "@/config";
import {
  getProjectById,
  getScenarioById,
  getInstancesByIds,
  getUserInfoByUserId,
  getModelById,
} from "@/api/request";
export default {
  computed: {
    ...mapState(["user", "permission"]),
  },
  data() {
    return {
      project: [],
      userInfo: [],
      ExpectedInstances: [],
      instanceTest: [],
      id: this.$route.params.id,
      folkVisible: false,
      activeNamesLeft: ["1", "2"],
      activeNamesRight: ["1", "2"],
      activeResource: "data",
      activeContext: "theme",
      activeExpected: "instance",
      fullscreenFlag: false,
      url: "",
      dataTable: [],
      modelTable: [],
    };
  },
  computed: {
    instanceList() {
      return this.ExpectedInstances.map((item) => {
        let stateEnum =
          status === "2" ? "success" : status === "-1" ? "failed" : "running";

        let form = {
          createTime: item.createTime,
          executorName: item.executorName,
          id: item.id,
          instanceEnum: "MODEL",
          isReproduced: item.isReproduced,
          name: item.name,
          behavior: item.behavior,
          stateEnum: stateEnum,
          updateTime: item.updateTime,
          modelId: item.modelId,
          executorId: item.executorId,
          modelDescription: item.modelDescription,
        };
        return form;
      });
    },
  },
  methods: {
    // 这个方法叫什么不重要，只需要知道所有的数据，这个页面和子页面你需要的所有原始数据都在这里，就够了
    async getExpectedInstances() {
      // 先拿到对应的工程 所包含的场景id
      this.project = await getProjectById(this.id);

      let scenario = await getScenarioById(this.project.scenario);
      this.ExpectedInstances = scenario.instanceObjectList;
      this.dataTable = scenario.resourceCollectionObjects.dataList;
      this.modelTable = scenario.resourceCollectionObjects.modelList;
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
    chosenInstance(instance) {
      this.$emit("chosenInstance", instance);
    },
  },

  async mounted() {
    this.getExpectedInstances();
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
  height: 100%;
}
.container {
  width: 100%;
  /* background-color: #666; */
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
  font-size: 12px;
  line-height: 15px;
}
.info-tag {
  display: flex;
  width: 120px;
  align-items: center;
}
/* .rightContent {
  flex: 1;
} */
/* .leftContent :deep(.el-collapse-item__header) {
  height: 40px;
  padding-left: 5px;
  font-weight: 600;
}
.rightContent :deep(.el-collapse-item__header) {
  background-color: rgb(243, 243, 243);
  border-radius: 5px;
  border: 1px solid rgb(222, 222, 222);
  padding-left: 20px;
  height: 40px;
  font-weight: 600;
  font-size: 16px;
}
.rightContent :deep(.el-collapse-item__wrap) {
  margin: 20px 20px;
  border-bottom: 0;
  line-height: 0;
}
.rightContent :deep(.el-tabs__item) {
  height: 35px;
}
.rightContent :deep(.el-table th) {
  background: rgb(243, 243, 243);
  height: 35px;
  border-radius: 5px;
  padding: 0;
} */
.contentCard {
  margin-bottom: 20px;
}
.instanceCard :deep(.el-card:hover) {
  background-color: #f2f6fc;
}
</style>

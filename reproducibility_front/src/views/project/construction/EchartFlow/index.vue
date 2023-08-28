<template>
  <div>
    <div
      style="width:100%;margin-top:20px;padding:15px;background-color:#f8f8f9"
      :style="{ height: contentHeight - 140 + 'px' }"
      ref="steps"
    ></div>

    <el-dialog
      title="Public resource selection"
      :visible.sync="resourceCollectVisible"
      :close-on-click-modal="false"
      width="60%"
    >
      <el-row>
        <el-col :span="10">
          <el-input
            placeholder="Search by name"
            v-model="keyword"
            class="input-with-select"
            size="small"
          >
            <el-button
              slot="append"
              icon="el-icon-search"
              @click="searchModelByName"
              v-if="!isSearching"
            ></el-button>
            <el-button
              slot="append"
              icon="el-icon-close"
              @click="clearSearch"
              v-else
            ></el-button>
          </el-input>
        </el-col>
      </el-row>

      <div class="transfer">
        <el-transfer
          class="mainContainer"
          v-model="selectedModelsWithId"
          @change="
            (value) => {
              selectChange(value, 'model');
            }
          "
          :titles="['Public models', 'Selected models']"
          :data="currentModelList"
        >
        </el-transfer>
        <el-pagination
          layout="prev, pager, next"
          :total="total"
          small
          :pager-count="5"
          :page-size="20"
          :current-page="1"
          @current-change="currentModelChange"
          class="pageClass"
          v-show="!isSearching"
        ></el-pagination>
        <div class="dialog-footer">
          <el-button @click="resourceCollectVisible = false" size="mini"
            >Cancle</el-button
          >
          <el-button type="primary" @click="setResourceCollect()" size="mini"
            >Submit</el-button
          >
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import config from "@/config";
import echarts from "echarts";
import { mapState } from "vuex";
import {
  getProjectById,
  getModelsByPrivacy,
  updateProjectWorkspace,
  getPublicModelListByIgnoreName,
} from "@/api/request";
export default {
  data() {
    return {
      projectId: this.$route.params.id, //projectId
      project: {},
      resourceCollectVisible: false,
      modelList: [],

      chart: {},
      contentHeight: window.innerHeight - 200,

      ops: {
        bar: {
          background: "#808695",
          keepShow: true,
        },
      },
      selectedModelsWithId: [],
      currentModelList: [],
      pageFilter: {
        pageSize: 8,
        page: 0,
      },
      total: 0,
      keyword: "",
      isSearching: false,
    };
  },
  computed: {
    ...mapState(["user"]),
  },
  methods: {
    // transfer----------
    async currentModelChange(val) {
      await this.getPublicModels(val - 1);
    },
    async getPublicModels(val) {
      let data = await getModelsByPrivacy("public", val, 20);

      this.total = data.totalElements;
      this.modelList = data.content;

      var currentModelList = this.modelList.map((value) => {
        return {
          label: value.name,
          key: value.id,
          obj: value,
        };
      });
      // 初始化数据
      this.currentModelList = currentModelList;
    },

    // 穿梭搜索
    async searchModelByName() {
      let data = await getPublicModelListByIgnoreName(this.keyword);
      this.modelList = data;
      var currentModelList = this.modelList.map((value) => {
        return {
          label: value.name,
          key: value.id,
          obj: value,
        };
      });
      // 初始化数据
      this.currentModelList = currentModelList;
      this.isSearching = true;
    },
    clearSearch() {
      this.keyword = "";
      this.init();
      this.isSearching = false;
    },

    async filterQueryModel(text, val) {
      let data = await getPublicModelListByIgnoreName(text, val, 20);
      console.log(data);
      this.total = data.totalElements;
      this.modelList = data.content;

      var currentModelList = this.modelList.map((value) => {
        return {
          label: value.name,
          key: value.id,
          obj: value,
        };
      });
      // 初始化数据
      this.currentModelList = currentModelList;
    },

    init() {
      this.getPublicModels(0);
    },

    selectChange(value, type) {
      if (type == "model") {
        this.modelList = value;
      }
    },

    view({ id }) {
      window.open(`${config.tomcatURL}/evaluation/#/gist/${id}`, "_blank");
    },
    edit({ id }) {
      window.open(
        `${config.tomcatURL}/evaluation/#/gist/${id}?name=${this.user.name}&token=${this.user.token}`,
        "_blank"
      );
    },

    initEchart() {
      let option = {
        animationDurationUpdate: 500,
        animationEasingUpdate: "quinticInOut",
        legend: {
          show: true,
          data: [
            {
              name: "Resource collection",
              icon: "circle",
            },
            {
              name: "Computational process construction",
              icon: "circle",
            },
          ],
        },
        series: [
          {
            type: "graph",
            layout: "none",
            legendHoverLink: true,
            roam: this.procedureDrag,
            label: {
              normal: {
                show: true,
              },
            },
            edgeSymbol: ["circle", "arrow"],
            edgeSymbolSize: [4, 10],
            focusNodeAdjacency: true,
            data: [
              {
                name: "Resource collection",
                category: "Resource collection",
                label: "资源收集",
                symbolSize: 45,
                x: 700,
                y: 500,
              },
              {
                name: "Computational process construction",
                category: "Computational process construction",
                label: "模拟计算",
                symbolSize: 45,
                x: 800,
                y: 500,
              },
            ],
            categories: [
              {
                name: "Resource collection",
              },
              {
                name: "Computational process construction",
              },
            ],
            links: [
              {
                source: "Resource collection",
                target: "Computational process construction",
              },
              {
                source: "Computational process construction",
                target: "Simulation  evaluation",
              },
            ],
            lineStyle: {
              normal: {
                opacity: 1,
                width: 5,
                curveness: 0.1,
              },
            },
          },
        ],
      };
      this.chart = echarts.init(this.$refs.steps);
      this.chart.setOption(option);
      this.chart.on("dblclick", ({ data }) => {
        let type = data.name;
        switch (type) {
          case "Resource collection":
            {
              this.resourceCollectVisible = true;
            }
            break;
          case "Computational process construction":
            {
              this.$emit("getProcessesPageFlag", true);
              // let routeUrl = this.$router.resolve({
              //   path: `/project/${this.projectId}/construction/compute`,
              // });
              // window.open(routeUrl.href, "_blank");
            }
            break;
        }
      });
    },

    async setResourceCollect() {
      let data = await updateProjectWorkspace(
        this.projectId,
        "modelList",
        this.selectedModelsWithId
      );
      this.project = data;
      this.resourceCollectVisible = false;
    },
  },
  async mounted() {
    this.project = await getProjectById(this.$route.params.id);
    // console.log(this.project);
    if (
      this.project.workspace != null &&
      this.project.workspace.modelList != null
    ) {
      this.selectedModelsWithId = this.project.workspace.modelList;
    }
    this.init();
    this.initEchart();
    // this.getData();
  },
};
</script>

<style scoped>
.time {
  font-size: 13px;
  color: #999;
}

.bottom {
  margin-top: 13px;
  line-height: 12px;
}

.button {
  padding: 0;
  float: right;
}

.image {
  width: 100%;
  display: block;
}

.clearfix:before,
.clearfix:after {
  display: table;
  content: "";
}

.clearfix:after {
  clear: both;
}
>>> .el-dialog__body {
  padding: 5px 20px 40px 20px;
  margin: 20px;
}
.mainContainer >>> .el-transfer-panel {
  width: 300px;
}
.mainContainer >>> .el-transfer-panel__body {
  height: 480px;
}
.mainContainer >>> .el-transfer-panel__list {
  height: 450px;
}
.mainContainer >>> .el-transfer-panel__list.is-filterable {
  height: 400px;
}
.mainContainer >>> .el-transfer__buttons .el-button {
  display: block;
  margin-left: 0;
}
.dialog-footer {
  float: right;

  /* margin-bottom: 10px; */
}

.pageClass {
  position: relative;
  bottom: 30px;
  left: 50px;
}
</style>

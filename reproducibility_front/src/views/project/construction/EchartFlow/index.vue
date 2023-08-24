<template>
  <div>
    <div
      style="width:100%;margin-top:20px;padding:15px;background-color:#f8f8f9"
      :style="{ height: contentHeight - 140 + 'px' }"
      ref="steps"
    ></div>

    <el-dialog
      title="Resource selection"
      :visible.sync="resourceCollectVisible"
      width="60%"
    >
      <div class="main-container">
        <template>
          <h4>Data services</h4>

          <el-transfer
            v-model="resourceCollectWithId.dataServices"
            :data="allDataServices"
            :titles="['All', 'Selected']"
            :filter-method="filterMethod"
            filterable
            @change="
              (value) => {
                selectChange(value, 'data');
              }
            "
          ></el-transfer>

          <el-divider></el-divider>
          <h4>Model services</h4>
          <el-transfer
            v-model="resourceCollectWithId.modelServices"
            :data="allModelServices"
            :titles="['All', 'Selected']"
            :filter-method="filterMethod"
            filterable
            @change="
              (value) => {
                selectChange(value, 'model');
              }
            "
          ></el-transfer>
        </template>
      </div>

      <div slot="footer" class="dialog-footer">
        <el-button @click="resourceCollectVisible = false">Cancle</el-button>
        <el-button type="primary" @click="setResourceCollect()"
          >Submit</el-button
        >
      </div>
    </el-dialog>
  </div>
</template>

<script>
import config from "@/config";
import { get, put } from "@/axios";
import echarts from "echarts";
import { mapState } from "vuex";
import { getProjectById } from "@/api/request";
export default {
  data() {
    return {
      id: this.$route.params.id,
      project: {},
      active: 0,
      avtiveTab: "manage",
      resourceCollect: {},
      resourceCollectVisible: false,
      evaluationVisible: false,
      evaluationService: {},
      modelServices: [],
      contextDefine: {
        theme: {
          name: null,
        },
        object: {
          name: null,
        },
        boundary: {
          name: null,
        },
      },
      contextVisible: false,
      typeList: [
        // "Context definition",
        // "Geographic simulation construction",
        "Resource collection",
        "Computational process construction",
        // "Simulation  evaluation"
      ],
      chart: {},
      contentHeight: window.innerHeight - 200,
      dataServices: [],
      choosenDataServices: [],
      ops: {
        bar: {
          background: "#808695",
          keepShow: true,
        },
      },
      allDataServices: [],
      allModelServices: [],
      selectedDataServices: [],
      selectedModelServices: [],
      resourceCollectWithId: {
        modelServices: [],
        dataServices: [],
      },
    };
  },
  computed: {
    ...mapState(["user"]),
  },
  methods: {
    filterMethod(query, item) {
      return item.label.indexOf(query) > -1;
    },
    selectChange(value, type) {
      if (type == "data") {
        this.resourceCollect.dataServices = value;
      } else {
        this.resourceCollect.modelServices = value;
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
              let routeUrl = this.$router.resolve({
                path: `/project/${this.id}/compute`,
              });
              window.open(routeUrl.href, "_blank");
            }
            break;
        }
      });
    },

    async getModelServices() {
      this.resourceCollectWithId.modelServices = this.resourceCollect.modelServices;

      try {
        let { data } = await get("/model_service/all", null, null, false);

        data.forEach((item) => {
          item.label = item.name;
          item.key = item.id;

          if (this.resourceCollect.modelServices == null) {
            item.isSelected = false;
          } else {
            this.resourceCollect.modelServices.forEach((select) => {
              if (select.id == item.id) {
                item.isSelected = true;
              } else {
                item.isSelected = false;
              }
            });
          }
        });
        this.allModelServices = data;

        // console.log(test, "test");
      } catch (error) {
        this.$throw(error);
      }
    },

    async getDataServices() {
      //Todo 后面直接g2s就应该包含这些信息
      let dataServices = await get("/project/{id}/dataServices", null, {
        id: this.id,
      });

      if (dataServices != null) {
        if (dataServices.originalDataServices != null) {
          dataServices.originalDataServices.forEach((el) => {
            this.dataServices.push({
              key: el.id,
              label: el.name,
              id: el.id,
              description: el.description,
              isIntermediate: false,
            });
          });
        }

        if (dataServices.intermediateDataServices != null) {
          dataServices.intermediateDataServices.forEach((el) => {
            this.dataServices.push({
              key: el.id,
              label: el.name,
              id: el.id,
              isIntermediate: true,
            });
          });
        }
      }

      this.resourceCollect.dataServices = this.dataServices;

      this.resourceCollectWithId.dataServices = this.dataServices.map(
        (item) => item.key
      );

      let { data } = await get("/data_service/all", null, null, false);
      this.allDataServices = data.filter((item) => {
        item.label = item.name;
        item.key = item.id;

        this.resourceCollect.dataServices.forEach((select) => {
          if (select.id == item.id) {
            item.isSelected = true;
          } else {
            item.isSelected = false;
          }
        });
        return item.isIntermediate == false;
      });
    },

    async publish() {
      this.project = await put(
        "/project/{id}",
        {
          isPublish: this.project.isPublish,
        },
        {
          id: this.id,
        }
      );
    },

    async update() {
      this.project = await put(
        "/project/{id}",
        {
          resourceCollect: this.resourceCollectWithId,
        },
        {
          id: this.id,
        }
      );
    },

    setResourceCollect() {
      this.update();
      this.resourceCollectVisible = false;
    },
  },
  async mounted() {
    this.project = await getProjectById(this.$route.params.id);
    if (this.project.contextDefine !== null) {
      this.contextDefine = this.project.contextDefine;
    }
    if (this.project.resourceCollect != null) {
      this.resourceCollect = this.project.resourceCollect;
    }
    this.initEchart();

    this.getDataServices();
    // this.getData();
    this.getModelServices();
  },
};
</script>

<style>
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
</style>

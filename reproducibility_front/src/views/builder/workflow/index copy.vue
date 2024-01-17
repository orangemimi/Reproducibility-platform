<template>
  <div class="mainContent">
    <el-row>
      <el-col :span="22" :offset="1">
        <el-row class="contentCard" v-if="invokingType != 'reproduction'">
          <el-card class="container">
            <template #header>
              <div class="card-header">
                <span>Resource collection</span>
              </div>
            </template>
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
                  <el-table-column fixed="right" label="operation" width="200">
                    <template v-slot="scope">
                      <el-button
                        @click.prevent="download(scope.row)"
                        type="text"
                        ><el-icon><Download /></el-icon>Download</el-button
                      >
                    </template>
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
                  <el-table-column fixed="right" label="operation" width="200">
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
          </el-card>
        </el-row>

        <el-row class="contentCard">
          <el-card class="container">
            <template #header>
              <div class="card-header">
                <span>Model instances</span>
              </div>
            </template>
            <el-row>
              <el-col
                :span="7"
                :offset="1"
                v-for="(instance, index) in instanceList"
                :key="index"
              >
                <InstanceCard
                  :cardData="instance"
                  @click="chosenInstance(instance)"
                ></InstanceCard>
              </el-col>
            </el-row>
          </el-card>
        </el-row>
        <el-row class="contentCard">
          <el-card class="container">
            <template #header>
              <div class="card-header">
                <span>Simulation workflow</span>
              </div>
            </template>
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
import mxgraph from "_com/MxGraph/mxGraph.vue";
import InstanceCard from "_com/common/InstanceCard.vue";
import { mapState } from "vuex";
export default {
  computed: {
    ...mapState(["user", "permission"]),
    instanceList() {
      if (
        this.expectedInstances == null ||
        this.expectedInstances == undefined
      ) {
        return;
      }
      return this.expectedInstances.map((item) => {
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
  props: {
    currentScenario: {
      type: Object,
    },
    reproducedScenario: {
      type: Object,
    },
    invokingType: {
      type: String,
    },
  },
  watch: {
    currentScenario: {
      handler(newVal) {
        if (Object.hasOwnProperty.call(newVal, "name")) {
          this.expectedInstances = newVal.instanceObjectList;
          this.dataTable = newVal.resourceCollectionObjects.dataList;
          this.modelTable = newVal.resourceCollectionObjects.modelList;
        }
      },
      deep: true,
      immediate: true,
    },
  },

  data() {
    return {
      expectedInstances: [],
      activeNamesRight: ["1", "2"],
      activeResource: "data",
      activeContext: "theme",
      activeExpected: "instance",
      fullscreenFlag: false,

      dataTable: [],
      modelTable: [],
    };
  },

  methods: {
    download(row) {
      let urls = row.value;
      // 创建一个链接元素
      const link = document.createElement("a");
      link.href = urls;
      link.target = "_blank"; // 在新窗口中打开链接
      link.download = "downloaded_file"; // 设置下载的文件名
      // 模拟点击链接，触发下载
      link.click();
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

  async mounted() {},
  components: {
    // workflow,
    InstanceCard,
    mxgraph,
  },
};
</script>

<style lang="scss" scoped>
.mainContent {
  height: 100%;
  .contentCard {
    margin-bottom: 20px;
    width: 100%;
    .container {
      width: 100%;

      :deep(.el-card__header) {
        background: #656565;
        height: 30px;
        padding: 0;
        color: #ffffff;
        font-weight: 600;
      }

      // margin-bottom: 10px;
    }
  }
}

.instanceCard :deep(.el-card:hover) {
  background-color: #f2f6fc;
}
</style>

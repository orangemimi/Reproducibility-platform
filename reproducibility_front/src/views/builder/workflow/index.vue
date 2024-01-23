<template>
  <div class="mainContent">
    <el-row>
      <el-col :span="24">
        <el-row class="contentCard" v-if="invokingType == 'initial_info'">
          <el-card class="container">
            <template #header>
              <div class="card-header">
                <span>Resource collection</span>
              </div>
            </template>
            <resource-collection
              :resourceCollectionObjects="resourceCollectionObjects"
            ></resource-collection>
          </el-card>
        </el-row>

        <el-row class="contentCard">
          <el-card
            :class="invokingType == 'reproduction' ? 'container' : 'container2'"
            shadow="never"
          >
            <template #header>
              <div class="card-header">
                <span>Model instances</span>
              </div>
            </template>
            <el-row>
              <el-col
                :span="
                  invokingType == 'construction'
                    ? 6
                    : invokingType == 'initial_info'
                    ? 6
                    : 12
                "
                v-for="(instance, index) in expectedInstances"
                :key="index"
              >
                <InstanceCard
                  :stepIndex="index"
                  :cardData="instance"
                  @click="chosenInstance(instance)"
                  :invokingType="invokingType"
                ></InstanceCard>
              </el-col>
            </el-row>
          </el-card>
        </el-row>
        <el-row class="contentCard">
          <el-card
            :class="invokingType == 'reproduction' ? 'container' : 'container2'"
            shadow="never"
          >
            <template #header>
              <div class="card-header">
                <span>Simulation workflow</span>
              </div>
            </template>
            <mxgraph
              id="fullScreenComponent"
              :expectedInstances="expectedInstances"
            ></mxgraph>
          </el-card>
        </el-row>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import mxgraph from "_com/MxGraph/mxGraph.vue";
import resourceCollection from "./components/ResourceCollection.vue";
import InstanceCard from "_com/common/InstanceCard.vue";
import ResourceCollection from "./components/ResourceCollection.vue";
import { mapState } from "vuex";
export default {
  components: {
    // workflow,
    InstanceCard,
    mxgraph,
    ResourceCollection,
  },
  computed: {
    ...mapState(["user", "permission"]),
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
        if (newVal && Object.hasOwnProperty.call(newVal, "name")) {
          if (
            newVal.instanceObjectList != undefined &&
            newVal.instanceObjectList.length != 0
          ) {
            newVal.instanceObjectList.filter((instance) => {
              let stateEnum =
                instance.status === "2"
                  ? "success"
                  : instance.status === "-1"
                  ? "failed"
                  : "running";
              instance.stateEnum = stateEnum;
              instance.instanceEnum = "MODEL";
            });
          }

          this.expectedInstances = newVal.instanceObjectList;
          this.resourceCollectionObjects = newVal.resourceCollectionObjects;
        }
      },
      deep: true,
      immediate: true,
    },
  },

  data() {
    return {
      expectedInstances: [],
      resourceCollectionObjects: {},
    };
  },

  methods: {
    chosenInstance(instance) {
      this.$emit("chosenInstance", instance);
    },
  },

  async mounted() {},
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
        // background: #656565;
        background: #8d8251;
        height: 30px;
        padding: 0;
        color: #ffffff;
        font-weight: 600;
      }
      :deep(.el-card__body) {
        padding: 10px;
      }
    }
    .container2 {
      width: 100%;

      :deep(.el-card__header) {
        background: #294978;
        height: 30px;
        padding: 0;
        color: #ffffff;
        font-weight: 600;
      }
      :deep(.el-card__body) {
        padding: 10px;
      }
    }
  }
}

.instanceCard :deep(.el-card:hover) {
  background-color: #f2f6fc;
}
</style>

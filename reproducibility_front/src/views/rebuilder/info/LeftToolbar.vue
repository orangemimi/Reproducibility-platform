<template>
  <div class="mainLeft">
    <el-row style="margin: 10px 0">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item>Scenarios</el-breadcrumb-item>
        <el-breadcrumb-item> {{ chosenScenario.name }}</el-breadcrumb-item>
      </el-breadcrumb>
    </el-row>
    <el-row>
      <div
        v-for="(item, key) in allScenarioList"
        :key="key"
        @click="chooseScenario(item)"
      >
        <scenario-card :secnarioForm="item"></scenario-card>
      </div>
    </el-row>
  </div>
</template>

<script>
// 场景选择
import scenarioCard from "_com/Cards/ScenarioListCard.vue";
import { getProjectById } from "@/api/request";
export default {
  data() {
    return {
      allScenarioList: [],
      chosenScenario: {},
      currentModel: {},
    };
  },
  components: {
    scenarioCard,
    // SelectedScenario
  },
  watch: {},
  computed: {},
  methods: {
    async init() {
      let data = await getProjectById(this.$route.params.id);
      this.allScenarioList = data.scenarioObjectList;
      this.chosenScenario = this.allScenarioList[0];
    },

    async chooseScenario(item) {
      this.chosenScenario = item;
      this.$emit("currentScenario", item);
    },
  },
  mounted() {
    this.init();
  },
  emits: ["currentScenario"],
};
</script>

<style lang="scss" scoped>
.mainLeft {
  width: 320px;
  float: left; /*//   padding: 5px 10px;*/
  // .scenario {
  //   // background-color: #6060601c;
  //   // width: 300px;
  // }

  .scenarioCard {
    margin-left: 10px;

    .el-card__body {
      min-height: calc(90vh);
    }
  }
  .createScenario {
    overflow: auto;
    :deep(.el-dialog__body) {
      height: 100%;
      overflow: auto;
    }
  }
  .selectResource {
    .el-dialog__body {
      height: 800px;
      overflow: auto;
    }
  }
}
</style>

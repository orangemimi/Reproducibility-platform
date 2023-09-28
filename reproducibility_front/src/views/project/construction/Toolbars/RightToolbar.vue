<!--  -->
<template>
  <div class="main">
    <div class="scenarioCard">
      <el-card>
        <el-row>
          <model-content
            :currentModel="currentModel"
            :scenarioId="scenarioId"
          ></model-content>
        </el-row>
      </el-card>
    </div>

    <!-- </el-row> -->
  </div>
</template>

<script>
import modelContent from "./ModelContent";
// import SelectedScenario from "_com/Cards/SelectedScenario.vue";
export default {
  components: {
    modelContent,
    // SelectedScenario
  },

  props: {
    currentModel: {
      type: Object,
    },
    scenarioId: {
      type: String,
    },
  },

  watch: {},

  computed: {},

  data() {
    return {};
  },

  methods: {},

  mounted() {},
};
</script>
<style lang="scss">
.main {
  width: 100%;
  padding: 5px 10px;
  /* min-height: calc(100vh - 240px); */

  //   .scenario {
  //     background-color: #3067d61c;
  //   }

  .scenarioCard {
    margin-left: 10px;

    .el-card__body {
      min-height: calc(100vh - 240px);
    }
  }
  .createScenario {
    .el-dialog__body {
      height: 1200px;
      overflow: auto;
    }
  }
}
</style>

<template>
  <div class="main">
    <div class="scenarioCard">
      <el-card>
          <model-content
            :currentModel="currentModel"
            :scenarioId="scenarioId"
            :codingOlShow="codingOlShow"
          ></model-content>
      </el-card>
    </div>

  </div>
</template>

<script>
import modelContent from './ModelContent.vue'
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
    codingOlShow: {
      type: Boolean,
    },
  },

  watch: {},

  computed: {},

  data() {
    return {}
  },

  methods: {},

  mounted() {
    // console.log(this.currentModel,this.scenarioId,this.codingOlShow,'101');
    // setInterval(() => {
    //   console.log(this.currentModel,this.scenarioId,this.codingOlShow,'100');
    // }, 5000);
  },
}
</script>

<style lang="scss">
.main {
  width: 100%;
  height: 100%;
  padding: 5px 10px; /*//   .scenario {
*/
  //     background-color: #3067d61c;
  //   }

  .scenarioCard {
    margin-left: 10px;
    width: 100%;
    height: 100%;
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

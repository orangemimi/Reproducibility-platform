<template>
  <div class="main">
    <div>
      <LeftToolbar
        @selectModel="selectModel"
        @codingOl="codingOl"
      ></LeftToolbar>
    </div>

    <div>
      <RightToolbar
        :currentModel="currentModel"
        :scenarioId="scenarioId"
        :codingOlShow="codingOlShow"
      ></RightToolbar>
    </div>
  </div>
</template>

<script>
import LeftToolbar from "./Toolbars/LeftToolbar.vue";
import RightToolbar from "./Toolbars/RightToolbar.vue";
// import SelectedScenario from "_com/Cards/SelectedScenario.vue";
export default {
  components: {
    LeftToolbar,
    RightToolbar,
  },
  watch: {},

  computed: {},
  data() {
    return {
      currentModel: {},
      scenarioId: "",
      codingOlShow: false,
    };
  },
  methods: {
    selectModel(val, scenarioId) {
      // console.log("left information", val, scenarioId);
      if (val.type == "code") {
        this.codingOl(true, scenarioId, val);
      } else {
        this.currentModel = val;
        this.scenarioId = scenarioId;
        this.codingOlShow = false;
      }
    },
    // 添加codingOl组件所需要的scenarioId
    codingOl(state, scenarioId, currentModel) {
      // console.log("right", state, scenarioId, currentModel);
      this.scenarioId = scenarioId;
      this.codingOlShow = state;
      if (currentModel) {
        this.currentModel = currentModel;
      } else {
        this.currentModel = {};
      }
    },
  },
  mounted() {},
};
</script>

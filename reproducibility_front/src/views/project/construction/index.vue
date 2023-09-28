<!--  -->
<template>
  <div class="main">
    <div>
      <LeftToolbar @selectModel="selectModel"></LeftToolbar>
    </div>

    <div>
      <RightToolbar  :currentModel="currentModel"
            :scenarioId="scenarioId"></RightToolbar>
    </div>

    <!-- </el-row> -->
  </div>
</template>

<script>
import LeftToolbar from "./Toolbars/LeftToolbar";
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
    return { currentModel: {},scenarioId:'' };
  },
  methods: {
    selectModel(val,scenarioId) {
      this.currentModel = val;
      this.scenarioId=scenarioId
    },
  },
};
</script>

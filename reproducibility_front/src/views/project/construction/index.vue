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
import LeftToolbar from './Toolbars/LeftToolbar.vue'
import RightToolbar from './Toolbars/RightToolbar.vue'
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
      scenarioId: '',
      codingOlShow: false,
    }
  },
  methods: {
    selectModel(val, scenarioId) {
      console.log('left',val,scenarioId);
      this.currentModel = val
      this.scenarioId = scenarioId
      this.codingOlShow = false
    },
    // 添加codingOl组件所需要的scenarioId
    codingOl(val, scenarioId) {
      console.log('right',val,scenarioId);
      this.scenarioId = scenarioId
      this.codingOlShow = val
      this.currentModel = {}
    },
  },
  mounted(){
  },
}
</script>

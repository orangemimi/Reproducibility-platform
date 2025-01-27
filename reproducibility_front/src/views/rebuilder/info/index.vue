<template>
  <div class="main">
    <br />

    <LeftToolbar @currentScenario="CurrentScenario"></LeftToolbar>

    <el-col :span="24">
      <el-card shadow="never">
        <RightToolbar :currentScenario="currentScenario"></RightToolbar>
      </el-card>
    </el-col>
  </div>
</template>

<script>
import LeftToolbar from "./LeftToolbar.vue";
import RightToolbar from "./RightToolbar.vue";
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
      currentScenario: {},
    };
  },
  methods: {
    CurrentScenario(val) {
      this.currentScenario = val;

      console.log("left", val);
      // debugger;
    },
  },
  mounted() {
    console.log(198156);
  },
};
</script>

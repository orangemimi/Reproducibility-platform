<!--  -->
<template>
  <div class="main">
    <!-- <el-row :gutter="20"> -->

    <el-col :span="5" class="scenario">
      <LeftToolbar></LeftToolbar>
    </el-col>

    <el-col :span="19">
      <RightToolbar></RightToolbar>
    </el-col>

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
};
</script>

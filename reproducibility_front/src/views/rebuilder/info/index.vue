<!--  -->
<template>
  <div>
    <el-tabs v-model="activeName" type="card" @tab-click="handleClick">
      <el-tab-pane label="Info" name="info">
        {{ reproduceShow }}
        <workflow @chosenInstance="chosenInstance"></workflow>
      </el-tab-pane>

      <el-tab-pane label="Reproduction" name="reproduce" v-if="reproduceShow">
        <el-row>
          <el-col :span="11" :offset="1">
            <model-content :currentModel="selectedInstance"></model-content>
          </el-col>
          <el-col :span="11" :offset="1">
            <model-content :currentModel="selectedInstance"></model-content>
          </el-col>
          <!-- <el-col :span="12">
            <model-content :currentModel="currentModel"></model-content>
          </el-col> -->
        </el-row>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import workflow from "@/views/builder/workflow/index.vue";
import modelContent from "../ModelContent/index.vue";
export default {
  components: {
    workflow,
    modelContent,
    // SelectedScenario
  },

  watch: {},

  computed: {},

  data() {
    return {
      activeName: "info",
      reproduceShow: false,
      selectedInstance: {},
    };
  },

  methods: {
    chosenInstance(selected) {
      this.selectedInstance = selected;
      console.log(this.selectedInstance, "selectedInstance");
      this.reproduceShow = true;
      this.activeName = "reproduce";
    },
  },

  mounted() {},
};
</script>
<style lang="scss" scoped></style>

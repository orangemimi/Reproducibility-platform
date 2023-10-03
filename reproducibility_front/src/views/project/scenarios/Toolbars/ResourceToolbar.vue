<!--  -->
<template>
  <div>
    <el-row style="float:left;margin:5px 10px  10px 0;">
      <el-tooltip
        :content="'Select ' + activeNames + 'to execute'"
        placement="top"
      >
        <el-switch
          v-model="activeNames"
          active-color="#13ce66"
          inactive-color="#ff4949"
          active-value="Models"
          inactive-value="Data"
          style="float:left;margin-right:10px"
        >
        </el-switch>
      </el-tooltip>
      <div style="float:left;margin-right:10px">{{ activeNames }}</div>
    </el-row>
    <el-input
      placeholder="Search model/tool"
      size="mini"
      class="search_input"
      v-show="modelList.length > 20"
    >
      <el-button slot="append" icon="el-icon-search"></el-button>
    </el-input>
    <div v-if="modelList != null">
      <div v-for="(item, index) in modelList" :key="index">
        <div @click="clickModel(item)">
          <model-card :modelFrom="item"></model-card>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import ModelCard from "_com/Cards/ModelCard";
export default {
  props: {
    modelList: {
      type: Array,
    },
  },
  components: {
    ModelCard,
    // modelToolbar
  },

  watch: {},

  computed: {},

  data() {
    return {
      activeNames: "Models",
      //mxgraph scrollbar
      // ops: {
      //   bar: {
      //     onlyShowBarOnScroll: false,
      //     keepShow: true,
      //     background: "#c1c1c1",
      //     opacity: 1,
      //     hoverStyle: false,
      //     specifyBorderRadius: false,
      //     minSize: 0,
      //     size: "6px",
      //     disable: false,
      //   },
      // },
    };
  },

  methods: {
    clickModel(val) {
      val.isCurrent = true;
      // console.log(val);
      this.$emit("selectModel", val);
    },
  },

  mounted() {},
};
</script>
<style lang="scss" scoped></style>

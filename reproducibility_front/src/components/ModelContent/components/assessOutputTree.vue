<!--  -->
<template>
  <div>
    <!-- {{ treeEvent }} -->
    <el-select
      v-model="selectVall"
      class="m-2"
      placeholder="Select"
      @change="handleNodeClick"
    >
      <el-option
        v-for="item in filterOutputs(dataFolderList)"
        :key="item.id"
        :label="item.datasetItem.dataName"
        :value="item.datasetItem.value"
      />
    </el-select>
  </div>
</template>

<script>
export default {
  props: {
    treeEvent: {
      type: Object,
    },
    dataFolderList: {
      type: Array,
    },
  },
  components: {},

  watch: {
    dataFolderList: {
      handler(val) {
        if (val != undefined && val.length > 0) {
          // return this.getOutputs(val);
        }
      },
      deep: true,
    },
  },

  computed: {},

  data() {
    return {
      selectVal: {},
      selectName: "",
      dataList: [],
      reproducedOutcomes: [],
    };
  },

  methods: {
    filterOutputs(instances) {
      if (
        instances.length == 0 ||
        instances == undefined ||
        instances == null
      ) {
        return;
      }
      let lastInstance = instances.slice(-1);

      let states = lastInstance[0].behavior;
      let outputI = [];
      states.forEach((state) => {
        outputI = [...state.outputs];
      });
      if (outputI.length == 1) {
        this.selectVal = outputI[0].value; // select绑定值为点击的选项的value
        this.treeEvent.value = outputI[0].value;
        this.treeEvent.dataId = outputI[0].dataId;
      }
      // this.dataList = outputI;
      return outputI;
    },

    // 结构树点击事件
    handleNodeClick(data) {
      console.log(data, "data");
      this.selectVal = data.value; // select绑定值为点击的选项的value
      this.treeEvent.value = data.value;
      this.treeEvent.dataId = data.id;
      //   this.$refs.select.blur(); // 点击后关闭下拉框，因为点击树形控件后select不会自动折叠
    },
  },

  mounted() {},
};
</script>
<style lang="scss" scoped></style>

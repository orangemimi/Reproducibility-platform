<template>
  <div class="about">
    <el-select
      v-model="selectVal"
      placeholder="Please select..."
      size="mini"
      clearable
      ref="select"
      style="width: 200px"
      @click.native="clickUserJumpIdState"
    >
      <el-input
        class="input"
        placeholder="Enter keywords to select"
        prefix-icon="el-icon-search"
        v-model="treeFilter"
        size="mini"
        clearable
      ></el-input>

      <el-option hidden key="id" :value="selectVal" :label="selectName">
      </el-option>

      <el-tree
        :data="treeData"
        :tree-props="{ children: 'children', hasChildren: true }"
        @node-click="handleNodeClick"
        :expand-on-click-node="false"
        :check-on-click-node="true"
        ref="tree"
        node-key="id"
        :default-expand-all="true"
        :filter-node-method="filterNode"
      >
        <!-- // @node-click：树形控件选项点击事件
        // :expand-on-click-node：使树形控件只有点箭头图标的时候才会展开或者收缩节点，为false则点文字直接选中该项
        // :check-on-click-node：是否在点击节点的时候选中节点，当选项为复选框时有用，这个属性可以删除
        // :default-expand-all：默认展开所有节点
        // :filter-node-method：实现搜索功能的筛选方法 -->

        <span slot-scope="{ data }">
          <span>{{ data.name }} {{ data.suffix }}</span>
          <!-- <el-tag
            size="mini"
            style="margin: 0 10px"
            v-show="filterorgType(data.tag)"
            >{{ filterorgType(data.tag) }}</el-tag
          > -->
        </span>
      </el-tree>
    </el-select>
  </div>
</template>

<script>
export default {
  name: "el-select-tree",
  props: {
    treeData: {
      type: Array,
    },
    treeEvent: {
      type: Object,
    },
  },
  data() {
    return {
      selectVal: "", // select框的绑定值
      selectName: "", // select框显示的name
      treeFilter: "", // 搜索框绑定值，用作过滤
      // 树形控件数据

      defaultProps: {
        children: "children",
        label: "name",
      },
      // 标签数组
    };
  },
  watch: {
    // 搜索过滤，监听input搜索框绑定的treeFilter
    treeFilter(val) {
      this.$refs.tree.filter(val);
      // 当搜索框键入值改变时，将该值作为入参执行树形控件的过滤事件filterNode
    },
  },
  methods: {
    clickUserJumpIdState() {
      // select单击事件，返回父组件 刷新data table
      this.$emit("refreshData", true);
    },
    // 结构树点击事件
    handleNodeClick(data) {
      this.selectVal = data.value; // select绑定值为点击的选项的value
      this.selectName = data.name + data.suffix; // input中显示值为label
      this.treeFilter = ""; // 点击后搜索框清空
      this.treeEvent.value = data.value;
      this.treeEvent.valueName = data.name;
      //   this.$forceUpdate();
      this.$refs.select.blur(); // 点击后关闭下拉框，因为点击树形控件后select不会自动折叠
      //   this.$emit("selectDataToEvent", data);
    },
    // 模糊查询（搜索过滤），实质为筛选出树形控件中符合输入条件的选项，过滤掉其他选项
    filterNode(value, data) {
      if (!value) return true;
      let filterRes = data.label.indexOf(value) !== -1;
      return filterRes;
    },
    // 选项卡标签（可忽略，增值迭代需求）
    filterorgType(val) {
      let arr = this.tagList.filter((item) => {
        return item.value == val;
      });
      return arr.length ? arr[0].label : "";
    },
  },

  mounted() {},
};
</script>

<style scoped>
.input {
  width: 260px;
  margin: 10px;
}
</style>

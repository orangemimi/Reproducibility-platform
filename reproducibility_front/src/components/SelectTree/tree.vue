<template>
  <div class="about">
    <el-select
      v-model="selectVal"
      placeholder="Please select..."
      size="default"
      clearable
      ref="select"
      style="width: 200px"
      @click="clickUserJumpIdState"
      popper-class="mySelectStyle"
    >
      <el-input
        class="input"
        placeholder="Enter keywords to select"
        v-model="treeFilter"
        size="default"
        clearable
      ></el-input>

      <el-option hidden key="id" :value="selectVal" :label="selectName">
      </el-option>
      <div>
        <el-tree
          :data="dataFolderList"
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

          <template v-slot="{ data }">
            <span>
              <span v-if="data.suffix">{{ data.name }} {{ data.suffix }}</span>
              <span v-else>{{ data.name }}</span>
              <!-- <el-tag
                size="default"
                style="margin: 0 10px"
                v-show="filterorgType(data.tag)"
                >{{ filterorgType(data.tag) }}</el-tag
              > -->
            </span>
          </template>
        </el-tree>
      </div>
    </el-select>
  </div>
</template>

<script>
import { getFolders, getScenarioById, getFoldersByTagId } from "@/api/request";
export default {
  data() {
    return {
      scenario: {},
      projectId: this.$route.params.id, //projectId
      dataFolderList: [],
      // select框的绑定值
      selectVal: "",
      // select框显示的name
      selectName: "",
      // 搜索框绑定值，用作过滤
      treeFilter: "",
      // 树形控件数据

      // 标签数组
      defaultProps: {
        children: "children",
        label: "name",
      },
    };
  },
  name: "el-select-tree",
  props: {
    treeEvent: {
      type: Object,
    },
    initialScenarioId: {
      type: String,
    },
    reproducedScenarioId: {
      type: String,
    },
    invokingType: {
      type: String,
    },
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
      // console.log(data,'data');
      this.selectVal = data.value; // select绑定值为点击的选项的value
      if (data.suffix) {
        this.selectName = data.name + data.suffix; // input中显示值为label
      } else {
        this.selectName = data.name;
      }
      this.treeFilter = ""; // 点击后搜索框清空
      this.treeEvent.value = data.value;
      this.treeEvent.dataId = data.id;
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
    async getFolders() {
      let folderData = [];
      let dataFolderList = [];
      //将绑定所有实例的输出数据也添加到dataFolderList当中

      let OutputFolderList = {};
      let intermediateInitial = [];
      let intermediateReproduced = [];
      let dataInitial = [];
      let dataReproduce = [];

      intermediateInitial = await this.getIntermediateData(
        this.initialScenarioId,
        "construction"
      );
      if (this.invokingType == "construction") {
        folderData = await getFolders();
        dataFolderList = folderData[0].children.filter(
          (item) => item.tagId == this.projectId
        );
        dataInitial = dataFolderList[0].children.filter(
          (item) => item.tagId == this.initialScenarioId
        );
        this.dataFolderList = [...dataInitial, ...intermediateInitial];
      }
      if (this.invokingType == "reproduction") {
        intermediateReproduced = await this.getIntermediateData(
          this.reproducedScenarioId,
          "reproduction"
        );

        dataInitial = await getFoldersByTagId(this.initialScenarioId);
        dataReproduce = await getFoldersByTagId(this.reproducedScenarioId);
        this.dataFolderList = [
          ...dataInitial,
          ...intermediateInitial,
          ...dataReproduce,
          ...intermediateReproduced,
        ];
        console.log(this.dataFolderList, "this.dataFolderList");
      }
    },
    async getIntermediateData(scenarioId, type) {
      let boundInstanceOutput = [];
      let intermediate = [];
      await this.getScenario(scenarioId);
      if (this.scenario.instanceObjectList == undefined) {
        return [];
      }
      this.scenario.instanceObjectList.filter((obj) => {
        obj.behavior.filter((state) => {
          state.outputs.forEach((out) => {
            out.id = out.dataId;
            out.name = out.datasetItem.dataName;
          });
          boundInstanceOutput.push(...state.outputs);
        });
      });

      let outputFolderList = {
        name:
          "Output of bound instances of " +
          (type == "construction" ? "initial" : "reproduced") +
          " scenario",
        children: boundInstanceOutput,
      };
      intermediate.push(outputFolderList);

      return intermediate;
    },
    async getScenario(scenarioId) {
      this.scenario = await getScenarioById(scenarioId);
    },
  },
  mounted() {
    this.getFolders();
  },
  emits: ["refreshData"],
};
</script>

<style lang="scss">
.input {
  width: 260px;
  margin: 10px;
}

.mySelectStyle {
  .el-select-dropdown {
    height: 500px;
  }
  .el-select-dropdown__wrap {
    max-height: 500px;
  }
}
</style>

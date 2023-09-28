<!--  -->
<template>
  <div>
    <div>
      <div
        v-if="
          chosenScenario.resourceCollection != null &&
            Object.prototype.hasOwnProperty.call(
              chosenScenario.resourceCollection,
              'modelList'
            ) &&
            chosenScenario.resourceCollection.modelList.length > 0
        "
      >
        <el-row style="float:left;margin:5px 10px  10px 0;">
          <div>
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
          </div>
          <div>
            <el-button
              icon="el-icon-upload2"
              size="mini"
              @click="addResourceInScenario"
              title="add a scenario"
            ></el-button>
          </div>
        </el-row>
        <el-input
          placeholder="Search model/tool"
          size="mini"
          class="search_input"
          v-show="modelList.length > 20"
        >
          <el-button slot="append" icon="el-icon-search"></el-button>
        </el-input>
        <div class="modelToolbarTable" v-show="activeNames == 'Models'">
          <el-table
            border
            ref="multipleModelTable"
            :data="modelList"
            @row-click="rowClick"
            highlight-current-row
            :default-sort="{ prop: 'name', order: 'descending' }"
            :cell-style="{
              color: '#666',
              fontFamily: 'Arial',
              fontSize: '10px',
              padding: '0',
            }"
            :header-cell-style="{
              fontFamily: 'Arial',
              fontSize: '14px',
            }"
          >
            <el-table-column
              prop="name"
              label="Name"
              width="140"
              sortable
            ></el-table-column>
            <el-table-column
              prop="updateTime"
              label="Update time"
              width="150"
              sortable
            ></el-table-column>
          </el-table>
        </div>
        <div class="modelToolbarTable" v-show="activeNames == 'Data'">
          <el-table
            border
            ref="multipleModelTable"
            :data="dataList"
            @row-click="rowClick"
            highlight-current-row
            :default-sort="{ prop: 'name', order: 'descending' }"
            :cell-style="{
              color: '#666',
              fontFamily: 'Arial',
              fontSize: '10px',
              padding: '0',
            }"
            :header-cell-style="{
              fontFamily: 'Arial',
              fontSize: '14px',
            }"
          >
            <el-table-column
              prop="name"
              label="Name"
              width="140"
              sortable
            ></el-table-column>
            <el-table-column
              prop="updateTime"
              label="Update time"
              width="150"
              sortable
            ></el-table-column>
          </el-table>
        </div>
      </div>

      <div v-else>
        <el-empty description="Please add the resources first"></el-empty>
      </div>
    </div>
    <div class="selectResource">
      <el-dialog
        :visible.sync="selectResourceDialog"
        width="1000px"
        title="Select resources"
        :close-on-click-modal="false"
        :destroy-on-close="true"
      >
        <el-steps
          class="steps_cont"
          :active="stepActive"
          simple
          finish-status="wait"
          process-status="finish"
        >
          <el-step
            v-for="(item, index) in stepsList"
            :key="index"
            :title="item.title"
            :icon="item.icon"
            @click.native="choiceStep(index)"
          ></el-step>
        </el-steps>
        <div>
          <template v-if="stepActive == 0">
            <el-table
              border
              ref="multipleTable"
              :data="allModelsWithUser"
              @select="handleSelectionChange"
            >
              <el-table-column type="selection" width="55"> </el-table-column>
              <el-table-column
                prop="name"
                label="Name"
                width="200"
              ></el-table-column>
              <el-table-column
                prop="description"
                label="Description"
              ></el-table-column>
              <el-table-column
                prop="privacy"
                label="Privacy"
                width="100"
              ></el-table-column>
            </el-table>
          </template>
          <!-- {{ multipleModelSelection }} -->
        </div>

        <span slot="footer" class="dialog-footer">
          <el-button type="primary" @click="addResourceToScenario"
            >Submit</el-button
          >
        </span>
      </el-dialog>
    </div>
  </div>
</template>

<script>
// import ModelCard from "_com/Cards/ModelCard";
import { updateresourceCollection } from "@/api/request";
export default {
  props: {
    chosenScenario: {
      type: Object,
    },
  },
  components: {
    // ModelCard,
    // modelToolbar
  },

  watch: {
    chosenScenario: {
      async handler(val) {
        let newVal = val.resourceCollection;
        if (
          newVal != null &&
          (Object.hasOwnProperty.call(newVal, "modelList") ||
            Object.hasOwnProperty.call(newVal, "dataList"))
        ) {
          this.modelList = newVal.modelList;
          this.dataList = newVal.dataList;
        }
        this.getSelectedResources();
      },
      deep: true,
      immediate: true,
    },
  },

  computed: {},

  data() {
    return {
      activeNames: "Models",
      modelList: this.resourceList.modelList,
      dataList: this.resourceList.dataList,
      modelSelection: [],
      resourceCollection: {},
      //mxgraph scrollbar
      multipleModelSelection: [],
      multipleDataSelection: [],
      stepActive: 0,
      stepsList: [
        { title: "Add model", icon: "el-icon-edit" },
        { title: "Add data", icon: "el-icon-edit" },
      ],
      selectResourceDialog: false,
      ops: {
        bar: {
          onlyShowBarOnScroll: false,
          keepShow: true,
          background: "#c1c1c1",
          opacity: 1,
          hoverStyle: false,
          specifyBorderRadius: false,
          minSize: 0,
          size: "6px",
          disable: false,
        },
      },
    };
  },

  methods: {
    handleSelectionChange(selection, row) {
      const selected = selection.some((item) => item === row); // 是取消选择还是选中
      if (selected) {
        // 选择
        this.multipleModelSelection.push(row.id);
      } else {
        // 取消选择
        var finalArr = this.multipleModelSelection.filter((item) => {
          return item !== row.id;
        });
        this.multipleModelSelection = finalArr;
      }
    },

    choiceStep(index) {
      this.stepActive = index;
    },

    // //init table selection
    // handleCurrentChange(rows) {
    //   if (rows) {
    //     rows.forEach((row) => {
    //       this.$refs.multipleTable.toggleRowSelection(row);
    //     });
    //   } else {
    //     this.$refs.multipleTable.clearSelection();
    //   }
    // },
    rowClick(row) {
      console.log(row);
      row.isCurrent = true;
      this.$emit("selectModel", row);
      this.$refs["multipleModelTable"].clearSelection();
      this.$refs["multipleModelTable"].toggleRowSelection(row, true);
    },

    clickModel(val) {
      val.isCurrent = true;
      this.$emit("selectModel", val);
    },
    addResourceInScenario() {
      this.selectResourceDialog = true;
      this.getSelectedResources();
    },
    getSelectedResources() {
      if (
        Object.prototype.hasOwnProperty.call(
          this.resourceCollection,
          "modelList"
        )
      ) {
        let selected = this.resourceCollection.modelList;

        this.multipleModelSelection = selected;
        this.multipleModelSelection.forEach((item) => {
          this.$nextTick(() => {
            this.allModelsWithUser.find((obj) => {
              if (item === obj.id) {
                this.$refs["multipleTable"].toggleRowSelection(obj, true);
              }
            });
          });
        });
      }
    },
    async addResourceToScenario() {
      this.selectResourceDialog = false;

      this.chosenScenario = await updateresourceCollection(
        this.chosenScenario.id,
        {
          modelList: this.multipleModelSelection,
          dataList: this.multipleDataSelection,
        }
      );
    },
  },

  mounted() {},
};
</script>
<style lang="scss" scoped>
.modelToolbarTable {
  /* ::v-deep .el-table .td.el-table__cell .cell .el-table__body .cell {
    font-size: 6px;
  } */
}
</style>

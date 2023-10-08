<!--  -->
<template>
  <div>
    <div>
      <div
        style="float:right;margin-right:10px"
        v-show="activeNames == 'Models'"
      >
        <el-button
          icon="el-icon-upload2"
          size="mini"
          @click="addResourceInScenario"
          title="add a scenario"
        ></el-button>
      </div>
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
        </el-row>
        <el-input
          placeholder="Search model/tool"
          size="mini"
          class="search_input"
          v-show="resourceCollection.modelList.length > 20"
        >
          <el-button slot="append" icon="el-icon-search"></el-button>
        </el-input>
        <div class="modelToolbarTable" v-show="activeNames == 'Models'">
          <el-table
            border
            ref="multipleModelTable"
            :data="resourceCollectionObjects.modelList"
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
          <DataTable :scenarioId="chosenScenario.id"></DataTable>
          <!-- <el-table
            border
            ref="multipleModelTable"
            :data="resourceCollectionObjects.dataList"
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
          </el-table> -->
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
        title="Select models"
        :close-on-click-modal="false"
        :destroy-on-close="true"
      >
        <el-table
          border
          ref="multipleModelTableInDialog"
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
import DataTable from "./DataTable";
import { updateresourceCollection, getMyModels } from "@/api/request";
export default {
  props: {
    chosenScenario: {
      type: Object,
    },
  },
  components: {
    DataTable,
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
          this.resourceCollection = newVal;
          this.resourceCollectionObjects = val.resourceCollectionObjects;
          this.getSelectedResources();
        }
      },
      deep: true,
      immediate: true,
    },
  },

  computed: {},

  data() {
    return {
      activeNames: "Models",
      modelSelection: [],
      resourceCollection: {},
      //mxgraph scrollbar
      multipleModelSelection: [],
      multipleDataSelection: [],
      selectResourceDialog: false,
      allModelsWithUser: [],
    };
  },

  methods: {
    handleSelectionChange(selection, row) {
      const selected = selection.some((item) => item === row); // 是取消选择还是选中

      if (!(this.multipleModelSelection instanceof Array)) {
        this.multipleModelSelection = [];
      }
      console.log(
        this.multipleModelSelection,
        this.multipleModelSelection instanceof Array,
        "modelselection"
      );
      if (selected) {
        // 选择
        this.multipleModelSelection.push(row.id);
      } else {
        // 取消选择
        let finalArr = this.multipleModelSelection.filter((item) => {
          return item !== row.id;
        });
        this.multipleModelSelection = finalArr;
      }
    },

    // //init table selection
    // handleCurrentChange(rows) {
    //   if (rows) {
    //     rows.forEach((row) => {
    //       this.$refs.multipleModelTableInDialog.toggleRowSelection(row);
    //     });
    //   } else {
    //     this.$refs.multipleModelTableInDialog.clearSelection();
    //   }
    // },
    rowClick(row) {
      row.isCurrent = true;
      this.$emit("selectModel", row);
      this.$refs["multipleModelTable"].clearSelection();
      this.$refs["multipleModelTable"].toggleRowSelection(row, true);
    },

    addResourceInScenario() {
      this.selectResourceDialog = true;
      this.getSelectedResources();
    },
    getSelectedResources() {
      if (
        this.chosenScenario.resourceCollection == null ||
        !Object.hasOwnProperty.call(
          this.chosenScenario.resourceCollection,
          "modelList"
        )
      ) {
        this.multipleModelSelection = {};
        return;
      }
      let selected = this.chosenScenario.resourceCollection.modelList;

      this.multipleModelSelection = selected;
      this.multipleModelSelection.forEach((item) => {
        this.$nextTick(() => {
          this.allModelsWithUser.find((obj) => {
            if (item === obj.id) {
              this.$refs["multipleModelTableInDialog"].toggleRowSelection(
                obj,
                true
              );
            }
          });
        });
      });
    },
    async addResourceToScenario() {
      console.log(
        this.multipleModelSelection,
        this.multipleDataSelection,
        "selection"
      );
      this.selectResourceDialog = false;

      this.chosenScenario = await updateresourceCollection(
        this.chosenScenario.id,
        {
          modelList: this.multipleModelSelection,
          dataList: this.multipleDataSelection,
        }
      );
    },
    async init() {
      //get my models
      this.allModelsWithUser = await getMyModels();
      console.log("this.allModelsWithUser", this.allModelsWithUser);
    },
  },

  async mounted() {
    await this.init();
  },
};
</script>
<style lang="scss" scoped>
.modelToolbarTable {
  /* ::v-deep .el-table .td.el-table__cell .cell .el-table__body .cell {
    font-size: 6px;
  } */
}
</style>

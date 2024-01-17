<template>
  <div>
    <div
      style="
        width: 100%;
        margin-top: 20px;
        padding: 15px;
        background-color: #f8f8f9;
      "
      :style="{ height: contentHeight - 140 + 'px' }"
      ref="steps"
    ></div>

    <el-dialog
      title="Public resource selection"
      v-model="resourceCollectDialog"
      :close-on-click-modal="false"
      width="60%"
    >
      <el-row>
        <el-col :span="10">
          <el-input
            placeholder="Search by name"
            v-model="keyword"
            class="input-with-select"
            size="small"
          >
            <template v-slot:append>
              <el-button
                :icon="ElIconSearch"
                @click="searchModelByName"
                v-if="!isSearching"
              ></el-button>
              <el-button
                :icon="ElIconClose"
                @click="clearSearch"
                v-else
              ></el-button>
            </template>
          </el-input>
        </el-col>
      </el-row>

      <div class="transfer">
        <el-transfer
          class="mainContainer"
          v-model="selectedModelsWithId"
          @change="
            (value) => {
              selectChange(value, 'model')
            }
          "
          :titles="['Public models', 'Selected models']"
          :data="currentModelList"
        >
        </el-transfer>
        <el-pagination
          layout="prev, pager, next"
          :total="total"
          small
          :pager-count="5"
          :page-size="20"
          :current-page="1"
          @current-change="currentModelChange"
          class="pageClass"
          v-show="!isSearching"
        ></el-pagination>
        <div class="dialog-footer">
          <el-button @click="resourceCollectDialog = false" size="mini"
            >Cancle</el-button
          >
          <el-button type="primary" @click="setResourceCollect()" size="mini"
            >Submit</el-button
          >
        </div>
      </div>
    </el-dialog>

    <el-dialog
      title="Create one scenerio computational processes"
      v-model="createScenarioDialog"
      :close-on-click-modal="false"
      width="60%"
    >
      <div>
        <el-form
          ref="scenarioInfoForm"
          :model="scenarioInfoForm"
          label-width="180px"
        >
          <el-form-item label="Scenerio Name">
            <el-input v-model="scenarioInfoForm.name"></el-input>
          </el-form-item>
          <el-form-item label="Scenerio Type">
            <el-radio v-model="scenarioInfoForm.type" label="sequentModels"
              >Sequent models</el-radio
            >
            <el-radio v-model="scenarioInfoForm.type" label="integrateTask"
              >Integrate task</el-radio
            >
          </el-form-item>
        </el-form>

        <div class="dialog-footer">
          <el-button @click="createScenarioDialog = false" size="mini"
            >Cancle</el-button
          >
          <el-button type="primary" @click="setScenerio()" size="mini"
            >Create</el-button
          >
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  Search as ElIconSearch,
  Close as ElIconClose,
} from '@element-plus/icons-vue'
import echarts from 'echarts'
import { mapState } from 'vuex'
import {
  getProjectById,
  getModelsByPrivacy,
  updateresourceCollection,
  getPublicModelListByIgnoreName,
  updateProject,
  // getScenarioById,
  saveScenario,
} from '@/api/request'
export default {
  data() {
    return {
      //projectId
      projectId: this.$route.params.id,
      project: {},
      resourceCollectDialog: false,
      modelList: [],
      chart: {},
      contentHeight: window.innerHeight - 200,
      ops: {
        bar: {
          background: '#808695',
          keepShow: true,
        },
      },
      selectedModelsWithId: [],
      currentModelList: [],
      pageFilter: {
        pageSize: 8,
        page: 0,
      },
      total: 0,
      keyword: '',
      isSearching: false,
      createScenarioDialog: false,
      typeOptions: [
        { label: 'Sequent models', value: 'sequentModels' },
        { label: 'Integrate Task', value: 'integrateTask' },
      ],
      scenarioInfoFormInit: null,
      scenarioInfoForm: { type: 'sequentModels' },
      hasScenario: false,
      selectedSecnario: {},
      ElIconSearch,
      ElIconClose,
    }
  },
  computed: {
    ...mapState(['user']),
  },
  methods: {
    // transfer----------
    async currentModelChange(val) {
      await this.getPublicModels(val - 1)
    },
    async getPublicModels(val) {
      let data = await getModelsByPrivacy('public', val, 20)

      this.total = data.totalElements
      this.modelList = data.content

      var currentModelList = this.modelList.map((value) => {
        return {
          label: value.name,
          key: value.id,
          obj: value,
        }
      })
      // 初始化数据
      this.currentModelList = currentModelList
    },

    // 穿梭搜索
    async searchModelByName() {
      let data = await getPublicModelListByIgnoreName(this.keyword)
      this.modelList = data
      var currentModelList = this.modelList.map((value) => {
        return {
          label: value.name,
          key: value.id,
          obj: value,
        }
      })
      // 初始化数据
      this.currentModelList = currentModelList
      this.isSearching = true
    },
    clearSearch() {
      this.keyword = ''
      this.init()
      this.isSearching = false
    },

    async filterQueryModel(text, val) {
      let data = await getPublicModelListByIgnoreName(text, val, 20)
      console.log(data)
      this.total = data.totalElements
      this.modelList = data.content

      var currentModelList = this.modelList.map((value) => {
        return {
          label: value.name,
          key: value.id,
          obj: value,
        }
      })
      // 初始化数据
      this.currentModelList = currentModelList
    },

    init() {
      this.getPublicModels(0)
    },

    selectChange(value, type) {
      if (type == 'model') {
        this.modelList = value
      }
    },

    initEchart() {
      let option = {
        animationDurationUpdate: 500,
        animationEasingUpdate: 'quinticInOut',
        legend: {
          show: true,
          data: [
            {
              name: 'Model collection',
              icon: 'circle',
            },
            {
              name: 'Computational process construction',
              icon: 'circle',
            },
          ],
        },
        series: [
          {
            type: 'graph',
            layout: 'none',
            legendHoverLink: true,
            roam: this.procedureDrag,
            label: {
              normal: {
                show: true,
              },
            },
            edgeSymbol: ['circle', 'arrow'],
            edgeSymbolSize: [4, 10],
            focusNodeAdjacency: true,
            data: [
              {
                name: 'Model collection',
                category: 'Model collection',
                label: '资源收集',
                symbolSize: 45,
                x: 700,
                y: 500,
              },
              {
                name: 'Computational process construction',
                category: 'Computational process construction',
                label: '模拟计算',
                symbolSize: 45,
                x: 800,
                y: 500,
              },
            ],
            categories: [
              {
                name: 'Model collection',
              },
              {
                name: 'Computational process construction',
              },
            ],
            links: [
              {
                source: 'Model collection',
                target: 'Computational process construction',
              },
              {
                source: 'Computational process construction',
                target: 'Simulation  evaluation',
              },
            ],
            lineStyle: {
              normal: {
                opacity: 1,
                width: 5,
                curveness: 0.1,
              },
            },
          },
        ],
      }
      this.chart = echarts.init(this.$refs.steps)
      this.chart.setOption(option)
      this.chart.on('dblclick', ({ data }) => {
        let type = data.name
        switch (type) {
          case 'Model collection':
            {
              this.resourceCollectDialog = true
            }
            break
          case 'Computational process construction':
            {
              this.createScenarioDialog = true
            }
            break
        }
      })
    },

    async setResourceCollect() {
      let data = await updateresourceCollection(
        this.projectId,
        'modelList',
        this.selectedModelsWithId
      )
      this.project = data
      this.resourceCollectDialog = false
    },

    async setScenerio() {
      let data = await saveScenario({
        projectId: this.projectId,
        name: this.scenarioInfoForm.name,
        type: this.scenarioInfoForm.type,
      })
      this.project.scenario = data.id
      await updateProject(this.projectId, this.project)
      // this.createScenarioDialog = false;
      this.$router.push({
        path: `/project/${this.projectId}/scenarios`,
      })
    },
  },
  async mounted() {
    this.project = await getProjectById(this.$route.params.id)

    if (this.project.scenarioList != null && this.project.scenarioList.length!=0) {
      this.hasScenario = true

      this.$router.push({
        path: `/project/${this.projectId}/scenarios`,
      })
    }
    // console.log(this.project);
    if (
      this.project.resourceCollection != null &&
      this.project.resourceCollection.modelList != null
    ) {
      this.selectedModelsWithId = this.project.resourceCollection.modelList
    }
    this.init()
    this.initEchart()
    // this.getData();
  },
}
</script>

<style scoped>
.main {
  width: 100%;
  min-height: calc(100vh - 240px);
}
.time {
  font-size: 13px;
  color: #999;
}
.bottom {
  margin-top: 13px;
  line-height: 12px;
}
.button {
  padding: 0;
  float: right;
}
.image {
  width: 100%;
  display: block;
}
.clearfix:before,
.clearfix:after {
  display: table;
  content: '';
}
.clearfix:after {
  clear: both;
}
>>> .el-dialog__body {
  padding: 5px 20px 40px 20px;
  margin: 20px;
}
.mainContainer >>> .el-transfer-panel {
  width: 300px;
}
.mainContainer >>> .el-transfer-panel__body {
  height: 480px;
}
.mainContainer >>> .el-transfer-panel__list {
  height: 450px;
}
.mainContainer >>> .el-transfer-panel__list.is-filterable {
  height: 400px;
}
.mainContainer >>> .el-transfer__buttons .el-button {
  display: block;
  margin-left: 0;
}
.dialog-footer {
  float: right;
}
.pageClass {
  position: relative;
  bottom: 30px;
  left: 50px;
}
</style>

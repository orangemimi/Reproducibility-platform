<template>
  <div class="main">
    <el-col
      :xs="{ span: 22, offset: 1 }"
      :sm="{ span: 22, offset: 1 }"
      :md="{ span: 22, offset: 1 }"
      :lg="{ span: 18, offset: 3 }"
    >
    </el-col>
    <el-row>
      <echart-flow></echart-flow>
    </el-row>
  </div>
</template>

<script>
// import stepCard from "_com/Cards/StepCard";
// import recordList from '_com/RecordList';
import EchartFlow from "./EchartFlow/index";

import { getProjectAndUsers } from "@/api/request";
import { dateFormat } from "@/utils/utils";
export default {
  components: {
    EchartFlow,
  },
  data() {
    return {
      projectId: this.$route.params.id,
      projectInfo: {},
      completion: [],
      context: {},
      resource: {},
      scenario: {},
      results: {},
      // cardInfos: [{ btnType: 'Context Definition' }, { btnType: 'Resource Collection' }]
    };
  },

  methods: {
    async init() {
      await this.getProjectInfo();
      // await this.judgeRole(this.projectInfo);
    },

    async getProjectInfo() {
      let data = await getProjectAndUsers(this.projectId);
      // console.log(data);
      this.projectInfo = data.project;
      this.creator = data.creator;
      this.members = data.members;
    },

    dateFormat(time) {
      if (time == null) {
        return "You have not do any operation";
      }
      return dateFormat(time);
    },
  },
  mounted() {
    this.init();
  },
};
</script>

<style lang="scss" scoped>
.main {
  width: 100%;
  min-height: calc(100vh - 240px);
  .main-card {
    background-color: white;
    min-height: calc(100vh - 242px);
    padding: 20px 32px;
  }
  .record {
    position: fixed;
    right: 30px;
    bottom: 75px;
    z-index: 100;
    .block {
      /deep/ .el-timeline-item__tail {
        left: 8px;
      }
      /deep/.el-timeline-item__wrapper {
        padding-left: 35px;
      }
      /deep/ .el-timeline-item__node--large {
        height: 25px;
        width: 25px;
      }
    }
  }
}
</style>

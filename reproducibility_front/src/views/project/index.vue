<!-- project info -->
<template>
  <div>
    <div class="page-header">
      <div class="nav">
        <el-col class="breadcrumb" :span="10">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item class="name">
              {{ creator.name }}
            </el-breadcrumb-item>
            <el-breadcrumb-item class="project">{{
              projectInfo.name
            }}</el-breadcrumb-item>
          </el-breadcrumb>
        </el-col>
        <!-- TODO-zzy -->
        <!-- <el-col class="info-btn" :span="14">
          <star-btn class="star"></star-btn>
          <watch-btn :count="projectInfo.watchCount" class="watch"></watch-btn>
        </el-col> -->
      </div>

      <div class="menu">
        <builder-menu
          v-if="permission.role == 'participant'"
          @toRouterType="toRouterType"
        ></builder-menu>
        <re-builder-menu v-else @toRouterType="toRouterType"></re-builder-menu>
      </div>
    </div>
    <div class="page-content">
      <router-view class="scroll-item"></router-view>
    </div>
  </div>
</template>

<script>
import { getProjectAndUsers, forkProject } from "@/api/request";
// import { judgeRole } from "@/api/requestVuex";
// import watchBtn from "_com/PageHeaderBtn/WatchBtn";
// import starBtn from "_com/PageHeaderBtn/StarBtn";
// import folkBtn from "_com/PageHeaderBtn/FolkBtn";
import builderMenu from "./components/BuilderMenu";
import reBuilderMenu from "./components/ReBuilderMenu";
import { mapState } from "vuex";

export default {
  components: {
    // watchBtn, starBtn,
    builderMenu,
    reBuilderMenu,
  },

  async beforeRouteUpdate(to, from, next) {
    this.projectId = to.params.id;
    await this.getProjectInfo();
    next();
  },

  computed: {
    ...mapState(["user", "permission"]),
  },

  data() {
    return {
      projectId: this.$route.params.id,
      projectInfo: {},
      creator: {},
      memberList: [],
    };
  },

  methods: {
    async init() {
      await this.getProjectInfo();
      await this.judgeRole(this.projectInfo, this.user.userId);
    },
    async judgeRole(project, userId) {
      await this.$store.dispatch("permission/getRole", {
        project: project,
        userId: userId,
      });
    },

    async getProjectInfo() {
      let data = await getProjectAndUsers(this.projectId);

      this.projectInfo = data.project;
      this.creator = data.creator;
      this.memberList = data.memberList;
      console.log;
    },

    toRouterType(val) {
      if (val != this.$router.currentRoute.name) {
        this.$router.push({
          name: val,
          query: {
            forkingProjectId: this.projectInfo.forkingProjectId,
          },
        });
      }
    },

    async createProject() {
      let form = {
        name: this.projectInfo.name,
        description: this.projectInfo.description,
        purpose: this.projectInfo.purpose,
        privacy: this.projectInfo.privacy,
        picture: this.projectInfo.picture,
        forkingProjectId: this.projectInfo.id,
      };
      let data = await forkProject(form);

      this.userInfo.folkedProjects.push(data.id);
      // await updateUserByJwtUserId({ createdProjects: this.userInfo.createdProjects });
    },
  },

  mounted() {
    this.init();
  },
};
</script>
<style lang="scss" scoped>
.page-header {
  height: 120px;
  width: 100%;
  background-color: $contain2Background;
  box-shadow: $underLineNavBoxShadow;
  padding: 16px 32px 0 32px;

  .nav {
    .breadcrumb {
      padding: 5px 0;
      /deep/ .el-breadcrumb__inner {
        font-size: 18px;
        font-weight: 550;
        line-height: 22px;
        color: $blueEmplasisFont;
      }
      .name {
        font-weight: 400;
      }
      .project {
        // width: 1200px;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
      }
    }
    .info-btn {
      line-height: 32px;
      float: right;
      .watch {
        float: right;
        margin: 0 10px;
      }
      .star {
        float: right;
        margin: 0 10px;
      }
      // .folk {
      //   float: right;
      //   margin: 0 10px;
      // }
    }
  }
  .menu {
    clear: both;
    // height: 32px;
    line-height: 72px;
  }
}
</style>

<template>
  <div class="project">
    <el-row>
      <el-col :span="4">
        <div class="left">
          <div class="new-project">
            projects
            <el-button
              type="success"
              size="default"
              class="btn"
              @click="dialogNewProject = true"
            >
              <el-icon><DocumentAdd /></el-icon>
            </el-button>
          </div>
          <el-input
            placeholder="Please enter the content"
            size="small"
          >
            <template #prepend>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>

          <div v-if="myProjects.length > 0">
            <!-- <div
              v-for="(item, index) in myProjects"
              :key="index"
              style="display: flex; margin-bottom: 5px"
            >
              <router-link
                tag="div"
                :to="{ path: `/project/${item.projectId}/info` }"
              >
                <div style="height: 30px; line-height: 30px" class="router">
                  {{ user.name }}
                  <span>/</span>
                  {{ item.name }}
                </div>
              </router-link>
            </div> -->
            <div
              v-for="(item, index) in myProjects"
              :key="index"
              style="display: flex; margin-bottom: 5px"
            >
              <router-link
                :to="{ path: `/project/${item.projectId}/info` }"
                v-slot="{ navigate }"
              >
                <div
                  @click="navigate"
                  style="height: 30px; line-height: 30px"
                  class="router"
                >
                  {{ user.name }}
                  <span>/</span>
                  {{ item.name }}
                </div>
              </router-link>
            </div>
            <el-divider></el-divider>
          </div>
        </div>
      </el-col>
      <el-col :span="20">
        <el-row :gutter="20">
          <el-col
            :span="6"
            v-for="(project, index) in projectList"
            :key="index"
          >
            <div class="project-card">
              <project-card :project="project"></project-card>
            </div>
          </el-col>
        </el-row>
      </el-col>
    </el-row>
    <div style="text-align: center; margin-top: 20px">
      <el-pagination
        background
        layout="prev, pager, next"
        :total="total"
        @current-change="change"
        :page-size="10"
      ></el-pagination>
    </div>

    <el-dialog title="New Project" v-model="dialogNewProject" width="60%">
      <create @dialogShow="dialogShow" />
    </el-dialog>
  </div>
</template>

<script>
import { getMyProjects, getAllProjects } from "@/api/request";
import create from "../create/index.vue";
import { imgBase64 } from "@/lib/utils";
import projectCard from "../components/ProjectCard.vue";
import { mapState } from "vuex";
export default {
  data() {
    return {
      projectList: [],
      userProjectsInfo: {},
      total: 0,
      dialogNewProject: false,
      myProjects: [],
    };
  },
  components: { projectCard, create },
  watch: {},
  computed: {
    ...mapState(["user"]),
  },
  methods: {
    async init() {
      this.myProjects = await getMyProjects();
      let data = await getAllProjects(0, 16);
      this.projectList = data.content;
      this.total = data.totalElements;
    },

    imgBase64(projectName) {
      return imgBase64(projectName);
    },

    async change(val) {
      let data = await getAllProjects(val - 1, 16);
      this.projectList = data.content;
    },

    async dialogShow(val) {
      console.log(val, "out1");
      this.dialogNewProject = val;
      await this.init();
    },
  },
  mounted() {
    console.log(this.user);
    this.init();
  },
};
</script>

<style lang="scss" scoped>
.left {
  margin-top: 10px;
  .new-project {
    display: flex;
    margin-bottom: 5px;

    .btn {
      margin-left: auto;
      margin-right: 20px;
    }
  }
  .el-input {
    width: calc(100% - 20px);
    margin-bottom: 25px;
  }
  .router {
    width: 220px;
    overflow: hidden;
    white-space: wrap;
    text-overflow: ellipsis;
    -o-text-overflow: ellipsis;
  }

  .router:hover {
    cursor: pointer;
    color: #1f77b4;
    text-decoration: underline;
  }
}
.el-col-5 {
  width: 20%;
}
.project {
  padding: 0 10px; /*// for col
*/
  .project-card:hover {
    cursor: pointer;
  }
}
</style>

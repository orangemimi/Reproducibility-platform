<template>
  <div class="information">
    <el-col
      :xs="24"
      :sm="24"
      :md="{ span: 22, offset: 1 }"
      :lg="{ span: 22, offset: 1 }" 
    >
    <!-- tmd好神奇，vue2转vue3后这个布局就有问题了，左半边空白，右半边对半分，加了这个br才好，真是玄学,盲猜跟el-row或者flex布局有关，升级后很多el-row的布局跟原先都不太一样 -->
    <br>
      <el-row :gutter="10">
        <el-col :span="12">
          <div class="info-card">
            <div class="info-card-top">
              <div
                class="info-detail"
                v-if="Object.keys(projectInfo).length != 0"
              >
                <div class="title">
                  <h2>{{ projectInfo.name }}</h2>
                  <div
                    v-if="
                      projectInfo.forkingProjectId != '' &&
                      projectInfo.forkingProjectId != undefined
                    "
                    class="prompt"
                  >
                    from
                    <router-link
                      :to="{
                        path: `/project/${projectInfo.forkingProjectId}/info`,
                      }"
                      >this project</router-link
                    >
                  </div>
                </div>
                <div class="content">
                  <div class="head">
                    <div class="mark"></div>
                    <div><strong>Category:</strong></div>
                    <div class="line"></div>
                  </div>
                  <div class="text">Reproduction</div>
                </div>
                <div class="content">
                  <div class="head">
                    <div class="mark"></div>
                    <div><strong>Tag:</strong></div>
                    <div class="line"></div>
                  </div>
                  <div class="tags">
                    <div v-if="projectInfo.tags != null">
                      <div
                        v-for="(item, index) in projectInfo.tags"
                        :key="index"
                      >
                        <el-tag type="success" style="float: left">{{
                          item
                        }}</el-tag>
                      </div>
                    </div>
                    <div v-else>
                      <el-tag type="success" style="float: left"
                        ><el-icon><el-icon-edit /></el-icon
                      ></el-tag>
                    </div>
                  </div>
                </div>

                <div class="content">
                  <div class="head">
                    <div class="mark"></div>
                    <div style="width: 50%"><strong>Create time:</strong></div>
                    <div class="line"></div>
                  </div>
                  <div class="time text">
                    {{ dateFormat(projectInfo.createTime) }}
                  </div>
                </div>
              </div>

              <div class="info-img">
                <div style="float: right">
                  <el-button
                    type="primary"
                    plain
                    size="small"
                    @click="editProjectInfoDialog"
                    >Edit</el-button
                  >
                  <el-button-group>
                    <el-button type="primary" plain size="small">
                      <el-icon><el-icon-share /></el-icon>
                    </el-button>
                    <el-button type="primary" plain size="small">
                      <el-icon><el-icon-more /></el-icon>
                    </el-button>
                  </el-button-group>
                </div>
                <div>
                  <img
                    v-if="projectInfo.picture"
                    :src="projectInfo.picture"
                    :alt="projectInfo.name"
                    class="avatar-img"
                    style="width: 200px; height: 200px; margin-top: 10px;"
                  />

                </div>
              </div>
            </div>
           <div class="info-card-bottom">
              <div class="des">
                <div class="head">
                  <div class="mark"></div>
                  <div><strong>Description:</strong></div>
                  <div class="line"></div>
                </div>
                <div
                  class="content"
                  v-if="
                    projectInfo.description != null &&
                    projectInfo.description != ''
                  "
                >
                  <div
                    class="item-box w-e-text"
                    v-html="projectInfo.description"
                  ></div>
                </div>
                <div v-else class="content">
                  <p class="nocontent">No description</p>
                </div>
              </div>
            </div>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="info-card-right">
            <div class="participants">
              <div class="info">
                <div class="mark"></div>
                <div class="title">Participants</div>
                <div class="add-participant">
                  <el-button
                    size="default"
                    @click="addParticipantDialogShow = true"
                  >
                    Add participant
                    <el-icon><el-icon-user-solid /></el-icon>
                  </el-button>
                </div>
              </div>

              <div class="content" v-if="creator.avatar != undefined">
                <!-- <vue-scroll
                  :ops="ops"
                  class="scroll"
                  style="height: calc(100vh - 754px)"
                > -->
                  <user-card
                    :user="{
                      name: creator.name,

                      id: creator.id,
                      avatar: creator.avatar,
                    }"
                    class=""
                  ></user-card>
                  <el-row>
                    <div v-if="memberList.length > 103">
                      <div v-for="(item, index) in 103" :key="index">
                        <el-col :span="3"
                          ><user-card :user="memberList[index]"></user-card
                        ></el-col>
                      </div>
                      <el-col :span="3">
                        <el-avatar :size="50" :icon="ElIconMore"></el-avatar>
                      </el-col>
                    </div>
                    <div v-else>
                      <div v-for="(member, index) in memberList" :key="index">
                        <el-col :span="3"
                          ><user-card :user="member"></user-card
                        ></el-col>
                      </div>
                    </div>
                  </el-row>
                <!-- </vue-scroll> -->
              </div>
            </div>
            <div class="citation">
              <div class="info">
                <div class="mark"></div>
                <div class="title">Citation</div>
                <div class="edit">
                  <el-button size="default" @click="editCitation = true"
                    >Edit</el-button
                  >
                </div>
              </div>

              <div
                class="content"
                v-if="
                  projectInfo.citation != undefined &&
                  projectInfo.citation != null
                "
              >
                <el-descriptions direction="vertical" :column="4" border>
                  <el-descriptions-item label="class">{{
                    projectInfo.citation.type
                  }}</el-descriptions-item>
                  <el-descriptions-item
                    :label="
                      projectInfo.citation.type == 'Book'
                        ? 'Book name'
                        : 'Title'
                    "
                  >
                    {{ projectInfo.citation.name }}
                  </el-descriptions-item>
                  <el-descriptions-item
                    :label="getAuthorLabel(projectInfo.citation.type)"
                    :span="2"
                    >{{ projectInfo.citation.author }}</el-descriptions-item
                  >
                  <el-descriptions-item
                    :label="getSourceLable(projectInfo.citation.type)"
                  >
                    <el-tag size="small">{{
                      projectInfo.citation.source
                    }}</el-tag>
                  </el-descriptions-item>
                  <el-descriptions-item label="Date">{{
                    getDate(
                      projectInfo.citation.date,
                      projectInfo.citation.dateType
                    )
                  }}</el-descriptions-item>
                </el-descriptions>
              </div>
              <div v-else>
                <p class="nocontent">No description</p>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>
    </el-col>
  </div>
</template>

<script>
import {
  Edit as ElIconEdit,
  Share as ElIconShare,
  More as ElIconMore,
  UserFilled as ElIconUserSolid,
} from '@element-plus/icons-vue'
import { getProjectAndUsers } from '@/api/request'
import { dateFormat } from '@/utils/utils'
import { mapState } from 'vuex'
import Avatar from 'vue-avatar'
import userCard from '_com/Cards/UserCard.vue'
export default {
  data() {
    return {
      temporaryIcon: null,
      projectId: this.$route.params.id,
      projectInfo: {},
      creator: {},
      memberList: [],
      ops: {
        bar: {
          background: '#808695',
        },
      },
      editProjectInfoDialogShow: false,
      addParticipantDialogShow: false,
      editCitation: false,
      ElIconMore,
    }
  },
  components: {
    Avatar,
    // editInfoForm,
    // shareProject,
    // citationForm,
    // reBuilderCard
    userCard,
    ElIconEdit,
    ElIconShare,
    ElIconMore,
    ElIconUserSolid,
  },
  async beforeRouteUpdate(to, from, next) {
    this.projectId = to.params.id
    await this.init()
    next()
  },
  computed: {
    ...mapState({
      // userId: state => state.user.userId,
      // role: (state) => state.permission.role,
      // token: state => state.user.token
    }),
    getAuthorLabel() {
      return function (type) {
        if (type == 'Almanac') {
          return 'Almanac name'
        } else if (type == 'Standard') {
          return 'Standard number'
        } else if (type == 'Achievement') {
          return 'Finisher'
        } else {
          return 'Author'
        }
      }
    },
    getSourceLable() {
      return function (type) {
        if (type == 'Thesis') {
          return 'Periodical'
        } else if (type == 'Dissertation') {
          return 'Degree-granting unit'
        } else if (type == 'Meeting') {
          return 'Meeting name'
        } else if (type == 'Newspaper') {
          return 'Newspaper name'
        } else if (type == 'Almanac') {
          return 'Column'
        } else if (type == 'Book') {
          return 'Publishing house'
        } else if (type == 'Patent') {
          return 'applicant'
        } else if (type == 'Standard') {
          return 'Source'
        } else if (type == 'Achievement') {
          return 'First completion unit'
        }
      }
    },
  },
  methods: {
    //project/info的头像，目前用本地资源代替
    async loadTemporaryIcon() {
      this.temporaryIcon = (await import('../../../assets/images/map.jpg'));
    },

    async init() {
      await this.getProjectInfo()
      // await this.judgeRole(this.projectInfo);
    },

    async getProjectInfo() {
      let data = await getProjectAndUsers(this.projectId)
      this.projectInfo = data.project
      this.creator = data.creator
      if (data.memberList != null) {
        //防止出现数组修改不响应的情况
        this['memberList'] = data.memberList
      } else {
        this['memberList'] = []
      }
    },

    editProjectInfoDialog() {
      this.editProjectInfoDialogShow = true
    },

    async getAllUsers() {},

    async editProjectInfoResponse(val) {
      if (val.flag) {
        this.editProjectInfoDialogShow = false
        await this.getProjectInfo()

        this.$notify({
          title: 'Success',
          message: 'You have update the project successfully!',
          type: 'success',
        })
      }
    },

    getDate(date, dateType) {
      if (dateType == 'year') {
        return this.dateFormat(date, 'yyyy')
      } else if (dateType == 'month') {
        return this.dateFormat(date, 'yyyy-MM')
      } else {
        return this.dateFormat(date, 'yyyy-MM-dd')
      }
    },

    getCitation(val) {
      this.projectInfo.citation = val
      this.editCitation = false
    },

    dateFormat(time, format = 'yyyy-MM-dd hh:mm:ss') {
      return dateFormat(time, format)
    },
  },
  mounted() {
    this.init()
    this.loadTemporaryIcon()
  },
}
</script>

<style lang="scss" scoped>
.nocontent {
  opacity: 0.3;
  margin-left: 8px;
  margin-top: 5px;
}
.information {
  width: 100%;
  min-height: calc(100vh - 242px);
  .info-card {
    background-color: white;
    min-height: calc(100vh - 242px);
    padding: 20px 32px;
    // border: 1px solid black;
    .info-card-top {
      width: 100%;
      height: 280px;

      .info-detail {
        width: 60%;
        height: 100%;
        float: left;
        .title {
          margin-bottom: 20px;
          .prompt {
            font-size: 10px;
            font-weight: 500;
            // color:#B3D8FF
          }
        }
        .content {
          margin-bottom: 10px;
          clear: both;
          .head {
            display: flex;
            height: 20px;
            margin-bottom: 10px;
            line-height: 20px;
            .mark {
              width: 10px;
              background: #67c6a6;
              margin-right: 10px;
            }
            .line {
              height: 10px;
              border-bottom-style: solid;
              border-bottom-color: #e9e9e9;
              border-bottom-width: 0.7px;
              width: 100%;
              margin-top: 4px;
              margin-left: 7px;
            }
          }
          .tags {
            height: 30px;
            .el-tag {
              display: inline;
              font-size: 14px;
              margin: 0 5px;
            }
          }
          .text {
            text-indent: 10px;
          }
        }
      }

      .info-img {
        // width: 40%;
        // height: 100%;
        float: right;
        // border: 1px solid $btnBorder;
        .avatar-img {
          float: right;
          border: 1px solid $btnBorder;
        }
        .el-avatar {
          float: right;
          border: 1px solid $btnBorder;
        }
      }
    }

    .info-card-bottom {
      width: 100%;
      margin-top: 10px;
      word-wrap: break-word;
      word-break: normal;
      .intro {
        margin-bottom: 10px;
        .content {
          margin-top: 5px;
          // height: 110px;
          text-indent: 25px;
        }
      }
      .des {
        margin-bottom: 5px;
        .content {
          margin-top: 5px;
        }
      }

      .head {
        display: flex;
        height: 20px;
        margin-bottom: 10px;
        line-height: 20px;
        .mark {
          width: 10px;
          background: #3c85e5;
          margin-right: 10px;
        }
        .line {
          height: 10px;
          border-bottom-style: solid;
          border-bottom-color: #e9e9e9;
          border-bottom-width: 0.7px;
          width: 100%;
          margin-top: 4px;
          margin-left: 7px;
        }
      }
    }
  }

  .info-card-right {
    background-color: white;
    min-height: calc(100vh - 242px);
    padding: 20px 32px;
    // border: 1px solid black;
    .info {
      height: 32px;
      line-height: 32px;
      margin-bottom: 10px;
      display: flex;
      .mark {
        width: 10px;
        margin-right: 10px;
        background: #e3c381;
        height: 20px;
        margin-top: 6px;
      }
      .title {
        font-weight: 800;
        font-size: 20px;
      }
      .add-participant {
        margin-left: auto;
        .el-button {
          background-color: $contain2Background;
          color: $normalFontColor;
          font-weight: 400;
          font-family: inherit;
        }
        .el-button:hover {
          color: $normalFontColor;
          border-color: $btnBorder;
          background-color: $btnHoverBg;
        }
      }
      .edit {
        margin-left: auto;
        .el-button {
          background-color: $contain2Background;
          color: $normalFontColor;
          font-weight: 400;
          font-family: inherit;
        }
        .el-button:hover {
          color: $normalFontColor;
          border-color: $btnBorder;
          background-color: $btnHoverBg;
        }
      }
    }

    .citation {
      .content {
        margin-top: 5px;
        height: 200px;
      }
    }
  }
}
</style>

<template>
  <div class="container">
    <div class="top">
      <div class="top-container">
        <div class="backgroud">
          <picture class="bg-img">
            <img src="@/assets/images/orange1.jpg" />
          </picture>
        </div>
        <el-row class="top-info">
          <el-row class="top-title">Models</el-row>
          <el-row class="top-desc">Various, useful Models.</el-row>
          <el-row class="top-desc">Collected by generous community of OpenGMS Team. 🎁</el-row>
          <el-row class="input-container">
            <el-input
              @keyup.enter="searchData"
              v-model="value"
              placeholder=""
            >
            <template v-slot:prepend>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          </el-row>
          <el-row class="search-note"
            >Trending searches: Geodynamics,Geostatics,Hydrology,Coastal
            Vulnerability,Urban Noise</el-row
          >
          <el-row>
            <el-button plain class="add-btn" @click="addModelDialogShow = true"
              >Add your model service ➔</el-button
            >
          </el-row>
        </el-row>
      </div>
    </div>
    <div style="text-align: center">
      <el-pagination
        layout="prev, pager, next, jumper"
        :total="total"
        small
        :pager-count="5"
        :page-size="20"
        :current-page="1"
        @current-change="currentChange"
      ></el-pagination>
    </div>

    <div class="main">
      <div>
        <el-row>
          <el-col :span="6" v-for="(item, index) in data" :key="index">
            <div class="main-container">
              <el-card class="box-card" v-if="item.type == 'service'">
                <el-row>
                  <el-col :span="4">
                    <img :src="imgPath(item.snapshot, item.name)" />
                  </el-col>
                  <el-col :span="20">
                    <div class="content">
                      <h3 class="title" :title="item.name">{{ item.name }}</h3>
                      <p class="desc" :title="item.description">
                        {{ item.description }}
                      </p>
                    </div>
                    <div>
                      <el-button
                        class="config-btn"
                        type="success"
                        @click="
                          operateModel(item.id, judgeModelIsContained(item.id))
                        "
                        :icon="
                          judgeModelIsContained(item.id)
                            ? 'Remove'
                            : 'ShoppingCart'
                        "
                        circle
                      />
                    </div>
                  </el-col>
                </el-row>

              </el-card>
            </div>
          </el-col>
        </el-row>
      </div>
    </div>

    <!-- add model -->
    <el-dialog
      title="Add model in Reproducibilty"
      v-model="addModelDialogShow"
      width="40%"
      :close-on-click-modal="false"
    >
      <create-model></create-model>
    </el-dialog>
  </div>
</template>

<script>
import { updateUsersModel, getModelsByPrivacy, getUser } from '@/api/request'
// import { addModelByMD5Local } from "@/api/request";
// import serviceCard from "_com/Cards/ModelCardInPortal.vue";
import createModel from './create.vue'
import { imgBase64 } from '@/lib/utils'
export default {
  components: { createModel },
  // setup(){
  //   const 
  // },
  data() {
    return {
      data: [],
      is_extending: false,
      value: '',
      pageFilter: {
        pageSize: 20,
        page: 0,
      },
      addModelDialogShow: false,
      total: 0,
      // currentPage: 1,
      modelList: [],
      selectedModels: [],
      currentUser: {},
    }
  },
  computed: {
    noMore() {
      return this.count >= 20
    },
    disabled() {
      return this.loading || this.noMore
    },
  },
  methods: {
    imgPath(snapshot, name) {
      if (snapshot != undefined) {
        return snapshot
      } else {
        return imgBase64(name)
      }
    },
    async operateModel(id, isContained) {
      let newarray = []
      if (isContained) {
        //remove
        newarray = this.selectedModels.filter((item) => item != id)
      } else {
        if (this.selectedModels.length == 0) {
          newarray = this.selectedModels = [id]
        } else {
          newarray = this.selectedModels.concat(id)
        }
      }
      let data = await updateUsersModel(newarray)
      this.selectedModels = data.modelList
      console.log('newnew', newarray, data)
    },

    async getPublicModels(val) {
      let data = await getModelsByPrivacy({
        privacy: 'public',
        currentPage: val,
        pageSize: 20,
        key: '',
      })
      this.total = data.totalElements
      this.data = data.content
      console.log(this.data,'111');
    },

    judgeModelIsContained(modelId) {
      return this.selectedModels.some((select) => select == modelId)
    },
    async init() {
      this.currentUser = await getUser()
      if (
        Object.prototype.hasOwnProperty.call(this.currentUser, 'modelList') &&
        this.currentUser.modelList != null
      ) {
        this.selectedModels = this.currentUser.modelList
      } else {
        this.selectedModels = []
      }
      await this.getPublicModels(0)
    },
    async currentChange(val) {
      this.data = (
        await getModelsByPrivacy({
          privacy: 'public',
          currentPage: val - 1,
          pageSize: 20,
          key: '',
        })
      ).content
    },
    async searchData() {
      let params = {
        page: 0,
        pageSize: 20,
        searchText: this.value,
      }

      let data = await getModelsByPrivacy({
        privacy: 'public',
        currentPage: params.page,
        pageSize: params.pageSize,
        key: this.value,
      })
      this.total = data.totalElements
      this.data = data.content
      this.$forceUpdate()
    },
  },
  mounted() {
    this.init()
  },
}
</script>

<style lang="scss">
.container {
  .top {
    // background-color: #000;
    color: #fff;
    //   margin-bottom: 48px;
    .top-container {
      position: relative;
      .bg-img {
        position: relative;
        img {
          position: relative;
          object-fit: cover;
          //obackground-size: cover;
          width: 100%;
          height: calc(100vh - 500px);
          filter: brightness(0.8);
        }
      }
    }

    .top-info {
      display: flex;
      flex-direction: column;
      position: absolute;
      left: 50%;
      top: 50%;
      width: 45%;
      transform: translate3d(-50%, -50%, 0);
      font-size: 1.4rem;
      font-family: Arial, Georgia, Times, 'Times New Roman', serif;
      .el-row {
        margin: 0.5rem 0;
        margin-bottom: 3px;
      }
      .top-title {
        font-size: 3.5rem;
      }
      .input-container {
        .el-input {
          .el-input__inner {
            height: 50px;
          }
        }
      }
      .el-button {
        background: rgba($color: #fff, $alpha: 0.5);
        color: #fff;
        font-weight: 700;
        font-size: 16px;
        height: 50px;
        margin-top: 10px;
        //   border: 1px solid rgba($color: #fff, $alpha: 0.5);
      }
      .el-button:hover {
        border: 1.5px solid $selectOrangeBorderColor;
        color: $selectOrangeBorderColor;
      }
    }
  }

  .main {
    position: relative;
    .main-container {
      // p {
      //   position: relative;
      //   display: flex;
      //   justify-content: center;
      //   flex-wrap: wrap;
      //   overflow: hidden;
      // }
      .box-card {
        margin: 10px 2%;
        width: 95%;
        transition: box-shadow 0.1s ease;

        height: 120px;
        /* 相对定位 */
        position: relative;
        :deep(.el-card__body) {
          padding: 10px;
        }
        .content {
          padding: 5px;
          word-wrap: break-word;
          width: 86%;
          min-height: 40px;
          max-height: 40px;
          .title,
          .desc {
            max-height: 40px;
            display: -webkit-box;
            -webkit-box-orient: vertical;
            -webkit-line-clamp: 2;
            overflow: hidden;
            font-size: 8px;
          }
        }

        // &:hover {
        //   box-shadow: 0px 0px 20px #666;
        // }
        img {
          width: 100%;
          object-fit: cover;
          height: 100px;
        }
        .config-btn {
          margin-bottom: 0;
          height: 40px;
          /* 绝对定位 */
          position: absolute;
          bottom: 10px;
          right: 10px;
          // position: relative; //将button按钮固定在页面底部，注意，：和；是英文的哦，一定不要写成中文哦。
          // bottom: 5px;
          // float: ;
          // margin: 0px 0px;
          // padding: 10px;
        }
      }
    }
  }
}
</style>

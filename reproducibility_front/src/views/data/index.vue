<template>
  <div class="container">
    <div class="top">
      <div class="top-container">
        <div class="backgroud">
          <picture class="bg-img">
            <img src="@/assets/images/orange2.jpg" />
          </picture>
        </div>
        <el-row class="top-info">
          <el-row class="top-title">DataServices</el-row>
          <el-row class="top-desc">Various, useful DataServices.</el-row>
          <el-row class="top-desc">Collected by generous community. 🎁</el-row>
          <el-row class="input-container">
            <el-input
              @keyup.enter="searchData"
              v-model="value"
              placeholder=""
              :prefix-icon="ElIconSearch"
            ></el-input>
          </el-row>
          <el-row class="search-note"
            >Trending searches: DEM,DOM,DLG,DSM.</el-row
          >
          <el-row>
            <el-button plain class="add-btn" @click="addModelDialogShow = true"
              >Add your data service ➔</el-button
            >
          </el-row>
        </el-row>
      </div>
    </div>

    <div class="main">
      <div class="main-container">
        <el-row
          class="infinite-list"
          v-infinite-scroll="extendData"
          infinite-scroll-disabled="disabled"
          style="overflow: auto"
        >
          <el-col :span="4" v-for="(item, index) in data" :key="index">
            <data-service-card :item="item"></data-service-card>
          </el-col>
        </el-row>
      </div>
    </div>
    <div>
      <DataTable></DataTable>
    </div>

    <!-- add model -->
    <el-dialog
      title="Add model in Reproducibilty"
      v-model="addModelDialogShow"
      width="40%"
      :close-on-click-modal="false"
    >
      <el-form :model="form">
        <el-form-item label="Name">
          <el-input v-model="form.name" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="数据描述">
          <el-input v-model="form.description" autocomplete="off"></el-input>
        </el-form-item>

        <el-form-item label="上传文件">
          <el-upload
            class="upload-demo"
            ref="uploads"
            action="/api/data_service/addByFileToPublic"
            :data="this.form"
            :limit="1"
            :on-success="upFile"
            :file-list="fileList"
            :auto-upload="false"
          >
            <template v-slot:trigger>
              <el-button size="small" type="primary">Select File</el-button>
            </template>
          </el-upload>
        </el-form-item>
      </el-form>
      <template v-slot:footer>
        <div class="dialog-footer">
          <el-button @click="dialogFormVisible = false">取 消</el-button>
          <el-button type="primary" @click="submitForm()">确 定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { Search as ElIconSearch } from '@element-plus/icons'
import { getDataServices } from '@/api/request'
import dataServiceCard from './components/DataServiceCard.vue'
import DataTable from './components/ResourceContent.vue'

export default {
  data() {
    return {
      data: [],
      is_extending: false,
      value: '',
      pageFilter: {
        pageSize: 8,
        page: 0,
      },
      form: {
        name: '',
        description: '',
        // resourceUrl: "",
      },
      fileList: [],
      addModelDialogShow: false,
      ElIconSearch,
    }
  },
  components: { DataTable, dataServiceCard },
  computed: {
    noMore() {
      return this.count >= 20
    },
    disabled() {
      return this.loading || this.noMore
    },
  },
  methods: {
    upFile() {
      this.dialogFormVisible = false
      this.searchData()
    },

    searchData() {
      this.pageFilter.page = 0
      this.getData()
    },

    async getData() {
      let { content } = await getDataServices(
        this.pageFilter.page,
        this.pageFilter.pageSize
      )
      // let { content } = await get(
      //   `/modelItems/${this.pageFilter.page}/${this.pageFilter.pageSize}`
      // );
      //   console.log(content);
      if (content.length == 0) {
        this.is_extending = false
        return
      } else {
        this.data = content
        this.is_extending = true
      }
    },

    extendData() {
      this.pageFilter.page++
      this.getData()
    },

    addModelItem() {},
  },
  mounted() {
    this.getData()
    // window.addEventListener('scroll', this.scrollDown);
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
          height: calc(100vh - 800px);
          filter: brightness(0.8);
        }
      }
    }

    .top-info {
      position: absolute;
      left: 50%;
      top: 50%;
      width: 45%;
      transform: translate3d(-50%, -50%, 0);
      font-size: 1.4rem;
      font-family: Arial, Georgia, Times, 'Times New Roman', serif;
      .el-row {
        margin: 0.5rem 0;
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
      p {
        position: relative;
        display: flex;
        justify-content: center;
        flex-wrap: wrap;
        overflow: hidden;
      }
    }
  }

  .list-enter-active,
  .list-leave-active {
    transition: all 1s;
  }
  .list-enter,
  .list-leave-to {
    opacity: 0;
    transform: translateY(30px);
  }
}
</style>

<template>
  <div>
    <div class="top">
      <div class="top-container">
        <div class="backgroud">
          <picture class="bg-img">
            <img src="../../../assets/images/bg_data_process.jpg" />
          </picture>
        </div>
        <el-row class="top-info">
          <el-row class="top-title">
            Data Resource
          </el-row>
          <el-row class="top-desc">
            Various, useful Data Resource.
          </el-row>
          <el-row class="top-desc">
            Collected by generous community of OpenGMS Team. 🎁
          </el-row>
          <el-row class="input-container">
            <el-input
              @keyup.enter.native="searchData"
              v-model="value"
              placeholder=""
              prefix-icon="el-icon-search"
            ></el-input>
          </el-row>
          <el-row class="search-note">
            Trending searches: DEM,DOM,DLG,DSM.
          </el-row>
        </el-row>
      </div>
    </div>
    <div class="main">
      <el-button
        type="primary"
        icon="el-icon-add"
        circle
        @click="dialogFormVisible = true"
        >+</el-button
      >
      <div class="main-container">
        <transition-group name="list" tag="p">
          <ServiceCard
            :item="item"
            type="data"
            v-for="item in data"
            :key="item.id"
          ></ServiceCard>
        </transition-group>
      </div>
    </div>
    <el-dialog title="Upload Data Service" :visible.sync="dialogFormVisible">
      <el-form :model="form">
        <el-form-item label="数据名称">
          <el-input v-model="form.name" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="数据描述">
          <el-input v-model="form.description" autocomplete="off"></el-input>
        </el-form-item>
        <!-- <el-form-item label="数据DOI">
          <el-input v-model="form.resourceUrl" autocomplete="off"></el-input>
        </el-form-item> -->
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
            <el-button slot="trigger" size="small" type="primary"
              >选取文件</el-button
            >
          </el-upload>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitForm()">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { get } from "@/axios";
import ServiceCard from "_com/common/ServiceCard";
export default {
  data() {
    return {
      data: [],
      is_extending: true,
      value: "",
      dialogFormVisible: false,
      dataFilter: {
        pageSize: 8,
        page: 0,
      },
      form: {
        name: "",
        description: "",
        // resourceUrl: "",
      },
      fileList: [],
    };
  },
  methods: {
    searchData() {
      this.dataFilter.page = 0;
      this.getData(false);
    },

    submitForm() {
      if (
        this.form.name == "" ||
        this.form.description == ""
        // ||        this.form.resourceUrl == ""
      ) {
        return;
      }
      this.form.isIntermediate = false;
      this.$msgbox
        .confirm("确定保存?", "提示信息", {
          cancelButtonText: "取消",
          confirmButtonText: "确定",
          type: "warning",
        })
        .then(() => {
          //进行文件上传
          this.$refs.uploads.submit();
        });
    },
    upFile() {
      this.dialogFormVisible = false;
      this.searchData();
    },

    async getData(extend = true) {
      try {
        let { content } = await get(
          "/data_service",
          {
            page: this.dataFilter.page,
            pageSize: this.dataFilter.pageSize,
            asc: true,
            value: this.value,
          },
          null,
          false
        );
        if (extend) {
          if (content.length == 0) {
            this.is_extending = false;
            return;
          }
          this.data = this.data.concat(content);
        } else {
          this.data = content;
        }
        this.is_extending = true;
      } catch (error) {
        this.$throw(error);
      }
    },
    extendData() {
      this.dataFilter.page++;
      this.getData();
    },
    scrollDown() {
      let scroll =
        document.documentElement.scrollTop || document.body.scrollTop;
      if (
        scroll + window.innerHeight + 20 >=
        document.documentElement.offsetHeight
      ) {
        if (this.is_extending) {
          this.is_extending = false;
          this.extendData();
        }
      }
    },
  },
  mounted() {
    this.getData();
    window.addEventListener("scroll", this.scrollDown);
  },
  components: { ServiceCard },
};
</script>

<style lang="scss">
.top {
  // background-color: #000;
  color: #fff;
  margin-bottom: 48px;

  .top-container {
    position: relative;

    .bg-img {
      position: relative;

      img {
        position: relative;
        object-fit: cover;
        //obackground-size: cover;
        width: 100%;
        height: 550px;
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
    font-family: Arial, Georgia, Times, "Times New Roman", serif;

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
  }
}

.main {
  position: relative;
}

.main-container {
  p {
    position: relative;
    display: flex;
    justify-content: center;
    flex-wrap: wrap;
    overflow: hidden;
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
</style>

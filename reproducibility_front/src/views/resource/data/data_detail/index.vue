<template>
  <div class="detail-container">
    <banner text="Data Service Details"></banner>
    <el-row class="content">
      <el-row>
        <el-col :span="19">
          <span class="service-name"> Name:{{ service.name }} </span></el-col
        >
        <el-col :span="4" :offset="1">
          <el-button style="margin-top: 5px;" @click="donwload()"
            >donwload</el-button
          >
        </el-col>
      </el-row>
      <el-row>
        <span v-for="(tag, index) of service.tags" :key="index" class="badge">{{
          tag
        }}</span>
      </el-row>

      <el-row class="abstract">
        <span class="label">Description</span>
        <el-row>
          <span class="info">
            {{ service.description }}
          </span>
        </el-row>
      </el-row>
      <el-row class="abstract">
        <span class="label">DOI</span>
        <el-row>
          <span class="info">
            <span id="learn-more">
              <a
                target="_blank"
                :href="
                  service.resourceUrl
                    ? service.resourceUrl
                    : 'http://geomodeling.njnu.edu.cn/'
                "
                >{{ service.resourceUrl }}</a
              >
            </span>
          </span>
        </el-row>
      </el-row>
    </el-row>
  </div>
</template>

<script>
import { get } from "@/axios";
import Banner from "_com/common/Banner";
import config from "@/config";
export default {
  data() {
    return {
      activeName: "first",
      id: this.$route.params.id,
      type: this.$route.params.type,
      service: {},
    };
  },
  methods: {
    donwload() {
      window.open(`${config.containerURL}/data_service/fetch/${this.id}`);
    },
    async getData() {
      try {
        let res = await get("/data_service/{id}", null, {
          id: this.id,
        });
        this.service = res;
      } catch (error) {
        this.$throw(error);
      }
    },
  },
  mounted() {
    this.getData();
  },
  components: {
    Banner,
  },
};
</script>

<style lang="scss" scoped>
.card {
  border: solid 1px #ccc;
  display: inline-block;
  padding: 1.5rem 2rem;
  border-radius: 6px;
  box-shadow: 2px 2px 2px rgba(122, 121, 121, 0.867);
  margin: 4px 10px;
}

.badge {
  border: 1px solid #323232;
  border-radius: 0.875rem;
  display: inline-block;
  line-height: 1.25rem;
  margin: 0 0.375rem 0.375rem 0;
  padding: 0 0.5rem;
  font-size: 0.6rem;
}

.detail-container {
  font-family: Arial, Georgia, sans-serif, serif;

  .content {
    font-family: Georgia, sans-serif, serif;
    margin-left: 5%;
    margin-right: 5%;

    .el-row {
      line-height: 1.8rem;
    }

    .creator {
      font-family: sans-serif, serif;
    }

    .service-name {
      font-size: 1.5rem;
      line-height: 3rem;
    }

    .abstract {
      border-top: solid 1px rgb(206, 206, 206);
      margin: 1rem 0 2rem;
      padding-top: 1rem;

      .label {
        display: inline-block;
        font-size: 1.4rem;
        margin: 0.5rem 0;
      }

      .info {
        display: inline-block;
        text-align: justify;
      }

      .learn-more {
        margin-left: 1rem;
        display: inline-block;
      }
    }
  }

  .config-btn {
    float: right;
    margin: 0px 20px;
  }

  .tabs-panel {
    min-height: 300px;
  }
}
</style>

<template>
  <div class="main">
    <el-row>
      <el-page-header
        @back="goBack"
        content="Computational processes"
        class="header_back"
      >
      </el-page-header>
    </el-row>
    <el-row>
      <el-col :span="5">
        <el-row>
          <h1>Model list</h1>

          <div
            class="infinite-list-wrapper"
            style="overflow:auto; margin:40px 30px 0 0
            "
          >
            <ul class="list">
              <li
                v-for="modelservice in modelServices"
                :key="modelservice.id"
                :title="modelservice.description"
                class="list-item"
                @click="show(modelservice, 'MODEL')"
              >
                {{ modelservice.name }}
              </li>
            </ul>
          </div>
        </el-row>

        <el-row> </el-row>
      </el-col>
      <el-col :span="19" v-if="choosenService != null">
        <config
          :service="choosenService"
          :type="choosenServiceType"
          :dataServices="dataServices"
        ></config>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import config from "./components/config.vue";
import { get } from "@/axios";

export default {
  data() {
    return {
      id: this.$route.params.id,
      choosenService: null,
      choosenServiceType: "",
      modelServices: [],
      dataServices: {},
      // dataProcessServices: [],
    };
  },
  methods: {
    goBack() {
      this.$emit("getPageFlag", "echarts");
    },
    async load() {
      let { modelServices } = await get("/g2s/{id}/computeServices", null, {
        id: this.id,
      });
      // debugger;
      modelServices.forEach((modelService) => {
        if (modelService.behavior.parameters != null) {
          modelService.behavior.parameters.forEach((parameter) => {
            if (
              parameter.defaultValue != null ||
              parameter.defaultValue != undefined
            ) {
              parameter.value = parameter.defaultValue;
            }
          });
        }
      });
      this.modelServices = modelServices;

      this.choosenService = this.modelServices[0];
      this.choosenServiceType = "MODEL";
      this.getDataServices();
    },
    show(choosen, type) {
      this.choosenService = {};
      this.type = "";
      this.$set(this, "choosenService", choosen);
      this.$set(this, "choosenServiceType", type);
      this.getDataServices();
    },
    async getDataServices() {
      let id = this.$route.params.id;
      let dataServices = await get("/g2s/{id}/dataServices", null, {
        id,
      });
      this.dataServices = dataServices;
    },
  },
  mounted() {
    // this.load();
  },
  components: { config },
};
</script>

<style scoped>
.infinite-list-wrapper {
  height: 600px;
  text-align: center;
}
.list {
  padding: 0;
  margin: 0;
  list-style: none;
}
.list-item {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 50px;
  background: #fff6f6;
  color: #ff8484;
}
>>> .el-page-header {
  float: left;
}
</style>

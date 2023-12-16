<template>
<<<<<<< Updated upstream
  <el-card style="height:150px;margin:8px -15px">
=======
  <el-card style="height: 240px; margin: 0px -15px">
>>>>>>> Stashed changes
    <div>
      <el-row style="height: 25px">
        <el-col :span="24">
          <div class="title">
<<<<<<< Updated upstream
            <a href="#" @click.prevent="reTry(cardData)">{{ cardData.name }}</a>
          </div>
        </el-col>
        <el-col :span="8">
          <div v-if="cardData.type === 'model'" style="float:right">
            <i class="iconfont icon-model" color="#daa520" />
            <span style="font-size:18px;color:#daa520">Model</span>
          </div>
          <div v-if="cardData.type === 'process'" style="float:right">
            <i class="iconfont icon-process" color="#d2691e" />
            <span style="font-size:10px;color:#d2691e">Data Process</span>
          </div>
          <div v-if="cardData.type === 'evaluation'" style="float:right">
            <i class="iconfont icon-dataanalysis" color="#d2691e" />
            <span style="font-size:10px;color:#d2691e">Evaluation</span>
=======
            <!-- <a href="#" @click.prevent="reTry(cardData)"> -->
            {{ cardData.name }}
            <!-- </a> -->
>>>>>>> Stashed changes
          </div>
        </el-col>
      </el-row>
      <el-row style="height: 140px">
        <div v-if="cardData.description != null" class="cmpItemDesc">
          {{ cardData.description }}
        </div>
        <div v-else class="noDes">No Description !</div>
      </el-row>
<<<<<<< Updated upstream
      <el-row>
        <el-col>
          <div class="info">
            <el-col :span="11">
              <i class="el-icon-user" :size="12" />
              <span style="color:#2b85e4">{{
                cardData.creator ? cardData.creator : "sunlingzhi"
              }}</span>
            </el-col>
            <el-col :span="13">
              <div style="float:right">
                <i class="el-icon-alarm-clock" :size="12" />
                <span>{{ cardData.createTime }}</span>
              </div>
            </el-col>
=======
      <el-row style="height: 20px">
        <el-col :span="4" :offset="1">
          <div style="float: right">
            <i class="iconfont icon-model" color="#daa520" />
            <span style="font-size: 18px; color: #daa520">{{
              cardData.instanceEnum
            }}</span>
          </div>
        </el-col>
        <el-col :span="19">
          <div style="float: right; margin-bottom: 0">
            <el-row>
              <el-icon><Alarm-clock /></el-icon>
              <span>{{ cardData.createTime }}</span>
            </el-row>
            <el-row>
              <el-icon><User /></el-icon>
              <span style="color: #2b85e4">{{
                cardData.executorName ? cardData.executorName : "OpenGMS Team"
              }}</span>
            </el-row>
>>>>>>> Stashed changes
          </div>
        </el-col>
      </el-row>
    </div>
  </el-card>
</template>

<script>
import config from "@/config";
export default {
  props: {
    cardData: {
      type: Object,
    },
  },
  methods: {
    // reTry(instance) {
    //   let projectId = this.$route.params.id;
    //   if (instance.instanceEnum === "MODEL") {
    //     this.userFeedback(instance.modelId);
    //   } else {
    //   }
    // },
    // // 路由跳转新界面
    // userFeedback(val) {
    //   console.log(this.$router.currentRoute.value.name);
    //   const { href } = this.$router.resolve({
    //     name: "R_Reproduction",
    //     path: `/project/r/${this.$route.params.id}/reproduction/`,
    //     query: {
    //       modelId: val,
    //     },
    //   });
    //   window.open(href, "_blank");
    // },
  },
};
</script>

<style>
.title {
  /* height: 3em; */
  overflow: hidden;
  /* text-overflow: ellipsis; */
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}
.title a {
  color: #2d8cf0;
  text-decoration: none;
  font-weight: 600;
  font-size: 20px;
  line-height: 1em;
}
#bottom-info {
  /* display: flex; */
  margin-top: 5px;
  justify-content: space-between;
}

.info {
  margin-right: 10px;
  font-size: 12px;
}
.cmpItemDesc {
  height: 80px;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 4;
  -webkit-box-orient: vertical;
  font-size: 12px;
  text-overflow: ellipsis;
  padding: 5px 0;
  word-break: break-all;
}
.noDes {
  color: #d8d8d8;
  height: 80px;
  text-align: center;
  font-size: 20px;
  font-weight: 600;
  padding-top: 8%;
}
</style>

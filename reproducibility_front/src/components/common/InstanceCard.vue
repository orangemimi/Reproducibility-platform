<template>
  <el-card
    :class="invokingType == 'reproduction' ? 'instanceCard2' : 'instanceCard'"
    shadow="never"
  >
    <div>
      <el-row style="height: 30px">
        <el-col :span="24">
          <div class="title">
            <!-- <a href="#" @click.prevent="reTry(cardData)"> -->
            <i style="font-weight: 600; font-size: 18px; color: #2b85e4"
              >Step {{ stepIndex }} :</i
            >

            {{ cardData.name }}
            <!-- </a> -->
          </div>
        </el-col>
      </el-row>
      <el-row style="height: 110px">
        <div v-if="cardData.description != null" class="cmpItemDesc">
          {{ cardData.description }}
        </div>
        <div v-else class="noDes">No Description !</div>
      </el-row>
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
              <span>{{
                cardData.executorName ? cardData.executorName : "OpenGMS Team"
              }}</span>
            </el-row>
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
    stepIndex: {
      type: Number,
    },
    invokingType: {
      type: String,
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

<style lang="scss" scoped>
.instanceCard {
  height: 200px;
  margin: 0px 5px 5px 5px;
  background-color: #eef8fde1;
  // padding: 0 10px;
  :deep(.el-card__body) {
    padding: 10px;
  }
}
.instanceCard2 {
  height: 200px;
  margin: 0px 5px 5px 5px;
  background-color: #f9f9f5;
  // padding: 0 10px;
  :deep(.el-card__body) {
    padding: 10px;
  }
}
.title {
  overflow: hidden;
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
  margin-top: 5px;
  justify-content: space-between;
}
.info {
  float: right;
  margin-right: 10px;
  font-size: 12px;
}
.cmpItemDesc {
  height: 115px;
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

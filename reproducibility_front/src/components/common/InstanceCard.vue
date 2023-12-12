<template>
  <el-card style="height: 200px; margin: 8px -15px">
    <div>
      <el-row>
        <el-col :span="16">
          <div class="title">
            <a href="#" @click.prevent="reTry(cardData)">{{ cardData.name }}</a>
          </div>
        </el-col>
        <el-col :span="8">
          <div v-if="cardData.type === 'model'" style="float: right">
            <i class="iconfont icon-model" color="#daa520" />
            <span style="font-size: 18px; color: #daa520">Model</span>
          </div>
          <div v-if="cardData.type === 'process'" style="float: right">
            <i class="iconfont icon-process" color="#d2691e" />
            <span style="font-size: 10px; color: #d2691e">Data Process</span>
          </div>
          <div v-if="cardData.type === 'evaluation'" style="float: right">
            <i class="iconfont icon-dataanalysis" color="#d2691e" />
            <span style="font-size: 10px; color: #d2691e">Evaluation</span>
          </div>
        </el-col>
      </el-row>
      <el-row>
        <div v-if="cardData.description != null" class="cmpItemDesc">
          {{ cardData.description }}
        </div>
        <div v-else class="noDes">No Description !</div>
      </el-row>
      <div style="display: flex; flex-direction: column-reverse">
        <el-row>
          <el-col :span="10">
            <el-icon><el-icon-user /></el-icon>
            <span style="color: #2b85e4">{{
              cardData.creator ? cardData.creator : "sunlingzhi"
            }}</span>
          </el-col>
          <el-col :span="14" style="text-align: right">
            <el-icon><el-icon-alarm-clock /></el-icon>
            <span>{{ cardData.createTime }}</span>
          </el-col>
        </el-row>
      </div>
    </div>
  </el-card>
</template>

<script>
import {
  User as ElIconUser,
  AlarmClock as ElIconAlarmClock,
} from "@element-plus/icons-vue";
import config from "@/config";
export default {
  components: {
    ElIconUser,
    ElIconAlarmClock,
  },
  props: {
    cardData: {
      type: Object,
    },
  },
  methods: {
    reTry({ type, id, serviceId }) {
      let projectId = this.$route.params.id;
      console.log(projectId);
      // debugger;
      if (type === "evaluation") {
        window.open(
          `${config.tomcatURL}/evaluation/#/gist/${serviceId}`,
          "_blank"
        );
      } else {
        this.$router.push({
          path: `/resource/${serviceId}/${type}/${projectId}/invokeCurrent?instanceId=${id}`,
        });
      }
    },
  },
};
</script>

<style>
:deep(.el-card__body) {
  width: 200px;
}
.title {
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
}
.title a {
  color: #2d8cf0;
  text-decoration: none;
  font-weight: 600;
  font-size: 18px;
  line-height: 1em;
}
#bottom-info {
  margin-top: 5px;
  justify-content: space-between;
}
.info {
  float: right;
  margin-right: 10px;
  font-size: 16px;
}
.cmpItemDesc {
  height: 115px;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 4;
  -webkit-box-orient: vertical;
  font-size: 18px;
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

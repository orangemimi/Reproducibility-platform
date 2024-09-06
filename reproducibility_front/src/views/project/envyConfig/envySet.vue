<template>
  <div class="envySet">
    <el-row :gutter="25" class="envyCardContainer">
      <el-col :span="8" class="envyCol">
        <el-card class="envyCard" @click="goTo('py38')"> py38 </el-card>
      </el-col>
      <el-col :span="8" class="envyCol">
        <el-card class="envyCard" @click="goTo('py38')"> matlab </el-card>
      </el-col>
      <el-col :span="8" class="envyCol">
        <el-card class="envyCard" @click="goTo('py38')"> R </el-card>
      </el-col>
      <el-col :span="8" class="envyCol">
        <el-card class="envyCard" @click="envySelection">
          overLineLayoutTest
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from "vue-router";
// @ts-ignore
import { getPython38DockerFile } from "@/api/request.js";

const envySelection = () => {
  alert("test");
};

const router = useRouter();
const goTo = (page: string) => {
  // 生成dockerfile
  getPython38DockerFile();

  // 如果想要拼凑ip：端口/#/envy/page就用这样的，但是路由没写好
  // let route = "/envy/" + page;
  let route = "/" + page;
  router.push(route);
};
</script>

<style scoped lang="scss">
.envySet {
  width: 60%;
  min-height: 60vh;
  margin: 20vh 20vw;
  box-shadow: 0 5px 12px 0 rgba(0, 0, 0, 0.3);
  display: flex;
  justify-content: center;
  .envyCardContainer {
    display: flex;
    justify-content: flex-start;
    align-content: flex-start;
    overflow: auto;
    width: 96%;
    .envyCol {
      margin: 2vh 0;
      .envyCard {
        cursor: pointer;
      }
    }
  }
}
</style>
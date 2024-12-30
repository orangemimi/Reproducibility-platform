<template>
  <div class="integrateContainer">
    <div class="flowCanvas">
      <workFlow @addInfo="addInfo"></workFlow>
    </div>
    <div class="executionInfo">
      <div class="consoler">
        consoles
        <el-button type="warning" round @click="clearConsoler">Clear</el-button>
      </div>
      <div class="consoleContent">
        <div v-for="(message, index) in messages" :key="index">
          {{ message }}
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
// @ts-ignore
import workFlow from "./integrateFlow.vue";
import { ref } from "vue";

const messages = ref(["--The relevant information will be printed here."]);

const addInfo = (info) => {
  messages.value.push(info);
};

const clearConsoler = () => {
  messages.value = ["--The relevant information will be printed here."];
};
</script>

<style scoped lang="scss">
.baseStyle {
  width: 100%;
  height: 100%;
}

.integrateContainer {
  display: flex;
  width: 100%;
  height: 100%;
}
.modelList {
  width: 20%;
  height: 100%;

  background: linear-gradient(
      45deg,
      transparent 33.33%,
      rgba(57, 144, 179, 0.1) 33.33%,
      rgba(0, 0, 0, 0.1) 66.66%,
      transparent 66.66%
    ),
    lightblue;
  background-size: 20px 20px;
}
.flowCanvas {
  width: 75%;
  height: 100%;
}
.executionInfo {
  width: 25%;
  height: 100%;
  display: flex;
  flex-direction: column;
  background-color: rgb(244, 244, 244);
  border-radius: 10px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
  .consoler {
    display: flex;
    justify-content: space-between;
    align-items: center;
    background-color: #e0e0e0;
    padding: 15px 20px;
    border-radius: 10px 10px 0 0;
    font-size: 20px; /* 字体大小 */
    font-weight: bold; /* 字体粗细 */
  }
  .consoleContent {
    padding: 20px 10px;
    // width: 100%;
    // height: 80%;
    flex: 1 1 auto;

    overflow-y: auto;
    white-space: pre-wrap; /* 保留换行符并自动换行 */
    word-break: break-word; /* 在长单词或URL中间进行换行 */
  }
}
</style>
<template>
  <div class="integrateContainer">
    <div class="flowCanvas">
      <workFlow></workFlow>
    </div>
    <div class="executionInfo">
      <div class="console">console</div>
      <div class="content">这是内容板块</div>
    </div>
  </div>
</template>

<script setup lang="ts">
// @ts-ignore
import workFlow from "./integrateFlow.vue";
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
  background-color: rgb(244, 244, 244);
  border-radius: 10px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
  .console {
    background-color: #e0e0e0;
    padding: 15px 20px;
    border-radius: 10px 10px 0 0;
    font-size: 20px; /* 字体大小 */
    font-weight: bold; /* 字体粗细 */
  }
  .content {
    padding: 20px 10px;
    width: 100%;
    overflow: auto;
  }
}
</style>
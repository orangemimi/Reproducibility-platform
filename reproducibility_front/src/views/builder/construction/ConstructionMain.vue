<template>
  <div
    class="constructionContainer"
    id="container"
    @mousemove="handleMousemove($event)"
    @mousedown="handleMousedown($event)"
    @mouseup="handleMouseup($event)"
  >
    <div class="code-container" id="codeContainer">
      <div class="middle-container" id="middleContainer">
        <AIfunction />
      </div>
    </div>
    <div class="splitPane-handle-horizontal" id="splitPaneHorizontal1"></div>
    <div class="visual-container" id="visualContainer">
      <modelContent />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
// @ts-ignore
import AIfunction from "@/views/builder/AIAssistant/AIHome.vue";
import modelContent from "@/views/builder/construction/index.vue";
// import modelContent from "_com/ModelContent/index.vue";

const activeSplitPane = ref<HTMLElement | null>(null);
const containerHeight = ref(0);
const mouseActTag = ref(false);

const handleMousedown = (e: MouseEvent) => {
  refreshContainerSize();
  mouseActTag.value = true;
  if (e.target instanceof HTMLElement) {
    activeSplitPane.value = e.target;
  }
};

const handleMousemove = (e: MouseEvent) => {
  if (
    mouseActTag.value &&
    activeSplitPane.value?.id === "splitPaneHorizontal1"
  ) {
    // 减去的180是容器上方固定内容的高度
    let percentageValue = ((e.y - 180) * 100) / containerHeight.value;
    // 限制最小和最大拖动范围
    if (percentageValue < 10) percentageValue = 10;
    if (percentageValue > 95) percentageValue = 95;

    activeSplitPane.value.style.top = percentageValue + "%";
    document.getElementById("codeContainer")!.style.height =
      percentageValue + "%";
    document.getElementById("visualContainer")!.style.height =
      100 - percentageValue + "%";
  }
};

const handleMouseup = (_e: MouseEvent) => {
  mouseActTag.value = false;
};

const refreshContainerSize = () => {
  const divElement = document.getElementById("container")!;
  containerHeight.value = divElement.offsetHeight;
};
</script>

<style scoped lang="scss">
.constructionContainer {
  width: 99vw;
  height: calc(100vh - 185px);
  display: flex;
  flex-direction: column;
  position: relative;
  .code-container {
    height: 80%;
    display: flex;
    .middle-container {
      width: 100%;
    }
  }
  .visual-container {
    height: 20%;
    padding-top: 8px;
    .modelContent {
      height: 100%;
    }
  }
  .splitPane-handle-horizontal {
    background-image: url("../../../assets/images/handle.png"),
      -webkit-linear-gradient(top, #f5f5f5, #f1f1f1);
    background-repeat: no-repeat;
    background-position: center;
    background-color: #f5f5f5;
    border-color: #dcdcdc;
    border-style: solid;
    border-width: 0;
    border-top-width: 1px;
    border-bottom-width: 1px;
    cursor: row-resize;
    width: 100%;
    height: 8px;
    position: absolute;
    z-index: 10;
    left: 0;
    top: 80%;
  }
}
</style>
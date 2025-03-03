<script setup>
import { nextTick, ref, onMounted, watch } from "vue";
import { Panel, VueFlow, useVueFlow, Handle } from "@vue-flow/core";
import { Background } from "@vue-flow/background";
import Icon from "./Icon.vue";
import ProcessNode from "./ProcessNode.vue";
import AnimationEdge from "./AnimationEdge.vue";

import { initialEdges, initialNodes } from "./initial-elements.js";
import { useRunProcess } from "./useRunProcess";
import { useShuffle } from "./useShuffle";
import { useLayout } from "./useLayout";

const props = defineProps({
  vueFlowData: {
    type: Object,
    required: true,
  },
});

const emits = defineEmits(["searchModel"]);

const nodes = ref(props.vueFlowData.nodes);

const edges = ref(props.vueFlowData.links);

const cancelOnError = ref(true);

const shuffle = useShuffle();

const { graph, layout, previousDirection } = useLayout();

const { run, stop, reset, isRunning } = useRunProcess({ graph, cancelOnError });

const { fitView } = useVueFlow();

// 控制布局方向的函数
async function layoutGraph(direction) {
  await stop();

  reset(nodes.value);

  nodes.value = layout(nodes.value, edges.value, direction);

  nextTick(() => {
    fitView();
  });
}

const search = (modelName) => {
  emits("searchModel", modelName);
};

const viewDrawer = () => {
  emits("viewDrawer", true);
};

// 监听数据变化，如果传入的数据变了，马上重新渲染流程图
watch(
  () => props.vueFlowData,
  (newVal) => {
    nodes.value = newVal.nodes;
    edges.value = newVal.links;
  },
  { deep: true }
);

onMounted(() => {});
</script>

<template>
  <div class="layout-flow">
    <VueFlow
      :nodes="nodes"
      :edges="edges"
      @nodes-initialized="layoutGraph('TB')"
    >
      <!-- process的自定义模版 -->
      <template #node-model="props">
        <Handle type="target" :position="props.targetPosition" />
        <div
          class="processNode"
          @click="search(props.label)"
          style="background-color: #87cefa"
        >
          <span>{{ props.label }}</span>
        </div>
        <Handle type="source" :position="props.sourcePosition" />
      </template>

      <!-- data的自定义模版 -->
      <template #node-inputData="props">
        <Handle type="target" :position="props.targetPosition" />
        <div class="dataNode" style="background-color: #ffab2d">
          {{ props.label }}
        </div>
        <Handle type="source" :position="props.sourcePosition" />
      </template>

      <template #node-outputData="props">
        <Handle type="target" :position="props.targetPosition" />
        <div class="dataNode" style="background-color: #b9e6d3">
          {{ props.label }}
        </div>
        <Handle type="source" :position="props.sourcePosition" />
      </template>

      <template #node-intermediateData="props">
        <Handle type="target" :position="props.targetPosition" />
        <div class="dataNode" style="background-color: #ffe6d3">
          {{ props.label }}
        </div>
        <Handle type="source" :position="props.sourcePosition" />
      </template>

      <template #edge-animation="edgeProps">
        <AnimationEdge
          :id="edgeProps.id"
          :source="edgeProps.source"
          :target="edgeProps.target"
          :source-x="edgeProps.sourceX"
          :source-y="edgeProps.sourceY"
          :targetX="edgeProps.targetX"
          :targetY="edgeProps.targetY"
          :source-position="edgeProps.sourcePosition"
          :target-position="edgeProps.targetPosition"
        />
      </template>

      <Background />

      <Panel class="process-panel" position="top-right">
        <div class="layout-panel">
          <div class="checkbox-panel"><label>Layout:</label></div>
          <!-- <button v-if="isRunning" class="stop-btn" title="stop" @click="stop">
            <Icon name="stop" />
            <span class="spinner" />
          </button>
          <button v-else title="start" @click="run(nodes)">
            <Icon name="play" />
          </button> -->

          <button title="set horizontal layout" @click="layoutGraph('LR')">
            <Icon name="horizontal" />
          </button>

          <button title="set vertical layout" @click="layoutGraph('TB')">
            <Icon name="vertical" />
          </button>
          <div class="checkbox-panel"><label>Search:</label></div>
          <button title="Search results and history" @click="viewDrawer">
            <Icon name="search" />
          </button>
        </div>

        <div class="tips">Click layout to fix issues.</div>
      </Panel>
      <div class="legend">
        <div class="processNode" style="background-color: #87cefa"></div>
        <span>Model node</span>
        <div class="dataNode" style="background-color: #ffab2d"></div>
        <span>Input Data</span>
        <div class="dataNode" style="background-color: #ffe6d3"></div>
        <span>Intermediate Data</span>
        <div class="dataNode" style="background-color: #b9e6d3"></div>
        <span>Output Data</span>
      </div>
    </VueFlow>
  </div>
</template>

<style lang="scss">
@import "@vue-flow/core/dist/style.css";
@import "@vue-flow/core/dist/theme-default.css";
.layout-flow {
  /* background-color: #1a192b; */
  height: 100%;
  width: 100%;
  .legend {
    z-index: 99;
    position: relative;
    top: 20px;
    left: 50px;
    width: 140px;
    display: flex;
    flex-wrap: wrap;
    align-items: center;
    flex-direction: column;
    justify-content: center;
    span {
      font-weight: 500;
    }
    .processNode {
      margin-top: 10px;
      width: 70px;
      height: 25px;
    }
    .dataNode {
      margin-top: 10px;
      width: 70px;
      height: 25px;
    }
  }
}
.processNode {
  width: 90px;
  height: 40px;
  display: flex;
  justify-content: center;
  align-items: center;
  color: #2d3748;
  font-size: 7px;
  line-height: 8px;
  text-align: center;
  font-weight: 600;
  border: 1px solid gray;
  // border-radius: 5px;
  background-color: #b9e6d3;
  // transform: skew(20deg);
  // span {
  //   transform: skew(-20deg); /* 反向倾斜，抵消父元素的倾斜 */
  // }
}
.dataNode {
  width: 90px;
  height: 35px;
  display: flex;
  justify-content: center;
  align-items: center;
  color: #2d3748;
  font-size: 7px;
  line-height: 8px;
  text-align: center;
  font-weight: 600;
  border: 1px solid gray;
  // border-radius: 5px;
  background-color: #f4d160;
}
/* 控制连接点隐藏，不然好丑 */
.vue-flow__handle {
  border: none;
  height: unset;
  width: unset;
  background: transparent;
  font-size: 2px;
}

.process-panel,
.layout-panel {
  opacity: 0.8;
  display: flex;
  gap: 10px;
}

.process-panel {
  background-color: #2d3748;
  padding: 10px;
  border-radius: 8px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.5);
  display: flex;
  flex-direction: column;
  .tips {
    color: white;
    font-size: 10px;
  }
}

.process-panel button {
  border: none;
  cursor: pointer;
  background-color: #4a5568;
  border-radius: 5px;
  color: white;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.5);
  font-size: 16px;
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.checkbox-panel {
  display: flex;
  align-items: center;
  gap: 10px;
}

.process-panel button:hover,
.layout-panel button:hover {
  background-color: #2563eb;
  transition: background-color 0.2s;
}

.process-panel label {
  color: white;
  font-size: 16px;
}

.stop-btn svg {
  display: none;
}

.stop-btn:hover svg {
  display: block;
}

.stop-btn:hover .spinner {
  display: none;
}

.spinner {
  border: 3px solid #f3f3f3;
  border-top: 3px solid #2563eb;
  border-radius: 50%;
  width: 10px;
  height: 10px;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}
</style>

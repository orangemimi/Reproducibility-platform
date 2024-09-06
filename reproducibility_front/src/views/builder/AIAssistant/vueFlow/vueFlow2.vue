<style lang="less" scoped>
.container {
  padding: 10px;
  height: 100%;
  width: 100%;
  .tools {
    display: block;
    margin: 10px;
    height: 8%;
  }
  .myFlow {
    margin: 10px;
    height: 92%;

    :deep(.node-light) {
      background: none;
    }
    :deep(.node-dark) {
      background: #eeeeee;
    }
  }
}
</style>
<template>
  <div class="container">
    <el-row class="tools mb-4">
      <el-button type="primary" @click="resetTransform">resetting</el-button>
      <!-- <el-button type="primary" @click="updatePos">修改属性</el-button>
      <el-button type="primary" @click="toggleclass">修改样式</el-button>
      <el-button type="primary" @click="logToObject">查看属性</el-button> -->
    </el-row>

    <VueFlow fit-view-on-init class="myFlow" v-model="elements">
      <!-- <Background /> -->
      <!-- <Panel :position="PanelPosition.TopRight">
        <div>
          <label for="ishidden">
            hidden
            <input id="ishidden" v-model="isHidden" type="checkbox" />
          </label>
        </div>
      </Panel> -->
      <Controls />
    </VueFlow>
  </div>
</template>


<script lang="ts" setup name="DemoBpmn">
import "@vue-flow/core/dist/style.css";
/* import the default theme (optional) */
import "@vue-flow/core/dist/theme-default.css";
// @ts-ignore
import {
  //   Background,
  //   Panel,
  //   PanelPosition,
  Controls,
  // @ts-ignore
} from "@vue-flow/controls";
// @ts-ignore
import { VueFlow, useVueFlow } from "@vue-flow/core";
import { reactive, ref, watch } from "vue";
import { ElMessage } from "element-plus";
import { useLayout } from "./useLayout";

const props = defineProps({
  vueFlowData: {
    type: Object,
  },
});

// const data = [
//   { id: "1", type: "input", label: "Node 1", position: { x: 250, y: 5 } },
//   { id: "2", label: "Node 2", position: { x: 100, y: 100 } },
//   { id: "3", label: "Node 3", position: { x: 400, y: 100 } },
//   { id: "4", label: "Node 4" },
//   { id: "e1-2", source: "1", target: "2", animated: true },
//   { id: "e1-3", source: "1", target: "3" },
// ];

let elements = ref(props.vueFlowData);

const isHidden = ref(false);

let {
  onPaneReady,
  onNodeDragStop,
  onConnect,
  addEdges,
  setTransform,
  toObject,
  nodes,
  edges,
} = useVueFlow();

// const flow = useVueFlow({
//   nodes,
//   edges,
//   layout: {
//     type: "dagre",
//   }
// });

watch(isHidden, () => {
  nodes.value.forEach((n) => (n.hidden = isHidden.value));
  edges.value.forEach((e) => (e.hidden = isHidden.value));
});

onPaneReady(({ fitView }) => {
  fitView();
});
onNodeDragStop((e) => console.log("drag stop", e));
onConnect((params) => addEdges([params]));

// const updatePos = () => {
//   nodes.value.forEach((el) => {
//     el.position = {
//       x: Math.random() * 400,
//       y: Math.random() * 400,
//     };
//   });
// };

// const logToObject = () => {
//   ElMessage.info(JSON.stringify(toObject()));
// };
const resetTransform = () => {
  elements.value = props.vueFlowData;
  setTransform({ x: 0, y: 0, zoom: 1 });
};

const arrangeNodes = (nodes) => {
  const spacing = 0; // 节点之间的间距
  nodes.forEach((node, index) => {
    node.position = {
      x: index * spacing,
      y: index * spacing,
    };
  });
};
if (props.vueFlowData) {
  arrangeNodes(props.vueFlowData.filter((el: any) => !el.source && !el.target));
}

// const toggleclass = () =>
//   nodes.value.forEach(
//     (el) => (el.class = el.class === "node-light" ? "node-dark" : "node-light")
//   );
</script>
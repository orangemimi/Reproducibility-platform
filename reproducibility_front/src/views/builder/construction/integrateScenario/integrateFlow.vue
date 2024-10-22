<template>
  <div style="height: 100%; width: 100%; display: flex" @drop="handleDrop">
    <Sidebar v-if="!nodeEdit" :scenarioId="currentScenario.id" />
    <div v-else-if="nodeEdit" style="width: 25%; height: 100%">
      <NodeBound
        :editModelNode="editModelNode"
        :projectId="projectId"
        :nodes="nodes"
        :edges="edges"
        @nodeEdited="nodeEdited"
      />
    </div>

    <VueFlow
      :nodes="nodes"
      :edges="edges"
      :key="flowKey"
      @dragover="onDragOver"
      @dragleave="onDragLeave"
      @nodeClick="handleNodeClick"
      @edgeClick="handleEdgeClick"
      class="flowContainer"
    >
      <Controls class="flowControls" />
      <div class="flowTopBar">
        <div class="topBarItem" @click="removeNodeOrEdge">
          <font-awesome-icon icon="fa-trash" />
          <span style="margin-left: 5px">Delete</span>
        </div>
        <div class="topBarItem" @click="clear">
          <font-awesome-icon icon="fa-window-close" />
          <span style="margin-left: 5px">Clear</span>
        </div>
        <div class="topBarItem" @click="undoFlow">
          <font-awesome-icon icon="fa-undo" />
          <span style="margin-left: 5px">Undo</span>
        </div>
        <div class="topBarItem" @click="redoFlow">
          <font-awesome-icon icon="fa-repeat" />
          <span style="margin-left: 5px">Redo</span>
        </div>
        <div class="topBarItem" @click="getTasksOrder">
          <font-awesome-icon icon="diamond" />
          <span style="margin-left: 5px">Init</span>
        </div>
        <div class="topBarItem" @click="saveWorkFlowInfo">
          <font-awesome-icon icon="floppy-disk" />
          <span style="margin-left: 5px">Save</span>
        </div>
        <div class="topBarItem" @click="printInfo" style="border-right: none">
          <font-awesome-icon icon="play" />
          <span style="margin-left: 5px">Console</span>
        </div>
      </div>
      <div
        class="dropzone-background"
        :style="{
          backgroundColor: isDragOver ? '#e7f3ff' : 'transparent',
          transition: 'background-color 0.2s ease',
        }"
      >
        <Background :size="2" :gap="20" pattern-color="#BDBDBD" />

        <!-- <div class="overlay">
          <slot />
        </div> -->
      </div>

      <template #node-model="modelNodeProps">
        <el-popover class="popover" placement="top" trigger="hover">
          <template #reference>
            <ModelNode ref="modelNode" :modelNodeProps="modelNodeProps" />
          </template>
          <div>
            {{ modelNodeProps.data.description }}
          </div>
        </el-popover>
      </template>

      <template #node-data="dataNodeProps">
        <el-popover class="popover" placement="top" trigger="hover">
          <template #reference>
            <DataNode :dataNodeProps="dataNodeProps" ref="dataNode" />
          </template>
          <div>
            {{ dataNodeProps.data.description }}
          </div>
        </el-popover>
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
    </VueFlow>
  </div>
</template>

<script setup>
import { onMounted, ref, nextTick } from "vue";
// @ts-ignore
import { updateWorkflowInfo, getScenariosByProjectId } from "@/api/request.js";
import { VueFlow, useVueFlow } from "@vue-flow/core";
import { Background } from "@vue-flow/background";
import AnimationEdge from "./integrateWorkFlow/AnimationEdge.vue";
import ModelNode from "./integrateWorkFlow/ModelNode.vue";
import useDragAndDrop from "./integrateWorkFlow/useDnD.js";
import DataNode from "./integrateWorkFlow/DataNode.vue";
// @ts-ignore
import Sidebar from "./integrateWorkFlow/Sidebar.vue";
import NodeBound from "./integrateWorkFlow/NodeBound.vue";
import { Controls } from "@vue-flow/controls";
// import default controls styles
import "@vue-flow/controls/dist/style.css";
import { ElMessage } from "element-plus";
import { useRoute } from "vue-router";
// @ts-ignore
import {
  getStartNodes,
  getExecutionOrder,
  getAllTasks,
  handleDbDataEdges,
  changeNodesState,
  getEndNodes,
  getAllInputDataNodes,
  getInputNodes,
} from "./integrateWorkFlow/VueFlowUtils.js";

const route = useRoute();
const projectId = route.params.id;
const currentScenario = ref({});
const {
  onConnect,
  addEdges,
  screenToFlowCoordinate,
  onNodeDragStop,
  onNodeDoubleClick,
} = useVueFlow();
const { onDragOver, onDrop, onDragLeave, isDragOver } = useDragAndDrop();
// const { addNodes, , onNodesInitialized } = useVueFlow();
const nodes = ref([]);
const edges = ref([]);
// 这个key是无奈之举，因为哪怕通过深拷贝进行nodes数据变化，依然无法侦听到深层数据的变化，所以用key来解决
const flowKey = ref(0);
const nodeEdit = ref(false);
const editModelNode = ref({});

// 处理输入和输出节点的数据（基于连接关系与赋值关系）
const handleDataNodeByConnection = () => {
  // 1、先清除所有model节点的输入数据中type为connection的dataRelation，再清除所有输出数据的equalData
  let allModelNodes = getAllTasks(nodes.value);
  allModelNodes.forEach((modelNode) => {
    // 1.1、清除所有输出数据的equalData
    modelNode.data.behavior.outputs.forEach((output) => {
      output.equalData = [];
    });
    // 1.2、清除输入数据中type为connection的dataRelation
    modelNode.data.behavior.inputs.forEach((input) => {
      if (input.dataRelation && input.dataRelation.type == "connection") {
        delete input.dataRelation;
      }
    });
  });
  // 2、重新添加DataRelation和equalData
  edges.value.forEach((edge) => {
    if (edge.source.includes("output") && edge.target.includes("input")) {
      // 2.1、给output节点添加equalData
      let outputNode = nodes.value.find((node) => node.id === edge.source);
      let outputAffiliationNode = nodes.value.find(
        (node) => node.id === outputNode.data.affiliation
      );
      outputAffiliationNode.data.behavior.outputs.forEach((output) => {
        if (output.name == outputNode.data.label) {
          output.equalData.push(edge.target);
        }
      });
      // 2.2、给input节点添加dataRelation
      let inputNode = nodes.value.find((node) => node.id === edge.target);
      let inputAffiliationNode = nodes.value.find(
        (node) => node.id === inputNode.data.affiliation
      );
      inputAffiliationNode.data.behavior.inputs.forEach((input) => {
        if (input.name == inputNode.data.label) {
          input.dataRelation = {
            type: "connection",
            value: "",
            label: outputNode.data.label,
          };
        }
      });
    }
  });
};

// 双击model节点打开编辑面板
onNodeDoubleClick((event) => {
  if (event.node.type == "model") {
    // 重新梳理一下数据绑定关系
    handleDataNodeByConnection();

    editModelNode.value = event.node;
    nodeEdit.value = true;
  }
});

const nodeEdited = (NodesData) => {
  nodeEdit.value = false;
};

// 处理拖放事件，将新产生的节点添加到节点列表中
const handleDrop = (event) => {
  saveFlowState();
  let newNode = onDrop(event);
  nodes.value.push(newNode);
  bindToNode(newNode);
};

// 传入模型的节点，生成输入和输出数据节点
const bindToNode = (modelNode) => {
  const nodeId = modelNode.id;
  // 找到模型节点的位置
  // 将选中model的元数据绑定到模型节点
  let modelNodePosition = modelNode ? modelNode.position : { x: 0, y: 0 };
  const dataSpacing = 120; // 数据之间的水平间隔

  // 生成输入节点
  modelNode.data.behavior.inputs.forEach((input, index) => {
    const inputNodeId = `${nodeId}_input_${index}`;

    nodes.value.push({
      id: inputNodeId,
      data: {
        label: input.name,
        dataType: "input",
        isParams: input.datasetItem?.isParams,
        affiliation: modelNode.id,
        description: input.description,
        state: "init",
      },
      position: {
        x:
          modelNodePosition.x -
          (dataSpacing * (modelNode.data.behavior.inputs.length - 1)) / 2 +
          index * dataSpacing,
        y: modelNodePosition.y - 80,
      },
      type: "data",
    });

    // 连接输入节点与模型节点
    edges.value.push({
      id: `edge-${inputNodeId}-${nodeId}`,
      source: inputNodeId,
      target: nodeId,
      type: "animation",
      animated: true,
    });
  });

  // 生成输入节点
  modelNode.data.behavior.inputs.forEach((input, index) => {
    const inputNodeId = `${nodeId}_input_${index}`;

    nodes.value.push({
      id: inputNodeId,
      data: {
        label: input.name,
        dataType: "input",
        isParams: input.datasetItem?.isParams,
        affiliation: modelNode.id,
        description: input.description,
        state: "init",
      },
      position: {
        x:
          modelNodePosition.x -
          (dataSpacing * (modelNode.data.behavior.inputs.length - 1)) / 2 +
          index * dataSpacing,
        y: modelNodePosition.y - 80,
      },
      type: "data",
    });

    // 连接输入节点与模型节点
    edges.value.push({
      id: `edge-${inputNodeId}-${nodeId}`,
      source: inputNodeId,
      target: nodeId,
      type: "animation",
      animated: true,
    });
  });

  // 生成输出节点
  modelNode.data.behavior.outputs.forEach((output, index) => {
    const outputNodeId = `${nodeId}_output_${index}`;
    nodes.value.push({
      id: outputNodeId,
      data: {
        label: output.name,
        dataType: "output",
        isParams: output.datasetItem?.isParams,
        affiliation: modelNode.id,
        description: output.description,
        state: "init",
      },
      position: {
        x:
          modelNodePosition.x -
          (dataSpacing * (modelNode.data.behavior.outputs.length - 1)) / 2 +
          index * dataSpacing,
        y: modelNodePosition.y + 80,
      },
      type: "data",
    });

    // 连接模型节点与输出节点
    edges.value.push({
      id: `edge-${nodeId}-${outputNodeId}`,
      source: nodeId,
      target: outputNodeId,
      type: "animation",
      animated: true,
    });
  });
};

// 删除node或者edge
// 删除node或者edge
// 删除node或者edge
const removeEdge = ref(null);
const removeNode = ref(null);

// 一方面拖拽节点要加入待删除的栈中，另一方面拖拽后要更新节点的位置信息
// 节点拖拽停止事件
onNodeDragStop(({ event, nodes: draggedNodes, node }) => {
  saveFlowState();
  // console.log("Node Drag Stop", { event, draggedNodes, node });
  nodes.value.forEach((n) => {
    if (n.id === node.id) {
      n.position = node.position;
      removeNode.value = node;
      removeEdge.value = null;
    }
  });
});

const handleNodeClick = (event) => {
  removeNode.value = event.node;
  removeEdge.value = null;
};

const handleEdgeClick = (event) => {
  removeEdge.value = event.edge;
  removeNode.value = null;
};

const removeNodeOrEdge = () => {
  saveFlowState();
  if (removeNode.value) {
    const nodeId = removeNode.value.id;
    nodes.value = nodes.value.filter((node) => node.id !== nodeId);
    edges.value = edges.value.filter(
      (edge) => edge.source !== nodeId && edge.target !== nodeId
    );
    return;
  }
  if (removeEdge.value) {
    const edgeId = removeEdge.value.id;
    edges.value = edges.value.filter((edge) => edge.id !== edgeId);
    return;
  }
};

// 缓存、撤销与重做
// 缓存、撤销与重做
// 缓存、撤销与重做
const undoStack = ref([]);
const redoStack = ref([]);

// 在每个需要保存的操作前先保存当前的状态
const saveFlowState = () => {
  if (undoStack.value.length >= 50) {
    undoStack.value.shift(); // 删除最早的状态
  }
  undoStack.value.push({
    nodes: JSON.parse(JSON.stringify(nodes.value)),
    edges: JSON.parse(JSON.stringify(edges.value)),
  });

  redoStack.value = []; // 清空 redo 栈
};

// 撤销步骤
const undoFlow = async () => {
  if (undoStack.value.length > 0) {
    const lastState = undoStack.value.pop();
    redoStack.value.push({
      nodes: JSON.parse(JSON.stringify(nodes.value)),
      edges: JSON.parse(JSON.stringify(edges.value)),
    });
    nodes.value = JSON.parse(JSON.stringify(lastState.nodes));
    edges.value = JSON.parse(JSON.stringify(lastState.edges));
  }
};

// 重做
const redoFlow = () => {
  if (redoStack.value.length > 0) {
    const nextState = redoStack.value.pop();
    undoStack.value.push({
      nodes: JSON.parse(JSON.stringify(nodes.value)),
      edges: JSON.parse(JSON.stringify(edges.value)),
    });
    nodes.value = nextState.nodes;
    edges.value = nextState.edges;
  }
};

// 清空
const clear = () => {
  saveFlowState();
  nodes.value = [];
  edges.value = [];
};

// 打印节点和边的信息
const printInfo = () => {
  console.log("nodes.value", nodes.value);
  console.log("edges.value", edges.value);
};

// 保存工作流信息
// 保存工作流信息
// 保存工作流信息
const saveWorkFlowInfo = async () => {
  // 在这里要先统一的将数据节点的dataType重新设置一下。
  updateOutputDataType();
  // 重新确定节点的数据绑定关系
  handleDataNodeByConnection();

  const flowData = {
    nodes: nodes.value,
    edges: edges.value,
  };
  let formData = new FormData();
  formData.append("flowData", JSON.stringify(flowData));

  await updateWorkflowInfo(projectId, formData);
  ElMessage.success("保存成功");
};

// 先将所有输出节点的dataType恢复默认，再重新根据节点数
const updateOutputDataType = () => {
  nodes.value.forEach((node) => {
    if (node.data.dataType === "intermediate") {
      node.data.dataType = "output";
    }
  });
  edges.value.forEach((edge) => {
    if (edge.source.includes("output") && edge.target.includes("input")) {
      let sourceNode = nodes.value.find((node) => node.id === edge.source);
      sourceNode.data.dataType = "intermediate";
    }
  });
};

// 获取执行顺序
// 获取执行顺序
// 获取执行顺序
const startNodes = ref([]);

// 执行前初始化节点数据
const getTasksOrder = () => {
  // 先校验能否执行，暂时没写，因为没想好数据怎么绑定
  // 先校验能否执行，暂时没写，因为没想好数据怎么绑定
  // 先校验能否执行，暂时没写，因为没想好数据怎么绑定

  // 获取所有起始节点，作为广度优先遍历的起始点
  startNodes.value = getStartNodes(nodes.value, edges.value);

  // 确定输出数据与输入数据的相等关系，关系存储在nodes.data.equalData中，无返回值
  // equalData是一个数组，因为要考虑一个输出多次使用的情况。
  handleDbDataEdges(nodes.value, edges.value);

  // 确定每个待运行任务的preconditions
  nodes.value = getExecutionOrder(nodes.value, edges.value);

  // 前置工作结束，开始运行workflow
  runWorkFlow();
};

// 初始化完毕，开始运行工作流
const runWorkFlow = async () => {
  // 得到所有待运行节点
  let todoTasks = getAllTasks(nodes.value);

  // 找到第一个运行节点，开始运行
  let firstTaskId = startNodes.value[0].data.affiliation;

  let firstTask = nodes.value.find((node) => node.id === firstTaskId);
  // 初始化已完成任务数组
  let doneTasks = [];
  await executedOperation(firstTask, todoTasks, doneTasks);
};

// 执行模型任务
const executedOperation = async (currentTask, todoTasks, doneTasks) => {
  const taskName = currentTask.data.label;
  await mockRun(taskName);

  // 将当前任务加入已完成任务队列,并且从待执行任务队列中移除
  doneTasks.push(currentTask.id);
  todoTasks = todoTasks.filter((task) => task.id !== currentTask.id);

  // 所有任务都完成，自动退出
  if (!todoTasks.length) {
    console.log("该轮次所有任务已经全部顺利执行完毕");
    return;
  }

  // 遍历待执行任务，找到符合条件的下一个任务
  for (let i = 0; i < todoTasks.length; i++) {
    let task = todoTasks[i];
    let canExecute;
    if (task.data.precondition) {
      canExecute = task.data.precondition.every((preId) =>
        doneTasks.includes(preId)
      );
    } else {
      canExecute = true;
    }

    if (canExecute) {
      // 递归执行下一个任务
      await executedOperation(task, todoTasks, doneTasks);
      break;
    }
  }
};

// 临时用，模拟运行
const mockRun = async (taskName) => {
  // 现在已经有了模型的元数据，剩下的就是如何执行了
  console.log("--------------------------------------------");
  console.log(`开始运行 ${taskName}`);
  // 模拟异步操作
  setTimeout(function () {
    console.log(`${taskName} 已经运行完毕`);
  }, 1000);

  return new Promise((resolve) => {
    setTimeout(() => {
      resolve({
        message: "已经运行完毕",
        code: 200,
      });
    }, 1000);
  });
};

// 节点连接行为
onConnect((connection) => {
  let newConnection = {
    ...connection,
    type: "animation",
    animated: true,
  };
  // 将新生成的边加入到edges中
  let newEdge = {
    id: `edge-${newConnection.source}-${newConnection.target}`,
    source: newConnection.source,
    target: newConnection.target,
    type: "animation",
    animated: true,
  };
  saveFlowState();
  edges.value.push(newEdge);
});

onMounted(async () => {
  let scenarioList = await getScenariosByProjectId(projectId);
  currentScenario.value = scenarioList[0];
  if (currentScenario.value.flowData) {
    let flowData = JSON.parse(currentScenario.value.flowData);
    nodes.value = flowData.nodes;
    edges.value = flowData.edges;
  }
});
</script>

<style scoped lang="scss">
/* these are necessary styles for vue flow */
@import "@vue-flow/core/dist/style.css";
/* this contains the default theme, these are optional styles */
@import "@vue-flow/core/dist/theme-default.css";
// .vue-flow__panel {
//   display: flex;
//   flex-direction: column;
//   align-items: flex-end;
//   height: fit-content;
//   width: fit-content;
//   position: absolute;
//   top: 50px;
//   left: 50px;
// }
.flowContainer {
  height: 100%;
  width: 100%;
  position: relative;

  .flowControls {
    flex-direction: column;
    height: fit-content;
    width: fit-content;
    position: absolute;
    top: 20px;
    left: unset;
    right: 10px;
    margin: 0 20px;
    zoom: 1.2;
  }
  .flowTopBar {
    display: flex;
    position: relative;
    top: 20px;
    left: 30px;
    width: fit-content;
    height: fit-content;
    cursor: pointer;
    overflow: visible;
    background-color: #f4f4f4;
    font-size: 15px;
    z-index: 999;
    border-radius: 8px;
    .topBarItem {
      padding: 6px 14px;
      border-right: 2px #dbdee2 dashed;
      vertical-align: middle;
      overflow: visible;
      color: #5f6477;
      font-size: 16px;
      cursor: pointer;
      &:hover {
        color: #1479d7;
      }
    }
  }
}

.container-topbar .topBarItem {
  float: right;
  height: fit-content;
  padding: 0 12px;
  border-right: 2px #dbdee2 dashed;
  vertical-align: middle;
  overflow: visible;
  zoom: 1;
  color: #5f6477;
  font-size: 15px;
  cursor: pointer;
}
.container-topbar .topBarItem:hover {
  color: #1479d7;
}
</style> 
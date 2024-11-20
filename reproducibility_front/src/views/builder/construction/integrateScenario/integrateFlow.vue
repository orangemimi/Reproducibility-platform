<template>
  <div style="height: 100%; width: 100%; display: flex" @drop="handleDrop">
    <Sidebar
      v-if="!nodeEdit"
      :scenarioId="currentScenario.id"
      :containerId="currentScenario.containerId"
      :dependencies="dependencies"
      @nodeEdited="nodeEdited"
    />
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
          <span style="margin-left: 5px">Run</span>
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

      <template #node-codeModel="modelNodeProps">
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
import { onMounted, ref, defineEmits } from "vue";
// @ts-ignore
import {
  updateWorkflowInfo,
  getScenariosByProjectId,
  downloadFilesToVolume,
  savePythonCode,
  executeScript,
  uploadFilesToDataContainer,
  getFoldersByScenarioId,
} from "@/api/request.js";
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
const currentFolder = ref({});
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
const dependencies = ref([]);
const containerFiles = ref([]);
// 这个key是无奈之举，因为哪怕通过深拷贝进行nodes数据变化，依然无法侦听到深层数据的变化，所以用key来解决
const flowKey = ref(0);
const nodeEdit = ref(false);
const editModelNode = ref({});

const emit = defineEmits(["addInfo"]);

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
  if (event.node.type == "model" || event.node.type == "codeModel") {
    // 重新梳理一下数据绑定关系
    handleDataNodeByConnection();

    editModelNode.value = event.node;
    nodeEdit.value = true;
  }
});

const nodeEdited = async (operation, data) => {
  nodeEdit.value = false;
  //完成编辑，先保存
  if (operation == "save") {
    await saveWorkFlowInfo("silent");
    ElMessage.success("Save model configuration successfully");
  } else if (operation == "generateNodes") {
    // 此时data是modelNode
    bindToNode(data);
  } else if (operation == "saveDependencies") {
    await saveWorkFlowInfo("silent");
  } else if (operation == "upperInfo") {
    emit("addInfo", data);
  }
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

  // 保留模型节点，删除旧的输入和输出节点
  nodes.value = nodes.value.filter((node) => {
    return !(node.id !== nodeId && node.id.includes(nodeId));
  });
  // 删除旧的相关边
  edges.value = edges.value.filter((edge) => !edge.id.includes(nodeId));

  // 找到模型节点的位置
  // 将选中model的元数据绑定到模型节点
  let modelNodePosition = modelNode ? modelNode.position : { x: 0, y: 0 };
  const dataSpacing = 120; // 数据之间的水平间隔

  let inputsAndParametersIndex = 0;
  // 生成输入节点
  modelNode.data.behavior.inputs.forEach((input, index) => {
    const inputNodeId = `${nodeId}_input_${index}`;

    nodes.value.push({
      id: inputNodeId,
      data: {
        label: input.name,
        dataType: "input",
        isParams: false,
        affiliation: modelNode.id,
        description: input.description,
        state: "init",
      },
      position: {
        x:
          modelNodePosition.x -
          (dataSpacing *
            (modelNode.data.behavior.inputs.length +
              modelNode.data.behavior.parameters.length -
              1)) /
            2 +
          inputsAndParametersIndex * dataSpacing,
        y: modelNodePosition.y - 80,
      },
      type: "data",
    });
    inputsAndParametersIndex++;

    // 连接输入节点与模型节点
    edges.value.push({
      id: `edge-${inputNodeId}-${nodeId}`,
      source: inputNodeId,
      target: nodeId,
      type: "animation",
      animated: true,
    });
  });

  // 生成参数节点
  modelNode.data.behavior.parameters.forEach((parameter, index) => {
    const parameterNodeId = `${nodeId}_parameter_${index}`;

    nodes.value.push({
      id: parameterNodeId,
      data: {
        label: parameter.name,
        dataType: "input",
        isParams: true,
        affiliation: modelNode.id,
        description: parameter.description,
        state: "init",
      },
      position: {
        x:
          modelNodePosition.x -
          (dataSpacing *
            (modelNode.data.behavior.parameters.length +
              modelNode.data.behavior.inputs.length -
              1)) /
            2 +
          inputsAndParametersIndex * dataSpacing,
        y: modelNodePosition.y - 80,
      },
      type: "data",
    });
    inputsAndParametersIndex++;

    // 连接输入节点与模型节点
    edges.value.push({
      id: `edge-${parameterNodeId}-${nodeId}`,
      source: parameterNodeId,
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
  console.log("dependencies.value", dependencies.value);
};

// 保存工作流信息
// 保存工作流信息
// 保存工作流信息
const saveWorkFlowInfo = async (state) => {
  // 在这里要先统一的将数据节点的dataType重新设置一下。这个是为了改变颜色
  updateOutputDataType();
  // 重新确定节点的数据绑定关系
  handleDataNodeByConnection();

  const flowData = {
    nodes: nodes.value,
    edges: edges.value,
    dependencies: dependencies.value,
    containerFiles: containerFiles.value,
  };
  let formData = new FormData();
  formData.append("flowData", JSON.stringify(flowData));

  await updateWorkflowInfo(projectId, formData);
  if (state != "silent") {
    ElMessage.success("Save successfully");
  }
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

// 获取执行顺序,并执行工作流
// 获取执行顺序,并执行工作流
// 获取执行顺序,并执行工作流
const startNodes = ref([]);
const currentModelOutputs = ref([]);

// 初始化校验，返回true代表未通过检验
const initVerifyError = () => {
  // 1、检验是否所有的input和parameter都初始化过了，返回为true，则检验不通过
  let errorNode = "";
  let flag = nodes.value.some((node) => {
    if (node.type == "model" || node.type == "codeModel") {
      errorNode = node.data.label;
      // 输入事件（文件）
      let inputFlag = node.data.behavior.inputs.some((input) => {
        if (!input.dataRelation) return true;
        if (input.dataRelation.type == "file") {
          return input.dataRelation.value == "Select Data" ? true : false;
        }
      });
      // 参数事件
      let parameterFlag = node.data.behavior.parameters.some((parameter) => {
        if (!parameter.dataRelation) return true;
        if (parameter.dataRelation.type == "parameter") {
          return parameter.dataRelation.value == "input parameter"
            ? true
            : false;
        }
      });
      return inputFlag || parameterFlag;
    }
  });
  // flag为true，校验不通过
  if (flag) {
    emit(
      "addInfo",
      `Workflow initialization test failed : Please check that the data binding of the ${errorNode} node is complete`
    );
    return false;
  }

  emit("addInfo", "Workflow initialization test passed");
  return true;
};

// 执行前初始化节点数据
const getTasksOrder = () => {
  if (!initVerifyError()) {
    return;
  }

  // 获取所有起始节点，作为广度优先遍历的起始点
  startNodes.value = getStartNodes(nodes.value, edges.value);

  // 确定输出数据与输入数据的相等关系，关系存储在nodes.data.equalData中，无返回值
  // equalData是一个数组，因为要考虑一个输出多次使用的情况。
  handleDbDataEdges(nodes.value, edges.value);

  // 确定每个待运行任务的preconditions
  nodes.value = getExecutionOrder(nodes.value, edges.value);

  // 重新确定节点的数据绑定关系
  handleDataNodeByConnection();

  // 前置工作结束，开始运行workflow
  runWorkFlow();
};

// 初始化完毕，开始运行工作流
const runWorkFlow = async () => {
  // 得到所有待运行节点
  let todoTasks = getAllTasks(nodes.value);

  // 找到第一个运行节点，开始运行
  let affiliationNodes = startNodes.value.map((node) => {
    return nodes.value.find((node2) => node2.id === node.data.affiliation);
  });

  // 通过JSON和set进行去重
  let uniqueAffiliationNodes = [
    ...new Set(
      affiliationNodes.map((affiliationNode) => JSON.stringify(affiliationNode))
    ),
  ].map(JSON.parse);

  let firstTask = uniqueAffiliationNodes.find((node) => {
    if (!node.data.precondition) {
      return node.id;
    }
  });
  // 初始化已完成任务数组
  let doneTasks = [];
  await executedOperation(firstTask, todoTasks, doneTasks);
};

// 执行模型任务
const executedOperation = async (currentTask, todoTasks, doneTasks) => {
  let result = await modelExecution(currentTask);
  if (result == "error") {
    return;
  }

  // 将当前任务加入已完成任务队列,并且从待执行任务队列中移除
  doneTasks.push(currentTask.id);
  todoTasks = todoTasks.filter((task) => task.id !== currentTask.id);

  // 所有任务都完成，自动退出
  if (!todoTasks.length) {
    emit("addInfo", "All tasks of the round have been successfully completed");
    saveWorkFlowInfo("silent");
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
const modelExecution = async (currentTask) => {
  // 传递过来的就已经不是引用了，需要重新获取
  currentTask = nodes.value.find((node) => node.id === currentTask.id);
  // 现在已经有了模型的元数据，剩下的就是如何执行了
  console.log("--------------------------------------------");
  emit("addInfo", "--------------------------------------------");
  emit("addInfo", `开始运行 ${currentTask.data.label}`);

  console.log(`开始运行 ${currentTask.data.label}`);
  // integrate中的codeModel根据对应的容器来运行
  if (currentTask.type == "codeModel") {
    // 初始化输出数据列表
    currentModelOutputs.value = [];

    // 1、解析代码，将引用的数据替换为真实数据相对路径
    let originCode = currentTask.data.behavior.code;
    let replacedCode = originCode.replace(
      /@\(\s*"([^"]*?)"\s*\)/g,
      (match, p1, offset, string) =>
        replaceSpecialMarkers(currentTask, match, p1)
    );

    replacedCode = replacedCode.replace(
      /@\(\s*"([^"]*?)"\s*,\s*"([^"]*?)"\s*\)/g,
      (match, p1, p2, offset, string) =>
        replaceSpecialMarkers(currentTask, match, p1, p2)
    );

    // 2、在执行位置生成（重写覆盖）一个python脚本
    let formDataPy = new FormData();
    formDataPy.append("code", replacedCode);
    formDataPy.append("scenarioId", currentScenario.value.id);
    formDataPy.append("fileName", currentTask.data.label);
    await savePythonCode(formDataPy);

    // 3、根据input声明，将输入数据挂载到docker容器中
    let urlList = [];
    currentTask.data.behavior.inputs.forEach((input) => {
      let fileName = input.dataRelation.label;
      let url = input.dataRelation.value;
      let urlExists = containerFiles.value.some((file) => {
        return file.url === url;
      });
      // 如果 url 不存在，才添加到 urlList 和 containerFiles.value
      if (!urlExists) {
        urlList.push({
          fileName,
          url,
        });
      }
    });
    let downloadResult = await downloadFilesToVolume(
      currentScenario.value.id,
      urlList
    );
    // 现在不确定怎么规定下载失败的定义，先处理成功的情况
    containerFiles.value.push(...urlList);
    await saveWorkFlowInfo();

    // 4、执行docker容器，获得打印内容
    let formDataExecute = new FormData();
    formDataExecute.append("scenarioId", currentScenario.value.id);
    formDataExecute.append("scriptName", currentTask.data.label + ".py");
    formDataExecute.append("containerId", currentScenario.value.containerId);
    let resultPrint = await executeScript(formDataExecute);
    console.log(resultPrint);
    if (resultPrint.state == 40000) {
      emit("addInfo", resultPrint.error);
      return "error";
    }

    emit("addInfo", resultPrint.output);

    // 5、执行结果处理，若成功则将输出数据上传到数据容器，并将元数据存储到数据库
    // 假定进行到这里必定成功
    // 11.13新增，后端有处理，如果路径存在对应的文件则上传，否则不上传

    let uploadRequestData = {
      folderId: currentFolder.value[0].id,
      fileNames: currentModelOutputs.value.map((output) => output.name),
      scenarioId: currentScenario.value.id,
    };

    let uploadResult = await uploadFilesToDataContainer(uploadRequestData);

    // 6、最后将输出数据的url绑定到对应output的value中，并将其传递给对应的输入数据
    // 6.1、将输出数据的url绑定到对应output的value
    if (uploadResult.length > 0) {
      uploadResult.forEach((result) => {
        if (result.status === "success") {
          console.log(result.message);
          let outputMap = currentModelOutputs.value.find(
            (output) => output.name == result.filePath
          );

          let output = currentTask.data.behavior.outputs.find(
            (output) => output.name == outputMap.label
          );
          output.value = result.message;
        }
      });
    }
    // 6.2、将输出数据的url传递给对应的输入数据
    currentTask.data.behavior.outputs.forEach((output) => {
      if (output.equalData[0]) {
        let inputNode = nodes.value.find(
          (node) => node.id === output.equalData[0]
        );
        console.log(inputNode, 1515);

        let modelNode = nodes.value.find(
          (node) => node.id === inputNode.data.affiliation
        );
        modelNode.data.behavior.inputs.find(
          (input) => input.name === inputNode.data.label
        ).dataRelation.value = output.value;
      }
    });
  } else if (currentTask.type == "model") {
    console.log("我要开始运行这个模型了噢", currentTask);
  } else {
    emit(
      "addInfo",
      `${currentTask.data.label}exception, not code model or service model`
    );
  }
};

// 替换函数，根据要替换的函数类型执行副作用
const replaceSpecialMarkers = (currentTask, match, p1, p2) => {
  // input和parameter
  if (typeof p2 !== "string") {
    let input = currentTask.data.behavior.inputs.find(
      (input) => input.name == p1
    );
    if (input) {
      let inputFullName = input.dataRelation.label;
      return `"app/data/${inputFullName}"`;
    }
    let parameter = currentTask.data.behavior.parameters.find(
      (parameter) => parameter.name == p1
    );
    if (parameter) {
      return `"${parameter.dataRelation.value}"`;
    }
  } else {
    // 处理输出文件，执行副作用
    let outputLabel = p1.trim();
    let fileName = p2.trim();
    let outputInfo = {
      label: outputLabel,
      name: fileName,
    };
    currentModelOutputs.value.push(outputInfo);

    // 可以在这里添加其他副作用，例如创建文件或更新文件路径等
    return `"app/data/${fileName}"`;
  }
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

  currentFolder.value = await getFoldersByScenarioId(projectId);

  if (currentScenario.value.flowData) {
    let flowData = JSON.parse(currentScenario.value.flowData);
    nodes.value = flowData.nodes;
    edges.value = flowData.edges;
    dependencies.value = flowData.dependencies ? flowData.dependencies : [];
    containerFiles.value = flowData.containerFiles
      ? flowData.containerFiles
      : [];
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
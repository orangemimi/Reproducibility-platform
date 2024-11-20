import { ElMessage } from "element-plus";

// 根据nodes和edges计算所有起始点
export function getStartNodes(nodes, edges) {
  let remainingNodes = nodes.filter(
    (node) => !edges.some((edge) => edge.target === node.id)
  );
  return remainingNodes;
}

// 根据nodes和edges计算所有结束点（即所有的最终输出数据）
export function getEndNodes(nodes, edges) {
  let remainingNodes = nodes.filter(
    (node) => !edges.some((edge) => edge.source === node.id)
  );
  return remainingNodes;
}

// 所有的输入数据，用于迭代
export function getAllInputDataNodes(nodes) {
  let inputNodes = nodes.filter((node) => node.data.dataType === "input");
  return inputNodes;
}

// 返回nodes中所有的model/method
export function getAllTasks(nodes) {
  let taskNodes = nodes.filter(
    (node) =>
      node.type === "model" ||
      node.type === "method" ||
      node.type === "codeModel"
  );
  return taskNodes;
}

//改变节点状态
export function changeNodesState(nodeIdList, nodes, state) {
  nodeIdList.forEach((nodeId) => {
    const targetNode = nodes.find((node) => node.id === nodeId);
    if (targetNode) {
      // 更新节点的背景颜色
      targetNode.data.state = state;
    }
  });
}

// 找出所有的数据连线，并保留数据之间的关系
export function handleDbDataEdges(nodes, edges) {
  edges.forEach((edge) => {
    if (edge.source.includes("output") && edge.target.includes("input")) {
      let outputNode = nodes.find((node) => node.id === edge.source);
      let inputNode = nodes.find((node) => node.id === edge.target);
      outputNode.data.equalData = [];
      outputNode.data.equalData.push(inputNode.id);
    }
  });
}

// 辅助函数：获取一个执行节点的所有输入数据节点
export function getInputNodes(node, nodes) {
  return nodes.filter(
    (n) => n.data.affiliation === node.id && n.data.dataType === "input"
  );
}

// 找到模型的调用顺序
export function getExecutionOrder(nodes, edges) {
  let startNodes = getStartNodes(nodes, edges);
  let lastExecutedNodeId = null;
  startNodes.forEach((startNode) => {
    traversePaths(startNode.id, edges, nodes, lastExecutedNodeId);
  });
  return nodes;
}

// 根据节点id获取与之相连的所有出边
function findOutgoingEdges(nodeId, edges) {
  return edges.filter((edge) => edge.source === nodeId);
}

// 递归遍历路径
function traversePaths(nodeId, edges, nodes, lastExecutedNodeId) {
  let currentNode = nodes.find((node) => node.id === nodeId);
  if (currentNode.type === "model" || currentNode.type === "method") {
    if (lastExecutedNodeId !== null) {
      // 如果 precondition 是空的，初始化它
      if (!currentNode.data.precondition) {
        currentNode.data.precondition = [];
      }
      // 添加上一个执行过的 model/method 节点的 id
      if (!currentNode.data.precondition.includes(lastExecutedNodeId)) {
        currentNode.data.precondition.push(lastExecutedNodeId);
      }
    }
    lastExecutedNodeId = nodeId;
  }
  let outgoingEdges = findOutgoingEdges(nodeId, edges);
  if (outgoingEdges.length === 0) {
    // 如果没有出边，意味着到达终点，打印或保存路径
    return;
  } else {
    // 递归遍历所有可能的路径
    try {
      outgoingEdges.forEach((edge) => {
        traversePaths(edge.target, edges, nodes, lastExecutedNodeId);
      });
    } catch (error) {
      ElMessage({
        type: "error",
        message: "工作流无法执行，请检查连接状态",
      });
      return;
    }
  }
}

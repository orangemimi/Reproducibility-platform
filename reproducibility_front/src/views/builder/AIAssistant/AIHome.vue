<template>
  <div class="AIContainer">
    <el-row>
      <el-col :span="7" class="paperController">
        <div class="uploader">
          <el-upload
            ref="uploadPaper"
            accept=".pdf"
            :limit="1"
            :http-request="submitFile"
            :on-exceed="handleExceed"
          >
            <el-button type="primary">upload paper</el-button>
            <template #tip>
              <div class="el-upload__tip">
                pdf files with a size less than 50MB.
              </div>
            </template>
          </el-upload>
        </div>
        <div id="pdf">
          <VuePdfApp class="pdfContainer" :pdf="uploadPaperLink"></VuePdfApp>
        </div>
      </el-col>
      <el-col :span="10" class="mermaidController">
        <!-- <el-button @click="viewMermaidCode"> 查看mermaid code </el-button> -->
        <!-- <VueMermaidRender :content="drawContent" /> -->
        <vueFlow
          :vueFlowData="parseMermaidToJson(testData)"
          @searchModel="searchModel"
          @viewDrawer="viewDrawer"
        ></vueFlow>
      </el-col>
      <el-col :span="7" class="AIdialogue">
        <QA :pdfContent="pdfContent" @mermaidContent="mermaidContent"></QA>
      </el-col>
    </el-row>
  </div>
  <el-drawer
    v-model="drawerState"
    title="Search results and history"
    direction="rtl"
    class="drawer"
  >
    <div class="drawerTip">
      Click the model node to search the relevant model services.
      <br />
      Click to explore the model in the openGMS model repository：
    </div>
    <div class="modelPanel">
      <searchCard
        class="modelCard"
        v-for="(item, index) in modelCache"
        :modelInfo="item"
        :key="index"
      ></searchCard>
    </div>
  </el-drawer>

  <el-drawer v-model="visiblePop" size="50%">
    <!-- @ts-ignore  -->
    <assessment
      :initialInstanceObjectList="instanceObjectList"
      :reproducedInstanceObjectList="instanceObjectList"
      :scenarioId="currentId"
    ></assessment>
  </el-drawer>
  <div class="affix">
    <div icon="Connection" class="circleBtn" @click="changeVisiblePop">
      <div class="circleText">
        <el-icon><Pointer /></el-icon>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, Ref, ref } from "vue";
import { genFileId } from "element-plus";
import type { UploadProps, UploadInstance, UploadRawFile } from "element-plus";
import VuePdfApp from "vue3-pdf-app";
import "vue3-pdf-app/dist/icons/main.css";
// @ts-ignore
import QA from "_com/chatgpt/QACom.vue";
import assessment from "@/views/rebuilder/info/components/Assessment.vue";
import {
  getPdfContent,
  modelQuery,
  getScenariosByProjectId,
  // @ts-ignore
} from "@/api/request.js";
// @ts-ignore
import vueFlow from "./vueFlow/vueFlow.vue";
// @ts-ignore
import searchCard from "./vueFlow/OpenGMSModelCard.vue";
import { useRoute } from "vue-router";

const route = useRoute();
// import VueFlow from "vueflow";
const visiblePop = ref(false);
const currentScenario = ref({});
const currentId = ref("");
const instanceObjectList = ref({});
const projectId = ref(route.params.id);
const changeVisiblePop = () => {
  visiblePop.value = true;
};

onMounted(async () => {
  // mermaidInitial();
  let result = await getScenariosByProjectId(projectId.value);
  // @ts-ignore
  currentScenario.value = result[0];
  // @ts-ignore
  // let instances = await getInstancesByIds(result[0].instances);

  // @ts-ignore
  instanceObjectList.value = result[0].instances;
  // @ts-ignore
  currentId.value = result[0].id;
});

const mermaidDataBase: Ref<string[]> = ref([
  `
graph TD
classDef inputData fill:#ffab2d,stroke:#000,stroke-width:2px;
classDef intermediateData fill:#ffe6d3,stroke:#000,stroke-width:2px;
classDef outputData fill:#b9e6d3,stroke:#000,stroke-width:2px;
classDef model fill:#87cefa,stroke:#000,stroke-width:2px,color:#fff;

A1[Road Length]:::inputData --> B1[/Random Forest Regression Model - Rooftop Area Prediction/]:::model
A2[Built-up Area]:::inputData --> B1
A3[Population Size]:::inputData --> B1
A4[Night Light Intensity]:::inputData --> B1
B1 --> C1[Predicted Rooftop Area]:::intermediateData
C1 --> C1_Input[Standardized Rooftop Area Data]:::inputData

C1_Input --> D1[/Installed Capacity Calculation Model/]:::model
A5[Roof Area]:::inputData --> D1
A6[Rated Power per Unit Area of PV Panel]:::inputData --> D1
D1 --> E1[Annual Power Generation]:::intermediateData
E1 --> E1_Input[Power Generation Data]:::inputData

E1_Input --> F1[/Carbon Mitigation Potential Assessment Model/]:::model
E1_Input --> G1[/Carbon Mitigation Factor Calculation Model/]:::model
F1 --> H1[Carbon Mitigation Total]:::outputData
G1 --> J1[Carbon Mitigation Result]:::outputData

E1_Input --> K1[/K-means++ Clustering Analysis/]:::model
K1 --> L1[City Classification Result]:::outputData

`,
  `
graph TD
classDef inputData fill:#ffab2d,stroke:#000,stroke-width:2px;
classDef intermediateData fill:#ffe6d3,stroke:#000,stroke-width:2px;
classDef outputData fill:#b9e6d3,stroke:#000,stroke-width:2px;
classDef model fill:#87cefa,stroke:#000,stroke-width:2px,color:#fff;

A1[Road Length]:::inputData --> B1[/Random Forest Regression Model - Rooftop Area Prediction/]:::model
A2[Built-up Area]:::inputData --> B1
A3[Population Size]:::inputData --> B1
A4[Night Light Intensity]:::inputData --> B1
B1 --> C1[Predicted Rooftop Area]:::intermediateData
C1 --> C1_Input[Standardized Rooftop Area Data]:::inputData

C1_Input --> D1[/Installed Capacity Calculation Model/]:::model
A5[Real Roof Area]:::inputData --> D1
A6[Rated Power per Unit Area of PV Panel]:::inputData --> D1
D1 --> E1[Annual Power Generation]:::intermediateData
E1 --> E1_Input[Power Generation Data]:::inputData

A7[OM Factor]:::inputData --> F1[/Carbon Mitigation Factor Calculation Model/]:::model
A8[BM Factor]:::inputData --> F1
F1 --> G1[Carbon Mitigation Factor]:::intermediateData
G1 --> G1_Input[Carbon Mitigation Factor]:::inputData

G1_Input --> H1[/Carbon Mitigation Potential Assessment Model/]:::model
E1_Input --> H1
H1 --> I1[Carbon Mitigation Potential]:::intermediateData
I1 --> I1_Input[Carbon Mitigation Potential]:::inputData

I1_Input --> J1[/K-means++ Clustering Analysis/]:::model
J1 --> K1[City Classification Result]:::outputData

`,

  `
graph TD
classDef inputData fill:#ffab2d,stroke:#000,stroke-width:2px;
classDef intermediateData fill:#ffe6d3,stroke:#000,stroke-width:2px;
classDef outputData fill:#b9e6d3,stroke:#000,stroke-width:2px;
classDef model fill:#87cefa,stroke:#000,stroke-width:2px,color:#fff;

A1[Time Series Data Xt]:::inputData --> B1[/GRU Network for Temporal Correlation/]:::model
A2[Previous Time Series Xt-T to Xt-1]:::inputData --> B1
B1 --> C1[Temporal Feature Output]:::intermediateData
C1 --> C1_Input[Temporal Features for Spatial Modelling]:::inputData

C1_Input --> D1[/Graph Construction for Spatial Correlation/]:::model
A3[Spatial Adjacency and Distance Matrices]:::inputData --> D1
D1 --> E1[Spatial Feature Output]:::intermediateData
E1 --> E1_Input[Spatial Features for Prediction]:::inputData

E1_Input --> F1[/GRU-GCN Spatiotemporal Prediction/]:::model
F1 --> G1[Predicted Carbon Emissions Xt+1]:::outputData
E1_Input --> H1[/GRU-GCN Multi-step Prediction/]:::model
H1 --> I1[Multi-step Predicted Carbon Emissions]:::outputData

E1_Input --> J1[/Performance Evaluation Model/]:::model
A4[MAE, RMSE, MAPE Metrics]:::inputData --> J1
J1 --> K1[Evaluation Results]:::outputData

`,
  `
graph TD
classDef inputData fill:#ffab2d,stroke:#000,stroke-width:2px;
classDef intermediateData fill:#ffe6d3,stroke:#000,stroke-width:2px;
classDef outputData fill:#b9e6d3,stroke:#000,stroke-width:2px;
classDef model fill:#87cefa,stroke:#000,stroke-width:2px,color:#fff;

A1[Time Series Data Xt]:::inputData --> B1[/GRU-GCN Spatiotemporal Prediction Model/]:::model
A2[Previous Time Series Xt-T to Xt-1]:::inputData --> B1
A3[Spatial Adjacency and Distance Matrices]:::inputData --> B1

B1 --> C1[Predicted Carbon Emissions Xt+1]:::intermediateData
C1 --> D1[Predicted Carbon Emissions Xt+1]:::inputData

D1 --> E1[/Recursive Multi-step Prediction/]:::model
E1 --> F1[Multi-step Predicted Carbon Emissions]:::intermediateData
F1 --> G1[Multi-step Predicted Carbon Emissions]:::inputData


C1_input[Predicted Carbon Emissions Xt+1]:::inputData --> H1[/Performance Evaluation Model/]:::model
G1 --> H1
A4[MAE, RMSE, MAPE Metrics]:::inputData --> H1
H1 --> I1[Evaluation Results for Single-step and Multi-step Predictions]:::outputData
`,
]);

const testData: Ref<string> = ref("");

const drawerState = ref(false);

const baseUrl: string = "http://geomodeling.njnu.edu.cn/computableModel/";

type modelInfo = {
  query: string;
  url: string;
  name: string;
  overview: string;
};

const modelCache: Ref<modelInfo[]> = ref([]);
// 自定义事件：打开drawer，并开始执行搜索操作
const searchModel = (modelName: string) => {
  drawerState.value = true;
  // 在这里执行搜索操作
  queryForResource(modelName);
};

const viewDrawer = (state: boolean) => {
  drawerState.value = state;
};

// 自定义事件：接收gpt返回的mermaidCode
const mermaidContent = (
  mermaidCode: any,
  changeMermaid: boolean,
  caseNum: number
) => {
  if (changeMermaid) {
    if (caseNum == 1) {
      if (testData.value.length == 0) {
        testData.value = mermaidDataBase.value[0];
        uploadPaperLink.value =
          "http://221.224.35.86:38083/data/559214d8-dc75-4842-9b37-ae78d32b6b4e";
      } else {
        testData.value = mermaidDataBase.value[1];
      }
    } else if (caseNum == 2) {
      if (testData.value.length == 0) {
        testData.value = mermaidDataBase.value[2];
        uploadPaperLink.value =
          "http://221.224.35.86:38083/data/d11bff62-fac7-4ee8-9168-f66fe782d9f6";
      } else {
        testData.value = mermaidDataBase.value[3];
      }
    }
    return;
  }
  // console.log(mermaidCode, "mermaidCode");
  testData.value = mermaidCode.content;
};

// 执行搜索操作
const queryForResource = async (
  query: string,
  collection: string = "computableModel"
) => {
  let formData = new FormData();
  formData.append("query", query);
  formData.append("collection", collection);
  let result = await modelQuery(formData);
  if (typeof result === "string") {
    // @ts-ignore
    const jsonString = result.replace(/'/g, '"').replace(/None/g, "null");
    const newJson = JSON.parse(jsonString);
    let newModelInfo: modelInfo = {
      query: query,
      url: newJson.ids[0][0] ? baseUrl + newJson.ids[0][0] : "",
      name: newJson.metadatas[0][0].name || "",
      overview: newJson.metadatas[0][0].overview || "",
    };
    modelCache.value.push(newModelInfo);
  }
};

// 将gpt提取的mermaid语言转化为json(复现文档)
interface FlowNode {
  id: string;
  label: string;
  type: string;
  position: { x: number; y: number };
  data: { description: string; label: string };
}

interface FlowEdge {
  id: string;
  source: string;
  target: string;
  sourceLabel: string;
  targetLabel: string;
  type: string;
}

const parseMermaidToJson = (
  mermaidText: string
): { nodes: FlowNode[]; links: FlowEdge[] } => {
  const nodes: FlowNode[] = [];
  const links: FlowEdge[] = [];
  const nodeMap = new Map<string, FlowNode>();
  let nodeCounter = 0;

  // 预定义坐标计算参数
  const COLUMN_WIDTH = 300;
  const ROW_HEIGHT = 150;
  const columnPositions: number[] = [0, 0, 0]; // 三列布局

  mermaidText.split("\n").forEach((line) => {
    const trimmed = line.trim();
    // 跳过空行和样式定义
    if (!trimmed || trimmed.startsWith("classDef")) return;

    // 解析连接关系
    if (trimmed.includes("-->")) {
      const [sourcePart, targetPart] = trimmed
        .split("-->")
        .map((p) => p.trim());

      const nodeRegex = /(\w+)\[(.*?)\](:::\w+)?/;
      const nodeMatch = sourcePart.match(nodeRegex);
      const nodeMatch2 = targetPart.match(nodeRegex);
      if (nodeMatch) {
        const [_, id, label, type] = nodeMatch;
        const nodeType = type ? type.replace(":::", "") : "default";

        if (!nodeMap.has(id)) {
          // 自动布局计算
          const col = nodeCounter % 3;
          const x = col * COLUMN_WIDTH + 50;
          const y = columnPositions[col] * ROW_HEIGHT + 50;
          columnPositions[col]++;

          const newNode: FlowNode = {
            id: id,
            label: label.replace(/^\/(.*)\/$/, "$1"), // 去除模型节点的斜杠
            type: nodeType,
            position: { x, y },
            data: { description: "", label },
          };

          nodes.push(newNode);
          nodeMap.set(id, newNode);
          nodeCounter++;
        }
      }
      if (nodeMatch2) {
        const [_, id, label, type] = nodeMatch2;
        const nodeType = type ? type.replace(":::", "") : "default";

        if (!nodeMap.has(id)) {
          // 自动布局计算
          const col = nodeCounter % 3;
          const x = col * COLUMN_WIDTH + 50;
          const y = columnPositions[col] * ROW_HEIGHT + 50;
          columnPositions[col]++;

          const newNode: FlowNode = {
            id: id,
            label: label.replace(/^\/(.*)\/$/, "$1"), // 去除模型节点的斜杠
            type: nodeType,
            position: { x, y },
            data: { description: "", label },
          };

          nodes.push(newNode);
          nodeMap.set(id, newNode);
          nodeCounter++;
        }
      }

      // 提取源节点ID（支持模型节点格式）
      const sourceIdMatch = sourcePart.match(/(\w+)(\[.*?\])?/);
      const sourceId = sourceIdMatch ? sourceIdMatch[1] : "";

      // 提取目标节点ID（支持多节点连接）
      const targetIds = targetPart.split(",").map((t) => {
        const match = t.trim().match(/(\w+)(\[.*?\])?/);
        return match ? match[1] : "";
      });

      // 创建连接关系
      targetIds.forEach((targetId) => {
        if (
          sourceId &&
          targetId &&
          nodeMap.has(sourceId) &&
          nodeMap.has(targetId)
        ) {
          const sourceNode = nodeMap.get(sourceId)!;
          const targetNode = nodeMap.get(targetId)!;

          links.push({
            id: `${sourceId}-${targetId}`,
            source: sourceId,
            target: targetId,
            sourceLabel: sourceNode.label,
            targetLabel: targetNode.label,
            type: "animation",
          });
        }
      });
    }
  });

  return { nodes, links };
};

/**
 * 最左侧上传与预览论文的版块
 */

// uploader
// uploader
// uploader
const uploadPaper = ref<UploadInstance>();

// pdfLoader初试状态
const uploadPaperLink = ref(
  "http://221.224.35.86:38083/data/385614f9-86ec-4e24-9a91-0772d15b0b3b"
  // "http://221.224.35.86:38083/data/574de5c5-33b4-4124-adf3-c9a232c88671"
);

const pdfContent = ref("");

const submitFile = (file: any) => {
  // 第一遍传入的不是File，是一个带有请求参数的类型
  if (file.file) {
    file = file.file;
  }
  let formData = new FormData();
  formData.append("file", file);
  getPdfContent(formData).then((res: any) => {
    uploadPaperLink.value = res.link;
    let content = res.pages;
    let pages = JSON.parse(content);

    let words = "";
    pages.forEach((page: any) => {
      words += page.md ? page.md : "";
    });
    words = words.length > 30000 ? words.slice(0, 30000) : words;

    pdfContent.value = words;
  });
};

// 超出了就提醒一下，确定是否替换
const handleExceed: UploadProps["onExceed"] = (files) => {
  uploadPaper.value!.clearFiles();
  const file = files[0] as UploadRawFile;
  file.uid = genFileId();
  uploadPaper.value!.handleStart(file);
  submitFile(file);
};
</script>

<style scoped lang="scss">
.affix {
  text-align: right;
  left: 35px;
  bottom: 35px;
  position: fixed;
  z-index: 100;
  .circleBtn {
    background: rgb(226, 127, 29);
    border-radius: 25px;

    width: 50px;
    height: 50px;
    .circleText {
      line-height: 50px;
      text-align: center;
      font-size: 30px;
      color: #ffffff;
    }
  }
}
.drawer {
  .modelPanel {
    width: 100%;
    height: calc(100% - 80px);
    .modelCard {
      margin-bottom: 10px;
    }
  }
  .drawerTip {
    margin: 0 0 20px 0;
  }
}

.AIContainer {
  width: 100%;
  height: 99.5%;
  display: flex;

  justify-content: center;
  align-items: center;
  flex-wrap: nowrap;

  .el-row {
    width: 99%;
    height: 98%;

    .paperController {
      height: 100%;
      box-shadow: 0 5px 12px 0 rgba(0, 0, 0, 0.3);
      border-radius: 10px;
      .uploader {
        padding: 2% 5%;
        height: 12%;
      }
      #pdf {
        height: 100%;
        .pdfContainer {
          width: 90%;
          // height: calc(96vh - 300px);
          height: 80% !important;
          box-shadow: 5px 5px 12px 0 rgba(0, 0, 0, 0.3);

          margin-left: 5%;

          border-radius: 10px;
        }
      }
    }
    .mermaidController {
      height: 100%;

      display: flex;
      justify-content: center; /* 水平居中 */
      align-items: center; /* 垂直居中 */
      .mermaid {
        width: 40vw; /* 或任何其他值 */
        height: 100%;
        display: flex;
        justify-content: center;
        overflow: auto;
      }
    }
    .AIdialogue {
      height: 99.5%;
    }
  }
}

:deep(.pdf-app .toolbar) {
  z-index: 100 !important;
}
</style>
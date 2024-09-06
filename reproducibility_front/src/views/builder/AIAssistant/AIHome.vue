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
        <!-- <QA :pdfContent="pdfContent"></QA> -->
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
</template>

<script setup lang="ts">
import { onMounted, Ref, ref } from "vue";
import { genFileId } from "element-plus";
import type { UploadProps, UploadInstance, UploadRawFile } from "element-plus";
import VuePdfApp from "vue3-pdf-app";
import "vue3-pdf-app/dist/icons/main.css";
// @ts-ignore
import QA from "_com/chatgpt/QACom.vue";
// import { VueMermaidRender } from "vue-mermaid-render";
// import mermaid from "mermaid";
// @ts-ignore
import { getPdfContent, modelQuery } from "@/api/request.js";
// @ts-ignore
import vueFlow from "./vueFlow/vueFlow.vue";
// @ts-ignore
import searchCard from "./vueFlow/OpenGMSModelCard.vue";

// import VueFlow from "vueflow";

onMounted(() => {
  // mermaidInitial();
});

/**
 * 中间mermaid，绘制流程图的模块
 */
// const content = `
// graph TD
// %% 定义节点样式
//   classDef inputData fill:#fff8f8,stroke:#000,stroke-width:2px;
//   classDef outputData fill:#b9e6d3,stroke:#000,stroke-width:2px;
//   classDef process fill:#07689f,stroke:#000,stroke-width:2px,color:#fff;

//   %% 节点定义
//   A1[数据1]:::inputData --> B1[/过程1/]:::process
//   A2[数据2]:::inputData --> B1

//   B1 --> C1[数据3]:::outputData
//   B1 --> C2[数据4]:::outputData
//   B1 --> C3[数据5]:::outputData

//   C1 --> D1[数据3]:::inputData
//   C2 --> D2[数据4]:::inputData

//   F1[数据6]:::inputData --> E1
//   D1 --> E1[/过程2/]:::process
//   D2 --> E1

//   E1 --> G1[数据7]:::outputData
//   E1 --> G2[数据8]:::outputData

//   G1 --> H1[数据7]:::inputData
//   H1 --> I1[/过程3/]:::process

//   I1 --> J1[数据9]:::outputData
//   I1 --> J2[数据10]:::outputData
//   I1 --> J3[数据11]:::outputData
//   I1 --> J4[数据12]:::outputData
//   I1 --> J5[数据13]:::outputData
// `;

// let drawContent = ref(
//   `graph LR
//     classDef inputData fill:#fff8f8,stroke:#000,stroke-width:2px;
//     classDef outputData fill:#b9e6d3,stroke:#000,stroke-width:2px;
//     classDef process fill:#07689f,stroke:#000,stroke-width:2px,color:#fff;

//     A[示例数据数据1]:::inputData
//     B[/示例步骤1/]:::process
//     A --> B
//     B --> C[示例输出数据1]:::outputData
//     B --> D[示例输出数据2]:::outputData`
// );
// // 初始化mermaid流程图
// const mermaidInitial = () => {
//   mermaid.initialize({
//     startOnLoad: false,
//     theme: "default",
//     flowchart: {
//       curve: "linear", // 将连线样式设置为折线
//     },
//   });
// };

// // 处理QACom传递过来的mermaid语法
// const mermaidContent = (value: any) => {
//   let lines = value.content.split("\n");
//   if (lines.length <= 2) {
//     return "";
//   }
//   lines.shift(); // 删除第一个元素
//   lines.pop(); // 删除最后一个元素
//   if (lines[0].includes("`")) {
//     lines.shift();
//   }
//   lines.shift();
//   let headLines = [
//     "graph LR\n",
//     "classDef inputData fill:#fff8f8,stroke:#000,stroke-width:2px;\n",
//     "classDef outputData fill:#b9e6d3,stroke:#000,stroke-width:2px;\n",
//     "classDef process fill:#07689f,stroke:#000,stroke-width:2px,color:#fff;\n",
//   ];
//   lines.unshift(...headLines);

//   if (lines[lines.length - 1].includes("`")) {
//     lines.pop();
//   }

//   // 将剩余的行重新连接成一个字符串
//   let trim = lines.join("\n");
//   let newValue = "`" + "\n" + trim + "\n" + "`";
//   console.log(newValue, "newValue");

//   drawContent.value = trim;
// };

// //@ts-ignore
// const viewMermaidCode = () => {
//   alert(drawContent.value);
// };

/**
 * 新版，使用vueFlow组件
 */
// const vueFlowData = ref([
//   { id: "1", type: "input", label: "Node 1", position: { x: 250, y: 5 } },
//   { id: "2", label: "Node 2", position: { x: 100, y: 100 } },
//   { id: "3", label: "Node 3", position: { x: 400, y: 100 } },
//   { id: "4", label: "Node 4", position: { x: 400, y: 200 } },
//   { id: "e12", source: "1", target: "2" },
//   { id: "e13", source: "1", target: "3" },
// ]);

// const flowJsonData = {
//   nodes: [
//     { id: "1", label: "data1", type: "data" },
//     { id: "2", label: "step1", type: "process" },
//     { id: "3", label: "data2", type: "data" },
//     { id: "4", label: "data3", type: "data" },
//     { id: "5", label: "step2", type: "process" },
//     { id: "6", label: "data4", type: "data" },
//     { id: "7", label: "data5", type: "data" },
//     { id: "8", label: "data6", type: "data" },
//     { id: "9", label: "step3", type: "process" },
//     { id: "10", label: "data7", type: "data" },
//     { id: "11", label: "step4", type: "process" },
//     { id: "12", label: "data8", type: "data" },
//     { id: "13", label: "step5", type: "process" },
//     { id: "14", label: "data9", type: "data" },
//   ],
//   edges: [
//     { source: "1", target: "2" },
//     { source: "2", target: "3" },
//     { source: "2", target: "4" },
//     { source: "4", target: "5" },
//     { source: "5", target: "6" },
//     { source: "5", target: "7" },
//     { source: "5", target: "8" },
//     { source: "8", target: "9" },
//     { source: "9", target: "10" },
//     { source: "10", target: "11" },
//     { source: "11", target: "12" },
//     { source: "12", target: "13" },
//     { source: "13", target: "14" },
//   ],
// };

// // 根据json数据生成vueFlowData
// const generateElementsFromJson = (data: any) => {
//   const nodes = data.nodes.map((node: any) => ({
//     id: node.id,
//     position: { x: 0, y: 0 },
//     label: node.label,
//     type: node.type,
//     data: { label: node.label },
//   }));

//   const edges = data.edges.map((edge: any) => ({
//     id: `e${edge.source}-${edge.target}`,
//     source: edge.source,
//     target: edge.target,
//     type: "animation",
//     // animated: true, // 可以根据需要调整
//   }));

//   return { nodes: nodes, edges: edges };
// };

const testData: Ref<string> = ref(`
graph LR
A[Multi-source heterogeneous geospatial data]:::data --> B[Machine learning regression for rooftop area estimation]:::process
B --> C1[Solar radiation data]:::data
B --> C2[Urban land use data]:::data
B --> C3[Building footprint data]:::data
C1 --> E[Assessment of rooftop PV carbon reduction potential]:::process
C2 --> E
C3 --> E
E --> F[Climate variation analysis]:::data
F --> G[Rooftop potential categorization]:::process
G --> H[Future potential prediction for 2030]:::process
H --> I[Impact of urban land expansion and energy structure transition]:::process
I --> J[Development priority analysis]:::process
J --> K[Challenges and opportunities discussion]:::process
K --> L[Key insights for rooftop PV development]:::data
L --> M[Foundation for similar studies in other countries]:::data
`);
// const testData: Ref<string> = ref(`graph TD

// A[多源异构地理空间数据]:::data --> B[利用机器学习方法识别屋顶面积]:::process
// B --> C[屋顶面积数据]:::data
// A --> D[分析城市土地扩张和电力混合转型情况]:::process
// D --> E[城市土地扩张和电力混合转型情况数据]:::data
// E --> F[分析城市屋顶光伏利用率]:::process
// C --> F
// F --> G[城市屋顶光伏利用率]:::data
// G --> H[分析地理资源分布情况]:::process
// E --> H
// H --> I[提出发展屋顶光伏的决策]:::data
// `);
// const testData: Ref<string> = ref(`
// graph TD
// A[data1]:::data --> B[process1]:::process
// B --> C[data2]:::data
// B --> D[data3]:::data
// D --> E[process2]:::process
// E --> F[data4]:::data
// `);
type node = {
  id: string;
  label: string;
  type: string;
  position: { x: number; y: number };
  data: { label: string; description: string };
};
type link = {
  id: string;
  source: string;
  sourceLabel: string;
  target: string;
  targetLabel: string;
  type: string;
};
type nodeMap = {
  [key: string]: node;
};

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
  console.log(modelName, "modelName");
};

const viewDrawer = (state: boolean) => {
  drawerState.value = state;
  console.log(modelCache.value, "1111");
};

// 自定义事件：接收gpt返回的mermaidCode
const mermaidContent = (mermaidCode: any) => {
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
    console.log(jsonString, "jsonString");

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
const parseMermaidToJson = (mermaidText: string) => {
  const nodes: node[] = [];
  const links: link[] = [];
  const nodeLabelList: string[] = [];
  const nodeMap: nodeMap = {};
  let nodeId = 0;

  // 解析节点和链接
  mermaidText.split("\n").forEach((line) => {
    if (line.trim() === "") return;
    const parts = line.split("-->");
    // 长度小于2，说明不含节点和联系
    if (parts.length < 2) return;
    const sourcePart = parts[0].trim();
    const targetPart = parts[1].trim();

    const sourceMatch = sourcePart.match(/(\w+)\[(.*?)\]:::(\w+)/);
    const targetMatch = targetPart.match(/(\w+)\[(.*?)\]:::(\w+)/);

    // 只有存在:::才有可能是一个新定义的节点,确定不重复就添加，正常不用去重，去重是为了防止gpt脑残重复定义

    if (sourceMatch && sourceMatch.length > 2) {
      let newNode = {
        id: nodeId.toString(),
        label: sourceMatch[2],
        type: sourceMatch[3],
        position: { x: 0, y: 0 },
        data: { description: "", label: sourceMatch[2] },
      };
      if (!nodeLabelList.includes(newNode.label)) {
        nodes.push(newNode);
        nodeLabelList.push(newNode.label);
        nodeId++;
        // 增加字母和node的映射关系
        nodeMap[sourceMatch[1]] = newNode;
      }
    }
    if (targetMatch && targetMatch.length > 2) {
      let newNode = {
        id: nodeId.toString(),
        label: targetMatch[2],
        type: targetMatch[3],
        position: { x: 0, y: 0 },
        data: { description: "", label: targetMatch[2] },
      };
      if (!nodeLabelList.includes(newNode.label)) {
        nodes.push(newNode);
        nodeLabelList.push(newNode.label);
        nodeId++;
      }
      // 增加字母和node的映射关系
      nodeMap[targetMatch[1]] = newNode;
    }
    if (parts.length === 2) {
      let source: any;
      let target: any;
      if (!sourceMatch) {
        source = parts[0].trim();
      } else {
        source = sourceMatch[1].trim();
      }
      if (!targetMatch) {
        target = parts[1].trim();
      } else {
        target = targetMatch[1].trim();
      }

      links.push({
        id: `${nodeMap[source].id}-${nodeMap[target].id}`,
        source: nodeMap[source].id,
        sourceLabel: nodeMap[source].label,
        target: nodeMap[target].id,
        targetLabel: nodeMap[target].label,
        type: "animation",
        // type: "",
      });
    }
  });
  const reproduceDoc = {
    nodes: nodes,
    links: links,
  };
  console.log(reproduceDoc, "reproduceDoc");

  // 输出JSON格式的数据
  return reproduceDoc;
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
  "http://112.4.132.6:8083/data/385614f9-86ec-4e24-9a91-0772d15b0b3b"
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
  console.log(file, "submitFile");
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
        padding: 2vh 5vh;
        height: 120px;
      }
      .pdfContainer {
        width: 90%;
        height: calc(96vh - 300px);
        box-shadow: 5px 5px 12px 0 rgba(0, 0, 0, 0.3);

        margin-left: 5%;

        border-radius: 10px;
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
</style>
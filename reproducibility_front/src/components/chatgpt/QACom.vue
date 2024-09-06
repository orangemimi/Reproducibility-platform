<template>
  <div
    class="flex flex-col h-full bg-white shadow-lg rounded-lg overflow-hidden"
  >
    <!-- 顶部标题栏 -->
    <div
      class="flex justify-between w-full px-6 py-4 bg-gradient-to-r from-blue-500 to-indigo-500 text-white flex-col"
    >
      <div class="font-bold text-lg">Reproducibility-Assistant</div>
      <div class="mt-1 text-xs opacity-70">
        基于人机迭代交互的地理分析实验工作流自动生成
      </div>
    </div>

    <!-- 聊天记录滚动区域 -->
    <div class="flex-1 mx-2 mt-4 mb-2 overflow-y-auto" ref="chatListDom">
      <div
        class="group flex flex-col px-6 py-4 hover:bg-gray-50 rounded-lg transition-colors duration-200"
        v-for="item of messageList.filter((v) => v.role !== 'system')"
        :key="item.content"
      >
        <div class="flex justify-between items-center mb-2">
          <div class="font-bold text-blue-700">
            {{ roleAlias[item.role] }}：
          </div>
          <Copy
            class="invisible group-hover:visible text-gray-400"
            :content="item.content"
          />
        </div>
        <div>
          <div
            class="prose text-base text-gray-800 leading-relaxed"
            v-if="item.content"
            v-html="md.render(item.content)"
          ></div>
          <Loading v-else />
        </div>
      </div>
    </div>

    <!-- 输入框区域 -->
    <div class="w-full p-4 bg-gray-100 border-t border-gray-300">
      <div class="flex">
        <input
          class="input flex-1 p-1 border border-gray-300 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
          type="text"
          placeholder="请输入"
          v-model="messageContent"
          @keydown.enter="isTalking || sendOrSave()"
        />
        <button
          class="btn ml-2 mr-1 px-4 py-2 bg-blue-500 text-white rounded-lg shadow hover:bg-blue-600 disabled:opacity-50"
          :disabled="isTalking"
          @click="sendOrSave()"
        >
          发送
        </button>
        <button
          class="btn ml-2 mr-1 px-4 py-2 bg-red-500 text-white rounded-lg shadow hover:bg-red-600 disabled:opacity-50"
          :disabled="isTalking"
          @click="finishChat()"
        >
          完成
        </button>
      </div>
    </div>
  </div>
</template>
  
  <script setup lang="ts">
// @ts-ignore
import type { ChatMessage } from "@/types/gpt.ts";
import { ref, watch } from "vue";
// @ts-ignore
import { chat } from "@/lib/gpt";
// @ts-ignore
import Loading from "./LoadingCom.vue";
// @ts-ignore
import Copy from "./CopyCom.vue";
// @ts-ignore
import { md } from "@/lib/markdown.ts";

let isTalking = ref(false);
let messageContent = ref("");
const chatListDom = ref<HTMLDivElement>();
const decoder = new TextDecoder("utf-8");
// 是否上传了pdf
const initEndFlag = ref(false);

const emits = defineEmits(["mermaidContent"]);

const props = defineProps({
  pdfContent: {
    type: String,
  },
});

const roleAlias = {
  user: "ME",
  assistant: "Reproducibility-Assistant",
  system: "System",
};
const messageList = ref<ChatMessage[]>([
  {
    role: "system",
    content: `你是ChatGPT，由OpenAI训练的大型语言模型，你是一个地理建模与模拟领域的实验专家。用户会给你发送地理建模与模拟领域的学术论文，你需要根据论文内容，生成这篇论文中所开展实验的工作流程。
        实验流程所包括的一些主体包括:要解决的问题、使用的地理分析模型、空间分析方法、数据处理方法、数据集、实验结果、实验结论，你生成的实验工作流程要足够细致，以便读者能够根据该工作流程，复现论文中的实验。`,
  },
  {
    role: "assistant",
    content: `你好，我是由OpenGMS开发的地理分析实验可复现工作流智能助手；
      您可以在左侧的文件上传区域，上传您要进行复现的实验论文；
      我们将基于领域知识增强及微调训练后的OpenAI GPT-4o模型，自动生成初步的实验复现工作流程；
      进一步的，您可以通过多轮对话的形式，指引GPT-4o模型进行进一步的工作流程优化，直至生成的复现流程满足您的需求；`,
  },
]);

const sendChatMessage = async (content: string = messageContent.value) => {
  try {
    isTalking.value = true;
    // 这里为什么要用 messageList.value.length === 2 来判断？  删掉之后好像也没有什么影响，那就忽略吧
    // if (messageList.value.length === 2) {
    //   messageList.value.pop();
    // }

    messageList.value.push({ role: "user", content });
    // console.log('messageList', messageList);

    clearMessageContent();
    messageList.value.push({ role: "assistant", content: "" });
    // console.log('messageList', messageList);

    const { body, status } = await chat(messageList.value);

    if (body) {
      const reader = body.getReader();
      await readStream(reader, status);
    }
  } catch (error: any) {
    appendLastMessageContent(error);
  } finally {
    isTalking.value = false;
  }
};

const readStream = async (
  reader: ReadableStreamDefaultReader<Uint8Array>,
  status: number
) => {
  let partialLine = "";

  // eslint-disable-next-line no-constant-condition
  while (true) {
    // eslint-disable-next-line no-await-in-loop
    const { value, done } = await reader.read();
    if (done) break;

    const decodedText = decoder.decode(value, { stream: true });

    if (status !== 200) {
      const json = JSON.parse(decodedText); // start with "data: "
      const content = json.error.message ?? decodedText;
      appendLastMessageContent(content);
      return;
    }

    const chunk = partialLine + decodedText;
    const newLines = chunk.split(/\r?\n/);

    partialLine = newLines.pop() ?? "";

    for (const line of newLines) {
      if (line.length === 0) continue; // ignore empty message
      if (line.startsWith(":")) continue; // ignore sse comment message
      if (line === "data: [DONE]") return; //

      const json = JSON.parse(line.substring(6)); // start with "data: "
      const content =
        status === 200
          ? json.choices[0].delta.content ?? ""
          : json.error.message;
      appendLastMessageContent(content);
    }
  }
};

const appendLastMessageContent = (content: string) =>
  (messageList.value[messageList.value.length - 1].content += content);

const sendOrSave = async () => {
  if (!messageContent.value.length) return;

  if (initEndFlag.value === false) {
    await sendChatMessage();
  } else {
    messageContent.value =
      messageContent.value +
      "  请你针对于我的问题，给出文字性的回答，在这一部分先不要涉及memarid语言，memarid语言在下一步会要求你回答";
    await sendChatMessage();
    messageContent.value =
      "根据刚才你对用户问题的回答，请你对你之前生成的mermaid进行修改(mermaid code依然用英文)，不要只回答你修改的部分，要在原有mermaid的基础上进行修改，修改后的mermaid如下，只回答mermaid，不要回答其他的";
    await sendChatMessage();
    emits("mermaidContent", messageList.value[messageList.value.length - 1]);
  }
};

const clearMessageContent = () => (messageContent.value = "");

// const scrollToBottom = () => {
//   if (!chatListDom.value) return;
//   scrollTo(0, chatListDom.value.scrollHeight);
// };

const workFlowInit = async (pdfContent: string) => {
  let paperQuestion = `用户上传了一篇论文，内容如下：${pdfContent}，请你总结一下这篇论文在做什么`;

  await sendChatMessage(paperQuestion);
  await new Promise((resolve) => setTimeout(resolve, 1000));
  clearMessageContent();
  clearUserInput();

  let mermaidQuestion =
    "请你以mermaid语法生成该论文的详细实验工作流程，且mermaid语法中均使用英文" +
    "要求：将元素分为过程和数据两类" +
    "这是一个样例，请你按照论文内容和这个样例生成基于这篇论文实现步骤的mermaid流程图：" +
    "graph LR\n" +
    "A[xxx市DEM数据]:::data --> B[DEM填洼]:::process\n" +
    "B --> C[无洼地DEM数据]:::data\n" +
    "C --> D[流向分析]:::process\n" +
    "D --> E[分析数据1]:::data\n" +
    "D --> F[分析数据2]:::data\n" +
    "类似这样直到完成整个流程,请注意，每个process都需要有输入和输出数据，也就是说：" +
    "1、要写出每两个process之间的中间数据\n" +
    "2、流程图最顶层和最底层的项目一定是数据项\n" +
    "请你一定记住，我要的是非常详细的实验流程，让用户一看就知道怎么复现该论文的实验，请用中文。请你只回答mermaid的类型定义+语言，不要回答任何其他的内容，meriad如下";

  await sendChatMessage(mermaidQuestion);

  clearMessageContent();

  clearUserInput();
  initEndFlag.value = true;
  emits("mermaidContent", messageList.value[messageList.value.length - 1]);
};

const clearUserInput = () => {
  // messageList是一个对象数组，我想要删除倒数第二个对象
  messageList.value.splice(-2, 1);
};

// 监听flag的变化，如果flag为true，就调用workFlowInit函数
watch(
  () => props.pdfContent,
  (newValue) => {
    if (newValue) {
      workFlowInit(newValue);
    }
  }
);

const finishChat = () => {
  isTalking.value = true;
  // 推荐资源
  generateResourceList();
  // 生成复现文档
  generateReproduceDoc();
};

const generateResourceList = async () => {
  // 获取最近一次的对话,即获取messageList 的最后一个对象
  const lastMessage = messageList.value[messageList.value.length - 1].content;
  // console.log(messageList);

  const modelmessageList = ref<ChatMessage[]>([
    {
      role: "system",
      content: `你是ChatGPT，你是一个地理建模与模拟领域的实验专家。用户会给你发送mermaid形式的实验流程图，该实验流程图是从某篇地理建模领域抽取出来的分析流程，我需要你从这个实现流程中，抽取出所涉及的所有地理分析模型或者深度学习算法，请你以字符串数组的形式返回，不要返回其他的任何内容。 例如['SWAT模型', '随机森林算法', 'AlexNet算法']`,
    },
    {
      role: "assistant",
      content: `请将你的实验流程以mermaid形式发送给我，我将返回一个字符串数组，里面包含实验流程中所涉及的的所有地理分析模型或者深度学习算法。`,
    },
    {
      role: "user",
      content: `我的实验流程为${lastMessage}`,
    },
  ]);

  const dataMessageList = ref<ChatMessage[]>([
    {
      role: "system",
      content: `你是ChatGPT，你是一个地理建模与模拟领域的实验专家。用户会给你发送mermaid形式的实验流程图，该实验流程图是从某篇地理建模领域抽取出来的分析流程，我需要你从这个实现流程中，抽取出所涉及的所有使用的数据集，请你以字符串数组的形式返回，不要返回其他的任何内容。 例如['江苏省数字高程数据', '陕西省50米分辨率DEM', '黄河流域90米分辨率的DEM数据']`,
    },
    {
      role: "assistant",
      content: `请将你的实验流程以mermaid形式发送给我，我将返回一个字符串数组，里面包含实验流程中所涉及的的所有数据集。`,
    },
    {
      role: "user",
      content: `我的实验流程为${lastMessage}`,
    },
  ]);

  const dataMethodMessageList = ref<ChatMessage[]>([
    {
      role: "system",
      content: `你是ChatGPT，你是一个地理建模与模拟领域的实验专家。用户会给你发送mermaid形式的实验流程图，该实验流程图是从某篇地理建模领域抽取出来的分析流程，我需要你从这个实现流程中，抽取出所涉及的所有使用数据处理方法、过程转换方法、空间分析方法等，请你以字符串数组的形式返回，不要返回其他的任何内容。 例如['Conversion From CSV File To SHP File', 'Shp Convert To Geojson', 'DEM(GRID) To Voronoi Grid(.shp)']`,
    },
    {
      role: "assistant",
      content: `请将你的实验流程以mermaid形式发送给我，我将返回一个字符串数组，里面包含实验流程中所涉及的的所有数据处理方法、过程转换方法、空间分析方法等。`,
    },
    {
      role: "user",
      content: `我的实验流程为${lastMessage}`,
    },
  ]);

  const modelList = await generateSingleAnswer(modelmessageList);
  const dataList = await generateSingleAnswer(dataMessageList);
  const dataMethodList = await generateSingleAnswer(dataMethodMessageList);
  console.log(modelList, dataList, dataMethodList, 10101);

  // 这里下一步，分别遍历这三个数组，并请求资源检索的API，生成对应的资源推荐列表
};

const generateReproduceDoc = () => {
  const reproduceDoc = "";
  console.log(reproduceDoc, "这是复现文档");
};

const generateSingleAnswer = async (SingleMessageList: any) => {
  const { body, status } = await chat(SingleMessageList.value);

  if (!body) {
    throw new Error("Failed to get response body");
  }

  const reader = body.getReader();
  const decoder = new TextDecoder();
  let partialLine = "";
  let result = "";
  let doneReading = false;

  while (!doneReading) {
    const { value, done } = await reader.read();
    doneReading = done; // 更新循环条件

    if (value) {
      const decodedText = decoder.decode(value, { stream: true });
      const chunk = partialLine + decodedText;
      const newLines = chunk.split(/\r?\n/);
      partialLine = newLines.pop() ?? "";

      for (const line of newLines) {
        if (line.length === 0) continue; // 忽略空消息
        if (line.startsWith(":")) continue; // 忽略 SSE 注释消息
        if (line === "data: [DONE]") {
          console.log(result); // 在返回之前打印结果
          return result; // 处理完成消息
        }

        const json = JSON.parse(line.substring(6)); // 从 "data: " 开始解析
        const content =
          status === 200
            ? json.choices[0].delta.content ?? ""
            : json.error.message;
        result += content;
      }
    }
  }

  return result;
};
</script>
  
<style scoped>
pre {
  font-family: -apple-system, "Noto Sans", "Helvetica Neue", Helvetica,
    "Nimbus Sans L", Arial, "Liberation Sans", "PingFang SC", "Hiragino Sans GB",
    "Noto Sans CJK SC", "Source Han Sans SC", "Source Han Sans CN",
    "Microsoft YaHei", "Wenquanyi Micro Hei", "WenQuanYi Zen Hei", "ST Heiti",
    SimHei, "WenQuanYi Zen Hei Sharp", sans-serif;
}
</style>
  
<template>
  <div
    class="flex flex-col h-full bg-white shadow-lg rounded-lg overflow-hidden"
  >
    <!-- 顶部标题栏 -->
    <div
      class="flex items-center justify-between w-full px-6 py-4 bg-gradient-to-r from-blue-500 to-indigo-500 text-white"
    >
      <div class="text-1vw font-bold">OpenGMS-IDE</div>
      <div class="gptTitle ml-4 text-sm opacity-75">
        基于 OpenAI 的 ChatGPT 自然语言模型人工智能对话
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
          <Loding v-else />
        </div>
      </div>
    </div>

    <!-- 输入框区域 -->
    <div class="w-full p-4 bg-gray-100 border-t border-gray-300">
      <div class="flex">
        <input
          class="input flex-1 p-4 border border-gray-300 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
          type="text"
          placeholder="请输入"
          v-model="messageContent"
          @keydown.enter="isTalking || sendOrSave()"
        />
        <button
          class="btn ml-4 px-6 py-2 bg-blue-500 text-white rounded-lg shadow hover:bg-blue-600 disabled:opacity-50"
          :disabled="isTalking"
          @click="sendOrSave()"
        >
          发送
        </button>
      </div>
    </div>
  </div>
</template>
    
  <script setup lang="ts">
// @ts-ignore
import type { ChatMessage } from "@/types/gpt.ts";
import { ref } from "vue";
// @ts-ignore
import { chat } from "@/lib/gpt";
// @ts-ignore
import Loding from "./LoadingCom.vue";
// @ts-ignore
import Copy from "./CopyCom.vue";
// @ts-ignore
import { md } from "@/lib/markdown.ts";

let isTalking = ref(false);
let messageContent = ref("");
const chatListDom = ref<HTMLDivElement>();
const decoder = new TextDecoder("utf-8");
const roleAlias = { user: "ME", assistant: "OpenGMS-IDE", system: "System" };
const messageList = ref<ChatMessage[]>([
  {
    role: "system",
    content: "你是 ChatGPT，OpenAI 训练的大型语言模型，尽可能简洁地回答。",
  },
  {
    role: "assistant",
    content: `你好，我是AI语言模型，我可以提供一些常用服务和信息，例如：
    
    1. 翻译：我可以把中文翻译成英文，英文翻译成中文，还有其他一些语言翻译，比如法语、日语、西班牙语等。
    
    2. 咨询服务：如果你有任何问题需要咨询，例如健康、法律、投资等方面，我可以尽可能为你提供帮助。
    
    3. 闲聊：如果你感到寂寞或无聊，我们可以聊一些有趣的话题，以减轻你的压力。
    
    请告诉我你需要哪方面的帮助，我会根据你的需求给你提供相应的信息和建议。`,
  },
]);

const sendChatMessage = async (content: string = messageContent.value) => {
  try {
    isTalking.value = true;
    // 这里为什么要用 messageList.value.length === 2 来判断？  删掉之后好像也没有什么影响，那就忽略吧
    if (messageList.value.length === 2) {
      messageList.value.pop();
    }
    messageList.value.push({ role: "user", content });
    clearMessageContent();
    messageList.value.push({ role: "assistant", content: "" });

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

const sendOrSave = () => {
  if (!messageContent.value.length) return;

  sendChatMessage();
};

const clearMessageContent = () => (messageContent.value = "");

// const scrollToBottom = () => {
//   if (!chatListDom.value) return;
//   scrollTo(0, chatListDom.value.scrollHeight);
// };

// watch(messageList.value, () => nextTick(() => scrollToBottom()));
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
    
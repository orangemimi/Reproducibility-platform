<template>
  <div
    class="custom-model"
    :style="{ backgroundColor: bgColor }"
    @dblclick="bindingModel"
  >
    <div class="label">{{ modelNodeProps.data.label }}</div>
    <Handle type="target" :position="Position.Top" @connection="onConnection" />
    <Handle
      type="source"
      :position="Position.Bottom"
      @connection="onConnection"
    />
  </div>
</template>
  
  <script setup>
import { Position, Handle } from "@vue-flow/core";
import {
  defineEmits,
  ref,
  defineExpose,
  onMounted,
  computed,
  watch,
} from "vue";

// props were passed from the slot using `v-bind="customNodeProps"`
const props = defineProps(["modelNodeProps", "selected"]);
const emit = defineEmits();

const targetConnected = ref(false);
const sourceConnected = ref(false);

// //定义背景颜色
const bgColor = computed(() => {
  if (props.modelNodeProps.data.state == "error") {
    return "#ffab2d";
  }

  if (props.modelNodeProps.data.state == "success") {
    // return "#00A86B";
    // return "#23d96e";
    return "#51ec7b";
    return "#0bc261";
  }

  if (props.modelNodeProps.data.state == "init") {
    return "#87cefa";
  }
});

function bindingModel() {
  emit("node-dblclick", props);
}

function onConnection(params) {
  console.log(params.targetHandle);
  if (params.targetHandle) {
    targetConnected.value = true;
  }
  if (params.sourceHandle) {
    sourceConnected.value = true;
  }
}
</script>
  
<style scoped>
.custom-model {
  display: flex;
  /* border-radius: 30px; */
  width: 100px;
  height: 30px;
  font-size: 12px;
  justify-content: center;
  align-items: center;
  background-color: #eeeeee;
  box-shadow: 2px 2px 5px #ddd;
  border: 1px solid #000000;
}

.custom-model .vue-flow__handle {
  border: none;
  height: unset;
  width: unset;
  background-color: #000;
  border-radius: 50%;
}

.label {
  height: 30px;
  width: 100px;
  display: flex;
  /* word-break: break-all; */
  white-space: nowrap;
  color: #000;
  font-size: 10px;
  justify-content: center;
  align-items: center;
  /* overflow: visible; */
}
</style>
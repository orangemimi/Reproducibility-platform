<template>
  <div class="custom-data" :style="{ backgroundColor: bgColor }">
    <Handle id="target-top" type="target" :position="Position.Top" />
    <div class="label">{{ props.dataNodeProps.data.label }}</div>
    <Handle id="source-bottom" type="source" :position="Position.Bottom" />
    <!-- <Handle id="target-left" type="target" :position="Position.Left" /> -->
    <!-- <Handle id="source-right" type="source" :position="Position.Right" /> -->
  </div>
</template>
  
  <script setup>
import { Position, Handle } from "@vue-flow/core";
import { computed, watch } from "vue";

//定义节点属性
const props = defineProps(["dataNodeProps"]);

// //定义背景颜色
const bgColor = computed(() => {
  if (props.dataNodeProps.data.state == "success") {
    return "#51ec7b";
  } else if (props.dataNodeProps.data.state == "error") {
    return "#ffab2d";
  }
  if (props.dataNodeProps.data.dataType == "input") {
    return "#ffab2d";
  } else if (props.dataNodeProps.data.dataType == "parameter") {
    return "#fce08f";
  } else if (props.dataNodeProps.data.dataType == "output") {
    return "#b9e6d3";
  } else if (props.dataNodeProps.data.dataType == "intermediate") {
    return "#ffe6d3";
  }
});
</script>
  
<style scoped>
.custom-data {
  display: flex;
  width: 100px;
  height: 30px;
  font-size: 12px;
  justify-content: center;
  align-items: center;
  border: 1px solid #000000;
  background-color: #eeeeee;
  box-shadow: 2px 2px 5px #ddd;
}

.custom-data .vue-flow__handle {
  border: none;
  height: unset;
  width: unset;
  background-color: #000;
  border-radius: 50%;
}

.label {
  word-wrap: break-word;
  display: flex;
  color: #000;
  font-size: 10px;
  justify-content: center;
  align-items: center;
}
</style>
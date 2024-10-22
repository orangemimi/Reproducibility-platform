<template>
  <div class="nodeBound">
    <div class="modelContent">
      <div
        class="modelName"
        v-if="
          props.editModelNode.data?.behavior &&
          props.editModelNode.data.behavior.inputs.length > 0
        "
      >
        {{ props.editModelNode.data.label }}
      </div>

      <!-- Inputs Section -->
      <h1 class="dataTitle">inputs：</h1>
      <div class="dataList">
        <div
          class="dataItem"
          v-for="item in props.editModelNode.data?.behavior.inputs"
          :key="item.eventId"
        >
          <span style="color: red">*</span>
          {{ item.name }}:
          <br />
          <el-select
            :disabled="judgeInputState(item)"
            v-model="item.dataRelation.value"
            @change="handleSelectChange(item)"
            style="margin-top: 10px"
            placeholder="Connected"
          >
            <el-option
              v-for="option in dataOptions"
              :key="option.id"
              :label="option.name"
              :value="option.value"
            />
          </el-select>
        </div>
      </div>

      <!-- Parameters Section -->
      <h1
        class="dataTitle"
        v-if="props.editModelNode.data?.behavior.parameters.length > 0"
      >
        parameters:
      </h1>
      <div class="dataList">
        <div
          class="dataItem"
          v-for="item in props.editModelNode.data?.behavior.parameters"
          :key="item.eventId"
        >
          <span style="color: red">*</span>
          {{ item.name }}:
          <br />
          <el-input
            v-model="item.dataRelation.value"
            @input="handleInputChange(item)"
            style="margin-top: 10px"
            placeholder="Enter value"
          />
        </div>
      </div>
    </div>

    <div class="foot">
      <el-button type="warning" @click="cancel">Cancel</el-button>
      <el-button type="primary" @click="confirm">Confirm</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { defineEmits, defineProps, onMounted, ref, watch, Ref } from "vue";
// @ts-ignore
import { getFolders } from "@/api/request";

const emits = defineEmits(["nodeEdited"]);
const props = defineProps({
  editModelNode: {
    type: Object,
    default: () => ({
      data: {
        label: "",
        behavior: {
          inputs: [],
          parameters: [],
        },
      },
    }),
  },
  nodes: {
    type: Array,
    default: () => [],
  },
  edges: {
    type: Array,
    default: () => [],
  },
  projectId: {
    type: String,
    default: () => "",
  },
});

// 判断输入框是否禁用
const judgeInputState = (item: any) => {
  if (item.dataRelation.type === "connection") {
    return true;
  }
  return false;
};

// 初始化或更新 dataRelation 对象
const initializeDataRelations = () => {
  // 遍历 inputs
  props.editModelNode.data.behavior.inputs.forEach((input: any) => {
    if (!input.dataRelation) {
      input.dataRelation = { value: "Select Data", type: "file", label: "" };
    } else {
      input.dataRelation.value = input.dataRelation.value ?? "Select Data";
      input.dataRelation.type = input.dataRelation.type ?? "file";
      input.dataRelation.label = input.dataRelation.label ?? "";
    }
  });

  // 遍历 parameters
  props.editModelNode.data.behavior.parameters.forEach((parameter: any) => {
    if (!parameter.dataRelation) {
      parameter.dataRelation = {
        value: "input parameter",
        type: "parameter",
        label: "",
      };
    } else {
      parameter.dataRelation.value =
        parameter.dataRelation.value ?? "input parameter";
      parameter.dataRelation.type = parameter.dataRelation.type ?? "parameter";
      parameter.dataRelation.label = parameter.dataRelation.label ?? "";
    }
  });
};

const confirm = () => {
  console.log("Updated editModelNode:", props.editModelNode);
  console.log("nodes", props.nodes);

  emits("nodeEdited", props.editModelNode);
};

const cancel = () => {
  emits("nodeEdited", null);
};

const dataOptions: Ref<any> = ref([]);
// 获取选择的 option 的标签,柯里化写法
const handleSelectChange = (item: any) => (value: string) => {
  const selectedOption = dataOptions.value.find(
    (option: any) => option.value === value
  );
  item.dataRelation.label = selectedOption ? selectedOption.name : "";
};

const handleInputChange = (item: any) => (value: string) => {
  item.dataRelation.label = value;
};

onMounted(async () => {
  let folders = (await getFolders()) as unknown as Array<any>;
  let projectFolders = folders[0].children.filter(
    (item: any) => item.tagId === props.projectId
  );
  dataOptions.value = projectFolders[0].dataItemList;
});

// 初次调用函数，确保 dataRelation 存在
initializeDataRelations();

// 监听 editModelNode 的变化
watch(() => props.editModelNode, initializeDataRelations, { deep: true });
</script>


<style scoped lang="scss">
.nodeBound {
  height: 100%;
  width: 100%;
  overflow: hidden;
  display: flex;
  flex-wrap: wrap;
  background: linear-gradient(
      45deg,
      transparent 33.33%,
      rgba(57, 144, 179, 0.1) 33.33%,
      rgba(0, 0, 0, 0.1) 66.66%,
      transparent 66.66%
    ),
    #e6f0ff;
  background-size: 30px 30px;

  .modelContent {
    width: 100%;
    max-height: 90%;
    height: fit-content;
    background-color: rgba(255, 255, 255, 0.5); // 半透明的白色
    overflow: auto;
    .modelName {
      font-size: 24px;
      font-weight: bolder;
      text-align: center;
      margin: 20px;
      color: #ff7006;
      // -webkit-text-stroke: 1px #fff;
      text-shadow: -2px -2px 0 #fff, 2px -2px 0 #fff, -2px 2px 0 #fff,
        2px 2px 0 #fff;
    }
    .dataTitle {
      font-size: 20px;
      font-weight: bold;
      margin: 20px 10px;
    }

    .dataList {
      .dataItem {
        font-size: 16px;
        margin: 20px 10px;
      }
    }
  }
  .foot {
    padding-top: 20px;
    width: 100%;
    height: 100%;
    display: flex;
    justify-content: space-evenly;
    background-color: rgba(255, 255, 255, 0.5); // 半透明的白色
  }
}
</style>
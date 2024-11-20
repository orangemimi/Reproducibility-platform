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
      <h1
        class="dataTitle"
        v-if="props.editModelNode.data?.behavior.inputs.length > 0"
      >
        inputs：
      </h1>
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
              :label="option.name + option.suffix"
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
      <div class="codeModelEdit">
        <div class="codeModelContent">
          <!-- code model的config按钮 -->
          <el-dialog
            class="codeController"
            v-model="codeEditorVisible"
            title="Code Model Configuration"
          >
            <div style="margin-bottom: 20px">Model Info and Data Events:</div>
            <div style="display: flex; align-items: center">
              ModelName:<span style="color: red">*</span>
              <el-input
                v-model="modelName"
                style="width: 200px; margin-right: 10px"
              ></el-input>

              <span>ModelDescription:</span>
              <el-input v-model="modelDescription"></el-input>
            </div>

            <el-table
              :data="eventsTableData"
              style="width: 100%"
              max-height="200"
            >
              <el-table-column prop="type" label="type" width="150">
                <template #default="scope">
                  <el-select v-model="scope.row.type" placeholder="Select Type">
                    <el-option label="input" value="input" />
                    <el-option label="parameter" value="parameter" />
                    <el-option label="output" value="output" />
                  </el-select>
                </template>
              </el-table-column>
              <el-table-column prop="name" label="name" width="120">
                <template #default="scope">
                  <el-input
                    v-model="scope.row.name"
                    placeholder="Enter name"
                  ></el-input>
                </template>
              </el-table-column>
              <el-table-column prop="description" label="description">
                <template #default="scope">
                  <el-input
                    v-model="scope.row.description"
                    placeholder="Enter description"
                  ></el-input>
                </template>
              </el-table-column>
              <el-table-column fixed="right" label="Operations" width="120">
                <template #default="scope">
                  <el-button
                    link
                    type="primary"
                    size="small"
                    @click.prevent="deleteRow(scope.$index)"
                  >
                    Remove
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
            <el-button class="mt-4" style="width: 100%" @click="onAddItem">
              Add Item
            </el-button>
            <div style="margin: 20px 0">Model Code:</div>

            <div class="codeContent">
              <codeEditor
                :language="'python'"
                :value="props.editModelNode.data.behavior.code || ''"
                @changed="changeTextarea"
                style="height: 40vh"
              ></codeEditor>
            </div>
            <template #footer>
              <div class="dialog-footer">
                <!-- <el-button @click="codeEditorVisible = false"></el-button> -->
                <el-button type="primary" @click="confirmModelConfiguration">
                  Confirm
                </el-button>
              </div>
            </template>
          </el-dialog>
        </div>
      </div>
    </div>

    <div class="foot">
      <el-button
        type="primary"
        v-if="props.editModelNode.type === 'codeModel'"
        @click="codeEditorVisible = true"
      >
        Config
      </el-button>
      <el-button type="warning" @click="cancelNodeEdit">Cancel</el-button>
      <el-button type="primary" @click="confirmNodeEdit">Confirm</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { defineEmits, defineProps, onMounted, ref, watch, Ref } from "vue";
// @ts-ignore
import { getFolders } from "@/api/request";
import codeEditor from "_com/codingOL/CommonEditor.vue";

const emits = defineEmits(["nodeEdited"]);
const props = defineProps({
  editModelNode: {
    type: Object,
    default: () => ({
      data: {
        label: "",
        behavior: {
          code: ``,
          dependency: ``,
          inputs: [],
          parameters: [],
          outputs: [],
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

// 退出节点
const confirmNodeEdit = () => {
  emits("nodeEdited", null);
};

const cancelNodeEdit = () => {
  emits("nodeEdited", null);
};

const dataOptions: Ref<any> = ref([]);
// 获取选择的 option 的标签,柯里化写法
const handleSelectChange = (item: any) => {
  const selectedOption = dataOptions.value.find(
    (option: any) => option.value == item.dataRelation.value
  );
  item.dataRelation.label = selectedOption
    ? selectedOption.name + selectedOption.suffix
    : "error";
};

// 将输入的参数写入到 node 中
const handleInputChange = (item: any) => (value: string) => {
  item.dataRelation.label = value;
};

/**
 * code edit 部分的解析与校验
 */
interface eventInfo {
  type: string;
  name: string;
  description: string;
}

const codeEditorVisible = ref(false);
const eventsTableData: Ref<eventInfo[]> = ref([]);
const modelDescription = ref("");
const modelName = ref("");

const onAddItem = () => {
  const newRow = {
    type: "input",
    name: "",
    description: "",
  };
  eventsTableData.value.push(newRow);
};

// 删除某一行
const deleteRow = (index: number) => {
  eventsTableData.value.splice(index, 1);
};

// ModelConfig dialogue编辑完毕
const confirmModelConfiguration = () => {
  // 解析modelInfo
  parseModelInfo();
  // 将编辑好的events信息保存到 code model 节点中
  saveEventsInfoToNode();
  codeEditorVisible.value = false;
  // 让父组件生成子节点
  emits("nodeEdited", "generateNodes", props.editModelNode);
};

// 将输入框的数据同步到 code
const changeTextarea = (val: string) => {
  if (props.editModelNode.data.behavior && val) {
    props.editModelNode.data.behavior.code = val;
  }
};

// 处理模型信息
const parseModelInfo = () => {
  props.editModelNode.data.label = modelName.value;
  props.editModelNode.data.description = modelDescription.value;
};

onMounted(async () => {
  let folders = (await getFolders()) as unknown as Array<any>;
  let projectFolders = folders[0].children.filter(
    (item: any) => item.tagId === props.projectId
  );
  dataOptions.value = projectFolders[0].dataItemList;
});

// 从node节点数据中获取events数据
const getEventsInfoFromNode = () => {
  const inputs = props.editModelNode.data.behavior.inputs.map((item: any) => {
    return {
      type: "input",
      name: item.name,
      description: item.description,
    };
  });
  const parameters = props.editModelNode.data.behavior.parameters.map(
    (item: any) => {
      return {
        type: "parameter",
        name: item.name,
        description: item.description,
      };
    }
  );
  const outputs = props.editModelNode.data.behavior.outputs.map((item: any) => {
    return {
      type: "output",
      name: item.name,
      description: item.description,
    };
  });
  return [...inputs, ...parameters, ...outputs];
};

// 将events数据保存到nodes中
const saveEventsInfoToNode = () => {
  console.log("Updated editModelNode:", eventsTableData.value);
  props.editModelNode.data.behavior.inputs = eventsTableData.value
    .filter((item) => item.type === "input")
    .map((item) => ({
      name: item.name,
      description: item.description,
    }));

  props.editModelNode.data.behavior.parameters = eventsTableData.value
    .filter((item) => item.type === "parameter")
    .map((item) => ({
      name: item.name,
      description: item.description,
    }));

  props.editModelNode.data.behavior.outputs = eventsTableData.value
    .filter((item) => item!.type === "output")
    .map((item) => ({
      name: item.name,
      description: item.description,
    }));
};

// 初次调用函数，确保 dataRelation 存在
initializeDataRelations();

// 监听 editModelNode 的变化
watch(
  () => props.editModelNode,
  () => {
    initializeDataRelations();
    eventsTableData.value = getEventsInfoFromNode() as any;

    modelName.value = props.editModelNode.data.label
      ? props.editModelNode.data.label
      : "";
    modelDescription.value = props.editModelNode.data.description
      ? props.editModelNode.data.description
      : "";
  },
  { deep: true, immediate: true }
);
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
      word-wrap: break-word;
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
    .codeModelContent {
      width: fit-content;
      height: fit-content;
      margin: 10px;
      display: flex;
      flex-wrap: wrap;
      justify-content: center;
      .codeController {
        .codeContent {
          height: 40vh;
        }
      }
      // box-shadow: 2px 2px 2px 2px rgba(0, 0, 0, 0.1);
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

:deep(.CodeMirror-sizer) {
  margin-left: 29px !important;
}
</style>
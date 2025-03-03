<template>
  <aside style="width: 30%">
    <el-collapse v-model="activeName" accordion style="height: 100%">
      <el-collapse-item title="Model List" name="1">
        <div class="modelList">
          <div
            class="modelNode"
            style="background-color: antiquewhite"
            :draggable="true"
            @dragstart="onDragStart($event, model, 'codeModel')"
          >
            Add a code model
          </div>
          <div
            class="modelNode"
            v-for="model in allModelsWithUser"
            :key="model.serviceId"
            :draggable="true"
            @dragstart="onDragStart($event, model, 'model')"
          >
            {{ model.name }}
          </div>
        </div>
      </el-collapse-item>
      <el-collapse-item title="Data List " name="2">
        <div class="modelList">
          <DataTable :scenarioId="scenarioId" style="opacity: 0.85"></DataTable>
        </div>
      </el-collapse-item>
      <el-collapse-item title="Module List" name="3">
        <div class="moduleList">
          <div class="installPackage">
            name:<span style="color: red">*</span>
            <el-input v-model="packageName"></el-input>

            version:
            <el-input v-model="packageVersion" style="width: 120px"></el-input>
            <el-button type="primary" @click="installPackage" link>
              install
            </el-button>
          </div>
          <div class="installedList">
            <el-table
              :data="dependencies"
              style="width: 100%"
              :cell-style="cellStyle"
            >
              <el-table-column prop="package" label="Package"></el-table-column>
              <el-table-column
                fixed="right"
                prop="version"
                label="Version"
                width="63"
              ></el-table-column>
              <el-table-column fixed="right" label="Operation" width="80">
                <template #default="scope">
                  <el-button
                    link
                    size="small"
                    type="warning"
                    @click="deletePackage(scope.$index, scope.row)"
                  >
                    Remove
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </div>
      </el-collapse-item>
    </el-collapse>
  </aside>
</template>

<script setup>
import useDragAndDrop from "./useDnD";
import { onMounted, ref, defineEmits } from "vue";
import { getMyModels, installSinglePackage } from "@/api/request";
import DataTable from "@/views/builder/construction/Toolbars/DataTable.vue";
import { ElMessage } from "element-plus";

const { onDragStart } = useDragAndDrop();

const emits = defineEmits(["nodeEdited"]);

const activeName = ref("1");
const allModelsWithUser = ref([]);
const props = defineProps({
  scenarioId: {
    type: String,
  },
  containerId: {
    type: String,
  },
  dependencies: {
    type: Array,
  },
});

// 调整一下表格格式，以适应小屏
const cellStyle = () => {
  return {
    "font-size": "10px",
    padding: "0 10px",
  };
};

const packageName = ref("");
const packageVersion = ref("");

// 安装包
const installPackage = async () => {
  // 防止安装空包
  if (packageName.value == "") {
    ElMessage.error(
      "Please enter at least the installation name of the package"
    );
    return;
  }

  let newPackage = {
    package: packageName.value,
    version: packageVersion.value,
  };
  // 发送安装请求
  // 发送安装请求
  // 发送安装请求
  let formData = new FormData();
  let packageInfo = packageVersion.value
    ? packageName.value + "==" + packageVersion.value
    : packageName.value;

  // formData.append("packageInfo", packageInfo);
  // formData.append("containerId", props.containerId);
  // let result = await installSinglePackage(formData);
  // console.log(result, 1519);
  // emits("nodeEdited", result);

  const socket = new WebSocket("ws://localhost:8082/installSinglePackage");
  socket.onopen = () => {
    // 将 containerId 和 packageInfo 发送到后端
    const data = JSON.stringify({
      containerId: props.containerId,
      packageInfo: packageInfo,
    });
    socket.send(data);
  };
  socket.onmessage = (event) => {
    // 接收服务器推送的实时日志
    emits("nodeEdited", "upperInfo", event.data);
  };
  socket.onclose = () => {
    emits("nodeEdited", "upperInfo", "---------------");
  };

  // 安装成功
  ElMessage.success(
    "Installation command executed. Keep an eye on the console output"
  );
  props.dependencies.push(newPackage);
  emits("nodeEdited", "saveDependencies");

  // 初始化数据
  packageName.value = "";
  packageVersion.value = "";
};

// 卸载包
const deletePackage = (index, row) => {
  props.dependencies.splice(index, 1);
  // 发送删除请求
  // 发送删除请求
  // 发送删除请求
  // 真的需要吗？重复安装又不犯天条

  // 删除成功
  ElMessage.success("Deletion successful");
  emits("nodeEdited", "saveDependencies");
};

onMounted(async () => {
  allModelsWithUser.value = await getMyModels();
});
</script>

<style lang="scss">
.el-collapse-item__content {
  padding-bottom: 0px;
}
.el-collapse-item__header {
  font-size: 20px;
  font-weight: bold;
  color: #3066d6;
  line-height: 30px;
  padding: 20px;
  background-color: #eaeffa;
  border-top: #3066d6 3px solid;
}

.modelList {
  width: 100%;
  height: calc((100vh - 260px) * 0.9 - 96px);
  display: flex;
  justify-content: flex-start;
  flex-direction: column;
  align-items: center;
  overflow-y: auto;
  background: linear-gradient(
      45deg,
      transparent 33.33%,
      rgba(57, 144, 179, 0.1) 33.33%,
      rgba(0, 0, 0, 0.1) 66.66%,
      transparent 66.66%
    ),
    #e6f0ff;
  background-size: 30px 30px;
  .modelNode {
    width: 70%;
    aspect-ratio: 2;
    background-color: white;
    margin: 10px;
    border-radius: 5px;
    overflow: hidden;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
    display: flex;
    justify-content: center;
    align-items: center;
    cursor: grab;
    font-weight: 600;
    font-size: 14px;
    word-wrap: break-word; /* 允许长单词换行 */
    word-break: break-word; /* 强制在需要的地方换行 */
    text-align: center; /* 水平居中文本 */
  }
}

.moduleList {
  width: 100%;
  background: linear-gradient(
      45deg,
      transparent 33.33%,
      rgba(57, 144, 179, 0.1) 33.33%,
      rgba(0, 0, 0, 0.1) 66.66%,
      transparent 66.66%
    ),
    #e6f0ff;
  background-size: 30px 30px;
  .installPackage {
    width: 90%;
    margin-left: 5%;
    padding-top: 5%;
    padding-bottom: 4px;
    display: flex;
    justify-content: center;
    align-items: center;
    opacity: 0.7;
  }
  .installedList {
    width: 95%;
    padding-bottom: 5%;
    margin-left: 2.5%;
    opacity: 0.85;
    font-size: 6px;
    display: flex;
    justify-content: center;
  }
}

.base {
  height: 100%;
  width: 100%;
}
</style>
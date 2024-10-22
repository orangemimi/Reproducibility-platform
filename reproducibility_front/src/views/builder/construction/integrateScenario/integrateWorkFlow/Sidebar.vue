<template>
  <aside style="width: 25%; height: 100%">
    <el-collapse v-model="activeName" accordion>
      <el-collapse-item title="Model List" name="1">
        <div class="modelList">
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
    </el-collapse>
  </aside>
</template>

<script setup>
import useDragAndDrop from "./useDnD";
import { onMounted, ref } from "vue";
import { getMyModels } from "@/api/request";
import DataTable from "@/views/builder/construction/Toolbars/DataTable.vue";

const { onDragStart } = useDragAndDrop();

const activeName = ref("1");
const allModelsWithUser = ref([]);
const props = defineProps({
  scenarioId: {
    type: String,
  },
});

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
  height: 100%;
  display: flex;
  justify-content: center;
  flex-direction: column;
  align-items: center;
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
    aspect-ratio: 5;
    background-color: white;
    margin: 10px;
    border-radius: 5px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
    display: flex;
    justify-content: center;
    align-items: center;
    cursor: grab;
    font-weight: 600;
    font-size: 14px;
  }
}
.base {
  height: 100%;
  width: 100%;
}
</style>
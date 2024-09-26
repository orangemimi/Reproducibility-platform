<template>
  <!-- <div
    v-for="(item, i) in envsName"
    :key="i"
    @click="selectEnvy(item)"
    class="mainCardConstruction"
  >
    <div class="cardBodyBinded">
      <el-card shadow="hover" class="card_main">
        <div class="card_info">
          <h4 class="card_info_name">{{ item.name }}</h4>
        </div>
      </el-card>
    </div>
  </div> -->
  <div @click="selectEnvy()" class="mainCardConstruction">
    <div class="cardBodyBinded">
      <el-card shadow="hover" class="card_main">
        <div class="card_info">
          <h4 class="card_info_name">{{ props.scenario.env }}</h4>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { defineEmits, onMounted } from "vue";

const props = defineProps(["scenario"]);

// const envsName2 = ref(["python3.8", "matlab", "R语言"]);
// const envsName = ref([
//   {
//     type: "docker",
//     name: "python3.8",
//   },

//   {
//     type: "docker",
//     name: "R语言",
//   },
//   {
//     type: "docker",
//     name: "matlab",
//   },
// ]);

const emit = defineEmits(["codeType"]);

const selectEnvy = () => {
  let data = {
    type: "docker",
    name: props.scenario.env,
  };

  emit("codeType", data);
};

onMounted(() => {
  selectEnvy();
});
</script>


<style scoped lang="scss">
.mainCardConstruction {
  margin: 10px 10px;
  width: 300px;
  /*// pointer-events: none;*/ /*//鼠标点击不可修改
*/
  // padding: 5px;/*// background-color: #3067d61c;*/.cardBodyBinded {
  :deep(.el-card__body) {
    padding: 5px;
    height: 30px;
    background-color: rgb(255, 255, 255);
  }
  :deep(.el-card:hover) {
    background-color: #67c23a;
    border: 0.5px solid #5d090934;
  }

  .card_main {
    background-color: rgb(64, 158, 255);
    cursor: pointer;
    // height: 42px;
    width: 100%;
    padding: 0 5px;

    .card_info {
      width: 100%;
      .card_info_name {
        display: block;
        width: 300;
        overflow: hidden;
        white-space: wrap;
        text-overflow: ellipsis;
      }
    }
  }
}
</style>
<template>
  <div>
    <el-card class="card"> <programol></programol></el-card>
  </div>
</template>

<script>
import programol from "../../components/dataprocess/programol.vue";
export default {
  components: {
    programol: programol,
  },
};
</script>

<style lang="less" scoped>
.card {
  width: 900px;
  margin-left: 500px;
}
</style>
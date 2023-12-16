<template>
  <div>
    <el-card class="card"><codingOL></codingOL></el-card>
  </div>
</template>

<script>
import codingOL from '../../../components/codingOL/codingOL.vue'
export default {
  components: {
    codingOL,
  },
  data() {
    return {}
  },
  mounted() {},
}
</script>

<style lang="less" scoped>
.card {
  width: 900px;
  margin-left: 300px;
}
</style>

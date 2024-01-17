<!--  -->
<template>
  <div>
    <el-tabs v-model="activeResource">
      <el-tab-pane label="Data Services" name="data">
        <el-table
          :data="dataTable"
          style="width: 100%; font-size: 18px"
          max-height="400"
        >
          <el-table-column
            prop="name"
            label="Name"
            width="300"
          ></el-table-column>
          <el-table-column prop="suffix" label="suffix"></el-table-column>
          <el-table-column fixed="right" label="operation" width="200">
            <template v-slot="scope">
              <el-button @click.prevent="download(scope.row)" type="text"
                ><el-icon><Download /></el-icon>Download</el-button
              >
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="Model Services" name="model">
        <el-table
          :data="modelTable"
          style="width: 100%; font-size: 18px"
          max-height="400"
        >
          <el-table-column
            prop="name"
            label="Name"
            width="300"
          ></el-table-column>
          <el-table-column
            prop="createTime"
            label="Invoke Time"
            width="350"
          ></el-table-column>
          <el-table-column
            prop="description"
            label="Description"
          ></el-table-column>
          <el-table-column fixed="right" label="operation" width="200">
            <template v-slot="scope">
              <el-button @click.prevent="view(scope.row, 'model')" type="text"
                >view</el-button
              >
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
export default {
  props: {
    resourceCollectionObjects: {
      type: Object,
    },
  },
  watch: {
    resourceCollectionObjects: {
      handler(newVal) {
        if (Object.hasOwnProperty.call(newVal, "modelList")) {
          this.dataTable = newVal.dataList;
          this.modelTable = newVal.modelList;
        }
      },
      deep: true,
      immediate: true,
    },
  },

  data() {
    return { activeResource: "data", dataTable: [], modelTable: [] };
  },

  methods: {
    download(row) {
      let urls = row.value;
      // 创建一个链接元素
      const link = document.createElement("a");
      link.href = urls;
      link.target = "_blank"; // 在新窗口中打开链接
      link.download = "downloaded_file"; // 设置下载的文件名
      // 模拟点击链接，触发下载
      link.click();
    },
  },

  mounted() {},
};
</script>
<style lang="scss" scoped></style>

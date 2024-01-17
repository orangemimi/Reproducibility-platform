<!-- <template>
  <div class="container">
    <el-row style="width: 60vw;">
      <el-col :span="4">
        <el-card class="left">在线表格列表</el-card>
      </el-col>
      <el-col :span="20">
        <el-card class="right">
          <el-card class="tools">工具栏</el-card>
          <el-table :data="table" style="width: 100%">
          <el-table-column
            v-for="(column, columnIndex) in table[0]"
            :key="columnIndex"
            :prop="columnIndex"
            :label="columnIndex"
          >
            <template v-slot="scope">
              <el-input v-model="scope.row[columnIndex]" />
            </template>
          </el-table-column>
        </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { onMounted, ref } from "vue";
// import { io } from 'socket.io-client';

const table = ref([
  { name: "小西", age: 21, city: "北京", gender: "男" },
  { name: "小瓜", age: 18, city: "上海", gender: "女" },
  { name: "小海", age: 20, city: "南京", gender: "女" },
]);

const updateTable = () => {
  // 发送表格更新事件到服务器
  socket.emit("updateTable", table.value);
};

// const socket = io("http://127.0.0.1:8082/formOL"); // 替换为实际的Socket.IO服务器地址

// // 监听服务器发来的表格更新事件
// socket.on('updateTable', (data) => {
//   table.value = data;
// });

onMounted(() => {
  // 在组件挂载时执行的逻辑
});
</script>

<style>
.container {
  width: 60vw;
  height: 60vh;
  margin-left: 20%;
  margin-top: 10%;s
}
.left {
  width: 100%;
  height: 100%;
}
.right {
  width: 100%;
  height: 100%;
}
.tools {
  height: 4vh;
}
</style> -->
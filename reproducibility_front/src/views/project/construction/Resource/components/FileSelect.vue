<template>
  <div class="main">
    <div class="header">
      <el-icon class="icon-operation"><el-icon-back /></el-icon>
      <el-icon class="icon-operation"><el-icon-right /></el-icon>
      <el-icon class="icon-operation"><el-icon-top /></el-icon>
      <el-input
        size="mini"
        placeholder="请输入内容"
        :suffix-icon="ElIconRefreshRight"
        prefix-icon="el-icon-s-platform"
        style="width: 30%; margin-right: 10px"
      ></el-input>
      <el-input
        size="mini"
        placeholder="请输入内容"
        :suffix-icon="ElIconDate"
        style="width: 20%"
      ></el-input>
    </div>
    <el-row>
      <el-col :span="24">
        <!-- <el-table
              :data="tableData"
              style="width: 100%"
              max-height="532"
              height="532"
              @row-contextmenu="test"
              :default-sort="{ prop: 'name', order: 'descending' }"
              class="customer-table"
              @cell-click="handleCurrentChange"
              :row-class-name="tableRowClassName"
              @cell-mouse-enter="mouseEnter"
              @cell-mouse-leave="mouseLeave"
              @header-click="headerClick"
          
            > -->
        <el-table
          :data="tableData"
          style="width: 100%"
          max-height="532"
          height="532"
          :default-sort="{ prop: 'name', order: 'descending' }"
          class="customer-table"
          @row-dblclick="rowDblclick"
        >
          <el-table-column prop="name" sortable label="Name" width="300">
            <template v-slot="scope">
              <svg
                class="icon"
                aria-hidden="true"
                style="width: 20px; height: 20px"
                v-if="scope.row.folder"
              >
                <use xlink:href="#icon-wenjianjia"></use>
              </svg>
              <el-icon><el-icon-document /></el-icon>
              <span style="margin-left: 10px">{{ scope.row.name }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="date" sortable width="150">
            <template v-slot:header>
              <el-divider direction="vertical" class="divider"></el-divider>
              Date
            </template>
            <template v-slot="scope">
              <span style="margin-left: 10px">{{ scope.row.date }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="province" sortable width="150">
            <template v-slot:header>
              <el-divider direction="vertical" class="divider"></el-divider>
              Type
            </template>
            <template v-slot="scope">
              <span style="margin-left: 10px" v-if="scope.row.folder"
                >folder</span
              >
              <span style="margin-left: 10px" v-else>file</span>
            </template>
          </el-table-column>
          <el-table-column prop="city" sortable width="120">
            <template v-slot:header>
              <el-divider direction="vertical" class="divider"></el-divider>
              Size
            </template>
            <template v-slot="scope">
              <span style="margin-left: 10px">{{ scope.row.fileSize }}</span>
            </template>
          </el-table-column>
        </el-table>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import {
  Back as ElIconBack,
  Right as ElIconRight,
  Top as ElIconTop,
  Document as ElIconDocument,
  RefreshRight as ElIconRefreshRight,
  Date as ElIconDate,
} from '@element-plus/icons-vue'
import { $on, $off, $once, $emit } from '../../../../../utils/gogocodeTransfer'
import { getFileItemByStoreyAndParent } from '@/api/request'
import { dateFormat } from '@/utils/utils'
export default {
  data() {
    return {
      tableData: [],
      stack: ['-1'],
      arrayStack: [],
      ElIconRefreshRight,
      ElIconDate,
    }
  },
  components: {
    ElIconBack,
    ElIconRight,
    ElIconTop,
    ElIconDocument,
  },
  methods: {
    async getFileItemByStoreyAndParent() {
      let data = await getFileItemByStoreyAndParent('0', '-1')
      data.forEach((item) => {
        item.date = dateFormat(item.date, 'yyyy/MM/dd hh:mm')
      })
      this.tableData = data
    },
    async init() {
      await this.getFileItemByStoreyAndParent()
    },
    iconClick(type) {
      if (type == 'left') {
        if (this.stack.length > 1) {
          let temp = this.arrayStack[this.arrayStack.length - 1]
          this.stack.pop()
          this.arrayStack.pop()
          this.tableData = temp
        }
      }
      if (type == 'right') {
        console.log(this.stack)
        console.log(this.arrayStack)
      }
    },
    async rowDblclick(row) {
      if (row.folder) {
        let data = await getFileItemByStoreyAndParent(
          this.stack.length.toString(),
          row.id
        )
        data.forEach((item) => {
          item.date = dateFormat(item.date, 'yyyy/MM/dd hh:mm')
        })
        this.stack.push({
          id: row.id,
          name: row.name,
        })
        this.arrayStack.push(this.tableData)
        this.tableData = data
      } else {
        let data = {
          row: row,
          stack: this.stack,
        }
        $emit(this, 'selectFile', data)
      }
    },
  },
  mounted() {
    this.init()
  },
  emits: ['selectFile'],
}
</script>

<style lang="scss" scoped>
.icon-operation {
  margin-right: 10px;
}
.main :deep(.el-table__row > td) {
  border: none;
}
</style>

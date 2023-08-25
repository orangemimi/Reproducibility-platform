<template>
  <!-- 创建模块 -->
  <div>
    <el-row style="padding-top: 20px;">
      <el-col span="12">
        <el-input
          placeholder="请输入搜索关键字"
          v-model="keyword"
          class="input-with-select"
          size="small"
        >
          <el-button
            slot="append"
            icon="el-icon-search"
            @click="filterChange"
          ></el-button>
        </el-input>
      </el-col>
    </el-row>
    <el-row>
      <el-transfer
        target-order="unshift"
        v-model="formDatas.river"
        @change="transferChange"
        @left-check-change="leftCheckChange"
        :titles="['Public models', 'Selected models']"
        :data="currentPageDatas"
        :format="{ noChecked: '${total}', hasChecked: '${checked}/${total}' }"
        class="el-transfer_cus"
        filterable
      >
        <el-pagination
          small
          slot="left-footer"
          @current-change="handleCurrentChange"
          :current-page="page.pageNo"
          :page-size="page.pageSize"
          :total="page.total"
          :pager-count="5"
          layout="prev, pager, next"
        ></el-pagination>
      </el-transfer>
    </el-row>
  </div>
</template>

<script>
export default {
  data() {
    return {
      isShow: this.attrShow,
      model_name: "",
      currentPageDatas: [], // 当前页数据
      currentDatas: [], // 当前数据
      sourceDatas: [], // 源数据,用于临时筛选数据
      currentChecks: [], // 左侧框当前已勾选的键数组
      page: { pageNo: 1, pageSize: 30, total: 0 },
      formDatas: {
        river: [], // 已勾选数据
      },
      numTotal: 30, // 最多只能选渠30个
      keyword: "", // 搜索关键字
    };
  },
  created() {
    this.init();
  },
  props: {
    attrShow: {
      type: Boolean,
      default: false,
    },
  },
  methods: {
    // 关闭对话框
    cancle() {
      this.$emit("updateStatusData", false);
    },

    // 提交数据
    submitData() {},

    // 提交成功时清空数据
    clearData() {
      this.formDatas.river = [];
      this.model_name = "";
      this.keyword = "";
      this.page.pageNo = 1;
    },

    // 初始化数据
    init() {
      let listData = [
        { id: 1, name: "你好1" },
        { id: 2, name: "你好2" },
        { id: 3, name: "你好3" },
      ];

      // 处理字段别名
      var currentDatas = listData.map((value) => {
        return {
          label: value.name,
          key: value.id,
          obj: value,
        };
      });
      // 总数
      let arrLength = currentDatas.length;
      this.page.total = arrLength;
      // 初始化数据
      this.currentDatas = currentDatas;
      this.sourceDatas = currentDatas;
      // 初始化20条数据给当前第一页的变量
      this.currentPageDatas = this.currentDatas.slice(0, this.page.pageSize);
    },

    // 分页change
    handleCurrentChange(page_cur) {
      this.page.pageNo = page_cur;

      // 先将选中的从当前数据过滤掉
      this.currentDatas = this.currentDatas.filter(
        function(value) {
          return !this.formDatas.river.includes(value.key);
        }.bind(this)
      );

      // 再将过滤好的当前数据选出指定页
      this.currentPageDatas = this.groupFunc(page_cur);

      // 再将选中的目标数组补给当前页变量，从而保证之前选的数据能在右边显示
      this.currentPageDatas = this.currentPageDatas.concat(
        this.sourceDatas.filter(
          function(val) {
            return this.formDatas.river.includes(val.key);
          }.bind(this)
        )
      );
    },

    // 监听勾选的数组
    leftCheckChange(checks) {
      this.currentChecks = checks;
    },

    // 穿梭change
    transferChange(current, direction) {
      // 为了保证数据的一致性，目标数组还回来之后要插进当前数据变量
      if (direction == "left") {
        this.currentDatas = this.sourceDatas.filter(
          function(val) {
            return !current.includes(val.key);
          }.bind(this)
        );
        let arrLength = this.currentDatas.length;
        this.page.total = arrLength;
      } else {
        if (this.formDatas.river.length > this.numTotal) {
          this.$message({
            showClose: true,
            message: "抱歉，自定义模块游戏不能超过30个",
            type: "warning",
          });

          this.formDatas.river = this.formDatas.river.filter(
            function(val) {
              return !this.currentChecks.includes(val);
            }.bind(this)
          );

          return;
        }
      }
      this.handleCurrentChange(this.page.pageNo);
    },

    // 穿梭搜索
    filterChange() {
      // 自定义搜索，从当前数组变量中过滤，再渲染回组件
      var currentDatas = this.sourceDatas.filter(
        function(val) {
          return (
            val.obj.name.indexOf(this.keyword) > -1 &&
            !this.formDatas.river.includes(val.key)
          );
        }.bind(this)
      );

      if (currentDatas.length != this.currentDatas.length) {
        this.currentDatas = currentDatas;
        this.handleCurrentChange(1);
      }
    },

    // 数组分页函数,返回当前页数据
    groupFunc(page_cur) {
      var currentDatas = [];
      let arrLength = this.currentDatas.length;
      if (arrLength <= 0) {
        return [];
      }
      this.page.total = arrLength;
      let num = this.page.pageSize;
      let pageTotal = 1;

      // 最后一页全部勾选时
      let total = arrLength / num;
      if (arrLength % num === 0) {
        pageTotal = total;
      } else {
        pageTotal = total + 1;
      }
      if (page_cur > pageTotal) {
        page_cur = pageTotal;
      }

      let index = 0;
      for (let i = 0; i < arrLength; i++) {
        if (i % num === 0 && i !== 0) {
          currentDatas.push(this.currentDatas.slice(index, i));
          index = i;
        }
        if (i + 1 === arrLength) {
          currentDatas.push(this.currentDatas.slice(index, i + 1));
        }
      }
      return currentDatas[page_cur - 1];
    },
  },
  watch: {
    // 监听对话框状态变化
    attrShow: function() {
      this.isShow = this.attrShow;
    },
    keyword: function() {
      this.filterChange();
    },
  },
};
</script>

<style>
.model_name_tip {
  font-size: 14px;
  font-weight: 700;
}
.input-with-select {
  background-color: #fff;
  width: 284px;
}

.input-with-select .el-input-group__append {
  padding: 0px 10px;
}

.input-with-select .el-input__inner {
  border: 1px solid #dcdfe6 !important;
}
.el-transfer_cus {
  width: 720px;
}
.el-transfer_cus .el-transfer__buttons {
  width: 120px !important;
}

.el-transfer_cus .el-transfer__buttons :nth-child(2) {
  margin-left: 0px !important;
}

.el-transfer_cus .el-transfer-panel {
  width: 284px;
  height: 450px;
}

.el-transfer_cus .el-transfer-panel__list {
  height: 450px;
}
</style>

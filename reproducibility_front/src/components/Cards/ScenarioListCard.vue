<!-- card -->
<template>
  <div class="main">
    <div :class="item.isBinded == true ? 'cardBodyBinded' : 'cardBody'">
      <el-card shadow="hover" class="card_main">
        <div class="card_info">
          <h4 :title="item.name" class="card_info_name">
            {{ item.name }}
          </h4>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    secnarioForm: {
      type: Object,
    },
  },

  watch: {
    secnarioForm: {
      handler(val) {
        this.item = val;
      },
    },
  },

  data() {
    return {
      item: this.secnarioForm,
    };
  },

  methods: {},
};
</script>
<style lang="scss" scoped>
.main {
  width: 100%;
  // pointer-events: none; //鼠标点击不可修改
  // padding: 5px;
  // background-color: #3067d61c;
  .cardBodyBinded {
    /deep/ .el-card__body {
      padding: 5px;
      height: 30px;
      background-color: rgb(255, 255, 255);
    }
    /deep/ .el-card:hover {
      background-color: #e631317d;
      border: 0.5px solid #5d090934;
    }

    .card_main {
      background-color: rgb(179, 43, 43);
      cursor: pointer;
      // height: 42px;
      width: 100%;
      padding: 0 5px;

      .card_info {
        width: 100%;
        .card_info_name {
          display: block;
          width: 120px;
          overflow: hidden;
          white-space: nowrap;
          text-overflow: ellipsis;
        }
      }
    }
  }

  .cardBody {
    /deep/ .el-card:hover {
      background-color: #3067d61c;
      border: 0.5px solid #25252534;
    }

    /deep/ .el-card__body {
      padding: 5px;
      height: 30px;
    }

    .card_main {
      background-color: rgb(255, 255, 255);
      cursor: pointer;
      // height: 42px;
      width: 100%;
      padding: 0 5px;

      .card_info {
        width: 100%;
        .card_info_name {
          display: block;
          width: 120px;
          overflow: hidden;
          white-space: nowrap;
          text-overflow: ellipsis;
        }
      }
    }
  }
}
</style>

<!-- card -->
<template>
  <div class="main">
    <el-card class="cardBody" shadow="hover">
      <div class="card_info">
        <h4 :title="item.name" class="card_info_name">
          {{ item.name }}
        </h4>
      </div>
    </el-card>
  </div>
</template>

<script>
// import { get } from "@/axios";
export default {
  props: {
    modelFrom: {
      type: Object,
    },
  },

  watch: {
    modelFrom: {
      async handler(val) {
        // debugger;
        this.item = val;
        // console.log(val);
        // if (val.tag == "data") {
        //   let res = await get(`/data_service/${val.id}`, null, {
        //     dataItem: val.id,
        //   });
        //   this.dataItem = res;
        // } else {
        //   this.$router.push({
        //     path: `/resource/${val.id}/model`,
        //   });
        // }
      },
    },
  },

  data() {
    return {
      item: this.modelFrom,
      isOpen: this.isOpenTool,
      openTool: false,
      dataItem: {},
      // toolPreview
    };
  },

  methods: {},
};
</script>
<style lang="scss" scoped>
.main {
  /deep/ .el-card:hover {
    background-color: #3067d61c;
    border: 0.5px solid #25252534;
  }

  /deep/ .el-card__body {
    padding: 5px;
    height: 40px;
  }

  .cardBody {
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
</style>

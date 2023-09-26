<!-- card -->
<template>
  <div class="main">
    <el-card class="cardBody" shadow="hover">
      <div class="card_info">
        <h4 :title="item.name" class="card_info_name">
          {{ item.name }}
        </h4>
        <p class="card_info_description">
          {{ item.description }}
        </p>
      </div>
    </el-card>
  </div>
</template>

<script>
export default {
  props: {
    stateItem: {
      type: Object,
    },
  },

  watch: {
    stateItem: {
      handler(val) {
        this.item = val;
      },
    },
  },

  data() {
    return {
      item: this.stateItem,
      // toolPreview
    };
  },

  methods: {},
};
</script>
<style lang="scss" scoped>
.main {
  width: 100%;
  padding: 0;

  // /deep/ .el-card:hover {
  //   background-color: #3067d61c;
  //   border: 0.5px solid #25252534;
  // }

  /deep/ .el-card__body {
    padding: 5px;
    // height: 40px;
    background: rgb(221, 238, 249);
  }
  .cardBody {
    height: 60px;
    margin: 10px 0;
    border: 0.5px solid #4f769be6;
    background-color: rgb(221, 238, 249);
    cursor: pointer;
    width: 100%;

    .card_info {
      width: 100%;
      .card_info_description {
        display: block;
        width: 500px;
        overflow: hidden;
        white-space: nowrap;
        text-overflow: ellipsis;
      }
    }
  }
}
</style>

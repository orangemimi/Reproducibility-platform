<!--  -->
<template>
  <template>
    <div class="star-box">
      <ul
        class="star-list"
        @touchstart="touchStart"
        @touchmove="touchMove"
        ref="star"
      >
        <li
          class="star-item"
          v-for="(i, index) in scoreList"
          :key="index"
          :class="{ half: i.state == 1, full: i.state == 2 }"
        ></li>
      </ul>
    </div>
  </template>
</template>

<script>
export default {
  props: {
    value: {
      type: Number,
      default: 0,
    },
  },
  data() {
    return {
      x0: 0, // 包裹星星的ul的X坐标
      starLen: 0, // 每个星星的宽度
      reallyScore: 0, // 5分数，一个星1分
      firstX: 0,
      score: 0, // 10分数，一个星2分
    };
  },
  computed: {
    scoreList() {
      // 用一个数组存储每个星星的状态，如果是满星就是2，半星就是1，没有就是0
      let i = 1;
      let state;
      const stateArr = [];
      do {
        if (this.score >= 2 * i) {
          state = 2;
        } else if (this.score === 2 * i - 1) {
          state = 1;
        } else {
          state = 0;
        }
        stateArr.push({
          state,
        });
        i++;
      } while (i <= 5);
      return stateArr;
    },
  },
  watch: {
    score(newVal) {
      this.$emit("input", newVal / 2);
      this.reallyScore = newVal / 2;
    },
  },
  mounted() {
    this.x0 = this.getParentLeft(this.$refs.star);
    this.starLen = this.$refs.star.offsetWidth / 5;
  },
  methods: {
    touchStart(evt) {
      if (evt && evt.touches.length === 1) {
        this.len = this.$refs.star.offsetWidth;
        let firstX = evt.touches[0].clientX - this.x0;
        this.score = this.scoreValue((firstX / this.starLen) * 2);
      }
    },
    touchMove(evt) {
      if (evt) {
        let distance = evt.touches[0].clientX - this.x0;
        this.score = this.scoreValue((distance / this.starLen) * 2);
      }
    },
    scoreValue(val) {
      if (val > 10) {
        return 10;
      } else if (val < 0) {
        return 0;
      } else {
        return val > parseInt(val) ? parseInt(val) + 1 : parseInt(val);
      }
    },
    getParentLeft(e) {
      var offset = e.offsetLeft;
      if (e.offsetParent != null) {
        offset += this.getParentLeft(e.offsetParent);
      }
      return offset;
    },
  },
};
</script>
<style lang="scss" scoped>
.star-box {
  display: flex;
  align-items: center;
  .star-item {
    display: inline-block;
    width: 24px;
    height: 24px;
    background: url("../../../../static/icon_sc02.png") no-repeat center;
    background-size: 24px 24px;
    &.half {
      background: url("../../../../static/icon_sc01.png") no-repeat center;
      background-size: 24px 24px;
    }
    &.full {
      background: url("../../../../static/icon_sc03.png") no-repeat center;
      background-size: 24px 24px;
    }
  }
  .star-num {
    font-size: 15px;
    margin-left: 10px;
  }
}
</style>

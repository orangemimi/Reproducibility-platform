<template>
  <div class="vue-block" :class="{ selected: selected }" :style="style">
    <header :style="headerStyle">
      {{ name }}
    </header>
    <div class="inputs">
      <div class="input" v-for="(slot, index) in inputs" :key="index">
        <div class="circle inputSlot" :class="{ active: slot.active }"></div>
        <span
          style="
            white-space: wrap;
            overflow: hidden;
            text-overflow: ellipsis;
            display: block;
          "
        >
          {{ slot.name }}</span
        >
      </div>
    </div>
    <div class="outputs">
      <div class="output" v-for="(slot, index) in outputs" :key="index">
        <div class="circle" :class="{ active: slot.active }"></div>
        <span
          style="
            white-space: wrap;
            overflow: hidden;
            text-overflow: ellipsis;
            display: block;
          "
        >
          {{ slot.name }}</span
        >
      </div>
    </div>
  </div>
</template>

<script>
import { $on, $off, $once, $emit } from "../../../../utils/gogocodeTransfer";
export default {
  name: "VueBlock",
  props: {
    x: {
      type: Number,
      default: 0,
      validator: function (val) {
        return typeof val === "number";
      },
    },
    y: {
      type: Number,
      default: 0,
      validator: function (val) {
        return typeof val === "number";
      },
    },
    selected: Boolean,
    name: {
      type: String,
      default: "Name",
    },
    inputs: Array,
    outputs: Array,

    options: {
      type: Object,
    },
  },
  data() {
    return {
      width: this.options.width,
      hasDragged: false,
    };
  },
  computed: {
    style() {
      return {
        top: this.options.center.y + this.y * this.options.scale + "px",
        left: this.options.center.x + this.x * this.options.scale + "px",
        width: this.width + "px",
        transform: "scale(" + (this.options.scale + "") + ")",
        transformOrigin: "top left",
      };
    },
    headerStyle() {
      return {
        height: this.options.titleHeight + "px",
      };
    },
  },
  methods: {
    handleMove(e) {
      this.mouseX = e.pageX || e.clientX + document.documentElement.scrollLeft;
      this.mouseY = e.pageY || e.clientY + document.documentElement.scrollTop;

      if (this.dragging && !this.linking) {
        let diffX = this.mouseX - this.lastMouseX;
        let diffY = this.mouseY - this.lastMouseY;

        this.lastMouseX = this.mouseX;
        this.lastMouseY = this.mouseY;

        this.moveWithDiff(diffX, diffY);

        this.hasDragged = true;
      }
    },
    handleDown(e) {
      this.mouseX = e.pageX || e.clientX + document.documentElement.scrollLeft;
      this.mouseY = e.pageY || e.clientY + document.documentElement.scrollTop;

      this.lastMouseX = this.mouseX;
      this.lastMouseY = this.mouseY;

      const target = e.target || e.srcElement;
      if (this.$el.contains(target) && e.which === 1) {
        this.dragging = true;

        $emit(this, "select");

        if (e.preventDefault) e.preventDefault();
      }
    },
    handleUp() {
      if (this.dragging) {
        this.dragging = false;

        if (this.hasDragged) {
          this.save();
          this.hasDragged = false;
        }
      }

      if (this.linking) {
        this.linking = false;
      }
    },
    save() {
      $emit(this, "update");
    },
    moveWithDiff(diffX, diffY) {
      let left = this.x + diffX / this.options.scale;
      let top = this.y + diffY / this.options.scale;

      $emit(this, "update:x", left);
      $emit(this, "update:y", top);
    },
  },
  created() {
    this.mouseX = 0;
    this.mouseY = 0;

    this.lastMouseX = 0;
    this.lastMouseY = 0;

    this.linking = false;
    this.dragging = false;
  },
  mounted() {
    document.documentElement.addEventListener(
      "mousemove",
      this.handleMove,
      true
    );
    document.documentElement.addEventListener(
      "mousedown",
      this.handleDown,
      true
    );
    document.documentElement.addEventListener("mouseup", this.handleUp, true);
    // this.$nextTick(() => {
    //   shave(".inputs", 50);
    //   shave(".outputs", 50);
    //   shave("header", 20);
    // });
  },
  beforeUnmount() {
    document.documentElement.removeEventListener(
      "mousemove",
      this.handleMove,
      true
    );
    document.documentElement.removeEventListener(
      "mousedown",
      this.handleDown,
      true
    );
    document.documentElement.removeEventListener(
      "mouseup",
      this.handleUp,
      true
    );
  },
  emits: ["update:x", "update:y", "select", "update"],
};
</script>

<style lang="scss" scoped>
$blockBorder: 1px;

$ioPaddingInner: 2px 0;

$ioHeight: 16px;

$ioFontSize: 14px;

$circleBorder: 1px;

$circleSize: 10px;

$circleMargin: 2px; // left/right

$circleNewColor: #00ff00;

$circleRemoveColor: #ff0000;

$circleConnectedColor: #ffff00;

.vue-block {
  position: absolute;
  box-sizing: border-box;
  border: $blockBorder solid black;
  background: white;
  z-index: 1;
  opacity: 0.9;
  cursor: move;
  &.selected {
    border: $blockBorder solid red;
    z-index: 2;
  }

  > header {
    background: #bfbfbf;
    text-align: center;

    > .delete {
      color: red;
      cursor: pointer;
      float: right;
      position: absolute;
      right: 5px;
    }
  }

  .inputs,
  .outputs {
    padding: $ioPaddingInner;

    display: block;
    width: 50%;

    > * {
      width: 100%;
    }
  }

  .circle {
    box-sizing: border-box;
    margin-top: $ioHeight / 2 - $circleSize / 2;

    width: $circleSize;
    height: $circleSize;

    border: $circleBorder solid rgba(0, 0, 0, 0.5);
    border-radius: 100%;

    cursor: crosshair;
    &.active {
      background: $circleConnectedColor;
    }
  }

  .inputs {
    float: left;
    text-align: left;

    margin-left: -($circleSize/2 + $blockBorder);
  }

  .input,
  .output {
    height: $ioHeight;
    overflow: hidden;
    font-size: $ioFontSize;
    &:last-child {
    }
  }

  .input {
    float: left;

    .circle {
      float: left;
      margin-right: $circleMargin;

      &:hover {
        background: $circleNewColor;

        &.active {
          background: $circleRemoveColor;
        }
      }
    }
  }

  .outputs {
    float: right;
    text-align: right;

    margin-right: -($circleSize/2 + $blockBorder);
  }

  .output {
    float: right;

    .circle {
      float: right;
      margin-left: $circleMargin;

      &:hover {
        background: $circleNewColor;
      }
    }
  }
}
</style>

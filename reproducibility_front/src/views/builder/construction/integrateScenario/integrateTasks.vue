<template>
  <div class="integrateContainer">
    <div class="modelList"></div>
    <div class="flowCanvas">222</div>
    <div class="executionInfo">333</div>
  </div>
</template>

<script setup lang="ts">
</script>

<style scoped lang="scss">
.baseStyle {
  width: 100%;
  height: 100%;
}

.integrateContainer {
  width: 100%;
  height: 100%;
}
.modelList {
  width: 20%;
  height: 100%;
  --s: 84px;
  --c1: #f2f2f2;
  --c2: #cdcbcc;
  --c3: #999999;

  --_g: 0 120deg, #0000 0;
  background: conic-gradient(
      from 0deg at calc(500% / 6) calc(100% / 3),
      var(--c3) var(--_g)
    ),
    conic-gradient(
      from -120deg at calc(100% / 6) calc(100% / 3),
      var(--c2) var(--_g)
    ),
    conic-gradient(
      from 120deg at calc(100% / 3) calc(500% / 6),
      var(--c1) var(--_g)
    ),
    conic-gradient(
      from 120deg at calc(200% / 3) calc(500% / 6),
      var(--c1) var(--_g)
    ),
    conic-gradient(
      from -180deg at calc(100% / 3) 50%,
      var(--c2) 60deg,
      var(--c1) var(--_g)
    ),
    conic-gradient(
      from 60deg at calc(200% / 3) 50%,
      var(--c1) 60deg,
      var(--c3) var(--_g)
    ),
    conic-gradient(
      from -60deg at 50% calc(100% / 3),
      var(--c1) 120deg,
      var(--c2) 0 240deg,
      var(--c3) 0
    );
  background-size: calc(var(--s) * 1.732) var(--s);
}
.flowCanvas {
  width: 60%;
  height: 100%;
}
.executionInfo {
  width: 20%;
  height: 100%;
}
</style>
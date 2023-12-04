<script lang="ts">
import { ref , onMounted } from 'vue';
// import HelloWorld from './components/HelloWorld.vue'
export default {
  setup() {
    const message = ref('Hello, Vue 3!');
    
    onMounted(() => {
      console.log(
        '%cOpenGMS',
        ' text-shadow: 0 1px 0 #ccc,0 2px 0 #c9c9c9,0 3px 0 #bbb,0 4px 0 #b9b9b9,0 5px 0 #aaa,0 6px 1px rgba(0,0,0,.1),0 0 5px rgba(0,0,0,.1),0 1px 3px rgba(0,0,0,.3),0 3px 5px rgba(0,0,0,.2),0 5px 10px rgba(0,0,0,.25),0 10px 10px rgba(0,0,0,.2),0 20px 20px rgba(0,0,0,.15);font-size:5em'
      )
    });

    return {
      message
    };
  }
};
</script>

<template>
  <div id="app">
    <router-view />
  </div>
</template>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}
html,
body {
  width: 100%;
  height: 100%;
  font-size: 100%;
}
.el-tabs__item {
  max-width: 200px;
  text-overflow: ellipsis;
  overflow: hidden;
}
.el-input-group__append {
  background: #ffffff;
}
.scrollbar::-webkit-scrollbar-track {
  -webkit-box-shadow: inset 0 0 6px rgba(0, 0, 0, 0.3);
  background-color: #f5f5f5;
  border-radius: 4px;
}
.scrollbar::-webkit-scrollbar {
  width: 6px;
  height: 5px !important;
  background-color: #f5f5f5;
}
.scrollbar::-webkit-scrollbar-thumb {
  background-color: #666666;
  border-radius: 4px;
}
.noselect {
  -webkit-touch-callout: none;
  -webkit-user-select: none;
  -khtml-user-select: none;
  -moz-user-select: none;
  -ms-user-select: none;
  user-select: none;
}
body {
  background-image: radial-gradient(
      farthest-corner at 120px 420px,
      #fff,
      rgba(255, 255, 255, 0.5)
    ),
    url('./assets/topo.svg');
  background-repeat: no-repeat;
  background-size: 100%, 1500px;
  width: 100%;
}
</style>

import { createApp } from 'vue';
import App from './App.vue';
import router from './router';
import ElementPlus from 'element-plus';
import 'element-plus/theme-chalk/index.css'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
// import locale from 'element-plus/lib/locale/lang/en';
// import locale from 'element-ui/lib/locale/lang/en' //修改成英文
import setupStore from './store';
import mavonEditor from 'mavon-editor';
import 'mavon-editor/dist/css/index.css';
import './assets/iconfont/iconfont.css';
// import './assets/variable.scss';
// import  VueScrollTo  from 'vuescroll';
import x2js from 'x2js';

const app = createApp(App);

app.use(router);
// app.use(ElementPlus, { locale });
app.use(ElementPlus);
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}
app.use(mavonEditor);
// app.use(VueScrollTo);
(async () => {
  // 异步初始化 Vuex store
  const store = await setupStore();
  app.use(store);

  // 使用 Vue.nextTick 来确保 Vue 完成刷新
  await new Promise(resolve => setTimeout(resolve, 0));

  // 设置全局属性
  app.config.globalProperties.$x2js = new x2js();
  app.config.globalProperties.routerAppend = (path: string, pathToAppend: string) => {
    return path + (path.endsWith('/') ? '' : '/') + pathToAppend;
  };

  // 挂载应用
  app.mount('#app');
})();

import {createApp} from 'vue';
import App from './App.vue';
import './style.css';
import "uno.css";
// @ts-ignore
import router from './router';
import ElementPlus from 'element-plus';
import 'element-plus/theme-chalk/index.css'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
// import locale from 'element-plus/lib/locale/lang/en';
// import locale from 'element-ui/lib/locale/lang/en' //修改成英文

// @ts-ignore
import setupStore from './store';
import mavonEditor from 'mavon-editor';
import 'mavon-editor/dist/css/index.css';
import './assets/iconfont/iconfont.css';
import PerfectScrollbar from 'vue3-perfect-scrollbar'
import 'vue3-perfect-scrollbar/dist/vue3-perfect-scrollbar.css'
// @ts-ignore
import eventBus from 'vue3-eventbus'
import rate from 'vue-rate'
import 'vue-rate/dist/vue-rate.css'
// 导入 Font Awesome 图标库
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";
import { library } from "@fortawesome/fontawesome-svg-core";
import { fas } from "@fortawesome/free-solid-svg-icons";
import { far } from "@fortawesome/free-regular-svg-icons";
import { fab } from "@fortawesome/free-brands-svg-icons";


// import mxgraph from './mxgraph';
// import './assets/variable.scss';
// import  VueScrollTo  from 'vuescroll';
import x2js from 'x2js';
library.add(fas, far, fab);

const app = createApp(App);

app.use(router);
app.use(ElementPlus)
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}
app.use(mavonEditor);
app.use(PerfectScrollbar);
app.use(eventBus);
app.use(rate);

app.component('font-awesome-icon', FontAwesomeIcon);


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

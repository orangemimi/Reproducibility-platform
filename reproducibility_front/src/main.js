import Vue from "vue";
import App from "./App.vue";
import router from "@/router";
import ElementUI from "element-ui";
import locale from "element-ui/lib/locale/lang/en"; //修改成英文
import store from "@/store";
import "element-ui/lib/theme-chalk/index.css";
import { errorHandler } from "@/lib/error";
import mavonEditor from "mavon-editor";
import "mavon-editor/dist/css/index.css";
// import "@/router/permission"; // permission control

import "./assets/iconfont/iconfont.css";
import "./assets/variable.scss";

import vuescroll from "vuescroll";

Vue.config.productionTip = false;
Vue.use(ElementUI, { locale });
Vue.use(mavonEditor);
Vue.use(vuescroll);

Vue.config.errorHandler = errorHandler;
Vue.prototype.$throw = (error) => errorHandler(error, this);
new Vue({
  router,
  store,
  render: (h) => h(App),
}).$mount("#app");

import x2js from "x2js"; // xml数据处理插件
Vue.prototype.$x2js = new x2js(); // 创建x2js对象，挂到vue原型上

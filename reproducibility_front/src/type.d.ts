// router.d.ts
declare module './router/index.js' {
    import { RouteRecordRaw } from 'vue-router';
    const routes: RouteRecordRaw[];
    export default routes;
  }
  
  // store.d.ts
  declare module "./store/index.js" {
    // 在这里编写模块的类型声明
    import { Store } from "vuex";
    const store: Store<any>; // 此处的类型可以根据你的实际 Store 类型进行调整
    export default store;
  }
  
  // @/api/request.d.ts
  declare module '@/api/request' {
    const api: any;
    export default api;
  }
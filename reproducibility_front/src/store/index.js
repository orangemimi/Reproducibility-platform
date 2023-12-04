// import * as Vue from 'vue'
import { createStore } from 'vuex';
import getters from './getters'

// // https://webpack.js.org/guides/dependency-management/#requirecontext
// const files = require.context('./modules', true, /\.js$/),
//   modules = {}

// files.keys().forEach((key) => {
//   modules[key.replace(/\.\/|\.js/g, '')] = files(key).default
// })

// // 导入所有 vuex 模块，自动加入namespaced:true，用于解决vuex命名冲突
// Object.keys(modules).forEach((key) => {
//   modules[key]['namespaced'] = true
// })
const modules = {}

// 使用 Vite 支持的动态导入语法
const importModules = async () => {
  // 使用 import.meta.globEager 一次性导入所有模块
  const moduleContext = import.meta.globEager('./modules/*.js')

  for (const path in moduleContext) { 
    if (moduleContext.hasOwnProperty(path)) {
      const moduleName = path.replace(/^\.\/modules\/|\.js$/g, '')
      modules[moduleName] = moduleContext[path].default
      modules[moduleName].namespaced = true
    }
  }
  const store = createStore({
    modules,
    getters,
  })
  return store
}


const setupStore = async () => {
  return await importModules();
}
export default setupStore;

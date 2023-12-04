import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';
import path from 'path';

function resolve(dir: string) {
  return path.join(__dirname, dir);
}

// 共用的配置选项
const sharedConfig = {
  css: {
    preprocessorOptions: {
      less: {
        modifyVars: {
          'javascriptEnabled': true,
        },
      },
      scss: {
        additionalData: `@import 'src/assets/variable.scss';`,
      },
    },
  },
  resolve: {
    alias: {
      '@': resolve('src'),
      '_com': resolve('src/components'),
    },
  },
  plugins: [
    vue(),
  ],
};

// 合并配置
export default defineConfig({
  base: '/',
  server: {
    host: '0.0.0.0',
    port: 8083,
    proxy: {
      '/api': {
        target: 'http://127.0.0.1:8082/',
        ws: true,
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, ''),
      },
    },
  },
  ...sharedConfig,
});

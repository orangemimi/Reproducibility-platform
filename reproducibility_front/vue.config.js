const path = require("path");

const resolve = (dir) => {
    return path.join(__dirname, dir);
};

const BASE_URL = "/";

module.exports = {
    publicPath: BASE_URL,
    css: {
        loaderOptions: {
            // 向 CSS 相关的 loader 传递选项
            less: {
                javascriptEnabled: true,
            },
            sass: {
                prependData: `@import 'src/assets/variable.scss';`,
            },
        },
    },
    chainWebpack: (config) => {
        config.resolve.alias
            .set("@", resolve("src"))
            .set("_com", resolve("src/components"));

        config
            .plugin("webpack-bundle-analyzer")
            .use(require("webpack-bundle-analyzer").BundleAnalyzerPlugin);
    },
    productionSourceMap: false,
    devServer: {
        host: "0.0.0.0",
        port: 8083,
        proxy: {
            "/api": {
                target: "http://127.0.0.1:8082/",
                ws: true,
                changeOrigin: true,
                pathRewrite: {
                    "^/api": "",
                },
            },
        },
    },
};

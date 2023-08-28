/**
 * meta可配置的参数有：
 * meta:{
 * title:(string)
 * requireAuth:(true)
 * }
 */
export default [
  {
    path: "*",
    name: "error_404",
    meta: {
      title: "404-页面不存在",
    },
    component: () => import("@/views/error-page/404.vue"),
  },

  {
    path: "/test/mavon",
    name: "mavon",
    meta: {
      title: "Reproducibility | mavon",
    },
    component: () => import("@/views/test/mavon"),
  },

  {
    path: "/login",
    name: "login",
    meta: {
      title: "Reproducibility | Login",
    },
    component: () => import("@/views/login"),
  },
  {
    path: "/",
    redirect: "/home",
    component: () => import("_com/layout/NormalLayout.vue"),
    children: [
      {
        path: "/home",
        name: "home",
        meta: {
          title: "Reproducibility | home",
        },
        components: {
          header: () => import("_com/layout/MyHeader.vue"),
          main: () => import("@/views/home"),
          footer: () => import("_com/layout/MyFooter.vue"),
        },
      },
      {
        path: "/projects",
        name: "Projects",
        meta: {
          title: "Reproducibility | projects",
        },
        components: {
          header: () => import("_com/layout/MyHeader.vue"),
          main: () => import("@/views/projects/list"),
          footer: () => import("_com/layout/MyFooter.vue"),
        },
      },
      {
        path: "/models",
        name: "Models",
        meta: {
          title: "models",
          requireAuth: true,
        },
        components: {
          header: () => import("_com/layout/MyHeader.vue"),
          main: () => import("@/views/models/index.vue"),
          footer: () => import("_com/layout/MyFooter.vue"),
        },
      },
      {
        path: "/data",
        name: "Data",
        meta: {
          title: "Community data",
          requireAuth: true,
        },
        components: {
          header: () => import("_com/layout/MyHeader.vue"),
          main: () => import("@/views/data/index.vue"),
          footer: () => import("_com/layout/MyFooter.vue"),
        },
      },
      {
        path: "/project/:id",
        name: "Project",
        meta: {
          title: "Reproducibility | project",
          requireAuth: true,
          // roles: ['builder'] // or you can only set roles in sub nav
        },
        components: {
          header: () => import("_com/layout/MyHeader.vue"),
          main: () => import("@/views/project"),
          footer: () => import("_com/layout/MyFooter.vue"),
        },
        children: [
          {
            path: "info",
            name: "Information",
            component: () => import("@/views/project/information"),
            meta: {
              requireAuth: true,
            },
          },
          {
            path: "construction",
            name: "Construction",
            component: () => import("@/views/project/construction/index.vue"),
            meta: {
              requireAuth: true,
            },
            // children: [
            //   {
            //     path: "compute",
            //     name: "Processes",
            //     component: () => import("@/views/project/construction/Compute"),
            //   },
            // ],
          },
          // {
          //   path: "compute",
          //   name: "Processes",
          //   meta: {
          //     requireAuth: true,
          //   },
          //   components: {
          //     header: () => import("_com/layout/MyHeader.vue"),
          //     main: () => import("@/views/project/construction/Compute"),
          //     footer: () => import("_com/layout/MyFooter.vue"),
          //   },
          // },
          // {
          //   path: "contributor",
          //   name: "Contributor",
          //   component: () => import("@/views/project/contributor"),
          //   meta: {
          //     requireAuth: true,
          //     roles: [
          //       "builder",
          //       "rebuilder_explorer",
          //       "rebuilder_operator",
          //       "visitor",
          //     ], // or you can only set roles in sub nav
          //   },
          // },
          // {
          //   path: "community",
          //   name: "Community",
          //   component: () => import("@/views/project/community"),
          //   meta: {
          //     requireAuth: true,
          //     roles: [
          //       "builder",
          //       "rebuilder_explorer",
          //       "rebuilder_operator",
          //       "visitor",
          //     ],
          //   },
          // },
          // {
          //   path: "reproduction",
          //   name: "Reproduction",
          //   component: () => import("@/views/project/reproduction"),
          //   meta: {
          //     requireAuth: true,
          //     roles: [
          //       "builder",
          //       "rebuilder_explorer",
          //       "rebuilder_operator",
          //       "visitor",
          //     ], // or you can only set roles in sub nav
          //   },
          // },
          // {
          //   path: "construction1",
          //   name: "Construction",
          //   component: () => import("@/views/project/construction1"),
          //   meta: {
          //     requireAuth: true,
          //     roles: [
          //       "builder",
          //       "rebuilder_explorer",
          //       "rebuilder_operator",
          //       "visitor",
          //     ], // or you can only set roles in sub nav
          //   },
          // },
        ],
      },

      {
        path: "/g2s",
        name: "g2s_list",
        meta: {
          title: "Reproducibility | list",
        },
        components: {
          header: () => import("_com/layout/MyHeader.vue"),
          main: () => import("@/views/g2s/list"),
          footer: () => import("_com/layout/MyFooter.vue"),
        },
      },
      {
        path: "/g2s/:id",
        name: "g2s_operation",
        meta: {
          title: "Reproducibility | operation",
          requireAuth: true,
        },
        components: {
          header: () => import("_com/layout/MyHeader.vue"),
          main: () => import("@/views/g2s/operation"),
          footer: () => import("_com/layout/MyFooter.vue"),
        },
      },
      {
        path: "/g2s/:id/compute",
        name: "g2s_compute",
        meta: {
          title: "Reproducibility | compute",
          requireAuth: true,
        },
        components: {
          header: () => import("_com/layout/MyHeader.vue"),
          main: () => import("@/views/g2s/operation/compute"),
          footer: () => import("_com/layout/MyFooter.vue"),
        },
      },
      {
        path: "/g2s/:id/view",
        name: "g2s_view",
        meta: {
          title: "Reproducibility | view",
          requireAuth: true,
        },
        components: {
          header: () => import("_com/layout/MyHeader.vue"),
          main: () => import("@/views/g2s/view"),
          footer: () => import("_com/layout/MyFooter.vue"),
        },
      },

      {
        path: "/resource/model",
        name: "model",
        meta: {
          title: "Reproducibility | model",
        },
        components: {
          header: () => import("_com/layout/MyHeader.vue"),
          main: () => import("@/views/resource/model"),
          footer: () => import("_com/layout/MyFooter.vue"),
        },
      },

      {
        path: "/resource/process",
        name: "process",
        meta: {
          title: "Reproducibility | model",
        },
        components: {
          header: () => import("_com/layout/MyHeader.vue"),
          main: () => import("@/views/resource/process"),
          footer: () => import("_com/layout/MyFooter.vue"),
        },
      },

      {
        path: "/resource/data",
        name: "data2",
        meta: {
          title: "Reproducibility | data",
        },
        components: {
          header: () => import("_com/layout/MyHeader.vue"),
          main: () => import("@/views/resource/data"),
          footer: () => import("_com/layout/MyFooter.vue"),
        },
      },

      {
        path: "/resource/data/:id",
        name: "data_detail",
        meta: {
          title: "Reproducibility | data_detail",
        },
        components: {
          header: () => import("_com/layout/MyHeader.vue"),
          main: () => import("@/views/resource/data/data_detail"),
          footer: () => import("_com/layout/MyFooter.vue"),
        },
      },
      {
        path: "/resource/:id/:type",
        name: "detail",
        meta: {
          title: "Reproducibility | detail",
        },
        components: {
          header: () => import("_com/layout/MyHeader.vue"),
          main: () => import("@/views/resource/detail"),
          footer: () => import("_com/layout/MyFooter.vue"),
        },
      },

      {
        path: "/resource/:id/:type/invoke",
        name: "invoke",
        meta: {
          title: "Reproducibility | invoke",
          requireAuth: true,
        },
        components: {
          header: () => import("_com/layout/MyHeader.vue"),
          main: () => import("@/views/resource/invoke"),
          footer: () => import("_com/layout/MyFooter.vue"),
        },
      },
      {
        path: "/resource/:id/:type/:projectId/invokeCurrent",
        name: "invoke",
        meta: {
          title: "Reproducibility | invoke",
          requireAuth: true,
        },
        components: {
          header: () => import("_com/layout/MyHeader.vue"),
          main: () => import("@/views/resource/invokeCurrent"),
          footer: () => import("_com/layout/MyFooter.vue"),
        },
      },
    ],
  },
];

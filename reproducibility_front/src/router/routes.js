// import * as Vue from 'vue'
/**
 * meta可配置的参数有：
 * meta:{
 * title:(string)
 * requireAuth:(true)
 * }
 */
export default [
  {
    path: "/:catchAll(.*)",
    name: "error_404",
    meta: {
      title: "404-页面不存在",
    },
    component: () => import("@/views/error-page/404.vue"),
  },
  {
    path: '/code',
    name: 'coding-test',
    meta: {
      title: 'coding-test',
    },
    component: () => import('@/views/project/codingOL/index.vue')
  },
  {
    path: '/form',
    name: 'form-test',
    meta: {
      title: 'form-test',
    },
    component: () => import('_com/formOL/formOL.vue')
  },
  {
    path: '/pdf',
    name: 'pdf-test',
    meta: {
      title: 'pdf-test',
    },
    component: () => import('@/views/project/document/index.vue')
  },

  {
    path: "/login",
    name: "login",
    meta: {
      title: "Reproducibility | Login",
    },
    component: () => import("@/views/login/index.vue"),
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
          main: () => import("@/views/home/index.vue"),
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
          main: () => import("@/views/projects/list/index.vue"),
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
          main: () => import("@/views/data/components/ResourceTable.vue"),
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
          main: () => import("@/views/project/index.vue"),
          footer: () => import("_com/layout/MyFooter.vue"),
        },
        children: [
          {
            path: "info",
            name: "Information",
            component: () => import("@/views/builder/information/index.vue"),
            meta: {
              requireAuth: true,
            },
          },
          {
            path: "construction",
            name: "Construction",
            component: () => import("@/views/builder/construction/index.vue"),
            meta: {
              requireAuth: true,
            },
          },
          // {
          //   path: 'scenarios',
          //   name: 'Scenarios',
          //   component:  () => import('@/views/project/scenarios/index.vue'),
          //   meta: {
          //     requireAuth: true,
          //   },
          // },
          {
            path: "workFlow",
            name: "WorkFlow",
            component: () => import("@/views/builder/workflow/index.vue"),
            meta: {
              requireAuth: true,
            },
          },
          
          {
            path: "r",
            name: "R_Info",
            component: () => import("@/views/rebuilder/info/index.vue"),
            meta: {
              requireAuth: true,
            },
          },
          {
            path: "r/reproduction",
            name: "R_Reproduction",
            component: () => import("@/views/rebuilder/info/index.vue"),
            meta: {
              requireAuth: true,
            },
          },
        ],
      },

      //     {
      //       path: '/g2s',
      //       name: 'g2s_list',
      //       meta: {
      //         title: 'Reproducibility | list',
      //       },
      //       components: {
      //         header: () => import('_com/layout/MyHeader.vue'),
      //         main: () => import('@/views/g2s/list'),
      //         footer: () => import('_com/layout/MyFooter.vue'),
      //       },
      //     },
      //     {
      //       path: '/g2s/:id',
      //       name: 'g2s_operation',
      //       meta: {
      //         title: 'Reproducibility | operation',
      //         requireAuth: true,
      //       },
      //       components: {
      //         header:  () => import('_com/layout/MyHeader.vue'),
      //         main: () => import('@/views/g2s/operation'),
      //         footer: () => import('_com/layout/MyFooter.vue'),
      //       },
      //     },
      //     {
      //       path: '/g2s/:id/compute',
      //       name: 'g2s_compute',
      //       meta: {
      //         title: 'Reproducibility | compute',
      //         requireAuth: true,
      //       },
      //       components: {
      //         header: () => import('_com/layout/MyHeader.vue'),
      //         main: () => import('@/views/g2s/operation/compute'),
      //         footer: () => import('_com/layout/MyFooter.vue'),
      //       },
      //     },
      //     {
      //       path: '/g2s/:id/view',
      //       name: 'g2s_view',
      //       meta: {
      //         title: 'Reproducibility | view',
      //         requireAuth: true,
      //       },
      //       components: {
      //         header: () => import('_com/layout/MyHeader.vue'),
      //         main: () => import('@/views/g2s/view'),
      //         footer: () => import('_com/layout/MyFooter.vue'),
      //       },
      //     },

      //     {
      //       path: '/resource/model',
      //       name: 'model',
      //       meta: {
      //         title: 'Reproducibility | model',
      //       },
      //       components: {
      //         header: () => import('_com/layout/MyHeader.vue'),
      //         main: () => import('@/views/resource/model'),
      //         footer:  () => import('_com/layout/MyFooter.vue'),
      //       },
      //     },

      //     {
      //       path: '/resource/process',
      //       name: 'process',
      //       meta: {
      //         title: 'Reproducibility | model',
      //       },
      //       components: {
      //         header: () => import('_com/layout/MyHeader.vue'),
      //         main: () => import('@/views/resource/process'),
      //         footer:  () => import('_com/layout/MyFooter.vue'),
      //       },
      //     },

      //     {
      //       path: '/resource/data',
      //       name: 'data2',
      //       meta: {
      //         title: 'Reproducibility | data',
      //       },
      //       components: {
      //         header: () => import('_com/layout/MyHeader.vue'),
      //         main: () => import('@/views/resource/data'),
      //         footer: () => import('_com/layout/MyFooter.vue'),
      //       },
      //     },

      //     {
      //       path: '/resource/data/:id',
      //       name: 'data_detail',
      //       meta: {
      //         title: 'Reproducibility | data_detail',
      //       },
      //       components: {
      //         header:() => import('_com/layout/MyHeader.vue'),
      //         main:  () => import('@/views/resource/data/data_detail'),
      //         footer:  () => import('_com/layout/MyFooter.vue'),
      //       },
      //     },
      //     {
      //       path: '/resource/:id/:type',
      //       name: 'detail',
      //       meta: {
      //         title: 'Reproducibility | detail',
      //       },
      //       components: {
      //         header:  () => import('_com/layout/MyHeader.vue'),
      //         main:  () => import('@/views/resource/detail'),
      //         footer:  () => import('_com/layout/MyFooter.vue'),
      //       },
      //     },

      //     {
      //       path: '/resource/:id/:type/invoke',
      //       name: 'invoke',
      //       meta: {
      //         title: 'Reproducibility | invoke',
      //         requireAuth: true,
      //       },
      //       components: {
      //         header:  () => import('_com/layout/MyHeader.vue'),
      //         main:  () => import('@/views/resource/invoke'),
      //         footer: () => import('_com/layout/MyFooter.vue'),
      //       },
      //     },
      //     {
      //       path: '/resource/:id/:type/:projectId/invokeCurrent',
      //       name: 'invoke',
      //       meta: {
      //         title: 'Reproducibility | invoke',
      //         requireAuth: true,
      //       },
      //       components: {
      //         header:  () => import('_com/layout/MyHeader.vue'),
      //         main:  () => import('@/views/resource/invokeCurrent'),
      //         footer:  () => import('_com/layout/MyFooter.vue'),
      //       },
      //     },
    ],
  },
];

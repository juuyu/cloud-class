// import process from 'process';
import path from 'path'
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'

import Unocss from 'unocss/vite'
import {
  presetAttributify,
  presetIcons,
  presetUno,
  transformerDirectives,
  transformerVariantGroup,
} from 'unocss'

const pathSrc = path.resolve(__dirname, 'src')

// https://vitejs.dev/config/
export default defineConfig({
  // define: {
  //   'process.env': process.env,
  // },
  // define: {
  //   global: {},
  //   // 'process.env': process.env
  // },
  // optimizeDeps: {
  //   include: [
  //     'randombytes'
  //   ]
  // },
  optimizeDeps: {
    include: [
        '@kangc/v-md-editor/lib/theme/vuepress.js'
    ]
  },
  resolve: {
    alias: {
      '~/': `${pathSrc}/`
    }
  },
  css: {
    preprocessorOptions: {
      scss: {
        additionalData: `@use "~/styles/element/index.scss" as *;`,
      },
    },
  },
  plugins: [
    vue(),
    Components({
      // allow auto load markdown components under `./src/components/`
      extensions: ['vue', 'md'],
      // allow auto import and register components used in markdown
      include: [/\.vue$/, /\.vue\?vue/, /\.md$/],
      resolvers: [
        ElementPlusResolver({
          importStyle: 'sass',
        }),
      ],
      dts: 'src/components.d.ts',
    }),


    // https://github.com/antfu/unocss
    // see unocss.config.ts for config
    Unocss({
      presets: [
        presetUno(),
        presetAttributify(),
        presetIcons({
          scale: 1.2,
          warn: true,
        }),
      ],
      transformers: [
        transformerDirectives(),
        transformerVariantGroup(),
      ]
    }),
  ],
  // server
  server: {
    hmr: {overlay: false}, // 禁用或配置 HMR 连接 设置 server.hmr.overlay 为 false 可以禁用服务器错误遮罩层
    // 服务配置
    port: 80, // 类型： number 指定服务器端口;
    https: false,
    open: false, // 类型： boolean | string在服务器启动时自动在浏览器中打开应用程序；
    cors: true, // 类型： boolean | CorsOptions 为开发服务器配置 CORS。默认启用并允许任何源
    host: '0.0.0.0', // IP配置，支持从IP启动
    proxy: {
      '/api': {
        target: 'http://127.0.0.1:8081',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '')
      },
      '/amap':{
        target: 'https://restapi.amap.com/v3',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/amap/, '')
      }
    }
  },

  // build
  build: {
    target: 'es2015',
    minify: 'terser',// 混淆器，terser构建后文件体积更小
    terserOptions: {
      compress: {
        keep_infinity: true,
        drop_console: true,
      },
    },
    // rollupOptions: {
    //   // 确保外部化处理那些你不想打包进库的依赖
    //   external: [],
    //   // https://rollupjs.org/guide/en/#big-list-of-options
    // },
    watch: {
      // https://rollupjs.org/guide/en/#watch-options
    },
    // Turning off brotliSize display can slightly reduce packaging time
    brotliSize: false,
    chunkSizeWarningLimit: 2000,
  },
})

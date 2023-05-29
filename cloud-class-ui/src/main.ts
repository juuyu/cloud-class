import { createApp } from "vue";
import App from "./App.vue"
import router from './router'
import { createPinia } from 'pinia'
// import "~/styles/element/index.scss";

import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
// import ElementPlus from "element-plus";
// import all element css, uncommented next line
import 'element-plus/theme-chalk/src/index.scss'
// or use cdn, uncomment cdn link in `index.html`

import "~/styles/index.scss";
import 'uno.css'

import { Boot } from '@wangeditor/editor'
import formulaModule from '@wangeditor/plugin-formula'
Boot.registerModule(formulaModule)
// @ts-ignore
import VMdPreviewHtml from '@kangc/v-md-editor/lib/preview-html';
import '@kangc/v-md-editor/lib/style/preview-html.css';
// @ts-ignore
import vuepressTheme from '@kangc/v-md-editor/lib/theme/vuepress.js';
import '@kangc/v-md-editor/lib/theme/style/vuepress.css';

// @ts-ignore
import Prism from 'prismjs';
// 代码高亮
import 'prismjs/components/prism-json';

import VueLatex from 'vatex'


// If you want to use ElMessage, import it.
// import "element-plus/theme-chalk/src/message.scss"
const store = createPinia();
const app = createApp(App);


// @ts-ignore
import vue3videoPlay from 'vue3-video-play' // 引入组件
import 'vue3-video-play/dist/style.css' // 引入css
app.use(vue3videoPlay)



// window.global = window;
// app.use(ElementPlus);
app.use(VMdPreviewHtml);
app.use(VueLatex)
app.use(ElementPlus)
app.use(store);
app.use(router);
app.mount("#app");



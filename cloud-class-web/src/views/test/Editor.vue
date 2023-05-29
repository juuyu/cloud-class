<template>
  <el-container>
    <el-main style="margin: 30px auto;width: 90%">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-scrollbar style="border: 1px solid #dcdfe6;padding: 20px;height: 600px;">
            <div>
              <el-input v-model="showValue">
                <template #suffix>
                  <div style="cursor: pointer;"
                       class="flex item-center"
                       @click="copyInviteCode"
                  >
                    <DocumentCopy style="width: 18px; height: 18px;"/>
                  </div>
                </template>
              </el-input>
            </div>
            <div>
              <preview-html :show-value="showValue"/>
            </div>
          </el-scrollbar>
        </el-col>
        <el-col :span="12">
          <div style="height: 1000px">
            <div style="border: 1px solid #dcdfe6;height: 1000px">
              <Toolbar
                  style="border-bottom: 1px solid #ccc"
                  :editor="editorRef"
                  :defaultConfig="toolbarConfig"
                  mode="default"
              />
              <Editor
                  style="height: 600px; overflow-y: hidden;"
                  v-model="valueHtml"
                  :defaultConfig="editorConfig"
                  mode="default"
                  @onCreated="handleCreated"
              />
            </div>
          </div>
          <el-form-item>
            <el-button type="primary" @click="submit">提交
            </el-button>
          </el-form-item>
        </el-col>
      </el-row>
    </el-main>
  </el-container>
</template>
<script lang="ts" setup>
import {
  DocumentCopy,
} from '@element-plus/icons-vue'
import '@wangeditor/editor/dist/css/style.css' // 引入 css
import {Editor, Toolbar} from '@wangeditor/editor-for-vue'
import {onBeforeUnmount, ref, shallowRef} from "vue";
import {ElMessage} from "element-plus";
import PreviewHtml from "~/components/table/editor/preview-html.vue";

/*-------------------------------------------------------------------------------------------------------------------*/
// generate template


/*-------------------------------------------------------------------------------------------------------------------*/

const submit = () => {
  showValue.value = valueHtml.value
  console.log(valueHtml.value)
}
const showValue = ref('')
const copyInviteCode = async () => {
  await navigator.clipboard.writeText(showValue.value)
  ElMessage.success("复制成功")
}
/* ---------------------------------------------------- editor -------------------------------------------------- */
// 编辑器实例，必须用 shallowRef
const editorRef = shallowRef()
// 内容 HTML
const valueHtml = ref('')
const toolbarConfig = {
  excludeKeys: [
    'underline',
    'italic',
    'bgColor',
    'group-justify',
    'group-indent',
    'group-video',
    'fontSize',
    'fontFamily',
    'lineHeight',
    'lineHeight',
    'undo',
    'redo',
    'fullScreen',
    '|',
  ],
  // 选中 公式节点 时的悬浮菜单
  insertKeys: {
    index: 0, // 自定义
    keys: [
      'insertFormula', // “插入公式”菜单
      // 'editFormula' // “编辑公式”菜单
    ],
  },
}
const editorConfig = {
  // 选中 公式节点 时的悬浮菜单
  hoverbarKeys: {
    formula: {
      menuKeys: ['editFormula'], // “编辑公式”菜单
    },
  },
  placeholder: '请输入内容...'
}
// 组件销毁时，也及时销毁编辑器
onBeforeUnmount(() => {
  const editor = editorRef.value
  if (editor == null) return
  editor.destroy()
})
const handleCreated = (editor: any) => {
  editorRef.value = editor // 记录 editor 实例，重要！
}
/* -------------------------------------------------------------------------------------------------------------- */
</script>
<style>
</style>

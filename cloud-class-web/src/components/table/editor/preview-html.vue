<template>
  <div>
    <Editor
        style="overflow-y: hidden;height: 300px"
        v-model="showValue"
        :defaultConfig="showConfig"
        @onCreated="handleCreated"
        mode="simple"
    />
  </div>
</template>
<script lang="ts" setup>
import {Editor} from "@wangeditor/editor-for-vue";
import '@wangeditor/editor/dist/css/style.css'
import {onBeforeUnmount, shallowRef, toRefs} from "vue"; // 引入 css
interface Props {
  showValue: string
}

const props = defineProps<Props>()
const {showValue} = toRefs(props)

const editorRef = shallowRef()
const handleCreated = (editor: any) => {
  editorRef.value = editor // 记录 editor 实例，重要！
}
const showConfig = {
  readOnly: true
}
// 组件销毁时，也及时销毁编辑器
onBeforeUnmount(() => {
  const editor = editorRef.value
  if (editor == null) return
  editor.destroy()
})
</script>
<style scoped>

</style>
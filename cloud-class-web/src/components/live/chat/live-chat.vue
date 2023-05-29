<template>
  <div class="chatBox">
    <div class="moduleContent" id="moduleContent">
      <el-scrollbar>
        <template v-for="(item,index) in chatContentLists" :key="index">
          <!--          <div style="text-align: center;color: #FFF7F7"-->
          <!--               v-show="index===0?true:item.time-chatContentLists[index-1].time>=15000">{{-->
          <!--            }}-->
          <!--          </div>-->
          <div class="other-content" v-if="item.sendUserName!==userName" key="index">
            <div>
              <img :src="item.avatar" alt="">
            </div>
            <div>
              <div class="chatUserName">
                {{ item.sendUserRealName }}
              </div>

              <div class="other-content-msg">
                <div v-html="item.msg"></div>
              </div>
            </div>

          </div>
          <div class="my-content" v-else>
            <div class="my-content-msg">
              <div v-html="item.msg"></div>
            </div>
            <img :src="item.avatar" alt="">
          </div>
        </template>
      </el-scrollbar>
    </div>
    <div class="editor">
      <Toolbar
          @click="focus"
          style="width:100%;height: 40px;"
          :editor="editorRef"
          :defaultConfig="toolbarConfig"
          mode="default"
      />
      <Editor
          style="width:100%;height: 70px"
          v-model="content"
          @keyup.enter="SendInformation"
          :defaultConfig="editorConfig"
          @onCreated="handleCreated"
          mode="default"
      />
      <div class="button">
        <el-tooltip
            class="box-item"
            effect="light"
            content="发送"
            placement="top"
        >
          <div @click="SendInformation" style="cursor: pointer">
            <img alt="" src="src/assets/send.svg"/>
          </div>
        </el-tooltip>
      </div>
    </div>
  </div>
</template>
<script lang="ts" setup>
import {defineComponent, onMounted, reactive, ref, shallowRef, toRefs} from 'vue'
import {Editor, Toolbar} from '@wangeditor/editor-for-vue'
import '@wangeditor/editor/dist/css/style.css' // 引入 css
import {IEditorConfig} from '@wangeditor/editor'
import localCache from "~/utils/cache";
import {ChatRespMessage, genChatSendMessage} from "~/utils/wsMessageUtil";
import {sendMsg} from "~/utils/webSocket";

interface Props {
  roomId: string,
  userName: string,
  chatContentLists: ChatRespMessage[]
}

const props = defineProps<Props>()
const {roomId, userName, chatContentLists} = toRefs(props)

const content = ref()
// 编辑器实例，必须用 shallowRef
const editorRef: any = shallowRef()
const editorConfig: Partial<IEditorConfig> = {  // TS 语法
  MENU_CONF: {
    uploadImage: {
      fieldName: 'your-fileName',
      base64LimitSize: 10 * 1024 * 1024 // 10M 以下插入 base64
    },

  },
  hoverbarKeys: {
    'image': {
      // 清空 image 元素的 hoverbar
      menuKeys: [],
    },
    'text': {
      menuKeys: [],
    }
  },
  placeholder: "请输入内容...",
  scroll: true,
}
const toolbarConfig: any = {
  toolbarKeys: ['emotion', 'uploadImage']
}
const handleCreated = (editor: any) => {
  editorRef.value = editor
}
//发送消息方法
const SendInformation = (e: any) => {
  e.preventDefault()
  let brReg = /<br\s*\/?>/gi
  content.value = content.value.replace(brReg, '')
  console.log(content.value)
  const data = genChatSendMessage(roomId.value, content.value)
  console.log(data)
  sendMsg(data)
  content.value = ''
}
</script>
<style scoped>
.chatBox {
  width: 100%;
  height: 100%;
}

.moduleContent {
  height: calc(100% - 170px);
  width: 100%;
  margin-bottom: 20px;
  background-image: url("/src/assets/live/chat_background.svg");
  background-repeat: no-repeat;
  background-size: 200px auto;
  background-position: center;
}

.other-content {
  margin-top: 10px;
  display: flex;
  justify-content: left;
  align-items: flex-start;
}

.chatUserName {
  margin-left: 5px;
  line-height: 20px;
  font-size: 13px;
  color: #606266;
}

.other-content-msg {
  max-width: 500px;
  min-width: 20px;
  min-height: 24px;
  border-radius: 5px;
  padding: 5px;
  position: relative;
  margin-left: 5px;
  word-break: break-all;
  word-wrap: break-word;
  background-color: #FFFFFF;
  display: flex;
  align-items: center;
}

.other-content-msg::before {
  position: absolute;
  top: 5px;
  left: -10px;
  content: '';
  width: 0;
  height: 0;
  border-right: 5px solid #FFFFFF;
  border-bottom: 5px solid transparent;
  border-left: 5px solid transparent;
  border-top: 5px solid transparent;
}

.my-content {
  margin-top: 10px;
  display: flex;
  justify-content: right;
  align-items: flex-start;
}

.my-content-msg {
  max-width: 500px;
  min-width: 20px;
  min-height: 24px;
  padding: 5px;
  border-radius: 5px;
  position: relative;
  margin-right: 5px;
  word-break: break-all;
  word-wrap: break-word;
  background-color: #67C23A;
  display: flex;
  align-items: center;
}

.my-content-msg::after {
  position: absolute;
  top: 5px;
  right: -10px;
  content: '';
  width: 0;
  height: 0;
  border-right: 5px solid transparent;
  border-bottom: 5px solid transparent;
  border-left: 5px solid #67C23A;
  border-top: 5px solid transparent;
}

img {
  width: 40px;
  height: 40px;
  border-radius: 5px;
  margin-left: 5px;
  margin-right: 5px;
}

.editor {
  height: 150px;
  width: 100%;
  box-sizing: border-box;
}

:deep(div) {
  padding: 0px;
  margin: 0px;
}

:deep(p) {
  padding: 0px;
  margin: 0px;
}


:deep(div) {
  padding: 0px;
  margin: 0px;
}

:deep(p) {
  padding: 0px;
  margin: 0px;
}

:deep(img) {
  max-width: 100px;
  max-height: 100px;
}

.button {
  height: 30px;
  width: 100%;
  margin-right: 10px;
  display: flex;
  justify-content: right;
  align-items: center;
  background-color: #FFFFFF;
}

.button img {
  height: 30px;
}

:deep(.w-e-text-container p) {
  margin: 0;
}

:deep(.w-e-text-placeholder ) {
  top: 0;
}
</style>

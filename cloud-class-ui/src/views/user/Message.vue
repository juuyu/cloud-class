<template>
  <el-container>
    <el-header>
      <header-base
          style="margin: auto;width: 90%;height: 60px;position: fixed;z-index: 10; transform: translate(-50%,0);left:50%"/>
    </el-header>
    <el-main style="margin: 30px auto;width: 65%">
      <el-page-header
          :icon="ArrowLeft"
          @back="goBack"
          style="margin-bottom: 50px;"
      >
        <template #content>
          <span class="text-large font-600 mr-3"> 信息中心 </span>
        </template>
      </el-page-header>
      <div style="width: 100%;">
        <div style="float: left">
          <el-form :inline="true" class="demo-form-inline">
            <el-form-item label="消息名">
              <el-input placeholder="搜索消息" v-model="searchData.messageName"/>
            </el-form-item>
            <el-form-item label="消息状态">
              <el-select placeholder="选择" style="width: 100px" v-model="searchData.msgRead">
                <el-option label="全部" value=""/>
                <el-option label="未读" value="false"/>
                <el-option label="已读" value="true"/>
              </el-select>
            </el-form-item>
          </el-form>
        </div>
        <div style="float: right">
          <el-button type="primary" bg :icon="Search" @click="searchMessage">搜索</el-button>
        </div>
      </div>
      <div>
        <el-table
            :data="messageList"
            style="width: 100%"
        >
          <el-table-column property="msgName"/>
          <el-table-column property="sendTime"/>
          <el-table-column property="sendRealName"/>
          <el-table-column property="sendUserName"/>
          <el-table-column>
            <template #default="scope">
              <el-tag type="success" v-if="scope.row.msgRead">已读</el-tag>
              <el-tag type="warning" v-else>未读</el-tag>
            </template>
          </el-table-column>
          <el-table-column align="right">
            <template #default="scope">
              <el-button
                  size="small"
                  type="primary"
                  @click="getMessageDetail(scope.row)"
              >
                查看
              </el-button>
              <el-button
                  size="small"
                  type="danger"
                  @click="deleteMessage(scope.row.id)"
              >
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div style="margin-top: 50px;float: right;">
        <el-pagination
            background
            layout="prev, pager, next"
            :page-size="10"
            :total="total"
            v-model:current-page="pageNum"
            @current-change="getMessageList"
        />
      </div>
    </el-main>
    <el-dialog
        v-model="messageVisible"
        title="信息内容"
        @close="getMessageList"
    >
      <div>
        <preview-html :show-value="message.msgContent" />
<!--        <v-md-preview-html :html="message.msgContent"-->
<!--                           preview-class="vuepress-markdown-body"></v-md-preview-html>-->
      </div>

      <template #footer>
      <span class="dialog-footer" v-show="message.needReply">
        <el-button type="warning" @click="reply(message.confirmUrl, false)">
          拒绝
        </el-button>
        <el-button type="success" @click="reply(message.confirmUrl, true)">
          同意
        </el-button>
      </span>
      </template>
    </el-dialog>
  </el-container>
</template>
<script lang="ts" setup>
import {
  ArrowLeft, Search,
} from '@element-plus/icons-vue'
import '@wangeditor/editor/dist/css/style.css' // 引入 css
import localCache from "~/utils/cache";
import {onMounted, reactive, ref} from "vue";
import {getData} from "~/api/main";
import {ElMessage, ElMessageBox, ElTable} from "element-plus";
import router from "~/router";

const user = localCache.getCache("userInfo")


onMounted(() => {
  getMessageList()
})
/*-------------------------------------------------------------------------------------------------------------------*/
const goBack = () => {
  router.back()
}
/*-------------------------------------------------------------------------------------------------------------------*/
const pageNum = ref(1)
const total = ref(1)
const messageList = ref([])
const messageVisible = ref(false)
const message = ref({})
const searchData = reactive({
  messageName: "",
  msgRead: "",
})
const searchMessage = () => {
  pageNum.value = 1
  getMessageList()
}
const reply = (messageCode: string, reply: boolean) => {
  getData("/message/reply", {
    messageCode: messageCode,
    reply: reply
  }).then((res: any) => {
    let {data} = res
    if (data.code === 200) {
      ElMessage.success("回复成功")
    } else {
      ElMessage.error(data?.msg)
    }
    messageVisible.value = false
  })
}

//isAsc:"desc",orderByColumn:"id,createTime"
const getMessageList = () => {
  getData("/message", {
    pageSize: 10,
    pageNum: pageNum.value,
    userId: user.id,
    isAsc: "desc",
    orderByColumn: "sendTime,id",
    messageName: searchData.messageName,
    msgRead: searchData.msgRead
  }).then((res: any) => {
    let {data} = res;
    console.log(data)
    if (data.code === 200) {
      const data1 = data.data
      if (data1 !== null) {
        total.value = data1.total
        messageList.value = data1.rows
        console.log(messageList.value)
      }
    } else {
      ElMessage.error(data?.msg)
    }
  })
}

const getMessageDetail = (row: any) => {
  message.value = row
  messageVisible.value = true
  if (!row.msgRead) {
    getData("/message/read", {
      messageId: row.id
    })
  }
}

/* -------------------------------------------------------------------------------------------------------------- */
const deleteMessage = (id: any) => {
  ElMessageBox.confirm(
      '确认删除？',
      '删除消息',
      {
        confirmButtonText: '删除',
        cancelButtonText: '取消',
        type: 'warning',
        autofocus: false,
      })
      .then(() => {
        console.log(id)
        getData("message/del", {
          messageId: id
        }).then((res: any) => {
          let {data} = res;
          console.log(data)
          if (data.code === 200) {
            ElMessage.success("删除成功")
            getMessageList()
          } else {
            ElMessage.error(data.msg)
          }
        })
      })
      .catch(() => {
      })
}
</script>
<style>
</style>

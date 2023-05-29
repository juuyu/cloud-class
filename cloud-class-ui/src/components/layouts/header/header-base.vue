<template>

  <el-menu mode="horizontal" :ellipsis="false" router>
    <div class="flex items-center">
      <span>
        <img alt="Cloud-Class" style="height: 55px;" src="../../../assets/logo.png"/>
      </span>
    </div>

    <el-menu-item index="1" route="/open/course">开放课程</el-menu-item>
    <el-menu-item index="2" route="/open/video">开放视频</el-menu-item>
    <div class="flex-grow"/>

    <div class="flex items-center">
      <el-dropdown>
        <span class="el-dropdown-link">
          <el-avatar :size="45" class="mr-3"
                     src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" v-show="user==null"/>
          <el-avatar :size="45" class="mr-3"
                     :src="user.avatar"/>
        </span>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item @click="router.push('/user/profile')">个人信息</el-dropdown-item>
            <!--            <el-dropdown-item>修改密码</el-dropdown-item>-->
            <el-dropdown-item divided @click="logout">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>

    <el-menu-item index="5" route="/user/workTable">工作台</el-menu-item>
    <el-sub-menu index="6">
      <template #title>课程</template>
      <el-menu-item index="2-1" route="/course/join">加入的课程</el-menu-item>
      <el-menu-item index="2-2" route="/course/create">开设的课程</el-menu-item>
    </el-sub-menu>

    <el-menu-item index="7" route="/tool/record">录制</el-menu-item>
    <el-menu-item index="8" route="/record/video">视频</el-menu-item>
    <el-menu-item index="9" route="/user/message">消息</el-menu-item>
    <!--    <el-menu-item index="9">动态</el-menu-item>-->
    <!--    <el-menu-item index="9">设置</el-menu-item>-->
  </el-menu>


</template>
<script lang="ts" setup>
import {ElMessage, ElNotification} from "element-plus";
import {Search} from '@element-plus/icons-vue'
import router from "~/router";
import {useIndexStore} from "~/store";
import {onMounted, onUnmounted, ref} from "vue";
import localCache from "~/utils/cache";
import {getData} from "~/api/main";

const user = localCache.getCache("userInfo")
const store = useIndexStore()
const logout = () => {
  router.push(
      {
        path: '/',
        query: {}
      }
  )
  store.logout().then(() => {
    ElMessage.success("已退出")
  })
}
let timeId: NodeJS.Timer | undefined
onMounted(() => {
  const userId = localCache.getCache("userInfo").id
  timeId = setInterval(() => hasNewMessage(userId), 5000);
})
onUnmounted(() => {
  clearInterval(timeId)
})

function hasNewMessage(userId: any): void {
  getData("/message/query", {
    userId: userId,
  }).then((res: any) => {
    let {data} = res;
    if (data.data) {
      ElNotification({
        title: '你收到一条新消息',
        message: '点击查看',
        type: 'success',
        duration: 4000,
        onClick: gotoMessage
      })
    }
  })
}

const gotoMessage = () => {
  router.push("/user/message")
}


</script>

<style>
.flex-grow {
  flex-grow: 1;
}


.input-with-select {
  /*width: 120px;*/
}

.input-with-select .el-input-group__prepend {
  /*width: 30px;*/
  /*padding: 0;*/
  /*background-color: #FFFFFF;*/
}

.type-select {
  width: 70px;
  background-color: #FFFFFF;
}

.input-with-select .ep-input-group__append {
  width: 20px;
  /*border-bottom-right-radius: 20px;*/
  /*border-top-right-radius: 20px;*/
  background-color: #f4f4f5;
  cursor: pointer;
}

.input-with-select .ep-input-group__append:hover {
  background-color: #e9e9eb;
}

</style>

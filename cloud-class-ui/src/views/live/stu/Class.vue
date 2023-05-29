<template>
  <div>
    <div class="live-header">
      <div class="live-title">
        <span style="margin-left: 20px">{{ course.courseName }}</span>
        <el-tag effect="plain" style="margin-left: 20px">{{ course.courseType }}</el-tag>
      </div>
      <div style="width: 33%">
        <!--          <Timing/>-->
      </div>
      <el-tooltip
          content="退出课堂"
          effect="light">
        <div @click="exitRoom" class="exit">
          <img alt="" src="/src/assets/media/close.svg"/>
        </div>
      </el-tooltip>
    </div>
    <div class="main">
      <div class="live-screen">
        <video id="screenRecord" controls v-show="isScreen" autoplay></video>
        <!--        <img v-show="!isScreen" src="src/assets/live/screen_background.svg"-->
        <!--             alt=""/>-->
      </div>
      <div class="live-side">
        <div style="width: 350px;height: 250px;">
          <video id="userCamera" controls v-show="isCamera" autoplay></video>
          <div v-show="!isCamera" class="no-camera">
            <img :src="roomInfo?.teacherInfo?.avatar"
                 alt="" style="width: 90px;border-radius: 16px"/>
          </div>
        </div>
        <el-tabs v-model="activeName" @tab-click="handlerClick" class="chat-tabs" :stretch="true">
          <el-tab-pane name="first">
            <template #label>
              <div style="display: flex;justify-content: center;align-items: center;height: 40px">
                <img src="src/assets/live/chat.svg" alt="" style="height: 25px">
              </div>
            </template>
            <live-chat class="chat"
                       :chat-content-lists="chatContentLists"
                       :room-id="roomId.toString()"
                       :user-name="user.userName"
            />
          </el-tab-pane>
          <el-tab-pane name="second">
            <template #label>
              <div style="display: flex;justify-content: center;align-items: center;height: 40px">
                <img src="src/assets/live/users.svg" alt="" style="height: 25px">
              </div>
            </template>
            <live-users
                v-show="userList.length !== 0"
                :data-list="userList"
                :is-admin="false"
                :room-id="roomId.toString()"
                @get-live-users="getLiveUsers"
            />
            <el-empty description="暂无人员加入" v-show="userList.length === 0"/>
          </el-tab-pane>
          <!--          <el-tab-pane name="third">-->
          <!--            <template #label>-->
          <!--              <div style="display: flex;justify-content: center;align-items: center;height: 40px">-->
          <!--                <img src="src/assets/live/tools.svg" alt="" style="height: 25px">-->
          <!--              </div>-->
          <!--            </template>-->
          <!--            <tool-class-stu></tool-class-stu>-->
          <!--          </el-tab-pane>-->
        </el-tabs>


      </div>
    </div>
  </div>
</template>
<script lang="ts" setup>
import {onBeforeMount, onMounted, onUnmounted, reactive, ref} from 'vue'
import {ElMessage, ElMessageBox, TabsPaneContext} from 'element-plus'
import {useRoute} from "vue-router";
import {JSWebrtc} from '~/components/JSWebrtc/JSWebrtc'
import router from "~/router";
import ToolClassStu from "~/components/live/tool/tool-class-stu.vue";
import LiveUsers from "~/components/live/user/live-users.vue";
import localCache from "~/utils/cache";
import {
  ChatRespMessage,
  CommandRespMsg, genCommandSendMessage,
  joinOrLeaveChatRoom,
  WsMessage
} from "~/utils/wsMessageUtil";
import {getToken} from "~/utils/auth";
import {destroyWs, sendMsg, startWs} from "~/utils/webSocket";
import {getData} from "~/api/main";

const activeName = ref('first')
const {query} = useRoute();
const courseId = query?.courseId
const courseCode: any = query?.courseCode
const roomId: any = query?.roomId
// 课程信息
const course = localCache.getCache(courseCode.toString())
// 直播信息
const liveInfo = localCache.getCache(roomId.toString())
// 用户信息
const user = localCache.getCache("userInfo")

const screenRecordUrl: string = liveInfo.screenVideoPlay
const userCameraUrl: string = liveInfo.userVideoPlay

const isCamera = ref(false)
const isScreen = ref(false)
let screenRecord: any
let userCamera: any

// 加载直播视频
const loadWebRtc = (camera: boolean, screen: boolean) => {
  if (camera) {
    console.log("camera," + userCameraUrl)
    userCamera = new JSWebrtc.Player(userCameraUrl, {
      video: document.getElementById('userCamera'),
      autoplay: true,
    })
  }

  if (screen) {
    console.log("screen," + screenRecordUrl)
    screenRecord = new JSWebrtc.Player(screenRecordUrl, {
      video: document.getElementById('screenRecord'),
      autoplay: true,
    })
  }
}
const whiteboardUrl = ref("")
onMounted(() => {

  getRoomInfo()


  startWs(options)
  // // 加入房间

  //
})

onUnmounted(() => {
  destroyWs()
})

const roomInfo = ref()
const backgroundImage = ref()
const getRoomInfo = () => {
  getData("live/info", {
    roomId: roomId.toString()
  }).then((res: any) => {
    let {data} = res
    if (data.code === 200) {
      const roomInfoData = data.data
      console.log(roomInfoData)
      roomInfo.value = roomInfoData
      backgroundImage.value = `url(${roomInfoData?.teacherInfo?.avatar})`
      // whiteboardUrl.value = roomInfoData.whiteboardUrl
      isCamera.value = roomInfoData.camera
      isScreen.value = roomInfoData.screen
      loadWebRtc(isCamera.value, isScreen.value)
    } else {
      ElMessage.error(data.msg)
    }
  })
}

//聊天信息
const chatContentLists: ChatRespMessage[] = reactive([])
let options: any = {
  url: 'ws://localhost:7001/websocket',
  protocols: [getToken()],
  openCb: (res: any) => {
    // ElMessage.success("ws连接成功")
    sendMsg(joinOrLeaveChatRoom(roomId.toString(), "欢迎 [ " + user.realName + " ] 进入云课堂"))
    console.log("----------------------ws连接成功----------------------")
  }, // 连接成功的回调
  closeCb: (res: any) => {
    console.log("----------------------ws连接关闭----------------------")
  }, // 关闭的回调
  messageCb: (res: any) => {
    console.log(res)
    const resMsg: WsMessage = JSON.parse(res)
    if (resMsg.messageType === "群聊") {
      chatContentLists.push(JSON.parse(resMsg.dataJsonString))
    } else if (resMsg.messageType === "指令") {
      const commandMsg: CommandRespMsg = JSON.parse(resMsg.dataJsonString)
      if (commandMsg.camera) {
        try {
          userCamera?.destroy()
          userCamera = null
        }catch (e) {
          console.log(e)
        }
        loadWebRtc(true, false)
        isCamera.value = true
        // if (!isCamera.value) {
        //   userCamera = null
        //   loadWebRtc(true, false)
        //   isCamera.value = true
        // }
      } else {
        if (isCamera.value) {
          isCamera.value = false
          userCamera = null
        }
      }
      if (commandMsg.screen) {
        if (!isScreen.value) {
          try {
            screenRecord?.destroy()
            screenRecord = null
          }catch (e) {
            console.log(e)
          }
          loadWebRtc(false, true)
          isScreen.value = true
        }
      } else {
        if (isScreen.value) {
          // ElMessage.success("销毁屏幕")
          isScreen.value = false
          try {
            screenRecord?.destroy()
            screenRecord = null
          }catch (e) {
            console.log(e)
          }
        }
      }
      if (commandMsg.exit) {
        ElMessageBox.confirm(
            '课程已结束, 请退出',
            '退出课堂',
            {
              confirmButtonText: '退出',
              type: 'warning',
              autofocus: false,
              showClose: false,
              showCancelButton: false
            })
            .then(() => {
              ElMessage({
                type: 'success',
                message: '课程结束',
              })

              leaveLive()
            })
      }

    }

  }, // 消息的回调
  errorCb: (res: any) => {

  } // 错误的回调
}

const loadUserCamera = () => {
  isCamera.value = true
  loadWebRtc(true, false)

}


const leaveLive = () => {
  informLeaveLive()
  ElMessage({
    type: 'success',
    message: '已退出',
  })
  getData("live/stu/leave", {
    roomId: roomId.toString()
  })
  router.push({
    path: "/course/info",
    query: {
      courseId: courseId,
      courseCode: courseCode
    }
  })
}

const userList = ref([])
const handlerClick = (tab: TabsPaneContext) => {
  const name = tab.paneName
  console.log("click name:" + name)

  switch (name) {

    case "second":
      getLiveUsers()
      break


    default:
      console.log("handlerClick fail")
  }
}
const getLiveUsers = () => {
  getData("/live/users", {
    roomId: roomId.toString()
  }).then((res: any) => {
    let {data} = res
    if (data.code === 200) {
      userList.value = data.data
    } else {
      ElMessage.error(data.msg)
    }
  })
}

const informLeaveLive = async () => {
  sendMsg(joinOrLeaveChatRoom(roomId.toString(), "[ " + user.realName + " ] 已退出"))
}
const exitRoom = () => {
  ElMessageBox.confirm(
      '确认退出？',
      '退出课堂',
      {
        confirmButtonText: '退出',
        cancelButtonText: '取消',
        type: 'warning',
        autofocus: false,
      })
      .then(() => {
        leaveLive()
      })
}
</script>

<style scoped>
.no-camera {
  position: relative;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background-position: center;
  background-repeat: no-repeat;
  background-size: cover;
  backdrop-filter: blur(10px);
  /*filter: blur(10px);*/
  /*background-image: url('https://th.bing.com/th/id/OIP.FUVMWHUuCm-O_JYn0F238gHaHa?w=201&h=201&c=7&r=0&o=5&dpr=2&pid=1.7')*/
}

.no-camera::before {
  content: "";
  position: absolute;
  width: 95%;
  height: 90%;
  background-image: v-bind(backgroundImage);
  background-position: center;
  background-repeat: no-repeat;
  background-size: cover;
  /*transform: scale(0.9);*/
  filter: blur(10px);
  z-index: -1;
}

.live-header {
  width: 100%;
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background-color: #F2F3F5;
  font-size: 16px;
  color: #303133;
}

.exit {
  display: flex;
  align-items: center;
  cursor: pointer
}

.exit img {
  height: 30px;
  width: 30px;
  margin-right: 20px;
}

.live-title {
  width: 33%;
  display: flex;
  align-items: center;
}

.main {
  width: 100%;
  height: calc(100vh - 50px);
  display: flex;
  background-color: #F2F3F5;
  overflow-y: hidden;
}

.live-screen {
  width: calc(100% - 350px);
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background-image: url("src/assets/live/screen_background.svg");
}

/*.live-screen img {*/
/*  width: 60%;*/
/*}*/

.live-side {
  width: 350px;
  height: 100%;
}

.chat-tabs {
  width: 330px;
  height: calc(100vh - 320px);
  margin: 10px;
}

/*:deep(.chatBox){*/
/*  height: calc(100vh - 370px);*/
/*}*/

/*:deep(.tabs__header) {*/
/*  height: 40px;*/
/*}*/

:deep(.ep-tabs__content) {
  height: calc(100% - 40px);
}

:deep(.ep-tab-pane) {
  height: 100%;
}


video {
  width: 100%;
  height: 100%;
}

video::-webkit-media-controls-timeline {
  display: none;
}

video::-webkit-media-controls-current-time-display {
  display: none;
}

video::-webkit-media-controls-time-remaining-display {
  display: none;
}

</style>

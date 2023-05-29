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
      <div class="live-tools">
        <div class="live-tools-video">
          <img alt="" v-show="isVideo" src="src/assets/media/video_on.svg"/>
          <img alt="" v-show="!isVideo" src="src/assets/media/video_off.svg"/>
          <el-tooltip
              content="摄像头"
              effect="light">
            <el-switch
                v-model="isVideo"
                @change="switchVideo"
                size="small"
            />
          </el-tooltip>
        </div>
        <div class="live-tools-audio">
          <img alt="" v-show="isMic" src="src/assets/media/mic_on.svg"/>
          <img alt="" v-show="!isMic" src="src/assets/media/mic_off.svg"/>
          <el-tooltip
              content="麦克风"
              effect="light">
            <el-switch
                v-model="isMic"
                @change="switchMic"
                size="small"
            />
          </el-tooltip>

        </div>
        <div class="live-tools-screen">
          <img alt="" src="src/assets/media/share.svg"/>
          <el-tooltip
              content="屏幕共享"
              effect="light">
            <el-switch
                v-model="isScreen"
                @change="switchScreen"
                size="small"
            />
          </el-tooltip>
        </div>
        <el-tooltip
            content="结束上课"
            effect="light">
          <div @click="exitRoom" class="exit">
            <img alt="" src="src/assets/media/close.svg"/>
          </div>
        </el-tooltip>
      </div>
    </div>
    <div class="main">
      <div class="live-screen">
        <iframe :src="whiteboardUrl" style="height: 100%;width: 100%;border: none">
          该网站使用了框架技术，但是您的浏览器不支持框架，请升级您的浏览器以便正常访问。
        </iframe>
      </div>

      <div class="live-side">
        <div style="width: 350px;height: 250px;">
          <video ref="cameraVideo" autoplay muted style="width: 100%;height: 100%;" v-show="isVideo"></video>
          <div v-show="!isVideo" class="no-camera">
            <img :src="user.avatar"
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
                :is-admin="true"
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
import {onMounted, onUnmounted, reactive, ref} from 'vue'
import {ElMessage, ElMessageBox, TabsPaneContext} from 'element-plus'
import {useRoute} from "vue-router";
import router from "~/router";
import ToolClassStu from "~/components/live/tool/tool-class-stu.vue";
import LiveUsers from "~/components/live/user/live-users.vue";
import localCache from "~/utils/cache";
import {parseWebRTCPublishUrl, SrsPublishParam} from "~/utils/webrtcPublishUtil";
import {closeMediaStream} from "~/utils/mediaStreamUtil";
import {getToken} from "~/utils/auth";
import {destroyWs, sendMsg, startWs} from "~/utils/webSocket";
import {ChatRespMessage, genCommandSendMessage, WsMessage} from "~/utils/wsMessageUtil";
import {getData} from "~/api/main";

const activeName = ref('first')
const {query} = useRoute();
const courseId = query?.courseId
const courseCode: any = query?.courseCode
const roomId: any = query?.roomId
// 课程信息
const course = localCache.getCache(courseCode.toString())
// 设备信息
const deviceSettings = localCache.getCache('liveSettings')
// 直播信息
const liveInfo = localCache.getCache(roomId.toString())
// 用户信息
const user = localCache.getCache("userInfo")
const backgroundImage = `url(${user.avatar})`
const whiteboardUrl = ref("https://excalidraw.com")

onMounted(() => {
  // whiteboardUrl.value = liveInfo.whiteboardUrl


  // live
  // videoAndMic()
  // ws
  startWs(options)

  startLive()

  isVideo.value = true
  isMic.value = true
  videoAndMic()
})


onUnmounted(() => {
  destroyWs()
})

const startLive = () => {
  getData("/live/tea/join", {
    roomId: roomId.toString()
  })
}

const userList = ref([])
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

//聊天信息
const chatContentLists: ChatRespMessage[] = reactive([])

let options: any = {
  url: 'ws://localhost:7001/websocket',
  protocols: [getToken()],
  openCb: (res: any) => {
    // ElMessage.success("ws连接成功")
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
    }

  }, // 消息的回调
  errorCb: (res: any) => {

  } // 错误的回调
}

const cameraVideo = ref<HTMLMediaElement>()
const isVideo = ref<Boolean>(false)
const isMic = ref<Boolean>(false)
const isScreen = ref<Boolean>(false)
let userStream: MediaStream
let screenStream: MediaStream
let pc: RTCPeerConnection | null = null
let pc1: RTCPeerConnection | null = null

// 1、开摄像头、麦克风
// 2、开摄像头、关麦克风
// 3、关摄像头、开麦克风
// 4、关摄像头、关麦克风

// 进入 - > 1

const switchVideo = () => {
  if (isVideo.value) {
    ElMessage.success("开启摄像头")
    if (isMic.value) {
      console.log("1、开摄像头、麦克风")
      videoAndMic()
    } else {
      console.log("2、开摄像头、关麦克风")
      videoAndNoMic()
    }
  } else {
    ElMessage.success("关闭摄像头")
    if (isMic.value) {
      console.log("3、关摄像头、开麦克风")
      // noVideoAndMic()
      isMic.value = false
    } else {
      console.log("4、关摄像头、关麦克风")
      noVideoAndNoMic()
    }
  }
}
const switchMic = () => {
  if (isMic.value) {
    ElMessage.success("关闭麦克风")
    if (isVideo.value) {
      console.log("1、开摄像头、麦克风")
      videoAndMic()
    } else {
      console.log("3、关摄像头、开麦克风")
      // noVideoAndMic()
      ElMessage.warning("请先打开摄像头")
      isMic.value = false
    }
  } else {
    ElMessage.success("打开麦克风")
    if (isVideo.value) {
      console.log("2、开摄像头、关麦克风")
      videoAndNoMic()
    } else {
      console.log("4、关摄像头、关麦克风")
      noVideoAndNoMic()
    }
  }
}

const switchScreen = () => {
  if (isScreen.value) {
    ElMessage.success("开启屏幕共享")
    startScreenLive()
  } else {
    ElMessage.success("关闭屏幕共享")
    stopScreenLive()
  }
}

let videoAndMicStatus = genCommandSendMessage(roomId.value, true, false, false)
// 1、开摄像头、麦克风
const videoAndMic = () => {
  stopUserLive()
  const constraints: MediaStreamConstraints = {
    video: {
      width: 350,
      height: 250,
    },
    audio: true
  }
  navigator.mediaDevices.getUserMedia(constraints).then((res) => {
    userStream = res
    cameraVideo.value!.srcObject = userStream
    startUserLive()
  })
}

// 2、开摄像头、关麦克风
const videoAndNoMic = () => {
  stopUserLive()
  const constraints: MediaStreamConstraints = {
    video: {
      width: 350,
      height: 250,
    },
    audio: false
  }
  navigator.mediaDevices.getUserMedia(constraints).then((res) => {
    userStream = res
    cameraVideo.value!.srcObject = userStream
    startUserLive()
  })
}


// 3、关摄像头、开麦克风
const noVideoAndMic = () => {
  stopUserLive()
  const constraints: MediaStreamConstraints = {
    audio: true,
    video: false
  }
  navigator.mediaDevices.getUserMedia(constraints).then((res) => {
    userStream = res
    startUserLive()
  })
}

// 4、关摄像头、关麦克风
const noVideoAndNoMic = () => {
  stopUserLive()
  videoAndMicStatus = genCommandSendMessage(roomId.toString(), isVideo.value.valueOf() || isMic.value.valueOf(), isScreen.value.valueOf(), false)
  sendMsg(videoAndMicStatus)
}


const stopUserLive = () => {
  //销毁流
  closeMediaStream(userStream)
  cameraVideo.value!.srcObject = null
  //销毁peer connect
  pc?.close()
  pc = null
}

const startUserLive = async () => {
  try {
    pc = new RTCPeerConnection()
    pc.addTransceiver('audio', {direction: 'sendonly'})
    pc.addTransceiver('video', {direction: 'sendonly'})

    userStream.getTracks().forEach((track) => {
      pc?.addTrack(track, userStream)
    })


    const offer = await pc?.createOffer()
    await pc?.setLocalDescription(offer!)

    const srsPublishParam: SrsPublishParam = parseWebRTCPublishUrl(liveInfo.userVideoPublish + "?autostart=true")
    const sdpInfo = JSON.stringify({
      api: srsPublishParam.api,
      streamurl: srsPublishParam.streamurl,
      clientip: null,
      sdp: offer?.sdp,
    })

    const response = await fetch(srsPublishParam.sdpUrl, {
      method: 'POST',
      body: sdpInfo,
      headers: {
        'Content-Type': 'application/json',
      },
    })

    const answer = await response.json()

    console.log("--------------answer----------------")
    console.log(answer)
    console.log("------------------------------------")

    await pc?.setRemoteDescription(new RTCSessionDescription({type: 'answer', sdp: answer.sdp}))
    ElMessage.success("开启摄像头推流成功")
  } catch (err) {
    ElMessage.error("网络连接失败,请检查网络")
    console.error('Error starting screen share:', err)
  }
  videoAndMicStatus = genCommandSendMessage(roomId.toString(), isVideo.value.valueOf() || isMic.value.valueOf(), isScreen.value.valueOf(), false)
  sendMsg(videoAndMicStatus)
}

const stopScreenLive = () => {
  //销毁流
  closeMediaStream(screenStream)
  //销毁peer connect
  pc1?.close()
  pc1 = null
  videoAndMicStatus = genCommandSendMessage(roomId.toString(), isVideo.value.valueOf() || isMic.value.valueOf(), isScreen.value.valueOf(), false)
  sendMsg(videoAndMicStatus)
}
const startScreenLive = async () => {
  const constraints = {
    audio: {
      echoCancellation: true,
      noiseSuppression: true,
      sampleRate: 44100
    },
    video: {
      width: 1920,
      height: 1080,
      frameRate: 30,
      cursor: "always"
    }
  };
  try {
    pc1 = new RTCPeerConnection()
    pc1.addTransceiver('audio', {direction: 'sendonly'})
    pc1.addTransceiver('video', {direction: 'sendonly'})

    screenStream = await navigator.mediaDevices.getDisplayMedia(constraints)
    screenStream.getTracks().forEach((track) => {
      pc1?.addTrack(track, screenStream)
    })

    const offer = await pc1?.createOffer()
    await pc1?.setLocalDescription(offer!)

    const srsPublishParam: SrsPublishParam = parseWebRTCPublishUrl(liveInfo.screenVideoPublish + "?autostart=true")
    const sdpInfo = JSON.stringify({
      api: srsPublishParam.api,
      streamurl: srsPublishParam.streamurl,
      clientip: null,
      sdp: offer?.sdp,
    })
    const response = await fetch(srsPublishParam.sdpUrl, {
      method: 'POST',
      body: sdpInfo,
      headers: {
        'Content-Type': 'application/json',
      },
    })
    const answer = await response.json()
    console.log("--------------answer----------------")
    console.log(answer)
    console.log("------------------------------------")
    await pc1?.setRemoteDescription(new RTCSessionDescription({type: 'answer', sdp: answer.sdp}))
    ElMessage.success("开启屏幕推流成功")
  } catch (err) {
    isScreen.value = false
    ElMessage.warning("开启屏幕分享失败")
    console.error('Error starting screen share:', err)
  }
  videoAndMicStatus = genCommandSendMessage(roomId.toString(), isVideo.value.valueOf() || isMic.value.valueOf(), isScreen.value.valueOf(), false)
  sendMsg(videoAndMicStatus)
}

const informStop = async () => {
  videoAndMicStatus = genCommandSendMessage(roomId.toString(), false, false, true)
  sendMsg(videoAndMicStatus)
}

const exitRoom = () => {
  ElMessageBox.confirm(
      '确认结束？',
      '结束上课',
      {
        confirmButtonText: '退出',
        cancelButtonText: '取消',
        type: 'warning',
        autofocus: false,

      })
      .then(() => {
        // 1、通知其他人课程已经结束
        informStop()
        // 2、结束上课
        ElMessage.success("课程结束")
        getData("live/tea/stop", {
          roomId: roomId.toString()
        })

        try {
          //销毁流
          closeMediaStream(userStream)
          closeMediaStream(screenStream)
          cameraVideo.value!.srcObject = null
          //销毁peer connect
          pc?.close()
          pc1?.close()
        }catch (e) {
          console.log(e)
        }
        // 3、退出直播间
        router.push({
          path: "/course/info/admin",
          query: {
            courseId: courseId,
            courseCode: courseCode
          }
        })
      })
      .catch(() => {
      })
}


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

.live-tools {
  height: 30px;
  width: 350px;
  display: flex;
  justify-content: space-around;
  align-items: center;
}

.live-tools img {
  height: 26px;
  width: 26px;
  margin-right: 5px;
}

.live-tools-video {
  display: flex;
  align-items: center;
}

.live-tools-audio {
  display: flex;
  align-items: center;
}

.live-tools-screen {
  display: flex;
  align-items: center;
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
}

.live-side {
  width: 350px;
  height: 100%;
}

.chat-tabs {
  width: 330px;
  height: calc(100vh - 320px);
  margin: 10px;
}

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

</style>

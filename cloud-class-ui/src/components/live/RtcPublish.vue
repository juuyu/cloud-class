<template>

  <div>
    <h2>Room</h2>
    <el-input maxlength="32" placeholder="请输入" type="text"/>
    <el-button type="primary" @click="startScreenShare">
      推流
    </el-button>

  </div>


  <div>
    <video class="remote" ref="remote" autoplay></video>
    <video class="local" ref="local" autoplay></video>
  </div>

</template>

<script lang="ts">
import {ref} from 'vue'
import {ElMessage} from 'element-plus'


//初始化录制
const captureScreen = (callback: any) => {
  if (navigator.mediaDevices.getDisplayMedia) {
    //录制结束,文件下载
    //getDisplayMedia 开启屏幕共享

    // let id=navigator.mediaDevices.enumerateDevices()
    navigator.mediaDevices.getDisplayMedia({
      video: true, audio: true
    }).then((screenStream: any) => {
      // screenStream 媒体流
      if (!screenStream.getAudioTracks()[0]) {
        // callback(screenStream);
        // stopR()
        ElMessage('请开启系统声音')
      } else {
        // callback(screenStream);
      }
    }).catch(function (error: any) {
      // console.log('error', error);
      ElMessage('请开启当前共享屏幕')
    });
  } else {
    ElMessage('当前浏览器不支持录屏，请切换浏览器使用')
  }
}


const constraints = {
  audio: {
    echoCancellation: true,
    noiseSuppression: true,
    sampleRate: 44100
  },
  video: {
    width: {max: 1280},
    height: {max: 720},
    frameRate: {idea: 25, max: 30},
    cursor: "always"
  }


};


async function startCapture() {
  let captureStream = null;

  try {
    captureStream = await navigator.mediaDevices.getDisplayMedia(constraints);
  } catch (err) {
    console.error("Error: " + err);
  }
  return captureStream;
}

export default {
  name: 'App',
  setup() {
    let pc: RTCPeerConnection | null = null
    let pc1: RTCPeerConnection | null = null
    // let screenStream: MediaStream | null = null
    let srsSession: any = null // you can define an interface for this object
    let srsSession1: any = null // you can define an interface for this object

    const isScreenSharing = ref(false)

    const startScreenShare = async () => {
      try {
        pc = new RTCPeerConnection()
        pc.addTransceiver('audio', {direction: 'sendonly'})
        pc.addTransceiver('video', {direction: 'sendonly'})

        pc1 = new RTCPeerConnection()
        pc1.addTransceiver('audio', {direction: 'sendonly'})
        pc1.addTransceiver('video', {direction: 'sendonly'})


        let screenStream = await navigator.mediaDevices.getDisplayMedia(constraints)
        screenStream.getTracks().forEach((track) => {
          pc?.addTrack(track, screenStream)
          //pc?.addTrack(track)
        })

        let userStream = await navigator.mediaDevices.getUserMedia({
          video: true,
          audio: true
        })
        userStream.getTracks().forEach((track) => {
          pc1?.addTrack(track, userStream)
        })


        const offer = await pc?.createOffer()
        await pc?.setLocalDescription(offer!)

        const response = await fetch('http://localhost:1985/rtc/v1/publish/', {
          method: 'POST',
          body: JSON.stringify({
            api: 'http://localhost',
            streamurl: 'live/livestream',
            clientip: null,
            sdp: offer?.sdp,
          }),
          headers: {
            'Content-Type': 'application/json',
          },
        })

        const answer = await response.json()
        await pc?.setRemoteDescription(new RTCSessionDescription({type: 'answer', sdp: answer.sdp}))

        // Store the SRS session object so we can close the connection later if necessary
        srsSession = answer



        const offer1 = await pc1?.createOffer()
        await pc1?.setLocalDescription(offer1!)
        const response1 = await fetch('http://localhost:1985/rtc/v1/publish/', {
          method: 'POST',
          body: JSON.stringify({
            api: 'http://localhost',
            streamurl: 'live/livestream1',
            clientip: null,
            sdp: offer1?.sdp,
          }),
          headers: {
            'Content-Type': 'application/json',
          },
        })
        const answer1 = await response1.json()
        await pc1?.setRemoteDescription(new RTCSessionDescription({type: 'answer', sdp: answer1.sdp}))
        srsSession1 = answer1



        // Notify that we are screen sharing
        isScreenSharing.value = true
      } catch (err) {
        console.error('Error starting screen share:', err)
        // stopScreenShare()
      }
    }

    // const stopScreenShare = () => {
    //   pc?.close()
    //   screenStream?.getTracks().forEach((track) => {
    //     track.stop()
    //   })
    //   isScreenSharing.value = false
    // }

    return {
      startScreenShare,
      //  stopScreenShare,
      isScreenSharing,
    }
  },
}
</script>


<style scoped>
.video {
  width: 50%;
  height: 100%;
}
</style>

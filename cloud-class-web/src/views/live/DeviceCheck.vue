<template>
  <div class="main">
    <div class="device">
      <div class="device-header">
        <span>设备设置</span>
      </div>
      <div class="device-component">
        <div class="device-video">
          <div style="width: 100%;height: 30px;">
            摄像头
          </div>
          <div>
            <el-select v-model="selectedVideoDevice" style="width: 300px;height: 30px;"
                       @change="handleChangeVideoDevice">
              <el-option v-for="device in videoDevices" :value="device.deviceId" :label="device.label"/>
            </el-select>
          </div>
          <div
              style="width: 300px;height: 170px;background-color: #F9F9FC;display: flex;align-items: center;justify-content: center;">
            <video ref="cameraVideo" autoplay style="width: 100%;height: 100%;" v-show="isVideo"></video>
            <img v-show="!isVideo" src="src/assets/media/camera_close.svg" alt="" style="width: 90px"/>
          </div>
        </div>
        <div class="device-audio">
          <div class="microphone">
            <div style="float: left;width: 100%;height: 30px;">
              麦克风
            </div>
            <div>
              <el-select v-model="selectedAudioInputDevice" style="width: 300px;height: 30px;"
                         @change="handleChangeAudioInputDevice">
                <el-option v-for="device in audioInputDevices" :value="device.deviceId" :label="device.label"/>
              </el-select>
            </div>
            <div
                style="margin-top: 20px ;display: flex;align-items:center;width: 300px;height: 30px;background-color: #FFFFFF;">
              <img src="src/assets/media/microphone.svg" alt="" style="width: 30px"/>
              <div v-for="(index) in 50"
                   :style="{ backgroundColor: index * 2 <= audioLevel ? '#0E83FE' : '#e5e6eb', width: '4px', height: '12px', marginLeft: '2px' }">
              </div>
            </div>
          </div>
          <div class="loudspeaker">
            <div style="width: 100%;height: 30px;">
              扬声器
            </div>
            <div>
              <el-select v-model="selectedAudioOutputDevice" style="width: 300px;height: 30px;"
                         @change="handleChangeAudioOutputDevice">
                <el-option v-for="device in audioOutputDevices" :value="device.deviceId" :label="device.label"/>
              </el-select>
            </div>
            <!--            <div style="margin-top: 20px ;display: flex;align-items:center;">-->
            <!--              <el-button style="width: 300px">-->
            <!--                <template #default>-->
            <!--                  <div class="loudspeaker-test">-->
            <!--                    <div style="line-height: 30px;margin-left: 10px">测试扬声器</div>-->
            <!--                    <img src="src/assets/media/play.svg" alt="" style="width: 28px;margin-right: 5px"/>-->
            <!--                  </div>-->
            <!--                </template>-->
            <!--              </el-button>-->
            <!--            </div>-->
          </div>
        </div>
      </div>
      <div class="device-bottom">
        <el-button type="primary" round @click="startLive">完成</el-button>
      </div>
    </div>
  </div>
</template>
<script lang="ts" setup>
import {ref, reactive, onMounted, onUnmounted} from 'vue';
import {ElMessage} from 'element-plus';
import SoundMeter from "../.././utils/soundmeter";
import {onBeforeRouteLeave, useRoute, useRouter} from 'vue-router';
import {getDeviceById, getTrueDeviceId} from "~/utils/mediaDeviceUtil";
import router from "~/router";
import {closeMediaStream} from "~/utils/mediaStreamUtil";
import localCache from "~/utils/cache";
const {query} = useRoute();
const courseId = query?.courseId
const courseCode = query?.courseCode
const roomId = query?.roomId

const deviceSettings = reactive({
  videoDeviceId: "",
  audioInputDeviceId: "",
  audioOutputDeviceId: ""
})

const startLive = () => {
  // 获取videoId
  const videoDevice: MediaDeviceInfo | undefined = getDeviceById(videoDevices.value, selectedVideoDevice.value)
  if (videoDevice) {
    deviceSettings.videoDeviceId = getTrueDeviceId(videoDevices.value, videoDevice)
  }
  // 获取audioInputDeviceId
  const audioInputDevice: MediaDeviceInfo | undefined = getDeviceById(audioInputDevices.value, selectedAudioInputDevice.value)
  if (audioInputDevice) {
    deviceSettings.audioInputDeviceId = getTrueDeviceId(audioInputDevices.value, audioInputDevice)
  }
  console.log("deviceSettings =>",deviceSettings)
  // 保存设置
  localCache.setCache('liveSettings', deviceSettings)
  // 销毁所有流
  closeDevice()
  router.push({
    path: "/live/tea/class",
    query: {
      courseId: courseId,
      courseCode: courseCode,
      roomId: roomId
    }
  })
}


onMounted(() => {
  getMediaDevices()
})

onUnmounted(() => {
  closeDevice()
})


const audioInputDevices = ref<MediaDeviceInfo[]>([]);
const audioOutputDevices = ref<MediaDeviceInfo[]>([]);
const videoDevices = ref<MediaDeviceInfo[]>([]);

const selectedAudioInputDevice = ref<string>("");
const selectedAudioOutputDevice = ref<string>("");
const selectedVideoDevice = ref<string>("");

// 获取当前计算机可列举的音频输入|输出|视频输入设备
const getMediaDevices = () => {
  navigator.mediaDevices
      .enumerateDevices()
      .then((devices) => {
        for (const device of devices) {
          if (device.kind === "audioinput") {
            audioInputDevices.value.push(device);
          } else if (device.kind === "audiooutput") {
            audioOutputDevices.value.push(device);
          } else if (device.kind === "videoinput") {
            videoDevices.value.push(device);
          }
        }
        const aid = audioInputDevices.value
        const aod = audioOutputDevices.value
        const vid = videoDevices.value
        if (aid.length !== 0) {
          selectedAudioInputDevice.value = aid[0].deviceId;
          // 打开麦克风
          console.log("open default microphone", aid[0])
          openMicrophone(aid[0])
        } else {
          ElMessage.error("未发现麦克风")
        }

        if (aod.length !== 0) {
          selectedAudioOutputDevice.value = aod[0].deviceId;
        } else {
          ElMessage.error("未发现扬声器")
        }

        if (vid.length !== 0) {
          selectedVideoDevice.value = vid[0].deviceId;
          // 打开摄像头
          console.log("open default camera", vid[0])
          openVideo(vid[0])
        } else {
          ElMessage.error("未发现摄像头")
        }
      })
  // .catch((err) => {
  //   ElMessage.error(`获取用户设备数据错误：${err.name}`);
  // })
}


const cameraVideo = ref<HTMLMediaElement>();
const isVideo = ref<Boolean>(false);
let videoStream: MediaStream | void
// 打开摄像头
const openVideo = async (video: MediaDeviceInfo) => {
  const deviceId = getTrueDeviceId(videoDevices.value, video)
  const constraints: MediaStreamConstraints = {
    video: {
      deviceId: deviceId,
      width: 300,
      height: 170,
    }
  }
  navigator.mediaDevices
      .getUserMedia(constraints)
      .then((stream) => {
        videoStream = stream
        cameraVideo.value!.srcObject = stream
        isVideo.value = true
      })
      .catch((error) => {
        ElMessage.error(`获取用户媒体数据错误：${error.name}`);
        isVideo.value = false
      })
}


// 切换摄像头
const handleChangeVideoDevice = (deviceId: string) => {
  selectedVideoDevice.value = deviceId;
  const device: MediaDeviceInfo | undefined = getDeviceById(videoDevices.value, deviceId)
  if (device) {
    cameraVideo.value!.srcObject = null
    if (videoStream) {
      closeMediaStream(videoStream)
    }
    console.log("switch camera", device)
    openVideo(device)
  }
}

const audioLevel = ref<number>(0);
const soundMeter = ref<SoundMeter>();
let audioStream: MediaStream | void

// 打开麦克风
const openMicrophone = async (audio: MediaDeviceInfo) => {
  const deviceId = getTrueDeviceId(audioInputDevices.value, audio)
  soundMeter.value = new SoundMeter(
      new window.AudioContext(),
      (instant: number) => {
        audioLevel.value = Number(instant.toFixed(2)) * 348 + 1;
      }
  );
  const constraints: MediaStreamConstraints = {
    audio: {
      deviceId: deviceId
    }
  };
  navigator.mediaDevices
      .getUserMedia(constraints)
      .then((stream: MediaStream) => {
        audioStream = stream
        soundMeter.value?.connectToSource(stream);
      })
      .catch((error) => {
        ElMessage.error(`获取用户媒体数据错误：${error.name}`)
        isVideo.value = false
      })
};

// 切换麦克风
const handleChangeAudioInputDevice = (deviceId: string) => {
  selectedAudioInputDevice.value = deviceId;
  const device: MediaDeviceInfo | undefined = getDeviceById(audioInputDevices.value, deviceId)
  console.log(device)
  if (device) {
    soundMeter.value?.stop();
    if (audioStream) {
      closeMediaStream(audioStream)
    }
    console.log("switch microphone", device)
    openMicrophone(device)
  }
}

const handleChangeAudioOutputDevice = (value: string) => {
  selectedAudioOutputDevice.value = value;
  // todo
}
const closeDevice = () => {
  isVideo.value = false
  soundMeter.value?.stop()
  if (videoStream) {
    console.log("销毁video流")
    closeMediaStream(videoStream)
  }
  if (audioStream) {
    console.log("销毁audio流")
    closeMediaStream(audioStream)
  }
}

</script>
<style scoped>
.main {
  height: 100vh;
  background-color: #C7C7C9;
}

.device {
  width: 700px;
  height: 400px;
  margin: auto;
  border-radius: 14px;
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #FFFFFF;
}

.device-header {
  height: 25px;
  width: 100%;
  border-radius: 14px 14px 0 0;
  background-color: #F7F7FA;
}

.device-header span {
  margin-left: 10px;
  font-size: 14px;
  line-height: 25px;
}

.device-component {
  width: 100%;
  height: 250px;
  margin-top: 30px;
  display: flex;
  justify-content: space-around;
}

.device-audio {
  height: 100%;
}

.microphone {
  height: 50%;
}

.loudspeaker-test {
  width: 300px;
  display: flex;
  justify-content: space-between;
}


.device-bottom {
  width: 100%;
  height: 40px;
  margin-top: 30px;
  display: flex;
  justify-content: center;
}
</style>
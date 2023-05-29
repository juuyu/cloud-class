<template>
  <el-container>
    <el-header>
      <header-base
          style="margin: auto;width: 90%;height: 60px;  position: fixed;z-index: 10; transform: translate(-50%,0);left:50%"/>
    </el-header>
    <el-main style="margin: 30px auto;width: 65%">
      <el-page-header
          :icon="ArrowLeft"
          @back="goBack"
          style="margin-bottom: 50px;"
      >
        <template #content>
          <span class="text-large font-600 mr-3"> 录制视频 </span>
        </template>
      </el-page-header>
      <div style="width: 100%;height: 50px">
        <div style="float: left">
          <el-form :inline="true" class="demo-form-inline">
            <el-form-item label="设备">
              <el-select
                  v-model="recordOptions.device"
                  @change="choseDevice"
                  :disabled="optionsDisable"
                  style="width: 100px"
              >
                <el-option label="未选择" value="cancel"/>
                <el-option label="屏幕" value="screen"/>
                <el-option label="摄像头" value="user"/>
              </el-select>
            </el-form-item>
            <el-form-item label="分辨率">
              <el-select
                  v-model="recordOptions.resolution"
                  :disabled="optionsDisable"
                  style="width: 100px">
                <el-option label="720p" value="720p"/>
                <el-option label="1080p" value="1080p"/>
              </el-select>
            </el-form-item>
            <el-form-item label="帧率">
              <el-select
                  v-model="recordOptions.frameRate"
                  :disabled="optionsDisable"
                  style="width: 100px">
                <el-option label="15fps" value="15"/>
                <el-option label="30fps" value="30"/>
                <el-option label="60fps" value="60"/>
              </el-select>
            </el-form-item>
            <!--            <el-form-item label="麦克风">-->
            <!--              <el-switch-->
            <!--                  v-model="recordOptions.mic"-->
            <!--              />-->
            <!--            </el-form-item>-->
            <el-form-item label="云录制">
              <el-switch
                  v-model="recordOptions.cloud"
                  :disabled="optionsDisable"
              />
            </el-form-item>
          </el-form>
        </div>
        <div style="float: right">
          <el-button
              @click="stop"
              type="danger"
              v-if="recording"
          >停止录制
          </el-button>
          <el-button
              @click="start"
              type="primary"
              v-else
          >开始录制
          </el-button>
        </div>
      </div>
      <div style="width: 900px;height: 600px;border-radius: 12px;background-color: #A8ABB2"
           v-loading="videoLoading"
      >
        <video
            v-show="showVideo"
            ref="videoRef"
            autoplay
            loop
            muted
            controls
            style="height: 100%;width: 100%"
        ></video>
      </div>
      <div style="width: 100%;height: 50px;margin-top: 30px" v-show="!recordOptions.cloud">
        <el-button
            @click="saveRecord"
            :disabled="optionsDisable"
            type="success"
            :icon="Download"
        >保存
        </el-button>
<!--        <el-button-->
<!--            @click="toEditor"-->
<!--            :disabled="optionsDisable"-->
<!--            type="warning"-->
<!--            :icon="Scissor"-->
<!--        >编辑-->
<!--        </el-button>-->
      </div>
      <div style="width: 100%;height: 50px;margin-top: 30px" v-show="recordOptions.cloud">
      </div>

    </el-main>
  </el-container>
</template>
<script lang="ts" setup>
import {reactive, ref} from "vue";
import ysFixWebmDuration from 'fix-webm-duration'
import RecordRTC, {invokeSaveAsDialog} from "recordrtc";
import {ArrowLeft, Download, Scissor} from "@element-plus/icons-vue";
import router from "~/router";
import {getData, postData} from "~/api/main";
import localCache from "~/utils/cache";
import {ElMessage, ElMessageBox} from "element-plus";
import {closeMediaStream} from "~/utils/mediaStreamUtil";
import pinia from "~/store";
/* ----------------------------------------------------------------------------------------------------------------- */
const goBack = () => {
  router.back()
}
/* ----------------------------------------------------------------------------------------------------------------- */
const recordOptions = reactive({
  device: "cancel",
  mic: false,
  cloud: true,
  resolution: "1080p",
  frameRate: 30,
})
const optionsDisable = ref(false)
const choseDevice = (val: string) => {
  if (val === "cancel") {
    closeMediaStream(recordStream)
    showVideo.value = false
    videoRef.value!.srcObject = null
    return
  }
  if (val === "screen") {
    closeMediaStream(recordStream)
    captureScreen()
    return
  }
  if (val === "user") {
    closeMediaStream(recordStream)
    captureUser()
    return;
  }
}
/* ----------------------------------------------------------------------------------------------------------------- */
let recordStream: MediaStream             // 视频流
const videoLoading = ref(false)     // 视频加载中
// 捕捉屏幕流
const captureScreen = async () => {
  videoLoading.value = true
  try {
    const constraints = {
      audio: {
        echoCancellation: true,
        noiseSuppression: true,
        sampleRate: 44100
      },
      video: {
        width: 1920,
        height: 1080,
        frameRate: recordOptions.frameRate,
        displaySurface: 'monitor', // monitor, window, application, browser
        logicalSurface: true,
        cursor: 'always' // never, always, motion
      }
    }
    // 获取屏幕流
    recordStream = await navigator.mediaDevices.getDisplayMedia(constraints)
    videoRef.value!.srcObject = recordStream
    showVideo.value = true
  } catch (err) {
    recordOptions.device = "cancel"
    showVideo.value = false
    videoRef.value!.srcObject = null
    console.error('Error starting screen share:', err)
    ElMessage.warning("开启屏幕分享失败")
  }
  videoLoading.value = false
}
// 捕捉视频流
const captureUser = async () => {
  videoLoading.value = true
  try {
    const constraints: MediaStreamConstraints = {
      video: true,
      audio: true
    }
    recordStream = await navigator.mediaDevices.getUserMedia(constraints)
    videoRef.value!.srcObject = recordStream
    showVideo.value = true
  } catch (err) {
    recordOptions.device = "cancel"
    showVideo.value = false
    videoRef.value!.srcObject = null
    console.error('Error starting user video:', err)
    ElMessage.warning("开启摄像头失败")
  }
  videoLoading.value = false
}
/* ----------------------------------------------------------------------------------------------------------------- */
// 开始录制
const start = () => {
  if (recordOptions.device === "cancel") {
    ElMessage.warning("请选择录制设备")
    return
  }
  if (!showVideo.value) {
    ElMessage.warning("请先开启录制设备")
    return
  }
  optionsDisable.value = true
  localBlob = null
  if (recordOptions.cloud) {
    startCloud()
  } else {
    startRecordLocal()
  }
}

// 停止录制
const stop = () => {
  optionsDisable.value = false
  if (recordOptions.cloud) {
    stopCloud()
  } else {
    stopRecordLocal()
  }
}
/* ----------------------------------------------------------------------------------------------------------------- */
const user = localCache.getCache("userInfo")
const videoRef = ref<HTMLVideoElement>()
const showVideo = ref(false)
let recordId: string = ""
// 获取视频封面
const captureFrame = async () => {
  // 睡眠8s
  await new Promise((resolve) => {
    setTimeout(() => {
      resolve('')
    }, 8000)
  })
  // 获取视频元素和画布元素
  const video = videoRef.value;
  const canvas = document.createElement('canvas');
  const ctx = canvas.getContext('2d');
  if (!ctx) {
    return
  }
  // 设置画布大小
  canvas.width = video!.videoWidth;
  canvas.height = video!.videoHeight;

  // 在画布上绘制视频帧
  ctx.drawImage(video!, 0, 0, canvas.width, canvas.height);

  // 将画布内容转换为Blob对象
  canvas.toBlob((blob) => {
    if (blob) {
      const file = new File([blob], 'cover.jpg', {type: 'image/jpeg'});
      let formData = new FormData()
      formData.append('recordId', recordId)
      formData.append('cover', file)
      postData("record/cover", formData).then((res: any) => {
        let {data} = res;
        console.log(data)
      })
    }
  }, 'image/jpeg', 0.95);
};
/* ----------------------------------------------------------------------------------------------------------------- */
// 开始录制
const startCloud = () => {
  getData("record/start", {
    userId: user.id
  }).then((res: any) => {
    let {data} = res
    if (data.code === 200) {
      recordId = data.data
      startRecord()
      captureFrame()
    } else {
      ElMessage.error(data.msg)
    }
  })
}

// 停止录制
const stopCloud = () => {
  ElMessageBox.confirm(
      '确认结束？',
      '停止录制',
      {
        confirmButtonText: '结束',
        cancelButtonText: '取消',
        type: 'warning',
        autofocus: false,
      })
      .then(() => {
        stopRecordTimer()
        recording.value = false
        showVideo.value = false
        ElMessage.success("录制成功")
      })
      .catch(() => {
      })
}
/* ----------------------------------------------------------------------------------------------------------------- */
const recording = ref(false)  // 录制状态
let recorder: any = null
let startTime: any = null
let recordTimer: NodeJS.Timer | null = null
// 开始录制
const startRecord = async () => {
  try {
    recording.value = true
    let options: any = {
      type: 'video',
      mimeType: 'video/webm;codecs=vp8',
      disableLogs: false,
      checkForInactiveTracks: false,
    }
    recorder = new RecordRTC(recordStream, options)
    addStreamStopListener(recordStream, () => {
      stopRecordTimer()
    })
    recordStart()
    //开启计时器
    openRecordTimer()
  } catch (e) {
    recording.value = false
    ElMessage.error("网络错误")
    console.log(e)
  }
}
// 计时器
const openRecordTimer = () => {
  recordTimer = setInterval(() => {
    try {
      recorder.stopRecording(function () {
        let blob = recorder.getBlob();
        fixBlobAndUpload(blob, Date.now() - startTime)
        recordStart()
      })
    } catch (e) {
      console.log(e)
    }
  }, 10 * 1000)
}
const recordStart = () => {
  try {
    recorder.startRecording()
    recorder.screen = screen
    startTime = Date.now()
  } catch (e) {
    console.log(e)
  }
}
// 补时长
const fixBlobAndUpload = async (blob: any, duration: any) => {
  ysFixWebmDuration(blob, duration, (fixBlob: Blob) => {
    console.log("=================补时长成功=================")
    uploadBlob(fixBlob);
  })
}
// 上传blob时间片
const uploadBlob = (blob: any) => {
  let fileObject = new File([blob], recordId + '-' + Date.now() + '.webm', {
    type: 'video/webm'
  })
  let formData = new FormData()
  formData.append('recordId', recordId)
  formData.append('slice', fileObject)
  postData("record/upload", formData).then((res: any) => {
    let {data} = res;
    if (data.code !== 200) {
      ElMessage.error(data.msg)
    }
  })
}
const stopRecordTimer = () => {
  clearInterval(Number(recordTimer));
  try {
    console.log("=================最后一段=================")
    recorder.stopRecording(function () {
      let blob = recorder.getBlob();

      videoRef.value!.srcObject = null
      closeMediaStream(recordStream)

      ysFixWebmDuration(blob, Date.now() - startTime, (fixBlob: Blob) => {
        let fileObject = new File([fixBlob], recordId + '-' + Date.now() + '.webm', {
          type: 'video/webm'
        })
        let formData = new FormData()
        formData.append('recordId', recordId)
        formData.append('slice', fileObject)
        postData("record/upload", formData).then(() => {
          getData("record/stop", {
            recordId: recordId
          })
        })
      })
      recorder = null;
    })
  } catch (e) {
    console.log(e)
  }
}
//停止录制
const stopRecord = () => {
  try {
    recording.value = false
    recorder.stopRecording(function () {
      recorder.screen.stop();
      recorder = null;
      videoRef.value!.srcObject = null
      closeMediaStream(recordStream)
    })
  } catch (e) {
    console.log(e)
  }
}
//流监听
const addStreamStopListener = (stream: any, callback: any) => {
  // ended 媒体停止触发
  stream.addEventListener('ended', function () {
    callback();
    callback = function () {
    };
  }, false);

  stream.addEventListener('inactive', function () {
    callback();
    callback = function () {
    };
  }, false);

  //给每个媒体轨道添加事件
  stream.getTracks().forEach(function (track: any) {
    track.addEventListener('ended', function () {
      callback();
      callback = function () {
      };
    }, false);
    track.addEventListener('inactive', function () {
      callback();
      callback = function () {
      };
    }, false);
  });
}
/* ----------------------------------------------------------------------------------------------------------------- */
const startRecordLocal = async () => {
  try {
    recording.value = true
    let options: any = {
      type: 'video',
      mimeType: 'video/webm;codecs=vp8',
      disableLogs: false,
      checkForInactiveTracks: false,
    }
    recorder = new RecordRTC(recordStream, options)
    // addStreamStopListener(recordStream, () => {
    //   stopRecord()
    // })
    recorder.startRecording()
    recorder.screen = screen
    startTime = Date.now()
  } catch (e) {
    recording.value = false
    ElMessage.error("网络错误")
    console.log(e)
  }
}
let localBlob: any = null
const stopRecordLocal = () => {
  ElMessageBox.confirm(
      '确认结束？',
      '停止录制',
      {
        confirmButtonText: '结束',
        cancelButtonText: '取消',
        type: 'warning',
        autofocus: false,
      })
      .then(() => {
        try {
          recording.value = false
          recorder.stopRecording(function () {
            let blob = recorder.getBlob();
            closeMediaStream(recordStream)
            ysFixWebmDuration(blob, Date.now() - startTime, (fixBlob: Blob) => {
              console.log("=================补时长成功=================")
              console.log(Date.now() - startTime)
              localBlob = fixBlob
              videoRef.value!.srcObject = null
              videoRef.value!.src = URL.createObjectURL(fixBlob);
            })
            recorder.destroy();
            recorder = null;
            ElMessage.success("录制成功")
          })
        } catch (e) {
          console.log(e)
        }
      })
      .catch(() => {
      })
}
const saveRecord = () => {
  if (recording.value) {
    ElMessage.warning("请录制结束后点击保存")
    return
  }
  if (!localBlob) {
    ElMessage.warning("请先录制")
    return
  }
  let fileObject = new File([localBlob], Date.now() + '.webm', {
    type: 'video/webm'
  })
  invokeSaveAsDialog(fileObject, fileObject.name);
}
</script>
<style scoped>

</style>
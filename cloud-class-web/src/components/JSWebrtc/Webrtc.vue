<template>
  <div class="jsWebrtc" ref="jsWebRtc"
  >
    <video :id="videoId" controls>
    </video>
  </div>
</template>

<script>
import {defineComponent, ref, watch} from 'vue'
import {JSWebrtc} from './index'
import {onMounted, onUnmounted} from "vue";

export default defineComponent({
  name: '',
  components: {},
  props: {
    url: {
      type: String,
    },
    videoId: {
      type: String,
    },
    volume: {
      type: Number
    },
    mute: {
      type: Boolean
    },
    rotate: {
      type: Number,
      default: 1,
    }
  },
  emits: ['screenshots', 'errCb', 'thenCb'],
  setup(props, {emit}) {
    const video = ref()
    const jsWebRtc = ref()
    let webrtc
    onMounted(() => {
      video.value = document.getElementById(props.videoId)
      webrtc = new JSWebrtc.Player(props.url, {
            video: video.value,
            autoplay: true,
            errCb: (err) => {
              console.log('err', err)
              emit('errCb', props.url)
            },
            thenCb: (res) => {
              console.log('su', res)
              emit('thenCb', props.url)
            }
          }
      )
      video.value.muted = false
    })
    let timeOut = null

    watch(
        () => props.volume,
        (newValue) => {
          if (timeOut) {
            clearTimeout(timeOut)
            timeOut = null
          }
          timeOut = setTimeout(
              () => {
                video.value.volume = newValue ? newValue / 100 : 0.1
              }, 500
          )
        }
    )
    watch(
        () => props.mute,
        (newValue) => {
          video.value.muted = newValue
        }
    )
    onUnmounted(() => {
      webrtc.destroy()
    })
    return {jsWebRtc}
  },
})
</script>
<style scoped>

video {
  width: 100%;
  height: 100%;
}

.jsWebrtc {
  position: relative;
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: rgba(0, 0, 0, 1);
}

</style>

<template>
  <el-button
      :type="countdownBtn.type"
      :loading="countdownBtn.loading"
      :disabled="countdownBtn.disabled"
  >
    {{ countdownBtn.title }}
  </el-button>
</template>
<script lang="ts" setup>
import {reactive, toRefs} from 'vue'

interface Props {
  // 样式
  type: "primary" | "success" | "warning" | "danger" | "info" | "text"
  // 文字
  title: string
  // 倒计时时间
  sec: number

}

const props = defineProps<Props>()
const {type, title, sec} = toRefs(props)

interface CountdownBtn {
  type: "primary" | "success" | "warning" | "danger" | "info" | "text"
  title: string
  duration: number
  loading: boolean
  disabled: boolean
  timer: NodeJS.Timer | null
}

let countdownBtn = reactive<CountdownBtn>({
  type: type.value,
  title: title.value,
  duration: sec.value,
  loading: false,
  disabled: false,
  timer: null
})

const clickBtn = () => {
  const tmp = countdownBtn.duration--
  countdownBtn.title = `${tmp}秒`
  if (countdownBtn.duration !== sec.value) {
    countdownBtn.disabled = true
  }
  // 清除掉定时器
  countdownBtn.timer && clearInterval(countdownBtn.timer)

  countdownBtn.timer = setInterval(() => {
    countdownBtn.disabled = true
    const tmp = countdownBtn.duration--
    countdownBtn.title = `${tmp}秒`
    if (tmp <= 0) {
      countdownBtn.timer && clearInterval(countdownBtn.timer)
      countdownBtn.duration = sec.value
      countdownBtn.title = title.value
      countdownBtn.disabled = false
    }

  }, 1000)
}
defineExpose({
  clickBtn
})
</script>

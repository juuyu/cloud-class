<template>
  <div v-loading="playbackListLoading">
    <el-empty description="暂无课程回放" v-show="dataList.length === 0"/>
    <el-scrollbar height="600px" v-show="dataList.length !== 0"
                  style="border: 1px solid #dcdfe6;border-radius: 5px;padding: 20px;">
      <el-timeline v-show="dataList.length !== 0">
        <el-timeline-item
            v-for="item in dataList"
            :timestamp="item?.uploadTime"
            placement="top"
        >
          <el-card>
            <div class="video-info flex items-center">
              <div class="flex items-center">
                <div @click="play(item?.playbackFileLink)" style="cursor: pointer">
                  <img alt="" class="video-cover" :src="item?.cover"/>
                </div>
                <div>
                  <div class="video-name">
                    {{ item?.playbackName }}
                  </div>
                  <div class="video-content">
                    <span>{{ item?.uploadUserName }}</span>
                    <span>{{ item?.uploadTime }}</span>
                  </div>
                </div>
              </div>
              <div class="flex items-center" style="justify-content: space-between;">
                <el-tooltip
                    content="分享"
                    effect="light">
                  <div style="width: 50px;cursor: pointer" @click="copyLink(item?.playbackFileLink)">
                    <img alt="" src="src/assets/tools/share.svg" style="width: 30px;height: 30px"/>
                  </div>
                </el-tooltip>
                <el-tooltip
                    content="删除"
                    effect="light">
                  <div style="width: 50px;cursor: pointer" @click="deletePlayback(item?.id)">
                    <img alt="" src="src/assets/tools/delete.svg" style="width: 30px;height: 30px"/>
                  </div>
                </el-tooltip>
              </div>
            </div>
          </el-card>
        </el-timeline-item>
      </el-timeline>
    </el-scrollbar>
  </div>
  <el-dialog
      v-model="dialogFormVisible"
      @close="close"
      align-center
      width="80%"
  >
    <div v-if="isHls">
      <vue3-video-play
          v-bind="options"
      />
    </div>
    <video
        v-else
        :src="videoUrl"
        controls
        style="width: 100%"
    ></video>
  </el-dialog>
</template>
<script lang="ts" setup>
import {reactive, ref, toRefs} from "vue";
import {ElMessage, ElMessageBox} from "element-plus";
import {getData} from "~/api/main";

interface Props {
  dataList: Array<any>
  playbackListLoading: boolean
}

const props = defineProps<Props>()
const emit = defineEmits(['getCoursePlayBack'])


const {dataList, playbackListLoading} = toRefs(props)

/*-------------------------------------------------------------------------------------------------------------------*/

// copy code
const copyLink = async (link: string) => {
  await navigator.clipboard.writeText(link)
  ElMessage.success("复制成功")
}
/*-------------------------------------------------------------------------------------------------------------------*/

const deletePlayback = (id: any) => {
  ElMessageBox.confirm(
      '确认删除？',
      '删除回放',
      {
        confirmButtonText: '删除',
        cancelButtonText: '取消',
        type: 'warning',
        autofocus: false,
      })
      .then(() => {
        console.log(id)
        getData("course/playback/del", {
          id: id
        }).then((res: any) => {
          let {data} = res;
          console.log(data)
          if (data.code === 200) {
            ElMessage.success("删除成功")
            emit('getCoursePlayBack')
          } else {
            ElMessage.error(data.msg)
          }
        })
      })
      .catch(() => {
      })
}

/*-------------------------------------------------------------------------------------------------------------------*/
const dialogFormVisible = ref(false)
const videoUrl = ref('')
const isHls = ref(false)
const options = reactive({
  width: '100%',
  control: true,
  src: '',
  type: 'm3u8',
})
const play = (url: string) => {
  console.log(url)
  videoUrl.value = url
  let urlType = ""
  try {
    urlType = url.substring(url.lastIndexOf('.') + 1)
  } catch (e) {
    console.log(e)
  }
  if (urlType === "m3u8") {
    options.src = videoUrl.value
    isHls.value = true
    console.log("m3u8类型")
  } else {
    isHls.value = false
  }
  dialogFormVisible.value = true
}

const close = () => {
  videoUrl.value = ""
  options.src = videoUrl.value
}
</script>
<style scoped>
.video-info {
  border-radius: 12px;
  width: 100%;
  justify-content: space-between;
}


.video-cover {
  width: 160px;
  height: 100px;
  margin: 10px;
  border-radius: 12px;
  border: 1px solid #dcdfe6;
}

.file-img img {
  height: 30px;
  width: 30px;
  margin: 0 auto
}


.video-name {
  margin-left: 20px;
  font-size: 16px;
  font-weight: bolder;
  color: #303133;
}

.video-content {
  margin-top: 30px;
  line-height: 26px;
  font-size: 13px;
  color: #606266;
}

.video-content span {
  margin-left: 20px;
}
</style>
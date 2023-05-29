<template>
  <el-container>
    <el-header>
      <header-base
          style="margin: auto;width: 90%;height: 60px;position: fixed;z-index: 10; transform: translate(-50%,0);left:50%"/>
    </el-header>
    <el-main style="margin: 30px auto;width: 65%">
      <div style="width: 100%;">
        <div style="float: left">
          <el-form :inline="true" class="demo-form-inline">
            <el-form-item label="视频名">
              <el-input
                  placeholder="搜索视频"
                  v-model="searchData.videoName"
              />
            </el-form-item>
            <el-form-item label="发布时间">
              <el-select
                  v-model="searchData.recordTime"
                  style="width: 100px">
                <el-option label="全部" value=""/>
                <el-option label="最近三天" value="-3"/>
                <el-option label="最近一周" value="-7"/>
                <el-option label="最近一月" value="-30"/>
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
            :data="recordList"
            style="width: 100%"
            v-loading="getVideoLoading"
        >
          <el-table-column property="videoCoverLink" width="230">
            <template #default="scope">
              <div @click="play(scope.row.videoFileLink)" style="cursor: pointer">
                <img alt="" class="video-cover" :src="scope.row.videoCoverLink"/>
              </div>
            </template>
          </el-table-column>
          <el-table-column width="500">
            <template #default="scope">
              <div>
                <div class="video-name">
                  {{ scope.row.videoName }}
                </div>
                <div class="video-content">
                  <span>{{ scope.row.recordStart }}</span>
                  <span>~</span>
                  <span>{{ scope.row.recordEnd }}</span>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column>
            <template #default="scope">
              {{ scope.row.userRealName }}&nbsp;&nbsp;&nbsp;&lt;{{ scope.row.userName }}&gt;
            </template>
          </el-table-column>
          <el-table-column align="right">
            <template #default="scope">
              <div style="cursor: pointer;margin-right: 30px;"
                   @click="copyLink(scope.row.videoFileLink)">
                <img alt="" src="src/assets/tools/share.svg" style="width: 30px;height: 30px"/>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div style="margin-top: 50px;float: right;">
        <el-pagination
            background
            layout="prev, pager, next"
            :page-size="4"
            :total="total"
            v-model:current-page="pageNum"
            @current-change="getRecordList"
        />
      </div>
    </el-main>
  </el-container>
  <el-dialog
      v-model="dialogFormVisible"
      @close="close"
      align-center
      width="80%"
  >
    <video :src="videoUrl" controls style="width: 100%"></video>
  </el-dialog>
</template>
<script lang="ts" setup>
import {
  ArrowLeft, Search,
} from '@element-plus/icons-vue'
import '@wangeditor/editor/dist/css/style.css' // 引入 css
import localCache from "~/utils/cache";
import {onMounted, reactive, ref} from "vue";
import {getData} from "~/api/main";
import {ElMessage, ElTable} from "element-plus";

const user = localCache.getCache("userInfo")

onMounted(() => {
  getRecordList()
})

/*-------------------------------------------------------------------------------------------------------------------*/
// copy code
const copyLink = async (link: string) => {
  await navigator.clipboard.writeText(link)
  ElMessage.success("复制成功")
}

/*-------------------------------------------------------------------------------------------------------------------*/
const dialogFormVisible = ref(false)
const videoUrl = ref("")
const play = (url: string) => {
  videoUrl.value = url
  dialogFormVisible.value = true

}
const close = () => {
  videoUrl.value = ""
}
/*-------------------------------------------------------------------------------------------------------------------*/
const getVideoLoading = ref(false)
const pageNum = ref(1)
const total = ref(1)
const recordList = ref([])

const message = ref({})
const searchData = reactive({
  videoName: "",
  recordTime: "",
})
const searchMessage = () => {
  pageNum.value = 1
  getRecordList()
}

const getRecordList = () => {
  getVideoLoading.value = true
  getData("open/video", {
    pageSize: 4,
    pageNum: pageNum.value,
    isAsc: "desc",
    orderByColumn: "recordStart,id",
    videoName: searchData.videoName,
    recordTime: searchData.recordTime,
  }).then((res: any) => {
    let {data} = res;
    console.log(data)
    if (data.code === 200) {
      const data1 = data.data
      if (data1 !== null) {
        total.value = data1.total
        recordList.value = data1.rows
      }
    } else {
      ElMessage.error(data?.msg)
    }
    getVideoLoading.value = false
  })
}
</script>
<style scoped>
.video-cover {
  width: 160px;
  height: 100px;
  margin: 10px;
  border-radius: 12px;
  border: 1px solid #dcdfe6;
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

<template>
  <el-container>
    <el-header>
      <header-base
          style="margin: auto;width: 90%;height: 60px;position: fixed;z-index: 10; transform: translate(-50%,0);left:50%"/>
    </el-header>
    <el-main style="margin: 30px auto;width: 65%">
      <el-page-header
          :icon="ArrowLeft"
          @back="goBack"
          style="margin-bottom: 50px;"
      >
        <template #content>
          <span class="text-large font-600 mr-3"> 云录制管理 </span>
        </template>
      </el-page-header>
      <div style="width: 100%;">
        <div style="float: left">
          <el-form :inline="true" class="demo-form-inline">
            <el-form-item label="视频名">
              <el-input
                  placeholder="搜索视频"
                  v-model="searchData.videoName"
              />
            </el-form-item>
            <el-form-item label="录制时间">
              <el-select
                  v-model="searchData.recordTime"
                  style="width: 100px">
                <el-option label="全部" value=""/>
                <el-option label="最近三天" value="-3"/>
                <el-option label="最近一周" value="-7"/>
                <el-option label="最近一月" value="-30"/>
              </el-select>
            </el-form-item>
            <el-form-item label="视频状态">
              <el-select
                  v-model="searchData.open"
                  style="width: 100px">
                <el-option label="全部" value=""/>
                <el-option label="公开" value="true"/>
                <el-option label="私密" value="false"/>
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
          <el-table-column width="100">
            <template #default="scope">
              <el-tag type="success" v-if="scope.row.open">公开</el-tag>
              <el-tag type="warning" v-else>私密</el-tag>
            </template>
          </el-table-column>
          <el-table-column align="right">
            <template #default="scope">
              <el-button
                  size="small"
                  type="success"
                  @click="publishVideo(scope.row.id)"
              >
                发布
              </el-button>
              <el-button
                  size="small"
                  type="primary"
                  @click="editRecordInfo(scope.row)"
              >
                编辑
              </el-button>
              <el-button
                  size="small"
                  type="danger"
                  @click="deleteRecord(scope.row.id)"
              >
                删除
              </el-button>
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
    <div v-if="isHls">
      <iframe :src="hlsValue" style="width: 100%;height: 80vh"></iframe>
      <!--      <vue3-video-play-->
      <!--          v-bind="options"-->
      <!--      />-->
    </div>
    <video
        v-else
        :src="videoUrl"
        controls
        style="width: 100%"
    ></video>
  </el-dialog>
  <el-drawer
      ref="drawerRef"
      v-model="dialogRecordVisible"
      title="修改录制信息"
      direction="rtl"
      class="demo-drawer"
  >
    <div class="demo-drawer__content">
      <el-form
          ref="ruleFormRef"
          label-width="50px"
          status-icon
      >
        <el-form-item label="名称">
          <el-input
              v-model="updateRecordForm.videoName"
              type="text"
          />
        </el-form-item>
        <el-form-item label="简介">
          <el-input
              type="textarea"
              v-model="updateRecordForm.videoIntro"
              maxlength="200"
              show-word-limit
          />
        </el-form-item>
        <el-form-item label="公开">
          <el-switch
              v-model="updateRecordForm.open"
          />
        </el-form-item>
        <el-form-item label="封面" prop="cover">
          <el-upload
              v-model:file-list="fileList"
              class="upload-demo"
              action="http://127.0.0.1:8081/upload/img"
              list-type="picture"
              :limit="1"
              :on-success="handleUploadImgSuccess"
              :on-error="handleUploadImgErr"
              :before-upload="beforeUploadImg"
          >
            <el-button type="primary">上传</el-button>
            <template #tip>
              <div class="el-upload__tip">
                小于5MB的 jpg/png 图片
              </div>
            </template>
          </el-upload>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="updateRecord" :loading="updateRecordLoading">修改</el-button>
          <el-button @click="dialogRecordVisible=false">取消</el-button>
        </el-form-item>
      </el-form>
    </div>
  </el-drawer>
  <el-dialog v-model="publishVisible" style="width: 600px;">
    <h3 style="margin-top: 0">发布视频</h3>
    <div style="width: 100%;justify-content: space-between;" class="flex items-center">
      <el-select
          style="width: 400px"
          v-model="courseValue"
          multiple
          filterable
          remote
          reserve-keyword
          placeholder="请选择课程"
          :remote-method="queryCourse"
          :loading="queryCourseLoading"
      >
        <el-option
            v-for="item in queryCourseOptions"
            :key="item.id"
            :label="item.courseName"
            :value="item.id"
            style="height: 62px"
        >
          <div class="flex items-center" style="justify-content: space-between">
            <div class="flex items-center">
              <img :src="item.cover" style="height: 60px;width: 100px;" alt="">
            </div>
            <div>{{ item.courseName }}</div>
          </div>
        </el-option>
      </el-select>
      <el-button type="primary" text bg @click="publish">发布</el-button>
    </div>
    <template #footer>
      <el-button type="primary" @click="publishVisible = false">关闭</el-button>
    </template>
  </el-dialog>
</template>
<script lang="ts" setup>
import {
  ArrowLeft, Search,
} from '@element-plus/icons-vue'
import '@wangeditor/editor/dist/css/style.css' // 引入 css
import localCache from "~/utils/cache";
import {onMounted, reactive, ref} from "vue";
import {getData, postData} from "~/api/main";
import {ElMessage, ElMessageBox, ElTable, UploadProps, UploadUserFile} from "element-plus";
import router from "~/router";
import 'vue3-video-play/dist/style.css'

const user = localCache.getCache("userInfo")

onMounted(() => {
  getRecordList()
})
/*-------------------------------------------------------------------------------------------------------------------*/
const dialogFormVisible = ref(false)
const videoUrl = ref('')
const isHls = ref(false)
const hlsValue = ref('')

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
    hlsValue.value = "https://tools.liumingye.cn/m3u8/#" + videoUrl.value
    isHls.value = true
    console.log("m3u8类型")
  } else {
    isHls.value = false
  }
  dialogFormVisible.value = true
}
const close = () => {
  videoUrl.value = ""
  hlsValue.value = videoUrl.value
}
/*-------------------------------------------------------------------------------------------------------------------*/
const goBack = () => {
  router.back()
}
/*-------------------------------------------------------------------------------------------------------------------*/
const publishVisible = ref(false)
const publishVideoId = ref('')
const queryCourseLoading = ref(true)
const queryCourseOptions = ref([])
const courseValue = ref([])

const publishVideo = (id: string) => {
  publishVideoId.value = id
  publishVisible.value = true
}

// 查询课程
const queryCourse = (query: string) => {
  queryCourseLoading.value = true
  if (query !== "") {
    getData("course/query", {
      queryParam: query,
      userId: user.id
    }).then((res: any) => {
      let {data} = res
      if (data.code === 200) {
        queryCourseOptions.value = data.data
        queryCourseLoading.value = false
      }
    })
  }
}
const publish = () => {
  const param = {
    videoId: publishVideoId.value,
    courseIds: courseValue.value
  }
  if (param.courseIds.length === 0) {
    ElMessage.warning("请选择课程")
    return
  }
  postData("course/playback", param).then((res: any) => {
    let {data} = res;
    console.log(data)
    if (data.code === 200) {
      ElMessage.success("发布成功")
    } else {
      ElMessage.error(data.msg)
    }
    courseValue.value = []
    publishVisible.value = false
  })
}

/*-------------------------------------------------------------------------------------------------------------------*/
const dialogRecordVisible = ref(false)
const updateRecordLoading = ref(false)

const updateRecordForm = reactive({
  videoId: '',
  videoName: '',
  videoIntro: '',
  videoCoverLink: '',
  open: false,
})
const editRecordInfo = (record: any) => {
  updateRecordForm.videoId = record.id
  updateRecordForm.videoName = record.videoName
  updateRecordForm.videoIntro = record.videoIntro
  updateRecordForm.videoCoverLink = record.videoCoverLink
  updateRecordForm.open = record.open

  fileList.value = []
  fileList.value.push({name: '', url: record.videoCoverLink})

  dialogRecordVisible.value = true
}

const updateRecord = () => {
  updateRecordLoading.value = true
  if (updateRecordForm.videoName === "") {
    ElMessage.warning("视频名称不能为空")
    updateRecordLoading.value = false
    dialogRecordVisible.value = false
    return
  }
  if (updateRecordForm.videoCoverLink === "") {
    ElMessage.warning("视频封面不能为空")
    updateRecordLoading.value = false
    dialogRecordVisible.value = false
    return
  }

  postData("record/update", updateRecordForm).then((res: any) => {
    let {data} = res;
    console.log(data)
    if (data.code === 200) {
      ElMessage.success("修改成功")
      getRecordList()
    } else {
      ElMessage.error(data.msg)
    }
    updateRecordLoading.value = false
    dialogRecordVisible.value = false
  })
}


/*-------------------------------------------------------------------------------------------------------------------*/
const fileList = ref<UploadUserFile[]>([])
const handleUploadImgErr: UploadProps['onError'] = (error) => {
  ElMessage.error(error.message)
}
const beforeUploadImg: UploadProps['beforeUpload'] = (rawFile) => {
  if (rawFile.type !== 'image/jpeg' && rawFile.type !== 'image/png') {
    ElMessage.error('请上传符合格式的 png/jpg 类型的图片')
    return false
  } else if (rawFile.size / 1024 / 1024 > 5) {
    ElMessage.error('请上传大小不超过5MB的图片')
    return false
  }
  return true
}
const handleUploadImgSuccess: UploadProps['onSuccess'] = (response) => {
  console.log(response)
  if (response.code === 200) {
    updateRecordForm.videoCoverLink = response.data
    ElMessage.success(response.msg)
  } else {
    ElMessage.error(response.msg)
  }
}
/*-------------------------------------------------------------------------------------------------------------------*/
const pageNum = ref(1)
const total = ref(1)
const recordList = ref([])

const message = ref({})
const searchData = reactive({
  videoName: "",
  recordTime: "",
  open: "",
})
const searchMessage = () => {
  pageNum.value = 1
  getRecordList()
}

const getRecordList = () => {
  getData("record/video", {
    pageSize: 4,
    pageNum: pageNum.value,
    isAsc: "desc",
    orderByColumn: "recordStart,id",
    userId: user.id,
    videoName: searchData.videoName,
    recordTime: searchData.recordTime,
    open: searchData.open
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
  })
}


/* -------------------------------------------------------------------------------------------------------------- */
const deleteRecord = (id: any) => {
  ElMessageBox.confirm(
      '确认删除？',
      '删除视频',
      {
        confirmButtonText: '删除',
        cancelButtonText: '取消',
        type: 'warning',
        autofocus: false,
      })
      .then(() => {
        console.log(id)
        getData("record/del", {
          id: id
        }).then((res: any) => {
          let {data} = res;
          if (data.code === 200) {
            ElMessage.success("删除成功")
            getRecordList()
          } else {
            ElMessage.error(data.msg)
          }
        })
      })
      .catch(() => {
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

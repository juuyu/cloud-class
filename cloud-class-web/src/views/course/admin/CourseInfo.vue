<template>
  <el-container>
    <el-header>
      <header-base
          style="margin: auto;width: 90%;height: 60px;  position: fixed;z-index: 10; transform: translate(-50%,0);left:50%"/>
    </el-header>
    <el-main style="margin: 30px auto;width: 65%">
      <el-page-header @back="goBack" :icon="ArrowLeft" style="margin-bottom: 50px">
        <template #content>
          <span class="text-large font-600 mr-3"> 课程详情 </span>
        </template>
      </el-page-header>
      <el-tabs type="card" @tab-click="handlerClick" v-model="activeTap">
        <el-tab-pane name="courseInfo">
          <template #label>
            <h3>
              <el-icon><House /></el-icon>
              当前课程
            </h3>
          </template>
          <data-course-admin
              :course="course"
              :course-info-loading="courseInfoLoading"
          />
        </el-tab-pane>
        <el-tab-pane name="courseUser">
          <template #label>
            <h3>
              <el-icon>
                <User/>
              </el-icon>
              课程人员
            </h3>
          </template>
          <table-user-admin
              :course-id="courseId"
              :user-list="userList"
              @get-course-user="getCourseUser"
          />
        </el-tab-pane>
        <el-tab-pane name="courseFile">
          <template #label>
            <h3>
              <el-icon>
                <Folder/>
              </el-icon>
              课程资料
            </h3>
          </template>
          <table-file-admin
              :course-id="courseId"
              :file-list-loading="fileListLoading"
              :data-list="fileList"
              @get-course-file="getCourseFile"
          />
        </el-tab-pane>
        <el-tab-pane name="courseJob">
          <template #label>
            <h3>
              <el-icon>
                <Notebook/>
              </el-icon>
              课程作业
            </h3>
          </template>
          <table-job-admin
              :course-id="courseId"
              :job-list="jobList"
              :job-list-loading="jobListLoading"
              @get-job-list="getCourseJob"
          />

        </el-tab-pane>
        <el-tab-pane name="coursePlayBack">
          <template #label>
            <h3>
              <el-icon>
                <VideoCamera/>
              </el-icon>
              课程回放
            </h3>
          </template>
          <table-playback-admin
              :data-list="playbackList"
              :playback-list-loading="playbackListLoading"
              @get-course-play-back="getCoursePlayBack"
          />
        </el-tab-pane>
      </el-tabs>
    </el-main>
  </el-container>
</template>
<script lang="ts" setup>
import {onMounted, ref} from 'vue'
import {
  VideoCamera,
  Folder,
  User,
  ArrowLeft, Notebook, House,
} from '@element-plus/icons-vue'
import {useRoute} from "vue-router";
import {getData} from "~/api/main";
import {ElMessage, TabsPaneContext} from "element-plus";
import localCache from "~/utils/cache";
import DataCourseAdmin from "~/components/data/course/data-course-admin.vue";
import TableFileAdmin from "~/components/table/file/table-file-admin.vue";
import TablePlaybackAdmin from "~/components/table/video/table-playback-admin.vue";
import router from "~/router";

const activeTap: any = ref("courseInfo")
const {query} = useRoute();
const courseId = query?.courseId

onMounted(() => {
  getCourseInfo()
})

const goBack = () => {
  router.back()
}

const course: any = ref({})
const userList = ref([])
const fileList = ref([])
const jobList = ref([])
const playbackList = ref([])
const courseInfoLoading = ref(true)
const userListLoading = ref(true)
const fileListLoading = ref(true)
const jobListLoading = ref(true)
const playbackListLoading = ref(true)

const getCourseInfo = () => {
  courseInfoLoading.value = true
  getData("/course/info", {
    courseId: courseId
  }).then((res: any) => {
    let {data} = res
    if (data.code === 200) {
      course.value = data.data
    } else {
      ElMessage.error(data.msg)
      router.push("/course/create")
    }
    courseInfoLoading.value = false
  })
}

const getCourseUser = () => {
  userListLoading.value = true
  getData("/course/user/list", {
    courseId: courseId
  }).then((res: any) => {
    let {data} = res
    console.log(data.data.rows)
    if (data.code === 200) {
      userList.value = data.data
    } else {
      ElMessage.error(data.msg)
    }
    userListLoading.value = false
  })
}

const getCourseFile = () => {
  fileListLoading.value = true
  getData("/course/file", {
    courseId: courseId
  }).then((res: any) => {
    let {data} = res
    console.log(data)
    if (data.code === 200) {
      fileList.value = data.data
    } else {
      ElMessage.error(data.msg)
    }
    fileListLoading.value = false
  })
}

const getCourseJob = () => {
  jobListLoading.value = true
  getData("/course/job", {
    courseId: courseId
  }).then((res: any) => {
    let {data} = res
    console.log(data)
    if (data.code === 200) {
      jobList.value = data.data
    } else {
      ElMessage.error(data.msg)
    }
    jobListLoading.value = false
  })
}

const getCoursePlayBack = () => {
  playbackListLoading.value = true
  getData("/course/playback", {
    courseId: courseId
  }).then((res: any) => {
    let {data} = res
    console.log(data)
    if (data.code === 200) {
      playbackList.value = data.data
    } else {
      ElMessage.error(data.msg)
    }
    playbackListLoading.value = false
  })
}


const handlerClick = (tab: TabsPaneContext) => {
  const name = tab.paneName
  console.log("click name:" + name)
  switch (name) {
    case "courseUser":
      getCourseUser()
      break

    case "courseFile":
      getCourseFile()
      break

    case "courseJob":
      getCourseJob()
      break

    case "coursePlayBack":
      getCoursePlayBack()
      break

    default:
      console.log("handlerClick fail")
  }
}

</script>
<style scoped>
.ep-tabs {
  --ep-tabs-header-height: 100%;

}

.demo-tabs > .el-tabs__content {
  padding: 32px;
  color: #6b778c;
  font-size: 32px;
  font-weight: 600;
}

.infinite-list {
  height: 300px;
  padding: 0;
  margin: 0;
  list-style: none;
}

.infinite-list .infinite-list-item {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 50px;
  background: var(--el-color-primary-light-9);
  margin: 10px;
  color: var(--el-color-primary);
}

.infinite-list .infinite-list-item + .list-item {
  margin-top: 10px;
}
</style>

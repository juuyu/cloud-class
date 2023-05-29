<template>
  <div v-loading="courseInfoLoading">
    <div style="width: 100%;height: 60px;display: flex;align-items: center;justify-content: space-between;">
                <span style="display: flex;align-items: center;">
                  <img src="../../../assets/live/live.svg" alt="" style="height: 40px"/>
                  <span style="margin-left: 30px">
                    {{ course?.courseName }}
                  </span>
                </span>
      <span style="float: right;">
        <el-button type="success" plain @click="userJoinLive">进入直播</el-button>
    </span>
    </div>
    <el-divider/>
    <h3></h3>
    <el-descriptions title="课程介绍" :column="2">
      <template #extra>
        <el-button type="danger" @click="exitCourse">退出</el-button>
      </template>
      <el-descriptions-item label="课程类型:">
        <el-tag size="small">{{ course?.courseType }}</el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="老师:">{{ course?.teacherName }}</el-descriptions-item>
      <el-descriptions-item label="课程时间:">{{ course?.startTime }} ~ {{ course?.endTime }}</el-descriptions-item>
      <el-descriptions-item label="课程人数:">{{ course?.courseUserNum }} / {{
          course?.userNum === 2147483647 ? "Max" : course?.userNum
        }}
      </el-descriptions-item>
    </el-descriptions>
    <el-descriptions :column="1">
      <el-descriptions-item label="课程安排:">{{ formatCourseSchedule(course?.weekArrange) }}</el-descriptions-item>
      <el-descriptions-item label="课程简介:">{{ course?.intro }}</el-descriptions-item>
    </el-descriptions>
  </div>
</template>
<script lang="ts" setup>
import {toRefs} from "vue";
import router from "~/router";
import {ElMessage, ElMessageBox} from "element-plus";
import localCache from "~/utils/cache";
import {getData} from "~/api/main";
import {formatCourseSchedule} from "~/utils/courseArrangeUtil";

const user = localCache.getCache("userInfo")

interface Props {
  course: any,
  courseInfoLoading: boolean
}

const props = defineProps<Props>()
const {course, courseInfoLoading} = toRefs(props)

const exitCourse = () => {
  ElMessageBox.confirm(
      '确认退出该课程？',
      '退出课程',
      {
        confirmButtonText: '退出',
        cancelButtonText: '取消',
        type: 'warning',
        autofocus: false,
      })
      .then(() => {
        getData("course/user/exit", {
          courseId: course.value?.id,
          userId: user.id
        }).then((res: any) => {
          let {data} = res
          if (data.code === 200) {
            ElMessage.success("退出成功")
          } else {
            ElMessage.error(data.msg)
          }
          router.push({
            path: "/course/join"
          })
        })
      })
      .catch(() => {
      })
}
const userJoinLive = () => {
  const courseId = course.value?.id
  const courseType = course.value?.courseType
  if (courseType === "私教课") {
    // todo
  } else {
    getData("/live/stu/join", {
      courseId: courseId
    }).then((res: any) => {
      let {data} = res
      console.log(data)
      if (data.code === 200) {
        const userJoinLiveDto = data.data;
        // 直播房间信息存入缓存
        localCache.setCache(userJoinLiveDto.roomId, userJoinLiveDto)
        router.push({
          path: "/live/stu/class",
          query: {
            courseId: courseId,
            courseCode: course.value?.courseCode,
            roomId: userJoinLiveDto.roomId
          }
        });
      } else {
        ElMessage.error(data.msg)
      }
    })
  }

}

</script>
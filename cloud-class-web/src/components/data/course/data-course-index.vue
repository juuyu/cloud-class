<template>
  <div class="course">
    <el-image :src="course?.cover"
              fit="fill" class="img"/>
    <div class="content">
      <div class="course-title">
        <span>{{ course?.courseName }}</span>
        <el-tag effect="plain" style="margin-right: 20px">{{ course?.courseType }}</el-tag>
      </div>
      <div class="course-info">
        <span class="course-tea-name">{{ course?.teacherName }}</span>
        <span class="course-time">{{ course?.startTime }} ~ {{ course?.endTime }}</span>
      </div>
      <div class="course-hot">
        <img src="src/assets/hot.svg" alt="">
        <span class="course-num">最近在学 {{ course?.courseUserNum }}人</span>
      </div>
    </div>
  </div>
</template>
<script lang="ts" setup>
import {toRefs} from "vue";
import router from "~/router";
import localCache from "~/utils/cache";

interface Props {
  course: any
}

const props = defineProps<Props>()
const {course} = toRefs(props)

</script>
<style scoped>
.course {
  width: 100%;
  height: 100%;
  border-radius: 8px;
  background-color: #F5F7FA;
  cursor: pointer;
}

.course .img {
  width: 100%;
  height: 250px;
  border-radius: 8px 8px 0 0;
}

.content {
  width: 100%;
  height: 100px;
  border-radius: 0 0 8px 8px;
}

.course-title {
  margin-left: 20px;
  font-size: 16px;
  font-weight: bolder;
  align-items: center;
  display: flex;
  justify-items: center;
  justify-content: space-between;
}

.course-info {
  display: flex;
  justify-content: space-between;
  font-size: 16px;
  margin-top: 10px
}

.course-tea-name {
  margin-left: 20px;

}

.course-time {
  margin-right: 20px;

}

.course-hot {
  margin-top: 10px;
  margin-left: 20px;
  align-items: center;
  display: flex;
  justify-items: center;
  justify-content: left;
}

.course-hot img {
  height: 20px;
}

.course-num {
  font-size: 16px;
  margin-left: 5px;
}
</style>
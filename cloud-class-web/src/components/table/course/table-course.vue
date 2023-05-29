<template>
  <div class="flex">
    <div class="course" v-for="item in dataList" @click="courseInfo(item?.courseCode, item)">
      <el-image :src="item?.cover"
                fit="fill" class="img"/>
      <div class="content">
        <div>
          <span style="font-size: 16px;font-weight: bolder">{{ item?.courseName }}</span>
          <el-tag style="float: right" effect="plain">{{ item?.courseType }}</el-tag>
        </div>
        <div style="font-size: 16px;margin-top: 10px">{{ item?.teacherName }}</div>
        <div style="margin-top: 10px;margin-bottom: 10px;">
          <span style="font-size: 14px;">{{ item?.startTime }} ~ {{ item?.endTime }}</span>
          <el-tag style="float: right" type="danger" effect="plain">{{ item?.courseStatus }}</el-tag>
        </div>
      </div>
    </div>
  </div>
</template>
<script lang="ts" setup>
import {toRefs} from "vue";
import router from "~/router";
import localCache from "~/utils/cache";

interface Props {
  dataList: Array<any>
}

const props = defineProps<Props>()
const {dataList} = toRefs(props)

const courseInfo = (courseCode: string, course: any) => {
  // 课程详情
  localCache.setCache(courseCode, course)

  const user = localCache.getCache("userInfo")
  if (user.id === course?.teacherId) {
    // 自己创建的课程
    router.push({
      path: "/course/info/admin",
      query: {
        courseId: course?.id,
        courseCode: courseCode
      }
    });
  } else {
    // 加入的课程
    router.push({
      path: "/course/info",
      query: {
        courseId: course?.id,
        courseCode: courseCode
      }
    });

  }



}

</script>
<style scoped>
.course {
  width: 23%;
  margin-left: 2%;
  border-radius: 12px;
  /*background-color: #d9f5d5;*/
  border: 1px solid #dcdfe6;
}

.course:hover {
  scale: 1.04;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.4);
  cursor: pointer;
}

.course .img {
  width: 100%;
  height: 230px;
  border-radius: 12px 12px 0 0;
  border-bottom: 1px solid #dcdfe6;
}

.content {
  padding: 10px;
  border-radius: 0 0 12px 12px;
  /*border-top: 1px solid #dcdfe6;*/
}

.content h3 {
  font-size: 16px;
}


</style>
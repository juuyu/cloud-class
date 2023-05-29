<template>
  <el-container>
    <el-header>
      <header-base
          style="margin: auto;width: 90%;height: 60px;  position: fixed;z-index: 10; transform: translate(-50%,0);left:50%"/>
    </el-header>
    <el-main style="margin: 30px auto;width: 65%">
      <div style="width: 100%;">
        <div style="float: left">
          <el-form :inline="true" :model="searchData" class="demo-form-inline">
            <el-form-item label="课程名">
              <el-input v-model="searchData.courseName" placeholder="请输入课程名"/>
            </el-form-item>
            <el-form-item label="课程类型">
              <el-select v-model="searchData.courseType" placeholder="课程类型" style="width: 100px">
                <el-option label="全部" value=""/>
                <el-option label="班级课" value="班级课"/>
                <el-option label="公开课" value="公开课"/>
              </el-select>
            </el-form-item>
          </el-form>
        </div>
        <div style="float: right">
          <el-button type="primary" :icon="Search" @click="getCourses">搜索</el-button>
        </div>
      </div>
      <div style="width: 100%;margin-top: 150px" v-loading="dataListLoading">
        <div class="flex">
          <div class="course" v-for="item in dataList" @click="joinCourse(item?.id)">
            <el-image :src="item?.cover"
                      fit="fill" class="img"/>
            <div class="content">
              <div>
                <span style="font-size: 16px;font-weight: bolder">{{ item?.courseName }}</span>
                <el-tag style="float: right" effect="plain">{{ item?.courseType }}</el-tag>
              </div>
              <div style="font-size: 16px;margin-top: 10px">
                <span>{{ item?.teacherName }}</span>
                <span style="float: right">{{ item?.courseUserNum }}&nbsp;/&nbsp;{{ item?.userNum }}</span>

              </div>
              <div style="margin-top: 10px;margin-bottom: 10px;">
                <span style="font-size: 14px;">{{ item?.startTime }} ~ {{ item?.endTime }}</span>
              </div>
            </div>
          </div>
        </div>
        <el-empty description="暂未加入课程" v-show="dataList.length === 0"/>
      </div>
      <div style="margin-top: 50px;float: right;">
        <el-pagination
            background
            layout="prev, pager, next"
            :page-size="4"
            :total="total"
            v-model:current-page="pageNum"
            @current-change="getCourses"
        />
      </div>
    </el-main>
  </el-container>
</template>

<script lang="ts" setup>
import {Search} from '@element-plus/icons-vue'
import {onMounted, reactive, ref} from 'vue'
import {getData} from "~/api/main";
import {ElMessage, ElMessageBox} from "element-plus";
import localCache from "~/utils/cache";

const user = localCache.getCache("userInfo")
const joinCourse = (courseId: number) => {
  ElMessageBox.confirm(
      '申请加入该课程？',
      '申请加入',
      {
        confirmButtonText: '申请',
        cancelButtonText: '取消',
        type: 'success',
        autofocus: false,
      })
      .then(() => {
        join(courseId)
      })
      .catch(() => {
      })
}
const join = (courseId: number) => {
  getData("course/apply", {
    courseId: courseId,
    userId: user?.id
  }).then((res: any) => {
    let {data} = res;
    console.log(data)
    if (data.code === 200) {
      ElMessage.success("已发送申请，请等待审核")
    } else {
      ElMessage.error(data.msg)
    }
  })

}

const searchData = reactive({
  courseName: "",
  courseType: "",
})

const dataList = ref([])
const dataListLoading = ref(true)
const pageNum = ref(1)
const total = ref(1)

onMounted(() => {
  getCourses()
})

const getCourses = () => {
  dataListLoading.value = true
  getData("open/course", {
    pageSize: 4,
    pageNum: pageNum.value,
    courseName: searchData.courseName,
    courseType: searchData.courseType,
  }).then((res: any) => {
    let {data} = res;
    console.log(data)
    if (data.code === 200) {
      const data1 = data.data
      if (data1 !== null) {
        total.value = data1.total
        dataList.value = data1.rows
      }
    } else {
      ElMessage.error(data?.msg)
    }
    dataListLoading.value = false
  })
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
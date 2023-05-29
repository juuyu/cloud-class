<template>
  <el-container>
    <el-header>
      <header-base
          style="margin: auto;width: 90%;height: 60px;  position: fixed;z-index: 10; transform: translate(-50%,0);left:50%"/>
    </el-header>
    <el-main style="margin: 30px auto;width: 65%">
      <div style="width: 100%">
        <el-page-header @back="goBack" :icon="ArrowLeft" style="margin-bottom: 50px">
          <template #content>
            <span class="text-large font-600 mr-3"> 加入的课程 </span>
          </template>
          <template #extra>
            <div class="flex items-center">
              <el-button type="primary" text bg :icon="Plus" @click="dialogFormVisible = true">加入</el-button>
            </div>
            <el-dialog v-model="dialogFormVisible" title="加入课程" center style="width: 400px;">
              <div style="margin-top: 20px;">
                <el-form>
                  <el-form-item label="">
                    <el-input autocomplete="off" v-model="inviteCode" placeholder="请输入邀请码"/>
                  </el-form-item>
                </el-form>
              </div>
              <template #footer>
                <span class="dialog-footer">
                  <el-button @click="dialogFormVisible = false">取消</el-button>
                  <el-button type="primary" @click="joinCourseByInviteCode">加入</el-button>
                </span>
              </template>
            </el-dialog>
          </template>
        </el-page-header>
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
              <el-form-item label="课程状态">
                <el-select v-model="searchData.courseStatus" placeholder="课程状态" style="width: 100px">
                  <el-option label="进行中" value="进行中"/>
                  <el-option label="已完结" value="已完结"/>
                </el-select>
              </el-form-item>
            </el-form>
          </div>
          <div style="float: right">
            <el-button type="primary" text bg :icon="Search" @click="getCourses">搜索</el-button>
          </div>
        </div>
        <div style="width: 100%;margin-top: 150px" v-loading="dataListLoading">
          <table-course :data-list="dataList"></table-course>
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
      </div>
    </el-main>
  </el-container>
</template>

<script lang="ts" setup>
import {Search, Plus, ArrowLeft} from '@element-plus/icons-vue'
import {onMounted, reactive, ref} from 'vue'
import {getData} from "~/api/main";
import {ElMessage} from "element-plus";
import router from "~/router";

const goBack = () => {
  router.back()
}


const dialogFormVisible = ref(false)
const inviteCode = ref("")
const joinCourseByInviteCode = () => {
  if (inviteCode.value === "") {
    ElMessage.error("邀请码不能为空")
    return
  }
  getData("/course/apply/code", {
    courseCode: inviteCode.value
  }).then((res: any) => {
    let {data} = res;
    if (data.code === 200) {
      ElMessage.success(data?.msg)
      dialogFormVisible.value = false
    } else {
      ElMessage.error(data?.msg)
    }
    getCourses()
  })
}


const searchData = reactive({
  courseName: "",
  courseType: "",
  courseStatus: "进行中"
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
  getData("/course", {
    pageSize: 4,
    pageNum: pageNum.value,
    courseName: searchData.courseName,
    courseType: searchData.courseType,
    courseStatus: searchData.courseStatus
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
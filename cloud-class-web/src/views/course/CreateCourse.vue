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
            <span class="text-large font-600 mr-3"> 创建的课程 </span>
          </template>
          <template #extra>
            <div class="flex items-center">
              <el-button type="primary" text bg :icon="Plus" @click="dialogFormVisible = true">创建</el-button>
            </div>
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
      <el-drawer
          ref="drawerRef"
          v-model="dialogFormVisible"
          title="创建课程"
          direction="rtl"
          class="demo-drawer"
      >
        <div class="demo-drawer__content">
          <el-form
              ref="ruleFormRef"
              label-width="120px"
              :model="addCourseForm"
              :rules="rules"
              status-icon
          >
            <el-form-item label="课程名称" prop="courseName">
              <el-input
                  v-model="addCourseForm.courseName"
                  maxlength="20"
                  show-word-limit
                  type="text"
              />
            </el-form-item>
            <el-form-item label="课程类型" prop="courseType">
              <el-select
                  v-model="addCourseForm.courseType"
                  @change="courseTypeChange"
              >
                <el-option label="班级课" value="班级课"/>
                <el-option label="公开课" value="公开课"/>
              </el-select>
            </el-form-item>
            <el-form-item label="课程日期" prop="courseTime">
              <el-date-picker
                  v-model="addCourseForm.courseTime"
                  type="daterange"
                  range-separator="To"
                  start-placeholder="开始日期"
                  end-placeholder="结束日期"
              />
            </el-form-item>
            <el-form-item label="截止加入" prop="joinEndTime">
              <el-date-picker
                  v-model="addCourseForm.joinEndTime"
                  type="date"
                  placeholder="Pick a date"
                  style="width: 100%"
              />
            </el-form-item>
            <el-form-item label="上限人数" v-show="userNumDisable">
              <el-input-number
                  :min="1"
                  :max="200"
                  v-model="addCourseForm.userNum"
              />
            </el-form-item>
            <el-form-item label="课程安排" prop="weekArrange">
              <el-cascader
                  :options="options"
                  :props="props"
                  clearable
                  v-model="addCourseForm.weekArrange"
              />
            </el-form-item>
            <el-form-item label="是否公开">
              <el-switch
                  v-model="addCourseForm.needOpen"
                  :disabled="needOpenDisable"
              />
            </el-form-item>
            <el-form-item label="是否审核">
              <el-switch v-model="addCourseForm.needReview"/>
            </el-form-item>
            <el-form-item label="课程封面" prop="cover">
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
            <el-form-item label="课程简介" prop="intro">
              <el-input
                  type="textarea"
                  v-model="addCourseForm.intro"
                  maxlength="200"
                  show-word-limit
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="createCourse(ruleFormRef)">创建</el-button>
              <el-button @click="resetForm(ruleFormRef)">取消</el-button>
            </el-form-item>
          </el-form>
        </div>
      </el-drawer>
    </el-main>
  </el-container>
</template>

<script lang="ts" setup>
import {Search, Plus, ArrowLeft} from '@element-plus/icons-vue'
import {onMounted, reactive, ref} from 'vue'
import {getData, postData} from "~/api/main";
import {ElMessage, ElDrawer, FormRules, FormInstance} from "element-plus"
import type {UploadProps, UploadUserFile} from 'element-plus'
import {generateCourseSchedule} from "~/utils/courseArrangeUtil";
import router from "~/router";

const goBack = () => {
  router.back()
}

const fileList = ref<UploadUserFile[]>([])
const drawerRef = ref<InstanceType<typeof ElDrawer>>()
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
    addCourseForm.cover = response.data
    ElMessage.success(response.msg)
  } else {
    ElMessage.error(response.msg)
  }
}
const dialogFormVisible = ref(false)
const userNumDisable = ref(true)
const needOpenDisable = ref(false)
const courseTypeChange = () => {
  if (addCourseForm.courseType === "公开课") {
    userNumDisable.value = false
    addCourseForm.needOpen = true
    needOpenDisable.value = true
  }
  if (addCourseForm.courseType === "班级课") {
    userNumDisable.value = true
    addCourseForm.needOpen = false
    needOpenDisable.value = false
  }
}

const addCourseForm = reactive({
  courseName: '',
  courseType: '班级课',
  courseTime: [],
  intro: '',
  cover: '',
  joinEndTime: '',
  weekArrange: [],
  userNum: '',
  needReview: false,
  needOpen: false
})

const addCourseParam = reactive({
  courseName: '',
  courseType: '',
  startTime: '',
  endTime: '',
  intro: '',
  cover: '',
  joinEndTime: '',
  weekArrange: '',
  userNum: '',
  needReview: false,
  needOpen: false,
})

const ruleFormRef = ref<FormInstance>()
const rules = reactive<FormRules>({
  courseName: [
    {required: true, message: '课程名不能为空', trigger: 'blur'},
    {min: 2, max: 20, message: '课程名长度为 2 ~ 20', trigger: 'blur'},
  ],
  courseTime: [
    {
      type: 'array',
      required: true,
      message: '课程日期不能为空',
      trigger: 'change'
    }
  ],
  joinEndTime: [
    {
      type: 'date',
      required: true,
      message: '截止日期不能为空',
      trigger: 'change'
    }
  ],
  weekArrange: [
    {
      type: 'array',
      required: true,
      message: '课程安排不能为空',
      trigger: 'change'
    }
  ],
  cover: [
    {
      required: true,
      message: '课程封面不能为空',
      trigger: 'change'
    }
  ],
  intro: [
    {required: true, message: '课程介绍不能为空', trigger: 'blur'},
    {min: 5, max: 200, message: '课程介绍长度为 5 ~ 200', trigger: 'blur'},
  ],

})

const resetForm = (formEl: FormInstance | undefined) => {
  if (!formEl) return
  formEl.resetFields()
  fileList.value = []
}

const createCourse = async (formEl: FormInstance | undefined) => {
  console.log(JSON.stringify(addCourseForm))
  if (!formEl) return
  await formEl.validate((valid, fields) => {
    if (valid) {
      const startTime: any = addCourseForm.courseTime[0]
      const endTime: any = addCourseForm.courseTime[1]
      const joinEndTime: any = addCourseForm.joinEndTime
      const today: any = new Date(new Date(new Date().toLocaleDateString()).getTime())
      if (startTime < today) {
        ElMessage.error("课程开始时间不得早于今天")
        return
      }
      const diffDays = Math.abs((endTime.getTime() - startTime.getTime()) / (1000 * 60 * 60 * 24));
      if (diffDays < 7) {
        ElMessage.error("课程安排时间不得小于一周")
        return
      }
      if (joinEndTime < startTime) {
        ElMessage.error("截止加入课程时间不得早于课程开始时间")
        return;
      }
      if (joinEndTime > endTime) {
        ElMessage.error("截止加入课程时间不得晚于课程结束时间")
        return
      }
      // const param = convertToParamAndJson()
      addCourseParam.courseName = addCourseForm.courseName
      addCourseParam.courseType = addCourseForm.courseType
      addCourseParam.startTime = addCourseForm.courseTime[0]
      addCourseParam.endTime = addCourseForm.courseTime[1]
      addCourseParam.intro = addCourseForm.intro
      addCourseParam.cover = addCourseForm.cover
      addCourseParam.joinEndTime = addCourseForm.joinEndTime
      addCourseParam.userNum = addCourseForm.userNum
      addCourseParam.needReview = addCourseForm.needReview
      addCourseParam.needOpen = addCourseForm.needOpen

      addCourseParam.weekArrange = generateCourseSchedule(addCourseForm.weekArrange)


      postData("course", addCourseParam).then((res: any) => {
        let {data} = res;
        console.log(data)
        if (data.code === 200) {
          ElMessage.success(data.msg)
          dialogFormVisible.value = false
          getCourses()
        } else {
          ElMessage.error(data.msg)
        }
      })


    } else {
      console.log('error submit!', fields)
    }
  })
}

const searchData = reactive({
  courseName: "",
  courseType: "",
  courseStatus: "进行中"
})

const dataList = ref([]);
const dataListLoading = ref(true)
const pageNum = ref(1)
const total = ref(1)
const getCourses = () => {
  dataListLoading.value = true
  getData("/course/created", {
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
      ElMessage.error(data.msg)
    }
    dataListLoading.value = false
  })
}

onMounted(() => {
  getCourses()
})
const props = {multiple: true}
const options = [
  {
    value: 1,
    label: '周一',
    children: [
      {value: 1, label: '第一节'},
      {value: 2, label: '第二节'},
      {value: 3, label: '第三节'},
      {value: 4, label: '第四节'},
      {value: 5, label: '第五节'},
      {value: 6, label: '第六节'}
    ]
  },
  {
    value: 2,
    label: '周二',
    children: [
      {value: 1, label: '第一节'},
      {value: 2, label: '第二节'},
      {value: 3, label: '第三节'},
      {value: 4, label: '第四节'},
      {value: 5, label: '第五节'},
      {value: 6, label: '第六节'}
    ]
  },
  {
    value: 3,
    label: '周三',
    children: [
      {value: 1, label: '第一节'},
      {value: 2, label: '第二节'},
      {value: 3, label: '第三节'},
      {value: 4, label: '第四节'},
      {value: 5, label: '第五节'},
      {value: 6, label: '第六节'}
    ]
  },
  {
    value: 4,
    label: '周四',
    children: [
      {value: 1, label: '第一节'},
      {value: 2, label: '第二节'},
      {value: 3, label: '第三节'},
      {value: 4, label: '第四节'},
      {value: 5, label: '第五节'},
      {value: 6, label: '第六节'}
    ]
  },
  {
    value: 5,
    label: '周五',
    children: [
      {value: 1, label: '第一节'},
      {value: 2, label: '第二节'},
      {value: 3, label: '第三节'},
      {value: 4, label: '第四节'},
      {value: 5, label: '第五节'},
      {value: 6, label: '第六节'}
    ]
  },
  {
    value: 6,
    label: '周六',
    children: [
      {value: 1, label: '第一节'},
      {value: 2, label: '第二节'},
      {value: 3, label: '第三节'},
      {value: 4, label: '第四节'},
      {value: 5, label: '第五节'},
      {value: 6, label: '第六节'}
    ]
  },
  {
    value: 7,
    label: '周日',
    children: [
      {value: 1, label: '第一节'},
      {value: 2, label: '第二节'},
      {value: 3, label: '第三节'},
      {value: 4, label: '第四节'},
      {value: 5, label: '第五节'},
      {value: 6, label: '第六节'}
    ]
  },

]
</script>
<style scoped>
</style>
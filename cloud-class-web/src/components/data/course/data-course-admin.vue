<template>
  <div v-loading="courseInfoLoading">
    <div style="width: 100%;height: 60px;display: flex;align-items: center;justify-content: space-between;">
                <span style="display: flex;align-items: center;">
                  <img src="src/assets/live/live.svg" alt="" style="height: 40px"/>
                  <span style="margin-left: 30px">
                    {{ course?.courseName }}
                  </span>
                </span>
      <span style="float: right;">
        <el-button type="success" plain @click="startLive">开始直播</el-button>
    </span>
    </div>
    <el-divider/>
    <h3></h3>
    <el-descriptions title="课程信息" :column="2">
      <template #extra>
        <el-button type="primary" plain :icon="Edit" @click="startFix">修改</el-button>
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
    <el-drawer
        ref="drawerRef"
        v-model="dialogFormVisible"
        title="修改课程"
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
                :disabled="true"
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
                :disabled="true"
            />
          </el-form-item>
          <el-form-item label="截止加入" prop="joinEndTime">
            <el-date-picker
                v-model="addCourseForm.joinEndTime"
                type="date"
                placeholder="Pick a date"
                style="width: 100%"
                :disabled="true"
            />
          </el-form-item>
          <el-form-item label="上限人数" v-show="userNumDisable">
            <el-input-number
                :min="course?.courseUserNum"
                :max="200"
                v-model="addCourseForm.userNum"
            />
          </el-form-item>
          <el-form-item label="课程安排" prop="weekArrange">
            <el-cascader
                :options="options"
                :props="props1"
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
            <el-button type="primary" @click="updateCourse(ruleFormRef)" :loading="waitLoading">修改</el-button>
            <el-button @click="dialogFormVisible=false">取消</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-drawer>
  </div>
</template>
<script lang="ts" setup>
import {onMounted, reactive, ref, toRefs} from "vue";
import router from "~/router";
import {ElDrawer, ElMessage, FormInstance, FormRules, UploadProps, UploadUserFile} from "element-plus";
import {Edit} from '@element-plus/icons-vue'
import localCache from "~/utils/cache";
import {getData, postData} from "~/api/main";
import {formatCourseSchedule, generateCourseSchedule, parseCourseSchedule} from "~/utils/courseArrangeUtil";
import videojs from "video.js";
import any = videojs.any;

interface Props {
  course: any
  courseInfoLoading: boolean
}

const props = defineProps<Props>()
const {course, courseInfoLoading} = toRefs(props)

const dialogFormVisible = ref(false)
const drawerRef = ref<InstanceType<typeof ElDrawer>>()
const fileList = ref<UploadUserFile[]>([{
  name: '',
  url: '',
},])
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
const userNumDisable = ref(true)
const needOpenDisable = ref(false)

const addCourseForm = reactive({
  courseName: '',
  courseType: '',
  courseTime: [new Date(), new Date()],
  intro: '',
  cover: '',
  joinEndTime: new Date(),
  weekArrange: [] as any[],
  userNum: '',
  needReview: false,
  needOpen: false
})

const updateCourseParam = reactive({
  id: '',
  courseName: '',
  intro: '',
  cover: '',
  weekArrange: '',
  userNum: '',
  needReview: false,
  needOpen: false,
})

const startFix = () => {
  addCourseForm.courseName = course?.value?.courseName
  addCourseForm.courseType = course?.value?.courseType
  addCourseForm.courseTime = [new Date(course?.value?.startTime?.replace(/-/, "/")), new Date(course?.value?.endTime?.replace(/-/, "/"))]
  addCourseForm.intro = course?.value?.intro
  addCourseForm.cover = course?.value?.cover
  addCourseForm.joinEndTime = new Date(course?.value?.joinEndTime?.replace(/-/, "/"))
  addCourseForm.weekArrange = parseCourseSchedule(course?.value?.weekArrange)
  addCourseForm.userNum = course?.value?.userNum
  addCourseForm.needReview = course?.value?.needReview
  addCourseForm.needOpen = course?.value?.needOpen

  fileList.value = [{
    name: '',
    url: course?.value?.cover,
  },]
  if (addCourseForm.courseType === "公开课") {
    userNumDisable.value = false
    needOpenDisable.value = true
  } else {
    userNumDisable.value = true
    needOpenDisable.value = false
  }

  dialogFormVisible.value = true
}

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
const waitLoading = ref(false)
const updateCourse = async (formEl: FormInstance | undefined) => {
  waitLoading.value = true
  console.log(JSON.stringify(addCourseForm))
  if (!formEl) return
  await formEl.validate((valid, fields) => {
    if (valid) {
      // const param = convertToParamAndJson()
      updateCourseParam.id = course?.value?.id
      updateCourseParam.courseName = addCourseForm.courseName
      updateCourseParam.intro = addCourseForm.intro
      updateCourseParam.cover = addCourseForm.cover
      updateCourseParam.userNum = addCourseForm.userNum
      updateCourseParam.needReview = addCourseForm.needReview
      updateCourseParam.needOpen = addCourseForm.needOpen

      updateCourseParam.weekArrange = generateCourseSchedule(addCourseForm.weekArrange)

      postData("course/update", updateCourseParam).then((res: any) => {
        let {data} = res;
        console.log(data)
        if (data.code === 200) {
          ElMessage.success(data.msg)
          dialogFormVisible.value = false
          const newCourse = data.data
          localCache.setCache(newCourse.courseCode, newCourse)
          waitLoading.value = true
          location.reload()
        } else {
          waitLoading.value = false
          ElMessage.error(data.msg)
        }
      })
    } else {
      console.log('error submit!', fields)
    }
  })
}
const props1 = {multiple: true}
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
const startLive = () => {
  const courseId = course.value?.id
  const courseType = course.value?.courseType
  if (courseType === "私教课") {
    // todo
  } else {
    getData("/live/tea/start", {
      courseId: courseId
    }).then((res: any) => {
      let {data} = res
      console.log(data)
      if (data.code === 200) {
        const userJoinLiveDto = data.data;
        // 直播房间信息存入缓存
        localCache.setCache(userJoinLiveDto.roomId, userJoinLiveDto)
        router.push({
          path: "/live/device",
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
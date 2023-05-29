<template>
  <el-table :data="jobList" style="width: 100%" v-loading="jobListLoading">
    <el-table-column label="名称" prop="jobName"/>
    <el-table-column label="发布时间" prop="startTime"/>
    <el-table-column label="截止日期" prop="endTime"/>
    <el-table-column align="right">
      <template #header>
        <el-button type="primary" text bg :icon="Plus" @click="addJobVisible=true" style="width: 150px;">发布作业
        </el-button>
      </template>
      <template #default="scope">
        <el-button size="small" @click="editCourseJob(scope.row)">
          编辑
        </el-button>
        <el-button
            size="small"
            type="primary"
            @click="getJobDetail(scope.row)">
          详情
        </el-button>
        <el-button
            size="small"
            type="danger"
            @click="deleteJob(scope.row.id)">
          删除
        </el-button>
      </template>
    </el-table-column>
  </el-table>

  <el-drawer
      ref="addJobDrawerRef"
      v-model="addJobVisible"
      title="发布作业"
      direction="rtl"
      class="demo-drawer"
  >
    <div class="demo-drawer__content">
      <el-form
          ref="addJobRuleFormRef"
          label-width="90px"
          :model="addJobForm"
          :rules="addJobFormRules"
          status-icon
      >
        <el-form-item label="名称" prop="jobName">
          <el-input
              v-model="addJobForm.jobName"
              maxlength="20"
              show-word-limit
              type="text"
          />
        </el-form-item>
        <el-form-item label="截止日期" prop="endTime">
          <el-date-picker
              v-model="addJobForm.endTime"
              type="datetime"
              placeholder="Pick a date"
              style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="AI批阅" prop="aiReview">
          <el-switch v-model="addJobForm.aiReview"/>
        </el-form-item>

        <el-form-item label="内容">
          <div style="border: 1px solid #ccc;">
            <Toolbar
                style="border-bottom: 1px solid #ccc"
                :editor="editorRef"
                :defaultConfig="toolbarConfig"
                mode="default"
            />
            <Editor
                style="height: 500px; overflow-y: hidden;"
                v-model="valueHtml"
                :defaultConfig="editorConfig"
                mode="default"
                @onCreated="handleCreated"
            />
          </div>
        </el-form-item>


        <el-form-item label="附件" prop="">
          <el-upload
              v-model:file-list="fileList"
              class="upload-demo"
              action="http://127.0.0.1:8081/upload/file"
              :limit="1"
              :on-success="handleUploadFileSuccess"
              :on-error="handleUploadFileErr"
              :before-upload="beforeUploadFile"
          >
            <el-button type="primary">Click to upload</el-button>
            <template #tip>
              <div class="el-upload__tip">
                文件大小不超过100MB, 大文件建议压缩后上传
              </div>
            </template>
          </el-upload>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="addJob(addJobRuleFormRef)" :loading="publishLoading">发布</el-button>
          <el-button @click="addJobVisible = false">取消</el-button>
        </el-form-item>
      </el-form>
    </div>
  </el-drawer>

  <el-drawer
      ref="updateJobDrawerRef"
      v-model="updateJobVisible"
      title="修改作业"
      direction="rtl"
      class="demo-drawer"
  >
    <div class="demo-drawer__content">
      <el-form
          ref="updateJobRuleFormRef"
          label-width="90px"
          :model="updateJobForm"
          :rules="updateJobFormRules"
          status-icon
      >
        <el-form-item label="名称" prop="jobName">
          <el-input
              v-model="updateJobForm.jobName"
              maxlength="20"
              show-word-limit
              type="text"
          />
        </el-form-item>
        <el-form-item label="截止日期" prop="endTime">
          <el-date-picker
              v-model="updateJobForm.endTime"
              type="datetime"
              placeholder="Pick a date"
              style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="AI批阅" prop="aiReview">
          <el-switch v-model="updateJobForm.aiReview"/>
        </el-form-item>

        <el-form-item label="内容">
          <div style="border: 1px solid #ccc;">
            <Toolbar
                style="border-bottom: 1px solid #ccc"
                :editor="editorRef"
                :defaultConfig="toolbarConfig"
                mode="default"
            />
            <Editor
                style="height: 500px; overflow-y: hidden;"
                v-model="valueHtml"
                :defaultConfig="editorConfig"
                mode="default"
                @onCreated="handleCreated"
            />
          </div>
        </el-form-item>


        <el-form-item label="附件" prop="">
          <el-upload
              v-model:file-list="fileList"
              class="upload-demo"
              action="http://127.0.0.1:8081/upload/file"
              :limit="1"
              :on-success="handleUpdateFileSuccess"
              :on-error="handleUploadFileErr"
              :before-upload="beforeUploadFile"
          >
            <el-button type="primary">Click to upload</el-button>
            <template #tip>
              <div class="el-upload__tip">
                文件大小不超过100MB, 大文件建议压缩后上传
              </div>
            </template>
          </el-upload>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="updateJob(updateJobRuleFormRef)" :loading="updateLoading">修改</el-button>
          <el-button @click="updateJobVisible = false">取消</el-button>
        </el-form-item>
      </el-form>
    </div>
  </el-drawer>

  <el-drawer
      ref="jobDetailsDrawerRef"
      v-model="jobDetailsVisible"
      :title="jobDetail.name"
      direction="rtl"
      class="demo-drawer"
  >
    <div class="demo-drawer__content">
      <div class="jobDetail">
        <div>
          发布时间:&nbsp;&nbsp;&nbsp;{{ jobDetail.startTime }}
        </div>
        <div style="margin-top: 10px">
          截止时间:&nbsp;&nbsp;&nbsp;{{ jobDetail.endTime }}
        </div>
        <div style="margin-top: 10px">
          作业附件:&nbsp;&nbsp;&nbsp;<a :href="jobDetail.fileLink" v-show="jobDetail.fileLink !== ''">下载</a><span
            v-show="jobDetail.fileLink === ''">暂无附件</span>
        </div>
        <div style="margin-top: 10px">
          作业内容:
        </div>
      </div>
      <div>
        <preview-html :show-value="jobDetail.content" />
<!--        <v-md-preview-html :html="jobDetail.content" preview-class="vuepress-markdown-body"></v-md-preview-html>-->
        <el-button type="success" bg :icon="Paperclip" style="width: 150px;" @click="getUserCommitInfo">提交信息
        </el-button>
      </div>
    </div>
  </el-drawer>
</template>

<script lang="ts" setup>
import {computed, onBeforeUnmount, reactive, ref, shallowRef, toRefs} from 'vue'
import '@wangeditor/editor/dist/css/style.css' // 引入 css
import {Plus, Paperclip} from '@element-plus/icons-vue'
import {Editor, Toolbar} from '@wangeditor/editor-for-vue'
import {ElDrawer, ElMessage, ElMessageBox, FormInstance, FormRules, UploadProps, UploadUserFile} from "element-plus";
import {getData, postData} from "~/api/main";
import router from "~/router";


interface Props {
  jobList: Array<any>
  jobListLoading: boolean
  courseId: any
}

const props = defineProps<Props>()
const emit = defineEmits(['getJobList'])


const {jobList, jobListLoading, courseId} = toRefs(props)


const getUserCommitInfo = () => {
  router.push({
    path: "/course/job/commit/admin",
    query: {
      courseId: courseId.value,
      courseJobId: jobDetail.id
    }
  })
}


/* --------------------------------------------------Job Data------------------------------------------------------ */
const jobDetailsVisible = ref(false)
const jobDetailsDrawerRef = ref<InstanceType<typeof ElDrawer>>()

const jobDetail = reactive({
  id: '',
  name: '',
  content: '',
  startTime: '',
  endTime: '',
  fileLink: '',
  commitInfo: '',

})

const search = ref('')
const filterTableData = computed(() =>
    jobList.value.filter(
        (data) =>
            !search.value ||
            data.jobName.toLowerCase().includes(search.value.toLowerCase())
    )
)

const getJobDetail = (job: any) => {
  jobDetailsVisible.value = true
  jobDetail.id = job.id
  jobDetail.name = job.jobName
  jobDetail.content = job.content
  jobDetail.startTime = job.startTime
  jobDetail.endTime = job.endTime
  jobDetail.fileLink = job.jobFileLink


}
/* --------------------------------------------------update Job------------------------------------------------------ */

const updateJobVisible = ref(false)
const updateJobDrawerRef = ref<InstanceType<typeof ElDrawer>>()

const updateJobRuleFormRef = ref<FormInstance>()
const updateJobFormRules = reactive<FormRules>({
  jobName: [
    {required: true, message: '作业名不能为空', trigger: 'blur'},
    {min: 2, max: 20, message: '作业名长度为 2 ~ 20', trigger: 'blur'},
  ],
  endTime: [
    {
      type: 'date',
      required: true,
      message: '截止日期不能为空',
      trigger: 'change'
    }
  ],
})

const handleUpdateFileSuccess: UploadProps['onSuccess'] = (response) => {
  console.log(response)
  if (response?.code === 200) {
    updateJobForm.jobFileLink = response?.data
  } else {
    ElMessage.error(response?.msg)
  }
}

const updateLoading = ref(false)

const updateJobForm = reactive({
  id: '',
  jobName: '',
  content: '',
  endTime: '',
  aiReview: false,
  jobFileLink: ''
})
const editCourseJob = (job: any) => {
  updateJobForm.id = job.id
  updateJobForm.jobName = job.jobName
  updateJobForm.content = job.content
  updateJobForm.aiReview = job.aiReview
  updateJobForm.jobFileLink = job.jobFileLink

  const date = new Date(job.endTime);
  const offset = 8 * 60; // GMT+8时区偏移量（单位：分钟）
  const localTime = date.getTime();
  const utcTime = localTime + (date.getTimezoneOffset() * 60000); // 转换为UTC时间
  updateJobForm.endTime = new Date(utcTime + (offset * 60000)).toISOString()
  valueHtml.value = job.content
  fileList.value = []
  if (job.jobFileLink !== '') {
    fileList.value.push({name: job.jobFileLink})
  }
  updateJobVisible.value = true
}


const updateJob = async (formEl: FormInstance | undefined) => {
  updateLoading.value = true
  updateJobForm.content = valueHtml.value
  console.log(JSON.stringify(updateJobForm))
  if (!formEl) return
  await formEl.validate((valid, fields) => {
    if (valid) {
      // todo
      const endTime: any = updateJobForm.endTime
      const today: any = new Date(new Date(new Date().toLocaleDateString()).getTime())
      if (endTime < today) {
        ElMessage.error("作业截止时间不得早于今天")
        return
      }
      postData("course/job/update", updateJobForm).then((res: any) => {
        let {data} = res;
        console.log(data)
        if (data.code === 200) {
          ElMessage.success(data.msg)
          updateJobVisible.value = false
          emit('getJobList')
        } else {
          ElMessage.error(data.msg)
        }
      })
      updateLoading.value = false
    } else {
      console.log('error submit!', fields)
    }
  })
}


/* --------------------------------------------------add Job------------------------------------------------------ */

const fileList = ref<UploadUserFile[]>([])
const handleUploadFileErr: UploadProps['onError'] = (error) => {
  ElMessage.error(error.message)
}
const beforeUploadFile: UploadProps['beforeUpload'] = (rawFile) => {
  let size = rawFile.size / 1024 / 1024
  if (size > 100) {
    ElMessage.error('请上传大小不超过100MB的文件')
    return false
  }
  return true
}
const handleUploadFileSuccess: UploadProps['onSuccess'] = (response) => {
  console.log(response)
  if (response?.code === 200) {
    addJobForm.jobFileLink = response?.data
  } else {
    ElMessage.error(response?.msg)
  }
}


const addJobVisible = ref(false)
const addJobDrawerRef = ref<InstanceType<typeof ElDrawer>>()

const addJobRuleFormRef = ref<FormInstance>()
const addJobFormRules = reactive<FormRules>({
  jobName: [
    {required: true, message: '作业名不能为空', trigger: 'blur'},
    {min: 2, max: 20, message: '作业名长度为 2 ~ 20', trigger: 'blur'},
  ],
  endTime: [
    {
      type: 'date',
      required: true,
      message: '截止日期不能为空',
      trigger: 'change'
    }
  ],

})
const publishLoading = ref(false)

const addJobForm = reactive({
  courseId: courseId.value,
  jobName: '',
  content: '',
  endTime: '',
  aiReview: false,
  jobFileLink: ''
})
const resetForm = () => {
  addJobForm.jobName = ''
  addJobForm.content = ''
  addJobForm.endTime = ''
  addJobForm.jobFileLink = ''
  valueHtml.value = ''
  fileList.value = []
}
const addJob = async (formEl: FormInstance | undefined) => {
  publishLoading.value = true
  addJobForm.content = valueHtml.value
  console.log(JSON.stringify(addJobForm))
  if (!formEl) return
  await formEl.validate((valid, fields) => {
    if (valid) {
      // todo
      const endTime: any = addJobForm.endTime
      const today: any = new Date(new Date(new Date().toLocaleDateString()).getTime())
      if (endTime < today) {
        ElMessage.error("作业截止时间不得早于今天")
        return
      }
      postData("course/job", addJobForm).then((res: any) => {
        let {data} = res;
        console.log(data)
        if (data.code === 200) {
          ElMessage.success(data.msg)
          addJobVisible.value = false
          resetForm()
          emit('getJobList')
        } else {
          ElMessage.error(data.msg)
        }
      })
      publishLoading.value = false
    } else {
      console.log('error submit!', fields)
    }
  })
}


/* ---------------------------------------------------- del ----------------------------------------------------- */

const deleteJob = (id: any) => {
  ElMessageBox.confirm(
      '确认删除？',
      '删除作业',
      {
        confirmButtonText: '删除',
        cancelButtonText: '取消',
        type: 'warning',
        autofocus: false,
      })
      .then(() => {
        console.log(id)
        getData("course/job/del", {
          courseJobId: id
        }).then((res: any) => {
          let {data} = res;
          console.log(data)
          if (data.code === 200) {
            ElMessage.success("删除成功")
            emit('getJobList')
          } else {
            ElMessage.error(data.msg)
          }
        })
      })
      .catch(() => {
      })
}

/* ---------------------------------------------------- editor -------------------------------------------------- */
// 编辑器实例，必须用 shallowRef
const editorRef = shallowRef()
// 内容 HTML
const valueHtml = ref('')
const toolbarConfig = {
  excludeKeys: [
    'underline',
    'italic',
    'bgColor',
    'group-justify',
    'group-indent',
    'group-video',
    'fontSize',
    'fontFamily',
    'lineHeight',
    'lineHeight',
    'undo',
    'redo',
    'fullScreen',
    '|',
  ],
  // 选中 公式节点 时的悬浮菜单
  insertKeys: {
    index: 0, // 自定义
    keys: [
      'insertFormula', // “插入公式”菜单
      // 'editFormula' // “编辑公式”菜单
    ],
  },
}
const editorConfig = {
  // 选中 公式节点 时的悬浮菜单
  hoverbarKeys: {
    formula: {
      menuKeys: ['editFormula'], // “编辑公式”菜单
    },
  },
  placeholder: '请输入内容...'
}
// 组件销毁时，也及时销毁编辑器
onBeforeUnmount(() => {
  const editor = editorRef.value
  if (editor == null) return
  editor.destroy()
})
const handleCreated = (editor: any) => {
  editorRef.value = editor // 记录 editor 实例，重要！
}
/* -------------------------------------------------------------------------------------------------------------- */
</script>
<style scoped>
.jobDetail {
  color: var(--el-text-color-regular);
  font-size: 14px;
}
</style>
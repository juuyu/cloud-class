<template>
  <el-container>
    <el-header>
      <header-base
          style="margin: auto;width: 90%;height: 60px;position: fixed;z-index: 10; transform: translate(-50%,0);left:50%"/>
    </el-header>
    <el-main style="margin: 30px auto;width: 90%">
      <el-page-header
          :icon="ArrowLeft"
          @back="goBack"
          style="margin-bottom: 50px;"
      >
        <template #content>
          <span class="text-large font-600 mr-3"> 提交作业 </span>
        </template>
      </el-page-header>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-scrollbar style="border: 1px solid #dcdfe6;padding: 20px;height: calc(100vh - 300px);">
            <div>
              <div v-show="hasCourseJob">
                <el-descriptions :title="jobDetail.jobName" :column="2">
                  <el-descriptions-item label="发布时间:">{{ jobDetail.startTime }}</el-descriptions-item>
                  <el-descriptions-item label="截止时间:">{{ jobDetail.endTime }}</el-descriptions-item>
                  <el-descriptions-item label="作业附件:">
                    <a :href="jobDetail.jobFileLink"
                       v-show="jobDetail.jobFileLink !== ''">下载</a><span
                      v-show="jobDetail.jobFileLink === ''">暂无附件</span>
                  </el-descriptions-item>
                </el-descriptions>
                <div>
                  <preview-html :show-value="jobDetail.content" />
<!--                  <v-md-preview-html :html="jobDetail.content"-->
<!--                                     preview-class="vuepress-markdown-body"></v-md-preview-html>-->
                </div>
              </div>
              <el-empty description="无此作业信息" v-show="!hasCourseJob"/>
            </div>
          </el-scrollbar>
        </el-col>
        <el-col :span="12">
          <div style="height: calc(100vh - 300px);">
            <div style="border: 1px solid #dcdfe6;height: 80%">
              <Toolbar
                  style="border-bottom: 1px solid #ccc"
                  :editor="editorRef"
                  :defaultConfig="toolbarConfig"
                  mode="default"
              />
              <Editor
                  style="height: 90%; overflow-y: hidden;"
                  v-model="valueHtml"
                  :defaultConfig="editorConfig"
                  mode="default"
                  @onCreated="handleCreated"
              />
            </div>
            <el-form-item label="附件" prop="aiReview">
              <el-switch v-model="hasAttachment"/>
            </el-form-item>
            <el-form-item v-show="hasAttachment">
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
              <el-button type="primary" @click="jobSubmit" v-loading="submitLoading">提交
              </el-button>
            </el-form-item>
          </div>
        </el-col>
      </el-row>
    </el-main>
  </el-container>
</template>
<script lang="ts" setup>
import {
  ArrowLeft,
} from '@element-plus/icons-vue'
import {useRoute} from "vue-router";
import '@wangeditor/editor/dist/css/style.css' // 引入 css
import {Editor, Toolbar} from '@wangeditor/editor-for-vue'
import localCache from "~/utils/cache";
import {onBeforeUnmount, onMounted, reactive, ref, shallowRef} from "vue";
import {getData, postData} from "~/api/main";
import {ElMessage, UploadProps, UploadUserFile} from "element-plus";
import router from "~/router";

const {query} = useRoute();
const courseId = query?.courseId
const courseJobId: any = query?.courseJobId
const user = localCache.getCache("userInfo")

const courseJobLoading = ref(true)
const hasCourseJob = ref(false)
const jobDetail = ref({})

onMounted(() => {
  // 获取课程作业信息
  getCourseJobInfo()
  // 获取提交信息
  getCourseJobCommit()

})
/*-------------------------------------------------------------------------------------------------------------------*/
const goBack = () => {
  router.back()
}
/*-------------------------------------------------------------------------------------------------------------------*/
const getCourseJobInfo = () => {
  courseJobLoading.value = true
  hasCourseJob.value = false
  getData("/course/job/one", {
    courseJobId: courseJobId
  }).then((res: any) => {
    let {data} = res
    if (data.code === 200) {
      if (data.data) {
        hasCourseJob.value = true
        jobDetail.value = data.data
      }
    } else {
      ElMessage.error(data.msg)
    }
    courseJobLoading.value = false
  })
}

const jobCommitInfo: any = ref({})
const getCourseJobCommit = () => {
  getData("course/job/commit/info", {
    courseJobId: courseJobId,
    userId: user.id
  }).then((res: any) => {
    let {data} = res
    if (data.code === 200) {
      jobCommitInfo.value = data.data
      if (jobCommitInfo.value) {
        // 内容
        valueHtml.value = jobCommitInfo.value.content
        courseJobCommitParam.commitFileLink = jobCommitInfo.value.commitFileLink
        // 文件
        fileList.value = []
        if (jobCommitInfo.value.commitFileLink !== '') {
          hasAttachment.value = true
          fileList.value.push({name: jobCommitInfo.value.commitFileLink})
        }
      }
    } else {
      ElMessage.error(data.msg)
    }
  })
}

const hasAttachment = ref(false)

const courseJobCommitParam = reactive({
  courseJobCommitId: '',
  courseJobId: courseJobId,
  studentId: user.id,
  content: '',
  commitFileLink: ''
})
const submitLoading = ref(false)
const jobSubmit = () => {
  submitLoading.value = true
  if (jobCommitInfo.value) {
    // 存在提交记录
    courseJobCommitParam.courseJobCommitId = jobCommitInfo.value.id
  }
  courseJobCommitParam.content = valueHtml.value

  if (courseJobCommitParam.content === '' && courseJobCommitParam.commitFileLink === '') {
    ElMessage.error("提交内容和附件至少需要提供其中之一")
    submitLoading.value = false
    return
  }

  console.log(courseJobCommitParam)
  postData("course/job/commit", courseJobCommitParam).then((res: any) => {
    let {data} = res;
    console.log(data)
    if (data.code === 200) {
      ElMessage.success(data.msg)
      location.reload()
    } else {
      ElMessage.error(data.msg)
    }
    submitLoading.value = false
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
    courseJobCommitParam.commitFileLink = response?.data
  } else {
    ElMessage.error(response?.msg)
  }
}
</script>
<style>
</style>

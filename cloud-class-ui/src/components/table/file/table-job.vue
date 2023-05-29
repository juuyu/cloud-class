<template>
  <el-table :data="jobList" style="width: 100%" v-loading="jobListLoading">
    <el-table-column label="名称" prop="jobName"/>
    <el-table-column label="发布时间" prop="startTime"/>
    <el-table-column label="截止日期" prop="endTime"/>
    <el-table-column align="right">
      <template #default="scope">
        <el-button
            size="small"
            type="primary"
            @click="getJobDetail(scope.row)">
          详情
        </el-button>
      </template>
    </el-table-column>
  </el-table>


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
        <div>
          <preview-html :show-value="jobDetail.content"/>
          <!--          <v-md-preview-html :html="jobDetail.content" preview-class="vuepress-markdown-body"></v-md-preview-html>-->
        </div>
      </div>
      <el-collapse
          v-model="activeNames"
      >
        <el-collapse-item title="提交信息" name="1">
          <div v-loading="commitInfoLoading">
            <div class="jobDetail" v-show="jobIsCommit">
              <div>
                提交时间:&nbsp;&nbsp;&nbsp;{{ jobCommitInfo?.commitTime }}
              </div>
              <div style="margin-top: 10px">
                提交附件:&nbsp;&nbsp;&nbsp;<a :href="jobCommitInfo?.commitFileLink"
                                              v-show="jobCommitInfo?.commitFileLink !== ''">下载</a><span
                  v-show="jobCommitInfo?.commitFileLink === ''">暂无附件</span>
              </div>
              <div style="margin-top: 10px">
                提交内容:
              </div>
              <div>
                <preview-html :show-value="jobCommitInfo?.content"/>
                <!--                <v-md-preview-html :html="jobCommitInfo?.content"-->
                <!--                                   preview-class="vuepress-markdown-body"></v-md-preview-html>-->
              </div>
              <el-button type="danger" bg :icon="Edit" style="width: 150px;" @click="startCommit">重新提交</el-button>
            </div>
            <div v-show="!jobIsCommit">
              <el-empty
                  description="作业未提交"
                  :image-size="80"
              />
              <el-button type="danger" bg :icon="Edit" style="width: 100px;" @click="startCommit">提交</el-button>
            </div>
          </div>
        </el-collapse-item>
        <el-collapse-item title="作业评分" name="2">
          <div v-loading="commitInfoLoading">
            <div
                class="jobDetail"
                v-show="jobIsCheck"
            >
              <div>
                <a :href="jobCommitInfo?.summaryLink">下载</a>
              </div>
              <div style="margin-top: 10px">
                评分:&nbsp;&nbsp;&nbsp;{{ jobCommitInfo?.score }}&nbsp;/&nbsp;10
              </div>
              <div style="margin-top: 10px">
                评语:&nbsp;&nbsp;&nbsp;{{ jobCommitInfo?.comment }}
              </div>
            </div>
            <el-empty
                :image-size="80"
                :description="jobNoCheckPrompt"
                v-show="!jobIsCheck"
            />
          </div>
        </el-collapse-item>
      </el-collapse>
    </div>
  </el-drawer>

</template>

<script lang="ts" setup>
import {reactive, ref, toRefs} from 'vue'
import '@wangeditor/editor/dist/css/style.css' // 引入 css
import {ElDrawer, ElMessage} from "element-plus";
import {Edit} from "@element-plus/icons-vue";
import {getData} from "~/api/main";
import router from "~/router";


interface Props {
  jobList: Array<any>
  jobListLoading: boolean
  courseId: any
  userId: any
}

const props = defineProps<Props>()
const emit = defineEmits(['getJobList'])


const {jobList, jobListLoading, courseId, userId} = toRefs(props)
/* --------------------------------------------------Job commit------------------------------------------------------ */

const startCommit = () => {
  router.push({
    path: "/course/job/commit",
    query: {
      courseId: courseId.value,
      courseJobId: jobDetail.id
    }
  })
}

/* --------------------------------------------------Job Data------------------------------------------------------ */
const activeNames = ref(['1', '2'])
const jobDetailsVisible = ref(false)
const jobDetailsDrawerRef = ref<InstanceType<typeof ElDrawer>>()
const jobCommitInfo: any = ref({})
const commitInfoLoading = ref(true)
const jobNoCheckPrompt = ref("")
const jobIsCheck = ref(false)
const jobIsCommit = ref(false)

const jobDetail = reactive({
  id: '',
  name: '',
  content: '',
  startTime: '',
  endTime: '',
  fileLink: '',
  commitInfo: '',
})

const getJobDetail = (job: any) => {
  jobIsCheck.value = false
  jobIsCommit.value = false
  commitInfoLoading.value = true
  jobDetailsVisible.value = true


  jobDetail.id = job.id
  jobDetail.name = job.jobName
  jobDetail.content = job.content
  jobDetail.startTime = job.startTime
  jobDetail.endTime = job.endTime
  jobDetail.fileLink = job.jobFileLink

  getData("course/job/commit/info", {
    courseJobId: job.id,
    userId: userId.value
  }).then((res: any) => {
    let {data} = res
    if (data.code === 200) {
      jobCommitInfo.value = data.data
      if (jobCommitInfo.value) {
        jobIsCommit.value = true
        if (jobCommitInfo.value.status === 2) {
          jobIsCheck.value = true
        } else {
          jobNoCheckPrompt.value = "等待老师批改"
        }
      } else {
        jobNoCheckPrompt.value = "作业未提交"

      }
    } else {
      ElMessage.error(data.msg)
    }
    commitInfoLoading.value = false
  })


}

</script>
<style scoped>
.jobDetail {
  color: var(--el-text-color-regular);
  font-size: 14px;
}
</style>
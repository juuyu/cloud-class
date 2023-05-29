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
          style="margin-bottom: 50px"
      >
        <template #content>
          <span class="text-large font-600 mr-3"> 提交信息 </span>
        </template>
      </el-page-header>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-scrollbar style="border: 1px solid #dcdfe6;border-radius: 5px;padding: 20px;height: calc(100vh - 300px);">
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
                  <preview-html :show-value="jobDetail.content"/>
                  <!--                  <v-md-preview-html :html="jobDetail.content"-->
                  <!--                                     preview-class="vuepress-markdown-body"></v-md-preview-html>-->
                </div>
              </div>
              <el-empty description="无此作业信息" v-show="!hasCourseJob"/>
            </div>
          </el-scrollbar>
        </el-col>
        <el-col :span="12">
          <div style="border: 1px solid #dcdfe6;border-radius: 5px;padding: 20px;height: calc(100vh - 300px);">
            <div style="height: 60px;margin-bottom: 20px">
              <div style="display: flex;justify-content: left;align-items: center;margin-bottom: 10px">
                <div style="width: 200px">
                  提交人数:&nbsp;&nbsp;&nbsp;{{ userCommitNum?.commitNum }}&nbsp;/&nbsp;{{ userCommitNum?.totalNum }}
                </div>
                <div style="width: 400px">
                  <el-progress :percentage="userCommitNum?.commitPercent"/>
                </div>
              </div>
              <div style="display: flex;justify-content: left;align-items: center">
                <div style="width: 200px">
                  批改数量:&nbsp;&nbsp;&nbsp;{{ userCommitNum?.checkNum }}&nbsp;/&nbsp;{{ userCommitNum?.commitNum }}
                </div>
                <div style="width: 400px">
                  <el-progress :percentage="userCommitNum?.checkPercent" status="success"/>
                </div>
              </div>
            </div>
            <el-table
                :data="userCommitList"
                v-loading="userCommitListLoading"
                style="width: 100%;height:  calc(100% - 80px);"
            >
              <el-table-column label="姓名" prop="realName"/>
              <el-table-column label="账号" prop="userName"/>
              <el-table-column label="提交时间" width="200" prop="commitTime"/>
              <el-table-column
                  label="批改状态"
                  :filters="[
                      { text: '未批改', value: '未批改' },
                      { text: '人工批改', value: '人工批改' },
                      { text: 'AI批改', value: 'AI批改' }
                      ]"
                  :filter-method="filterTag"
                  filter-placement="bottom-end"
              >
                <template #default="scope">
                  <el-tag
                      :type="scope.row.checkInfoTag"
                      disable-transitions
                  >{{ scope.row.checkInfo }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column align="right">
                <template #header>
                  <el-button type="success" bg :icon="Download" @click="downloadAll" v-loading="downloadAllLoading"
                             style="width: 100px;">
                    打包下载
                  </el-button>
                </template>
                <template #default="scope">
                  <el-button
                      size="small"
                      type="primary"
                      @click="getCommitDetail(scope.row)"
                  >
                    详情
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-col>
      </el-row>
    </el-main>
  </el-container>

  <el-drawer
      ref="jobDetailsDrawerRef"
      v-model="commitDetailsVisible"
      title="提交详情"
      direction="rtl"
      class="demo-drawer"
  >
    <div class="demo-drawer__content">
      <div>
        <div class="jobDetail">
          <el-descriptions :column="2">
            <el-descriptions-item label="姓名:">{{ commitDetail?.realName }}</el-descriptions-item>
            <el-descriptions-item label="账号:">{{ commitDetail?.userName }}</el-descriptions-item>
            <el-descriptions-item label="时间:">{{ commitDetail?.commitTime }}</el-descriptions-item>
            <el-descriptions-item label="附件:">
              <a :href="commitDetail?.courseJobCommitDto?.commitFileLink"
                 v-show="commitDetail?.courseJobCommitDto?.commitFileLink !== ''">下载</a><span
                v-show="commitDetail?.courseJobCommitDto?.commitFileLink === ''">暂无附件</span>
            </el-descriptions-item>
          </el-descriptions>
          <div>
            <preview-html :show-value="commitDetail?.courseJobCommitDto?.content"/>
            <!--            <v-md-preview-html :html="commitDetail?.courseJobCommitDto?.content"-->
            <!--                               preview-class="vuepress-markdown-body"></v-md-preview-html>-->
          </div>
          <el-switch
              v-model="checkJob"
              active-text="批改"
          />
          <el-divider/>
          <div
              class="jobDetail"
              v-show="!checkJob"
          >
            <div>
              <a :href="commitDetail?.courseJobCommitDto?.summaryLink">下载</a>
            </div>
            <div style="margin-top: 10px">
              评分:&nbsp;&nbsp;&nbsp;{{ commitDetail?.courseJobCommitDto?.score }}&nbsp;/&nbsp;10
            </div>
            <div style="margin-top: 10px">
              评语:&nbsp;&nbsp;&nbsp;{{ commitDetail?.courseJobCommitDto?.comment }}
            </div>
          </div>
          <div v-show="checkJob">
            <el-form label-width="60">
              <el-form-item label="评分:">
                <el-slider
                    v-model="courseJobCheckParam.score"
                    :step="1"
                    max="10"
                    show-stops
                />
              </el-form-item>
              <el-form-item label="评语:">
                <el-input
                    v-model="courseJobCheckParam.comment"
                    maxlength="200"
                    placeholder="Please input"
                    show-word-limit
                    type="textarea"
                    :autosize="{ minRows: 2, maxRows: 6 }"
                />
              </el-form-item>
              <el-form-item>
                <el-button
                    @click="check"
                    v-loading="checkLoading"
                    type="primary"
                    style="width: 100px"
                >提交
                </el-button>
              </el-form-item>
            </el-form>
          </div>

        </div>
      </div>
    </div>
  </el-drawer>

</template>
<script lang="ts" setup>
import {
  ArrowLeft,
  Download,
  Edit
} from '@element-plus/icons-vue'
import {useRoute} from "vue-router";
import localCache from "~/utils/cache";
import {onMounted, reactive, ref} from "vue";
import {getData, postData} from "~/api/main";
import {ElMessage} from "element-plus";
import router from "~/router";

const {query} = useRoute();
const courseId = query?.courseId
const courseJobId: any = query?.courseJobId
const user = localCache.getCache("userInfo")

onMounted(() => {
  // 获取课程作业信息
  getCourseJobInfo()

  getUserCommitNum()

  getUserCommitList()

})

/*-------------------------------------------------------------------------------------------------------------------*/
const checkJob = ref(false)
const checkLoading = ref(false)
const courseJobCheckParam = reactive({
  courseJobCommitId: '',
  score: 0,
  comment: '',
})

const check = () => {
  checkLoading.value = true
  if (courseJobCheckParam.courseJobCommitId === '' || courseJobCheckParam.courseJobCommitId == null) {
    ElMessage.error("未选中提交信息")
    checkLoading.value = false
    return
  }
  postData("course/job/commit/check", courseJobCheckParam).then((res: any) => {
    let {data} = res;
    checkLoading.value = false
    if (data.code === 200) {
      ElMessage.success(data.msg)
      location.reload()
    } else {
      ElMessage.error(data.msg)
    }
  })
}

/*-------------------------------------------------------------------------------------------------------------------*/
const goBack = () => {
  router.back()
}
/*-------------------------------------------------------------------------------------------------------------------*/
const downloadAllLoading = ref(false)

const downloadAll = () => {
  downloadAllLoading.value = true
  const url = 'http://localhost:8081/course/job/commit/download/all?courseJobId=' + courseJobId;
  window.open(url, '_blank');
  downloadAllLoading.value = false
}

/*-------------------------------------------------------------------------------------------------------------------*/
const commitDetailsVisible = ref(false)
const commitDetail = ref({})
const getCommitDetail = (commit: any) => {
  commitDetail.value = commit
  commitDetailsVisible.value = true

  courseJobCheckParam.courseJobCommitId = commit.courseJobCommitDto.id
  courseJobCheckParam.score = commit.courseJobCommitDto.score
  courseJobCheckParam.comment = commit.courseJobCommitDto.comment

}


/*-------------------------------------------------------------------------------------------------------------------*/
const userCommitList = ref([])
const userCommitListLoading = ref(true)


const getUserCommitList = () => {
  getData("/course/job/commit/list", {
    courseJobId: courseJobId
  }).then((res: any) => {
    let {data} = res
    if (data.code === 200) {
      userCommitList.value = data.data
    } else {
      ElMessage.error(data.msg)
    }
    userCommitListLoading.value = false
  })
}

const filterTag = (value: string, row: any) => {
  return row.checkInfo === value
}

/*-------------------------------------------------------------------------------------------------------------------*/
const userCommitNum = ref({})

const getUserCommitNum = () => {
  getData("/course/job/commit/num", {
    courseJobId: courseJobId
  }).then((res: any) => {
    let {data} = res
    if (data.code === 200) {
      if (data.data) {
        userCommitNum.value = data.data
      }
    } else {
      ElMessage.error(data.msg)
    }
  })

}


/*-------------------------------------------------------------------------------------------------------------------*/
const courseJobLoading = ref(true)
const hasCourseJob = ref(false)
const jobDetail = ref({})
// getCourseInfo
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
</script>
<style scoped>
.jobDetail {
  color: var(--el-text-color-regular);
  font-size: 14px;
}
</style>

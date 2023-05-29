<template>
  <div>
    <el-upload
        class="upload-demo"
        drag
        multiple
        action="http://127.0.0.1:8081/upload/file"
        :on-success="handleUploadFileSuccess"
        :on-error="handleUploadFileErr"
        :before-upload="beforeUploadFile"
    >
      <el-icon class="el-icon-upload">
        <upload-filled/>
      </el-icon>
      <div class="el-upload-text">
        Drop file here or <em>click to upload</em>
      </div>
      <template #tip>
        <div class="el-upload-tip">
          文件大小不超过1GB, 大文件建议压缩后上传
        </div>
      </template>
    </el-upload>
  </div>
  <div class="flex items-center" style="justify-content: space-between;margin-top: 20px;margin-bottom: 20px">
    <div class="flex items-center" style="width: 30%;">
      <el-input
          v-model="searchParam"
          placeholder="搜索"
          style="width: 100%"
          class="input-with-select"
          :prefix-icon="Search"
      >
      </el-input>
    </div>
  </div>
  <div v-loading="fileListLoading">
    <el-empty description="暂无课程资料" v-show="fileListFilter.length === 0"/>
    <el-scrollbar height="300px" v-show="fileListFilter.length !== 0"
                  style="border: 1px solid #dcdfe6;border-radius: 5px;padding: 20px;">
      <div v-for="item in fileListFilter" class="file flex items-center">
        <div class="file-info flex items-center">
          <div class="flex items-center" style="border-radius: 12px">
            <div class="file-img flex items-center">
              <img alt="" :src="item?.cover"/>
            </div>
            <div>
              <div class="file-name">
                {{ item?.fileName }}
              </div>
              <div class="file-content">
                <span>{{ item?.size }}</span>
                <span>{{ item?.uploadUserName }}</span>
                <span>{{ item?.uploadTime }}</span>
              </div>
            </div>
          </div>
          <div class="flex" style="align-items: center;justify-content: space-between">
            <el-tooltip
                content="下载"
                effect="light">
              <a :href="item?.fileLink">
                <img alt="" src="src/assets/file/download.svg"/>
              </a>
            </el-tooltip>
            <el-tooltip
                content="删除"
                effect="light">
              <div style="width: 50px;cursor: pointer" @click="deleteFile(item?.id)">
                <img alt="" src="src/assets/tools/delete.svg" style="width: 30px;height: 30px"/>
              </div>
            </el-tooltip>
          </div>
        </div>
      </div>
    </el-scrollbar>
  </div>
  <el-dialog v-model="addFileFormVisible" title="上传课程资料" style="width: 500px;">
    <div style="width: 100%;margin-top: 20px;">
      <el-form
          ref="uploadCourseFileRuleFormRef"
          :model="uploadCourseFile"
          label-width="70px"
          :rules="uploadCourseFileRules"
          status-icon
          style="width: 100%"
      >
        <el-form-item label="文件名" prop="fileName">
          <el-input
              v-model="uploadCourseFile.fileName"
              type="text"
          />
        </el-form-item>
        <el-form-item label="文件简介" prop="intro">
          <el-input
              type="textarea"
              v-model="uploadCourseFile.fileIntro"
              maxlength="200"
              show-word-limit
          />
        </el-form-item>
      </el-form>
    </div>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="addFileFormVisible = false">取消</el-button>
        <el-button type="primary" @click="addCourseFile(uploadCourseFileRuleFormRef)"
                   :loading="uploadLoading">上传</el-button>
      </span>
    </template>
  </el-dialog>
</template>
<script lang="ts" setup>
import {computed, reactive, ref, toRefs} from "vue";
import {UploadFilled, Search} from '@element-plus/icons-vue'
import {ElMessage, ElMessageBox, FormInstance, FormRules, UploadProps} from "element-plus";
import {getData, postData} from "~/api/main";

interface Props {
  dataList: Array<any>
  fileListLoading: boolean
  courseId: any
}

const props = defineProps<Props>()
const emit = defineEmits(['getCourseFile'])


const {dataList, fileListLoading, courseId} = toRefs(props)

const searchParam = ref('')
const fileListFilter = computed(() =>
    dataList.value.filter(
        (data) =>
            !searchParam.value ||
            data.fileName.toLowerCase().includes(searchParam.value.toLowerCase())
    )
)

const uploadLoading = ref(false)
const uploadCourseFileRuleFormRef = ref<FormInstance>()
const addFileFormVisible = ref(false)
const uploadCourseFile = reactive({
  courseId: courseId.value,
  trueFileName: '',
  fileName: '',
  size: '',
  fileIntro: '',
  fileLink: ''
})
const addCourseFile = async (formEl: FormInstance | undefined) => {
  if (!formEl) return
  await formEl.validate((valid, fields) => {
    if (valid) {
      uploadLoading.value = true
      postData("course/file", uploadCourseFile).then((res: any) => {
        let {data} = res;
        if (data.code === 200) {
          ElMessage.success("上传成功")
          addFileFormVisible.value = false
          emit('getCourseFile')
        } else {
          ElMessage.error(data.msg)
        }
      })
      uploadLoading.value = false
    } else {
      console.log('error submit!', fields)
    }
  })
}

const handleUploadFileErr: UploadProps['onError'] = (error) => {
  ElMessage.error(error.message)
}
const beforeUploadFile: UploadProps['beforeUpload'] = (rawFile) => {
  let size = rawFile.size / 1024 / 1024
  if (size > 1024) {
    ElMessage.error('请上传大小不超过1GB的文件')
    return false
  }
  uploadCourseFile.trueFileName = rawFile.name
  uploadCourseFile.fileName = rawFile.name
  uploadCourseFile.size = size.toFixed(2) + "MB"
  return true
}


const handleUploadFileSuccess: UploadProps['onSuccess'] = (response) => {
  console.log(response)
  if (response?.code === 200) {
    uploadCourseFile.fileLink = response?.data
    addFileFormVisible.value = true
  } else {
    ElMessage.error(response?.msg)
  }
}

const deleteFile = (id: any) => {
  ElMessageBox.confirm(
      '确认删除？',
      '删除文件',
      {
        confirmButtonText: '删除',
        cancelButtonText: '取消',
        type: 'warning',
        autofocus: false,
      })
      .then(() => {
        console.log(id)
        getData("course/file/del", {
          courseFileId: id
        }).then((res: any) => {
          let {data} = res;
          console.log(data)
          if (data.code === 200) {
            ElMessage.success("删除成功")
            emit('getCourseFile')
          } else {
            ElMessage.error(data.msg)
          }
        })
      })
      .catch(() => {
      })
}


const uploadCourseFileRules = reactive<FormRules>({
  fileName: [
    {required: true, message: '文件名不能为空', trigger: 'blur'},
    {min: 2, max: 50, message: '文件名长度为 2 ~ 50', trigger: 'blur'},
  ],
  fileIntro: [
    {min: 5, max: 200, message: '课程介绍长度为 5 ~ 200', trigger: 'blur'},
  ]
})
</script>
<style scoped>
.el-icon-upload {
  font-size: 67px;
  color: var(--el-text-color-placeholder);
  margin-bottom: 16px;
  line-height: 50px;
}

.el-upload-text {
  color: var(--el-text-color-regular);
  font-size: 14px;
  text-align: center;
}

.el-upload-tip {
  font-size: 12px;
  color: var(--el-text-color-regular);
  margin-top: 7px;
}

.file {
  width: 100%;
  height: 70px;
  border-radius: 12px;
}

.file :hover {
  background-color: #EBEDF0;
}

.file-info {
  border-radius: 12px;
  width: 100%;
  justify-content: space-between;
}


.file-info a img {
  height: 20px;
  width: 20px;
  margin-right: 20px
}

.file-img {
  width: 50px;
  height: 50px;
  margin: 10px;
  border-radius: 12px;
  border: 1px solid #dcdfe6;
}

.file-img img {
  height: 30px;
  width: 30px;
  margin: 0 auto
}

.file-name {
  margin-left: 20px;
  font-size: 16px;
  font-weight: bolder;
  color: #303133;
}

.file-content {
  line-height: 26px;
  font-size: 13px;
  color: #606266;
}

.file-content span {
  margin-left: 20px;
}
</style>

<template>
  <el-container>
    <el-header>
      <header-base
          style="margin: auto;width: 90%;height: 60px;  position: fixed;z-index: 10; transform: translate(-50%,0);left:50%"/>
    </el-header>
    <el-main style="margin: 30px auto;width: 65%">
      <div>
        <el-upload
            action="http://127.0.0.1:8081/upload/img"
            :limit="1"
            :on-success="handleUploadAvatarSuccess"
            :on-error="handleUploadAvatarErr"
            :before-upload="beforeUploadAvatar"
        >
          <el-tooltip content="修改头像"
                      effect="light"
                      placement="right"
          >
            <el-avatar :size="145"
                       class="mr-3"
                       :src="user?.avatar"
                       style="cursor: pointer;"
                       v-loading="avatarLoading"
            />
          </el-tooltip>
        </el-upload>
      </div>
      <el-divider/>
      <div>
        <el-descriptions title="个人信息" :column="2">
          <template #extra>
            <el-button
                type="primary"
                plain
                @click="dialogFormVisible = true"
            >修改
            </el-button>
          </template>
          <el-descriptions-item label="姓名:">{{ user?.realName }}</el-descriptions-item>
          <el-descriptions-item label="帐号:">{{ user?.userName }}</el-descriptions-item>
          <el-descriptions-item label="年龄:">{{ user?.age }}</el-descriptions-item>
          <el-descriptions-item label="电话:">{{ user?.tel }}</el-descriptions-item>
          <el-descriptions-item label="性别:">
            <span v-show="user?.sex === 1">未知</span>
            <span v-show="user?.sex === 2">男</span>
            <span v-show="user?.sex === 3">女</span>
          </el-descriptions-item>
          <el-descriptions-item label="邮箱:">{{ user?.email }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-main>
    <el-drawer
        v-model="dialogFormVisible"
        title="修改信息"
        direction="rtl"
    >
      <div class="demo-drawer__content">
        <el-form
            label-width="120px"
            status-icon
        >
          <el-form-item label="账号">
            <el-input
                v-model="user.userName"
                type="text"
                disabled
            />
          </el-form-item>
          <el-form-item label="邮箱">
            <el-input
                v-model="user.email"
                type="text"
                disabled
            />
          </el-form-item>
          <el-form-item label="姓名">
            <el-input
                v-model="updateUserInfo.realName"
                type="text"
            />
          </el-form-item>
          <el-form-item label="年龄">
            <el-input-number
                :min="1"
                :max="200"
                v-model="updateUserInfo.age"
            />
          </el-form-item>
          <el-form-item label="性别">
            <el-select
                v-model="updateUserInfo.sex"
            >
              <el-option label="男" value="2"/>
              <el-option label="女" value="3"/>
              <el-option label="未知" value="1"/>
            </el-select>
          </el-form-item>
          <el-form-item label="手机号">
            <el-input
                v-model="updateUserInfo.tel"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="updateUser">修改</el-button>
            <el-button @click="dialogFormVisible = false">取消</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-drawer>
  </el-container>
</template>
<script lang="ts" setup>
import localCache from "~/utils/cache";
import {ElMessage, UploadProps} from "element-plus";
import {reactive, ref} from "vue";
import {postData} from "~/api/main";

const user = localCache.getCache("userInfo")


/*-------------------------------------------------------------------------------------------------------------------*/
const dialogFormVisible = ref(false)
const updateUserInfo = reactive({
  userId: user?.id,
  realName: user?.realName,
  age: user?.age,
  sex: user?.sex.toString(),
  tel: user?.tel
})
const updateUser = () => {
  console.log(JSON.stringify(updateUserInfo))
  if (updateUserInfo.realName === ""){
    ElMessage.warning("姓名不能为空")
    return
  }
  if (updateUserInfo.age === ""){
    ElMessage.warning("年龄不能为空")
    return
  }
  postData("user/update", updateUserInfo).then((res: any) => {
    let {data} = res;
    console.log(data)
    if (data.code === 200) {
      dialogFormVisible.value = false
      ElMessage.success(data.msg)
      localCache.setCache("userInfo", data.data)
      location.reload()
    } else {
      ElMessage.error(data.msg)
    }
  })

}
/*-------------------------------------------------------------------------------------------------------------------*/
const avatarLoading = ref(false)
const handleUploadAvatarErr: UploadProps['onError'] = (error) => {
  ElMessage.error(error.message)
}
const beforeUploadAvatar: UploadProps['beforeUpload'] = (rawFile) => {
  avatarLoading.value = true
  if (rawFile.type !== 'image/jpeg' && rawFile.type !== 'image/png') {
    ElMessage.error('请上传符合格式的 png/jpg 类型的图片')
    avatarLoading.value = false
    return false
  } else if (rawFile.size / 1024 / 1024 > 5) {
    ElMessage.error('请上传大小不超过5MB的图片')
    avatarLoading.value = false
    return false
  }
  return true
}
const handleUploadAvatarSuccess: UploadProps['onSuccess'] = (response) => {
  console.log(response)
  if (response.code === 200) {
    let param = new FormData();
    param.append("userId", user?.id)
    param.append("avatarUrl", response.data)
    postData("user/avatar", param).then((res: any) => {
      let {data} = res;
      console.log(data)
      if (data.code === 200) {
        ElMessage.success(data.msg)
        localCache.setCache("userInfo", data.data)
        location.reload()
      } else {
        ElMessage.error(data.msg)
      }
    })
    ElMessage.success(response.msg)
  } else {
    ElMessage.error(response.msg)
  }
  avatarLoading.value = false
}


</script>
<style scoped>

</style>
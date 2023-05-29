<template>
  <el-container>
    <el-header>
      <header-login
          style="margin: auto;width: 90%;height: 60px;  position: fixed;z-index: 10; transform: translate(-50%,0);left:50%"/>
    </el-header>
    <el-main style="margin: 30px auto;width: 65%">
      <el-page-header
          :icon="ArrowLeft"
          @back="goBack"
          style="margin-bottom: 50px;"
      >
        <template #content>
          <span class="text-large font-600 mr-3"> 注册账号 </span>
        </template>
      </el-page-header>
      <div v-if="registerStatus">
        <el-result
            icon="success"
            title="注册成功"
        >
          <template #extra>
            <el-button type="primary" @click="toLogin">登录</el-button>
          </template>
        </el-result>
      </div>
      <div
          v-else
      >
        <el-row :gutter="20">
          <el-col :span="16">
            <el-form
                label-width="120px"
                ref="regFormRef"
                :model="regFormParam"
                :rules="regRules"
                status-icon
            >
              <el-form-item label="账号" prop="userName">
                <el-input
                    v-model="regFormParam.userName"
                    maxlength="10"
                    show-word-limit
                    type="text"
                />
              </el-form-item>
              <el-form-item label="邮箱" prop="email">
                <el-input
                    v-model="regFormParam.email"
                    autocomplete="off"
                    type="text"
                />
              </el-form-item>
              <el-form-item label="验证码" prop="emailCode">
                <template #default>
                  <div style="width: 100%;display: flex;justify-items: center;justify-content: space-between">
                    <div style="width: 200px">
                      <el-input
                          v-model="regFormParam.emailCode"
                          type="text"
                      />
                    </div>
                    <countdown-btn
                        @click="sendMailCode"
                        ref="countdownBtnRef"
                        type="warning"
                        title="获取验证码"
                        :sec=5
                    />
                  </div>
                </template>
              </el-form-item>
              <el-form-item label="密码" prop="password">
                <el-input
                    v-model="regFormParam.password"
                    type="password"
                    autocomplete="new-password"
                    placeholder="请输入密码"
                />
              </el-form-item>
              <el-form-item label="确认密码" prop="confirmPassword">
                <el-input
                    v-model="regFormParam.confirmPassword"
                    type="password"
                    autocomplete="new-password"
                    placeholder="请输入密码"
                />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="userRegister(regFormRef)">注册</el-button>
                <el-button @click="resetForm(regFormRef)">重置</el-button>
              </el-form-item>
            </el-form>
          </el-col>
          <el-col :span="8">
          </el-col>
        </el-row>
      </div>
    </el-main>
  </el-container>
</template>
<script lang="ts" setup>

import {getData, postData} from "~/api/main";
import {reactive, ref} from "vue";
import {ElMessage, FormInstance, FormRules} from "element-plus";
import {ArrowLeft} from "@element-plus/icons-vue";
import router from "~/router";
import {generateCourseSchedule} from "~/utils/courseArrangeUtil";

const registerStatus = ref(false)
/*-------------------------------------------------------------------------------------------------------------------*/
const regFormParam = reactive({
  userName: "",
  email: "",
  emailCode: "",
  password: "",
  confirmPassword: ""
})

const validateUserName = (rule: any, value: any, callback: any) => {
  getData("user/userName", {
    userName: regFormParam.userName
  }).then((res: any) => {
    let {data} = res;
    if (data.data) {
      callback(new Error("该账号已存在"))
    } else {
      callback()
    }
  })
}
const validateEmail = (rule: any, value: any, callback: any) => {
  const regEmail = /^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/
  if (!regEmail.test(value)) {
    callback(new Error('请输入合法的邮箱'))
  } else {
    callback()
  }
}
const validatePass = (rule: any, value: any, callback: any) => {
  if (value !== regFormParam.password) {
    callback(new Error("两次输入密码不一致"))
  } else {
    callback()
  }
}
const regFormRef = ref<FormInstance>()
const regRules = reactive<FormRules>({
  userName: [
    {required: true, message: '用户名不能为空', trigger: 'blur'},
    {min: 5, max: 10, message: '用户名长度为 5 ~ 10', trigger: 'blur'},
    {validator: validateUserName, trigger: 'blur'}
  ],
  email: [
    {required: true, message: '邮箱不能为空', trigger: 'blur'},
    {validator: validateEmail, trigger: 'blur'}
  ],
  emailCode: [
    {required: true, message: '验证码不能为空', trigger: 'blur'}
  ],
  password: [
    {required: true, message: '密码不能为空', trigger: 'blur'},
    {min: 5, max: 15, message: '密码长度为 5 ~ 15', trigger: 'blur'},
  ],
  confirmPassword: [
    {validator: validatePass, trigger: 'blur'}
  ]
})
const userRegisterParam = reactive({
  userName: "",
  password: "",
  email: "",
  emailCode: ""
})
const userRegister = async (formEl: FormInstance | undefined) => {
  if (!formEl) return
  await formEl.validate((valid, fields) => {
    if (valid) {
      userRegisterParam.userName = regFormParam.userName
      userRegisterParam.password = regFormParam.password
      userRegisterParam.email = regFormParam.email
      userRegisterParam.emailCode = regFormParam.emailCode

      postData("register", userRegisterParam).then((res: any) => {
        let {data} = res;
        console.log(data)
        if (data.code === 200) {
          ElMessage.success(data.msg)
          registerStatus.value = true
        } else {
          ElMessage.error(data.msg)
        }
      })
    } else {
      console.log('error submit!', fields)
    }
  })

}
const resetForm = (formEl: FormInstance | undefined) => {
  if (!formEl) return
  formEl.resetFields()
}
/*-------------------------------------------------------------------------------------------------------------------*/
const countdownBtnRef = ref()
const sendMailCode = () => {
  if (regFormParam.email === "") {
    ElMessage.error("请输入邮箱")
    return
  }
  countdownBtnRef.value.clickBtn()
  getData("mail/registerCode", {
    mail: regFormParam.email
  }).then((res: any) => {
    let {data} = res;
    console.log(data)
    if (data.code === 200) {
      ElMessage.success("发送成功")
    } else {
      ElMessage.error(data.msg)
    }
  })

}

const toLogin = () => {
  router.push({
    path: "/login"
  })
}
const goBack = () => {
  router.back()
}
</script>
<style scoped>

</style>
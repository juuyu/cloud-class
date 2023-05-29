<template>

  <el-menu mode="horizontal" :ellipsis="false" router>
    <div class="flex items-center">
      <span>
        <img alt="Cloud-Class"
             style="height: 55px;"
             src="src/assets/logo.png"/>
      </span>
    </div>
<!--    <el-menu-item index="1">首页</el-menu-item>-->
<!--    <el-menu-item index="2">公开课</el-menu-item>-->
<!--    <el-menu-item index="3">视频</el-menu-item>-->
<!--    <div class="flex-grow"/>-->
<!--    <div class="flex items-center">-->
<!--      <el-button type="primary" @click="loginFormVisible = true">登录</el-button>-->
<!--    </div>-->
  </el-menu>


<!--  <el-dialog v-model="loginFormVisible" title="" center style="height: 300px;width: 600px;">-->
    <!--    <div style="float: left;margin: 0 20px 30px 30px;width: 30%;height: 100%;">-->
    <!--      <div style="width: 100%; text-align: center;font-size: 18px;font-weight:bold;">-->
    <!--        扫码登录-->
    <!--      </div>-->
    <!--      <div style="width: 100%;margin-top: 20px; text-align: center;">-->
    <!--        <svg t="1676101639677" class="icon" viewBox="0 0 1024 1024" version="1.1"-->
    <!--             xmlns="http://www.w3.org/2000/svg" p-id="15486" id="mx_n_1676101639678" width="100px"-->
    <!--             height="100px">-->
    <!--          <path-->
    <!--              d="M896 992h-224v-64h224a32 32 0 0 0 32-32v-224h64v224a96 96 0 0 1-96 96z m32-864a32 32 0 0 0-32-32h-224V32h224a96 96 0 0 1 96 96v224h-64V128zM96 128v224H32V128a96 96 0 0 1 96-96h224v64H128a32 32 0 0 0-32 32z m896 416H32v-64h960v64zM96 896a32 32 0 0 0 32 32h224v64H128a96 96 0 0 1-96-96v-224h64v224z"-->
    <!--              fill="#409EFF" p-id="15487" data-spm-anchor-id="a313x.7781069.0.i11" class=""></path>-->
    <!--        </svg>-->
    <!--      </div>-->

    <!--    </div>-->
    <!--    <el-divider direction="vertical" style="margin-top: 30px; height: 260px;"/>-->
    <!--    <div style="float: right;margin: 0 30px 30px 0;width: 50%;height: 100%;">-->
<!--    <div style="display: flex;justify-content: center">-->
<!--      <div style="width: 70%">-->
<!--        <div style="width: 100%; text-align: center;font-size: 18px;font-weight:bold;">-->
<!--          密码登录-->
<!--        </div>-->

<!--        <el-form style="width: 100%;margin-top: 20px;">-->
<!--          <el-form-item label="账号">-->
<!--            <el-input type="text" autocomplete="off" maxlength="32" placeholder="请输入账号"-->
<!--                      v-model="form.username"/>-->
<!--          </el-form-item>-->

<!--          <el-form-item label="密码">-->
<!--            <el-input type="password" autocomplete="off" placeholder="请输入密码"-->
<!--                      show-password v-model="form.password"/>-->
<!--          </el-form-item>-->
<!--        </el-form>-->

<!--        <div style="margin-left: 40px;">-->
<!--          <div style="width: 100%;height: 50px;">-->
<!--            <div style="float: left;">-->
<!--              <el-button @click="toRegister">-->
<!--                注册-->
<!--              </el-button>-->
<!--            </div>-->
<!--            <div style="float: right;">-->
<!--              <el-button type="primary" @click="login">-->
<!--                登录-->
<!--              </el-button>-->
<!--            </div>-->
<!--          </div>-->
<!--        </div>-->

        <!--        <div style="margin-top: 20px;margin-bottom: 14px;  width: 100%;text-align: center;font-size: 14px;">-->
        <!--          其它方式登录-->
        <!--        </div>-->

        <!--        <div style="margin: 0 0;display: flex;align-items: center;justify-content: center;">-->
        <!--                        <span>-->
        <!--                            <img-->
        <!--                                src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADgAAAA4CAMAAACfWMssAAAAgVBMVEUAAABXu0BYt0BQt0BXu0BWu0BXu0BXu0BXu0BWukBWukBXvEBXu0BXvEBXvEBYukBVukBXu0BWt0BXukBXuEBXu0D////1+/Pq9+fV7s/A5rer3aBsw1jg89uBzHBiwEy14auL0Xug2ZOW1YeBzG93yGSW1YjL6sO14azL6cN2yGP3XpzOAAAAFXRSTlMA3yAQ78+/r5+AUI9w74BgYEBAkHDBb56KAAACF0lEQVRIx52W6XKDIBRGwT3GZmsRUXFP0vb9H7AKGS8aiCXnR0TCmU/gOoh0uJck8jEZwUGYXND/cOPggyz4CE//0HZgKeDIsdSA3Qs1Bk2XejLF7ckGe1fnOT7ZBDsaDxNiZ4Jna4Jnb7rgbeKrK7QnFuzBOxIrYrsJAth9iIdl/9CwLE0pv/elqfoegWpfXdAUYINW9GRkRIBWakBemiOVGRbpE1lpijwaPDANCxvCc8qBbcVF47vq5EQ1YjCK3nyXiXE3QqrpSseeu+jptc96XgWmHSGDEGtCmDHygpK5nUuRdr2MvvfNdMvzXCN+KVNk6RO0qOpr37fXJzFCwdzmT9532THZovmqFHxlF3/WcdWQGUsBI2g3K/G3WG3o4oEVsVqOK4RHaTpfaKkXCVsWzPTL65pPN7X4kxnEX6qIXS4mJqfOH5tVKSJWzJsiXqlcklxe5AI0yuL4RDUpiKkGphRrRFRK+lLk88AQSg4KXVC9TvwSRQ4MU5m1xZ2xlmnEm1LkrqeKTVbU5rcaNtJDCAWqCOutq90CpjiKMTFQZuuah/9Oo+h6ZtPkYTSxI0YKReWLtxFBpJ5bzjOasWYsoBp6HQSRW5R5tz4C4HS0PltjO/H05sH6iQDXtz0d3/94ANPes/9Asjd9572PwE8X6Tm+DPViZMQ5mLUDxGnVCFtqwDH0VlYQS22bcxIGIhn7UXLWWn+10s6FZo+4YQAAAABJRU5ErkJggg=="-->
        <!--                                class="login-sns-item-icon" style="height: 30px;" alt=""/>-->
        <!--                        </span>-->
        <!--          <span style="margin-left: 10px;">-->
        <!--                            微信登录-->
        <!--                        </span>-->
        <!--          <span style="margin-left: 30px;">-->
        <!--                            <img-->
        <!--                                src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADgAAAA4CAMAAACfWMssAAAAb1BMVEUAAABMouVIn+NEn99MouVMouVMo+ZMouVKouRKouJMn+NMouZLouRLo+VMoeNMouZLoeVLoeVNn+RMouX///+83Pal0PL0+v6x1vRireiay/FXqOfS6Pl5uevH4vePxe+Ev+1utOrp9Pzp8/yEv+7XzqLPAAAAE3RSTlMA3yAQ74C/n2BQQK9wz5CPz7BwJ8NfpgAAAbNJREFUSMe1lutygjAQhTeEOyi2AQIC3t//GcvYqk3OYsh0+v1yMN+czc4mQBwyL5ONUDMiLsqc1iGzOFQGYZGu0KLZQkQSrNSQ6I2aGRqkpktxlXJQSc4LNsqJCBhPOCQwwXOa6PmbErxlNpJeVMqD6uWliqOfprPiyN5tcOp0PaObntnmo9hIAV39pFNA9BOogKb+RQN/h9+RCfzR1gbtQqSArtQmemAjUy4QI7GxBTy92qKGJfEshlgp0DO15srmhuIRFuX0Cc/2KO5hUUkF1xt3dxKKuUS3uCPBjA0w4rySwqYyDLCM+EqxVrd45UTtFm81ywlEuzkHXhyhOfY1DArfntg+jRMY/PB80CeODaLhHigpt7fYNSgeuvpgD7k0j9V4Gfbc6RjN7oRE1rCeejVo27vMD4/WFokynB33qUpnUYb+t5zg7+OhNYrVxx5PIzGRJ9ijniwxIC7y3DV3urZ9/DrDfQxvR793a+YnpvRk6+Nt6YXceRQq//rxgKb/h87/fVrtAmKQW2c/JfGkb0PDjBYJomUtMuJQTcRKDUmL0LLiDDWevCzie7LYJGXOWl/BnLhvbq/sWgAAAABJRU5ErkJggg=="-->
        <!--                                class="login-sns-item-icon" style="height: 30px;" alt=""/>-->
        <!--                        </span>-->
        <!--          <span style="margin-left: 10px;">-->
        <!--                            QQ登录-->
        <!--                        </span>-->

        <!--        </div>-->

<!--      </div>-->
<!--    </div>-->
<!--  </el-dialog>-->


</template>

<script lang="ts" setup>
// import {ref, reactive} from 'vue'
// import {Search} from '@element-plus/icons-vue'
// import {useIndexStore} from "~/store";
// import router from "~/router";
// import {ElMessage} from 'element-plus'
// import {useRoute} from "vue-router";
//
// const {query} = useRoute();
// const isLogin = query?.login
// const loginFormVisible = ref(false)
// if (isLogin) {
//   loginFormVisible.value = true
// }
//
// const form = reactive({
//   username: "",
//   password: ""
// });
//
// const store = useIndexStore()
//
// const login = () => {
//   store.login(form).then((res) => {
//     if (res.code === 200) {
//       ElMessage.success("login success")
//       router.push(
//           {
//             path: '/user/workTable',
//             query: {}
//           }
//       )
//     } else {
//       ElMessage.error("login failed")
//     }
//   })
// }
//
// const toRegister = () => {
//   loginFormVisible.value = false
//   router.push('/register')
// }

</script>
<style scoped>
.dialog-footer button:first-child {
  margin-right: 10px;
}

.input-with-select {
  /*width: 120px;*/
}

.input-with-select .el-input-group__prepend {
  /*width: 30px;*/
  /*padding: 0;*/
  /*background-color: #FFFFFF;*/
}

.type-select {
  width: 70px;
  background-color: #FFFFFF;
}

.input-with-select .ep-input-group__append {
  width: 20px;
  border-bottom-right-radius: 20px;
  border-top-right-radius: 20px;
  background-color: #f4f4f5;
  cursor: pointer;
}

.input-with-select .ep-input-group__append:hover {
  background-color: #e9e9eb;
}
</style>
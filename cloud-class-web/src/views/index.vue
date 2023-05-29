<template>
  <div id="userLoginLayout">
    <div class="login_panel">
      <div class="login_panel_form">
        <div class="login_panel_form_title">
          <img
              class="login_panel_form_title_logo"
              src="src/assets/logo.png"
              alt
          >
          <p class="login_panel_form_title_p">云课堂</p>
        </div>
        <el-form
            @keyup.enter="login"
        >
          <el-form-item>
            <el-input
                type="text"
                autocomplete="off"
                maxlength="32"
                placeholder="请输入账号"
                v-model="form.username"
                style="height: 50px"
            />
          </el-form-item>

          <el-form-item>
            <el-input
                type="password"
                autocomplete="off"
                placeholder="请输入密码"
                show-password
                v-model="form.password"
                style="height: 50px"
            />
          </el-form-item>
          <el-form-item>
            <el-button
                style="width: 46%"
                size="large"
                @click="toRegister"
            >注册
            </el-button>
            <el-button
                type="primary"
                size="large"
                style="width: 46%; margin-left: 8%"
                @click="login"
            >登录
            </el-button>
          </el-form-item>
        </el-form>
      </div>
      <div class="login_panel_right"/>
    </div>
  </div>
  <!--  <el-container>-->
  <!--    <el-header>-->
  <!--      <header-login-->
  <!--          style="margin: auto;width: 90%;height: 60px;  position: fixed;z-index: 10; transform: translate(-50%,0);left:50%"/>-->
  <!--    </el-header>-->
  <!--    <el-main style="margin: 30px auto;width: 65%">-->
  <!--      <h2>热门课程</h2>-->
  <!--      <el-carousel :interval="4000" type="card" height="350px">-->
  <!--        <el-carousel-item v-for="item in 6" :key="item" style="">-->
  <!--          <data-course-index :course="course"></data-course-index>-->
  <!--        </el-carousel-item>-->
  <!--      </el-carousel>-->
  <!--      <h2>热门视频</h2>-->
  <!--    </el-main>-->
  <!--  </el-container>-->
</template>
<script lang="ts" setup>
import {reactive} from "vue";
import {useIndexStore} from "~/store";
import {ElMessage} from "element-plus";
import router from "~/router";

const form = reactive({
  username: "",
  password: ""
});
const store = useIndexStore()
const login = () => {
  store.login(form).then((res) => {
    if (res.code === 200) {
      ElMessage.success("登录成功")
      router.push(
          {
            path: '/user/workTable'
          }
      )
    } else {
      ElMessage.error("网络错误")
    }
  })
}
const toRegister = () => {
  router.push('/register')
}
</script>
<style lang="scss" scoped>
#userLoginLayout {
  margin: 0;
  padding: 0;
  background-image: url("/src/assets/index/login_background.jpg");
  background-size: cover;
  width: 100vw;
  height: 100vh;
  position: relative;

  .input-icon {
    padding-right: 6px;
    padding-top: 4px;
  }

  .login_panel {
    position: absolute;
    top: 3vh;
    left: 2vw;
    width: 96vw;
    height: 94vh;
    z-index: 2;
    background-color: rgba(255, 255, 255, .8);
    border-radius: 10px;
    display: flex;
    align-items: center;
    justify-content: space-evenly;

    .login_panel_right {
      background-image: url("/src/assets/index/login_left.svg");
      background-size: cover;
      width: 40%;
      height: 60%;
      float: right !important;
    }

    .login_panel_form {
      width: 420px;
      background-color: #fff;
      padding: 40px 40px 40px 40px;
      border-radius: 10px;
      box-shadow: 2px 3px 7px rgba(0, 0, 0, .2);

      .login_panel_form_title {
        display: flex;
        align-items: center;
        margin-bottom: 30px;

        .login_panel_form_title_logo {
          width: 200px;
          height: 72px;
        }

        .login_panel_form_title_p {
          font-size: 40px;
          padding-left: 20px;
        }
      }

      .vPicBox {
        display: flex;
        justify-content: space-between;
        width: 100%;
      }

      .vPic {
        width: 33%;
        height: 38px;
        background: #ccc;

        img {
          width: 100%;
          height: 100%;
          vertical-align: middle;
        }
      }
    }
  }
}

//小屏幕不显示右侧，将登录框居中
@media (max-width: 750px) {
  .login_panel_right {
    display: none;
  }
  .login_panel {
    width: 100vw;
    height: 100vh;
    top: 0;
    left: 0;
  }
  .login_panel_form {
    width: 100%;
  }
}

</style>
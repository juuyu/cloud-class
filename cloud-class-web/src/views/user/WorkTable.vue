<template>
  <el-container>
    <el-header>
      <header-base
          style="margin: auto;width: 90%;height: 60px;  position: fixed;z-index: 10; transform: translate(-50%,0);left:50%"/>
    </el-header>
    <el-main style="margin: 30px auto;width: 65%">
      <div class="page">
        <div class="gva-card-box">
          <div class="gva-card gva-top-card">
            <div class="gva-top-card-left">
              <div class="gva-top-card-left-title">{{ workTitle }}</div>
              <div class="gva-top-card-left-dot">{{ weatherInfo }}</div>
              <div class="gva-top-card-left-rows">
              </div>
              <div>
<!--                <div class="gva-top-card-left-item">-->
<!--                  使用教学：-->
<!--                  <a-->
<!--                      style="color:#409EFF"-->
<!--                      target="view_window"-->
<!--                      href="https://www.bilibili.com/video/BV1Rg411u7xH/"-->
<!--                  >https://www.bilibili.com/video/BV1Rg411u7xH</a>-->
<!--                </div>-->
<!--                <div class="gva-top-card-left-item">-->
<!--                  插件仓库：-->
<!--                  <a-->
<!--                      style="color:#409EFF"-->
<!--                      target="view_window"-->
<!--                      href="https://plugin.gin-vue-admin.com/#/layout/home"-->
<!--                  >https://plugin.gin-vue-admin.com</a>-->
<!--                </div>-->
              </div>
            </div>
            <img src="src/assets/dashboard.png" class="gva-top-card-right" alt>
          </div>
        </div>
      </div>
      <el-row :gutter="20" style="margin-top: 30px">
        <el-col :span="12">
          <el-card class="box-card">
            <template #header>
              <div class="card-header">
                <div style="display: flex;justify-content: space-between">
                  <div>加入课程汇总</div>
                  <div>
                    <el-button
                        type="primary"
                        link
                        @click="router.push('/course/join')"
                    >详情
                    </el-button>
                  </div>
                </div>
              </div>
            </template>
            <div style="height: 300px" v-loading="joinCourseInfoLoading">
              <PieChart :data="joinCourseInfo" v-if="!joinCourseInfoLoading"/>
            </div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card class="box-card">
            <template #header>
              <div class="card-header">
                <div style="display: flex;justify-content: space-between">
                  <div>创建课程汇总</div>
                  <div>
                    <el-button
                        type="primary"
                        link
                        @click="router.push('/course/create')"
                    >详情
                    </el-button>
                  </div>
                </div>
              </div>
            </template>
            <div style="height: 300px" v-loading="createCourseInfoLoading">
              <PieChart :data="createCourseInfo" v-if="!createCourseInfoLoading"/>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </el-main>
  </el-container>
</template>
<script lang="ts" setup>
import {onMounted, ref} from "vue";
import localCache from "~/utils/cache";
import {getData} from "~/api/main";
import {ElMessage} from "element-plus";
import router from "~/router";
import {getWeatherInfo} from "~/utils/weatherUtil";

const user = localCache.getCache("userInfo")


const workTitle = ref("早安，管理员，请开始一天的工作吧")
const weatherInfo = getWeatherInfo()
const joinCourseInfoLoading = ref(true)
const createCourseInfoLoading = ref(true)
const joinCourseInfo = ref([])
const createCourseInfo = ref([])

onMounted(() => {
  genWorkTitle()
  getJoinCourseInfo()
  getCreateCourseInfo()
})

const genWorkTitle = async () => {
  const date = new Date();
  let tip = ""
  if (date.getHours() >= 4 && date.getHours() < 9) {
    tip = "早上好，" + user.realName + "，" + "请开始一天的工作吧！"
  } else if (date.getHours() >= 9 && date.getHours() < 11) {
    tip = "上午好，" + user.realName + "，" + "请开始一天的工作吧！"
  } else if (date.getHours() >= 11 && date.getHours() < 13) {
    tip = "中午好，" + user.realName + "，" + "吃完午饭记得休息一下！"
  } else if (date.getHours() >= 13 && date.getHours() < 18) {
    tip = "下午好，" + user.realName + "，" + "记得喝杯咖啡提提神！"
  } else if (date.getHours() >= 18 && date.getHours() < 22) {
    tip = "晚上好，" + user.realName + "，" + "今天的工作完成了吗？"
  } else {
    tip = "夜深了，" + user.realName + "，" + "请注意休息！"
  }
  workTitle.value= tip
}

const getJoinCourseInfo = () => {
  joinCourseInfoLoading.value = true
  getData("table/joinCourse", {
    userId: user.id
  }).then((res: any) => {
    let {data} = res
    if (data.code === 200) {
      joinCourseInfo.value = data.data
    } else {
      ElMessage.error(data.msg)
    }
    joinCourseInfoLoading.value = false
  })
}

const getCreateCourseInfo = () => {
  createCourseInfoLoading.value = true
  getData("table/createCourse", {
    userId: user.id
  }).then((res: any) => {
    let {data} = res
    if (data.code === 200) {
      createCourseInfo.value = data.data
    } else {
      ElMessage.error(data.msg)
    }
    createCourseInfoLoading.value = false
  })
}

</script>
<style lang="scss" scoped>
@mixin flex-center {
  display: flex;
  align-items: center;
}

.page {
  background: #f0f2f5;
  padding: 0;

  .gva-card-box {
    padding: 12px 16px;

    & + .gva-card-box {
      padding-top: 0px;
    }
  }

  .gva-card {
    box-sizing: border-box;
    background-color: #fff;
    border-radius: 2px;
    height: auto;
    padding: 26px 30px;
    overflow: hidden;
    box-shadow: 0 0 7px 1px rgba(0, 0, 0, 0.03);
  }

  .gva-top-card {
    height: 260px;
    @include flex-center;
    justify-content: space-between;
    color: #777;

    &-left {
      height: 100%;
      display: flex;
      flex-direction: column;

      &-title {
        font-size: 22px;
        color: #343844;
      }

      &-dot {
        font-size: 16px;
        color: #6B7687;
        margin-top: 40px;
      }

      &-rows {
        // margin-top: 15px;
        margin-top: 18px;
        color: #6B7687;
        width: 600px;
        align-items: center;
      }

      &-item {
        + .gva-top-card-left-item {
          margin-top: 24px;
        }

        margin-top: 14px;
      }
    }

    &-right {
      height: 600px;
      width: 600px;
      margin-top: 28px;
    }
  }

  ::v-deep(.el-card__header) {
    padding: 0;
    border-bottom: none;
  }

  .card-header {
    padding-bottom: 20px;
    border-bottom: 1px solid #e8e8e8;
  }

  .quick-entrance-title {
    height: 30px;
    font-size: 22px;
    color: #333;
    width: 100%;
    border-bottom: 1px solid #eee;
  }

  .quick-entrance-items {
    @include flex-center;
    justify-content: center;
    text-align: center;
    color: #333;

    .quick-entrance-item {
      padding: 16px 28px;
      margin-top: -16px;
      margin-bottom: -16px;
      border-radius: 4px;
      transition: all 0.2s;

      &:hover {
        box-shadow: 0px 0px 7px 0px rgba(217, 217, 217, 0.55);
      }

      cursor: pointer;
      height: auto;
      text-align: center;
      // align-items: center;
      &-icon {
        width: 50px;
        height: 50px !important;
        border-radius: 8px;
        @include flex-center;
        justify-content: center;
        margin: 0 auto;

        i {
          font-size: 24px;
        }
      }

      p {
        margin-top: 10px;
      }
    }
  }

  .echart-box {
    padding: 14px;
  }
}

.dashboard-icon {
  font-size: 20px;
  color: rgb(85, 160, 248);
  width: 30px;
  height: 30px;
  margin-right: 10px;
  @include flex-center;
}

.flex-center {
  @include flex-center;
}

//小屏幕不显示右侧，将登录框居中
@media (max-width: 750px) {
  .gva-card {
    padding: 20px 10px !important;

    .gva-top-card {
      height: auto;

      &-left {
        &-title {
          font-size: 20px !important;
        }

        &-rows {
          margin-top: 15px;
          align-items: center;
        }
      }

      &-right {
        display: none;
      }
    }

    .gva-middle-card {
      &-item {
        line-height: 20px;
      }
    }

    .dashboard-icon {
      font-size: 18px;
    }
  }
}
</style>

<template>
  <el-table
      :data="userListFilter"
      style="height: 500px;"
  >
    <el-table-column>
      <template #header>
        <el-input
            v-model="searchParam"
            size="small"
            :prefix-icon="Search"
            placeholder="搜索"
        />
      </template>
      <template #default="scope">
        <el-avatar :size="45"
                   :src="scope.row.avatar"/>
      </template>
    </el-table-column>
    <el-table-column prop="realName"/>
    <el-table-column prop="userName"/>
    <el-table-column prop="email"/>
    <el-table-column align="right">
      <template #header>
        <el-button type="primary" text bg :icon="Plus" @click="addUserFormVisible = true">邀请人员</el-button>
      </template>
      <template #default="scope">
        <el-button
            size="small"
            type="danger"
            @click="kickUser(scope.row.id)"
        >
          踢出
        </el-button>
      </template>
    </el-table-column>
  </el-table>
  <el-dialog v-model="addUserFormVisible" style="width: 600px;">
    <h3 style="margin-top: 0">邀请人员</h3>
    <div style="width: 100%;justify-content: space-between;" class="flex items-center">
      <el-select
          style="width: 300px"
          v-model="inviteUserValue"
          multiple
          filterable
          remote
          reserve-keyword
          placeholder="请输入"
          :remote-method="queryUser"
          :loading="queryUserLoading"
      >
        <el-option
            v-for="item in queryUserOptions"
            :key="item.id"
            :label="item.realName"
            :value="item.id"
        >
          <div class="flex items-center" style="justify-content: space-between">
            <div class="flex items-center">
              <img :src="item.avatar" style="height: 28px;width: 28px;border-radius: 14px;" alt="">
            </div>
            <div>{{ item.realName }}</div>
            <div>{{ item.userName }}</div>
          </div>
        </el-option>
      </el-select>
      <el-button type="primary" text bg @click="inviteUser" :loading="inviteUserLoading">邀请</el-button>
    </div>
    <h3 style="margin-top: 40px">生成邀请码</h3>
    <div style="width: 100%;justify-content: space-between;" class="flex items-center">
      <el-form label-width="70px">
        <el-form-item label="有效天">
          <el-input-number :min="1" :max="5" v-model="inviteCodeExpire"/>
          <el-button type="primary" text bg @click="getInviteCode" :loading="genInviteCodeLoading">生成</el-button>
        </el-form-item>
        <el-form-item label="邀请码">
          <el-input
              readonly
              v-model="inviteCode"
          >
            <template #suffix>
              <div style="cursor: pointer;"
                   class="flex item-center"
                   @click="copyInviteCode"
              >
                <DocumentCopy style="width: 18px; height: 18px;"/>
              </div>
            </template>
          </el-input>
        </el-form-item>
      </el-form>
    </div>
    <template #footer>
      <el-button type="primary" @click="addUserFormVisible = false">关闭</el-button>
    </template>
  </el-dialog>
</template>
<script lang="ts" setup>
import {computed, ref, toRefs} from "vue";
import {getData, postData} from "~/api/main";
import {ElMessage, ElMessageBox} from "element-plus";
import {
  Search,
  Plus,
  DocumentCopy
} from '@element-plus/icons-vue'

interface Props {
  userList: any[]
  courseId: any
}

const props = defineProps<Props>()
const emit = defineEmits(['getCourseUser'])

const {userList, courseId} = toRefs(props)

const searchParam = ref('')
const userListFilter = computed(() =>
    userList.value.filter(
        (data) =>
            !searchParam.value ||
            data.realName.toLowerCase().includes(searchParam.value.toLowerCase())
    )
)


const addUserFormVisible = ref(false)
const queryUserLoading = ref(true)
const queryUserOptions = ref([])
const inviteUserValue = ref([])
const inviteUserLoading = ref(false)
const genInviteCodeLoading = ref(false)
const inviteCodeExpire = ref(1)
const inviteCode = ref("")

// invite users
const inviteUser = () => {
  inviteUserLoading.value = true
  const courseInviteParam = {
    courseId: courseId.value,
    userIds: inviteUserValue.value
  }
  if (courseInviteParam.userIds.length === 0){
    ElMessage.warning("请选择用户")
    inviteUserLoading.value = false
    return
  }
  postData("course/invite", courseInviteParam).then((res: any) => {
    let {data} = res;
    console.log(data)
    if (data.code === 200) {
      ElMessage.success("你已成功发出邀请, 请等待用户回应")
    } else {
      ElMessage.error(data.msg)
    }
    inviteUserValue.value = []
    inviteUserLoading.value = false
  })

}
// get invite code
const getInviteCode = () => {
  genInviteCodeLoading.value = true
  getData("course/createCode", {
    courseId: courseId.value,
    expireDay: inviteCodeExpire.value,
  }).then((res: any) => {
    let {data} = res
    if (data.code === 200) {
      ElMessage.success("课程邀请码生成成功")
      inviteCode.value = data.data
    } else {
      ElMessage.error("课程邀请码生成失败")
    }
    genInviteCodeLoading.value = false
  })
}
// copy code
const copyInviteCode = async () => {
  await navigator.clipboard.writeText(inviteCode.value)
  ElMessage.success("复制成功")
}

// 查询人员
const queryUser = (query: string) => {
  queryUserLoading.value = true
  if (query !== "") {
    getData("user/query", {
      queryParam: query,
    }).then((res: any) => {
      let {data} = res
      console.log(data)
      if (data.code === 200) {
        queryUserOptions.value = data.data
        queryUserLoading.value = false
      }
    })
  }
}

// 踢出课程
const kickUser = (id: number) => {
  ElMessageBox.confirm(
      '确认将此学生踢出课程？',
      '删除学生',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning',
        autofocus: false,

      })
      .then(() => {
        getData("course/user/kick", {
          courseId: courseId.value,
          userId: id
        }).then((res: any) => {
          let {data} = res;
          console.log(data)
          if (data.code === 200) {
            ElMessage.success("删除成功")
            emit('getCourseUser')
          } else {
            ElMessage.error(data.msg)
          }
        })
      })
      .catch(() => {
      })
}


</script>
<style scoped>
</style>
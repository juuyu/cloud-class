<template>
  <div v-for="item in dataList"
       :key="item"
       class="users flex items-center"
  >
    <div class="user-info flex items-center"
    >
      <img alt="" :src="item?.avatar"/>
      <div class="realName">{{ item?.realName }}</div>
      <div class="userName">{{ item?.userName }}</div>
      <el-tooltip
          content="踢出课堂"
          effect="light">
        <div class="kick" v-show="isAdmin" @click="kickUser(item?.id, item?.realName)">
          <img alt="" src="src/assets/live/kick.svg"/>
        </div>
      </el-tooltip>
    </div>
  </div>
</template>
<script lang="ts" setup>
import {toRefs} from "vue";
import {ElMessage, ElMessageBox} from "element-plus";
import {getData} from "~/api/main";

interface Props {
  dataList: Array<any>
  isAdmin: Boolean
  roomId: string
}

const props = defineProps<Props>()
const emit = defineEmits(['getLiveUsers'])


const {dataList, isAdmin, roomId} = toRefs(props)

const kickUser = (id: number, name: string) => {
  if (isAdmin.value) {
    ElMessageBox.confirm(
        '确认将用户[' + name + ']踢出课堂？',
        {
          confirmButtonText: '确认',
          cancelButtonText: '取消',
          type: 'warning',
          autofocus: false,
        })
        .then(() => {
          getData("live/kick", {
            roomId: roomId.value,
            userId: id
          }).then((res: any) => {
            let {data} = res
            if (data.code === 200 && data.data) {
              ElMessage.success("操作成功")
              emit('getLiveUsers')
            } else {
              ElMessage.error(data.msg)
            }
          })
        })
        .catch(() => {
        })
  }

}


</script>
<style scoped>
.users {
  width: 100%;
  height: 50px;
}

.users :hover {
  background-color: #E6E8EB;
}

.user-info {
  width: 100%;
  height: 100%;
  border-radius: 10px;
  /*margin: 0 10px;*/
  justify-content: space-between;
}

.user-info img {
  height: 40px;
  width: 40px;
  border-radius: 10px;
  margin-left: 10px;
}

.realName {
  width: 100px;
  margin-left: 20px;
  font-size: 14px;
}

.userName {
  width: 90px;
  font-size: 14px;
}

.kick {
  align-items: center;
  cursor: pointer
}

.kick img {
  height: 30px;
  width: 30px;
}
</style>

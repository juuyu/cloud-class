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
  </el-table>

</template>
<script lang="ts" setup>
import {computed, ref, toRefs} from "vue";
import {
  Search
} from '@element-plus/icons-vue'

interface Props {
  userList: any[]
  courseId: any
}

const props = defineProps<Props>()
const {userList, courseId} = toRefs(props)

const searchParam = ref('')
const userListFilter = computed(() =>
    userList.value.filter(
        (data) =>
            !searchParam.value ||
            data.realName.toLowerCase().includes(searchParam.value.toLowerCase())
    )
)

</script>
<style scoped>
</style>
<template>
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
    <el-scrollbar height="400px" v-show="fileListFilter.length !== 0"
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
          <a :href="item?.fileLink">
            <img alt="" src="src/assets/file/download.svg"/>
          </a>
        </div>
      </div>
    </el-scrollbar>
  </div>
</template>
<script lang="ts" setup>
import {computed, ref, toRefs} from "vue"
import {Search} from '@element-plus/icons-vue'

interface Props {
  dataList: Array<any>
  fileListLoading: boolean
}

const props = defineProps<Props>()
const {dataList, fileListLoading} = toRefs(props)

const searchParam = ref('')
const fileListFilter = computed(() =>
    dataList.value.filter(
        (data) =>
            !searchParam.value ||
            data.fileName.toLowerCase().includes(searchParam.value.toLowerCase())
    )
)
</script>
<style scoped>
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

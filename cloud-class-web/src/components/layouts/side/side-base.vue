<!-- <template>

  <el-menu
    default-active="2"
    class="el-menu-vertical-demo"
    :collapse="isCollapse"
    @public="handleOpen"
    @close="handleClose"
  >
    <el-sub-menu index="1">
      <template #title>
        <el-icon><location /></el-icon>
        <span>Navigator One</span>
      </template>
      <el-menu-item-group>
        <template #title><span>Group One</span></template>
        <el-menu-item index="1-1">item one</el-menu-item>
        <el-menu-item index="1-2">item two</el-menu-item>
      </el-menu-item-group>
      <el-menu-item-group title="Group Two">
        <el-menu-item index="1-3">item three</el-menu-item>
      </el-menu-item-group>
      <el-sub-menu index="1-4">
        <template #title><span>item four</span></template>
        <el-menu-item index="1-4-1">item one</el-menu-item>
      </el-sub-menu>
    </el-sub-menu>
    <el-menu-item index="2">
      <el-icon><icon-menu /></el-icon>
      <template #title>Navigator Two</template>
    </el-menu-item>
    <el-menu-item index="3" disabled>
      <el-icon><document /></el-icon>
      <template #title>Navigator Three</template>
    </el-menu-item>
    <el-menu-item index="4">
      <el-icon><setting /></el-icon>
      <template #title>Navigator Four</template>
    </el-menu-item>
  </el-menu>
</template>

<script lang="ts" setup>
import { ref } from 'vue'
import {
  Location,
  Document,
  Menu as IconMenu,
  Setting,
} from '@element-plus/icons-vue'

const isCollapse = ref(true)
const handleOpen = (key: string, keyPath: string[]) => {
  console.log(key, keyPath)
}
const handleClose = (key: string, keyPath: string[]) => {
  console.log(key, keyPath)
}
</script> -->


<template>
  <el-scrollbar height="calc('100vh - 100px')">
                        
                      
  <el-menu default-active="1" @open="handleOpen" @close="handleClose" style="border-style: none;">

    <el-menu-item index="1">
      <el-icon><Notebook /></el-icon>
      <span>公开课</span>
    </el-menu-item>

    <el-menu-item index="2">
      <el-icon><Notebook /></el-icon>
      <span>工作台</span>
    </el-menu-item>
    <el-sub-menu index="3">
      <template #title>
        <el-icon>
          <Notebook />
        </el-icon>
        <span>我的课程</span>
      </template>
      <el-menu-item index="1-1">私教课</el-menu-item>
      <el-menu-item index="1-2">班级课</el-menu-item>
      <el-menu-item index="1-3">公开课</el-menu-item>

    </el-sub-menu>


    <el-menu-item index="4">
      <el-icon><Notebook /></el-icon>
      <span>消息中心</span>
    </el-menu-item>




    <el-menu-item index="5">
      <el-icon>
        <setting />
      </el-icon>
      <span>设置</span>
    </el-menu-item>
  </el-menu>
</el-scrollbar>
</template>

<script lang="ts" setup>
import {
  Document,
  Notebook,
  Menu as IconMenu,
  Location,
  Setting,
} from '@element-plus/icons-vue'
const handleOpen = (key: string, keyPath: string[]) => {
  console.log(key, keyPath)
}
const handleClose = (key: string, keyPath: string[]) => {
  console.log(key, keyPath)
}
</script>

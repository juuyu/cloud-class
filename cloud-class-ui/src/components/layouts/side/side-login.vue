<template>
    <el-menu default-active="1" @open="handleOpen" @close="handleClose" style="border-style: none;">
  
      <el-menu-item index="1">
        <el-icon><Notebook /></el-icon>
        <span>公开课</span>
      </el-menu-item>

      <el-menu-item index="2">
        <el-icon><Notebook /></el-icon>
        <span>教学视频</span>
      </el-menu-item>
  
      <el-menu-item index="3">
        <el-icon><Notebook /></el-icon>
        <span>知识专栏</span>
      </el-menu-item>
  
  
    </el-menu>
  
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
  
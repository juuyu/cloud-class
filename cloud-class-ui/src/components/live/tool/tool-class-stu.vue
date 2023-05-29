<template>
  <el-scrollbar>
    <div class="user-tools">
      <div class="user-tool-row">
        <div class="user-tool user-tool-col-left" style="background-color:  #c6e2ff">
          <div>
            <div class="tool-img flex items-center">
              <img alt="" src="/src/assets/tools/hands-up.svg"/>
            </div>
            <div class="tool-img-title">举手</div>
          </div>
        </div>


        <el-popover :visible="visible" placement="left" :width="650" popper-class="note-popper">
          <template #default>
            <Note></Note>
            <div style="text-align: right; margin: 0">
              <el-button size="small" text @click="visible = false">
                cancel
              </el-button>

              <el-button size="small" type="primary" @click="visible = false">
                confirm
              </el-button>
            </div>
          </template>


          <template #reference>
            <div @click="visible = true" class="user-tool user-tool-col-right" style="background-color:  #c6e2ff">
              <div>
                <div class="tool-img flex items-center">
                  <img alt="" src="/src/assets/tools/note.svg"/>
                </div>
                <div class="tool-img-title">记笔记</div>
              </div>
            </div>
          </template>
        </el-popover>


      </div>

      <div class="user-tool-row">
        <div class="user-tool user-tool-col-left" style="background-color:  #d1edc4">
          <div>
            <div class="tool-img flex items-center">
              <img alt="" src="/src/assets/tools/files.svg"/>
            </div>
            <div class="tool-img-title">资料</div>
          </div>
        </div>
        <div class="user-tool user-tool-col-right" style="background-color:  #d1edc4">

        </div>
      </div>

      <div class="user-tool-row">
        <div class="user-tool user-tool-col-left" style="background-color:  #f8e3c5">

        </div>
        <div class="user-tool user-tool-col-right" style="background-color:  #f8e3c5">

        </div>
      </div>
    </div>
  </el-scrollbar>
</template>
<script lang="ts" setup>
import {ref} from "vue";

const visible = ref(false)
</script>
<style scoped>
.user-tools {
  height: 100%;
  width: 100%;
}

.user-tool {
  height: 100px;
  width: 140px;
  border-radius: 16px;
}

.user-tool:hover {
  cursor: pointer;
  scale: 1.04;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.2);
}

.user-tool-row {
  height: 100px;
  margin-top: 15px;
  margin-left: 15px;
  margin-right: 15px;
}

.user-tool-col-left {
  float: left;
}

.user-tool-col-right {
  float: right;
}

.tool-img {
  width: 50px;
  height: 50px;
  margin: 10px;
  border-radius: 16px;
  background-color: #FFFFFF;
}

.tool-img img {
  height: 30px;
  width: 30px;
  margin: 0 auto
}

.tool-img-title {
  margin-left: 20px;
}

</style>
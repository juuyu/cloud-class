<template>
  <div style="width: 100%">
    <v-md-editor
        v-model="text"
        mode="edit"
        height="400px"
        placeholder="请输入..."
        left-toolbar="undo redo clear | h bold italic strikethrough | image code "
        right-toolbar="preview sync-scroll"
    >

    </v-md-editor>
  </div>
</template>

<script lang="ts" setup>
const text = ""

</script>
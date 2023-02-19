<script setup lang="ts">
defineProps<{
  msg: string
}>()
</script>

<template>
  <div class="greetings mt-3">
    <h1 class="green">{{ msg }}</h1>
  </div>
</template>

<style scoped>

</style>

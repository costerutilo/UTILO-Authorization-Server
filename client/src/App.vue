<script setup lang="ts">

  import { RouterView } from 'vue-router';
  import HelloWorld from '@/components/HelloWorld.vue';
  import NavigationItem from "@/components/NavigationItem.vue";

</script>

<template>

  <NavigationItem />
  <div class="container">

    <HelloWorld :msg="$t('message.headline')" />

  </div>

  <RouterView />

</template>

<style scoped>

</style>

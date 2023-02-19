<script setup lang="ts">

  import { RouterView } from 'vue-router';
  import HelloWorld from '@/components/HelloWorld.vue';
  import Navigation from "@/components/Navigation.vue";

</script>

<template>

  <Navigation />
  <div class="container">

    <HelloWorld msg="SVV Authorization Server" />

  </div>

  <RouterView />

</template>

<style scoped>

</style>

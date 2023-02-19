<template>
  <div class="container">
    <h2>Login</h2>
  </div>
</template>

<script setup lang="ts">

import { onMounted } from "vue";

// Parameter
const props = defineProps({});

// nicht reaktive Variablen:
const loginurl: string = 'oauth2/authorize?response_type=code&client_id=' + import.meta.env.VITE_CLIENT_ID + '&scope=openid&redirect_uri=';

// Benutzerereignisse
function openLoginWindow() {

  console.info('openLoginWindow base URL: ', import.meta.env.VITE_BASE_URL);
  console.info('LOGIN URI: ', import.meta.env.VITE_BASE_URL + loginurl);

  window.open(
      import.meta.env.VITE_BASE_URL + loginurl + import.meta.env.VITE_AUTHORIZE_REDIRECT_URI,
      "_parent"
  );

}

onMounted(() => {
  openLoginWindow();
});

</script>
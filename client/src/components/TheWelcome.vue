<template>

  <div class="container">
    <h2>Administration</h2>
    <p>Diese Anwendung ist nur für autorisierte Benutzer zugänglich.</p>

    <ul class="list-group">
      <li class="list-group-item">Version: {{ serverConfig.version }}</li>
      <li class="list-group-item">Profil: {{ serverConfig.profile }}</li>
    </ul>

  </div>

</template>
<script setup lang="ts">

  import axios from "axios";
  import {Ref, ref, onMounted} from "vue";

  // reaktive Variablen:
  let serverConfig: Ref<string> = ref('');

  async function getVersion() {

    const tagsResponse = await axios.get('main/version');
    serverConfig.value = tagsResponse.data;

  }

  onMounted(() => {
    getVersion();
  });


</script>
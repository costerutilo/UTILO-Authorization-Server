<template>

  <div class="container">
    <h2>{{ $t('message.theWelcomeHeadline') }}</h2>
    <p>{{ $t('message.theWelcomeDesc') }}</p>

    <ul class="list-group">
      <li class="list-group-item">{{ $t('message.theWelcomeVersion') }}: {{ serverConfig.version }}</li>
      <li class="list-group-item">{{ $t('message.theWelcomeProfile') }}: {{ serverConfig.profile }}</li>
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
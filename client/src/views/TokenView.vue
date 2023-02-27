<template>
  <div class="container">
    <h2>Administration</h2>

    {{ token }}

  </div>
</template>

<script setup lang="ts">

  import { onMounted, Ref, ref } from "vue";
  import { useRoute } from 'vue-router';
  import axios from "axios";

  // Parameter
  const props = defineProps({});
  const route = useRoute();

  // nicht reaktive Variablen:
  // const loginurl: string = '/oauth2/authorize?response_type=code&client_id=utilo-client&scope=openid&redirect_uri=http://127.0.0.1:9000/test/utilo';

  // reaktive Variablen:
  let code: Ref<string> = ref('');
  let token: Ref<string> = ref('');

  // berechnete Werte:
  // let result: ComputedRef<number> = computed(() => {
  //   x.value;
  // });

  // Benutzerereignisse
  function init() {

    console.log('Admininistrator Client code available: ' + route.query);
    if (route.query && route.query.code) {
      code.value = '' + route.query.code;
    var urlencoded = new URLSearchParams();
    urlencoded.append("grant_type", "authorization_code");
    // urlencoded.append("code", "yyJTTI3JqNno1XlSW59qxX3CCytMm-ChoHnqVw3iSUGyT6ltT_tpPclQ8bdSyeApO4IWE442irBiRwntJJzae9BIntpC3_vshTgNhAfbsBlkwh3n50jkAxs3hTqavqsy");
    urlencoded.append("code", code);
    urlencoded.append("redirect_uri", "https://www.utilo.eu");

    var requestOptions = {
      method: 'POST',
      headers: myHeaders,
      body: urlencoded,
      redirect: 'follow'
    };

    fetch(url, requestOptions)
        .then(response => response.text())
        .then(result => console.log(result))
        .catch(error => console.log('error', error));


  }


  onMounted(() => {
    init();
  });

</script>
<template>
  <div class="container">
    <h2>Administration</h2>

    {{ code }}
    <br/>
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
      createToken(code.value);
    }

  }
  async function getToken(code: string) {

    console.info('getToken', code);
    const params = {
      grant_type: 'authorization_code',
      code: code,
      redirect_uri: import.meta.env.VITE_AUTHORIZE_REDIRECT_URI,
    };

    var credentials = btoa(import.meta.env.VITE_CLIENT_ID + ':' + 'secret');
    var basicAuth = 'Basic ' + credentials;

    axios.post(
        'oauth2/token',
        params,
    ).then(response => token.value = response.data);

    const user = await axios.post(
        'oauth2/token',
        { headers: { 'Authorization': basicAuth } }
    );

  }

  async function getAuthToken(code: string) {

    var credentials = btoa(import.meta.env.VITE_CLIENT_ID + ':' + 'secret');
    var basicAuth = 'Basic ' + credentials;
    console.info('Basic', credentials)

    const data = new URLSearchParams();
    data.append('grant_type', 'authorization_code');
    data.append('code', code);
    data.append('redirect_uri', import.meta.env.VITE_AUTHORIZE_REDIRECT_URI);
    const fetchAuthToken = await axios({
      url: 'oauth2/token',
      method: 'POST',
      headers: {
        'authorization': basicAuth,
        'utilo': 'test'
      },
      data,
      withCredentials: true,
    });

    return fetchAuthToken;

  }

  async function createToken(code: string) {

    const url =  'http://127.0.0.1:9000/oauth2/token';

    var myHeaders = new Headers();
    myHeaders.append("Authorization", "Basic dXRpbG8tY2xpZW50OnNlY3JldA==");
    myHeaders.append("Content-Type", "application/x-www-form-urlencoded");

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
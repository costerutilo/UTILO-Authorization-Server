<template>
  <div class="container">
    <h2>Administration</h2>

    code: {{ code }}
    <br/>
    token: {{ token }}

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
      // getToken(code.value);
      // createTokenXhR(code.value);
    }

  }

  // function createTokenXhR(code: string) {
  //
  //   // WARNING: For POST requests, body is set to null by browsers.
  //   var data = "grant_type=authorization_code&code=" + code + "&redirect_uri=http%3A%2F%2F127.0.0.1%3A9010%2Fadmin";
  //
  //   var xhr = new XMLHttpRequest();
  //   xhr.withCredentials = true;
  //
  //   xhr.addEventListener("readystatechange", function() {
  //     if(this.readyState === 4) {
  //       console.log('XHR: ' + this.responseText);
  //     }
  //   });
  //
  //   xhr.open("POST", "http://127.0.0.1:9000/oauth2/token");
  //   xhr.setRequestHeader("Authorization", "Basic dXRpbG8tY2xpZW50OnNlY3JldA==");
  //   xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
  //
  //   xhr.send(data);
  //
  // }

  async function createToken(code: string) {

    console.info('createToken per fetch');

    const url =  'http://127.0.0.1:9000/oauth2/token';

    var credentials = btoa(import.meta.env.VITE_CLIENT_ID + ':' + 'secret');
    var basicAuth = 'Basic ' + credentials;

    let data = {
      "grant_type": "authorization_code",
      "code": code,
      "redirect_uri": import.meta.env.VITE_AUTHORIZE_REDIRECT_URI,
      "client_id": import.meta.env.VITE_CLIENT_ID,
      "client_secret":  'secret',
    }

    let requestOptions = {
        method: 'POST', // *GET, POST, PUT, DELETE, etc.
        mode: 'no-cors', // no-cors, *cors, same-origin
        cache: 'no-cache', // *default, no-cache, reload, force-cache, only-if-cached
        credentials: 'same-origin', // include, *same-origin, omit
        headers: {
          'cache-control': 'no-cache',
          'content-type': 'application/x-www-form-urlencoded',
          'Authorization': basicAuth, // Here you can add your token -> not possible with no-cors???
        },
        // redirect: 'follow', // manual, *follow, error
        // referrerPolicy: 'no-referrer', // no-referrer, *no-referrer-when-downgrade, origin, origin-when-cross-origin, same-origin, strict-origin, strict-origin-when-cross-origin, unsafe-url
        body: JSON.stringify(data) // body data type must match "Content-Type" header
    }

    fetch(url, requestOptions)
        .then(response => response.text())
        .then(result => {
          console.log('result: ', result);
          token.value = result;
        })
        .catch(error => console.log('error', error));

  }

  // async function getToken(code: string) {
  //
  //   console.info('getToken', code);
  //   const params = {
  //     grant_type: 'authorization_code',
  //     code: code,
  //     redirect_uri: import.meta.env.VITE_AUTHORIZE_REDIRECT_URI,
  //     'Content-Type': "text/plain"
  //   };
  //
  //   var credentials = btoa(import.meta.env.VITE_CLIENT_ID + ':' + 'secret');
  //   var basicAuth = 'Basic ' + credentials;
  //
  //   axios.post(
  //       'oauth2/token',
  //       params,
  //   ).then(response => token.value = response.data);
  //
  //   const user = await axios.post(
  //       'oauth2/token',
  //       { headers: { 'Authorization': basicAuth } }
  //   );
  //
  // }
  //
  // async function getAuthToken(code: string) {
  //
  //   var credentials = btoa(import.meta.env.VITE_CLIENT_ID + ':' + 'secret');
  //   var basicAuth = 'Basic ' + credentials;
  //   console.info('Basic', credentials)
  //
  //   const data = new URLSearchParams();
  //   data.append('grant_type', 'authorization_code');
  //   data.append('code', code);
  //   data.append('redirect_uri', import.meta.env.VITE_AUTHORIZE_REDIRECT_URI);
  //   const fetchAuthToken = await axios({
  //     url: 'oauth2/token',
  //     method: 'POST',
  //     headers: {
  //       'authorization': basicAuth,
  //       'utilo': 'test'
  //     },
  //     data,
  //     withCredentials: true,
  //   });
  //
  //   return fetchAuthToken;
  //
  // }

  onMounted(() => {
    init();
  });

</script>
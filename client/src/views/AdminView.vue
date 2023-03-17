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

    let myHeaders = new Headers();
    myHeaders.append("Content-Type", "application/x-www-form-urlencoded");

    var urlencoded = new URLSearchParams();
    urlencoded.append("grant_type", "authorization_code");
    urlencoded.append("code", code);
    urlencoded.append("redirect_uri", "http://127.0.0.1:9010/admin");
    urlencoded.append("client_id", "utilo-client");
    urlencoded.append("client_secret", "secret");

    var requestOptions = {
      method: 'POST',
      mode: 'no-cors', // no-cors, *cors, same-origin
      headers: myHeaders,
      body: urlencoded,
      redirect: 'follow'
    };

    fetch("http://127.0.0.1:9000/oauth2/token", requestOptions)
        .then((response) => {
          console.log('headers:');
          console.log(...response.headers);
          return response.text();
        })
        .then((data) => {
          console.log('data: ' + data);
        })
        .catch(error => console.log('error', error));

  }

  async function getToken(code: string) {

    console.info('getToken', code);

    const headers =  {
      "Content-Type": "application/x-www-form-urlencoded"
    }
    const params = {
      method: 'POST',
      grant_type: 'authorization_code',
      code: code,
      redirect_uri: import.meta.env.VITE_AUTHORIZE_REDIRECT_URI,
      client_id: "utilo-client",
      client_secret: "secret",
      mode: 'no-cors',
      headers: headers,
      withCredentials: true
    };

    axios(
        'oauth2/token',
        params
    ).then(response => token.value = response.data);

  }
  //
  // async function getAuthToken(code: string) {
  //
  //   const data = new URLSearchParams();
  //   data.append('grant_type', 'authorization_code');
  //   data.append('code', code);
  //   data.append('redirect_uri', import.meta.env.VITE_AUTHORIZE_REDIRECT_URI);
  //   const fetchAuthToken = await axios({
  //     url: 'oauth2/token',
  //     method: 'POST',
  //     data
  //   });
  //
  //   console.log('token' , fetchAuthToken);
  //   return fetchAuthToken;
  //
  // }

  onMounted(() => {
    init();
  });

</script>
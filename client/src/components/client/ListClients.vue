<template>

  <div class="container clientList">

    <h2>Client Liste</h2>

    <table class="table">
      <thead>
      <tr>
        <th scope="col">client_id</th>
        <th scope="col">clientAuthenticationMethods</th>
        <th scope="col">authorizationGrantTypes</th>
        <th scope="col">redirectUris</th>
        <th scope="col">scopes</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="client in clients" :key="client.id">
        <td>{{ client.clientId }}</td>
        <td>{{ client.clientAuthenticationMethods }}</td>
        <td>{{ client.authorizationGrantTypes }}</td>
        <td>{{ client.redirectUris }}</td>
        <td>{{ client.scopes }}</td>
      </tr>
      </tbody>
    </table>

  </div>

</template>
<script setup lang="ts">

  import axios from "axios";
  import {Ref, ref, onMounted} from "vue";

  // reaktive Variablen:
  let clients: Ref<object> = ref([]);

  async function getClients() {

    const tagsResponse = await axios.get('client/findAllClients');
    clients.value = tagsResponse.data;

  }

  onMounted(() => {
    getClients();
  });

</script>
<template>

  <div class="container clientList">

    <h2>{{ $t('message.clientListHeadline') }}</h2>

    <table class="table">
      <thead>
      <tr>

        <th scope="col">client_id</th>
        <th scope="col">{{ $t('message.clientAuthenticationMethods') }}</th>
        <th scope="col">{{ $t('message.authorizationGrantTypes') }}</th>
        <th scope="col">{{ $t('message.redirectUris') }}</th>
        <th scope="col">{{ $t('message.scopes') }}</th>
        <th scope="col"><!-- actions --></th>
        <th scope="col"><!-- actions --></th>

      </tr>
      </thead>
      <tbody>
      <tr v-for="client in clients" :key="client.id">

        <td>{{ client.clientId }}</td>
        <td>{{ client.clientAuthenticationMethods }}</td>
        <td>{{ client.authorizationGrantTypes }}</td>
        <td>{{ client.redirectUris }}</td>
        <td>{{ client.scopes }}</td>
        <td>
          <!-- Edit Button -->
          <create-or-edit-client :client="client" />
        </td>
        <td>
          <!-- Delete Button -->
          <button type="button" class="btn btn-danger btn-sm" @click="trash(client, $t('message.confirmDeleteClient', { msg: client.clientId }))">
            <b-icon-trash />
          </button>
        </td>

      </tr>
      </tbody>
    </table>

  </div>

</template>
<script setup lang="ts">

  import axios from "axios";
  import {Ref, ref, onMounted} from "vue";
  import { BIconPenFill, BIconTrash } from 'bootstrap-icons-vue';
  import CreateOrEditClient from "@/components/client/CreateOrEditClient.vue";

  // reaktive Variablen:
  let clients: Ref<object> = ref([]);

  async function getClients() {

    const tagsResponse = await axios.get('client/findAllClients');
    clients.value = tagsResponse.data;

  }

  /**
   * show form to edit the client
   */
  function edit(client: object) {

    console.info('edit client', client);

  }
  /**
   * delete the client - show confirm message
   */
  function trash(client: object, msg: string) {

    console.info('delete client', client);
    if (confirm(msg)) {
      // delete it
    }

  }

  onMounted(() => {
    getClients();
  });

</script>
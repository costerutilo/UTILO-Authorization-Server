<template>

  <span class="createOrEditWrapper">

    <!-- Edit Button -->
    <button type="button" class="btn btn-outline-primary btn-sm" @click="edit(client)" v-if="client">
      <div style="display: flex">
        <b-icon-pen-fill style="margin-top: 5px;" />&nbsp;{{ $t('message.edit') }}
      </div>
    </button>
    <!-- Create Button -->
    <button type="button" class="btn btn-outline-primary btn-sm" @click="create(client)" v-else>
      <div style="display: flex">
        <b-icon-pen-fill style="margin-top: 5px;" />&nbsp;{{ $t('message.createClient') }}
      </div>
    </button>

    <!-- Modal to edit or to create client -->
    <b-modal v-model="modalShow"
             id="createOrEditClientModal"
             size="xl"
             :title="client && client.id != null ? $t('message.editClient') : $t('message.createClient')">
      hallo modal
    </b-modal>

  </span>

</template>
<script setup lang="ts">

  import axios from "axios";
  import {Ref, ref, onMounted, watch} from "vue";
  import { BIconPenFill, BIconTrash } from 'bootstrap-icons-vue';
  import { BModal } from "bootstrap-vue";

  // Parameter
  const props = defineProps({
    client: Object
  });

  // reaktive Variablen:
  /** new oder client to edit */
  let myClient: Ref<object> = ref([]);
  let modalShow: Ref<boolean> = ref(false);

  /**
   * show form to edit the client
   */
  function edit(client: object) {

    console.info('edit client', client);
    myClient.value = client;
    modalShow.value = true;

  }
  /**
   * show form to create the client
   */
  function create() {

    console.info('create client');
    myClient.value = {
      id: null
    };
    modalShow.value = true;

  }

  // onMounted(() => {
  //
  //   if (props.client) {
  //     myClient.value = props.client;
  //   }
  //
  // });
  // // notwendig, wenn sich Parameterwert ändert
  // watch(() => props.client, () => {
  //   if (props.client) {
  //     myClient.value = props.client;
  //   }
  // });

</script>
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
    <div class="modal fade"
         id="createOrEditClientModal"
         tabindex="-1"
         aria-labelledby="createOrEditClientModalLabel"
         aria-hidden="true">

      <div class="modal-dialog modal-xl">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title" id="createOrEditClientModalLabel">
              {{ client && client.id != null ? $t('message.editClient') : $t('message.createClient') }}
            </h5>
            <button type="button"
                    class="btn-close"
                    data-bs-dismiss="modal"
                    :aria-label="$t('message.close')">
            </button>
          </div>
          <div class="modal-body">
            <!-- Form to edit or to create client -->

          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
              {{ $t('message.close') }}
            </button>
            <button type="button" class="btn btn-primary">
              {{ $t('message.save') }}
            </button>
          </div>
        </div>
      </div>

    </div>


  </span>

</template>
<script setup lang="ts">

  import axios from "axios";
  import {Ref, ref, onMounted, watch} from "vue";
  import { BIconPenFill, BIconTrash } from 'bootstrap-icons-vue';
  import 'bootstrap';

  // Parameter
  const props = defineProps({
    client: Object
  });

      // reaktive Variablen:
  /** new oder client to edit */
  let myClient: Ref<object> = ref([]);

  /**
   * show form to edit the client
   */
  function edit(client: object) {

    console.info('edit client', client);
    myClient.value = client;
    // open Modal with form:
    var myModal = new bootstrap.Modal(document.getElementById('createOrEditClientModal'), {
      keyboard: false
    })
    myModal.show();

  }
  /**
   * show form to create the client
   */
  function create() {

    console.info('create client');
    myClient.value = {
      id: null
    };

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
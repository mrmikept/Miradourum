<script setup>

import router from "@/router/index.js";
import {computed} from "vue";
import {useRoute} from "vue-router";

const route = useRoute();
const showBackButton = computed(() => route.path !== '/home');

</script>



<template>
  <button
      v-if="showBackButton"
      @click="router.back()"
      class="nav-button"
  >
    Voltar
  </button>

  <v-btn
      v-if="showBackButton"
      color="#427F99"
      @click="router.back()"
      prepend-icon="fa-solid fa-arrow-left"
      class="font-weight-bold"
      variant="text">
    Voltar atrás
  </v-btn>

</template>

<style scoped>

</style>
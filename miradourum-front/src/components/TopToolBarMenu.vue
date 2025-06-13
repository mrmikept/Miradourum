<script setup>

import LogoButton from "./LogoButton.vue";
import {onMounted, ref} from "vue";
import router from "../router/index.js";
import Location_chip from "./location_chip.vue";
import {UserStore} from "@/store/userStore.js";
import { useRoute } from 'vue-router';
import { computed } from 'vue';

defineProps({
  avatarUrl: String,
  username: String,
  email: String
})

const actualUsername = ref('')
const actualAvatarUrl = ref('')
const actualEmail = ref('')
const userStore = UserStore();

const token = userStore.authToken

const handleLogout = () => {
  userStore.clearUserStore()
  // localStorage.removeItem('authToken')  // Remove o token de autenticação
  router.push('/login')                  // Redireciona para a página de login
}
const route = useRoute(); 
const showBackButton = computed(() => route.path !== '/home');
</script>

<template>
  <v-toolbar color="#427F99">
    <v-container fluid>
      <v-row align="center" justify="space-between" no-gutters>
        

        <v-col class="d-flex justify-end align-center" cols="auto">
          <!-- Left Side: Back Button + Logo + Location Chip -->
<v-col class="d-flex align-center" cols="auto">
  <LogoButton to="/home" class="mr-16" />
  <location_chip class="mr-12" />
  
  <button
    v-if="showBackButton"
    @click="router.back()"
    class="nav-button"
  >
    Voltar
  </button>
</v-col>




          <v-navigation-drawer
              location="right"
              expand-on-hover
              rail
              permanent
          >
            <v-list>
              <v-list-item
                  :prepend-avatar="actualAvatarUrl || userStore.avatarUrl"
                  :title="actualUsername || userStore.username"
                  :subtitle="actualEmail || userStore.email"
              ></v-list-item>
            </v-list>

            <v-divider></v-divider>

            <v-list density="compact" nav>
              <v-list-item
                  to="/home"
                  nav
                  value="home"
                  active-color="#1976D2"
                  link
              >
                <template #prepend>
                  <v-icon icon="fa-solid fa-house" color="#5FB3CE" />
                </template>
                <v-list-item-title class="drawer-text">Página Inicial</v-list-item-title>
              </v-list-item>


              <v-list-group
                  value="profile"
                  prepend-icon="fa-solid fa-user"
                  color="#5FB3CE"
                  base-color="#5FB3CE"
                  activator
                  fluid
              >
                <template #activator="{ props }">
                  <v-list-item v-bind="props">
                    <v-list-item-title class="drawer-text">Perfil de Utilizador</v-list-item-title>
                  </v-list-item>
                </template>

                <v-list-item to="/history" class="sub-item" link>
                  <template #prepend>
                    <v-icon icon="fa-solid fa-clock-rotate-left" color="#5FB3CE"/>
                  </template>
                  <v-list-item-title class="drawer-text">Histórico</v-list-item-title>
                </v-list-item>

                <v-list-item to="/editProfile" class="sub-item" link>
                  <template #prepend>
                    <v-icon icon="fa-solid fa-pen-to-square" color="#5FB3CE"/>
                  </template>
                  <v-list-item-title class="drawer-text">Editar Perfil</v-list-item-title>
                </v-list-item>


              </v-list-group>


              <v-list-item
                  to="/photoGallery"
                  nav
                  value="galeria"
                  active-color="#1976D2"
                  link
              >
                <template #prepend>
                  <v-icon icon="fa-solid fa-image" color="#5FB3CE" />
                </template>
                <v-list-item-title class="drawer-text">Minha Galeria</v-list-item-title>
              </v-list-item>


            </v-list>
            <v-list-item
                v-if="userStore.userType === 2"
                to="/review"
                nav
                value="special"
                active-color="#1976D2"
                link
              >
                <template #prepend>
                  <v-icon icon="fa-solid fa-list-check" color="#5FB3CE" />
                </template>
                <v-list-item-title class="drawer-text">Review Pontos</v-list-item-title>
              </v-list-item>


            <v-divider></v-divider>
            <v-list density="compact" nav>

              <v-list-item
                  @click="handleLogout"
              >
                <template #prepend>
                  <v-icon icon="fa-solid fa-right-from-bracket" color="#D32F2F" />
                </template>
                <v-list-item-title class="drawer-text">Terminar Sessão</v-list-item-title>
              </v-list-item>

            </v-list>

          </v-navigation-drawer>

        </v-col>


      </v-row>

    </v-container>





      <v-main style="height: 250px"></v-main>

  </v-toolbar>
</template>

<style scoped>

.navbar {
  border: none;
  outline: none;
  display: flex;
  justify-content: space-between;
  background-color: #427F99;
  padding: 0.75rem 1.5rem;
  color: white;
}

.nav-button {
  background-color: white;
  color: #6e98db; /* same as your login button */
  border-radius: 8px;
  padding: 0.5rem 1rem;
  margin-left: 0.5rem;
  font-weight: bold;
  border: none;
  cursor: pointer;
  transition: background-color 0.2s ease;
  text-decoration: none;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.nav-button:hover {
  background-color: #e0e0e0;
}


.drawer-text {
  color: #427F99;
  font-weight: bold;
}


</style>



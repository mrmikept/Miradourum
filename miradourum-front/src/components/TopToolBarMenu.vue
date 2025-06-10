<script setup>

import LogoButton from "./LogoButton.vue";
import {onMounted, ref} from "vue";
import router from "../router/index.js";
import Location_chip from "./location_chip.vue";

// defineProps({
//   avatarUrl: {
//     type: String,
//     default: 'https://randomuser.me/api/portraits/women/85.jpg',
//   },
//   username: {
//     type: String,
//     default: 'Sandra Adams',
//   },
//   email: {
//     type: String,
//     default: 'sandra_a88@gmail.com',
//   },
// })

defineProps({
  avatarUrl: String,
  username: String,
  email: String
})

const actualUsername = ref('')
const actualAvatarUrl = ref('')
const actualEmail = ref('')


// Estado do perfil
const username = ref('')
const avatarUrl = ref('/default-profile.png')
const email = ref('')

const token = localStorage.getItem('authToken')


const fetchUserProfile = async () => {
  if (!token) {
    router.push('/login')
    return
  }

  try {
    const response = await fetch('http://localhost:8080/user/edit', {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    })

    if (!response.ok) {
      // Se token inválido ou outro erro, redireciona para login
      router.push('/login')
      return
    }

    const data = await response.json()
    username.value = data.username
    avatarUrl.value = data.profileImage || '/default-profile.png'
    email.value = data.email

  } catch (error) {
    console.error('Erro a buscar perfil:', error)
    router.push('/login')
  }
}

onMounted(() => {
  fetchUserProfile()
})


const handleLogout = () => {
  localStorage.removeItem('authToken')  // Remove o token de autenticação
  router.push('/login')                  // Redireciona para a página de login
}

</script>

<template>
<!--  <nav class="navbar">-->
<!--    <div class="navbar-left">-->
<!--      <LogoButton to="/home">-->
<!--      </LogoButton>-->
<!--    </div>-->
<!--    <div class="navbar-right">-->
<!--      <RouterLink v-if="isAdmin" to="/review" class="nav-button">Review</RouterLink>-->

<!--      <div class="location-info" v-if="userLocation">-->
<!--        <span class="location-text">📍 {{ userLocation.lat.toFixed(4) }}, {{ userLocation.lng.toFixed(4) }}</span>-->
<!--      </div>-->
<!--      &lt;!&ndash; Botão de editar perfil &ndash;&gt;-->
<!--      <button class="nav-button" @click="goToProfile">Ver Perfil</button>-->
<!--    </div>-->
<!--  </nav>-->

  <v-toolbar color="#427F99">
    <v-container fluid>
      <v-row align="center" justify="space-between" no-gutters>
        <!-- Left: Logo -->
        <v-col cols="auto">
          <LogoButton to="/home" />
        </v-col>

        <v-col class="d-flex justify-end align-center" cols="auto">
          <location_chip />

          <v-navigation-drawer
              location="right"
              expand-on-hover
              rail
              permanent
          >
            <v-list>
              <v-list-item
                  :prepend-avatar="actualAvatarUrl || avatarUrl"
                  :title="actualUsername || username"
                  :subtitle="actualEmail || email"
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
  border: none;
  outline: none;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  white-space: nowrap;
  background-color: white;
  color: #427F99;
  border-radius: 8px;
  padding: 0.5rem 1rem;
  margin-left: 0.5rem;
  text-decoration: none;
  font-weight: bold;
  transition: background-color 0.2s ease;
}


.nav-button:hover {
  border: none;
  outline: none;
  background-color: #e0e0e0;
}

.drawer-text {
  color: #427F99;
  font-weight: bold;
}


</style>



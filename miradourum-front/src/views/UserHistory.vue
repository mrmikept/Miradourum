<template>
  <div class="history-page">
    <!-- Navbar -->
    <top-tool-bar-menu/>

    <!--    <nav class="navbar">-->
<!--      <div class="navbar-left">-->
<!--        <LogoButton to="/" />-->
<!--        <button class="nav-button" @click="goBack">← Anterior</button>-->
<!--      </div>-->

<!--      <div class="navbar-right">-->
<!--        <button class="nav-button" @click="goEditProfile">Editar Perfil</button>-->
<!--        <button class="nav-button" @click="handleLogout">Terminar Sessão ⎋</button>-->
<!--      </div>-->
<!--    </nav>-->

    <!-- Conteúdo principal -->
    <div class="profile-header">
      <div class="profile-picture-wrapper">
        <img class="profile-picture" :src="profileImage" alt="Foto de Perfil" />
        <h2 class="username-display">{{ username }}</h2>
      </div>
    </div>

    <div class="content-container">
      <!-- Visitas Recentes -->
      <div class="recent-visits">
        <h3>Visitas Recentemente</h3>
        <ul>
          <li v-for="(ponto, index) in visitedPoints" :key="index">{{ ponto.name }}</li>
          <li v-if="visitedPoints.length === 0">Sem visitas recentes.</li>
        </ul>
      </div>

      <!-- Mapa no centro -->
      <div class="map-container" ref="mapContainer"></div>

      <!-- Galeria à direita -->
      <div class="gallery">
        <h3>Galeria</h3>
        <div class="gallery-grid">
          <img
              v-for="img in images"
              :key="img.id"
              :src="img.url"
              :alt="'Foto ' + img.id"
              class="gallery-image"
          />
          <p v-if="images.length === 0">Sem imagens publicadas.</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import LogoButton from '@/components/LogoButton.vue'
import L from 'leaflet'
import 'leaflet/dist/leaflet.css'
import TopToolBarMenu from "../components/TopToolBarMenu.vue";
import Breadcrumbs from "../components/breadcrumbs.vue";

const router = useRouter()

const profileImage = ref('/default-profile.png')
const username = ref('')

// Dados do backend
const visitedPoints = ref([]) // {name, lat, lng}
const images = ref([]) // {id, url, ...}

const token = localStorage.getItem('authToken')

// Funções de navegação
const goBack = () => router.push('/home')
const goEditProfile = () => router.push('/editProfile')
const handleLogout = () => {
  localStorage.removeItem('authToken')
  router.push('/login')
}

// Fetch do perfil básico (foto e nome)
const fetchUserProfile = async () => {
  try {
    const res = await fetch('http://localhost:8080/user/edit', {
      headers: { Authorization: `Bearer ${token}` }
    })
    if (!res.ok) {
      router.push('/login')
      return
    }
    const data = await res.json()
    username.value = data.username
    profileImage.value = data.profileImage || '/default-profile.png'
  } catch {
    router.push('/login')
  }
}

// Fetch pontos visitados
const fetchVisitedPoints = async () => {
  try {
    const res = await fetch('http://localhost:8080/user/pontos', {
      headers: { Authorization: `Bearer ${token}` }
    })
    if (!res.ok) {
      console.warn('Erro a carregar pontos visitados')
      return
    }
    const data = await res.json()
    // Assumindo que cada ponto tem {name, lat, lng}
    visitedPoints.value = data
  } catch (err) {
    console.error('Erro fetch pontos:', err)
  }
}

// Fetch galeria de imagens do utilizador
const fetchImages = async () => {
  try {
    const res = await fetch('http://localhost:8080/user/images', {
      headers: { Authorization: `Bearer ${token}` }
    })
    if (!res.ok) {
      console.warn('Erro a carregar imagens')
      return
    }
    const data = await res.json()
    // Supondo que retorna lista com {id, url}
    images.value = data.sort((a, b) => new Date(b.date) - new Date(a.date)) // ordem cronológica decrescente
  } catch (err) {
    console.error('Erro fetch imagens:', err)
  }
}

// Setup mapa leaflet
const mapContainer = ref(null)
let map = null

const setupMap = () => {
  if (!mapContainer.value) return

  map = L.map(mapContainer.value).setView([38.7169, -9.1399], 13) // Lisboa default

  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '&copy; OpenStreetMap contributors'
  }).addTo(map)

  // Adicionar marcadores para cada ponto visitado
  visitedPoints.value.forEach(ponto => {
    if (ponto.lat && ponto.lng) {
      L.marker([ponto.lat, ponto.lng])
          .addTo(map)
          .bindPopup(ponto.name)
    }
  })

  // Ajustar mapa para mostrar todos pontos
  if (visitedPoints.value.length > 0) {
    const group = new L.featureGroup(
        visitedPoints.value.map(p => L.marker([p.lat, p.lng]))
    )
    map.fitBounds(group.getBounds().pad(0.5))
  }
}

onMounted(async () => {
  await fetchUserProfile()
  await fetchVisitedPoints()
  await fetchImages()
  setupMap()
})
</script>

<style scoped>
.history-page {
  background-color: white;
  min-height: 100vh;
  font-family: sans-serif;
  color: white;
}

.navbar {
  display: flex;
  justify-content: space-between;
  background-color: #427F99;
  padding: 0.75rem 1.5rem;
  color: white;
}

.navbar-left {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.navbar-right {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.nav-button {
  background-color: white;
  color: #427F99;
  border-radius: 8px;
  padding: 0.5rem 1rem;
  font-weight: bold;
  cursor: pointer;
  border: none;
}

.nav-button:hover {
  background-color: #e0e0e0;
}

.profile-picture-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.profile-header {
  text-align: center;
  margin: 2rem 0;
}

.profile-picture-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.profile-picture {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  object-fit: cover;
  border: 4px solid white;
  margin-bottom: 1rem;
  background-color: #427F99;
}

.username-display {
  color: #427F99;
  font-size: 1.5rem;
  font-weight: bold;
}


.content-container {
  display: flex;
  max-width: 1200px;
  margin: 0 auto;
  gap: 2rem;
  padding: 0 1rem;
  color: black;
}

/* Visitas recentas */
.recent-visits {
  flex: 1;
  background-color: #e6f0f5;
  border-radius: 12px;
  padding: 1rem;
  max-height: 400px;
  overflow-y: auto;
}

.recent-visits h3 {
  margin-bottom: 1rem;
  color: #427F99;
}

.recent-visits ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.recent-visits li {
  padding: 0.4rem 0;
  border-bottom: 1px solid #ccc;
  color: #427F99;
}

/* Mapa */
.map-container {
  flex: 2;
  height: 400px;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 0 10px rgba(66, 127, 153, 0.4);
}

/* Galeria */
.gallery {
  flex: 1;
  background-color: #e6f0f5;
  border-radius: 12px;
  padding: 1rem;
  max-height: 400px;
  overflow-y: auto;
  text-align: center;
}

.gallery h3 {
  color: #427F99;
  margin-bottom: 1rem;
}

.gallery-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  justify-content: center;
}

.gallery-image {
  width: 100px;
  height: 100px;
  object-fit: cover;
  border-radius: 8px;
  cursor: pointer;
  border: 2px solid #427F99;
}

/* Responsive */
@media (max-width: 900px) {
  .content-container {
    flex-direction: column;
  }
  .recent-visits, .gallery, .map-container {
    max-height: none;
    width: 100%;
  }
}
</style>

<template>
  <div class="home-page">
    <!-- Navbar -->
    <nav class="navbar">
      <div class="navbar-left">
        <LogoButton to="/" />
        <button class="nav-button" @click="goBack">← Anterior</button>
      </div>

      <div class="navbar-right">
        <button v-if="!isPremium" class="nav-button" @click="goPremium">Torne-se Premium</button>
        <button class="nav-button" @click="handleLogout">Terminar Sessão ⎋</button>
      </div>
    </nav>
g
    <!-- Profile Content -->
    <div class="profile-container">
      <div class="profile-picture-wrapper">
        <img class="profile-picture" :src="profileImage" alt="Foto de Perfil" />
        <button class="change-photo-button" @click="alterarFoto">Alterar Foto de Utilizador</button>
      </div>

      <h2 class="username-display">{{ username }}</h2>

      <div class="form-group">
        <label for="username">Nome de Utilizador</label>
        <input v-model="username" id="username" type="text" />
      </div>



      <div class="buttons">
        <button class="save-button" @click="guardarPerfil">Guardar</button>
        <button class="nav-button" @click="alterarPassword">Alterar Palavra Passe</button>
      </div>


    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { ref, onMounted } from 'vue'
import LogoButton from '@/components/LogoButton.vue'

const router = useRouter()

const goBack = () => {
  router.push('/home')
}

const handleLogout = () => {
  localStorage.removeItem('authToken')  // Remove o token de autenticação
  router.push('/login')                  // Redireciona para a página de login
}

const goPremium = () => {
  router.push('/become-premium')
}


// Estado do perfil
const username = ref('')
const profileImage = ref('/default-profile.png')

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
    profileImage.value = data.profileImage || '/default-profile.png'

  } catch (error) {
    console.error('Erro a buscar perfil:', error)
    router.push('/login')
  }
}

// Parte do plano, premium ou não
const isPremium = ref(false)

const checkPremiumStatus = async () => {
  try {
    const response = await fetch('http://localhost:8080/user/premium', {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${token}`
      }
    })

    if (response.ok) {
      const result = await response.json()
      isPremium.value = result.isPremium // ou `true` diretamente se a API retorna booleano
    } else {
      console.warn('Erro ao verificar status premium.')
    }
  } catch (err) {
    console.error('Erro na verificação de premium:', err)
  }
}

onMounted(() => {
  fetchUserProfile()
  checkPremiumStatus()
})

// Funções para guardar alterações, alterar foto, logout etc aqui
const guardarPerfil = () => {
  fetch('http://localhost:8080/user/profile', {
    method: 'PUT',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      username: username.value,
    })
  })
      .then(res => {
        if (res.ok) alert('Perfil guardado com sucesso!')
        else alert('Erro ao guardar perfil.')
      })
      .catch(() => alert('Erro na rede.'))
}
</script>

<style scoped>
.home-page {
  background-color: white;
  min-height: 100vh;
  color: white;
  font-family: sans-serif;
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
  flex-direction: column;
  align-items: flex-start;
  gap: 0.5rem;
}


.nav-button {
  background-color: white;
  color: #427F99;
  border-radius: 8px;
  padding: 0.5rem 1rem;
  margin-left: 0.5rem;
  text-decoration: none;
  font-weight: bold;
  cursor: pointer;
  border: none;
}

.nav-button:hover {
  background-color: #e0e0e0;
}

.profile-container {
  max-width: 500px;
  background-color: #427F99;
  color: white;
  margin: 2rem auto;
  padding: 2rem;
  border-radius: 16px;
  text-align: center;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.2);
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
}

.change-photo-button {
  background-color: white;
  color: #427F99;
  border: none;
  border-radius: 8px;
  padding: 0.4rem 1rem;
  font-weight: bold;
  cursor: pointer;
}

.change-photo-button:hover {
  background-color: #f0f0f0;
}

.username-display {
  margin: 1rem 0;
}

.form-group {
  margin-bottom: 1.5rem;
  text-align: left;
}

.form-group label {
  display: block;
  margin-bottom: 0.25rem;
  font-weight: bold;
}

.form-group input {
  width: 100%;
  padding: 0.6rem;
  border-radius: 8px;
  border: none;
  font-size: 1rem;
}

.buttons {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.save-button {
  background-color: #3cb371;
  color: white;
  padding: 0.75rem;
  border: none;
  border-radius: 8px;
  font-weight: bold;
  cursor: pointer;
}

.save-button:hover {
  background-color: #2e8b57;
}

@media (max-width: 768px) {
  .profile-container {
    width: 90%;
    margin: 1.5rem auto;
  }

  .navbar {
    flex-direction: column;
    text-align: center;
  }

  .nav-button {
    margin: 0.5rem auto;
  }
}
</style>

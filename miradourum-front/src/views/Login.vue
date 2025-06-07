<template>
  <div class="home-page">
    <nav class="navbar">
      <div class="navbar-left">
        <LogoButton to="/" />
      </div>
      <div class="navbar-right">
        
      </div>
    </nav>

    <div class="hero">
      <img class="hero-image" src="@/assets/baloicoog.png" alt="Miradouro" />
      <div class="hero-text">
        <h1><strong>MiradourUM</strong></h1>
        <p>Descobre os melhores miradouros e baloiços ao teu alcance!</p>
        <p>
          Com a nossa app, podes explorar a beleza das paisagens.
          A sensação de liberdade nunca foi tão boa!
          Encontra miradouros espetaculares e baloiços escondidos em locais únicos,
          planeia as tuas aventuras ao ar livre e aproveita cada momento.
        </p>
      </div>

  <ErrorPopup v-if="errorMessage" :text="errorMessage" />

    <!-- Login Popup -->
    <div class="login-popup">
      <h2>Iniciar Sessão</h2>
      <form @submit.prevent="handleLogin">
        <input type="email" v-model="email" placeholder="Email" required />
        <input type="password" v-model="password" placeholder="Password" required />
        <button type="submit">Entrar</button>
      </form>
      <p class="register-text">
        Ainda não tem conta?
        <RouterLink to="/register">Registe-se</RouterLink>
      </p>
    
      <p class="forgot-password">
        <RouterLink to="/reset-password">Esqueceu a palavra-passe?</RouterLink>
      </p>
    </div>

      <img class="corner-image" src="@/assets/hand.png" alt="Decoration" />

    </div>

  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import LogoButton from '@/components/LogoButton.vue'
import ErrorPopup from '@/components/ErrorPopup.vue'

const router = useRouter()
const route = useRoute()

const email = ref('')
const password = ref('')
const errorMessage = ref('')
const showReset = ref(false)

function showError(msg) {
  errorMessage.value = msg
  setTimeout(() => {
    errorMessage.value = ''
  }, 3000) // hide after 3 seconds
}
const handleLogin = async () => {
  errorMessage.value = ''
  try {
    const response = await fetch('http://localhost:8080/auth/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        email: email.value,
        password: password.value,
      }),
    })

    if (response.status === 401) {
      showError('Email ou palavra-passe incorretos.')
      showReset.value = true
      return
    }


    if (!response.ok) {
      showError('Erro ao tentar iniciar sessão.')
      return
    }

    const token = await response.text()
    localStorage.setItem('authToken', token)
    router.push('/home')
  } catch (error) {
    showError('Erro de rede. Tente novamente.')
    console.error(error)
  }
}


</script>

<style scoped>

.forgot-password {
  margin-top: 1rem;
}

.forgot-password a {
  color: #ffffff;
  text-decoration: underline;
  font-size: 0.9rem;
}


.home-page {
  background-color: white;
  min-height: 100vh;
  color: white;
  font-family: sans-serif;
  position: relative;
}

.navbar {
  display: flex;
  justify-content: space-between;
  background-color: #427F99;
  padding: 0.75rem 1.5rem;
  color: white;
}

.nav-button {
  background-color: white;
  color: #6e98db;
  border-radius: 8px;
  padding: 0.5rem 1rem;
  margin-left: 0.5rem;
  text-decoration: none;
  font-weight: bold;
  transition: background-color 0.2s ease;
}

.nav-button:hover {
  background-color: #e0e0e0;
}

.hero {
  position: relative;
  text-align: center;
  z-index: 1;
}


.hero-image {
  width: 100%;
  max-height: 500px;
  object-fit: cover;
}

.hero-text {
  background-color: white;
  color: black;
  padding: 1rem;
  max-width: 600px;
  margin: 0 auto;
}

.login-popup {
  position: absolute;
  top: 40%;
  right: 5%;
  transform: translateY(-50%);
  background-color: #427F99;
  color: white;
  padding: 2rem;
  border-radius: 12px;
  width: 400px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.2);
  z-index: 100; /* Higher than corner image */
  text-align: center;
}

.login-popup h2 {
  margin-bottom: 1rem;
}

.login-popup input {
  width: 100%;
  padding: 0.75rem;
  margin: 0.5rem 0;
  border: none;
  border-radius: 8px;
  font-size: 1rem;
}

.login-popup button {
  width: 100%;
  padding: 0.75rem;
  background-color: white;
  color: #427F99;
  border: none;
  border-radius: 8px;
  font-weight: bold;
  cursor: pointer;
  margin-top: 0.5rem;
}

.login-popup button:hover {
  background-color: #f0f0f0;
}

.register-text {
  font-size: 0.85rem;
  margin-top: 1rem;
}

.register-text a {
  color: #fff;
  text-decoration: underline;
}

/* 📱 Mobile tweaks */
@media (max-width: 768px) {
  .login-popup {
    position: static;
    transform: none;
    width: 90%;
    margin: 2rem auto;
  }



  .navbar {
    flex-direction: column;
    text-align: center;
  }

  .navbar-left,
  .navbar-right {
    width: 100%;
    margin-bottom: 0.5rem;
    display: flex;
    justify-content: center;
    flex-direction: column;
    gap: 0.5rem;
  }

  .nav-button {
    width: 60%;
    margin: 0 auto;
  }
}
.corner-image {
  position: fixed;
  bottom: 0%;
  right: 20px;
  width: 300px;
  height: auto;
  z-index: 10;
  pointer-events: none;
}

/* Responsive override */
@media (max-width: 768px) {
  .corner-image {
    position: static;
    display: block;
    margin: 2rem auto 1rem auto;
    width: 150px; /* smaller on small screens */
  }
}
</style>

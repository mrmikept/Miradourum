<template>
  <div class="premium-page">
    <!-- Navbar -->
    <top-tool-bar-menu/>

    <!-- Premium Form -->
    <h1>Torne-se Premium</h1>

    <form class="premium-form" @submit.prevent="confirmarUpgrade">
      <div class="form-group">
        <label for="cardName">Nome do Titular</label>
        <input type="text" id="cardName" v-model="cardName" required />
      </div>

      <div class="form-group">
        <label for="cardNumber">Número do Cartão</label>
        <input type="text" id="cardNumber" v-model="cardNumber" required />
      </div>

      <div class="form-group">
        <label for="expiry">Data de Validade</label>
        <input type="text" id="expiry" v-model="expiry" placeholder="MM/AA" required />
      </div>

      <div class="form-group">
        <label for="cvc">CVC</label>
        <input type="text" id="cvc" v-model="ccv" required />
      </div>

      <button class="confirm-button" type="submit">Confirmar</button>
    </form>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import LogoButton from '@/components/LogoButton.vue'
import {UserStore} from "@/store/userStore.js";
const API_BASE_URL = import.meta.env.VITE_API_URL;
import TopToolBarMenu from "../components/TopToolBarMenu.vue";

const router = useRouter()
const cardName = ref('')
const cardNumber = ref('')
const expiry = ref('')
const ccv = ref('')
const userStore = UserStore()

const token = userStore.authToken

const goBack = () => {
  router.push('/editProfile')
}

const handleLogout = () => {
  localStorage.removeItem('authToken')
  router.push('/login')
}

const confirmarUpgrade = async () => {
  try {
    const response = await fetch(`${API_BASE_URL}/user/upgrade-premium`, {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        cardName: cardName.value,
        cardNumber: cardNumber.value,
        ccv: ccv.value,
        expiry: expiry.value,
      })
    })

    if (response.ok) {
      alert('Upgrade Premium realizado com sucesso!')
      router.push('/home')
    } else {
      alert('Erro ao fazer upgrade.')
    }
  } catch (err) {
    console.error('Erro ao fazer upgrade:', err)
    alert('Erro de rede.')
  }
}
</script>

<style scoped>
.premium-page {
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

.navbar-right {
  display: flex;
  align-items: center;
  gap: 1rem;
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

h1 {
  text-align: center;
  margin: 2rem 0 1rem;
  color: #427F99;
}

.premium-form {
  max-width: 500px;
  margin: 0 auto;
  background-color: #427F99;
  padding: 2rem;
  border-radius: 16px;
  color: white;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.2);
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
}

.form-group {
  text-align: left;
}

label {
  display: block;
  font-weight: bold;
  margin-bottom: 0.5rem;
}

input {
  width: 100%;
  padding: 0.75rem;
  border-radius: 8px;
  border: none;
  font-size: 1rem;
  background-color: white; /* <-- Add this line */
  color: black; /* Optional: to make sure text is readable */
}


.confirm-button {
  background-color: #3cb371;
  color: white;
  padding: 0.75rem;
  border: none;
  border-radius: 8px;
  font-weight: bold;
  cursor: pointer;
}

.confirm-button:hover {
  background-color: #2e8b57;
}

@media (max-width: 768px) {
  .navbar {
    flex-direction: column;
    text-align: center;
  }

  .nav-button {
    margin: 0.5rem auto;
  }

  .premium-form {
    width: 90%;
    margin: 1rem auto;
  }
}
</style>

<template>
  <div class="details-page">
    <!-- Navbar -->
    <nav class="navbar">
      <div class="navbar-left">
        <LogoButton to="/" />
        <button class="nav-button" @click="goBack">← Anterior</button>
      </div>

      <div class="navbar-right">
        <button class="nav-button" @click="handleLogout">Terminar Sessão ⎋</button>
      </div>
    </nav>

    <!-- Conteúdo principal -->
    <div class="content-container">
      <!-- Detalhes do ponto (lado esquerdo) -->
      <div class="point-details">
        <h2>{{ pointDetails.name || 'Carregando...' }}</h2>

        <div class="details-section">
          <h3>Informações</h3>
          <p><strong>Descrição:</strong> {{ pointDetails.description || 'Sem descrição.' }}</p>
          <p><strong>Localização:</strong> {{ pointDetails.location || 'Não disponível' }}</p>
          <p><strong>Categoria:</strong> {{ pointDetails.category || 'Não especificada' }}</p>
          <!-- Podes adicionar mais campos conforme o objeto recebido -->
        </div>

        <div class="buttons">
          <button class="action-btn" @click="markAsVisited" :disabled="loadingVisit">
            {{ loadingVisit ? 'A processar...' : 'Marcar como visitado' }}
          </button>
          <button class="action-btn" @click="openCommentModal">Adicionar comentário</button>
        </div>
      </div>

      <!-- Reviews (lado direito) -->
      <div class="reviews-section">
        <h3>Reviews</h3>
        <ul v-if="reviews.length > 0">
          <li v-for="review in reviews" :key="review.id" class="review-item">
            <p><strong>{{ review.user }}</strong> - <em>{{ formatDate(review.date) }}</em></p>
            <p>{{ review.comment }}</p>
            <p>⭐ {{ review.rating }}/5</p>
            <hr />
          </li>
        </ul>
        <p v-else>Sem reviews disponíveis.</p>
      </div>
    </div>

    <!-- Modal para adicionar comentário -->
    <div v-if="showCommentModal" class="modal-overlay" @click.self="closeCommentModal">
      <div class="modal-content">
        <h3>Adicionar Comentário</h3>
        <textarea v-model="newComment" placeholder="Escreve o teu comentário aqui..."></textarea>
        <label>
          Avaliação:
          <select v-model="newRating">
            <option disabled value="">Seleciona a avaliação</option>
            <option v-for="n in 5" :key="n" :value="n">{{ n }}</option>
          </select>
        </label>
        <div class="modal-buttons">
          <button @click="submitComment" :disabled="loadingComment">
            {{ loadingComment ? 'A enviar...' : 'Enviar' }}
          </button>
          <button @click="closeCommentModal">Cancelar</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import LogoButton from '@/components/LogoButton.vue'

const router = useRouter()
const route = useRoute()

const token = localStorage.getItem('authToken')

const pointDetails = ref({})
const reviews = ref([])

const loadingVisit = ref(false)
const loadingComment = ref(false)

const showCommentModal = ref(false)
const newComment = ref('')
const newRating = ref('')

// Funções de navegação
const goBack = () => router.back()
const goEditProfile = () => router.push('/editProfile')
const handleLogout = () => {
  localStorage.removeItem('authToken')
  router.push('/login')
}

// Pega o id do ponto da rota
const pointId = route.params.id

// Fetch detalhes do ponto
const fetchPointDetails = async () => {
  try {
    const res = await fetch(`http://localhost:8080/pi/details/${pointId}`, {
      headers: { Authorization: `Bearer ${token}` }
    })
    if (!res.ok) {
      console.warn('Erro ao carregar detalhes do ponto')
      return
    }
    const data = await res.json()
    pointDetails.value = data
  } catch (err) {
    console.error('Erro fetch detalhes:', err)
  }
}

// Fetch reviews do ponto
const fetchReviews = async () => {
  try {
    const res = await fetch(`http://localhost:8080/pi/${pointId}/reviews`, {
      headers: { Authorization: `Bearer ${token}` }
    })
    if (!res.ok) {
      console.warn('Erro ao carregar reviews')
      return
    }
    const data = await res.json()
    reviews.value = data
  } catch (err) {
    console.error('Erro fetch reviews:', err)
  }
}

// Marcar como visitado
const markAsVisited = async () => {
  loadingVisit.value = true
  try {
    const res = await fetch(`http://localhost:8080/pi/${pointId}/visit`, {
      method: 'POST',
      headers: {
        Authorization: `Bearer ${token}`,
        'Content-Type': 'application/json',
      }
    })
    if (!res.ok) {
      alert('Erro ao marcar como visitado')
      loadingVisit.value = false
      return
    }
    alert('Ponto marcado como visitado com sucesso!')
  } catch (err) {
    alert('Erro ao marcar como visitado')
    console.error(err)
  } finally {
    loadingVisit.value = false
  }
}

// Modal comentário
const openCommentModal = () => {
  newComment.value = ''
  newRating.value = ''
  showCommentModal.value = true
}
const closeCommentModal = () => {
  showCommentModal.value = false
}

// Submeter comentário
const submitComment = async () => {
  const ratingValue = Number(newRating.value)

  if (!newComment.value.trim() || !ratingValue || ratingValue < 1 || ratingValue > 5) {
    alert('Por favor preencha o comentário e uma avaliação válida entre 1 e 5')
    return
  }

  const payload = {
    comment: newComment.value.trim(),
    rating: ratingValue,
    images: []
  }

  console.log('Payload enviado:', payload)

  loadingComment.value = true
  try {
    const res = await fetch(`http://localhost:8080/pi/${pointId}/reviews`, {
      method: 'POST',
      headers: {
        Authorization: `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(payload)
    })
    if (!res.ok) {
      const errorText = await res.text()
      console.error('Resposta com erro:', errorText)
      alert('Erro ao enviar comentário')
      return
    }

    alert('Comentário enviado com sucesso!')
    closeCommentModal()
    await fetchReviews()
  } catch (err) {
    alert('Erro ao enviar comentário')
    console.error(err)
  } finally {
    loadingComment.value = false
  }
}


const formatDate = (dateString) => {
  const d = new Date(dateString)
  return d.toLocaleDateString('pt-PT', { day: '2-digit', month: 'short', year: 'numeric' })
}

onMounted(() => {
  fetchPointDetails()
  fetchReviews()
})
</script>

<style scoped>
.details-page {
  background-color: white;
  min-height: 100vh;
  font-family: sans-serif;
  color: black;
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

.content-container {
  display: flex;
  max-width: 1200px;
  margin: 2rem auto;
  gap: 2rem;
  padding: 0 1rem;
  color: black;
}

/* Lado esquerdo - detalhes */
.point-details {
  flex: 1;
  background-color: #e6f0f5;
  border-radius: 12px;
  padding: 1rem 1.5rem;
  max-height: 600px;
  overflow-y: auto;
}

.point-details h2 {
  color: #427F99;
  margin-bottom: 1rem;
}

.details-section p {
  margin: 0.5rem 0;
}

/* Botões */
.buttons {
  margin-top: 2rem;
  display: flex;
  gap: 1rem;
  flex-wrap: wrap;
}

.action-btn {
  background-color: #427F99;
  color: white;
  padding: 0.75rem 1.2rem;
  border-radius: 8px;
  border: none;
  font-weight: bold;
  cursor: pointer;
  flex-grow: 1;
}

.action-btn:disabled {
  background-color: #7ea5b7;
  cursor: not-allowed;
}

/* Lado direito - reviews */
.reviews-section {
  flex: 1;
  background-color: #e6f0f5;
  border-radius: 12px;
  padding: 1rem 1.5rem;
  max-height: 600px;
  overflow-y: auto;
}

.reviews-section h3 {
  color: #427F99;
  margin-bottom: 1rem;
}

.review-item {
  margin-bottom: 1rem;
  text-align: left;
  color: #333;
}

.review-item hr {
  border: none;
  border-bottom: 1px solid #ccc;
  margin-top: 0.5rem;
}

/* Modal */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0,0,0,0.6);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 12px;
  padding: 1.5rem;
  width: 90%;
  max-width: 500px;
  box-shadow: 0 4px 15px rgba(0,0,0,0.3);
}

.modal-content textarea {
  width: 100%;
  height: 100px;
  resize: vertical;
  margin-bottom: 1rem;
  padding: 0.5rem;
  font-size: 1rem;
}

.modal-content select {
  width: 100%;
  padding: 0.5rem;
  margin-bottom: 1rem;
  font-size: 1rem;
}

.modal-buttons {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
}

.modal-buttons button {
  padding: 0.5rem 1rem;
  font-weight: bold;
  border: none;
  border-radius: 8px;
  cursor: pointer;
}

.modal-buttons button:first-child {
  background-color: #427F99;
  color: white;
}

.modal-buttons button:last-child {
  background-color: #ccc;
  color: black;
}

.modal-buttons button:disabled {
  background-color: #7ea5b7;
  cursor: not-allowed;
}

/* Responsive */
@media (max-width: 900px) {
  .content-container {
    flex-direction: column;
  }
  .point-details, .reviews-section {
    max-height: none;
    width: 100%;
  }
}
</style>

<template>
  <div class="details-page">
    <TopToolBarMenu />

    <!-- Conteúdo principal -->
    <div class="content-container">
      <!-- Detalhes do ponto (lado esquerdo) -->
      <div class="point-details">
        <h2>{{ pointDetails.name || 'Carregando...' }}</h2>

        <div class="details-section">
          <h3>Informações</h3>
          <p><strong>Data de Criação:</strong> {{ formatDateSemHoras(pointDetails.creationDate) }}</p>
          <p><strong>Descrição:</strong> {{ pointDetails.description || 'Sem descrição.' }}</p>

          <!-- Imagens do ponto -->
          <div v-if="pointDetails.images && pointDetails.images.length">
            <h4>Imagens</h4>
            <div class="point-images">
              <img
                v-for="img in pointDetails.images"
                :key="img.id"
                :src="img.url"
                alt="Imagem do ponto"
                class="point-image"
                @click="openImage(img.url)"
              />
            </div>
          </div>

          <!-- Modal da imagem -->
          <div v-if="enlargedImage" class="modal" @click="enlargedImage = null">
            <img :src="enlargedImage" alt="Imagem ampliada" class="modal-image" />
          </div>

          <!-- Outras informações -->
          <p><strong>Premium:</strong> {{ pointDetails.premium ? 'Sim' : 'Não' }}</p>
          <p><strong>Latitude:</strong> {{ pointDetails.latitude }}</p>
          <p><strong>Longitude:</strong> {{ pointDetails.longitude }}</p>
          <p><strong>Distância:</strong> {{ pointDetails.distanceFromUser ? pointDetails.distanceFromUser.toFixed(2) + ' km' : 'N/A' }}</p>
          <p><strong>Score:</strong> {{ pointDetails.score?.toFixed(1) }}</p>
          <p><strong>Dificuldade:</strong> {{ pointDetails.difficulty }}</p>
        </div>

        <div class="buttons">
          <!-- <button class="action-btn" @click="markAsVisited" :disabled="loadingVisit">
            {{ loadingVisit ? 'A processar...' : 'Marcar como visitado' }}
          </button> -->
          <button class="action-btn" @click="openCommentModal">Adicionar comentário</button>
        </div>
      </div> <!-- fecha .point-details -->

      <!-- Reviews (lado direito) -->
      <div class="reviews-section">
        <h3>Reviews</h3>
        <ul v-if="reviews.length > 0">
          <li v-for="review in sortedReviews" :key="review.creationDate" class="review-item">
            <p><strong>Data:</strong> {{ formatDate(review.creationDate) }}</p>
            <p><strong>Comentário:</strong> {{ review.comment }}</p>
            <p><strong>Avaliação:</strong> {{ review.rating }}/5 ⭐ </p>

            <!-- Imagens da review -->
            <div v-if="review.images && review.images.length > 0" class="review-pictures">
              <p><strong>Image{{ review.images.length > 1 ? 'ns' : 'm' }}:</strong></p>
              <div class="point-images">
                <img
                  v-for="(img, index) in review.images"
                  :key="index"
                  class="point-image"
                  :src="typeof img === 'string' ? img : img.url"
                  alt="Foto da review"
                  @click="openImage(img.url)"
                />
              </div>
            </div>

            <!-- Ações da review -->
            <div v-if="parseInt(review.userid) === parseInt(userStore.id)" class="review-actions">
              <button class="edit-btn" @click="editReview(review)">Editar</button>
              <button class="delete-btn" @click="deleteReview(review.id)">Apagar</button>
            </div>

            <hr />
          </li>
        </ul>
        <p v-else>Sem reviews disponíveis.</p>
      </div> <!-- fecha .reviews-section -->
    </div> <!-- fecha .content-container -->

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

        <label class="file-upload-label">
          Adicionar imagem
          <input type="file" accept="image/*" @change="handleReviewImageUpload" />
        </label>

        <div v-if="reviewImagePreview" class="preview">
          <img :src="reviewImagePreview" alt="Preview da imagem" />
        </div>

        <div class="modal-buttons">
          <button @click="submitComment" :disabled="loadingComment">
            {{ loadingComment ? 'A enviar...' : 'Enviar' }}
          </button>
          <button @click="closeCommentModal">Cancelar</button>
        </div>
      </div>
    </div>

    <!-- Popup de erro -->
    <ErrorPopup v-if="showError" :text="errorText" />
    <SuccessPopup v-if="showSuccess" :text="successText" />

  </div>
</template>


<script setup>
import { ref, onMounted, computed } from 'vue'
import { calculateDistance } from '@/utils/distance.js'
import { useRouter, useRoute } from 'vue-router'
import LogoButton from '@/components/LogoButton.vue'
import { S3Client } from '@aws-sdk/client-s3'
import { Upload } from '@aws-sdk/lib-storage'
import axios from "axios";
import {UserStore} from "@/store/userStore.js";
import TopToolBarMenu from "../components/TopToolBarMenu.vue";
import ErrorPopup from "../components/ErrorPopup.vue"
import SuccessPopup from "../components/SuccessPopup.vue"

const editingReviewId = ref(null)
const reviewImageFile = ref(null)
const reviewImagePreview = ref('')
const reviewImage = ref('/default-review.png')
const router = useRouter()
const route = useRoute()
const pi = ref(null)
const userStore = UserStore()

const showError = ref(false)
const errorText = ref('')
const showSuccess = ref(false)
const successText = ref('')


const token = userStore.authToken;

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

function displayError(msg) {
  errorText.value = msg
  showError.value = true
  setTimeout(() => {
    showError.value = false
  }, 3000) // popup desaparece após 3 segundos
}

function displaySuccess(msg) {
  successText.value = msg
  showSuccess.value = true
  setTimeout(() => {
    showSuccess.value = false
  }, 3000) // desaparece após 3 segundos
}


const sortedReviews = computed(() => {
  return reviews.value.slice().sort((a, b) => new Date(b.creationDate) - new Date(a.creationDate))
})


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
    const { lat, lng } = userStore.location
    const enrichedPonto = {
      ...data,
      distanceFromUser: lat && lng
        ? calculateDistance(lat, lng, data.latitude, data.longitude)
        : null
    }

    pointDetails.value = enrichedPonto
    console.log('Detalhes do ponto recebidos:', JSON.stringify(enrichedPonto, null, 2))
    
    reviewImage.value = data.reviewImage || '/default-review.png'
  } catch (err) {
    console.error('Erro fetch detalhes:', err)
  }
}

const enlargedImage = ref(null)

function openImage(url) {
  enlargedImage.value = url
}





const editReview = (review) => {
  newComment.value = review.comment
  newRating.value = review.rating
  editingReviewId.value = review.id
  showCommentModal.value = true
}


const deleteReview = async (reviewId) => {
  if (!confirm("Tens a certeza que queres apagar esta review?")) return

  try {
    const res = await axios.delete(`http://localhost:8080/reviews/${reviewId}`, {
      headers: { Authorization: `Bearer ${token}` }
    })

    if (res.status === 200) {
      displaySuccess('Review apagada com sucesso!')
      await fetchReviews()
      await fetchPointDetails()
    } else {
      displayError("Erro ao apagar review.")
    }
  } catch (err) {
    console.error("Erro ao apagar review", err)
    alert("Erro inesperado.")
  }
}







//upload de imagens
const handleReviewImageUpload = (event) => {
  const file = event.target.files[0]
  if (!file) return
  reviewImageFile.value = file
  reviewImagePreview.value = URL.createObjectURL(file)
}

const s3Client = new S3Client({
  region: 'us-east-1',
  endpoint: 'http://localhost:9000',
  credentials: {
    accessKeyId: 'admin',
    secretAccessKey: 'admin123',
  },
  forcePathStyle: true,
})

const uploadReviewImageToMinIO = async (file) => {
  const fileName = `review-${Date.now()}-${file.name}`
  const upload = new Upload({
    client: s3Client,
    params: {
      Bucket: 'review-images',
      Key: fileName,
      Body: file,
      ContentType: file.type,
      ACL: 'public-read',
    },
  })
  await upload.done()
  return `http://localhost:9000/review-images/${fileName}`
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

    //console.log('Reviews recebidas do backend:', JSON.stringify(data, null, 2))
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
  editingReviewId.value = null
}

// Submeter comentário
async function submitComment() {
  if (!pointDetails.value || !pointId) {
    console.error("Erro: PI ainda não carregado.");
    return;
  }
if (!newComment.value.trim()) {
    displayError('O campo comentário é obrigatório.')
    return
  }
  if (!newRating.value) {
    displayError('Por favor, selecione uma avaliação.')
    return
  }
  
  if (!pointDetails.value || !pointId) {
    console.error("Erro: PI ainda não carregado.");
    return;
  }
  loadingComment.value = true

  try {
    let imageUrls = []

    if (reviewImageFile.value) {
      const imageUrl = await uploadReviewImageToMinIO(reviewImageFile.value)
      imageUrls.push(imageUrl)
    }

    const payload = {
      rating: newRating.value,
      comment: newComment.value,
      images: imageUrls
    }

    let response
    if (editingReviewId.value) {
      // EDIT mode
      response = await axios.put(
        `http://localhost:8080/reviews/${editingReviewId.value}`,
        payload,
        { headers: { Authorization: `Bearer ${token}` } }
      )
    } else {
      // CREATE mode
      response = await axios.post(
        `http://localhost:8080/pi/${pointId}/reviews`,
        payload,
        { headers: { Authorization: `Bearer ${token}` } }
      )
    }

    console.log("Comentário enviado/editado com sucesso", response.data)

    await fetchPointDetails()
    await fetchReviews()
    displaySuccess(editingReviewId.value ? 'Review atualizada com sucesso!' : 'Review adicionada com sucesso!')
    closeCommentModal()
    reviewImageFile.value = null
    reviewImagePreview.value = ''
    editingReviewId.value = null // reset after submit
  } catch (error) {
    console.error("Erro ao enviar comentário", error)
  } finally {
    loadingComment.value = false
  }
}


const formatDate = (dateString) => {
  if (!dateString) return 'Data não disponível'

  let d = new Date(dateString)

  if (isNaN(d)) {
    d = new Date(dateString + 'T00:00:00')
  }

  if (isNaN(d)) return 'Data inválida'

  return d.toLocaleString('pt-PT', {
    day: '2-digit',
    month: 'short',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  })
}

const formatDateSemHoras = (dateString) => {
  if (!dateString) return 'Data não disponível'

  // Tenta criar a data diretamente
  let d = new Date(dateString)

  // Se inválido, tenta acrescentar T00:00:00
  if (isNaN(d)) {
    d = new Date(dateString + 'T00:00:00')
  }

  if (isNaN(d)) return 'Data inválida'

  return d.toLocaleDateString('pt-PT', { day: '2-digit', month: 'short', year: 'numeric' })
}




onMounted(() => {
  fetchPointDetails()
  fetchReviews()
})
</script>

<style scoped>
.point-images {
  display: flex;
  gap: 10px;
  margin-top: 0.5rem;
  flex-wrap: wrap;
}

.point-image {
  width: 120px;
  height: 80px;
  object-fit: cover;
  border-radius: 8px;
  border: 2px solid #427F99;
}

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
.review-actions {
  margin-top: 0.5rem;
  display: flex;
  gap: 0.5rem;
  justify-content: flex-end;
}

.review-actions button {
  padding: 0.4rem 0.8rem;
  font-size: 0.9rem;
  border-radius: 6px;
  border: none;
  cursor: pointer;
  font-weight: bold;
  transition: background-color 0.2s ease, color 0.2s ease;
}

.review-actions .edit-btn {
  background-color: #ffd966; /* Light yellow */
  color: #333;
}

.review-actions .edit-btn:hover {
  background-color: #ffcf33;
}

.review-actions .delete-btn {
  background-color: #e06666; /* Soft red */
  color: white;
}

.review-actions .delete-btn:hover {
  background-color: #d43f3f;
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
  z-index: 100;
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
  border: 1px solid black;
  border-radius: 6px;
}

.modal-content select {
  width: 100%;
  padding: 0.5rem;
  margin-bottom: 1rem;
  font-size: 1rem;
  border: 1px solid black;
  border-radius: 6px;
}

.modal-content .preview {
  display: flex;
  justify-content: center;
  margin: 1rem 0;
}

.modal-content .preview img {
  max-width: 120px;
  max-height: 120px;
  width: auto;
  height: auto;
  border-radius: 8px;
  border: 2px solid #427F99;
  object-fit: cover;
}

/* File upload label styling for modal */
.modal-content .file-upload-label {
  display: block;
  margin: 1rem 0;
  background-color: #f8f9fa;
  color: #427F99;
  padding: 0.75rem;
  border-radius: 8px;
  cursor: pointer;
  font-size: 1rem;
  text-align: center;
  border: 2px solid #427F99;
  transition: all 0.2s ease;
  width: 100%;
  box-sizing: border-box;
}

.modal-content .file-upload-label:hover {
  background-color: #e9ecef;
}

.modal-content .file-upload-label input {
  display: none;
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

.review-picture-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.review-picture {
  width: 100px;
  height: 100px;
  border-radius: 0%;
  object-fit: cover;
  border: 4px solid white;
  margin-bottom: 1rem;
}


.point-image:hover {
  transform: scale(1.05);
}

.modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 999;
}

.modal-image {
  max-width: 90%;
  max-height: 90%;
  border-radius: 8px;
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

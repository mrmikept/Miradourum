<template>
  <div class="home-page">
    <nav class="navbar">
      <div class="navbar-left">
        <LogoButton to="/home">
        </LogoButton>
        
      </div>
      <div class="navbar-right">

      </div>
    </nav>
    <p class = mimi>
      if you are here you sucessfully have a token :)
    </p>
    
  </div>
</template>

<script setup>
import { useRoute, useRouter } from 'vue-router'
import LogoButton from '@/components/LogoButton.vue'

const router = useRouter()
const route = useRoute()

const handleLogoClick = () => {
  if (route.path === '/') {
    // Do something special if on homepage (e.g., scroll to top)
    window.scrollTo({ top: 0, behavior: 'smooth' })
  } else {
    router.push('/')
  }
}
</script>

<style scoped>
.mimi{
  color: black;
}
.home-page {
  background-color: white;
  min-height: 100vh;
  color: white;
  font-family: sans-serif;
}

.navbar {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  align-items: center;
  background-color: #427F99;
  padding: 0.75rem 1.5rem;
  color: white;
}

@media (max-width: 768px) {
  .navbar {
    flex-direction: column;
    align-items: stretch;
    text-align: center;
    gap: 1rem;
  }

  .navbar-left,
  .navbar-right {
    width: 100%;
    display: flex;
    justify-content: center;
    flex-direction: column;
    gap: 0.5rem;
  }

  .navbar-right .nav-button {
    width: 60%;
    margin: 0 auto;
  }

  .navbar-left .logo-button {
    justify-content: center;
  }
}


.navbar-left .logo-button {
  display: flex;
  align-items: center;
  background: none;
  border: none;
  color: white;
  font-size: 1.25rem;
  cursor: pointer;
}

</style>

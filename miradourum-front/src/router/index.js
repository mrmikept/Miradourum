import { createRouter, createWebHistory } from 'vue-router'
import Home from '@/views/Home.vue'
import Login from '@/views/Login.vue'
import NotFoundComponent from '@/views/NotFound.vue'
import Register from '@/views/Register.vue'
import HomeLogged from '@/views/HomeLogged.vue'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home,
  },
  {
    path: '/login',
    name: 'Login',
    component: Login,
  },
  {
    path: '/register',
    name: 'Register',
    component: Register,
  },
  {
    path: '/home',
    name: 'HomeLogged',
    component: HomeLogged,
    meta: { requiresAuth: true },
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'CatchAll',
    component: NotFoundComponent,
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

// 🔐 Global auth guard
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('authToken')
  const isLoggedIn = !!token

  // Redirect to /home if already logged in and trying to access public routes
  if (isLoggedIn && (to.name === 'Home' || to.name === 'Login' || to.name === 'Register')) {
    next('/home')
  }

  // Redirect to login if trying to access protected routes without token
  else if (to.meta.requiresAuth && !isLoggedIn) {
    next('/login')
  }

  // All other cases allowed
  else {
    next()
  }
})

export default router

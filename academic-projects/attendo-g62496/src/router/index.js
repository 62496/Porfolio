import { createRouter, createWebHistory } from 'vue-router'
import Layout from '../views/Layout.vue'
import Accueil from '../views/Accueil.vue'
import Sessions from '../views/Sessions.vue'
import SessionDetails from '../views/SessionDetails.vue'
import Epreuve from '../views/Epreuve.vue'
import APropos from '../views/APropos.vue'
import { useAuthStore } from '@/stores/AuthStore'
import Local from '../views/Local.vue'
import Presence from '../views/Presence.vue'
const routes = [
  {
    path: '/',
    component: Layout,
    meta: { breadcrumb: 'Accueil' },
    children: [
      {
        path: '',
        name: 'accueil',
        component: Accueil,
        meta: { breadcrumb: 'Accueil' }
      },
      {
        path: 'sessions',
        name: 'sessions',
        component: Sessions,
        meta: { breadcrumb: 'Sessions', requiresAuth: true }
      },
      {
        path: 'sessions/:id',
        name: 'session-details',
        component: SessionDetails,
        meta: { breadcrumb: route => `Session `, parent: 'sessions' }
      },
      {
        path: 'sessions/:id/ue/:ue',
        name: 'epreuves-par-ue',
        component: Epreuve,
        meta: {
          breadcrumb: route =>`ue `, parent: 'session-details'
        }
      },
      {
        path: 'sessions/:id/ue/:ue/event/:eventId',
        name: 'local-par-epreuve',
        component: Local,
        meta: {
          breadcrumb: route => `Épreuve `,parent: 'epreuves-par-ue'
        }
      },
      {
        path: 'sessions/:id/ue/:ue/event/:eventId/exam/:examId',
        name: 'presence-par-local',
        component: Presence,
        meta: {
          breadcrumb: route => `Local `,
          parent: 'local-par-epreuve'
        }
      },
      {
        path: 'a-propos',
        name: 'a-propos',
        component: APropos,
        meta: { breadcrumb: 'À propos' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const auth = useAuthStore()

  if (to.meta.requiresAuth && !auth.user) {
    return next('/')
  }

  next()
})


export default router

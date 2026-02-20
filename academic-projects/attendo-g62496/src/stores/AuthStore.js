import { defineStore } from 'pinia'
import { supabase } from '../service/supabase'
import router from '../router'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: null
  }),
  actions: {
    async init() {
      const { data: { session } } = await supabase.auth.getSession()
      this.user = session?.user

      supabase.auth.onAuthStateChange((_event, session) => {
        this.user = session?.user ?? null
      })
    },
    async login() {
      console.log("login")
      const { error } = await supabase.auth.signInWithOAuth({
        provider: 'google',
        options: { redirectTo: window.location.origin }
      })
      if (error) console.error('Login error:', error.message)
    },
    async logout() {
      const { error } = await supabase.auth.signOut()
      if (error){
        console.error('Logout error:', error.message)
      }else{
        router.push('/')
      }
      
    }
  }
})


  
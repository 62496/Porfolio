<script setup>
import { computed } from 'vue'
import { useAuthStore } from '../stores/AuthStore'

const auth = useAuthStore()
const isLoggedIn = computed(() => !!auth.user)
const login  = () => auth.login()
const logout = () => auth.logout()
</script>

<template>
  <div class="flex items-center space-x-4">
    <button
      v-if="!isLoggedIn"
      @click="login"
      class="px-4 py-2 bg-blue-500 text-white rounded"
    >
      Se connecter avec Google
    </button>

    <template v-else>
      <img
        :src="auth.user.user_metadata.avatar_url"
        alt="avatar"
        class="w-8 h-8 rounded-full"
      />
      <span>{{ auth.user.email }}</span>
      <button
        @click="logout"
        class="px-2 py-1 bg-gray-300 rounded"
      >
        Déconnexion
      </button>
    </template>
  </div>
</template>


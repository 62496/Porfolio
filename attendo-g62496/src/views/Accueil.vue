<script setup>
import { computed } from 'vue'
import { useAuthStore } from '../stores/AuthStore'

const auth = useAuthStore()
const email = computed(() => auth.user?.email)
</script>

<template>
  <div>
    <h2 class="text-xl mb-4">Page d’accueil</h2>
    <p v-if="email">Connecté en tant que : {{ email }}</p>
    <p v-else>Bonjour veuillez vous connecter pour plus de contenu! </p>
  </div>
</template>


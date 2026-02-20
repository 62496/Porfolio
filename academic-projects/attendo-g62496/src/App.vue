<script setup>
import { RouterLink, RouterView } from 'vue-router'
import LoginButton from './components/LoginButton.vue'
</script>

<template>
  <div class="min-h-screen flex flex-col">
    <header class="bg-black text-white">
      <div class="px-6 py-4 flex justify-center">
        <h1 class="text-3xl font-bold text-purple-400">Attendo</h1>
      </div>
      <nav class="bg-gray-100 px-6 py-2 flex justify-between items-center">
        <ul class="flex space-x-6 text-gray-700">
          <li><RouterLink to="/">Accueil</RouterLink></li>
          <li><RouterLink to="/sessions">Sessions</RouterLink></li>
          <li><RouterLink to="/a-propos">À propos</RouterLink></li>
        </ul>
        <LoginButton />
      </nav>
    </header>

    <main class="flex-1 p-6">
      <RouterView />
    </main>

    <footer class="bg-gray-200 text-center py-4">
      © 2025 Attendo
    </footer>
  </div>
</template>

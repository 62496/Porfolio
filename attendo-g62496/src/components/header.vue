<template>
    <header class="bg-black text-white">
      <div class="flex justify-between items-center px-6 py-4">
        <h1 class="text-3xl font-bold text-purple-400">Attendo</h1>
        <button class="border px-3 py-1 rounded hover:bg-gray-100 text-black bg-white">
          Connexion avec Google
        </button>
      </div>
      <nav class="bg-gray-100 px-6 py-2">
        <ul class="flex space-x-6 text-gray-700">
          <li><RouterLink to="/">Accueil</RouterLink></li>
          <li><RouterLink to="/sessions">Sessions</RouterLink></li>
          <li><RouterLink to="/a-propos">À propos</RouterLink></li>
        </ul>
      </nav>
    </header>
  </template>
  
  <script setup>
  import { RouterLink } from 'vue-router'
  </script>
  
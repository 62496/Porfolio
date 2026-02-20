<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {fetchEpreuves, fetchSessionCompoBySessionAndUE, addEpreuve } from '../service/epreuveService'

const route = useRoute()
const router = useRouter()

const sessionId = route.params.id
const ue = route.params.ue

const sessionCompoId = ref(null)
const epreuves = ref([])
const newLabel = ref('')

onMounted(async () => {
  sessionCompoId.value = await fetchSessionCompoBySessionAndUE(sessionId, ue)
  if (sessionCompoId.value) {
    epreuves.value = await fetchEpreuves(sessionCompoId.value)
  }
})

async function handleAdd() {
  if (!newLabel.value.trim() || !sessionCompoId.value) return
  const added = await addEpreuve(sessionCompoId.value, newLabel.value)
  if (added) {
    epreuves.value.push(added)
    newLabel.value = ''
  }
}
function goToEvent(id,epreuve) {
  router.push({
    name: 'local-par-epreuve',
    params: {
      id: sessionId,
      ue: ue,
      eventId: id
    },
    query: {
      epreuve: epreuve
    }
  })
}
</script>
<template>
  <div class="max-w-4xl mx-auto p-6 space-y-6">
    <h2 class="text-2xl font-semibold text-blue-800">
      Liste des épreuves de {{ ue }} <span class="text-gray-500">(session : {{ ue }})</span>
    </h2>

    <div v-if="epreuves.length === 0" class="text-gray-500 italic">
      Aucune épreuve
    </div>

    <div class="flex gap-4 flex-wrap">
      <div
        v-for="epreuve in epreuves"

        :key="epreuve.id"
        class="cursor-pointer shadow rounded border p-4 hover:bg-gray-50 w-32 text-center"
      >
        <div class="font-semibold text-md capitalize" @click="goToEvent(epreuve.id,epreuve.label)" >{{ epreuve.label }}</div>
      </div>
    </div>

    <div class="bg-white mt-6 p-4 rounded flex items-center gap-4">
      <label class="font-medium">Intitulé :</label>
      <input
        v-model="newLabel"
        type="text"
        placeholder="bilan, projet, examen…"
        class="border px-2 py-1 rounded"
      />
      <button @click="handleAdd" class="border px-4 py-1 rounded hover:bg-gray-100">
        Créer
      </button>
    </div>
  </div>
</template>


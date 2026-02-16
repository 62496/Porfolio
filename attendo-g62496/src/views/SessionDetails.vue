<template>
  <div class="max-w-4xl mx-auto p-6 space-y-6">
    <h2 class="text-2xl font-semibold text-gray-800">
      Session <em class="text-blue-700">{{ session?.label }}</em>
    </h2>

    <TableSession
      v-if="ueDetails.length"
      :headers="['UE']"
      :fields="['ue']"
      :content="ueDetails"
    >
      <template #row="{ item }">
        <tr
          @click="goToUe(item.ue)"
          class="hover:bg-gray-100 cursor-pointer transition"
        >
          <td class="px-6 py-4">{{ item.ue}}</td>
        </tr>
      </template>
    </TableSession>

    <p v-else class="text-gray-500 italic">Aucune UE pour cette session</p>

    <div class="bg-white shadow p-4 rounded space-y-4">
      <h3 class="text-lg font-medium">Ajouter une UE dans la session</h3>
      <div class="flex gap-4 items-center">
        <select
          v-model="selectedUE"
          class="border px-4 py-2 rounded text-gray-700"
        >
          <option disabled value="">Choisissez une UE</option>
          <option
            v-for="ue in ueList"
            :key="ue.ue"
            :value="ue.ue"
          >
            {{ ue.ue }}
          </option>
        </select>
        <button
          @click="handleAddUE"
          class="border border-gray-800 px-4 py-2 rounded hover:bg-gray-100"
        >
          Ajouter l’UE
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import TableSession from '../components/TableSession.vue'
import { fetchSessionById } from '../service/listSessionsService'
import {
  fetchAllUEs,
  fetchUEsForSession,
  addUEToSession
} from '../service/ueService'

const route = useRoute()
const router = useRouter()
const sessionId = route.params.id

const session = ref(null)
const ueDetails = ref([])
const ueList = ref([])
const selectedUE = ref('')

async function loadData() {
  session.value = await fetchSessionById(sessionId)
  const allUEs = await fetchAllUEs()
  const usedUEs = await fetchUEsForSession(sessionId)

  ueDetails.value = allUEs.filter(ue => usedUEs.includes(ue.ue))
  ueList.value = allUEs.filter(ue => !usedUEs.includes(ue.ue))
}

async function handleAddUE() {
  if (!selectedUE.value) return
  const success = await addUEToSession(sessionId, selectedUE.value)
  if (success) {
    selectedUE.value = ''
    await loadData()
  } else {
    console.error('Échec de l’ajout de l’UE')
  }
}

function goToUe(ueLabel) {

  router.push({
    name: 'epreuves-par-ue',
    params: {
      id: sessionId,
      ue: ueLabel
    }
  })

}

onMounted(loadData)
</script>

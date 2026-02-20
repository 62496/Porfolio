<template>
  <div class="max-w-3xl mx-auto p-6">
    <h2 class="text-3xl font-semibold text-blue-800 mb-6">Sessions</h2>

    <div class="bg-white shadow rounded-lg overflow-hidden mb-8">
      <TableSession
        v-if="sessions.length"
        :headers="['Sessions']"
        :fields="['label']"
        :content="sessions"
      >
        <template #row="{ item }">
          <tr
            @click="goToDetail(item)"
            class="hover:bg-gray-100 cursor-pointer transition"
          >
            <td class="px-6 py-4">{{ item.label }}</td>
          </tr>
        </template>
      </TableSession>

      <p v-else class="text-gray-500 italic">Aucune session</p>

    </div>


    <div class="bg-white shadow p-4 rounded flex items-center gap-2">
      <span class="text-lg font-medium">Ajouter une session</span>

      <div class="flex items-center bg-gray-100 rounded px-3 py-2 text-gray-700">
        <span class="mr-2">Nouvelle session 👥</span>
        <input
          v-model="newSession"
          type="text"
          placeholder="juin"
          required
          class="bg-transparent outline-none w-28"
        />
      </div>

      <button
        @click="handleAdd"
        class="ml-auto border border-gray-600 px-4 py-2 rounded hover:bg-gray-100"
      >
        Ajouter
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { fetchSessions, addSession } from '../service/listSessionsService'
import { useRouter } from 'vue-router'
import TableSession from '../components/TableSession.vue' 
const sessions = ref([])
const newSession = ref('')
const router = useRouter()

async function loadSessions() {
  sessions.value = await fetchSessions()
}

function goToDetail(item) {
  router.push(`/sessions/${item.id}`)
}

async function handleAdd() {
  if (!newSession.value.trim()) return
  const added = await addSession({ label: newSession.value })
  if (added) {
    sessions.value.push(added)
    newSession.value = ''
  }
}

onMounted( loadSessions)
</script>

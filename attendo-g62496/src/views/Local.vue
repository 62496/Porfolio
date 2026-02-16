<template>
  <div class="max-w-5xl mx-auto p-6 space-y-6">
    <h2 class="text-xl font-semibold text-blue-800">
      Locaux pour l’épreuve <strong>{{ epreuve  }}</strong> - UE <strong>{{ ue }}</strong>
    </h2>

    <div v-if="assignedRooms.length" class="flex flex-wrap gap-4">
      <div v-for="room in assignedRooms" :key="room.id" class="border rounded p-4 w-32 text-center shadow" 
      @click="goToPresence(room)">
        <div class="text-xl font-bold">{{ room.room }}</div>
        <div class="text-xs text-gray-600">{{ stats[room.room]?.count ?? '...' }} / {{ stats[room.room]?.capacity ?? '...' }}</div>
        <div class="mt-1 text-sm">
          <strong>Surveillant</strong><br />
          {{ room.supervisor || 'N/A' }}
        </div>
      </div>
    </div>
    <p v-else class="italic text-gray-500">Aucun local encore assigné.</p>

    <div class="mt-6 bg-white p-4 shadow rounded flex items-center gap-4">
      <label class="font-medium">Local</label>
      <select v-model="selectedRoom" class="border px-3 py-1 rounded text-gray-700">
        <option disabled value="">Choisissez un local</option>
        <option v-for="room in availableRooms" :key="room" :value="room">{{ room.label }}</option>
      </select>
      <button @click="addRoom" class="border px-4 py-2 rounded hover:bg-gray-100">
        Ajouter
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'

import {
  fetchAssignedRooms,
  fetchAvailableRooms,
  assignRoomToEvent,
  countStudentsInRoom,
  RoomCapacity
} from '../service/localService'

const route = useRoute()
const router = useRouter()
const eventId = route.params.eventId
const sessionId = route.params.id
const epreuve = route.query.epreuve
const ue = route.params.ue

const assignedRooms = ref([])
const availableRooms = ref([])
const selectedRoom = ref('')

const stats = ref({})

async function loadRooms() {
  assignedRooms.value = await fetchAssignedRooms(eventId)
  availableRooms.value = await fetchAvailableRooms(eventId, assignedRooms.value)
  // Charger les stats pour chaque salle
  const newStats = {}
  for (const room of assignedRooms.value) {
    const count = await countStudentsInRoom(eventId, room.room)
    const capacity = await RoomCapacity(eventId, room.room)
    newStats[room.room] = { count, capacity }
  }
  stats.value = newStats
}
async function addRoom() {
  if (!selectedRoom.value) return
  const success = await assignRoomToEvent(eventId, selectedRoom.value)
  if (success) {
    selectedRoom.value = ''
    await loadRooms()
  }
}
function goToPresence(room) {
  console.log('room', room.room)
  console.log('supervisor', room.supervisor)
  router.push({
    name: 'presence-par-local',
    params: {
      id: sessionId,
      ue: ue,
      eventId: eventId,
      examId: room.id
    },
    query: {
    localRoom: room.room,
    }
  })
}

onMounted(loadRooms)
</script>

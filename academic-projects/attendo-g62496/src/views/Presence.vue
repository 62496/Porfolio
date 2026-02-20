<template>
  <div class="max-w-5xl mx-auto p-6 space-y-6">
    <h2 class="text-2xl font-semibold">
      Prise de présence du local {{localRoom }} par {{ supervisor || '...' }}
    </h2>

    <div class="flex gap-2 items-center">
      <span class="font-semibold">Surveillant</span>
      <input
        v-model="supervisorInput"
        placeholder="Choisir/modifier le surveillant"
        class="border px-3 py-1 rounded"
      />
      <button @click="defineSupervisor" class="border px-4 py-1 rounded" >
        Définir le surveillant
      </button>
    </div>

    <TableSession
      v-if="students.length"
      :headers="['Matricule', 'Groupe', 'Nom', 'Prénom']"
      :fields="['student_id', 'group', 'lastname', 'firstname']"
      :content="students"
    >
      <template #row="{ item }">
        <tr
          :class="{
            'bg-blue-100': presence.includes(item.student_id),
            'cursor-pointer': true
          }"
          @click="togglePresence(item.student_id)"
        >
          <td class="p-2">{{ item.student_id }}</td>
          <td class="p-2">{{ item.group }}</td>
          <td class="p-2">{{ item.lastname }}</td>
          <td class="p-2">{{ item.firstname }}</td>
        </tr>
      </template>
    </TableSession>

    <p v-else class="text-gray-500 italic">Aucun étudiant</p>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import TableSession from '../components/TableSession.vue'
import {
  getSupervisor,
  updateSupervisor,
  fetchStudentsByUe,fetchPresence,
  addPresence,
  removePresence
} from '../service/PresenceService'

const route = useRoute()
const { eventId, examId, ue } = route.params
const localRoom = route.query.localRoom
const supervisor = ref('')
const supervisorInput = ref('')
const students = ref([])
const presence = ref([])

onMounted(async () => {
  supervisor.value = await getSupervisor(eventId, localRoom)
  students.value = await fetchStudentsByUe(ue)
  presence.value   = await fetchPresence(eventId, localRoom)
})
/*
function togglePresence(id) {
  if (presence.value.includes(id)) {
    presence.value = presence.value.filter(i => i !== id)
  } else {
    presence.value.push(id)
  }
}*/
async function togglePresence(studentId) {
  if (presence.value.includes(studentId)) {
    await removePresence(eventId, localRoom, studentId)
    presence.value = presence.value.filter(id => id !== studentId)
  } else {
    await addPresence(eventId, localRoom, studentId)
    presence.value = [...presence.value, studentId]
  }
}

async function defineSupervisor() {
  if (!supervisorInput.value) return
  const success = await updateSupervisor(eventId, localRoom, supervisorInput.value)
  if (success) {
    supervisor.value = supervisorInput.value
    supervisorInput.value = ''
  }
}
</script>
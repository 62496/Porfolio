<template>
  <table class="w-full table-auto border-collapse">
    <thead class="bg-gray-100">
      <tr>
        <th
          v-for="(header, colIndex) in headers"
          :key="colIndex"
          class="px-4 py-2 border text-left"
        >
          {{ header }}
        </th>
      </tr>
    </thead>
    <tbody>
      <template v-if="$slots.row">
        <slot
          name="row"
          v-for="(item, rowIndex) in content"
          :item="item"
          :index="rowIndex"
          :key="rowIndex"
        />
      </template>
      
      <template v-else>
        <tr
          v-for="(item, rowIndex) in content"
          :key="rowIndex"
          class="cursor-pointer"
        >
          <td
            v-for="(field, colIndex) in fields"
            :key="colIndex"
            class="px-4 py-2 border break-words whitespace-normal"
          >
            {{ item[field] }}
          </td>
        </tr>
      </template>
    </tbody>
  </table>
</template>

<script setup>
import { useRouter } from 'vue-router'

const props = defineProps({
  headers: { type: Array, required: true },
  content: { type: Array, required: true },
  fields:  { type: Array, required: true }
})

const router = useRouter()
</script>

<style scoped>
table {
  border-collapse: collapse;
}
</style>

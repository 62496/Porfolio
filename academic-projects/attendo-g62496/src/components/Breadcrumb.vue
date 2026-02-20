<script setup>
import { computed } from 'vue'
import { useRoute, useRouter, RouterLink } from 'vue-router'

const route = useRoute()
const router = useRouter()

function buildBreadcrumb(routeMatch) {
  const breadcrumb = routeMatch.meta.breadcrumb
  return {
    text: typeof breadcrumb === 'function' ? breadcrumb(route) : breadcrumb,
    path: router.resolve({ name: routeMatch.name, params: route.params }).href,
    parent: routeMatch.meta.parent
  }
}

const breadcrumbs = computed(() => {
  const crumbs = []
  let currentRoute = route.matched[route.matched.length - 1]
  console.log('curentroute',currentRoute)
  while (currentRoute) {
    crumbs.unshift(buildBreadcrumb(currentRoute))
    currentRoute = currentRoute.meta.parent
      ? router.getRoutes().find(r => r.name === currentRoute.meta.parent)
      : null
  }
  if (!crumbs.find(c => c.text === 'Accueil')) {
    console.log("else")
    crumbs.unshift({
      text: 'Accueil',
      path: '/'
    })
  }

  return crumbs
})
</script>

<template>
  <nav class="breadcrumb p-4">
    <ol class="flex gap-2 text-gray-600">
      <li v-for="(crumb, index) in breadcrumbs" :key="crumb.path" class="flex items-center">
        <RouterLink
          v-if="index < breadcrumbs.length - 1"
          :to="crumb.path"
          class="hover:text-blue-700"
        >
          {{ crumb.text }}
        </RouterLink>
        <span v-else class="text-blue-700">{{ crumb.text }}</span>
        <span v-if="index < breadcrumbs.length - 1" class="mx-2">/</span>
      </li>
    </ol>
  </nav>
</template>

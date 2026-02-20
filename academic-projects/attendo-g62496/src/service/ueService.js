import { supabase } from './supabase'

export async function fetchAllUEs() {
  const { data, error } = await supabase.from('ue').select('*')
  if (error) {
    console.error('Erreur lors de la récupération des UEs :', error.message)
    return []
  }
  return data
}

export async function fetchUEsForSession(sessionId) {
  const { data, error } = await supabase
    .from('session_compo')
    .select('ue')
    .eq('session', sessionId)
  if (error) {
    console.error('Erreur lors de la récupération des UEs de la session :', error.message)
    return []
  }
  return data.map(item => item.ue)
}

export async function addUEToSession(sessionId, ueId) {
  const { error } = await supabase
    .from('session_compo')
    .insert({ ue: ueId,session: sessionId })
  if (error) {
    console.error("Erreur lors de l'ajout de l'UE :", error.message)
    return false
  }
  return true
}

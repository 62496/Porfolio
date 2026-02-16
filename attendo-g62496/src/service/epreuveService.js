import { supabase } from './supabase'

export async function fetchEpreuves(sessionCompoId) {
  const { data, error } = await supabase
    .from('event')
    .select('*')
    .eq('session_compo', sessionCompoId)

  if (error) {
    console.error('Erreur lors du chargement des épreuves :', error.message)
    return []
  }

  return data
}

export async function fetchSessionCompoBySessionAndUE(sessionId, ueLabel) {
  const { data, error } = await supabase
    .from('session_compo')
    .select('id')
    .eq('session', sessionId)
    .eq('ue', ueLabel)
    .single()

  if (error) {
    console.error('Erreur session_compo :', error.message)
    return null
  }
  return data.id
}

export async function addEpreuve(sessionCompoId, label) {
  const { data, error } = await supabase
    .from('event')
    //identique label et label
    .insert([{ session_compo: sessionCompoId, label }])
    .select()

  if (error) {
    console.error('Erreur ajout épreuve :', error.message)
    return null
  }

  return data[0]
}


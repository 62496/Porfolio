import { supabase } from './supabase'

export async function fetchAssignedRooms(eventId) {
  const { data, error } = await supabase
    .from('examination_room')
    .select('*')
    .eq('event', eventId)
  if (error) {
    console.error('Erreur de récupération des locaux assignés:', error.message)
    return []
  }

  return data
}

export async function fetchAvailableRooms(eventId) {
  const assigned = await fetchAssignedRooms(eventId)
  const assignedLabels = assigned.map(r => r.room)

  const { data, error } = await supabase
    .from('room')
    .select('*')

  if (error) {
    console.error('Erreur de récupération des locaux:', error.message)
    return []
  }

  return data.filter(room => !assignedLabels.includes(room.label))
}

export async function assignRoomToEvent(eventId, room, supervisor = null) {
  const { data, error } = await supabase
    .from('examination_room')
    .insert([{ event: eventId, room: room.label, supervisor }])
    .select()

  if (error) {
    console.error('Erreur lors de l’assignation du local:', error.message)
    return null
  }

  return data[0]
}
// je veux une fonctione qui me donne le nombre d'étudiants dans un local pour une épreuve donnée et comparer au nombre de places disponibles dans le local
export async function RoomCapacity(eventId, roomLabel) {
   const {data,error} =await supabase
   .from('room')
   .select('capacity')
   .eq('label',roomLabel)
   .single()
   if(error){
    console.error('Une erreur c est produite pour recuper la capacity', error.message )
    return ''
   }
   return data.capacity
}
export async function countStudentsInRoom(eventId, roomLabel) {
  const { data: roomData, error: roomError } = await supabase
    .from('examination_room')
    .select('id')
    .eq('event', eventId)
    .eq('room', roomLabel)
    .single()

  if (roomError || !roomData) {
    console.error('Erreur lors de la récupération du local:', roomError?.message || 'Local non trouvé')
    return 0
  }

  const { data, error } = await supabase
    .from('examination')
    .select('student')
    .eq('examination_room', roomData.id)

  if (error) {
    console.error('Erreur lors du comptage des étudiants dans le local:', error.message)
    return 0
  }

  return data.length
}

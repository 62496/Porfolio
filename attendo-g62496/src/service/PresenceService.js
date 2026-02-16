import { supabase } from '../service/supabase'

export async function getSupervisor(eventId, roomLabel) {
  const { data } = await supabase
    .from('examination_room')
    .select('supervisor')
    .eq('event', eventId)
    .eq('room', roomLabel)
    .single()
  return data?.supervisor || ''
}

export async function updateSupervisor(eventId, roomLabel, supervisor) {
  const { data, error } = await supabase
    .from('examination_room')
    .update({ supervisor })
    .match({ event: eventId, room:roomLabel })

  if (error) {
    console.error('Erreur updateSupervisor:', error)
    return false
  }

  return true
}

export async function fetchStudentsByUe(ue) {
  const { data: pae } = await supabase.from('pae').select('*').eq('ue', ue)
  const studentIds = pae.map(p => p.student_id)

  const { data: students } = await supabase
    .from('student')
    .select('*')
    //sert à faire une requête de type "WHERE ... IN (...)", exactement comme en SQL :
    .in('student_id', studentIds)

  return students.map(student => {
    const match = pae.find(p => p.student_id === student.student_id)
    //on regruppe les étudiants avec leur groupe
    //si on ne trouve pas de correspondance, on met un groupe vide
    //ce qui est le cas pour les étudiants qui n'ont pas de PAE
    //ou qui n'ont pas de PAE pour cette UE
    return {
      ...student,
      group: match?.group || ''
    }
  })
}

async function getRoomId(eventId, roomLabel) {
  const { data, error } = await supabase
    .from('examination_room')
    .select('id')
    .eq('event', eventId)
    .eq('room', roomLabel)
    .single()
  if (error) throw error
  return data.id
}

export async function fetchPresence(eventId, roomLabel) {
  const roomId = await getRoomId(eventId, roomLabel)
  const { data, error } = await supabase
    .from('examination')
    .select('student')
    .eq('examination_room', roomId)
  if (error) throw error
  return data.map(row => row.student)
}

export async function addPresence(eventId, roomLabel, studentId) {
  const roomId = await getRoomId(eventId, roomLabel)
  const { error } = await supabase
    .from('examination')
    .insert([{ student: studentId, examination_room: roomId }])
  if (error) throw error
  return true
}

export async function removePresence(eventId, roomLabel, studentId) {
  const roomId = await getRoomId(eventId, roomLabel)
  const { error } = await supabase
    .from('examination')
    .delete()
    .match({ student: studentId, examination_room: roomId })
  if (error) throw error
  return true
}

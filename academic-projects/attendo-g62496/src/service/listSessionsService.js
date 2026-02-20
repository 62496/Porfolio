import {supabase} from './supabase.js'

export async function fetchSessions() {
    const {data ,error} = await supabase
    .from('session')
    .select('*')
    
    if (error){
        console.error('Error to get sessions data: ',error.message)
        return null
    }
    return data
}
export async function fetchSessionById(id) {
  const { data, error } = await supabase
    .from('session')
    .select('*')
    .eq('id', id)
    .single()
  if (error) {
    console.error('Error to get session:', error.message)
    return null
  }
  return data
}
export async function addSession(session){
    console.log('session to add: ',session)
    const {data,error} = await supabase
    .from('session') 
    .insert([session])
    .select() 
    if (error){
        console.error('Error to add session: ',error.message)
        return null
    }
    return data[0]
}

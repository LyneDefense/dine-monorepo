import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
  const token = ref<string | null>(localStorage.getItem('token'))
  const username = ref<string | null>(null)

  function setToken(newToken: string) {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  function setUsername(name: string) {
    username.value = name
  }

  function logout() {
    token.value = null
    username.value = null
    localStorage.removeItem('token')
  }

  function isAuthenticated() {
    return !!token.value
  }

  return { token, username, setToken, setUsername, logout, isAuthenticated }
})

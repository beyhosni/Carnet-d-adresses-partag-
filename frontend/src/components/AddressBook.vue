<script setup>
import { ref, onMounted, watch } from 'vue'
import api from '../api'

const contacts = ref([])
const searchQuery = ref('')
const loading = ref(false)
const showAddModal = ref(false)
const newContact = ref({ nom: '', prenom: '', email: '', telephone: '', tag: '' })

const loadContacts = async () => {
  loading.value = true
  try {
    const response = await api.get('/contacts', { params: { q: searchQuery.value } })
    contacts.value = response.data.content
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

// Debounce search
let timeout
watch(searchQuery, (newVal) => {
  clearTimeout(timeout)
  timeout = setTimeout(() => loadContacts(), 300)
})

onMounted(loadContacts)

const addContact = async () => {
  try {
    await api.post('/contacts', newContact.value)
    showAddModal.value = false
    newContact.value = { nom: '', prenom: '', email: '', telephone: '', tag: '' }
    loadContacts()
  } catch (e) {
    alert('Erreur lors de la création')
  }
}

const updateContact = async (contact) => {
  try {
    await api.put(`/contacts/${contact.id}`, contact)
  } catch (e) {
    console.error('Update failed', e)
  }
}

const deleteContact = async (id) => {
  if (!confirm('Supprimer ?')) return
  try {
    await api.delete(`/contacts/${id}`)
    loadContacts()
  } catch (e) {
    alert('Erreur suppression')
  }
}

const uploadPhoto = async (id, event) => {
  const file = event.target.files[0]
  if (!file) return
  const formData = new FormData()
  formData.append('file', file)
  try {
    await api.post(`/contacts/${id}/photo`, formData)
    // Force refresh image by updating a timestamp or reloading
    loadContacts()
  } catch (e) {
    alert('Upload failed')
  }
}

const exportCSV = () => {
  const headers = ['Nom', 'Prenom', 'Email', 'Telephone', 'Tag']
  const rows = contacts.value.map(c => [c.nom, c.prenom, c.email, c.telephone, c.tag])
  const csvContent = "data:text/csv;charset=utf-8," 
    + [headers.join(','), ...rows.map(r => r.join(','))].join('\n')
  const encodedUri = encodeURI(csvContent)
  const link = document.createElement("a")
  link.setAttribute("href", encodedUri)
  link.setAttribute("download", "contacts.csv")
  document.body.appendChild(link)
  link.click()
}
</script>

<template>
  <div class="container">
    <header class="header">
      <h1>📇 Carnet d'adresses</h1>
      <div class="actions">
        <input v-model="searchQuery" placeholder="Rechercher..." class="search-input" />
        <button @click="showAddModal = true" class="btn primary">Ajouter</button>
        <button @click="exportCSV" class="btn secondary">Export CSV</button>
      </div>
    </header>

    <div class="table-container">
      <table>
        <thead>
          <tr>
            <th>Photo</th>
            <th>Nom</th>
            <th>Prénom</th>
            <th>Email</th>
            <th>Téléphone</th>
            <th>Tag</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="contact in contacts" :key="contact.id">
            <td class="photo-cell">
              <div class="photo-wrapper">
                <img :src="`/api/contacts/${contact.id}/photo`" @error="$event.target.src='https://via.placeholder.com/50'" alt="Photo" />
                <input type="file" @change="(e) => uploadPhoto(contact.id, e)" class="file-input" />
              </div>
            </td>
            <td><input v-model="contact.nom" @change="updateContact(contact)" /></td>
            <td><input v-model="contact.prenom" @change="updateContact(contact)" /></td>
            <td><input v-model="contact.email" @change="updateContact(contact)" /></td>
            <td><input v-model="contact.telephone" @change="updateContact(contact)" /></td>
            <td><span class="tag" contenteditable @blur="(e) => { contact.tag = e.target.innerText; updateContact(contact) }">{{ contact.tag }}</span></td>
            <td>
              <button @click="deleteContact(contact.id)" class="btn icon">🗑️</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Modal -->
    <div v-if="showAddModal" class="modal-overlay">
      <div class="modal">
        <h2>Nouveau Contact</h2>
        <form @submit.prevent="addContact">
          <input v-model="newContact.nom" placeholder="Nom" required />
          <input v-model="newContact.prenom" placeholder="Prénom" required />
          <input v-model="newContact.email" placeholder="Email" type="email" />
          <input v-model="newContact.telephone" placeholder="Téléphone" />
          <input v-model="newContact.tag" placeholder="Tag" />
          <div class="modal-actions">
            <button type="button" @click="showAddModal = false" class="btn secondary">Annuler</button>
            <button type="submit" class="btn primary">Sauvegarder</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<style scoped>
.container {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.actions {
  display: flex;
  gap: 1rem;
}

.search-input {
  padding: 0.5rem 1rem;
  border-radius: 8px;
  border: 1px solid var(--border);
  background: var(--surface);
  color: var(--text);
  min-width: 300px;
}

table {
  width: 100%;
  border-collapse: collapse;
  background: var(--surface);
  border-radius: 12px;
  overflow: hidden;
}

th, td {
  padding: 1rem;
  text-align: left;
  border-bottom: 1px solid var(--border);
}

th {
  background: rgba(255, 255, 255, 0.05);
  font-weight: 600;
  color: var(--text-muted);
}

input {
  background: transparent;
  border: none;
  color: var(--text);
  width: 100%;
  padding: 0.25rem;
  border-radius: 4px;
}

input:focus {
  background: rgba(255, 255, 255, 0.1);
}

.photo-wrapper {
  position: relative;
  width: 50px;
  height: 50px;
  cursor: pointer;
}

.photo-wrapper img {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid var(--primary);
}

.file-input {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  opacity: 0;
  cursor: pointer;
}

.btn {
  padding: 0.5rem 1rem;
  border-radius: 8px;
  border: none;
  font-weight: 600;
}

.primary {
  background: var(--primary);
  color: white;
}

.primary:hover {
  background: var(--primary-hover);
}

.secondary {
  background: transparent;
  border: 1px solid var(--border);
  color: var(--text);
}

.secondary:hover {
  background: rgba(255,255,255,0.05);
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.7);
  display: flex;
  justify-content: center;
  align-items: center;
  backdrop-filter: blur(5px);
}

.modal {
  background: var(--surface);
  padding: 2rem;
  border-radius: 16px;
  width: 400px;
  display: flex;
  flex-direction: column;
  gap: 1rem;
  border: 1px solid var(--border);
}

.modal input {
  background: rgba(0,0,0,0.2);
  padding: 0.75rem;
  border: 1px solid var(--border);
  border-radius: 8px;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
  margin-top: 1rem;
}
</style>

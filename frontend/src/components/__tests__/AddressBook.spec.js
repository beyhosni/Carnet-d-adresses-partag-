import { mount } from '@vue/test-utils'
import { describe, it, expect, vi, beforeEach } from 'vitest'
import AddressBook from '../AddressBook.vue'
import api from '../../api'

// Mock Axios
vi.mock('../../api', () => ({
    default: {
        get: vi.fn(),
        post: vi.fn(),
        put: vi.fn(),
        delete: vi.fn()
    }
}))

describe('AddressBook', () => {
    beforeEach(() => {
        vi.clearAllMocks()
    })

    it('renders correctly', () => {
        api.get.mockResolvedValue({ data: { content: [] } })
        const wrapper = mount(AddressBook)
        expect(wrapper.find('h1').text()).toContain("Carnet d'adresses")
    })

    it('loads contacts on mount', async () => {
        const contacts = [
            { id: 1, nom: 'Doe', prenom: 'John', email: 'john@example.com', telephone: '123', tag: 'Work' }
        ]
        api.get.mockResolvedValue({ data: { content: contacts } })

        const wrapper = mount(AddressBook)

        // Wait for promises to resolve
        await new Promise(resolve => setTimeout(resolve, 0))
        await wrapper.vm.$nextTick()

        expect(api.get).toHaveBeenCalledWith('/contacts', expect.any(Object))
        expect(wrapper.findAll('tbody tr')).toHaveLength(1)
        expect(wrapper.html()).toContain('John')
    })

    it('opens add modal when clicking add button', async () => {
        api.get.mockResolvedValue({ data: { content: [] } })
        const wrapper = mount(AddressBook)

        await wrapper.find('button.primary').trigger('click')

        expect(wrapper.find('.modal').exists()).toBe(true)
    })

    it('calls create api when form is submitted', async () => {
        api.get.mockResolvedValue({ data: { content: [] } })
        api.post.mockResolvedValue({ data: {} })

        const wrapper = mount(AddressBook)
        await wrapper.find('button.primary').trigger('click') // Open modal

        // Fill form
        await wrapper.find('input[placeholder="Nom"]').setValue('Smith')
        await wrapper.find('input[placeholder="Prénom"]').setValue('Jane')

        await wrapper.find('form').trigger('submit')

        expect(api.post).toHaveBeenCalledWith('/contacts', expect.objectContaining({
            nom: 'Smith',
            prenom: 'Jane'
        }))
    })
})

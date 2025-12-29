import apiClient from '../client';
import { API_ENDPOINTS } from '../endpoints';

const subjectService = {
    /**
     * Get all subjects
     * @param {Object} params
     * @returns {Promise<Array>}
     */
    getAll: async (params = {}) => {
        const response = await apiClient.get(API_ENDPOINTS.SUBJECTS.GET_ALL, { params });
        return response.data;
    },

    /**
     * Get subject by ID
     * @param {number} id
     * @returns {Promise<Object>}
     */
    getById: async (id) => {
        const response = await apiClient.get(API_ENDPOINTS.SUBJECTS.GET_BY_ID(id));
        return response.data;
    },

    /**
     * Create a new subject (LIBRARIAN only)
     * @param {Object} data - { name: string }
     * @returns {Promise<Object>}
     */
    create: async (data) => {
        const response = await apiClient.post(API_ENDPOINTS.SUBJECTS.CREATE, data);
        return response.data;
    },

    /**
     * Update a subject (LIBRARIAN only)
     * @param {number} id
     * @param {Object} data - { name: string }
     * @returns {Promise<Object>}
     */
    update: async (id, data) => {
        const response = await apiClient.put(API_ENDPOINTS.SUBJECTS.UPDATE(id), data);
        return response.data;
    },

    /**
     * Delete a subject (LIBRARIAN only)
     * @param {number} id
     * @returns {Promise<void>}
     */
    delete: async (id) => {
        await apiClient.delete(API_ENDPOINTS.SUBJECTS.DELETE(id));
    },
};

export default subjectService;
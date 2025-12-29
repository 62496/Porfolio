import apiClient from '../client';
import { API_ENDPOINTS } from '../endpoints';

const authorService = {
    /**
     * Get all authors
     * @param {Object} params
     * @returns {Promise}
     */
    getAll: async (params = {}) => {
        try {
            const response = await apiClient.get(API_ENDPOINTS.AUTHORS.GET_ALL, {
                params,
            });
            return response.data;
        } catch (error) {
            throw error;
        }
    },

    /**
     * Get author by ID with detailed information
     * @param {string|number} id - Author ID
     * @returns {Promise} - Author detail object with books and series
     */
    getById: async (id) => {
        try {
            const response = await apiClient.get(API_ENDPOINTS.AUTHORS.GET_BY_ID(id));
            return response.data;
        } catch (error) {
            throw error;
        }
    },

    create: async (formData = {}) => {
        try {
            const response = await apiClient.post(
                API_ENDPOINTS.AUTHORS.CREATE,
                formData,
                {
                    headers: {
                        "Content-Type": "multipart/form-data",
                    },
                }
            );
            return response.data;
        } catch (error) {
            throw error;
        }
    },

    /**
     * Update an existing author (requires LIBRARIAN role)
     * @param {string|number} id - Author ID
     * @param {FormData} formData - Form data with author JSON and optional image file
     * @returns {Promise} - Updated author object
     */
    update: async (id, formData) => {
        try {
            const response = await apiClient.put(
                API_ENDPOINTS.AUTHORS.UPDATE(id),
                formData,
                {
                    headers: {
                        "Content-Type": "multipart/form-data",
                    },
                }
            );
            return response.data;
        } catch (error) {
            throw error;
        }
    },

    /**
     * Delete an author (requires ADMIN role)
     * Cascade: deletes all books by author, series, removes from followers
     * @param {string|number} id - Author ID
     * @returns {Promise} - 204 No Content on success
     */
    delete: async (id) => {
        try {
            const response = await apiClient.delete(API_ENDPOINTS.AUTHORS.DELETE(id));
            return response.data;
        } catch (error) {
            throw error;
        }
    }
};

export default authorService;
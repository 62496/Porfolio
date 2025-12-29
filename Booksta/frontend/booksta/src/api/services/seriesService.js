import apiClient from '../client';
import { API_ENDPOINTS } from '../endpoints';

const seriesService = {
    /**
     * Get all series
     */
    getAll: async (params = {}) => {
        try {
            const response = await apiClient.get(API_ENDPOINTS.SERIES.GET_ALL, { params });
            return response.data;
        } catch (err) {
            console.error('Error fetching series list:', err);
            throw err;
        }
    },

    /**
     * Get series by ID
     */
    getById: async (id) => {
        try {
            const response = await apiClient.get(API_ENDPOINTS.SERIES.GET_BY_ID(id));
            return response.data;
        } catch (err) {
            console.error('Error fetching series by id:', err);
            throw err;
        }
    },

    /**
     * Get series by author ID
     */
    getByAuthor: async (authorId) => {
        try {
            const response = await apiClient.get(API_ENDPOINTS.SERIES.GET_BY_AUTHOR(authorId));
            return response.data;
        } catch (err) {
            console.error('Error fetching series by author:', err);
            throw err;
        }
    },

    /**
     * Get books in a series
     */
    getBooks: async (id) => {
        try {
            const response = await apiClient.get(API_ENDPOINTS.SERIES.GET_BOOKS(id));
            return response.data;
        } catch (err) {
            console.error('Error fetching series books:', err);
            throw err;
        }
    },

    /**
     * Create a new series (AUTHOR only)
     */
    create: async (payload) => {
        try {
            const response = await apiClient.post(API_ENDPOINTS.SERIES.CREATE, payload);
            return response.data;
        } catch (err) {
            console.error('Error creating series:', err);
            throw err;
        }
    },

    /**
     * Update a series (AUTHOR owner only)
     */
    update: async (id, payload) => {
        try {
            const response = await apiClient.put(API_ENDPOINTS.SERIES.UPDATE(id), payload);
            return response.data;
        } catch (err) {
            console.error('Error updating series:', err);
            throw err;
        }
    },

    /**
     * Delete a series (AUTHOR owner only)
     */
    delete: async (id) => {
        try {
            const response = await apiClient.delete(API_ENDPOINTS.SERIES.DELETE(id));
            return response.data;
        } catch (err) {
            console.error('Error deleting series:', err);
            throw err;
        }
    },

    /**
     * Add a book to a series (AUTHOR owner only)
     */
    addBook: async (seriesId, isbn) => {
        try {
            const response = await apiClient.post(API_ENDPOINTS.SERIES.ADD_BOOK(seriesId, isbn));
            return response.data;
        } catch (err) {
            console.error('Error adding book to series:', err);
            throw err;
        }
    },

    /**
     * Remove a book from a series (AUTHOR owner only)
     */
    removeBook: async (seriesId, isbn) => {
        try {
            const response = await apiClient.delete(API_ENDPOINTS.SERIES.REMOVE_BOOK(seriesId, isbn));
            return response.data;
        } catch (err) {
            console.error('Error removing book from series:', err);
            throw err;
        }
    },
};

export default seriesService;
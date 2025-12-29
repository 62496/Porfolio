import apiClient from '../client';
import { API_ENDPOINTS } from '../endpoints';

const bookCollectionsService = {
    // ========================
    // CRUD Operations
    // ========================

    /**
     * Create a new collection (multipart: collection + optional image)
     */
    create: async (collectionData, imageFile = null) => {
        const formData = new FormData();
        formData.append(
            'collection',
            new Blob([JSON.stringify(collectionData)], { type: 'application/json' })
        );
        if (imageFile) {
            formData.append('image', imageFile);
        }
        const response = await apiClient.post(API_ENDPOINTS.COLLECTIONS.CREATE, formData, {
            headers: { 'Content-Type': 'multipart/form-data' },
        });
        return response.data;
    },

    /**
     * Update a collection (multipart: collection + optional image)
     */
    update: async (collectionId, collectionData, imageFile = null) => {
        const formData = new FormData();
        formData.append(
            'collection',
            new Blob([JSON.stringify(collectionData)], { type: 'application/json' })
        );
        if (imageFile) {
            formData.append('image', imageFile);
        }
        const response = await apiClient.put(
            API_ENDPOINTS.COLLECTIONS.UPDATE(collectionId),
            formData,
            { headers: { 'Content-Type': 'multipart/form-data' } }
        );
        return response.data;
    },

    /**
     * Delete a collection
     */
    delete: async (collectionId) => {
        const response = await apiClient.delete(API_ENDPOINTS.COLLECTIONS.DELETE(collectionId));
        return response.data;
    },

    /**
     * Get a single collection by ID
     */
    getById: async (collectionId) => {
        const response = await apiClient.get(API_ENDPOINTS.COLLECTIONS.GET_BY_ID(collectionId));
        return response.data;
    },

    // ========================
    // Listing
    // ========================

    /**
     * Get collections owned by current user
     */
    getOwnCollections: async () => {
        const response = await apiClient.get(API_ENDPOINTS.COLLECTIONS.GET_OWN);
        return response.data;
    },

    /**
     * Get all collections user has access to (own + shared + public)
     */
    getAllowedCollections: async () => {
        const response = await apiClient.get(API_ENDPOINTS.COLLECTIONS.GET_ALLOWED);
        return response.data;
    },

    /**
     * Get all public collections
     */
    getPublicCollections: async () => {
        const response = await apiClient.get(API_ENDPOINTS.COLLECTIONS.GET_PUBLIC);
        return response.data;
    },

    /**
     * Get collections shared with current user
     */
    getSharedCollections: async () => {
        const response = await apiClient.get(API_ENDPOINTS.COLLECTIONS.GET_SHARED);
        return response.data;
    },

    // ========================
    // Access Control
    // ========================

    /**
     * Check if current user can access a collection
     */
    checkAccess: async (collectionId) => {
        const response = await apiClient.get(API_ENDPOINTS.COLLECTIONS.CHECK_ACCESS(collectionId));
        return response.data;
    },

    // ========================
    // Sharing
    // ========================

    /**
     * Share collection with a user by email
     */
    shareWithUser: async (collectionId, userEmail) => {
        const response = await apiClient.post(
            API_ENDPOINTS.COLLECTIONS.SHARE(collectionId, userEmail),
            {}
        );
        return response.data;
    },

    /**
     * Remove a user from collection sharing
     */
    unshareWithUser: async (collectionId, userId) => {
        const response = await apiClient.delete(
            API_ENDPOINTS.COLLECTIONS.UNSHARE(collectionId, userId)
        );
        return response.data;
    },

    // ========================
    // Books Management
    // ========================

    /**
     * Add a book to collection
     */
    addBook: async (collectionId, isbn) => {
        const response = await apiClient.post(
            API_ENDPOINTS.COLLECTIONS.ADD_BOOK(collectionId, isbn),
            {}
        );
        return response.data;
    },

    /**
     * Remove a book from collection
     */
    removeBook: async (collectionId, isbn) => {
        const response = await apiClient.delete(
            API_ENDPOINTS.COLLECTIONS.REMOVE_BOOK(collectionId, isbn)
        );
        return response.data;
    },

    /**
     * Check if collection contains a book
     */
    containsBook: async (collectionId, isbn) => {
        const response = await apiClient.get(
            API_ENDPOINTS.COLLECTIONS.CONTAINS_BOOK(collectionId, isbn)
        );
        return response.data;
    },

    // ========================
    // Image
    // ========================

    /**
     * Get collection image URL
     */
    getImageUrl: (collectionId) => {
        const baseUrl = process.env.REACT_APP_API_BASE_URL || 'http://localhost:8081/api';
        return `${baseUrl}${API_ENDPOINTS.COLLECTIONS.GET_IMAGE(collectionId)}`;
    },
};

export default bookCollectionsService;

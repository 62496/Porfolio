import apiClient from '../client';
import { API_ENDPOINTS } from '../endpoints';

const inventoryService = {
    /**
     * Get all inventory items for the current user
     * @returns {Promise<Array>} - Array of inventory items with book, quantity, and pricePerUnit
     */
    getAll: async () => {
        const response = await apiClient.get(API_ENDPOINTS.INVENTORY.GET_ALL);
        return response.data;
    },

    /**
     * Add a book to inventory
     * @param {string} bookIsbn - ISBN of the book
     * @param {number} quantity - Stock quantity
     * @param {number} pricePerUnit - Price per unit
     * @returns {Promise} - Created inventory item
     */
    create: async (bookIsbn, quantity, pricePerUnit) => {
        const response = await apiClient.post(API_ENDPOINTS.INVENTORY.CREATE, {
            bookIsbn,
            quantity,
            pricePerUnit,
        });
        return response.data;
    },

    /**
     * Update an inventory item
     * @param {string} bookIsbn - ISBN of the book
     * @param {number} quantity - Updated stock quantity
     * @param {number} pricePerUnit - Updated price per unit
     * @returns {Promise} - Updated inventory item
     */
    update: async (bookIsbn, quantity, pricePerUnit) => {
        const response = await apiClient.put(API_ENDPOINTS.INVENTORY.UPDATE(bookIsbn), {
            quantity,
            pricePerUnit,
        });
        return response.data;
    },

    /**
     * Remove a book from inventory
     * @param {string} bookIsbn - ISBN of the book to remove
     * @returns {Promise}
     */
    delete: async (bookIsbn) => {
        const response = await apiClient.delete(API_ENDPOINTS.INVENTORY.DELETE(bookIsbn));
        return response.data;
    },
};

export default inventoryService;

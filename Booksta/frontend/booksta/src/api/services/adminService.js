import apiClient from '../client';
import { API_ENDPOINTS } from '../endpoints';

const adminService = {
    /**
     * Get all users
     * @returns {Promise<Array>} - Array of user objects
     */
    getAllUsers: async () => {
        const response = await apiClient.get(API_ENDPOINTS.ADMIN.GET_ALL_USERS);
        return response.data;
    },

    /**
     * Get user by ID
     * @param {number} id - User ID
     * @returns {Promise<Object>} - User object
     */
    getUserById: async (id) => {
        const response = await apiClient.get(API_ENDPOINTS.ADMIN.GET_USER_BY_ID(id));
        return response.data;
    },

    /**
     * Get all available roles
     * @returns {Promise<Array>} - Array of role objects
     */
    getAllRoles: async () => {
        const response = await apiClient.get(API_ENDPOINTS.ADMIN.GET_ALL_ROLES);
        return response.data;
    },

    /**
     * Add role to user
     * @param {number} userId - User ID
     * @param {string} roleName - Role name (e.g., 'SELLER', 'ADMIN')
     * @returns {Promise<Object>} - Updated user object
     */
    addRoleToUser: async (userId, roleName) => {
        const response = await apiClient.post(API_ENDPOINTS.ADMIN.ADD_ROLE(userId, roleName));
        return response.data;
    },

    /**
     * Remove role from user
     * @param {number} userId - User ID
     * @param {string} roleName - Role name
     * @returns {Promise<Object>} - Updated user object
     */
    removeRoleFromUser: async (userId, roleName) => {
        const response = await apiClient.delete(API_ENDPOINTS.ADMIN.REMOVE_ROLE(userId, roleName));
        return response.data;
    },

    /**
     * Format user for display
     * @param {Object} user - Raw user from API
     * @returns {Object} - Formatted user object
     */
    formatUserForDisplay: (user) => {
        if (!user) return null;

        return {
            id: user.id,
            email: user.email,
            firstName: user.firstName,
            lastName: user.lastName,
            fullName: `${user.firstName || ''} ${user.lastName || ''}`.trim() || 'Unknown User',
            picture: user.picture,
            googleId: user.googleId,
            roles: user.roles || [],
            roleNames: (user.roles || []).map(r => r.name),
        };
    },
};

export default adminService;

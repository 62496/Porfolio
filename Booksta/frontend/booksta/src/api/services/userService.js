import apiClient from '../client';
import { API_ENDPOINTS } from '../endpoints';

const userService = {

    getCurrentUser() {
        const userStr = localStorage.getItem('user');
        if (!userStr) return null;

        try {
            return JSON.parse(userStr);
        } catch {
            return null;
        }
    },

    /* =========================
       Favorites
       ========================= */

    addFavorite: async (bookIsbn) => {
        try {
            const response = await apiClient.post(API_ENDPOINTS.USERS.ADD_FAVORITE(bookIsbn));
            return response.data;
        } catch (error) {
            console.error('Error adding favorite:', error);
            throw error;
        }
    },

    removeFavorite: async (bookIsbn) => {
        try {
            const response = await apiClient.delete(API_ENDPOINTS.USERS.REMOVE_FAVORITE(bookIsbn));
            return response.data;
        } catch (error) {
            console.error('Error removing favorite:', error);
            throw error;
        }
    },

    getFavorites: async () => {
        try {
            const response = await apiClient.get(API_ENDPOINTS.USERS.GET_FAVORITES);
            return response.data;
        } catch (error) {
            console.error('Error fetching favorites:', error);
            throw error;
        }
    },

    isFavorite(favorites, bookIsbn) {
        return Array.isArray(favorites)
            ? favorites.some(book => book.isbn === bookIsbn)
            : false;
    },

    /* =========================
       Google user search
       ========================= */

    searchGoogleUsers: async (query, excludeId) => {
        try {
            const response = await apiClient.get(API_ENDPOINTS.USERS.SEARCH_GOOGLE, {
                params: { query, excludeId }
            });
            return response.data;
        } catch (error) {
            console.error('Error searching users:', error);
            throw error;
        }
    },

    /* =========================
       Follow / Unfollow Authors
       ========================= */

    followAuthor: async (authorId) => {
        const currentUser = userService.getCurrentUser();
        if (!currentUser?.id) throw new Error('Not authenticated');

        try {
            const response = await apiClient.post(API_ENDPOINTS.USERS.FOLLOW_AUTHOR(authorId));
            return response.data;
        } catch (error) {
            console.error('Error following author:', error);
            throw error;
        }
    },

    unfollowAuthor: async (authorId) => {
        const currentUser = userService.getCurrentUser();
        if (!currentUser?.id) throw new Error('Not authenticated');

        try {
            const response = await apiClient.delete(API_ENDPOINTS.USERS.UNFOLLOW_AUTHOR(authorId));
            return response.data;
        } catch (error) {
            console.error('Error unfollowing author:', error);
            throw error;
        }
    },

    getFollowedAuthors: async () => {
        try {
            const response = await apiClient.get(API_ENDPOINTS.USERS.GET_FOLLOWED_AUTHORS);
            return response.data;
        } catch (error) {
            console.error('Error fetching followed authors:', error);
            throw error;
        }
    },

    isFollowingAuthor: async (authorId) => {
        const currentUser = userService.getCurrentUser();
        if (!currentUser?.id) return false;

        try {
            const response = await apiClient.get(API_ENDPOINTS.USERS.IS_FOLLOWING_AUTHOR(authorId));
            return response.data === true;
        } catch (error) {
            console.error('Error checking follow status for author:', error);
            return false;
        }
    },

    /* =========================
       Follow / Unfollow Series
       ========================= */

    followSeries: async (seriesId) => {
        const currentUser = userService.getCurrentUser();
        if (!currentUser?.id) throw new Error('Not authenticated');

        try {
            const response = await apiClient.post(API_ENDPOINTS.USERS.FOLLOW_SERIES(seriesId));
            return response.data;
        } catch (error) {
            console.error('Error following series:', error);
            throw error;
        }
    },

    unfollowSeries: async (seriesId) => {
        const currentUser = userService.getCurrentUser();
        if (!currentUser?.id) throw new Error('Not authenticated');

        try {
            const response = await apiClient.delete(API_ENDPOINTS.USERS.UNFOLLOW_SERIES(seriesId));
            return response.data;
        } catch (error) {
            console.error('Error unfollowing series:', error);
            throw error;
        }
    },

    getFollowedSeries: async () => {
        try {
            const response = await apiClient.get(API_ENDPOINTS.USERS.GET_FOLLOWED_SERIES);
            return response.data;
        } catch (error) {
            console.error('Error fetching followed series:', error);
            throw error;
        }
    },

    isFollowingSeries: async (seriesId) => {
        const currentUser = userService.getCurrentUser();
        if (!currentUser?.id) return false;

        try {
            const response = await apiClient.get(API_ENDPOINTS.USERS.IS_FOLLOWING_SERIES(seriesId));
            return response.data === true;
        } catch (error) {
            console.error('Error checking follow status for series:', error);
            return false;
        }
    },

    /* =========================
       Owned Books
       ========================= */

    getOwnedBooks: async () => {
        const currentUser = userService.getCurrentUser();
        if (!currentUser?.id) throw new Error('Not authenticated');

        try {
            const response = await apiClient.get(API_ENDPOINTS.USERS.GET_OWNED_BOOKS(currentUser.id));
            return response.data;
        } catch (error) {
            console.error('Error fetching owned books:', error);
            throw error;
        }
    },

    getOwnedBooksWithLatestReadingStatus: async () => {
        try {
            const response = await apiClient.get(API_ENDPOINTS.CURRENT_USER.GET_BOOKS_WITH_READ_STATUS);
            return response.data;
        } catch (error) {
            console.error("Error fetching owned books with reading status:", error);
            throw error;
        }
    },

    getOwnedBooksWithExistingReadingStatus: async () => {
        try {
            const response = await apiClient.get(API_ENDPOINTS.CURRENT_USER.GET_BOOKS_WITH_EXISTING_READ_STATUS);
            return response.data;
        } catch (error) {
            console.error("Error fetching owned books that have at least one reading status");
            throw error;
        }
    },

    addOwnedBook: async (bookIsbn) => {
        const currentUser = userService.getCurrentUser();
        if (!currentUser?.id) throw new Error('Not authenticated');

        try {
            const response = await apiClient.post(
                API_ENDPOINTS.USERS.ADD_OWNED_BOOK(currentUser.id, bookIsbn)
            );
            return response.data;
        } catch (error) {
            console.error('Error adding owned book:', error);
            throw error;
        }
    },

    removeOwnedBook: async (bookIsbn) => {
        const currentUser = userService.getCurrentUser();
        if (!currentUser?.id) throw new Error('Not authenticated');

        try {
            const response = await apiClient.delete(
                API_ENDPOINTS.USERS.REMOVE_OWNED_BOOK(currentUser.id, bookIsbn)
            );
            return response.data;
        } catch (error) {
            console.error('Error removing owned book:', error);
            throw error;
        }
    },

    isOwned(ownedBooks, bookIsbn) {
        return Array.isArray(ownedBooks)
            ? ownedBooks.some(book => book.isbn === bookIsbn)
            : false;
    }
};

export default userService;

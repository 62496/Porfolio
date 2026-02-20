import apiClient from '../client';
import { API_ENDPOINTS } from '../endpoints';

const reportService = {
    /**
     * Create a book report
     * @param {Object} params
     * @returns {Promise}
     */
    createBookReport: async (bookId, data = {}) => {
        try {
            const response = await apiClient.post(API_ENDPOINTS.REPORTS.CREATE_BOOK_REPORT(bookId), data);
            return response.data;
        } catch (error) {
            throw error;
        }
    },

    /**
     * Gets all reports
     * @returns a list of all reports
     */
    getAll: async () => {
        const response = await apiClient.get(API_ENDPOINTS.REPORTS.GET_ALL);
        return response.data;
    },

    /**
     * Resolves a book report
     * @param {Object} payload - Resolve information
     * @returns {Promise} - new report state
     */
    resolveBookReport: async (reportId, payload) => {
        try {
            const response = await apiClient.post(
                API_ENDPOINTS.REPORTS.RESOLVE_BOOK_REPORT(reportId),
                payload,
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

    dismissBookReport: async (reportId) => {
        const response = await apiClient.post(
            API_ENDPOINTS.REPORTS.DISMISS_REPORT(reportId)
        );
        return response.data;
    }
};

export default reportService;
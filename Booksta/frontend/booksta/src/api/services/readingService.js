import apiClient from "../client";
import API_ENDPOINTS from "../endpoints";

const readingService = {
    /**
     * Start a new reading session
     * Backend sets: startedAt, lastResumedAt, totalActiveSeconds=0, status=ACTIVE
     * @param {string} isbn - Book ISBN
     * @param {number} startPage - Starting page number
     */
    create: async (isbn, startPage) => {
        const response = await apiClient.post(
            API_ENDPOINTS.READING_SESSION.CREATE,
            { isbn, startPage }
        );
        return response.data;
    },

    /**
     * Pause an active session
     * Backend adds active time since last resume and sets status=PAUSED
     * Idempotent: safe to call multiple times
     * @param {number} sessionId - Session ID
     */
    pause: async (sessionId) => {
        const response = await apiClient.put(
            API_ENDPOINTS.READING_SESSION.PAUSE(sessionId)
        );
        return response.data;
    },

    /**
     * Resume a paused session
     * Backend sets status=ACTIVE and lastResumedAt=now
     * Does NOT add time, only restarts the clock
     * @param {number} sessionId - Session ID
     */
    resume: async (sessionId) => {
        const response = await apiClient.put(
            API_ENDPOINTS.READING_SESSION.RESUME(sessionId)
        );
        return response.data;
    },

    /**
     * End a session with final details
     * Should call pause() first to stop the timer
     * @param {number} sessionId - Session ID
     * @param {Object} data - End session data
     * @param {number} [data.startPage] - Optional start page override
     * @param {number} data.endPage - Required end page
     * @param {string} [data.note] - Optional notes
     */
    end: async (sessionId, { startPage, endPage, note }) => {
        const response = await apiClient.put(
            API_ENDPOINTS.READING_SESSION.END(sessionId),
            { startPage, endPage, note }
        );
        return response.data;
    },

    /**
     * Delete a reading session
     * @param {number} sessionId - Session ID
     */
    delete: async (sessionId) => {
        const response = await apiClient.delete(
            API_ENDPOINTS.READING_SESSION.DELETE(sessionId)
        );
        return response.data;
    },
};

export default readingService;

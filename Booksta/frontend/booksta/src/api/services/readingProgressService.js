import apiClient from "../client";

const API_URL = "/progress";

class ReadingProgressService {

    async getUserProgress() {
        const response = await apiClient.get(API_URL);
        return response.data;
    }

    async createProgress(bookIsbn, currentPage) {
        const response = await apiClient.post(`${API_URL}/create/${bookIsbn}`, {}, {
            params: { currentPage }
        });
        return response.data;
    }

    async updateProgress(bookIsbn, currentPage) {
        const response = await apiClient.post(`${API_URL}/update/${bookIsbn}`, {}, {
            params: { currentPage }
        });
        return response.data;
    }
}

export default new ReadingProgressService();

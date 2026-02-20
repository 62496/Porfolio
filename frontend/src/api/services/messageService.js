import apiClient from "../client";

const basePath = "/messages";

const messageService = {
  async listConversations() {
    const response = await apiClient.get(`${basePath}/conversations`);
    return response.data;
  },

  async getConversationMessages(conversationId) {
    const response = await apiClient.get(
      `${basePath}/conversations/${conversationId}`
    );
    return response.data;
  },

  async sendMessage(recipientId, content) {
    const response = await apiClient.post(basePath, {
      recipientId,
      content,
    });
    return response.data;
  },

  async markConversationAsRead(conversationId) {
    await apiClient.post(`${basePath}/conversations/${conversationId}/read`);
  },
};

export default messageService;

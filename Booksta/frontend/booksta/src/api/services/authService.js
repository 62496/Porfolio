// src/api/services/authService.js
import axios from "axios";
import API_ENDPOINTS from "../endpoints";

const API_BASE_URL = process.env.REACT_APP_API_BASE_URL || "http://localhost:8081/api";

// In-memory storage for access token (more secure than localStorage)
let accessToken = null;

// Subscribers for token changes (used by apiClient)
let tokenChangeCallbacks = [];

class AuthService {
    constructor() {
        // Try to restore session on initialization
        this._initializeFromStorage();
    }

    _initializeFromStorage() {
        // Check if we have a refresh token - session will be restored when needed
        this.getRefreshToken();
    }

    // Subscribe to token changes
    onTokenChange(callback) {
        tokenChangeCallbacks.push(callback);
        return () => {
            tokenChangeCallbacks = tokenChangeCallbacks.filter(cb => cb !== callback);
        };
    }

    // Notify subscribers of token change
    _notifyTokenChange(token) {
        tokenChangeCallbacks.forEach(cb => cb(token));
    }

    // Get access token (in-memory)
    getAccessToken() {
        return accessToken;
    }

    // Set access token (in-memory) and notify subscribers
    setAccessToken(token) {
        accessToken = token;
        this._notifyTokenChange(token);
    }

    // Get refresh token from localStorage
    getRefreshToken() {
        return localStorage.getItem("refreshToken");
    }

    // Set refresh token in localStorage
    setRefreshToken(token) {
        if (token) {
            localStorage.setItem("refreshToken", token);
        } else {
            localStorage.removeItem("refreshToken");
        }
    }

    async loginWithGoogle(googleToken) {
        const response = await axios.post(`${API_BASE_URL}${API_ENDPOINTS.AUTH.GOOGLE}`, {
            token: googleToken
        });

        const { accessToken: newAccessToken, refreshToken, user } = response.data;

        if (newAccessToken && refreshToken && user) {
            // Store access token in memory
            this.setAccessToken(newAccessToken);
            // Store refresh token in localStorage
            this.setRefreshToken(refreshToken);
            // Store user in localStorage
            localStorage.setItem("user", JSON.stringify(user));
        }

        return user;
    }

    async logout() {
        const refreshToken = this.getRefreshToken();

        // Call logout endpoint to invalidate refresh token on server
        if (refreshToken) {
            try {
                await axios.post(`${API_BASE_URL}${API_ENDPOINTS.AUTH.LOGOUT}`, {
                    refreshToken
                });
            } catch {
                // Ignore logout API failures
            }
        }

        // Clear local state
        this.setAccessToken(null);
        this.setRefreshToken(null);
        localStorage.removeItem("user");
    }

    async refreshAccessToken() {
        const refreshToken = this.getRefreshToken();

        if (!refreshToken) {
            return null;
        }

        try {
            const response = await axios.post(`${API_BASE_URL}${API_ENDPOINTS.AUTH.REFRESH}`, {
                refreshToken
            });

            const { accessToken: newAccessToken, refreshToken: newRefreshToken } = response.data;

            if (newAccessToken) {
                this.setAccessToken(newAccessToken);
                // Update refresh token if a new one is provided
                if (newRefreshToken) {
                    this.setRefreshToken(newRefreshToken);
                }
                return newAccessToken;
            } else {
                // Refresh failed - tokens are invalid
                this.setAccessToken(null);
                this.setRefreshToken(null);
                localStorage.removeItem("user");
                return null;
            }
        } catch {
            // Clear tokens on refresh failure
            this.setAccessToken(null);
            this.setRefreshToken(null);
            localStorage.removeItem("user");
            return null;
        }
    }

    getCurrentUser() {
        const userStr = localStorage.getItem("user");

        if (!userStr) {
            return null;
        }

        try {
            return JSON.parse(userStr);
        } catch {
            return null;
        }
    }

    // Legacy method for compatibility - returns access token
    getToken() {
        return this.getAccessToken();
    }

    isAuthenticated() {
        // Check if we have either an access token or a refresh token
        return !!(this.getAccessToken() || this.getRefreshToken());
    }

    hasValidAccessToken() {
        return !!this.getAccessToken();
    }

    async fetchCurrentUserFromApi() {
        // Ensure we have a valid access token
        let token = this.getAccessToken();

        if (!token) {
            // Try to refresh
            token = await this.refreshAccessToken();
            if (!token) {
                return null;
            }
        }

        try {
            const response = await axios.get(`${API_BASE_URL}${API_ENDPOINTS.AUTH.ME}`, {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });

            const user = response.data;
            localStorage.setItem("user", JSON.stringify(user));
            return user;
        } catch (error) {
            if (error.response?.status === 401) {
                // Try to refresh and retry once
                const newToken = await this.refreshAccessToken();
                if (newToken) {
                    try {
                        const response = await axios.get(`${API_BASE_URL}${API_ENDPOINTS.AUTH.ME}`, {
                            headers: {
                                Authorization: `Bearer ${newToken}`,
                            },
                        });
                        const user = response.data;
                        localStorage.setItem("user", JSON.stringify(user));
                        return user;
                    } catch {
                        // Retry failed
                    }
                }
                // Refresh failed, logout
                await this.logout();
            }
            return null;
        }
    }
}

const authService = new AuthService();
export default authService;

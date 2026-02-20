// src/api/client.js
import axios from 'axios';
import authService from './services/authService';

const apiClient = axios.create({
  baseURL: process.env.REACT_APP_API_BASE_URL ,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Flag to prevent multiple refresh attempts
let isRefreshing = false;
// Queue of failed requests to retry after token refresh
let failedQueue = [];

const processQueue = (error, token = null) => {
  failedQueue.forEach(prom => {
    if (error) {
      prom.reject(error);
    } else {
      prom.resolve(token);
    }
  });
  failedQueue = [];
};

// Request interceptor - add access token to all requests
apiClient.interceptors.request.use(
  async (config) => {
    // Get access token from authService (in-memory)
    let token = authService.getAccessToken();

    // If no access token but we have refresh token, try to refresh first
    if (!token && authService.getRefreshToken()) {
      token = await authService.refreshAccessToken();
    }

    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }

    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor - handle 401 and refresh token
apiClient.interceptors.response.use(
  (response) => {
    return response;
  },
  async (error) => {
    const originalRequest = error.config;

    if (error.response) {
      const { status } = error.response;

      // Handle 401 - try to refresh token
      if (status === 401 && !originalRequest._retry) {
        // Check if we have a refresh token
        const refreshToken = authService.getRefreshToken();

        if (!refreshToken) {
          authService.logout();
          window.location.href = '/login';
          return Promise.reject(error);
        }

        if (isRefreshing) {
          // If already refreshing, queue this request
          return new Promise((resolve, reject) => {
            failedQueue.push({ resolve, reject });
          })
            .then(token => {
              originalRequest.headers.Authorization = `Bearer ${token}`;
              return apiClient(originalRequest);
            })
            .catch(err => Promise.reject(err));
        }

        originalRequest._retry = true;
        isRefreshing = true;

        try {
          const newToken = await authService.refreshAccessToken();

          if (newToken) {
            // Refresh successful, retry the original request
            originalRequest.headers.Authorization = `Bearer ${newToken}`;
            processQueue(null, newToken);
            return apiClient(originalRequest);
          } else {
            // Refresh failed
            processQueue(error, null);
            window.location.href = '/login';
            return Promise.reject(error);
          }
        } catch (refreshError) {
          processQueue(refreshError, null);
          authService.logout();
          window.location.href = '/login';
          return Promise.reject(refreshError);
        } finally {
          isRefreshing = false;
        }
      }

    }

    return Promise.reject(error);
  }
);

export default apiClient;

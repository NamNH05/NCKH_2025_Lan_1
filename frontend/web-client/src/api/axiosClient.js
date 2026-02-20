import axios from "axios";
import { getCSRFToken, invalidateCSRFToken, getCachedCSRFToken } from "./csrfClient";

const axiosClient = axios.create({
  baseURL: "http://localhost:3000/api",
  withCredentials: true // Send cookies automatically
});

/**
 * Request interceptor
 * Adds JWT token and CSRF token to requests
 */
axiosClient.interceptors.request.use(async (config) => {
  // Add JWT token
  const token = localStorage.getItem("token");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  // Add CSRF token for state-changing operations
  if (['post', 'put', 'delete', 'patch'].includes(config.method?.toLowerCase())) {
    try {
      const csrfToken = await getCSRFToken();
      if (csrfToken) {
        config.headers['X-CSRF-Token'] = csrfToken;
      }
    } catch (error) {
      console.error('[Axios] Failed to add CSRF token:', error.message);
      // Continue request without CSRF token - backend will reject if needed
    }
  }

  return config;
});

/**
 * Response interceptor
 * Handles errors and token invalidation
 */
axiosClient.interceptors.response.use(
  response => response,
  error => {
    // Handle 401 Unauthorized
    if (error.response?.status === 401) {
      const token = localStorage.getItem("token");
      if (token) {
        localStorage.removeItem("token");
        localStorage.removeItem("user");
        window.location.href = "/login";
      }
    }

    // Handle 403 Forbidden (CSRF token invalid)
    if (error.response?.status === 403) {
      const errorMsg = error.response?.data?.error || '';
      if (errorMsg.includes('CSRF') || errorMsg.includes('token')) {
        console.warn('[Axios] CSRF token invalid, invalidating cache');
        invalidateCSRFToken();
      }
    }

    return Promise.reject(error);
  }
);

export default axiosClient;

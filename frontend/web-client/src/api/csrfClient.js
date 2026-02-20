/**
 * CSRF Token Manager for Frontend
 * Handles retrieval and management of CSRF tokens
 * 
 * @module csrfClient
 */

import axios from 'axios';

// Use relative path - Vite proxy will forward to API Gateway
// Store CSRF token in memory
let cachedCSRFToken = null;
let tokenExpireTime = null;

/**
 * Retrieve CSRF token from server
 * Caches token for 1 hour to reduce API calls
 * 
 * @returns {Promise<string>} CSRF token
 * @throws {Error} If token retrieval fails
 */
export const getCSRFToken = async () => {
  try {
    // Return cached token if still valid
    if (cachedCSRFToken && tokenExpireTime && Date.now() < tokenExpireTime) {
      console.debug('[CSRF] Using cached token');
      return cachedCSRFToken;
    }

    console.debug('[CSRF] Fetching new token from server');
    // Use relative path - Vite proxy handles routing to API Gateway
    const response = await axios.get('/api/csrf-token', {
      withCredentials: true
    });

    cachedCSRFToken = response.data.csrfToken;
    // Token expires in 55 minutes (5 minute buffer)
    tokenExpireTime = Date.now() + (55 * 60 * 1000);

    return cachedCSRFToken;
  } catch (error) {
    console.error('[CSRF] Failed to get CSRF token:', error.message);
    cachedCSRFToken = null;
    tokenExpireTime = null;
    throw new Error('Failed to retrieve CSRF token');
  }
};

/**
 * Invalidate cached CSRF token
 * Should be called after logout or on 403 Forbidden
 */
export const invalidateCSRFToken = () => {
  console.debug('[CSRF] Invalidating cached token');
  cachedCSRFToken = null;
  tokenExpireTime = null;
};

/**
 * Get current cached token without fetching
 * Returns null if no cached token available
 * 
 * @returns {string|null} Cached CSRF token or null
 */
export const getCachedCSRFToken = () => {
  if (cachedCSRFToken && tokenExpireTime && Date.now() < tokenExpireTime) {
    return cachedCSRFToken;
  }
  return null;
};

export default {
  getCSRFToken,
  invalidateCSRFToken,
  getCachedCSRFToken
};

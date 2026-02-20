import axiosClient from './axiosClient';

// Get all users (admin only)
export const getUsersApi = () => {
  return axiosClient.get('/users');
};

// Get user by ID
export const getUserByIdApi = (id) => {
  return axiosClient.get(`/users/${id}`);
};

// Create new user (admin only)
export const createUserApi = (userData) => {
  return axiosClient.post('/users', userData);
};

// Update user (admin only)
export const updateUserApi = (id, userData) => {
  console.log('[updateUserApi] Calling with ID:', id);
  console.log('[updateUserApi] Sending data:', userData);
  console.log('[updateUserApi] Full URL:', `/users/${id}`);
  return axiosClient.put(`/users/${id}`, userData);
};

// Delete user (admin only)
export const deleteUserApi = (id) => {
  return axiosClient.delete(`/users/${id}`);
};

import axiosClient from "./axiosClient";

// Get all addresses for user
export const getUserAddressesApi = (userId) => {
  return axiosClient.get(`/addresses/user/${userId}`);
};

// Create new address
export const createAddressApi = (userId, addressData) => {
  return axiosClient.post(`/addresses/user/${userId}`, addressData);
};

// Update address
export const updateAddressApi = (userId, addressId, addressData) => {
  return axiosClient.put(`/addresses/${addressId}/user/${userId}`, addressData);
};

// Delete address
export const deleteAddressApi = (userId, addressId) => {
  return axiosClient.delete(`/addresses/${addressId}/user/${userId}`);
};

import axiosClient from "./axiosClient";

// Lấy tất cả sản phẩm
export const getAllProductsApi = () => {
  return axiosClient.get("/products");
};

// Lấy sản phẩm theo ID
export const getProductByIdApi = (id) => {
  return axiosClient.get(`/products/${id}`);
};

// Lấy sản phẩm theo loại
export const getProductsByCategoryApi = (type) => {
  return axiosClient.get(`/products/category/${type}`);
};

// Tìm kiếm sản phẩm
export const searchProductsApi = (keyword) => {
  return axiosClient.get(`/products/search?keyword=${keyword}`);
};

// Thêm sản phẩm mới (admin)
export const createProductApi = (productData) => {
  return axiosClient.post("/products", productData);
};

// Cập nhật sản phẩm (admin)
export const updateProductApi = (id, productData) => {
  return axiosClient.put(`/products/${id}`, productData);
};

// Xóa sản phẩm (admin)
export const deleteProductApi = (id) => {
  return axiosClient.delete(`/products/${id}`);
};

// Mua sản phẩm
export const purchaseProductApi = (id, quantity) => {
  return axiosClient.post(`/products/purchase/${id}?quantity=${quantity}`);
};

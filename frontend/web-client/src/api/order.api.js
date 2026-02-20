import axiosClient from './axiosClient';

const orderAPI = {
  // ============ CART OPERATIONS ============
  
  /**
   * Get user's cart
   */
  getCart: (userId) => {
    return axiosClient.get(`/carts/${userId}`);
  },

  /**
   * Add item to cart
   */
  addItemToCart: (userId, productName, price, quantity) => {
    return axiosClient.post(`/carts/${userId}/items`, {
      name: productName,
      price,
      quantity
    });
  },

  /**
   * Update cart item quantity and price
   */
  updateCartItem: (itemId, productName, price, quantity) => {
    return axiosClient.put(`/carts/items/${itemId}`, {
      name: productName,
      price,
      quantity
    });
  },

  /**
   * Remove item from cart
   */
  removeCartItem: (itemId) => {
    return axiosClient.delete(`/carts/items/${itemId}`);
  },

  /**
   * Get all items in user's cart
   */
  getCartItems: (userId) => {
    return axiosClient.get(`/carts/${userId}/items`);
  },

  /**
   * Clear entire cart
   */
  clearCart: (userId) => {
    return axiosClient.delete(`/carts/${userId}`);
  },

  // ============ ORDER OPERATIONS ============

  /**
   * Create order from cart (checkout)
   */
  checkout: (userId, checkoutData) => {
    return axiosClient.post('/orders/checkout', { 
      userId,
      cartItems: checkoutData?.cartItems || [],
      shippingFee: checkoutData?.shippingFee || 0,
      totalAmount: checkoutData?.totalAmount || 0,
      address: checkoutData?.address || ''
    });
  },

  /**
   * Get all orders for a user
   */
  getOrders: (customerId) => {
    return axiosClient.get(`/orders?customerId=${customerId}`);
  },

  /**
   * Get all orders (for admin dashboard)
   */
  getAllOrders: () => {
    return axiosClient.get('/orders');
  },

  /**
   * Get single order details
   */
  getOrder: (orderId) => {
    return axiosClient.get(`/orders/${orderId}`);
  },

  /**
   * Process payment for order
   */
  payOrder: (orderId) => {
    return axiosClient.post(`/orders/${orderId}/pay`, {});
  },

  /**
   * Cancel/delete order
   */
  cancelOrder: (orderId) => {
    return axiosClient.delete(`/orders/${orderId}`);
  },

  /**
   * Update order status
   */
  updateOrderStatus: (orderId, status) => {
    return axiosClient.put(`/orders/${orderId}/status`, { status });
  }
};

export default orderAPI;

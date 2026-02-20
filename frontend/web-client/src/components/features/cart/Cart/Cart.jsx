import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { message, Spin } from 'antd';
import logger from '../../../../utils/logger';
import Header from '../../../ui/Header/Header';
import Footer from '../../../ui/Footer/Footer';
import { useCart } from '../../../../context/CartContext';
import { useAuth } from '../../../../context/AuthContext';
import orderAPI from '../../../../api/order.api';
import './Cart.css';

const Cart = () => {
  const navigate = useNavigate();
  const { user } = useAuth();
  const { cartItems, updateQuantity, removeFromCart, getCartTotal } = useCart();
  const [loading, setLoading] = useState(false);

  const getShippingFee = () => {
    const subtotal = getCartTotal();
    return subtotal > 500000 ? 0 : 30000; // Miễn phí ship cho đơn > 500k
  };

  const getTotal = () => {
    return getCartTotal() + getShippingFee();
  };

  const handleCheckout = async () => {
    if (!user?.id) {
      message.error('Vui lòng đăng nhập');
      navigate('/login');
      return;
    }

    // Kiểm tra số lượng không vượt quá kho
    for (const item of cartItems) {
      const maxQuantity = item.availableQuantity || 999;
      if (item.quantity > maxQuantity) {
        message.warning(`"${item.name}" chỉ còn ${maxQuantity} sản phẩm. Vui lòng cập nhật giỏ hàng.`);
        return;
      }
    }

    try {
      setLoading(true);
      
      // Store checkout data in sessionStorage
      const checkoutData = {
        cartItems: cartItems,
        shippingFee: getShippingFee(),
        totalAmount: getTotal()
      };
      sessionStorage.setItem('checkoutData', JSON.stringify(checkoutData));
      
      // Navigate to payment
      setTimeout(() => {
        navigate('/payment');
      }, 1000);
    } catch (error) {
      console.error('Lỗi đặt hàng:', error);
      message.error(error.response?.data?.message || 'Không thể đặt hàng. Vui lòng thử lại.');
    } finally {
      setLoading(false);
    }
  };

  if (cartItems.length === 0) {
    return (
      <div className="cart-page">
        <Header />
        <main className="cart-container">
          <div className="empty-cart">
            <div className="empty-cart-icon">
              <span className="material-icons">shopping_cart</span>
            </div>
            <h2>Giỏ hàng của bạn đang trống</h2>
            <p>Hãy thêm sản phẩm vào giỏ hàng để tiếp tục mua sắm</p>
            <a href="/" className="continue-shopping-btn">
              Tiếp tục mua sắm
            </a>
          </div>
        </main>
        <Footer />
      </div>
    );
  }

  return (
    <div className="cart-page">
      <Header />

      <main className="cart-container">
        <h1 className="cart-title">Giỏ Hàng Của Bạn</h1>

        <Spin spinning={loading} tip="Đang xử lý đơn hàng...">
          <div className="cart-content">
            {/* Cart Items */}
            <div className="cart-items">
              {/* Desktop Header */}
              <div className="cart-header">
                <div className="header-product">Sản phẩm</div>
                <div className="header-quantity">Số lượng</div>
                <div className="header-total">Tổng giá</div>
              </div>

              {/* Cart Items List */}
              {cartItems.map(item => (
                <div key={item.id} className="cart-item">
                  <div className="item-product">
                    <div className="item-image">
                      <img src={item.imageUrl} alt={item.name} />
                    </div>
                    <div className="item-details">
                      <h3 className="item-name">{item.name}</h3>
                      <p className="item-variant">Size: {item.size} | Màu: {item.color}</p>
                      <p className="item-price">{item.price.toLocaleString()} ₫</p>
                      <button
                        className="remove-btn"
                        onClick={() => removeFromCart(item.id, item.size, item.color)}
                      >
                        <span className="material-icons">delete</span>
                        Xóa
                      </button>
                    </div>
                  </div>

                  <div className="item-quantity">
                    <div className="quantity-controls">
                      <button
                        className="quantity-btn"
                        onClick={() => updateQuantity(item.id, item.size, item.color, item.quantity - 1)}
                        disabled={item.quantity <= 1}
                      >
                        -
                      </button>
                      <span className="quantity-value">{item.quantity}</span>
                      <button
                        className="quantity-btn"
                        onClick={() => {
                          const maxQuantity = item.availableQuantity || 999;
                          if (item.quantity < maxQuantity) {
                            updateQuantity(item.id, item.size, item.color, item.quantity + 1);
                          } else {
                            message.warning(`Chỉ có ${maxQuantity} sản phẩm còn lại`);
                          }
                        }}
                        disabled={item.availableQuantity !== undefined && item.quantity >= item.availableQuantity}
                      >
                        +
                      </button>
                    </div>
                    {item.availableQuantity !== undefined && item.quantity > item.availableQuantity && (
                      <p className="quantity-warning">⚠️ Vượt quá số lượng kho ({item.availableQuantity})</p>
                    )}
                  </div>

                  <div className="item-total">
                    {(item.price * item.quantity).toLocaleString()} ₫
                  </div>
                </div>
              ))}
            </div>

            {/* Cart Summary */}
            <div className="cart-summary">
              <h2 className="summary-title">Tóm tắt đơn hàng</h2>

              <div className="summary-row">
                <span>Tạm tính:</span>
                <span>{getCartTotal().toLocaleString()} ₫</span>
              </div>

              <div className="summary-row">
                <span>Phí vận chuyển:</span>
                <span>
                  {getShippingFee() === 0 ? 'Miễn phí' : `${getShippingFee().toLocaleString()} ₫`}
                </span>
              </div>

              {getShippingFee() === 0 && (
                <p className="free-shipping-note">
                  🎉 Miễn phí vận chuyển cho đơn hàng từ 500.000 ₫
                </p>
              )}

              <div className="summary-divider"></div>

              <div className="summary-row total-row">
                <span>Tổng cộng:</span>
                <span className="total-amount">{getTotal().toLocaleString()} ₫</span>
              </div>

              <button 
                className="checkout-btn" 
                onClick={handleCheckout}
                disabled={loading}
              >
                {loading ? 'Đang xử lý...' : 'Tiến hành đặt hàng'}
              </button>

              <a href="/" className="continue-shopping-link">
                ← Tiếp tục mua sắm
              </a>
            </div>
          </div>
        </Spin>
      </main>

      <Footer />
    </div>
  );
};

export default Cart;
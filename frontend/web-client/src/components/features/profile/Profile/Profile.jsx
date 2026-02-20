import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { message } from 'antd';
import logger from '../../../../utils/logger';
import { useAuth } from '../../../../context/AuthContext';
import { updateUserApi } from '../../../../api/user.api';
import orderAPI from '../../../../api/order.api';
import { validateProfileForm } from '../../../../utils/validation';
import Header from '../../../ui/Header/Header';
import Footer from '../../../ui/Footer/Footer';
import Address from '../Address/Address';
import './Profile.css';

const Profile = () => {
  const { logout, user, login } = useAuth();
  const navigate = useNavigate();
  const [activeTab, setActiveTab] = useState('profile');
  const [formData, setFormData] = useState({
    fullName: '',
    email: '',
    phone: ''
  });
  const [originalData, setOriginalData] = useState({
    fullName: '',
    email: '',
    phone: ''
  });
  const [loading, setLoading] = useState(false);
  const [isEditing, setIsEditing] = useState(false);
  const [orders, setOrders] = useState([]);
  const [ordersLoading, setOrdersLoading] = useState(false);
  const [errors, setErrors] = useState({});

  // Load user data from context when component mounts
  useEffect(() => {
    if (user) {
      const userData = {
        fullName: user.fullName || '',
        email: user.email || '',
        phone: user.phone || ''
      };
      setFormData(userData);
      setOriginalData(userData);
    } else {
      // If no user data, redirect to login
      navigate('/login');
    }
  }, [user, navigate]);

  // Load orders when orders tab is active
  useEffect(() => {
    if (activeTab === 'orders' && user?.id) {
      loadOrders();
    }
  }, [activeTab, user?.id]);

  const loadOrders = async () => {
    try {
      setOrdersLoading(true);
      logger.log('📦 Loading orders for user:', user?.id);
      const response = await orderAPI.getOrders(user.id);
      logger.log('📦 Orders response:', response);
      
      let ordersData = [];
      if (response.data) {
        // Handle both direct array and wrapped response { code, data, message }
        if (Array.isArray(response.data)) {
          ordersData = response.data;
        } else if (response.data.data && Array.isArray(response.data.data)) {
          ordersData = response.data.data;
        } else if (response.data.code === 200) {
          ordersData = response.data.data || [];
        }
      }
      
      logger.log('📦 Processed orders:', ordersData);
      setOrders(Array.isArray(ordersData) ? ordersData : []);
    } catch (error) {
      logger.error('❌ Lỗi tải đơn hàng:', error);
      message.error('Không thể tải danh sách đơn hàng');
      setOrders([]);
    } finally {
      setOrdersLoading(false);
    }
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
    
    // Clear error when user starts typing
    if (errors[name]) {
      setErrors(prev => ({
        ...prev,
        [name]: ''
      }));
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    // Validate form
    const validation = validateProfileForm(formData);
    if (!validation.isValid) {
      setErrors(validation.errors);
      message.error('Vui lòng kiểm tra lại các trường có lỗi');
      return;
    }

    try {
      setLoading(true);

      // Call API to update user profile
      const response = await updateUserApi(user.id, {
        fullName: formData.fullName,
        email: formData.email,
        phone: formData.phone
      });

      // Update user in context/localStorage
      const updatedUser = {
        ...user,
        fullName: formData.fullName,
        email: formData.email,
        phone: formData.phone
      };
      
      // Update localStorage and context
      localStorage.setItem('user', JSON.stringify(updatedUser));
      login({ token: localStorage.getItem('token'), user: updatedUser });

      // Update original data
      setOriginalData(formData);
      setIsEditing(false);

      message.success('Thông tin đã được cập nhật thành công!');
    } catch (error) {
      logger.error('Failed to update profile:', error);
      message.error(error.response?.data?.message || 'Cập nhật thông tin thất bại');
    } finally {
      setLoading(false);
    }
  };

  const handleCancel = () => {
    setFormData(originalData);
    setErrors({});
    setIsEditing(false);
  };

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const menuItems = [
    { id: 'profile', label: 'Thông tin tài khoản', icon: 'person' },
    { id: 'orders', label: 'Đơn hàng của tôi', icon: 'receipt_long' },
    { id: 'addresses', label: 'Địa chỉ giao hàng', icon: 'location_on' }
  ];

  const renderContent = () => {
    switch (activeTab) {
      case 'profile':
        return (
          <div className="profile-content">
            <div className="content-header">
              <h2>Thông tin tài khoản</h2>
              <p>Quản lý thông tin cá nhân của bạn</p>
            </div>

            <form onSubmit={handleSubmit} className="profile-form">
              <div className="form-section">
                <div className="section-header">
                  <h3>Thông tin cơ bản</h3>
                  {!isEditing && (
                    <button 
                      type="button" 
                      className="edit-btn"
                      onClick={() => setIsEditing(true)}
                    >
                      ✏️ Chỉnh sửa
                    </button>
                  )}
                </div>

                <div className="form-group">
                  <label htmlFor="fullName">Họ tên</label>
                  <input
                    type="text"
                    id="fullName"
                    name="fullName"
                    value={formData.fullName}
                    onChange={handleInputChange}
                    disabled={!isEditing}
                    required
                    className={errors.fullName ? 'input-error' : ''}
                  />
                  {errors.fullName && (
                    <span className="error-message">{errors.fullName}</span>
                  )}
                </div>

                <div className="form-group">
                  <label htmlFor="email">Email</label>
                  <input
                    type="email"
                    id="email"
                    name="email"
                    value={formData.email}
                    onChange={handleInputChange}
                    disabled={!isEditing}
                    required
                    className={errors.email ? 'input-error' : ''}
                  />
                  {errors.email && (
                    <span className="error-message">{errors.email}</span>
                  )}
                </div>

                <div className="form-group">
                  <label htmlFor="phone">Số điện thoại</label>
                  <input
                    type="tel"
                    id="phone"
                    name="phone"
                    value={formData.phone}
                    onChange={handleInputChange}
                    disabled={!isEditing}
                    required
                    className={errors.phone ? 'input-error' : ''}
                  />
                  {errors.phone && (
                    <span className="error-message">{errors.phone}</span>
                  )}
                </div>
              </div>

              {isEditing && (
                <div className="form-actions">
                  <button type="submit" className="save-btn" disabled={loading}>
                    {loading ? 'Đang lưu...' : '💾 Lưu thay đổi'}
                  </button>
                  <button type="button" className="cancel-btn" onClick={handleCancel} disabled={loading}>
                    ✕ Hủy
                  </button>
                </div>
              )}
            </form>
          </div>
        );

      case 'orders':
        return (
          <div className="orders-content">
            <div className="content-header">
              <h2>Đơn hàng của tôi</h2>
              <p>Lịch sử mua hàng và theo dõi đơn hàng</p>
            </div>

            <div className="orders-list">
              {ordersLoading ? (
                <div style={{ textAlign: 'center', padding: '40px 20px', color: '#999' }}>
                  <p>Đang tải đơn hàng...</p>
                </div>
              ) : orders.length === 0 ? (
                <div style={{ textAlign: 'center', padding: '40px 20px', color: '#999' }}>
                  <p>Bạn chưa có đơn hàng nào</p>
                </div>
              ) : (
                <table className="orders-table">
                  <thead>
                    <tr>
                      <th>Mã đơn hàng</th>
                      <th>Ngày tạo</th>
                      <th>Sản phẩm</th>
                      <th>Tổng tiền</th>
                      <th>Phí vận chuyển</th>
                      <th>Trạng thái</th>
                      <th>Địa chỉ giao</th>
                    </tr>
                  </thead>
                  <tbody>
                    {orders && orders.length > 0 && orders.map((order, index) => {
                      let itemsText = '-';
                      try {
                        if (order.itemDetails) {
                          const items = typeof order.itemDetails === 'string' 
                            ? JSON.parse(order.itemDetails) 
                            : order.itemDetails;
                          if (Array.isArray(items) && items.length > 0) {
                            itemsText = items.map(item => `${item.name} (x${item.quantity})`).join(', ');
                          }
                        }
                      } catch (e) {
                        console.warn('Error parsing items:', e);
                      }
                      
                      return (
                        <tr key={order.id || index}>
                          <td>#{order.id}</td>
                          <td>{order.createdAt ? new Date(order.createdAt).toLocaleDateString('vi-VN') : '-'}</td>
                          <td title={itemsText}>{itemsText.substring(0, 50)}{itemsText.length > 50 ? '...' : ''}</td>
                          <td>{Number(order.total).toLocaleString('vi-VN')} ₫</td>
                          <td>{order.shippingFee ? Number(order.shippingFee).toLocaleString('vi-VN') + ' ₫' : 'Miễn phí'}</td>
                          <td>
                            <span className={`status-badge status-${order.status?.toLowerCase() || 'pending'}`}>
                              {order.status || 'PENDING'}
                            </span>
                          </td>
                          <td>{order.address || 'Chưa xác định'}</td>
                        </tr>
                      );
                    })}
                  </tbody>
                </table>
              )}
            </div>
          </div>
        );

      case 'addresses':
        return <Address />;

      default:
        return null;
    }
  };

  return (
    <div className="profile-page">
      <Header />

      <main className="profile-container">
        <div className="profile-layout">
          {/* Sidebar */}
          <aside className="profile-sidebar">
            <div className="user-info">
              <div className="user-avatar">
                <span>{formData.fullName.charAt(0)}</span>
              </div>
              <div className="user-details">
                <h3>Tài khoản của</h3>
                <p>{formData.fullName}</p>
              </div>
            </div>

            <nav className="profile-nav">
              {menuItems.map(item => (
                <button
                  key={item.id}
                  className={`nav-item ${activeTab === item.id ? 'active' : ''}`}
                  onClick={() => setActiveTab(item.id)}
                >
                  <span className="material-icons">{item.icon}</span>
                  <span>{item.label}</span>
                </button>
              ))}

              <div className="nav-divider"></div>

              <button className="nav-item logout" onClick={handleLogout}>
                <span className="material-icons">logout</span>
                <span>Đăng xuất</span>
              </button>
            </nav>
          </aside>

          {/* Main Content */}
          <div className="profile-main">
            {renderContent()}
          </div>
        </div>
      </main>

      <Footer />
    </div>
  );
};

export default Profile;
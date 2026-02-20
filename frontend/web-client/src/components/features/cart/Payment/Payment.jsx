import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { message } from 'antd';
import logger from '../../../../utils/logger';
import { useCart } from '../../../../context/CartContext';
import { useAuth } from '../../../../context/AuthContext';
import { getUserAddressesApi, createAddressApi } from '../../../../api/address.api';
import { purchaseProductApi, getProductByIdApi } from '../../../../api/product.api';
import orderAPI from '../../../../api/order.api';
import './Payment.css';

const Payment = () => {
  const navigate = useNavigate();
  const { cartItems, clearCart } = useCart();
  const { user } = useAuth();
  
  const [step, setStep] = useState(1);
  const [paymentMethod, setPaymentMethod] = useState('card');
  const [userAddresses, setUserAddresses] = useState([]);
  const [selectedAddressId, setSelectedAddressId] = useState(null);
  const [showAddNewAddress, setShowAddNewAddress] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  
  const [formData, setFormData] = useState({
    // Shipping Info
    fullName: '',
    email: '',
    phone: '',
    address: '',
    ward: '',
    district: '',
    city: '',
    postalCode: '',
    recipientName: '',
    notes: '',
    
    // Payment Info
    cardName: '',
    cardNumber: '',
    expiryDate: '',
    cvv: '',
  });

  // Load user info và addresses khi component mount
  useEffect(() => {
    if (user) {
      setFormData(prev => ({
        ...prev,
        fullName: user.fullName || '',
        email: user.email || '',
        phone: user.phone || '',
        recipientName: user.fullName || '',
      }));
      
      // Load user addresses
      loadUserAddresses(user.id);
    }
  }, [user]);

  const loadUserAddresses = async (userId) => {
    try {
      setLoading(true);
      const response = await getUserAddressesApi(userId);
      setUserAddresses(response.data || []);
      
      // Auto select first address if available
      if (response.data && response.data.length > 0) {
        const defaultAddress = response.data.find(addr => addr.isDefault) || response.data[0];
        setSelectedAddressId(defaultAddress.id);
        fillFormWithAddress(defaultAddress);
      }
    } catch (err) {
      logger.error('Error loading addresses:', err);
      setError('Không thể tải danh sách địa chỉ');
    } finally {
      setLoading(false);
    }
  };

  const fillFormWithAddress = (address) => {
    if (address) {
      setFormData(prev => ({
        ...prev,
        address: address.addressLine || '',
        ward: address.ward || '',
        district: address.district || '',
        city: address.province || '',
        postalCode: address.postalCode || '',
        phone: address.phone || prev.phone,
        recipientName: address.recipientName || prev.recipientName,
        notes: address.notes || '',
      }));
    }
  };

  const subtotal = cartItems.reduce((sum, item) => sum + (item.price * item.quantity), 0);
  const shipping = 30000;
  const discount = 0;
  const total = subtotal + shipping - discount;

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleCardNumberChange = (e) => {
    let value = e.target.value.replace(/\s+/g, '');
    if (value.length > 16) value = value.slice(0, 16);
    const formatted = value.replace(/(\d{4})(?=\d)/g, '$1 ');
    setFormData(prev => ({
      ...prev,
      cardNumber: formatted
    }));
  };

  const handleExpiryChange = (e) => {
    let value = e.target.value.replace(/\D/g, '');
    if (value.length >= 2) {
      value = value.slice(0, 2) + '/' + value.slice(2, 4);
    }
    setFormData(prev => ({
      ...prev,
      expiryDate: value
    }));
  };

  const handleCVVChange = (e) => {
    let value = e.target.value.replace(/\D/g, '');
    if (value.length > 3) value = value.slice(0, 3);
    setFormData(prev => ({
      ...prev,
      cvv: value
    }));
  };

  const validateAddressFields = () => {
    const { address, ward, district, city, postalCode } = formData;
    const errors = [];

    // Validate addressLine (address)
    if (!address.trim() || address.trim().length < 5 || address.trim().length > 255) {
      errors.push('Địa chỉ chi tiết phải từ 5-255 ký tự');
    }

    // Validate ward
    if (!ward.trim() || ward.trim().length < 2 || ward.trim().length > 100) {
      errors.push('Phường/Xã phải từ 2-100 ký tự');
    }

    // Validate district
    if (!district.trim() || district.trim().length < 2 || district.trim().length > 100) {
      errors.push('Quận/Huyện phải từ 2-100 ký tự');
    }

    // Validate province (city)
    if (!city.trim() || city.trim().length < 2 || city.trim().length > 100) {
      errors.push('Tỉnh/Thành phố phải từ 2-100 ký tự');
    }

    // Validate postalCode - phải là 5-6 chữ số
    if (postalCode.trim()) {
      const postalCodeDigits = postalCode.trim().replace(/\D/g, '');
      if (postalCodeDigits.length < 5 || postalCodeDigits.length > 6) {
        errors.push('Mã bưu chính phải có 5-6 chữ số');
      }
      // Check if contains only digits
      if (!/^\d{5,6}$/.test(postalCodeDigits)) {
        errors.push('Mã bưu chính chỉ được chứa chữ số');
      }
    }

    if (errors.length > 0) {
      message.error(errors.join('. '));
      return false;
    }

    return true;
  };

  const validateStep1 = () => {
    const { fullName, email, phone, address, district, city, recipientName } = formData;
    if (!fullName.trim() || !email.trim() || !phone.trim() || !address.trim() || !district.trim() || !city.trim() || !recipientName.trim()) {
      message.error('Vui lòng điền đầy đủ thông tin giao hàng');
      return false;
    }
    
    // Validate address field format when saving new address
    if (showAddNewAddress && !validateAddressFields()) {
      return false;
    }
    
    return true;
  };

  const validateStep2 = () => {
    const { cardName, cardNumber, expiryDate, cvv } = formData;
    if (!cardName.trim() || !cardNumber.trim() || !expiryDate.trim() || !cvv.trim()) {
      message.error('Vui lòng điền đầy đủ thông tin thẻ');
      return false;
    }
    if (cardNumber.replace(/\s/g, '').length !== 16) {
      message.error('Số thẻ phải có 16 chữ số');
      return false;
    }
    return true;
  };

  const handleNextStep = () => {
    if (step === 1 && !validateStep1()) return;
    setStep(step + 1);
  };

  const handlePreviousStep = () => {
    if (step > 1) setStep(step - 1);
  };

  const handleSubmit = async () => {
    if (step === 2) {
      if (paymentMethod === 'card' && !validateStep2()) return;
      
      // Check if cart is not empty
      if (cartItems.length === 0) {
        message.error('Giỏ hàng của bạn trống!');
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
      
      setStep(3);
    }
  };

  const handleConfirmOrder = async () => {
    try {
      setLoading(true);
      
      logger.log('=== Starting Order Confirmation ===');
      logger.log('Cart Items:', JSON.stringify(cartItems, null, 2));
      
      // Bước 1: Kiểm tra lại số lượng từ database trước khi purchase
      logger.log('📋 Step 1: Verifying stock from database...');
      for (const item of cartItems) {
        if (!item.id) {
          console.warn('⚠️ Item missing ID:', item);
          continue;
        }
        
        try {
          const productId = parseInt(item.id) || item.id;
          const quantity = parseInt(item.quantity) || 1;
          
          logger.log(`🔍 Checking product ${productId} in database...`);
          const productData = await getProductByIdApi(productId);
          const dbQuantum = productData.data?.quantum || 0;
          
          logger.log(`   DB Quantity: ${dbQuantum}, Requested: ${quantity}`);
          
          if (dbQuantum < quantity) {
            // Không đủ số lượng
            const productName = item.name || productData.data?.name || `sản phẩm ${productId}`;
            const errorMsg = `❌ "${productName}" chỉ còn ${dbQuantum} sản phẩm trong kho. Không thể mua ${quantity} sản phẩm.`;
            logger.error(errorMsg);
            message.error(`"${productName}" chỉ còn ${dbQuantum} sản phẩm trong kho. Không thể mua ${quantity} sản phẩm.`);
            setError(errorMsg);
            return; // Dừng lại, không tiếp tục
          }
          
          logger.log(`✅ Product ${productId}: OK (${quantity} <= ${dbQuantum})`);
        } catch (checkErr) {
          logger.error(`❌ Error checking quantity for product ${item.id}:`, {
            status: checkErr.response?.status,
            data: checkErr.response?.data,
            message: checkErr.message
          });
          message.error(`Không thể kiểm tra số lượng sản phẩm "${item.name}". Vui lòng thử lại.`);
          setError(`Error checking stock for ${item.name}`);
          return;
        }
      }
      
      // Bước 2: Giảm số lượng sản phẩm trong kho (purchase)
      logger.log('📦 Step 2: Processing purchases...');
      if (cartItems && cartItems.length > 0) {
        for (const item of cartItems) {
          if (!item.id) {
            logger.warn('⚠️ Item missing ID:', item);
            continue;
          }
          
          try {
            const productId = parseInt(item.id) || item.id;
            const quantity = parseInt(item.quantity) || 1;
            
            logger.log(`📦 Calling API to purchase: ${quantity} of product ID=${productId}`);
            logger.log(`   API URL: /api/products/purchase/${productId}?quantity=${quantity}`);
            
            const result = await purchaseProductApi(productId, quantity);
            logger.log(`✅ Successfully purchased ${quantity} of product ${productId}:`, result.data);
          } catch (purchaseErr) {
            logger.error(`❌ Error reducing quantity for product ${item.id}:`, {
              status: purchaseErr.response?.status,
              statusText: purchaseErr.response?.statusText,
              data: purchaseErr.response?.data,
              message: purchaseErr.message
            });
            // Vẫn tiếp tục nếu một sản phẩm lỗi
          }
        }
      } else {
        logger.warn('⚠️ No cart items to process');
      }
      
      // Bước 3: Nếu là địa chỉ mới, lưu vào DB
      if (showAddNewAddress && user) {
        // Validate address fields before sending
        if (!validateAddressFields()) {
          message.error('Vui lòng kiểm tra lại thông tin địa chỉ trước khi lưu');
          setError('Address validation failed');
          return;
        }
        
        const newAddressData = {
          recipientName: formData.recipientName,
          phone: formData.phone,
          addressLine: formData.address.trim(),
          ward: formData.ward.trim(),
          district: formData.district.trim(),
          province: formData.city.trim(),
          postalCode: formData.postalCode.trim(),
          notes: formData.notes.trim(),
          isDefault: userAddresses.length === 0, // Nếu chưa có địa chỉ thì mặc định
        };
        
        logger.log('📍 Step 3: Saving new address...');
        logger.log('Address data to save:', newAddressData);
        
        try {
          const response = await createAddressApi(user.id, newAddressData);
          logger.log('✅ Address saved successfully:', response.data);
        } catch (addressErr) {
          logger.error('❌ Error saving address:', {
            status: addressErr.response?.status,
            data: addressErr.response?.data,
            message: addressErr.message
          });
          
          // Extract error messages from backend
          if (addressErr.response?.data?.errors) {
            const errorMessages = Object.values(addressErr.response.data.errors);
            message.error(`Lỗi lưu địa chỉ: ${errorMessages.join(', ')}`);
          } else {
            message.error('Không thể lưu địa chỉ. Vui lòng kiểm tra lại thông tin.');
          }
          setError('Address save failed');
          return;
        }
      }
      
      // Bước 4: Gọi Order Service để tạo đơn hàng (checkout)
      if (user) {
        logger.log('🛒 Step 4: Creating order via Order Service...');
        try {
          // Get checkout data from sessionStorage
          const checkoutDataStr = sessionStorage.getItem('checkoutData');
          const checkoutData = checkoutDataStr ? JSON.parse(checkoutDataStr) : {
            cartItems: cartItems,
            shippingFee: 0,
            totalAmount: 0
          };
          
          // Build full address
          const fullAddress = `${formData.address}, ${formData.ward}, ${formData.district}, ${formData.city}`;
          
          // Add address to checkout data
          checkoutData.address = fullAddress;
          
          const checkoutResponse = await orderAPI.checkout(user.id, checkoutData);
          console.log('✅ Order created successfully:', checkoutResponse.data);
        } catch (checkoutErr) {
          console.error('❌ Error creating order:', {
            status: checkoutErr.response?.status,
            data: checkoutErr.response?.data,
            message: checkoutErr.message
          });
          
          // Vẫn tiếp tục vì sản phẩm đã được purchase, chỉ cảnh báo lỗi tạo order
          message.warning('Sản phẩm đã được mua nhưng có lỗi tạo đơn hàng. Vui lòng kiểm tra lại.');
        }
      }
      
      console.log('✅ Order placement completed successfully!');
      message.success('Đơn hàng đã được đặt thành công!');
      setError(''); // Clear any previous errors
      clearCart();
      setTimeout(() => {
        navigate('/profile');
      }, 1500);
    } catch (err) {
      console.error('❌ Error placing order:', err);
      const errorMsg = err.message || 'Có lỗi khi đặt hàng. Vui lòng thử lại!';
      setError(errorMsg);
      message.error(errorMsg);
    } finally {
      setLoading(false);
    }
  };

  const formatCurrency = (value) => {
    return new Intl.NumberFormat('vi-VN', {
      style: 'currency',
      currency: 'VND',
      maximumFractionDigits: 0,
    }).format(value);
  };

  return (
    <div className="payment-container">
      <div className="payment-wrapper">
        {/* Progress Bar */}
        <div className="progress-bar">
          <div className={`progress-step ${step >= 1 ? 'active' : ''}`}>
            <div className="step-number">1</div>
            <span>Giao Hàng</span>
          </div>
          <div className={`progress-line ${step >= 2 ? 'active' : ''}`}></div>
          <div className={`progress-step ${step >= 2 ? 'active' : ''}`}>
            <div className="step-number">2</div>
            <span>Thanh Toán</span>
          </div>
          <div className={`progress-line ${step >= 3 ? 'active' : ''}`}></div>
          <div className={`progress-step ${step >= 3 ? 'active' : ''}`}>
            <div className="step-number">3</div>
            <span>Xác Nhận</span>
          </div>
        </div>

        <div className="payment-content">
          {/* Left - Form */}
          <div className="payment-form">
            {/* Step 1: Shipping */}
            {step === 1 && (
              <div className="form-section">
                <h2>Thông Tin Giao Hàng</h2>

                {error && <div className="error-message">{error}</div>}
                {loading && <div className="loading-message">Đang tải...</div>}

                {/* Select existing address */}
                {!showAddNewAddress && userAddresses.length > 0 && (
                  <div className="address-selection">
                    <h3>Chọn Địa Chỉ Có Sẵn</h3>
                    <div className="address-list">
                      {userAddresses.map((addr) => (
                        <label key={addr.id} className="address-option">
                          <input
                            type="radio"
                            name="addressSelection"
                            value={addr.id}
                            checked={selectedAddressId === addr.id}
                            onChange={() => {
                              setSelectedAddressId(addr.id);
                              fillFormWithAddress(addr);
                            }}
                          />
                          <div className="address-info">
                            <p className="recipient-name">{addr.recipientName} ({addr.phone})</p>
                            <p className="address-text">{addr.addressLine}, {addr.ward}, {addr.district}, {addr.province}</p>
                            {addr.isDefault && <span className="badge-default">Mặc định</span>}
                          </div>
                        </label>
                      ))}
                    </div>
                    <button 
                      type="button"
                      className="btn-add-new"
                      onClick={() => {
                        setShowAddNewAddress(true);
                        setFormData(prev => ({
                          ...prev,
                          address: '',
                          ward: '',
                          district: '',
                          city: '',
                          postalCode: '',
                          phone: user?.phone || '',
                          recipientName: user?.fullName || '',
                          notes: '',
                        }));
                      }}
                    >
                      + Thêm Địa Chỉ Mới
                    </button>
                  </div>
                )}

                {/* Add new address form */}
                {showAddNewAddress && (
                  <div className="new-address-form">
                    <h3>Thêm Địa Chỉ Mới</h3>
                    
                    <div className="form-group">
                      <label>Tên Người Nhận *</label>
                      <input
                        type="text"
                        name="recipientName"
                        value={formData.recipientName}
                        onChange={handleInputChange}
                        placeholder="Nhập tên người nhận"
                      />
                    </div>

                    <div className="form-row">
                      <div className="form-group">
                        <label>Email</label>
                        <input
                          type="email"
                          name="email"
                          value={formData.email}
                          onChange={handleInputChange}
                          placeholder="example@email.com"
                        />
                      </div>
                      <div className="form-group">
                        <label>Số Điện Thoại *</label>
                        <input
                          type="tel"
                          name="phone"
                          value={formData.phone}
                          onChange={handleInputChange}
                          placeholder="0912 345 678"
                        />
                      </div>
                    </div>

                    <div className="form-group">
                      <label>Địa Chỉ Chi Tiết *</label>
                      <input
                        type="text"
                        name="address"
                        value={formData.address}
                        onChange={handleInputChange}
                        placeholder="Nhập địa chỉ cụ thể (Số nhà, đường phố)"
                      />
                    </div>

                    <div className="form-row">
                      <div className="form-group">
                        <label>Phường/Xã *</label>
                        <input
                          type="text"
                          name="ward"
                          value={formData.ward}
                          onChange={handleInputChange}
                          placeholder="Nhập phường/xã"
                        />
                      </div>
                      <div className="form-group">
                        <label>Quận/Huyện *</label>
                        <input
                          type="text"
                          name="district"
                          value={formData.district}
                          onChange={handleInputChange}
                          placeholder="Nhập quận/huyện"
                        />
                      </div>
                    </div>

                    <div className="form-row">
                      <div className="form-group">
                        <label>Tỉnh/Thành Phố *</label>
                        <input
                          type="text"
                          name="city"
                          value={formData.city}
                          onChange={handleInputChange}
                          placeholder="Nhập tỉnh/thành phố"
                        />
                      </div>
                      <div className="form-group">
                        <label>Mã Bưu Chính</label>
                        <input
                          type="text"
                          name="postalCode"
                          value={formData.postalCode}
                          onChange={handleInputChange}
                          placeholder="Mã bưu chính"
                        />
                      </div>
                    </div>

                    <div className="form-group">
                      <label>Ghi Chú</label>
                      <textarea
                        name="notes"
                        value={formData.notes}
                        onChange={handleInputChange}
                        placeholder="VD: Cách nhà trước tòa nhà màu xanh"
                        rows="3"
                      />
                    </div>

                    {userAddresses.length > 0 && (
                      <button 
                        type="button"
                        className="btn-cancel-new"
                        onClick={() => {
                          setShowAddNewAddress(false);
                          if (userAddresses.length > 0) {
                            const defaultAddress = userAddresses.find(addr => addr.isDefault) || userAddresses[0];
                            setSelectedAddressId(defaultAddress.id);
                            fillFormWithAddress(defaultAddress);
                          }
                        }}
                      >
                        ← Quay Lại Danh Sách Địa Chỉ
                      </button>
                    )}
                  </div>
                )}

                {/* Form for when no addresses exist */}
                {!showAddNewAddress && userAddresses.length === 0 && (
                  <div className="no-address-form">
                    <p>Bạn chưa có địa chỉ nào. Vui lòng thêm một địa chỉ mới.</p>
                    
                    <div className="form-group">
                      <label>Tên Người Nhận *</label>
                      <input
                        type="text"
                        name="recipientName"
                        value={formData.recipientName}
                        onChange={handleInputChange}
                        placeholder="Nhập tên người nhận"
                      />
                    </div>

                    <div className="form-row">
                      <div className="form-group">
                        <label>Email</label>
                        <input
                          type="email"
                          name="email"
                          value={formData.email}
                          onChange={handleInputChange}
                          placeholder="example@email.com"
                        />
                      </div>
                      <div className="form-group">
                        <label>Số Điện Thoại *</label>
                        <input
                          type="tel"
                          name="phone"
                          value={formData.phone}
                          onChange={handleInputChange}
                          placeholder="0912 345 678"
                        />
                      </div>
                    </div>

                    <div className="form-group">
                      <label>Địa Chỉ Chi Tiết *</label>
                      <input
                        type="text"
                        name="address"
                        value={formData.address}
                        onChange={handleInputChange}
                        placeholder="Nhập địa chỉ cụ thể"
                      />
                    </div>

                    <div className="form-row">
                      <div className="form-group">
                        <label>Phường/Xã *</label>
                        <input
                          type="text"
                          name="ward"
                          value={formData.ward}
                          onChange={handleInputChange}
                          placeholder="Nhập phường/xã"
                        />
                      </div>
                      <div className="form-group">
                        <label>Quận/Huyện *</label>
                        <input
                          type="text"
                          name="district"
                          value={formData.district}
                          onChange={handleInputChange}
                          placeholder="Nhập quận/huyện"
                        />
                      </div>
                    </div>

                    <div className="form-row">
                      <div className="form-group">
                        <label>Tỉnh/Thành Phố *</label>
                        <input
                          type="text"
                          name="city"
                          value={formData.city}
                          onChange={handleInputChange}
                          placeholder="Nhập tỉnh/thành phố"
                        />
                      </div>
                      <div className="form-group">
                        <label>Mã Bưu Chính</label>
                        <input
                          type="text"
                          name="postalCode"
                          value={formData.postalCode}
                          onChange={handleInputChange}
                          placeholder="Mã bưu chính"
                        />
                      </div>
                    </div>

                    <div className="form-group">
                      <label>Ghi Chú</label>
                      <textarea
                        name="notes"
                        value={formData.notes}
                        onChange={handleInputChange}
                        placeholder="VD: Cách nhà trước tòa nhà màu xanh"
                        rows="3"
                      />
                    </div>
                  </div>
                )}
              </div>
            )}

            {/* Step 2: Payment */}
            {step === 2 && (
              <div className="form-section">
                <h2>Phương Thức Thanh Toán</h2>

                <div className="payment-methods">
                  <label className="payment-method-option">
                    <input
                      type="radio"
                      name="paymentMethod"
                      value="card"
                      checked={paymentMethod === 'card'}
                      onChange={(e) => setPaymentMethod(e.target.value)}
                    />
                    <span className="option-label">💳 Thẻ Tín Dụng / Ghi Nợ</span>
                  </label>
                  <label className="payment-method-option">
                    <input
                      type="radio"
                      name="paymentMethod"
                      value="bank"
                      checked={paymentMethod === 'bank'}
                      onChange={(e) => setPaymentMethod(e.target.value)}
                    />
                    <span className="option-label">🏦 Chuyển Khoản Ngân Hàng</span>
                  </label>
                  <label className="payment-method-option">
                    <input
                      type="radio"
                      name="paymentMethod"
                      value="wallet"
                      checked={paymentMethod === 'wallet'}
                      onChange={(e) => setPaymentMethod(e.target.value)}
                    />
                    <span className="option-label">📱 Ví Điện Tử</span>
                  </label>
                </div>

                {paymentMethod === 'card' && (
                  <div className="card-form">
                    <div className="form-group">
                      <label>Tên Chủ Thẻ</label>
                      <input
                        type="text"
                        name="cardName"
                        value={formData.cardName}
                        onChange={handleInputChange}
                        placeholder="Nhập tên trên thẻ"
                      />
                    </div>

                    <div className="form-group">
                      <label>Số Thẻ</label>
                      <input
                        type="text"
                        name="cardNumber"
                        value={formData.cardNumber}
                        onChange={handleCardNumberChange}
                        placeholder="1234 5678 9012 3456"
                        maxLength="19"
                      />
                    </div>

                    <div className="form-row">
                      <div className="form-group">
                        <label>Hạn Sử Dụng</label>
                        <input
                          type="text"
                          name="expiryDate"
                          value={formData.expiryDate}
                          onChange={handleExpiryChange}
                          placeholder="MM/YY"
                          maxLength="5"
                        />
                      </div>
                      <div className="form-group">
                        <label>CVV</label>
                        <input
                          type="text"
                          name="cvv"
                          value={formData.cvv}
                          onChange={handleCVVChange}
                          placeholder="123"
                          maxLength="3"
                        />
                      </div>
                    </div>

                    <div className="security-info">
                      🔒 Thông tin thẻ của bạn được mã hóa và bảo mật
                    </div>
                  </div>
                )}

                {paymentMethod === 'bank' && (
                  <div className="bank-info">
                    <p>Vui lòng chuyển khoản đến tài khoản dưới đây:</p>
                    <div className="bank-details">
                      <div><strong>Ngân hàng:</strong> ACB (Ngân hàng Á Châu)</div>
                      <div><strong>Số tài khoản:</strong> 123456789</div>
                      <div><strong>Tên chủ tài khoản:</strong> CÔNG TY CỔ PHẦN THƯƠNG MỤC</div>
                    </div>
                  </div>
                )}

                {paymentMethod === 'wallet' && (
                  <div className="wallet-info">
                    <p>Chọn ví điện tử để thanh toán</p>
                    <div className="wallet-options">
                      <button className="wallet-btn">Momo</button>
                      <button className="wallet-btn">ZaloPay</button>
                      <button className="wallet-btn">ViettelPay</button>
                    </div>
                  </div>
                )}
              </div>
            )}

            {/* Step 3: Confirmation */}
            {step === 3 && (
              <div className="form-section">
                <h2>Xác Nhận Đơn Hàng</h2>
                
                <div className="confirmation-section">
                  <div className="confirmation-block">
                    <h3>Địa Chỉ Giao Hàng</h3>
                    <p><strong>{formData.recipientName}</strong></p>
                    <p>{formData.address}, {formData.ward}, {formData.district}, {formData.city}</p>
                    <p>ĐT: {formData.phone}</p>
                    {formData.notes && <p>Ghi chú: {formData.notes}</p>}
                  </div>

                  <div className="confirmation-block">
                    <h3>Phương Thức Thanh Toán</h3>
                    <p>
                      {paymentMethod === 'card' && '💳 Thẻ Tín Dụng / Ghi Nợ'}
                      {paymentMethod === 'bank' && '🏦 Chuyển Khoản Ngân Hàng'}
                      {paymentMethod === 'wallet' && '📱 Ví Điện Tử'}
                    </p>
                  </div>
                </div>
              </div>
            )}

            {/* Buttons */}
            <div className="form-actions">
              {step > 1 && (
                <button className="btn btn-secondary" onClick={handlePreviousStep}>
                  ← Quay Lại
                </button>
              )}
              {step < 3 && (
                <button className="btn btn-primary" onClick={handleNextStep} disabled={loading}>
                  Tiếp Tục →
                </button>
              )}
              {step === 3 && (
                <button className="btn btn-success" onClick={handleConfirmOrder} disabled={loading}>
                  {loading ? 'Đang xử lý...' : '✓ Đặt Hàng Ngay'}
                </button>
              )}
            </div>
          </div>

          {/* Right - Order Summary */}
          <div className="order-summary">
            <h3>Tóm Tắt Đơn Hàng</h3>

            {cartItems && cartItems.length > 0 ? (
              <>
                <div className="cart-items">
                  {cartItems.map((item) => (
                    <div key={`${item.id}-${item.size}-${item.color}`} className="cart-item">
                      <div className="item-image">
                        {item.imageUrl ? <img src={item.imageUrl} alt={item.name} /> : '📦'}
                      </div>
                      <div className="item-details">
                        <p className="item-name">{item.name}</p>
                        {item.size && <p className="item-size">Size: {item.size}</p>}
                        {item.color && <p className="item-color">Màu: {item.color}</p>}
                        <p className="item-qty">x{item.quantity}</p>
                      </div>
                      <div className="item-price">
                        {formatCurrency(item.price * item.quantity)}
                      </div>
                    </div>
                  ))}
                </div>

                <div className="summary-divider"></div>

                <div className="summary-row">
                  <span>Tạm Tính:</span>
                  <span>{formatCurrency(subtotal)}</span>
                </div>
                <div className="summary-row">
                  <span>Phí Vận Chuyển:</span>
                  <span>{formatCurrency(shipping)}</span>
                </div>
                {discount > 0 && (
                  <div className="summary-row discount">
                    <span>Giảm Giá:</span>
                    <span>-{formatCurrency(discount)}</span>
                  </div>
                )}

                <div className="summary-divider"></div>

                <div className="summary-total">
                  <span>Tổng Tiền:</span>
                  <span>{formatCurrency(total)}</span>
                </div>

                <div className="payment-info">
                  <p>✓ Miễn phí vận chuyển cho đơn hàng trên 500.000đ</p>
                  <p>✓ Hỗ trợ 24/7</p>
                  <p>✓ Hoàn tiền 100% nếu không hài lòng</p>
                </div>
              </>
            ) : (
              <div className="empty-cart">
                <p>🛒 Giỏ hàng của bạn trống</p>
                <p>Vui lòng quay lại cửa hàng để thêm sản phẩm</p>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default Payment;

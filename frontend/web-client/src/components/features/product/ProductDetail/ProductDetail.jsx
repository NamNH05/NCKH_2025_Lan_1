import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { message, Spin } from 'antd';
import logger from '../../../../utils/logger';
import Header from '../../../ui/Header/Header';
import Footer from '../../../ui/Footer/Footer';
import { useCart } from '../../../../context/CartContext';
import { getProductByIdApi } from '../../../../api/product.api';
import './ProductDetail.css';

const ProductDetail = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { addToCart } = useCart();
  const [product, setProduct] = useState(null);
  const [loading, setLoading] = useState(true);
  const [selectedImage, setSelectedImage] = useState(0);
  const [quantity, setQuantity] = useState(1);
  const [activeTab, setActiveTab] = useState('description');

  // Load product data from API
  useEffect(() => {
    loadProductDetail();
  }, [id]);

  const loadProductDetail = async () => {
    try {
      setLoading(true);
      const response = await getProductByIdApi(id);
      setProduct(response.data);
      
      // Set default selections
      if (response.data.images && response.data.images.length > 0) {
        setSelectedImage(0);
      }
    } catch (error) {
      logger.error('Failed to load product:', error);
      message.error('Không thể tải chi tiết sản phẩm');
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="product-detail-page">
        <Header />
        <div className="product-detail-container">
          <Spin size="large" style={{ margin: '100px auto' }} />
        </div>
        <Footer />
      </div>
    );
  }

  if (!product) {
    return (
      <div className="product-detail-page">
        <Header />
        <div className="product-detail-container">
          <p>Sản phẩm không tồn tại hoặc đã hết hàng</p>
        </div>
        <Footer />
      </div>
    );
  }

  // Check if product is out of stock
  const isOutOfStock = product.quantum !== undefined && product.quantum <= 0;
  const availableQuantity = product.quantum || 0;

  const handleQuantityChange = (change) => {
    const newQuantity = quantity + change;
    // Đảm bảo không vượt quá số lượng trong kho
    if (newQuantity >= 1 && newQuantity <= availableQuantity) {
      setQuantity(newQuantity);
    }
  };

  const handleAddToCart = () => {
    // Kiểm tra số lượng không vượt quá kho
    if (quantity > availableQuantity) {
      message.warning(`Chỉ có ${availableQuantity} sản phẩm còn lại. Vui lòng giảm số lượng.`);
      return;
    }
    
    // Get image - handle both single URL and array
    const imageUrl = Array.isArray(product?.images) 
      ? product.images[0] 
      : product?.imageUrl || product?.image;
    
    const cartItem = {
      id: product.id,
      name: product.name,
      price: product.price,
      imageUrl: imageUrl,
      quantity: quantity,
      availableQuantity: availableQuantity  // Lưu số lượng trong kho
    };

    addToCart(cartItem);
    message.success('Đã thêm sản phẩm vào giỏ hàng!');
  };

  const handleBuyNow = () => {
    // Thêm vào giỏ hàng trước
    handleAddToCart();
    // Chuyển hướng tới trang giỏ hàng
    setTimeout(() => navigate('/cart'), 500);
  };

  return (
    <div className="product-detail-page">
      <Header />

      <main className="product-detail-container">
        {/* Breadcrumb */}
        <nav className="breadcrumb">
          <a href="/">Trang chủ</a>
          <span>/</span>
          <a href="/nam">Nam</a>
          <span>/</span>
          <span>{product?.name}</span>
        </nav>

        <div className="product-detail-content">
          {/* Product Images */}
          <div className="product-images">
            <div className="main-image">
              <img
                src={Array.isArray(product?.images) 
                  ? product?.images?.[selectedImage] 
                  : product?.imageUrl || product?.image || 'https://via.placeholder.com/500'}
                alt={product?.name}
                className="product-main-img"
              />
            </div>
            <div className="thumbnail-images">
              {Array.isArray(product?.images) && product?.images?.map((image, index) => (
                <img
                  key={index}
                  src={image}
                  alt={`${product?.name} ${index + 1}`}
                  className={`thumbnail ${selectedImage === index ? 'active' : ''}`}
                  onClick={() => setSelectedImage(index)}
                />
              ))}
            </div>
          </div>

          {/* Product Info */}
          <div className="product-info">
            <h1 className="product-title">{product?.name}</h1>

            <div className="product-price">
              <span className="current-price">{product?.price?.toLocaleString()} ₫</span>
              {product?.oldPrice && (
                <span className="old-price">{product?.oldPrice?.toLocaleString()} ₫</span>
              )}
              {product?.oldPrice && (
                <span className="discount">
                  -{Math.round((1 - product?.price / product?.oldPrice) * 100)}%
                </span>
              )}
            </div>

            {isOutOfStock && (
              <div className="out-of-stock-badge">
                ⚠️ Sản phẩm tạm thời hết hàng
              </div>
            )}
            
            {!isOutOfStock && availableQuantity > 0 && (
              <div className="stock-info">
                <span className="stock-available"> Còn {availableQuantity} sản phẩm</span>
              </div>
            )}

            {/* Quantity */}
            <div className="quantity-section">
              <h3>Số lượng:</h3>
              <div className="quantity-controls">
                <button
                  className="quantity-btn"
                  onClick={() => handleQuantityChange(-1)}
                  disabled={quantity <= 1}
                >
                  -
                </button>
                <span className="quantity-value">{quantity}</span>
                <button
                  className="quantity-btn"
                  onClick={() => handleQuantityChange(1)}
                  disabled={quantity >= availableQuantity}
                >
                  +
                </button>
              </div>
              {quantity > availableQuantity && (
                <p className="quantity-warning">⚠️ Chỉ có {availableQuantity} sản phẩm còn lại</p>
              )}
            </div>

            {/* Add to Cart */}
            <div className="action-buttons">
              <button 
                className="add-to-cart-btn" 
                onClick={handleAddToCart}
                disabled={isOutOfStock}
              >
                {isOutOfStock ? 'Hết hàng' : 'Thêm vào giỏ hàng'}
              </button>
              <button 
                className="buy-now-btn"
                onClick={handleBuyNow}
                disabled={isOutOfStock}
              >
                {isOutOfStock ? 'Hết hàng' : 'Mua ngay'}
              </button>
            </div>
          </div>
        </div>

        {/* Product Description */}
        <div className="product-tabs">
          <div className="tab-buttons">
            <button 
              className={`tab-btn ${activeTab === 'description' ? 'active' : ''}`}
              onClick={() => setActiveTab('description')}
            >
              Mô tả
            </button>
          </div>

          <div className="tab-content">
            {activeTab === 'description' && (
              <div className="tab-panel active">
                <h3>Mô tả sản phẩm</h3>
                <p>{product?.description}</p>
              </div>
            )}
          </div>
        </div>
      </main>

      <Footer />
    </div>
  );
};

export default ProductDetail;
import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { message } from 'antd';
import logger from '../../../../utils/logger';
import Header from '../../../ui/Header/Header';
import Footer from '../../../ui/Footer/Footer';
import ProductCard from '../../../ui/ProductCard/ProductCard';
import { getProductsByCategoryApi, getAllProductsApi } from '../../../../api/product.api';
import './ProductPage.css';

const ProductPage = ({
  title,
  description,
  category,
  products = []
}) => {
  const [displayProducts, setDisplayProducts] = useState([]);
  const [loading, setLoading] = useState(false);
  const [currentCategory, setCurrentCategory] = useState(category);

  // Load products from API based on category
  useEffect(() => {
    setCurrentCategory(category);
    loadProductsByCategory();
  }, [category]);

  const loadProductsByCategory = async () => {
    try {
      setLoading(true);
      let response;
      if (category === 'all') {
        response = await getAllProductsApi();
      } else {
        response = await getProductsByCategoryApi(category);
      }
      // Lọc chỉ hiển thị sản phẩm có số lượng > 0
      const availableProducts = (response.data || []).filter(product => 
        product.quantum !== undefined ? product.quantum > 0 : true
      );
      setDisplayProducts(availableProducts);
    } catch (error) {
      console.error('Failed to load products:', error);
      message.error('Không thể tải sản phẩm');
      setDisplayProducts([]);
    } finally {
      setLoading(false);
    }
  };

  const categories = [
    { value: 'all', label: 'Tất cả sản phẩm' },
    { value: 'nam', label: 'Thời trang nam' },
    { value: 'nu', label: 'Thời trang nữ' },
    { value: 'giay', label: 'Giày dép' },
    { value: 'phu-kien', label: 'Phụ kiện' }
  ];

  const handleCategoryChange = (categoryValue) => {
    setCurrentCategory(categoryValue);
    // Reload products based on selected category
    if (categoryValue === 'all') {
      loadAllProducts();
    } else {
      loadProductsBySpecificCategory(categoryValue);
    }
  };

  const loadAllProducts = async () => {
    try {
      setLoading(true);
      const response = await getAllProductsApi();
      const availableProducts = (response.data || []).filter(product => 
        product.quantum !== undefined ? product.quantum > 0 : true
      );
      setDisplayProducts(availableProducts);
    } catch (error) {
      console.error('Failed to load products:', error);
      message.error('Không thể tải sản phẩm');
      setDisplayProducts([]);
    } finally {
      setLoading(false);
    }
  };

  const loadProductsBySpecificCategory = async (categoryValue) => {
    try {
      setLoading(true);
      const response = await getProductsByCategoryApi(categoryValue);
      const availableProducts = (response.data || []).filter(product => 
        product.quantum !== undefined ? product.quantum > 0 : true
      );
      setDisplayProducts(availableProducts);
    } catch (error) {
      console.error('Failed to load products:', error);
      message.error('Không thể tải sản phẩm');
      setDisplayProducts([]);
    } finally {
      setLoading(false);
    }
  };

  const colors = [
    { name: 'black', hex: '#000000' },
    { name: 'white', hex: '#FFFFFF' },
    { name: 'gray', hex: '#D2B48C' },
    { name: 'blue', hex: '#003380' },
    { name: 'navy', hex: '#1e40af' }
  ];

  const sizes = ['S', 'M', 'L', 'XL'];

  return (
    <div className="product-page">
      <Header />

      <main className="product-main">
        <div className="container">
          {/* Breadcrumb */}
          <div className="breadcrumb">
            <Link to="/" className="breadcrumb-link">Trang chủ</Link>
            <span className="breadcrumb-separator">/</span>
            <span className="breadcrumb-current">{title}</span>
          </div>

          {/* Header */}
          <div className="page-header">
            <h1 className="page-title">{title}</h1>
            <p className="page-description">{description}</p>
          </div>

          <div className="product-content">
            {/* Sidebar */}
            <aside className="product-sidebar">
              {/* Categories */}
              <div className="filter-section">
                <h3 className="filter-title">Danh mục</h3>
                <ul className="filter-list">
                  {categories.map((cat) => (
                    <li key={cat.value}>
                      <button
                        className={`filter-item ${currentCategory === cat.value ? 'active' : ''}`}
                        onClick={() => handleCategoryChange(cat.value)}
                      >
                        {cat.label}
                      </button>
                    </li>
                  ))}
                </ul>
              </div>
            </aside>

            {/* Main Content */}
            <div className="product-grid-section">
              {/* Sort and Results */}
              <div className="product-controls">
                <div className="results-count">
                  Hiển thị {displayProducts.length} sản phẩm
                </div>
              </div>

              {/* Products Grid */}
              <div className="products-grid">
                {displayProducts.length > 0 ? displayProducts.map((product) => (
                  <ProductCard
                    key={product.id}
                    image={product.imageUrl || product.image || 'https://via.placeholder.com/300'}
                    alt={product.name}
                    category={product.category || product.type || 'Sản phẩm'}
                    name={product.name}
                    currentPrice={`${product.price?.toLocaleString()}đ`}
                    oldPrice={product.oldPrice ? `${product.oldPrice.toLocaleString()}đ` : undefined}
                    link={`/product/${product.id}`}
                  />
                )) : (
                  <div style={{ gridColumn: '1 / -1', textAlign: 'center', padding: '40px' }}>
                    <p>Không có sản phẩm nào trong danh mục này</p>
                  </div>
                )}
              </div>

              {/* Pagination */}
              <div className="pagination">
                <button className="pagination-btn prev" disabled>
                  <span className="material-icons">chevron_left</span>
                  Trước
                </button>

                <div className="pagination-numbers">
                  <button className="pagination-number active">1</button>
                </div>

                <button className="pagination-btn next" disabled>
                  Sau
                  <span className="material-icons">chevron_right</span>
                </button>
              </div>
            </div>
          </div>
        </div>
      </main>

      <Footer />
    </div>
  );
};

export default ProductPage;
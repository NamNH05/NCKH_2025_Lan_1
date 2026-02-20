import React from 'react';
import { useParams, useLocation } from 'react-router-dom';
import ProductPage from '../../components/features/product/ProductPage/ProductPage';

// Function to normalize Vietnamese characters and remove diacritics
const normalizeCategory = (str) => {
  if (!str) return '';
  
  // Normalize Vietnamese characters to base characters
  const normalized = str
    .normalize('NFD')
    .replace(/[\u0300-\u036f]/g, '') // Remove diacritics
    .toLowerCase()
    .replace(/\s+/g, '-') // Replace spaces with hyphens
    .replace(/[^\w-]/g, ''); // Remove special characters except hyphens
  
  return normalized;
};

const CategoryPage = () => {
  const { category: paramCategory } = useParams();
  const location = useLocation();
  
  // Get category from URL param or pathname
  let category = paramCategory;
  
  if (!category) {
    // Extract category from pathname
    const pathName = location.pathname;
    const categoryMatch = pathName.replace(/^\//, '').split('/')[0];
    
    // Normalize and map category
    category = normalizeCategory(categoryMatch) || 'san-pham';
  } else {
    // Normalize param category as well
    category = normalizeCategory(category);
  }

  // Map category from URL to product page config
  const categoryConfig = {
    'san-pham': {
      title: 'Tất Cả Sản Phẩm',
      description: 'Khám phá bộ sưu tập sản phẩm thời trang đa dạng với những thiết kế hiện đại, chất liệu cao cấp và phong cách tinh tế từ Sixedi.',
      category: 'all'
    },
    'nam': {
      title: 'Thời Trang Nam',
      description: 'Khám phá bộ sưu tập thời trang nam hiện đại với những thiết kế nam tính, chất liệu cao cấp và phong cách phù hợp từ Sixedi.',
      category: 'nam'
    },
    'nu': {
      title: 'Thời Trang Nữ',
      description: 'Khám phá bộ sưu tập thời trang nữ quyến rũ với những thiết kế tinh tế, chất liệu cao cấp và phong cách thời thượng từ Sixedi.',
      category: 'nu'
    },
    'giay': {
      title: 'Giày Dép',
      description: 'Khám phá bộ sưu tập giày dép đa phong cách từ Sixedi. Từ giày thể thao tới giày lưới, tìm kiếm chiếc giày hoàn hảo cho bất kỳ dịp nào.',
      category: 'giay'
    },
    'phu-kien': {
      title: 'Phụ Kiện',
      description: 'Khám phá bộ sưu tập phụ kiện thời trang từ Sixedi. Hoàn thiện bộ trang phục của bạn với các lựa chọn phụ kiện đa dạng.',
      category: 'phu-kien'
    }
  };

  const config = categoryConfig[category] || categoryConfig['san-pham'];

  return (
    <ProductPage
      title={config.title}
      description={config.description}
      category={config.category}
    />
  );
};

export default CategoryPage;

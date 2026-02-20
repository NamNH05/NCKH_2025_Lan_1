import React from 'react';
import { Link } from 'react-router-dom';
import './ProductCard.css';

const ProductCard = ({
  image,
  alt,
  category,
  name,
  currentPrice,
  oldPrice,
  badge,
  link = '#'
}) => {
  const [imageError, setImageError] = React.useState(false);
  
  const handleImageError = () => {
    setImageError(true);
  };

  return (
    <Link to={link} className="product-card-link">
      <div className="product-card">
        <div className="product-image">
          {badge && (
            <span className={`badge ${badge.type}`}>
              {badge.text}
            </span>
          )}
          {!imageError ? (
            <img 
              alt={alt} 
              src={image}
              onError={handleImageError}
              style={{ objectFit: 'cover', width: '100%', height: '100%' }}
            />
          ) : (
            <div style={{
              width: '100%',
              height: '100%',
              backgroundColor: '#f0f0f0',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              color: '#999',
              fontSize: '14px'
            }}>
              Không có ảnh
            </div>
          )}
        </div>
        <div className="product-info">
          <h3>{category}</h3>
          <span className="product-name">{name}</span>
          <div className="product-price">
            <span className="current-price">{currentPrice}</span>
            {oldPrice && <span className="old-price">{oldPrice}</span>}
          </div>
        </div>
      </div>
    </Link>
  );
};

export default ProductCard;
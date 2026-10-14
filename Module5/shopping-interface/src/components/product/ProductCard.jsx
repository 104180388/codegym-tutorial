import React from 'react';
import { Link } from 'react-router-dom';
import { useDispatch } from 'react-redux';
import { ShoppingCart, Eye, Star } from 'lucide-react';
import { addToCart } from '../../redux/cartSlice';
import { showToast } from '../../redux/uiSlice';
import { formatCurrency } from '../../utils/formatters';

export default function ProductCard({ product }) {
  const dispatch = useDispatch();

  if (!product) return null;

  const id = product.id;
  const name = product['product-name'] || product.name;
  const picture = product['product-picture'] || product.picture;
  const desc = product['product-description'] || product.description;
  const price = product.price || 0;
  const quantity = product.quantity || 0;

  const handleAddToCart = (e) => {
    e.preventDefault();
    e.stopPropagation();
    dispatch(addToCart({ product, quantity: 1 }));
    dispatch(
      showToast({
        message: `Đã thêm "${name}" vào giỏ hàng!`,
        type: 'success',
      })
    );
  };

  return (
    <article className="product-card">
      {/* Product Image */}
      <Link to={`/product/${id}`} className="product-image-container">
        <img src={picture} alt={name} className="product-image" loading="lazy" />
        <div className="product-badge-overlay">
          {quantity < 8 ? (
            <span className="badge badge-low">Chỉ còn {quantity} máy</span>
          ) : (
            <span className="badge badge-stock">Còn hàng ({quantity})</span>
          )}
        </div>
      </Link>

      {/* Card Body */}
      <div className="product-body">
        {/* Rating */}
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.3rem', marginBottom: '0.4rem', color: '#f59e0b', fontSize: '0.8rem' }}>
          <Star size={14} fill="#f59e0b" />
          <Star size={14} fill="#f59e0b" />
          <Star size={14} fill="#f59e0b" />
          <Star size={14} fill="#f59e0b" />
          <Star size={14} fill="#f59e0b" />
          <span style={{ color: 'var(--text-muted)', marginLeft: 4 }}>(5.0)</span>
        </div>

        {/* Title */}
        <h3 className="product-title">
          <Link to={`/product/${id}`}>{name}</Link>
        </h3>

        {/* Description snippet */}
        <p className="product-desc-snippet">{desc}</p>

        {/* Footer info & CTA */}
        <div className="product-footer">
          <div className="product-price-row">
            <span className="product-price">{formatCurrency(price)}</span>
            <span className="product-stock-text">Kho: {quantity} máy</span>
          </div>

          <div className="product-actions">
            <Link
              to={`/product/${id}`}
              className="btn btn-secondary btn-sm"
              aria-label={`Xem chi tiết ${name}`}
            >
              <Eye size={15} />
              <span>Chi tiết</span>
            </Link>

            <button
              type="button"
              className="btn btn-primary btn-sm"
              onClick={handleAddToCart}
              aria-label={`Thêm ${name} vào giỏ hàng`}
              title="Thêm vào giỏ hàng"
            >
              <ShoppingCart size={15} />
              <span>Thêm giỏ</span>
            </button>
          </div>
        </div>
      </div>
    </article>
  );
}

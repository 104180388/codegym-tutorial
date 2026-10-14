import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useDispatch } from 'react-redux';
import {
  ShoppingCart,
  Zap,
  Gift,
  Truck,
  ShieldCheck,
  RotateCcw,
  Star,
  Cpu,
  HardDrive,
  Monitor,
  Battery,
} from 'lucide-react';
import { addToCart } from '../../redux/cartSlice';
import { showToast } from '../../redux/uiSlice';
import { formatCurrency } from '../../utils/formatters';
import QuantitySelector from '../shopping/QuantitySelector';

export default function ProductDetail({ product }) {
  const dispatch = useDispatch();
  const navigate = useNavigate();

  const [quantity, setQuantity] = useState(1);

  if (!product) return null;

  const id = product.id;
  const name = product['product-name'] || product.name;
  const picture = product['product-picture'] || product.picture;
  const desc = product['product-description'] || product.description;
  const price = product.price || 0;
  const stock = product.quantity || 0;

  const handleDecrease = () => {
    if (quantity > 1) {
      setQuantity((prev) => prev - 1);
    }
  };

  const handleIncrease = () => {
    if (quantity < stock) {
      setQuantity((prev) => prev + 1);
    } else {
      dispatch(
        showToast({
          message: `Kho hàng chỉ còn tối đa ${stock} sản phẩm!`,
          type: 'error',
        })
      );
    }
  };

  const handleAddToCart = () => {
    dispatch(addToCart({ product, quantity }));
    dispatch(
      showToast({
        message: `Đã thêm ${quantity} x "${name}" vào giỏ hàng!`,
        type: 'success',
      })
    );
  };

  const handleBuyNow = () => {
    dispatch(addToCart({ product, quantity }));
    navigate('/checkout');
  };

  return (
    <div className="product-detail-container">
      {/* Top Section: Gallery & Info */}
      <div className="detail-grid">
        {/* Left Column: Image Gallery */}
        <div className="detail-gallery">
          <img src={picture} alt={name} className="detail-main-img" />

          <div className="detail-badges">
            <span className="badge badge-brand">Chính Hãng 100%</span>
            <span className="badge badge-stock">Bảo hành 24 tháng</span>
            <span className="badge badge-brand">Freeship Toàn Quốc</span>
          </div>
        </div>

        {/* Right Column: Information & Actions */}
        <div className="detail-info">
          {/* Rating */}
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '0.75rem', color: '#f59e0b' }}>
            <div style={{ display: 'flex', gap: '2px' }}>
              <Star size={18} fill="#f59e0b" />
              <Star size={18} fill="#f59e0b" />
              <Star size={18} fill="#f59e0b" />
              <Star size={18} fill="#f59e0b" />
              <Star size={18} fill="#f59e0b" />
            </div>
            <span style={{ color: 'var(--text-muted)', fontSize: '0.88rem' }}>
              (Đánh giá 5/5 từ 48 khách hàng)
            </span>
          </div>

          <h1 className="detail-title">{name}</h1>

          {/* Price Box */}
          <div className="detail-price-box">
            <div>
              <span style={{ fontSize: '0.85rem', color: 'var(--text-muted)', display: 'block' }}>
                Giá bán ưu đãi chính hãng:
              </span>
              <span className="detail-price">{formatCurrency(price)}</span>
            </div>

            <div className="detail-stock-indicator">
              <span className="stock-dot" />
              <span>{stock > 0 ? `Còn hàng trong kho (${stock} máy)` : 'Tạm hết hàng'}</span>
            </div>
          </div>

          {/* Description */}
          <div className="detail-description-section">
            <h4>Mô Tả Sản Phẩm</h4>
            <p className="detail-description-text">{desc}</p>
          </div>

          {/* Perks */}
          <div className="detail-perks">
            <div className="perk-item">
              <Gift size={18} color="#818cf8" />
              <span>Tặng Balo Laptop Chống Sốc Cao Cấp</span>
            </div>
            <div className="perk-item">
              <Truck size={18} color="#818cf8" />
              <span>Giao hàng miễn phí toàn quốc</span>
            </div>
            <div className="perk-item">
              <ShieldCheck size={18} color="#818cf8" />
              <span>Bảo hành chính hãng 24 tháng</span>
            </div>
            <div className="perk-item">
              <RotateCcw size={18} color="#818cf8" />
              <span>Lỗi 1 đổi 1 trong 30 ngày đầu</span>
            </div>
          </div>

          {/* Quantity Selector using QuantitySelector component */}
          <div className="quantity-control">
            <span className="qty-label">Chọn số lượng:</span>
            <QuantitySelector
              quantity={quantity}
              onDecrease={handleDecrease}
              onIncrease={handleIncrease}
              max={stock}
            />
            <span style={{ color: 'var(--text-sub)', fontSize: '0.88rem' }}>
              Tổng: <strong style={{ color: '#38bdf8' }}>{formatCurrency(price * quantity)}</strong>
            </span>
          </div>

          {/* Action CTA Buttons */}
          <div className="detail-buttons">
            <button
              id="add-to-cart-detail-btn"
              type="button"
              className="btn btn-secondary btn-lg"
              style={{ flex: 1 }}
              onClick={handleAddToCart}
              disabled={stock <= 0}
            >
              <ShoppingCart size={19} />
              <span>Thêm Vào Giỏ Hàng</span>
            </button>

            <button
              id="buy-now-btn"
              type="button"
              className="btn btn-primary btn-lg"
              style={{ flex: 1 }}
              onClick={handleBuyNow}
              disabled={stock <= 0}
            >
              <Zap size={19} />
              <span>Mua Ngay</span>
            </button>
          </div>
        </div>
      </div>

      {/* Specifications Checklist */}
      <div style={{ marginTop: '3.5rem' }}>
        <h3 style={{ fontSize: '1.6rem', fontWeight: 800, marginBottom: '1.5rem' }}>
          Thông Số Kỹ Thuật Nổi Bật
        </h3>
        <div
          style={{
            background: 'var(--bg-card)',
            border: '1px solid var(--border-color)',
            borderRadius: 'var(--radius-lg)',
            padding: '1.75rem',
            display: 'grid',
            gridTemplateColumns: 'repeat(auto-fit, minmax(240px, 1fr))',
            gap: '1.5rem',
          }}
        >
          <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
            <div className="feature-icon-wrapper"><Cpu size={22} /></div>
            <div>
              <p style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>Bộ vi xử lý</p>
              <h5 style={{ fontSize: '0.95rem' }}>Intel Core Ultra / Apple Silicon M-Series</h5>
            </div>
          </div>

          <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
            <div className="feature-icon-wrapper"><HardDrive size={22} /></div>
            <div>
              <p style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>Lưu trữ & RAM</p>
              <h5 style={{ fontSize: '0.95rem' }}>RAM DDR5 tốc độ cao & SSD PCIe Gen4</h5>
            </div>
          </div>

          <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
            <div className="feature-icon-wrapper"><Monitor size={22} /></div>
            <div>
              <p style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>Màn hình hiển thị</p>
              <h5 style={{ fontSize: '0.95rem' }}>OLED / Liquid Retina XDR 120Hz sắc nét</h5>
            </div>
          </div>

          <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
            <div className="feature-icon-wrapper"><Battery size={22} /></div>
            <div>
              <p style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>Thời lượng pin</p>
              <h5 style={{ fontSize: '0.95rem' }}>Lên đến 14 - 18 giờ sử dụng liên tục</h5>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

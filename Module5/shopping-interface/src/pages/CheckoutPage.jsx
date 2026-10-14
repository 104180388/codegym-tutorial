import React, { useEffect } from 'react';
import { useSelector } from 'react-redux';
import { Link } from 'react-router-dom';
import { ShoppingBag, ArrowLeft, ShieldCheck, CheckCircle2 } from 'lucide-react';
import {
  selectCartItems,
  selectCartTotalPrice,
  selectCartTotalCount,
} from '../redux/cartSlice';
import CheckoutForm from '../components/shopping/CheckoutForm';
import { formatCurrency } from '../utils/formatters';

export default function CheckoutPage() {
  const cartItems = useSelector(selectCartItems);
  const totalPrice = useSelector(selectCartTotalPrice);
  const totalCount = useSelector(selectCartTotalCount);

  useEffect(() => {
    window.scrollTo(0, 0);
  }, []);

  if (cartItems.length === 0) {
    return (
      <div className="checkout-page">
        <div className="container" style={{ textAlign: 'center', padding: '5rem 0' }}>
          <div
            className="modal-success-icon"
            style={{ borderColor: 'var(--border-color)', color: 'var(--text-muted)', margin: '0 auto 1.5rem auto' }}
          >
            <ShoppingBag size={38} />
          </div>
          <h2 style={{ fontSize: '1.8rem', fontWeight: 800, marginBottom: '0.75rem', color: '#ffffff' }}>
            Giỏ Hàng Của Bạn Đang Trống
          </h2>
          <p style={{ color: 'var(--text-muted)', marginBottom: '2rem' }}>
            Bạn chưa thêm sản phẩm nào vào giỏ để tiến hành thanh toán đơn hàng.
          </p>
          <Link to="/" className="btn btn-primary btn-lg">
            <ArrowLeft size={18} /> Khám phá laptop ngay
          </Link>
        </div>
      </div>
    );
  }

  return (
    <div className="checkout-page">
      <div className="container">
        {/* Breadcrumb Navigation */}
        <nav className="breadcrumbs" aria-label="Đường dẫn điều hướng">
          <Link to="/">Trang chủ</Link>
          <span>/</span>
          <Link to="/cart">Giỏ hàng</Link>
          <span>/</span>
          <span className="current">Trang thanh toán đơn hàng</span>
        </nav>

        <h1 style={{ fontSize: '2.2rem', fontWeight: 800, marginBottom: '0.5rem', color: '#ffffff' }}>
          Xác Nhận & Thanh Toán Đơn Hàng
        </h1>
        <p style={{ color: 'var(--text-muted)', marginBottom: '2.5rem' }}>
          Không yêu cầu đăng ký/đăng nhập. Điền thông tin nhận hàng để hoàn tất đơn ngay lập tức.
        </p>

        {/* Two-Column Checkout Layout */}
        <div className="checkout-grid">
          {/* Left Column: CheckoutForm Component */}
          <div>
            <CheckoutForm cartItems={cartItems} totalPrice={totalPrice} />
          </div>

          {/* Right Column: Order Items & Summary Box */}
          <div>
            <div className="order-summary-box">
              <h3 style={{ fontSize: '1.25rem', fontWeight: 800, marginBottom: '1.25rem', color: '#ffffff' }}>
                Đơn Hàng ({totalCount} sản phẩm)
              </h3>

              {/* Items List Preview */}
              <div className="summary-item-list">
                {cartItems.map((item) => {
                  const product = item.product;
                  const name = product['product-name'] || product.name;
                  const picture = product['product-picture'] || product.picture;
                  const price = product.price || 0;

                  return (
                    <div key={product.id} className="summary-item">
                      <img src={picture} alt={name} />
                      <div className="summary-item-details">
                        <h4 className="summary-item-name">{name}</h4>
                        <div className="summary-item-meta">
                          Số lượng: <strong>{item.quantity}</strong> × {formatCurrency(price)}
                        </div>
                      </div>
                      <span className="summary-item-price">
                        {formatCurrency(price * item.quantity)}
                      </span>
                    </div>
                  );
                })}
              </div>

              {/* Price Breakdown */}
              <div className="summary-breakdown">
                <div className="breakdown-row">
                  <span>Tạm tính giỏ hàng:</span>
                  <span>{formatCurrency(totalPrice)}</span>
                </div>
                <div className="breakdown-row">
                  <span>Phí vận chuyển toàn quốc:</span>
                  <span style={{ color: '#10b981', fontWeight: 600 }}>Miễn phí (0₫)</span>
                </div>
                <div className="breakdown-row">
                  <span>Khuyến mãi đặc quyền:</span>
                  <span style={{ color: '#10b981', fontWeight: 600 }}>Tặng Balo + Chuột Wireless</span>
                </div>

                <div className="breakdown-row total-row">
                  <span>Tổng tiền thanh toán:</span>
                  <span className="total-amount">{formatCurrency(totalPrice)}</span>
                </div>
              </div>

              {/* Guarantees */}
              <div style={{ display: 'flex', flexDirection: 'column', gap: '0.6rem', fontSize: '0.82rem', color: 'var(--text-muted)' }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                  <ShieldCheck size={16} color="#10b981" />
                  <span>Bảo hành phần cứng 24 tháng chính hãng</span>
                </div>
                <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                  <CheckCircle2 size={16} color="#10b981" />
                  <span>Không cần nạp tiền, hoàn tất đơn lập tức</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

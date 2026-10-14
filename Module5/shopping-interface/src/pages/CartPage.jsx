import React, { useEffect } from 'react';
import { useSelector } from 'react-redux';
import { Link } from 'react-router-dom';
import { ShoppingBag, ArrowLeft, ArrowRight, ShieldCheck, Sparkles } from 'lucide-react';
import {
  selectCartItems,
  selectCartTotalPrice,
  selectCartTotalCount,
} from '../redux/cartSlice';
import CartList from '../components/shopping/CartList';
import CartSummary from '../components/shopping/CartSummary';

export default function CartPage() {
  const cartItems = useSelector(selectCartItems);
  const totalPrice = useSelector(selectCartTotalPrice);
  const totalCount = useSelector(selectCartTotalCount);

  useEffect(() => {
    window.scrollTo(0, 0);
  }, []);

  return (
    <div className="cart-page">
      <div className="container">
        {/* Breadcrumb */}
        <nav className="breadcrumbs" aria-label="Đường dẫn điều hướng">
          <Link to="/">Trang chủ</Link>
          <span>/</span>
          <span className="current">Giỏ hàng của bạn</span>
        </nav>

        <div className="cart-page-header">
          <h1 className="cart-page-title">
            Giỏ Hàng Mua Sắm ({totalCount} sản phẩm)
          </h1>
          <p className="cart-page-subtitle">
            Kiểm tra danh sách laptop và số lượng trước khi tiến hành thanh toán
          </p>
        </div>

        {cartItems.length === 0 ? (
          /* Empty Cart State */
          <div className="cart-empty-view">
            <div className="cart-empty-icon-box">
              <ShoppingBag size={54} />
            </div>
            <h2 style={{ fontSize: '1.6rem', fontWeight: 800, marginBottom: '0.6rem', color: '#ffffff' }}>
              Giỏ hàng của bạn đang trống
            </h2>
            <p style={{ color: 'var(--text-muted)', maxWidth: '440px', margin: '0 auto 2rem auto' }}>
              Hãy khám phá các dòng laptop chính hãng mới nhất và chọn cho mình sản phẩm phù hợp.
            </p>
            <Link to="/" className="btn btn-primary btn-lg">
              <ArrowLeft size={18} />
              <span>Khám phá laptop ngay</span>
            </Link>
          </div>
        ) : (
          /* Two-Column Cart Layout: CartList on left, CartSummary on right */
          <div className="cart-page-grid">
            {/* Left: Cart Items List */}
            <div className="cart-items-section">
              <CartList items={cartItems} />
            </div>

            {/* Right: Cart Summary Box */}
            <div className="cart-summary-section">
              <CartSummary
                totalPrice={totalPrice}
                totalCount={totalCount}
                showCheckoutButton={true}
              />
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

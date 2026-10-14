import React from 'react';
import { Link } from 'react-router-dom';
import { useSelector } from 'react-redux';
import { Laptop, ShoppingBag, User } from 'lucide-react';
import { selectCartTotalCount, selectCartTotalPrice } from '../../redux/cartSlice';
import { formatCurrency } from '../../utils/formatters';

export default function Header() {
  const totalCount = useSelector(selectCartTotalCount);
  const totalPrice = useSelector(selectCartTotalPrice);

  return (
    <div className="header-wrapper">
      <div className="container">
        <div className="header-top">
          {/* Brand Logo */}
          <Link to="/" className="nav-brand" aria-label="LaptopZone Trang chủ">
            <div className="nav-brand-icon">
              <Laptop size={22} strokeWidth={2.5} />
            </div>
            <span>Laptop<span style={{ color: '#06b6d4' }}>Zone</span></span>
          </Link>

          {/* Header Actions: User Account, Cart */}
          <div className="header-actions">
            {/* Customer Account indicator */}
            <div className="header-user-btn" title="Tài khoản khách hàng">
              <div className="header-icon-circle">
                <User size={18} />
              </div>
              <div className="header-user-text">
                <span className="user-label">Khách hàng</span>
                <span className="user-status">Khách vãng lai</span>
              </div>
            </div>

            {/* Cart Button navigating to /cart */}
            <Link
              to="/cart"
              id="header-cart-btn"
              className="cart-btn"
              aria-label={`Giỏ hàng có ${totalCount} sản phẩm`}
            >
              <div style={{ position: 'relative', display: 'flex' }}>
                <ShoppingBag size={20} />
                <span className="cart-badge">{totalCount}</span>
              </div>
              <div className="cart-btn-text">
                <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)', display: 'block' }}>Giỏ hàng</span>
                <span style={{ fontSize: '0.88rem', fontWeight: 700, color: '#38bdf8' }}>
                  {formatCurrency(totalPrice)}
                </span>
              </div>
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
}

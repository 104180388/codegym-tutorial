import React, { useState } from 'react';
import { NavLink, Link } from 'react-router-dom';
import { useSelector } from 'react-redux';
import { Home, ShoppingCart, CreditCard, Phone, Menu, X, Sparkles } from 'lucide-react';
import { selectCartTotalCount } from '../../redux/cartSlice';

export default function Navbar() {
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
  const totalCartCount = useSelector(selectCartTotalCount);

  return (
    <nav className="navbar-wrapper">
      <div className="container">
        <div className="navbar-content">
          {/* Main Navigation Links */}
          <ul className="nav-links">
            <li>
              <NavLink
                to="/"
                className={({ isActive }) => (isActive ? 'nav-link active' : 'nav-link')}
                end
              >
                <Home size={17} />
                <span>Trang chủ</span>
              </NavLink>
            </li>
            <li>
              <NavLink
                to="/cart"
                className={({ isActive }) => (isActive ? 'nav-link active' : 'nav-link')}
              >
                <ShoppingCart size={17} />
                <span>Giỏ hàng ({totalCartCount})</span>
              </NavLink>
            </li>
            <li>
              <NavLink
                to="/checkout"
                className={({ isActive }) => (isActive ? 'nav-link active' : 'nav-link')}
              >
                <CreditCard size={17} />
                <span>Thanh toán</span>
              </NavLink>
            </li>
          </ul>

          {/* Quick hotline & guarantee info */}
          <div className="nav-extra">
            <span className="nav-hotline">
              <Phone size={15} color="#06b6d4" />
              <span>Hotline tư vấn: <strong>1800 6868</strong> (Miễn phí)</span>
            </span>
          </div>

          {/* Mobile Menu Toggle Button */}
          <button
            type="button"
            className="mobile-menu-btn"
            onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
            aria-label="Toggle menu"
          >
            {mobileMenuOpen ? <X size={24} /> : <Menu size={24} />}
          </button>
        </div>

        {/* Mobile dropdown */}
        {mobileMenuOpen && (
          <div className="mobile-nav-dropdown">
            <Link
              to="/"
              className="nav-link"
              onClick={() => setMobileMenuOpen(false)}
            >
              <Home size={16} /> Trang chủ
            </Link>
            <Link
              to="/cart"
              className="nav-link"
              onClick={() => setMobileMenuOpen(false)}
            >
              <ShoppingCart size={16} /> Giỏ hàng ({totalCartCount})
            </Link>
            <Link
              to="/checkout"
              className="nav-link"
              onClick={() => setMobileMenuOpen(false)}
            >
              <CreditCard size={16} /> Trang thanh toán
            </Link>
          </div>
        )}
      </div>
    </nav>
  );
}

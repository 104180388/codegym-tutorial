import { Link, useLocation } from 'react-router-dom';
import { BookOpen, PlusCircle } from 'lucide-react';

export const Navbar = () => {
  const location = useLocation();

  return (
    <header className="navbar-container">
      <div className="navbar-inner">
        {/* Brand Logo & Name */}
        <Link to="/" className="navbar-brand">
          <div className="brand-icon-wrapper">
            <BookOpen className="brand-icon" size={26} />
            <span className="brand-dot"></span>
          </div>
          <div className="brand-text">
            <div className="brand-title">
              <span>Sky</span>Garden
              <span className="brand-badge">Thư viện</span>
            </div>
            <span className="brand-subtitle">Hệ thống quản lý sách hiện đại</span>
          </div>
        </Link>

        {/* Navigation Actions */}
        <nav className="navbar-nav">

          <Link
            to="/add"
            className={`btn-primary-nav ${location.pathname === '/add' ? 'active' : ''}`}
          >
            <PlusCircle size={18} />
            <span>Thêm sách mới</span>
          </Link>
        </nav>
      </div>
    </header>
  );
};

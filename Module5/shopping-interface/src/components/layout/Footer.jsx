import React from 'react';
import { Link } from 'react-router-dom';
import { Laptop, ShieldCheck, Truck, RotateCcw, Headphones } from 'lucide-react';

export default function Footer() {
  return (
    <footer className="footer">
      <div className="container">
        <div className="footer-grid">
          {/* Brand Info */}
          <div className="footer-brand">
            <Link to="/" className="nav-brand">
              <div className="nav-brand-icon">
                <Laptop size={20} strokeWidth={2.5} />
              </div>
              <span>Laptop<span style={{ color: '#06b6d4' }}>Zone</span></span>
            </Link>
            <p>
              Hệ thống bán lẻ máy tính xách tay cao cấp, chính hãng hàng đầu Việt Nam. Cam kết 100% sản phẩm chất lượng cao, bảo hành chính hãng và hỗ trợ kỹ thuật trọn đời.
            </p>
            <div style={{ display: 'flex', gap: '0.75rem' }}>
              <span className="badge badge-brand">Chính hãng 100%</span>
              <span className="badge badge-stock">Bảo hành 24T</span>
            </div>
          </div>

          {/* Column 1: Dòng máy */}
          <div className="footer-column">
            <h4>Dòng Máy Phổ Biến</h4>
            <ul className="footer-links">
              <li><Link to="/">Laptop Gaming & Esports</Link></li>
              <li><Link to="/">Laptop Mỏng Nhẹ Doanh Nhân</Link></li>
              <li><Link to="/">Laptop Đồ Họa - Kỹ Thuật</Link></li>
              <li><Link to="/">Laptop Học Tập - Văn Phòng</Link></li>
              <li><Link to="/">Apple MacBook Chính Hãng</Link></li>
            </ul>
          </div>

          {/* Column 2: Chính sách */}
          <div className="footer-column">
            <h4>Chính Sách & Hỗ Trợ</h4>
            <ul className="footer-links">
              <li><a href="#!">Chính sách bảo hành 1 đổi 1</a></li>
              <li><a href="#!">Giao hàng hỏa tốc trong 2 giờ</a></li>
              <li><a href="#!">Chính sách trả góp 0% lãi suất</a></li>
              <li><a href="#!">Quy định bảo mật thông tin</a></li>
              <li><a href="#!">Hướng dẫn đặt hàng online</a></li>
            </ul>
          </div>

          {/* Column 3: Liên hệ */}
          <div className="footer-column">
            <h4>Tổng Đài Hỗ Trợ</h4>
            <ul className="footer-links">
              <li style={{ color: '#ffffff', fontWeight: 600 }}>
                Tư vấn mua hàng: <span style={{ color: '#38bdf8' }}>1800 6868</span>
              </li>
              <li style={{ color: '#ffffff', fontWeight: 600 }}>
                Chăm sóc khách hàng: <span style={{ color: '#38bdf8' }}>1900 8888</span>
              </li>
              <li style={{ color: '#94a3b8' }}>Email: cskh@laptopzone.vn</li>
              <li style={{ color: '#94a3b8' }}>Địa chỉ: Tầng 5, Tòa nhà Công Nghệ cao, Hà Nội & TP. HCM</li>
            </ul>
          </div>
        </div>

        {/* Bottom copyright */}
        <div className="footer-bottom">
          <p>© 2026 LaptopZone</p>
        </div>
      </div>
    </footer>
  );
}

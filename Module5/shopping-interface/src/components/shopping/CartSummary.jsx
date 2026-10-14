import React from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { ArrowRight, ArrowLeft, ShieldCheck, Truck, CheckCircle2 } from 'lucide-react';
import { formatCurrency } from '../../utils/formatters';

export default function CartSummary({
  totalPrice = 0,
  totalCount = 0,
  showCheckoutButton = true,
}) {
  const navigate = useNavigate();

  return (
    <div className="cart-summary-card">
      <h3 className="cart-summary-title">Tóm Tắt Đơn Hàng</h3>

      <div className="cart-summary-rows">
        <div className="cart-summary-row">
          <span>Tổng số lượng:</span>
          <strong>{totalCount} sản phẩm</strong>
        </div>

        <div className="cart-summary-row">
          <span>Tạm tính giỏ hàng:</span>
          <span>{formatCurrency(totalPrice)}</span>
        </div>

        <div className="cart-summary-row">
          <span>Phí giao hàng toàn quốc:</span>
          <span style={{ color: '#10b981', fontWeight: 600 }}>Miễn phí (0₫)</span>
        </div>

        <div className="cart-summary-row">
          <span>Quà tặng kèm:</span>
          <span style={{ color: '#38bdf8', fontWeight: 600 }}>Balo + Chuột Wireless</span>
        </div>

        <div className="cart-summary-row total-row">
          <span>Tổng thanh toán:</span>
          <span className="cart-total-amount">{formatCurrency(totalPrice)}</span>
        </div>
      </div>

      {showCheckoutButton && (
        <div className="cart-summary-cta">
          <button
            type="button"
            className="btn btn-primary btn-block btn-lg"
            onClick={() => navigate('/checkout')}
            disabled={totalPrice <= 0}
          >
            <span>Tiến Hành Thanh Toán</span>
            <ArrowRight size={18} />
          </button>

          <Link to="/" className="btn btn-secondary btn-block btn-sm" style={{ marginTop: '0.75rem' }}>
            <ArrowLeft size={16} />
            <span>Tiếp tục mua hàng</span>
          </Link>
        </div>
      )}

      {/* Trust badges */}
      <div className="cart-summary-badges">
        <div className="trust-item">
          <Truck size={16} color="#10b981" />
          <span>Giao hàng hỏa tốc trong 2 giờ nội thành</span>
        </div>
        <div className="trust-item">
          <ShieldCheck size={16} color="#10b981" />
          <span>Bảo hành chính hãng 24 tháng toàn quốc</span>
        </div>
        <div className="trust-item">
          <CheckCircle2 size={16} color="#38bdf8" />
          <span>Thử nghiệm: Thanh toán không trừ tiền thật</span>
        </div>
      </div>
    </div>
  );
}

import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import { CheckCircle2, PackageCheck, ShoppingBag, ArrowRight } from 'lucide-react';
import { selectOrderSuccessData, clearOrderSuccess } from '../redux/uiSlice';
import { formatCurrency } from '../utils/formatters';

export default function NotificationModal() {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const orderData = useSelector(selectOrderSuccessData);

  if (!orderData) return null;

  const handleClose = () => {
    dispatch(clearOrderSuccess());
    navigate('/');
  };

  return (
    <div className="modal-overlay" role="dialog" aria-modal="true" aria-labelledby="order-success-title">
      <div className="modal-content">
        {/* Success Icon */}
        <div className="modal-success-icon">
          <CheckCircle2 size={46} strokeWidth={2.5} />
        </div>

        <h2 id="order-success-title" className="modal-title">
          Đặt Hàng Thành Công!
        </h2>
        <p className="modal-subtitle">
          Cảm ơn bạn! Đơn hàng laptop đã được hệ thống ghi nhận thành công (Môi trường thử nghiệm không trừ tiền).
        </p>

        {/* Order Details Breakdown */}
        <div className="modal-details-box">
          <div className="modal-detail-row">
            <span className="modal-detail-label">Mã đơn hàng:</span>
            <span className="modal-detail-value" style={{ color: '#818cf8', fontFamily: 'monospace', fontSize: '1rem' }}>
              {orderData.orderCode || `#LTZ-${Math.floor(100000 + Math.random() * 900000)}`}
            </span>
          </div>

          <div className="modal-detail-row">
            <span className="modal-detail-label">Khách hàng:</span>
            <span className="modal-detail-value">{orderData.fullName}</span>
          </div>

          <div className="modal-detail-row">
            <span className="modal-detail-label">Số điện thoại:</span>
            <span className="modal-detail-value">{orderData.phoneNumber}</span>
          </div>

          <div className="modal-detail-row">
            <span className="modal-detail-label">Địa chỉ nhận:</span>
            <span className="modal-detail-value">{orderData.shippingAddress}</span>
          </div>

          <div className="modal-detail-row">
            <span className="modal-detail-label">Phương thức:</span>
            <span className="modal-detail-value" style={{ color: '#10b981' }}>
              {orderData.paymentMethod} (Đã xác nhận 0₫)
            </span>
          </div>

          <div className="modal-detail-row">
            <span className="modal-detail-label">Tổng thanh toán:</span>
            <span className="modal-detail-value" style={{ color: '#38bdf8', fontSize: '1.1rem', fontWeight: 800 }}>
              {formatCurrency(orderData.totalAmount)}
            </span>
          </div>
        </div>

        {/* Action Button */}
        <button
          id="confirm-modal-ok-btn"
          className="btn btn-primary btn-block btn-lg"
          onClick={handleClose}
        >
          <ShoppingBag size={18} />
          <span>Tiếp tục mua sắm</span>
          <ArrowRight size={18} />
        </button>
      </div>
    </div>
  );
}

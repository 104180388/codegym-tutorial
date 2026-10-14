import React, { useState } from 'react';
import { useDispatch } from 'react-redux';
import {
  Truck,
  CreditCard,
  DollarSign,
  QrCode,
  CheckCircle2,
  AlertCircle,
} from 'lucide-react';
import { productService } from '../../api/productService';
import { clearCart } from '../../redux/cartSlice';
import { setOrderSuccess, showToast } from '../../redux/uiSlice';

export default function CheckoutForm({ cartItems = [], totalPrice = 0 }) {
  const dispatch = useDispatch();

  const [formData, setFormData] = useState({
    fullName: '',
    phoneNumber: '',
    email: '',
    city: 'Hà Nội',
    shippingAddress: '',
    deliveryNote: '',
    paymentMethod: 'COD',
  });

  const [errors, setErrors] = useState({});
  const [isSubmitting, setIsSubmitting] = useState(false);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
    if (errors[name]) {
      setErrors((prev) => ({ ...prev, [name]: '' }));
    }
  };

  const validateForm = () => {
    const newErrors = {};
    if (!formData.fullName.trim()) {
      newErrors.fullName = 'Vui lòng nhập họ và tên người nhận!';
    }
    if (!formData.phoneNumber.trim()) {
      newErrors.phoneNumber = 'Vui lòng nhập số điện thoại liên hệ!';
    } else if (!/^[0-9]{9,11}$/.test(formData.phoneNumber.replace(/\s+/g, ''))) {
      newErrors.phoneNumber = 'Số điện thoại không hợp lệ (9 - 11 chữ số)!';
    }
    if (!formData.shippingAddress.trim()) {
      newErrors.shippingAddress = 'Vui lòng nhập địa chỉ giao hàng cụ thể!';
    }
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (cartItems.length === 0) {
      dispatch(
        showToast({
          message: 'Giỏ hàng của bạn đang trống!',
          type: 'error',
        })
      );
      return;
    }

    if (!validateForm()) {
      dispatch(
        showToast({
          message: 'Vui lòng điền đầy đủ các thông tin giao hàng bắt buộc!',
          type: 'error',
        })
      );
      return;
    }

    setIsSubmitting(true);

    const paymentLabel =
      formData.paymentMethod === 'COD'
        ? 'Thanh toán khi nhận hàng (COD)'
        : formData.paymentMethod === 'VIETQR'
        ? 'Chuyển khoản VietQR'
        : 'Ví điện tử VNPAY / MoMo';

    const orderPayload = {
      orderCode: `LAPTOP-${Date.now().toString().slice(-6)}`,
      createdAt: new Date().toISOString(),
      customer: {
        fullName: formData.fullName,
        phoneNumber: formData.phoneNumber,
        email: formData.email,
        shippingAddress: `${formData.shippingAddress}, ${formData.city}`,
        deliveryNote: formData.deliveryNote,
      },
      paymentMethod: paymentLabel,
      items: cartItems.map((item) => ({
        id: item.product.id,
        name: item.product['product-name'] || item.product.name,
        price: item.product.price,
        quantity: item.quantity,
      })),
      totalAmount: totalPrice,
      paymentStatus: 'CONFIRMED_ZERO_MONEY',
    };

    try {
      // POST order to JSON Server via Axios
      await productService.createOrder(orderPayload);

      // Trigger Redux confirmation modal
      dispatch(
        setOrderSuccess({
          orderCode: orderPayload.orderCode,
          fullName: formData.fullName,
          phoneNumber: formData.phoneNumber,
          shippingAddress: `${formData.shippingAddress}, ${formData.city}`,
          paymentMethod: paymentLabel,
          totalAmount: totalPrice,
        })
      );

      // Clear cart
      dispatch(clearCart());
    } catch (err) {
      console.error('Lỗi khi gửi đơn hàng:', err);
      dispatch(
        showToast({
          message: 'Không thể kết nối đến máy chủ, vui lòng thử lại sau!',
          type: 'error',
        })
      );
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <form className="checkout-form" onSubmit={handleSubmit}>
      {/* 1. Customer Information Card */}
      <div className="checkout-card">
        <h3 className="checkout-card-title">
          <Truck size={20} color="#6366f1" />
          <span>1. Thông Tin Nhận Hàng</span>
        </h3>

        <div className="form-row">
          <div className="form-group">
            <label className="form-label" htmlFor="fullName">
              Họ và tên người nhận <span style={{ color: '#f43f5e' }}>*</span>
            </label>
            <input
              id="fullName"
              name="fullName"
              type="text"
              className="form-input"
              placeholder="Ví dụ: Nguyễn Văn A"
              value={formData.fullName}
              onChange={handleInputChange}
            />
            {errors.fullName && (
              <span className="form-error-msg">{errors.fullName}</span>
            )}
          </div>

          <div className="form-group">
            <label className="form-label" htmlFor="phoneNumber">
              Số điện thoại <span style={{ color: '#f43f5e' }}>*</span>
            </label>
            <input
              id="phoneNumber"
              name="phoneNumber"
              type="tel"
              className="form-input"
              placeholder="Ví dụ: 0912345678"
              value={formData.phoneNumber}
              onChange={handleInputChange}
            />
            {errors.phoneNumber && (
              <span className="form-error-msg">{errors.phoneNumber}</span>
            )}
          </div>
        </div>

        <div className="form-row">
          <div className="form-group">
            <label className="form-label" htmlFor="email">
              Email (Nhận hóa đơn điện tử)
            </label>
            <input
              id="email"
              name="email"
              type="email"
              className="form-input"
              placeholder="email@example.com"
              value={formData.email}
              onChange={handleInputChange}
            />
          </div>

          <div className="form-group">
            <label className="form-label" htmlFor="city">
              Tỉnh / Thành phố
            </label>
            <select
              id="city"
              name="city"
              className="form-select"
              value={formData.city}
              onChange={handleInputChange}
            >
              <option value="Hà Nội">Hà Nội</option>
              <option value="TP. Hồ Chí Minh">TP. Hồ Chí Minh</option>
              <option value="Đà Nẵng">Đà Nẵng</option>
              <option value="Hải Phòng">Hải Phòng</option>
              <option value="Cần Thơ">Cần Thơ</option>
              <option value="Tỉnh thành khác">Tỉnh thành khác</option>
            </select>
          </div>
        </div>

        <div className="form-group">
          <label className="form-label" htmlFor="shippingAddress">
            Địa chỉ nhận hàng chi tiết <span style={{ color: '#f43f5e' }}>*</span>
          </label>
          <input
            id="shippingAddress"
            name="shippingAddress"
            type="text"
            className="form-input"
            placeholder="Số nhà, tên đường, phường/xã, quận/huyện..."
            value={formData.shippingAddress}
            onChange={handleInputChange}
          />
          {errors.shippingAddress && (
            <span className="form-error-msg">{errors.shippingAddress}</span>
          )}
        </div>

        <div className="form-group" style={{ marginBottom: 0 }}>
          <label className="form-label" htmlFor="deliveryNote">
            Ghi chú giao hàng (Tùy chọn)
          </label>
          <textarea
            id="deliveryNote"
            name="deliveryNote"
            className="form-textarea"
            placeholder="Giao hàng giờ hành chính, gọi trước khi đến..."
            value={formData.deliveryNote}
            onChange={handleInputChange}
          />
        </div>
      </div>

      {/* 2. Payment Methods Card */}
      <div className="checkout-card">
        <h3 className="checkout-card-title">
          <CreditCard size={20} color="#6366f1" />
          <span>2. Phương Thức Thanh Toán (Không Cần Tiền)</span>
        </h3>

        <div className="zero-money-note">
          <CheckCircle2 size={18} />
          <span>
            Chính sách Demo: Không yêu cầu nạp tiền thực tế. Đơn hàng sẽ được xác nhận tự động 0₫.
          </span>
        </div>

        <div className="payment-methods">
          <label
            className={`payment-option ${formData.paymentMethod === 'COD' ? 'active' : ''}`}
          >
            <input
              type="radio"
              name="paymentMethod"
              value="COD"
              checked={formData.paymentMethod === 'COD'}
              onChange={handleInputChange}
              className="payment-radio"
            />
            <DollarSign size={24} color="#10b981" />
            <div className="payment-option-info">
              <h5>Thanh toán khi nhận hàng (COD)</h5>
              <p>Nhận hàng tại nhà rồi thanh toán, hoàn toàn an tâm.</p>
            </div>
          </label>

          <label
            className={`payment-option ${formData.paymentMethod === 'VIETQR' ? 'active' : ''}`}
          >
            <input
              type="radio"
              name="paymentMethod"
              value="VIETQR"
              checked={formData.paymentMethod === 'VIETQR'}
              onChange={handleInputChange}
              className="payment-radio"
            />
            <QrCode size={24} color="#06b6d4" />
            <div className="payment-option-info">
              <h5>Chuyển khoản VietQR Siêu Tốc</h5>
              <p>Quét mã QR từ ngân hàng (Mô phỏng 0₫).</p>
            </div>
          </label>

          <label
            className={`payment-option ${formData.paymentMethod === 'E_WALLET' ? 'active' : ''}`}
          >
            <input
              type="radio"
              name="paymentMethod"
              value="E_WALLET"
              checked={formData.paymentMethod === 'E_WALLET'}
              onChange={handleInputChange}
              className="payment-radio"
            />
            <CreditCard size={24} color="#818cf8" />
            <div className="payment-option-info">
              <h5>Ví điện tử VNPAY / MoMo</h5>
              <p>Cổng thanh toán không chạm tiện lợi.</p>
            </div>
          </label>
        </div>
      </div>

      {/* Submit Button */}
      <button
        id="submit-order-button"
        type="submit"
        className="btn btn-primary btn-block btn-lg"
        disabled={isSubmitting}
        style={{ height: '54px' }}
      >
        {isSubmitting ? (
          <span>Đang xử lý đơn hàng...</span>
        ) : (
          <>
            <CheckCircle2 size={20} />
            <span>Xác Nhận Đặt Hàng & Thanh Toán</span>
          </>
        )}
      </button>
    </form>
  );
}

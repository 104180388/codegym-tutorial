import React from 'react';
import { useDispatch } from 'react-redux';
import { Trash2 } from 'lucide-react';
import { clearCart } from '../../redux/cartSlice';
import CartItem from './CartItem';

export default function CartList({ items = [] }) {
  const dispatch = useDispatch();

  if (items.length === 0) {
    return null;
  }

  return (
    <div className="cart-list-container">
      {/* Table Header */}
      <div className="cart-table-header">
        <span className="cart-th-product">Sản phẩm</span>
        <span className="cart-th-qty">Số lượng</span>
        <span className="cart-th-subtotal">Thành tiền</span>
        <span className="cart-th-action">Xóa</span>
      </div>

      {/* List of Cart Items */}
      <div className="cart-table-body">
        {items.map((item) => (
          <CartItem key={item.product.id} item={item} />
        ))}
      </div>

      {/* Table Footer Actions */}
      <div className="cart-list-actions">
        <button
          type="button"
          className="btn btn-secondary btn-sm"
          onClick={() => dispatch(clearCart())}
          style={{ display: 'inline-flex', alignItems: 'center', gap: '0.4rem' }}
        >
          <Trash2 size={15} color="#f43f5e" />
          <span>Xóa tất cả</span>
        </button>
      </div>
    </div>
  );
}

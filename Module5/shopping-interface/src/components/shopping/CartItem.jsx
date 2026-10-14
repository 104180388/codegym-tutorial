import React from 'react';
import { useDispatch } from 'react-redux';
import { Link } from 'react-router-dom';
import { Trash2 } from 'lucide-react';
import { updateQuantity, removeFromCart } from '../../redux/cartSlice';
import { formatCurrency } from '../../utils/formatters';
import QuantitySelector from './QuantitySelector';

export default function CartItem({ item }) {
  const dispatch = useDispatch();
  const product = item.product;
  const name = product['product-name'] || product.name;
  const picture = product['product-picture'] || product.picture;
  const price = product.price || 0;
  const stock = product.quantity || 99;

  const handleDecrease = () => {
    dispatch(
      updateQuantity({
        productId: product.id,
        quantity: item.quantity - 1,
      })
    );
  };

  const handleIncrease = () => {
    if (item.quantity < stock) {
      dispatch(
        updateQuantity({
          productId: product.id,
          quantity: item.quantity + 1,
        })
      );
    }
  };

  const handleRemove = () => {
    dispatch(removeFromCart(product.id));
  };

  return (
    <div className="cart-table-row">
      {/* Product Image & Info */}
      <div className="cart-product-col">
        <Link to={`/product/${product.id}`} className="cart-item-img-wrapper">
          <img src={picture} alt={name} className="cart-item-img" />
        </Link>
        <div className="cart-item-details">
          <h4 className="cart-item-title">
            <Link to={`/product/${product.id}`}>{name}</Link>
          </h4>
          <span className="cart-item-unit-price">{formatCurrency(price)}</span>
        </div>
      </div>

      {/* Quantity Selector */}
      <div className="cart-qty-col">
        <QuantitySelector
          quantity={item.quantity}
          onDecrease={handleDecrease}
          onIncrease={handleIncrease}
          max={stock}
          size="small"
        />
      </div>

      {/* Item Subtotal */}
      <div className="cart-subtotal-col">
        <span className="cart-subtotal-price">
          {formatCurrency(price * item.quantity)}
        </span>
      </div>

      {/* Remove Action */}
      <div className="cart-action-col">
        <button
          type="button"
          className="cart-delete-btn"
          onClick={handleRemove}
          title="Xóa khỏi giỏ hàng"
          aria-label={`Xóa ${name}`}
        >
          <Trash2 size={18} />
        </button>
      </div>
    </div>
  );
}

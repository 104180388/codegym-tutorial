import React from 'react';
import { Plus, Minus } from 'lucide-react';

/**
 * QuantitySelector Component
 * Reusable component for increasing and decreasing item quantity.
 */
export default function QuantitySelector({
  quantity = 1,
  onIncrease,
  onDecrease,
  min = 1,
  max = 99,
  size = 'medium', // 'small' | 'medium'
}) {
  const isSmall = size === 'small';

  return (
    <div
      className="qty-input-group"
      style={isSmall ? { transform: 'scale(0.85)', transformOrigin: 'left' } : {}}
    >
      <button
        type="button"
        className="qty-btn"
        onClick={onDecrease}
        disabled={quantity <= min}
        aria-label="Giảm số lượng"
      >
        <Minus size={isSmall ? 14 : 16} />
      </button>

      <span
        className="qty-display"
        style={{ minWidth: isSmall ? '32px' : '44px' }}
      >
        {quantity}
      </span>

      <button
        type="button"
        className="qty-btn"
        onClick={onIncrease}
        disabled={quantity >= max}
        aria-label="Tăng số lượng"
      >
        <Plus size={isSmall ? 14 : 16} />
      </button>
    </div>
  );
}

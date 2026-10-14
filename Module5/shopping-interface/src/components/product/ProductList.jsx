import React from 'react';
import { Sparkles, Search } from 'lucide-react';
import ProductCard from './ProductCard';

export default function ProductList({
  products = [],
  loading = false,
  emptyMessage = 'Không tìm thấy sản phẩm phù hợp',
  onResetFilters,
}) {
  if (loading) {
    return (
      <div style={{ textAlign: 'center', padding: '4rem 0', color: 'var(--text-muted)' }}>
        <div
          className="modal-success-icon"
          style={{ borderColor: 'var(--primary)', color: 'var(--primary)', margin: '0 auto 1.5rem auto' }}
        >
          <Sparkles size={32} />
        </div>
        <h3 style={{ color: '#ffffff' }}>Đang tải danh sách laptop...</h3>
      </div>
    );
  }

  if (products.length === 0) {
    return (
      <div style={{ textAlign: 'center', padding: '4rem 0', color: 'var(--text-muted)' }}>
        <Search size={48} style={{ opacity: 0.3, margin: '0 auto 1rem auto' }} />
        <h3 style={{ color: '#ffffff', marginBottom: '0.5rem' }}>{emptyMessage}</h3>
        <p>Hãy thử tìm kiếm với từ khóa khác hoặc bỏ các bộ lọc đang chọn.</p>
        {onResetFilters && (
          <button
            type="button"
            className="btn btn-secondary btn-sm"
            style={{ marginTop: '1.25rem' }}
            onClick={onResetFilters}
          >
            Đặt lại bộ lọc
          </button>
        )}
      </div>
    );
  }

  return (
    <div className="products-grid">
      {products.map((product) => (
        <ProductCard key={product.id} product={product} />
      ))}
    </div>
  );
}

import React from 'react';
import { Search, SlidersHorizontal } from 'lucide-react';

export default function ProductFilter({
  searchTerm = '',
  onSearchChange,
  sortBy = 'default',
  onSortChange,
  priceFilter = 'all',
  onPriceFilterChange,
}) {
  return (
    <div className="catalog-controls">
      {/* Search Bar */}
      <div className="search-box">
        <Search className="search-icon" size={18} />
        <input
          type="text"
          className="search-input"
          placeholder="Tìm theo tên laptop, chip, RAM..."
          value={searchTerm}
          onChange={(e) => onSearchChange && onSearchChange(e.target.value)}
          aria-label="Tìm kiếm laptop"
        />
      </div>

      {/* Price Range Filter */}
      {onPriceFilterChange && (
        <select
          className="sort-select"
          value={priceFilter}
          onChange={(e) => onPriceFilterChange(e.target.value)}
          aria-label="Lọc theo mức giá"
        >
          <option value="all">Khoảng giá: Tất cả</option>
          <option value="under-35">Dưới 35 triệu</option>
          <option value="35-45">Từ 35 - 45 triệu</option>
          <option value="over-45">Trên 45 triệu</option>
        </select>
      )}

      {/* Sort Select */}
      <select
        className="sort-select"
        value={sortBy}
        onChange={(e) => onSortChange && onSortChange(e.target.value)}
        aria-label="Sắp xếp sản phẩm"
      >
        <option value="default">Sắp xếp: Mặc định</option>
        <option value="price-asc">Giá: Thấp đến cao</option>
        <option value="price-desc">Giá: Cao đến thấp</option>
        <option value="name-asc">Tên: A → Z</option>
      </select>
    </div>
  );
}

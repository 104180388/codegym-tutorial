import React from 'react';

const DEFAULT_CATEGORIES = [
  'Tất cả',
  'Apple',
  'Dell',
  'ASUS',
  'Lenovo',
  'Acer',
  'HP',
  'LG',
  'MSI',
];

export default function CategoryList({
  categories = DEFAULT_CATEGORIES,
  selectedCategory = 'Tất cả',
  onSelectCategory,
}) {
  return (
    <div className="filter-tabs" role="tablist" aria-label="Danh mục thương hiệu laptop">
      {categories.map((category) => {
        const isActive = selectedCategory === category;
        return (
          <button
            key={category}
            type="button"
            role="tab"
            aria-selected={isActive}
            className={`filter-tab ${isActive ? 'active' : ''}`}
            onClick={() => onSelectCategory && onSelectCategory(category)}
          >
            {category}
          </button>
        );
      })}
    </div>
  );
}

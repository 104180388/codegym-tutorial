import { Search, Filter, RotateCcw, BookMarked } from 'lucide-react';

export const BookSearch = ({
  searchTerm,
  onSearchChange,
  selectedCategory,
  onCategoryChange,
  categories,
  onReset
}) => {
  const hasActiveFilter = searchTerm.trim() !== '' || selectedCategory !== '';

  return (
    <div className="search-card">
      <div className="search-card-header">
        <div className="search-card-title">
          <Filter size={18} className="text-emerald" />
          <span>Bộ lọc & Tìm kiếm sách</span>
        </div>
        {hasActiveFilter && (
          <button
            type="button"
            className="btn-reset-filter"
            onClick={onReset}
            title="Đặt lại bộ lọc tìm kiếm"
          >
            <RotateCcw size={14} />
            <span>Xóa bộ lọc</span>
          </button>
        )}
      </div>

      <div className="search-inputs-grid">
        {/* Tìm kiếm theo tên sách (gần đúng) */}
        <div className="search-field-group">
          <label htmlFor="search-title" className="search-label">
            <Search size={15} />
            <span>Tìm kiếm theo tên sách (gần đúng)</span>
          </label>
          <div className="input-with-icon">
            <Search className="input-icon" size={18} />
            <input
              id="search-title"
              type="text"
              className="search-input"
              placeholder="Nhập tên sách cần tìm..."
              value={searchTerm}
              onChange={(e) => onSearchChange(e.target.value)}
              autoComplete="off"
            />
            {searchTerm && (
              <button
                type="button"
                className="input-clear-btn"
                onClick={() => onSearchChange('')}
                title="Xóa từ khóa"
              >
                ✕
              </button>
            )}
          </div>
          <span className="search-hint">Hỗ trợ tìm kiếm không phân biệt chữ hoa, chữ thường</span>
        </div>

        {/* Tìm kiếm theo thể loại (dropdown list) */}
        <div className="search-field-group">
          <label htmlFor="search-category" className="search-label">
            <BookMarked size={15} />
            <span>Lọc theo thể loại</span>
          </label>
          <div className="select-wrapper">
            <select
              id="search-category"
              className="search-select"
              value={selectedCategory}
              onChange={(e) => onCategoryChange(e.target.value)}
            >
              <option value="">-- Tất cả thể loại --</option>
              {categories.map((cat) => (
                <option key={cat.id} value={cat.id}>
                  {cat.name} ({cat.code || `Mã ${cat.id}`})
                </option>
              ))}
            </select>
          </div>
          <span className="search-hint">Danh sách thể loại nạp trực tiếp từ db.json</span>
        </div>
      </div>
    </div>
  );
};

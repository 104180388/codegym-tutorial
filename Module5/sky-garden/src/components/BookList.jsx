import { useState, useMemo } from 'react';
import { 
  ArrowUpDown, 
  BookCopy, 
  Calendar, 
  Bookmark, 
  Layers, 
  AlertCircle,
  Hash
} from 'lucide-react';
import { formatDateToDDMMYYYY } from '../utils/formatters';
import { BookSearch } from './BookSearch';

export const BookList = ({ books, categories, loading, error, onRefresh }) => {
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('');
  
  // Sort state: 'asc' (tăng dần - trang 1) or 'desc' (giảm dần - bảng rubric trang 2)
  // Default to 'asc' as per Yêu cầu 1 (Page 1)
  const [sortOrder, setSortOrder] = useState('asc');

  // Map categoryId to Category Object for fast lookup
  const categoryMap = useMemo(() => {
    const map = new Map();
    categories.forEach(cat => {
      map.set(String(cat.id), cat);
    });
    return map;
  }, [categories]);

  // Filter books according to:
  // 1. Partial/fuzzy match by title (case-insensitive)
  // 2. Exact match by categoryId
  const filteredAndSortedBooks = useMemo(() => {
    return books
      .filter((book) => {
        // Search by title (gần đúng)
        const matchTitle = !searchTerm.trim() || 
          book.title.toLowerCase().includes(searchTerm.trim().toLowerCase());

        // Search by category
        const matchCategory = !selectedCategory || 
          String(book.categoryId) === String(selectedCategory);

        return matchTitle && matchCategory;
      })
      .sort((a, b) => {
        const qtyA = Number(a.quantity) || 0;
        const qtyB = Number(b.quantity) || 0;
        return sortOrder === 'asc' ? qtyA - qtyB : qtyB - qtyA;
      });
  }, [books, searchTerm, selectedCategory, sortOrder]);

  const handleResetFilters = () => {
    setSearchTerm('');
    setSelectedCategory('');
  };

  const toggleSortOrder = () => {
    setSortOrder((prev) => (prev === 'asc' ? 'desc' : 'asc'));
  };

  // Calculate stats
  const totalCopies = useMemo(() => {
    return books.reduce((sum, b) => sum + (Number(b.quantity) || 0), 0);
  }, [books]);

  return (
    <div className="book-list-container">
      {/* Page Header */}
      <div className="page-header">
        <div className="page-title-group">
          <h1 className="page-title">
            <BookCopy className="text-emerald" size={28} />
            Quản lý Sách SkyGarden
          </h1>
          <p className="page-subtitle">
            Hệ thống tra cứu, kiểm kê và cập nhật thông tin sách thư viện
          </p>
        </div>

      </div>

      {/* Quick Library Summary Bar */}
      <div className="stats-grid">
        <div className="stat-card">
          <div className="stat-icon-box bg-emerald-light">
            <BookCopy size={22} className="text-emerald" />
          </div>
          <div className="stat-info">
            <span className="stat-label">Tổng đầu sách</span>
            <span className="stat-value">{books.length}</span>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon-box bg-blue-light">
            <Layers size={22} className="text-blue" />
          </div>
          <div className="stat-info">
            <span className="stat-label">Tổng số lượng bản sách</span>
            <span className="stat-value">{totalCopies}</span>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon-box bg-purple-light">
            <Bookmark size={22} className="text-purple" />
          </div>
          <div className="stat-info">
            <span className="stat-label">Số thể loại sách</span>
            <span className="stat-value">{categories.length}</span>
          </div>
        </div>
      </div>

      {/* Search & Filter Component (Yêu cầu 3) */}
      <BookSearch
        searchTerm={searchTerm}
        onSearchChange={setSearchTerm}
        selectedCategory={selectedCategory}
        onCategoryChange={setSelectedCategory}
        categories={categories}
        onReset={handleResetFilters}
      />

      {/* Control bar: Sorting controls & record count */}
      <div className="table-controls-bar">
        <div className="results-count">
          Hiển thị <strong>{filteredAndSortedBooks.length}</strong> / {books.length} sách
          {(searchTerm || selectedCategory) && (
            <span className="filtered-tag">Đã áp dụng bộ lọc</span>
          )}
        </div>
        
      </div>

      {/* Loading & Error States */}
      {loading && (
        <div className="loading-state">
          <div className="loading-spinner"></div>
          <p>Đang tải dữ liệu sách từ db.json...</p>
        </div>
      )}

      {error && !loading && (
        <div className="error-banner">
          <AlertCircle size={20} />
          <span>{error}</span>
          <button type="button" onClick={onRefresh} className="btn-retry">
            Thử lại
          </button>
        </div>
      )}

      {/* 
        Yêu cầu 3:
        "Tìm kiếm nhưng không có thông tin thì hiển thị thông báo “Không có thông tin sách này” lên màn hình cho người dùng. 
         Lưu ý: Thông báo dạng text trên màn hình thay thế cho table (5 điểm)."
      */}
      {!loading && filteredAndSortedBooks.length === 0 ? (
        <div className="empty-search-state" role="alert">
          <div className="empty-search-icon">
            <AlertCircle size={44} className="text-amber" />
          </div>
          {/* Exact text required by the exam */}
          <h2 className="empty-search-text">Không có thông tin sách này</h2>
          <p className="empty-search-description">
            Không tìm thấy quyển sách nào khớp với từ khóa
            {searchTerm && <strong> &quot;{searchTerm}&quot;</strong>}
            {selectedCategory && (
              <span> trong thể loại <strong>&quot;{categoryMap.get(String(selectedCategory))?.name || selectedCategory}&quot;</strong></span>
            )}.
          </p>
          <button
            type="button"
            className="btn-clear-empty"
            onClick={handleResetFilters}
          >
            Xóa bộ lọc để hiển thị toàn bộ sách
          </button>
        </div>
      ) : (
        !loading && (
          <div className="table-responsive">
            <table className="books-table" id="books-table">
              <thead>
                <tr>
                  <th className="th-stt">
                    <span className="th-content">
                      <Hash size={14} />
                      STT
                    </span>
                  </th>
                  <th className="th-code">Mã sách</th>
                  <th className="th-title">Tên sách</th>
                  <th className="th-category">Thể loại</th>
                  <th className="th-date">
                    <span className="th-content">
                      <Calendar size={14} />
                      Ngày nhập (dd/MM/yyyy)
                    </span>
                  </th>
                  <th className="th-quantity cursor-pointer" onClick={toggleSortOrder}>
                    <div className="th-sortable">
                      <span>Số lượng</span>
                      <ArrowUpDown size={14} className="sort-icon-indicator" />
                      <span className="sort-order-badge">
                        {sortOrder === 'asc' ? '▲ Tăng' : '▼ Giảm'}
                      </span>
                    </div>
                  </th>
                </tr>
              </thead>
              <tbody>
                {filteredAndSortedBooks.map((book, index) => {
                  const cat = categoryMap.get(String(book.categoryId));
                  return (
                    <tr key={book.id || book.code || index} className="book-row">
                      <td className="td-stt">{index + 1}</td>
                      <td className="td-code">
                        <span className="book-code-badge">{book.code}</span>
                      </td>
                      <td className="td-title">
                        <div className="book-title-cell">
                          <span className="book-title-text">{book.title}</span>
                        </div>
                      </td>
                      <td className="td-category">
                        <span className="category-pill">
                          {cat ? cat.name : `Thể loại #${book.categoryId}`}
                        </span>
                      </td>
                      <td className="td-date">
                        {/* Yêu cầu 1: Ngày nhập phải đúng định dạng dd/MM/yyyy (5 điểm) */}
                        <span className="date-badge">
                          {formatDateToDDMMYYYY(book.dateAdded)}
                        </span>
                      </td>
                      <td className="td-quantity">
                        <div className="quantity-badge-container">
                          <span
                            className={`quantity-pill ${
                              Number(book.quantity) > 20
                                ? 'qty-high'
                                : Number(book.quantity) > 10
                                ? 'qty-medium'
                                : 'qty-low'
                            }`}
                          >
                            {book.quantity} quyển
                          </span>
                        </div>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        )
      )}
    </div>
  );
};

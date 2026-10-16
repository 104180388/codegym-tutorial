import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { 
  BookPlus, 
  ArrowLeft, 
  Save, 
  AlertCircle, 
  Sparkles,
  Calendar,
  Layers,
  Tag,
  FileText
} from 'lucide-react';
import { getTodayDateString, validateBook } from '../utils/formatters';

export const BookForm = ({ categories, onAddBook, existingBooks = [] }) => {
  const navigate = useNavigate();
  const today = getTodayDateString();

  // Suggest next code based on existing books
  const suggestNextCode = () => {
    let maxNum = 0;
    existingBooks.forEach(b => {
      const match = String(b.code).match(/\d+/);
      if (match) {
        const num = parseInt(match[0], 10);
        if (num > maxNum) maxNum = num;
      }
    });
    const nextNum = String(maxNum + 1).padStart(4, '0');
    return `BO-${nextNum}`;
  };

  const [formData, setFormData] = useState({
    code: '',
    title: '',
    categoryId: '',
    dateAdded: today,
    quantity: ''
  });

  const [errors, setErrors] = useState({});
  const [submitting, setSubmitting] = useState(false);
  const [touched, setTouched] = useState({});

  const handleChange = (e) => {
    const { name, value } = e.target;
    const updated = { ...formData, [name]: value };
    setFormData(updated);

    // If already touched, validate this field dynamically
    if (touched[name]) {
      const validationErrors = validateBook(updated);
      setErrors((prev) => ({
        ...prev,
        [name]: validationErrors[name] || ''
      }));
    }
  };

  const handleBlur = (e) => {
    const { name } = e.target;
    setTouched((prev) => ({ ...prev, [name]: true }));
    const validationErrors = validateBook(formData);
    setErrors((prev) => ({
      ...prev,
      [name]: validationErrors[name] || ''
    }));
  };

  const handleApplySuggestedCode = () => {
    const code = suggestNextCode();
    setFormData((prev) => ({ ...prev, code }));
    setTouched((prev) => ({ ...prev, code: true }));
    setErrors((prev) => {
      const newErrors = { ...prev };
      delete newErrors.code;
      return newErrors;
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    // Mark all fields as touched
    setTouched({
      code: true,
      title: true,
      categoryId: true,
      dateAdded: true,
      quantity: true
    });

    const validationErrors = validateBook(formData);
    setErrors(validationErrors);

    // If there are errors, stop
    if (Object.keys(validationErrors).length > 0) {
      return;
    }

    try {
      setSubmitting(true);
      await onAddBook(formData);
      // Requirement: Sau khi thêm mới thành công thì hiển thị thông báo cho người dùng và quay về màn hình danh sách (5 điểm)
      navigate('/', { state: { successMessage: `Đã thêm sách "${formData.title}" thành công!` } });
    } catch (err) {
      console.error('Error adding book:', err);
      setErrors((prev) => ({
        ...prev,
        form: 'Không thể thêm sách. Vui lòng kiểm tra lại kết nối JSON-Server.'
      }));
    } finally {
      setSubmitting(false);
    }
  };

  const titleLength = formData.title.trim().length;

  return (
    <div className="form-page-container">
      {/* Header with back button */}
      <div className="form-header-bar">
        <Link to="/" className="btn-back">
          <ArrowLeft size={18} />
          <span>Quay lại danh sách</span>
        </Link>
        <div className="form-header-title">
          <h1 className="form-title">
            <BookPlus size={24} className="text-emerald" />
            Thêm mới sách vào Thư viện
          </h1>
        </div>
      </div>

      <div className="form-card-container">
        {errors.form && (
          <div className="form-global-error">
            <AlertCircle size={18} />
            <span>{errors.form}</span>
          </div>
        )}

        <form onSubmit={handleSubmit} noValidate className="book-form">
          {/* Field 1: Mã sách */}
          <div className={`form-group ${errors.code && touched.code ? 'has-error' : ''}`}>
            <div className="label-row">
              <label htmlFor="code" className="form-label required">
                <Tag size={15} />
                <span>Mã sách</span>
              </label>
              <button
                type="button"
                className="btn-suggestion"
                onClick={handleApplySuggestedCode}
                title="Tự động điền mã tiếp theo"
              >
                <Sparkles size={12} />
                <span>Gợi ý mã: {suggestNextCode()}</span>
              </button>
            </div>
            <div className="input-field-wrapper">
              <input
                id="code"
                name="code"
                type="text"
                className="form-input"
                placeholder="VD: BO-0007 hoặc BO - 0007"
                value={formData.code}
                onChange={handleChange}
                onBlur={handleBlur}
                autoFocus
              />
            </div>
            {errors.code && touched.code && (
              <div className="field-error-message">
                <AlertCircle size={14} />
                <span>{errors.code}</span>
              </div>
            )}
          </div>

          {/* Field 2: Tên sách */}
          <div className={`form-group ${errors.title && touched.title ? 'has-error' : ''}`}>
            <div className="label-row">
              <label htmlFor="title" className="form-label required">
                <FileText size={15} />
                <span>Tên sách</span>
              </label>
              <span className={`char-counter ${titleLength > 100 ? 'text-danger' : ''}`}>
                {titleLength} / 100 ký tự
              </span>
            </div>
            <div className="input-field-wrapper">
              <input
                id="title"
                name="title"
                type="text"
                maxLength={120}
                className="form-input"
                placeholder="Nhập tên sách (tối đa 100 ký tự)..."
                value={formData.title}
                onChange={handleChange}
                onBlur={handleBlur}
              />
            </div>
            {errors.title && touched.title && (
              <div className="field-error-message">
                <AlertCircle size={14} />
                <span>{errors.title}</span>
              </div>
            )}
          </div>

          {/* Field 3: Thể loại (Dropdown list từ db.json) */}
          <div className={`form-group ${errors.categoryId && touched.categoryId ? 'has-error' : ''}`}>
            <label htmlFor="categoryId" className="form-label required">
              <Layers size={15} />
              <span>Thể loại sách</span>
            </label>
            <div className="select-field-wrapper">
              <select
                id="categoryId"
                name="categoryId"
                className="form-select"
                value={formData.categoryId}
                onChange={handleChange}
                onBlur={handleBlur}
              >
                <option value="">-- Chọn thể loại sách --</option>
                {categories.map((cat) => (
                  <option key={cat.id} value={cat.id}>
                    {cat.name} ({cat.code || `Mã ${cat.id}`})
                  </option>
                ))}
              </select>
            </div>
            {errors.categoryId && touched.categoryId && (
              <div className="field-error-message">
                <AlertCircle size={14} />
                <span>{errors.categoryId}</span>
              </div>
            )}
          </div>

          {/* Field 4 & 5 Grid: Ngày nhập & Số lượng */}
          <div className="form-row-two-cols">
            {/* Field 4: Ngày nhập sách */}
            <div className={`form-group ${errors.dateAdded && touched.dateAdded ? 'has-error' : ''}`}>
              <label htmlFor="dateAdded" className="form-label required">
                <Calendar size={15} />
                <span>Ngày nhập sách</span>
              </label>
              <div className="input-field-wrapper">
                <input
                  id="dateAdded"
                  name="dateAdded"
                  type="date"
                  max={today}
                  className="form-input"
                  value={formData.dateAdded}
                  onChange={handleChange}
                  onBlur={handleBlur}
                />
              </div>
              {errors.dateAdded && touched.dateAdded && (
                <div className="field-error-message">
                  <AlertCircle size={14} />
                  <span>{errors.dateAdded}</span>
                </div>
              )}
            </div>

            {/* Field 5: Số lượng */}
            <div className={`form-group ${errors.quantity && touched.quantity ? 'has-error' : ''}`}>
              <label htmlFor="quantity" className="form-label required">
                <Layers size={15} />
                <span>Số lượng sách</span>
              </label>
              <div className="input-field-wrapper">
                <input
                  id="quantity"
                  name="quantity"
                  type="number"
                  min="1"
                  step="1"
                  className="form-input"
                  placeholder="Ví dụ: 10"
                  value={formData.quantity}
                  onChange={handleChange}
                  onBlur={handleBlur}
                />
              </div>
              {errors.quantity && touched.quantity && (
                <div className="field-error-message">
                  <AlertCircle size={14} />
                  <span>{errors.quantity}</span>
                </div>
              )}
            </div>
          </div>

          {/* Form Actions */}
          <div className="form-actions-bar">
            <Link to="/" className="btn-cancel">
              Hủy bỏ
            </Link>
            <button
              type="submit"
              disabled={submitting}
              className="btn-submit-primary"
            >
              {submitting ? (
                <>
                  <div className="btn-spinner"></div>
                  <span>Đang lưu...</span>
                </>
              ) : (
                <>
                  <Save size={18} />
                  <span>Lưu sách mới</span>
                </>
              )}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

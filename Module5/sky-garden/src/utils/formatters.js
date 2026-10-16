/**
 * Utility functions for formatting and validation in SkyGarden Library
 */

/**
 * Format a date string (YYYY-MM-DD or ISO) into dd/MM/yyyy
 * @param {string|Date} dateInput 
 * @returns {string} Formatted date (dd/MM/yyyy)
 */
export const formatDateToDDMMYYYY = (dateInput) => {
  if (!dateInput) return '';
  
  // If it's already in dd/MM/yyyy format, return as is
  if (typeof dateInput === 'string' && /^\d{2}\/\d{2}\/\d{4}$/.test(dateInput)) {
    return dateInput;
  }

  // Parse date
  const date = new Date(dateInput);
  if (isNaN(date.getTime())) {
    // If string is YYYY-MM-DD
    const parts = String(dateInput).split('-');
    if (parts.length === 3) {
      const [year, month, day] = parts;
      return `${day.padStart(2, '0')}/${month.padStart(2, '0')}/${year}`;
    }
    return String(dateInput);
  }

  const day = String(date.getDate()).padStart(2, '0');
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const year = date.getFullYear();

  return `${day}/${month}/${year}`;
};

/**
 * Get today's date in YYYY-MM-DD format (for input[type="date"] max attribute)
 * @returns {string} YYYY-MM-DD
 */
export const getTodayDateString = () => {
  const today = new Date();
  const year = today.getFullYear();
  const month = String(today.getMonth() + 1).padStart(2, '0');
  const day = String(today.getDate()).padStart(2, '0');
  return `${year}-${month}-${day}`;
};

/**
 * Validates book code format BO-XXXX or BO - XXXX (with 4 digits)
 * @param {string} code 
 * @returns {boolean}
 */
export const isValidBookCode = (code) => {
  if (!code) return false;
  // Match BO-XXXX or BO - XXXX where X is digit (4 digits)
  // Supports regular hyphen -, en-dash –, and em-dash —
  const regex = /^BO\s*[-–—]\s*\d{4}$/i;
  return regex.test(code.trim());
};

/**
 * Validate book input form data
 * @param {Object} formData 
 * @returns {Object} errors object with field names as keys
 */
export const validateBook = (formData) => {
  const errors = {};

  // 1. Mã sách đúng định dạng BO – XXXX (với X là các số)
  if (!formData.code || !formData.code.trim()) {
    errors.code = 'Mã sách không được để trống';
  } else if (!isValidBookCode(formData.code)) {
    errors.code = 'Mã sách phải có định dạng BO - XXXX hoặc BO-XXXX (với X là 4 chữ số, VD: BO-0001)';
  }

  // 2. Tên sách không dài quá 100 ký tự
  if (!formData.title || !formData.title.trim()) {
    errors.title = 'Tên sách không được để trống';
  } else if (formData.title.trim().length > 100) {
    errors.title = `Tên sách không được vượt quá 100 ký tự (hiện tại: ${formData.title.trim().length} ký tự)`;
  }

  // 3. Thể loại được chọn từ dropdown list
  if (!formData.categoryId || formData.categoryId === '') {
    errors.categoryId = 'Vui lòng chọn thể loại sách';
  }

  // 4. Ngày nhập sách không được lớn hơn ngày hiện tại
  if (!formData.dateAdded) {
    errors.dateAdded = 'Vui lòng chọn ngày nhập sách';
  } else {
    const selectedDate = new Date(formData.dateAdded);
    selectedDate.setHours(0, 0, 0, 0);

    const today = new Date();
    today.setHours(0, 0, 0, 0);

    if (isNaN(selectedDate.getTime())) {
      errors.dateAdded = 'Ngày nhập sách không hợp lệ';
    } else if (selectedDate > today) {
      errors.dateAdded = 'Ngày nhập sách không được lớn hơn ngày hiện tại';
    }
  }

  // 5. Số lượng sách phải là số nguyên lớn hơn 0
  const qtyStr = String(formData.quantity ?? '').trim();
  if (qtyStr === '') {
    errors.quantity = 'Số lượng không được để trống';
  } else {
    const qtyNum = Number(qtyStr);
    if (!Number.isInteger(qtyNum) || qtyNum <= 0) {
      errors.quantity = 'Số lượng sách phải là số nguyên lớn hơn 0';
    }
  }

  return errors;
};

import axios from 'axios';

const API_BASE_URL = 'http://localhost:3001';

// Initial fallback data matching db.json
const INITIAL_CATEGORIES = [
  {
    id: "1",
    code: "CAT-01",
    name: "Khoa học - Công nghệ",
    description: "Sách về khoa học máy tính, trí tuệ nhân tạo, thiên văn và kỹ thuật"
  },
  {
    id: "2",
    code: "CAT-02",
    name: "Văn học nghệ thuật",
    description: "Tiểu thuyết, truyện ngắn, thơ ca và các tác phẩm văn học kinh điển"
  },
  {
    id: "3",
    code: "CAT-03",
    name: "Kinh tế - Khởi nghiệp",
    description: "Quản trị kinh doanh, tài chính, đầu tư và kỹ năng khởi nghiệp"
  },
  {
    id: "4",
    code: "CAT-04",
    name: "Kỹ năng sống - Tâm lý",
    description: "Phát triển bản thân, tâm lý học ứng dụng và kỹ năng giao tiếp"
  },
  {
    id: "5",
    code: "CAT-05",
    name: "Lịch sử - Triết học",
    description: "Khảo cứu lịch sử Việt Nam, thế giới và các tư tưởng triết học"
  }
];

const INITIAL_BOOKS = [
  {
    id: 1,
    code: "BO-0001",
    title: "Clean Code - Nghệ thuật viết mã sạch",
    categoryId: "1",
    dateAdded: "2024-01-15",
    quantity: 18
  },
  {
    id: 2,
    code: "BO-0002",
    title: "Đắc Nhân Tâm",
    categoryId: "4",
    dateAdded: "2024-02-10",
    quantity: 35
  },
  {
    id: 3,
    code: "BO-0003",
    title: "Nhà Giả Kim",
    categoryId: "2",
    dateAdded: "2023-11-20",
    quantity: 12
  },
  {
    id: 4,
    code: "BO-0004",
    title: "Kinh tế học vi mô căn bản",
    categoryId: "3",
    dateAdded: "2024-03-05",
    quantity: 8
  },
  {
    id: 5,
    code: "BO-0005",
    title: "Lược sử loài người (Sapiens)",
    categoryId: "5",
    dateAdded: "2024-04-12",
    quantity: 25
  },
  {
    id: 6,
    code: "BO-0006",
    title: "Lập trình ReactJS và hệ sinh thái Front-End",
    categoryId: "1",
    dateAdded: "2024-05-18",
    quantity: 40
  }
];

// Helper to interact with local storage if json-server is not reachable
const getLocalData = (key, initial) => {
  try {
    const data = localStorage.getItem(`skygarden_${key}`);
    if (data) return JSON.parse(data);
    localStorage.setItem(`skygarden_${key}`, JSON.stringify(initial));
    return initial;
  } catch {
    return initial;
  }
};

const setLocalData = (key, data) => {
  try {
    localStorage.setItem(`skygarden_${key}`, JSON.stringify(data));
  } catch (err) {
    console.error('Failed to save to localStorage', err);
  }
};

/**
 * Get all categories
 */
export const getCategories = async () => {
  try {
    const res = await axios.get(`${API_BASE_URL}/categories`, { timeout: 2000 });
    // Keep local storage in sync
    setLocalData('categories', res.data);
    return res.data;
  } catch (error) {
    console.warn('JSON-Server not reachable, falling back to local storage for categories:', error.message);
    return getLocalData('categories', INITIAL_CATEGORIES);
  }
};

/**
 * Get all books
 */
export const getBooks = async () => {
  try {
    const res = await axios.get(`${API_BASE_URL}/books`, { timeout: 2000 });
    setLocalData('books', res.data);
    return res.data;
  } catch (error) {
    console.warn('JSON-Server not reachable, falling back to local storage for books:', error.message);
    return getLocalData('books', INITIAL_BOOKS);
  }
};

/**
 * Add a new book
 * @param {Object} bookData 
 */
export const addBook = async (bookData) => {
  const newBook = {
    ...bookData,
    quantity: Number(bookData.quantity)
  };

  try {
    const res = await axios.post(`${API_BASE_URL}/books`, newBook, { timeout: 2000 });
    // Sync local
    const current = getLocalData('books', INITIAL_BOOKS);
    setLocalData('books', [...current, res.data]);
    return res.data;
  } catch (error) {
    console.warn('JSON-Server not reachable, persisting book to local storage:', error.message);
    const current = getLocalData('books', INITIAL_BOOKS);
    const createdBook = {
      id: Date.now(),
      ...newBook
    };
    const updated = [...current, createdBook];
    setLocalData('books', updated);
    return createdBook;
  }
};

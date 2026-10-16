import { useState, useEffect, useCallback } from 'react';
import { BrowserRouter, Routes, Route, useLocation } from 'react-router-dom';
import { Navbar } from './components/Navbar';
import { BookList } from './components/BookList';
import { BookForm } from './components/BookForm';
import { Toast } from './components/Toast';
import { getBooks, getCategories, addBook } from './services/bookService';
import './App.css';

function AppContent() {
  const [books, setBooks] = useState([]);
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [toast, setToast] = useState({ show: false, message: '', type: 'success' });

  const location = useLocation();

  // Load data from JSON-server / db.json
  const refreshData = useCallback(async () => {
    try {
      setLoading(true);
      setError(null);
      const [catsData, booksData] = await Promise.all([
        getCategories(),
        getBooks()
      ]);
      setCategories(catsData || []);
      setBooks(booksData || []);
    } catch (err) {
      console.error('Lỗi khi tải dữ liệu:', err);
      setError('Không thể nạp dữ liệu từ hệ thống. Đang sử dụng bản lưu tạm.');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    let isCancelled = false;
    const initData = async () => {
      try {
        const [catsData, booksData] = await Promise.all([
          getCategories(),
          getBooks()
        ]);
        if (!isCancelled) {
          setCategories(catsData || []);
          setBooks(booksData || []);
          setLoading(false);
        }
      } catch (err) {
        if (!isCancelled) {
          console.error('Lỗi khi tải dữ liệu:', err);
          setError('Không thể nạp dữ liệu từ hệ thống. Đang sử dụng bản lưu tạm.');
          setLoading(false);
        }
      }
    };

    initData();
    return () => {
      isCancelled = true;
    };
  }, []);

  // Listen for navigation state (e.g. success message after adding a book)
  useEffect(() => {
    if (location.state?.successMessage) {
      const msg = location.state.successMessage;
      window.history.replaceState({}, document.title);
      const timer = setTimeout(() => {
        setToast({
          show: true,
          message: msg,
          type: 'success'
        });
      }, 0);
      return () => clearTimeout(timer);
    }
  }, [location.state]);

  // Handle adding a new book
  const handleAddBook = async (bookData) => {
    const createdBook = await addBook(bookData);
    setBooks((prev) => [...prev, createdBook]);
    return createdBook;
  };

  const handleCloseToast = () => {
    setToast((prev) => ({ ...prev, show: false }));
  };

  return (
    <div className="app-layout">
      {/* Toast Notification */}
      {toast.show && (
        <Toast
          message={toast.message}
          type={toast.type}
          onClose={handleCloseToast}
        />
      )}

      {/* Main Navbar */}
      <Navbar bookCount={books.length} />

      {/* Main Content Area */}
      <main className="main-content">
        <Routes>
          <Route
            path="/"
            element={
              <BookList
                books={books}
                categories={categories}
                loading={loading}
                error={error}
                onRefresh={refreshData}
              />
            }
          />
          <Route
            path="/add"
            element={
              <BookForm
                categories={categories}
                onAddBook={handleAddBook}
                existingBooks={books}
              />
            }
          />
          <Route
            path="*"
            element={
              <BookList
                books={books}
                categories={categories}
                loading={loading}
                error={error}
                onRefresh={refreshData}
              />
            }
          />
        </Routes>
      </main>

      {/* Footer */}
      <footer className="app-footer">
        <div className="footer-inner">
          <div className="footer-brand">
            <strong>SkyGarden Library</strong> — Hệ thống quản lý thư viện thông minh
          </div>
        </div>
      </footer>
    </div>
  );
}

export default function App() {
  return (
    <BrowserRouter>
      <AppContent />
    </BrowserRouter>
  );
}

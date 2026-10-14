import React, { useEffect } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { CheckCircle2, AlertCircle, Info, X } from 'lucide-react';
import { selectToast, hideToast } from '../redux/uiSlice';

export default function Toast() {
  const dispatch = useDispatch();
  const toast = useSelector(selectToast);

  useEffect(() => {
    if (!toast) return;

    const timer = setTimeout(() => {
      dispatch(hideToast());
    }, 3200);

    return () => clearTimeout(timer);
  }, [toast, dispatch]);

  if (!toast) return null;

  const isSuccess = toast.type === 'success';
  const isError = toast.type === 'error';

  return (
    <div
      className={`toast-container ${isSuccess ? 'toast-success' : ''} ${isError ? 'toast-error' : ''}`}
      role="status"
    >
      {isSuccess && <CheckCircle2 size={20} color="#10b981" />}
      {isError && <AlertCircle size={20} color="#f43f5e" />}
      {!isSuccess && !isError && <Info size={20} color="#6366f1" />}

      <span>{toast.message}</span>

      <button
        onClick={() => dispatch(hideToast())}
        style={{
          background: 'transparent',
          border: 'none',
          color: 'var(--text-muted)',
          cursor: 'pointer',
          padding: '0.2rem',
          marginLeft: '0.5rem',
          display: 'flex',
          alignItems: 'center',
        }}
        aria-label="Đóng thông báo"
      >
        <X size={16} />
      </button>
    </div>
  );
}

import { useEffect } from 'react';
import { CheckCircle2, XCircle, Info, X } from 'lucide-react';

export const Toast = ({ message, type = 'success', onClose, duration = 3500 }) => {
  useEffect(() => {
    if (!message) return;
    const timer = setTimeout(() => {
      onClose();
    }, duration);
    return () => clearTimeout(timer);
  }, [message, duration, onClose]);

  if (!message) return null;

  const isSuccess = type === 'success';
  const isError = type === 'error';

  return (
    <div className={`toast-notification ${type}`}>
      <div className="toast-icon">
        {isSuccess && <CheckCircle2 size={22} className="text-emerald" />}
        {isError && <XCircle size={22} className="text-rose" />}
        {!isSuccess && !isError && <Info size={22} className="text-sky" />}
      </div>
      <div className="toast-content">
        <p className="toast-title">{isSuccess ? 'Thành công!' : isError ? 'Đã xảy ra lỗi' : 'Thông báo'}</p>
        <p className="toast-message">{message}</p>
      </div>
      <button type="button" className="toast-close" onClick={onClose} aria-label="Đóng">
        <X size={16} />
      </button>
      <div className="toast-progress"></div>
    </div>
  );
};

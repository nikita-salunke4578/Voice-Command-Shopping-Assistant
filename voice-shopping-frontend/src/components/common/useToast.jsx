import { useState, useCallback } from 'react';
import Toast from './Toast';

// Global toast state hook
let addToastGlobal = null;

export function useToast() {
  const [toasts, setToasts] = useState([]);

  const addToast = useCallback((message, type = 'success', duration = 3000) => {
    const id = Date.now() + Math.random();
    setToasts((prev) => [...prev, { id, message, type, duration }]);
  }, []);

  const removeToast = useCallback((id) => {
    setToasts((prev) => prev.filter((t) => t.id !== id));
  }, []);

  // Expose globally
  addToastGlobal = addToast;

  const ToastContainer = () => (
    <div className="toast-container">
      {toasts.map((toast) => (
        <Toast
          key={toast.id}
          message={toast.message}
          type={toast.type}
          duration={toast.duration}
          onClose={() => removeToast(toast.id)}
        />
      ))}
    </div>
  );

  return { addToast, ToastContainer };
}

// Utility to trigger toast from anywhere
export const toast = {
  success: (msg) => addToastGlobal?.(msg, 'success'),
  error: (msg) => addToastGlobal?.(msg, 'error'),
  info: (msg) => addToastGlobal?.(msg, 'info'),
  warning: (msg) => addToastGlobal?.(msg, 'warning'),
};

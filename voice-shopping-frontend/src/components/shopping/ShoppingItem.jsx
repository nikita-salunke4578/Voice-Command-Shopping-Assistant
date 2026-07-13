import { useState } from 'react';

const FALLBACK_IMAGE = "data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='160' height='160' viewBox='0 0 160 160'%3E%3Crect width='160' height='160' fill='%23202a3d'/%3E%3Cpath d='M45 55h70l-6 65H51z' fill='%237c5cff'/%3E%3Cpath d='M63 55c0-20 34-20 34 0' fill='none' stroke='%23d9d2ff' stroke-width='8'/%3E%3C/svg%3E";

export default function ShoppingItem({ item, onToggle, onDelete, onUpdate }) {
  const [editQty, setEditQty] = useState(item.quantity);

  const handleQtyChange = (newQty) => {
    if (newQty < 1) return;
    setEditQty(newQty);
    onUpdate(item.id, { quantity: newQty });
  };

  const categoryIcons = {
    'Dairy': '🥛',
    'Fruits': '🍎',
    'Vegetables': '🥬',
    'Bakery': '🍞',
    'Beverages': '🥤',
    'Snacks': '🍿',
    'Meat': '🥩',
    'Frozen': '🧊',
    'Grains': '🌾',
    'Household': '🧹',
    'Personal Care': '🧴',
    'Spices': '🌶️',
  };

  const categoryImages = {
    'Dairy': 'https://images.unsplash.com/photo-1550583724-b2692b85b150?auto=format&fit=crop&w=150&q=80',
    'Fruits': 'https://images.unsplash.com/photo-1619566636858-adf3ef46400b?auto=format&fit=crop&w=150&q=80',
    'Vegetables': 'https://images.unsplash.com/photo-1566385101042-1a0aa0c1268c?auto=format&fit=crop&w=150&q=80',
    'Bakery': 'https://images.unsplash.com/photo-1509440159596-0249088772ff?auto=format&fit=crop&w=150&q=80',
    'Beverages': 'https://images.unsplash.com/photo-1534080391025-097b03b77385?auto=format&fit=crop&w=150&q=80',
    'Snacks': 'https://images.unsplash.com/photo-1599490659213-e2b9527b0876?auto=format&fit=crop&w=150&q=80',
    'Meat': 'https://images.unsplash.com/photo-1607623814075-e51df1bdc82f?auto=format&fit=crop&w=150&q=80',
    'Frozen': 'https://images.unsplash.com/photo-1516559828984-fb3b9952c64b?auto=format&fit=crop&w=150&q=80',
    'Grains': 'https://images.unsplash.com/photo-1574316071802-0d684efa7bf5?auto=format&fit=crop&w=150&q=80',
    'Household': 'https://images.unsplash.com/photo-1581578731548-c64695cc6952?auto=format&fit=crop&w=150&q=80',
    'Personal Care': 'https://images.unsplash.com/photo-1556228720-195a672e8a03?auto=format&fit=crop&w=150&q=80',
    'Spices': 'https://images.unsplash.com/photo-1596797038530-2c107229654b?auto=format&fit=crop&w=150&q=80',
  };

  const imgUrl = item.imageUrl || categoryImages[item.category] || 'https://images.unsplash.com/photo-1542838132-92c53300491e?auto=format&fit=crop&w=150&q=80';

  return (
    <div className={`shopping-item animate-fade-in ${item.isChecked ? 'checked' : ''}`}>
      <button
        className={`item-checkbox ${item.isChecked ? 'checked' : ''}`}
        onClick={() => onToggle(item.id)}
        disabled={item.isChecked}
        title={item.isChecked ? 'Purchased items cannot be unchecked' : 'Mark as purchased'}
        aria-label={item.isChecked ? 'Purchased item' : 'Mark item as purchased'}
      >
        {item.isChecked && <span className="checkmark">✓</span>}
      </button>

      <img 
        src={imgUrl} 
        onError={(event) => {
          event.currentTarget.onerror = null;
          event.currentTarget.src = FALLBACK_IMAGE;
        }}
        alt={item.itemName} 
        style={{ width: '38px', height: '38px', borderRadius: 'var(--radius-sm)', objectFit: 'cover', border: '1px solid rgba(255,255,255,0.08)', flexShrink: 0 }} 
      />

      <div className="item-info">
        <span className="item-name">{item.itemName}</span>
        <div className="item-meta">
          {item.category && (
            <span className="item-category">
              {categoryIcons[item.category] || '📦'} {item.category}
            </span>
          )}
          {item.estimatedPrice && (
            <span className="item-price">₹{Number(item.estimatedPrice).toFixed(0)}</span>
          )}
        </div>
      </div>

      <div className="item-quantity">
        <button className="qty-btn" onClick={() => handleQtyChange(editQty - 1)}>−</button>
        <span className="qty-value">{item.quantity}</span>
        <button className="qty-btn" onClick={() => handleQtyChange(editQty + 1)}>+</button>
        {item.unit && <span className="qty-unit">{item.unit}</span>}
      </div>

      <button
        className="item-delete"
        onClick={() => onDelete(item.id)}
        aria-label="Remove item"
      >
        ✕
      </button>
    </div>
  );
}

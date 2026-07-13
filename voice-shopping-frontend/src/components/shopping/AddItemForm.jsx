import { useState } from 'react';

export default function AddItemForm({ onAdd }) {
  const [itemName, setItemName] = useState('');
  const [quantity, setQuantity] = useState(1);
  const [isExpanded, setIsExpanded] = useState(false);

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!itemName.trim()) return;

    onAdd({
      itemName: itemName.trim(),
      quantity: quantity,
    });

    setItemName('');
    setQuantity(1);
    setIsExpanded(false);
  };

  const handleKeyDown = (e) => {
    if (e.key === 'Enter') handleSubmit(e);
    if (e.key === 'Escape') {
      setItemName('');
      setIsExpanded(false);
    }
  };

  return (
    <div className="add-item-form">
      {!isExpanded ? (
        <button className="add-item-trigger" onClick={() => setIsExpanded(true)}>
          <span className="add-icon">+</span>
          <span>Add item manually</span>
        </button>
      ) : (
        <form onSubmit={handleSubmit} className="add-item-expanded animate-fade-in">
          <input
            type="text"
            className="add-item-input"
            placeholder="Enter item name (e.g., Milk, Bread)"
            value={itemName}
            onChange={(e) => setItemName(e.target.value)}
            onKeyDown={handleKeyDown}
            autoFocus
          />
          <div className="add-item-qty">
            <button type="button" className="qty-btn" onClick={() => setQuantity(Math.max(1, quantity - 1))}>−</button>
            <span className="qty-display">{quantity}</span>
            <button type="button" className="qty-btn" onClick={() => setQuantity(quantity + 1)}>+</button>
          </div>
          <button type="submit" className="add-item-submit" disabled={!itemName.trim()}>
            Add
          </button>
          <button type="button" className="add-item-cancel" onClick={() => { setIsExpanded(false); setItemName(''); }}>
            ✕
          </button>
        </form>
      )}
    </div>
  );
}

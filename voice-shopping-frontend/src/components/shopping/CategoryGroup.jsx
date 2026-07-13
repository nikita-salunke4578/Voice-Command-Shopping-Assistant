import { useState } from 'react';
import ShoppingItem from './ShoppingItem';

export default function CategoryGroup({ category, items, onToggle, onDelete, onUpdate }) {
  const [isExpanded, setIsExpanded] = useState(true);

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
    'Uncategorized': '📦',
  };

  const checkedCount = items.filter((i) => i.isChecked).length;

  return (
    <div className="category-group animate-fade-in">
      <button className="category-header" onClick={() => setIsExpanded(!isExpanded)}>
        <div className="category-title">
          <span className="category-icon">{categoryIcons[category] || '📦'}</span>
          <span className="category-name">{category}</span>
          <span className="category-count">
            {checkedCount}/{items.length}
          </span>
        </div>
        <span className={`category-chevron ${isExpanded ? 'expanded' : ''}`}>▾</span>
      </button>

      {isExpanded && (
        <div className="category-items">
          {items.map((item) => (
            <ShoppingItem
              key={item.id}
              item={item}
              onToggle={onToggle}
              onDelete={onDelete}
              onUpdate={onUpdate}
            />
          ))}
        </div>
      )}
    </div>
  );
}

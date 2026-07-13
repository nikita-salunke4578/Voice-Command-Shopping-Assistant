import React from 'react';
import useShoppingStore from '../../store/useShoppingStore';
import { useToast } from '../common/useToast';

const FALLBACK_IMAGE = "data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='300' height='120' viewBox='0 0 300 120'%3E%3Crect width='300' height='120' fill='%23202a3d'/%3E%3Cpath d='M115 35h70l-6 65h-58z' fill='%237c5cff'/%3E%3Cpath d='M133 35c0-20 34-20 34 0' fill='none' stroke='%23d9d2ff' stroke-width='8'/%3E%3C/svg%3E";

export default function SearchResultsPanel({ results }) {
  const { addItem } = useShoppingStore();
  const { addToast } = useToast();

  if (!results || results.length === 0) return null;

  const handleAddSearchResult = async (product) => {
    try {
      await addItem({ 
        itemName: product.name, 
        quantity: 1, 
        category: product.category,
        estimatedPrice: product.price
      });
      addToast(`Added "${product.name}"`, 'success');
    } catch {
      addToast('Failed to add item', 'error');
    }
  };

  const categoryImages = {
    'Dairy': 'https://images.unsplash.com/photo-1550583724-b2692b85b150?auto=format&fit=crop&w=300&q=80',
    'Fruits': 'https://images.unsplash.com/photo-1619566636858-adf3ef46400b?auto=format&fit=crop&w=300&q=80',
    'Vegetables': 'https://images.unsplash.com/photo-1566385101042-1a0aa0c1268c?auto=format&fit=crop&w=300&q=80',
    'Bakery': 'https://images.unsplash.com/photo-1509440159596-0249088772ff?auto=format&fit=crop&w=300&q=80',
    'Beverages': 'https://images.unsplash.com/photo-1534080391025-097b03b77385?auto=format&fit=crop&w=300&q=80',
    'Snacks': 'https://images.unsplash.com/photo-1599490659213-e2b9527b0876?auto=format&fit=crop&w=300&q=80',
    'Meat': 'https://images.unsplash.com/photo-1607623814075-e51df1bdc82f?auto=format&fit=crop&w=300&q=80',
    'Frozen': 'https://images.unsplash.com/photo-1516559828984-fb3b9952c64b?auto=format&fit=crop&w=300&q=80',
    'Grains': 'https://images.unsplash.com/photo-1574316071802-0d684efa7bf5?auto=format&fit=crop&w=300&q=80',
    'Household': 'https://images.unsplash.com/photo-1581578731548-c64695cc6952?auto=format&fit=crop&w=300&q=80',
    'Personal Care': 'https://images.unsplash.com/photo-1556228720-195a672e8a03?auto=format&fit=crop&w=300&q=80',
    'Spices': 'https://images.unsplash.com/photo-1596797038530-2c107229654b?auto=format&fit=crop&w=300&q=80',
  };

  return (
    <div className="search-panel">
      <h3 className="category-title" style={{marginTop: '1.5rem', marginBottom: '1rem'}}>
        <span className="category-icon">🔍</span>
        <span className="category-name">Voice Search Results</span>
      </h3>
      <div className="product-grid">
        {results.map((product, i) => (
          <div key={i} className="product-card" style={{ display: 'flex', flexDirection: 'column', overflow: 'hidden', padding: 0 }}>
            <img 
              src={product.imageUrl || categoryImages[product.category] || 'https://images.unsplash.com/photo-1542838132-92c53300491e?auto=format&fit=crop&w=300&q=80'} 
              onError={(event) => {
                event.currentTarget.onerror = null;
                event.currentTarget.src = FALLBACK_IMAGE;
              }}
              alt={product.name} 
              style={{ width: '100%', height: '120px', objectFit: 'cover', borderBottom: '1px solid rgba(255,255,255,0.08)' }} 
            />
            <div style={{ padding: 'var(--space-md)', flex: 1, display: 'flex', flexDirection: 'column', justifyContent: 'space-between' }}>
              <div>
                <div className="product-brand">{product.brand || product.category}</div>
                <div className="product-name" style={{ marginTop: '2px', fontWeight: '600' }}>{product.name}</div>
              </div>
              <div className="product-footer" style={{ marginTop: '12px' }}>
                <div>
                  <div className="product-price">₹{product.price}</div>
                  <div className="product-size">{product.size}</div>
                </div>
                <button className="product-add-btn" onClick={() => handleAddSearchResult(product)}>
                  + Add
                </button>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}

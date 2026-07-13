import React from 'react';
import useShoppingStore from '../../store/useShoppingStore';
import { useToast } from '../common/useToast';

export default function SuggestionsPanel({ suggestions }) {
  const { addItem } = useShoppingStore();
  const { addToast } = useToast();

  if (!suggestions || suggestions.length === 0) return null;

  const handleAddSuggestion = async (sugg) => {
    try {
      await addItem({ itemName: sugg.name, quantity: 1, category: sugg.category });
      addToast(`Added "${sugg.name}"`, 'success');
    } catch {
      addToast('Failed to add suggestion', 'error');
    }
  };

  const substitutes = suggestions.filter(s => s.type === 'substitute');
  const seasonal = suggestions.filter(s => s.type === 'seasonal');
  const history = suggestions.filter(s => s.type !== 'substitute' && s.type !== 'seasonal');

  const renderSection = (title, icon, items, themeClass) => {
    if (items.length === 0) return null;

    return (
      <div className={`suggestion-section ${themeClass}`} style={{ marginBottom: '1.5rem', animation: 'fadeIn var(--transition-fast) ease-out' }}>
        <h4 className="category-title" style={{ marginBottom: '0.75rem', display: 'flex', alignItems: 'center', gap: '8px' }}>
          <span className="category-icon" style={{ fontSize: '1.1rem' }}>{icon}</span>
          <span className="category-name" style={{ fontSize: '0.8rem', letterSpacing: '0.05em' }}>{title}</span>
        </h4>
        <div className="suggestion-bar" style={{ display: 'flex', gap: '8px', overflowX: 'auto', paddingBottom: '4px' }}>
          {items.map((sugg, i) => (
            <button 
              key={i} 
              className="suggestion-chip" 
              onClick={() => handleAddSuggestion(sugg)}
              title={sugg.reason}
              style={{
                display: 'inline-flex',
                alignItems: 'center',
                gap: '6px',
                padding: '6px 12px',
                borderRadius: '9999px',
                border: '1px solid var(--border-color)',
                background: 'var(--bg-glass)',
                color: 'var(--text-secondary)',
                fontSize: '0.8rem',
                whiteSpace: 'nowrap',
                transition: 'all var(--transition-fast)'
              }}
            >
              <span className="chip-add" style={{ color: 'var(--accent-green)', fontWeight: 'bold' }}>+</span>
              <span style={{ fontWeight: '500' }}>{sugg.name}</span>
              {sugg.reason && (
                <span className="chip-reason" style={{ opacity: 0.7, fontSize: '0.7rem', marginLeft: '2px', borderLeft: '1px solid rgba(255,255,255,0.15)', paddingLeft: '6px' }}>
                  {sugg.reason}
                </span>
              )}
            </button>
          ))}
        </div>
      </div>
    );
  };

  const hasSubstitutes = substitutes.length > 0;

  return (
    <div id="smart-recommendations" className={`suggestions-panel-container ${hasSubstitutes ? 'highlight-glow' : ''}`} style={{ marginTop: '2rem', padding: '1.25rem', borderRadius: 'var(--radius-lg)', background: 'rgba(255,255,255,0.01)', border: '1px solid rgba(255,255,255,0.03)' }}>
      <h3 className="category-title" style={{ marginBottom: '1.25rem', borderBottom: '1px solid rgba(255,255,255,0.05)', paddingBottom: '0.5rem' }}>
        <span className="category-icon">💡</span>
        <span className="category-name" style={{ color: 'var(--text-primary)', fontWeight: '700' }}>Smart Recommendations</span>
      </h3>
      
      {renderSection("Alternatives & Substitutes", "🔄", substitutes, "substitutes-section")}
      {renderSection("Seasonal & Offers", "🍁", seasonal, "seasonal-section")}
      {renderSection("Product Recommendations", "⏱️", history, "history-section")}
    </div>
  );
}

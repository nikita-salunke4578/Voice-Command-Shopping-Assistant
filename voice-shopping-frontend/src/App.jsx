import { useEffect, useState, useRef } from 'react';
import useShoppingStore from './store/useShoppingStore';
import CategoryGroup from './components/shopping/CategoryGroup';
import AddItemForm from './components/shopping/AddItemForm';
import Loader from './components/common/Loader';
import EmptyState from './components/common/EmptyState';
import VoiceAssistant from './components/voice/VoiceAssistant';
import { useToast } from './components/common/useToast';
import SuggestionsPanel from './components/shopping/SuggestionsPanel';
import SearchResultsPanel from './components/shopping/SearchResultsPanel';
import './App.css';

function App() {
  const {
    lists,
    activeList,
    items,
    loading,
    error,
    searchResults,
    suggestions,
    modalMessage,
    setModalMessage,
    commandAcknowledgement,
    setCommandAcknowledgement,
    voiceErrorMessage,
    setVoiceErrorMessage,
    fetchLists,
    fetchListById,
    addItem,
    updateItem,
    deleteItem,
    toggleItem,
    getGroupedItems,
    createList,
    deleteList,
    clearError,
  } = useShoppingStore();

  const { addToast, ToastContainer } = useToast();
  const [listDropdownOpen, setListDropdownOpen] = useState(false);
  const dropdownRef = useRef(null);

  const handleCloseModal = () => {
    setModalMessage(null);
    setTimeout(() => {
      const element = document.getElementById('smart-recommendations');
      if (element) {
        element.scrollIntoView({ behavior: 'smooth', block: 'center' });
      }
    }, 150);
  };

  // Load lists on mount
  useEffect(() => {
    fetchLists();
  }, [fetchLists]);

  // Show errors as toasts
  useEffect(() => {
    if (error) {
      addToast(error, 'error');
      clearError();
    }
  }, [error, addToast, clearError]);

  // Close dropdown on outside click
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
        setListDropdownOpen(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  const handleAddItem = async (itemData) => {
    try {
      await addItem(itemData);
      addToast(`Added "${itemData.itemName}" to your list`, 'success');
    } catch {
      addToast('Failed to add item', 'error');
    }
  };

  const handleDeleteItem = async (itemId) => {
    const item = items.find((i) => i.id === itemId);
    await deleteItem(itemId);
    addToast(`Removed "${item?.itemName || 'item'}" from your list`, 'info');
  };

  const handleUpdateItem = async (itemId, updates) => {
    await updateItem(itemId, updates);
  };

  const handleToggleItem = async (itemId) => {
    await toggleItem(itemId);
  };

  const handleCreateList = async () => {
    const name = prompt('Enter list name:', 'My Shopping List');
    if (name) {
      await createList(name);
      addToast(`Created list "${name}"`, 'success');
      setListDropdownOpen(false);
    }
  };

  const handleSwitchList = (listId) => {
    fetchListById(listId);
    setListDropdownOpen(false);
  };

  const handleDeleteList = async (e, listId, listName) => {
    e.stopPropagation();
    if (window.confirm(`Delete "${listName}"? This will remove all its items.`)) {
      await deleteList(listId);
      addToast(`Deleted list "${listName}"`, 'info');
    }
  };

  const groupedItems = getGroupedItems();
  const categoryKeys = Object.keys(groupedItems).sort();
  const checkedCount = items.filter((i) => i.isChecked).length;
  const totalCost = items
    .filter((i) => i.estimatedPrice && !i.isChecked)
    .reduce((sum, i) => sum + Number(i.estimatedPrice) * i.quantity, 0);

  return (
    <div className="app">
      <ToastContainer />

      {/* Navbar */}
      <nav className="navbar">
        <div className="navbar-brand">
          <span className="navbar-logo">🛒</span>
          <div>
            <div className="navbar-title">Voice Shopping Assistant</div>
            <div className="navbar-subtitle">Speak to manage your list</div>
          </div>
        </div>
        <div className="navbar-actions">
          {/* List Selector Dropdown */}
          <div className="list-selector" ref={dropdownRef}>
            <button
              className="nav-btn list-selector-toggle"
              onClick={() => setListDropdownOpen(!listDropdownOpen)}
            >
              📋 My Lists
              {lists.length > 0 && (
                <span className="list-count-badge">{lists.length}</span>
              )}
              <span className={`dropdown-chevron ${listDropdownOpen ? 'open' : ''}`}>▾</span>
            </button>
            {listDropdownOpen && (
              <div className="list-selector-dropdown">
                <div className="list-selector-header">
                  <span>Shopping Lists</span>
                  <button className="list-selector-new-btn" onClick={handleCreateList}>
                    + New
                  </button>
                </div>
                {lists.length === 0 ? (
                  <div className="list-selector-empty">
                    No lists yet. Create one to get started!
                  </div>
                ) : (
                  <div className="list-selector-items">
                    {lists.map((list) => (
                      <div
                        key={list.id}
                        className={`list-selector-item ${activeList?.id === list.id ? 'active' : ''}`}
                        onClick={() => handleSwitchList(list.id)}
                      >
                        <div className="list-selector-item-info">
                          <span className="list-selector-item-name">{list.name}</span>
                          <span className="list-selector-item-count">
                            {list.itemCount || 0} items
                          </span>
                        </div>
                        {lists.length > 1 && (
                          <button
                            className="list-selector-delete-btn"
                            onClick={(e) => handleDeleteList(e, list.id, list.name)}
                            title="Delete list"
                          >
                            ✕
                          </button>
                        )}
                      </div>
                    ))}
                  </div>
                )}
              </div>
            )}
          </div>
          <button className="nav-btn" onClick={handleCreateList}>
            + New List
          </button>
        </div>
      </nav>

      {/* Main Content */}
      <main className="main-content">
        {loading && items.length === 0 ? (
          <Loader text="Loading your shopping list..." />
        ) : !activeList ? (
          <EmptyState
            icon="📝"
            title="No shopping list yet"
            message="Create your first shopping list to get started"
            action={{ label: 'Create List', onClick: handleCreateList }}
          />
        ) : (
          <>
            {/* List Header */}
            <div className="list-header">
              <h1 className="list-title">{activeList.name}</h1>
              <div className="list-stats">
                <div className="stat">
                  <div className="stat-value">{checkedCount}/{items.length}</div>
                  <div className="stat-label">Items</div>
                </div>
                {totalCost > 0 && (
                  <div className="stat">
                    <div className="stat-value">₹{totalCost.toFixed(0)}</div>
                    <div className="stat-label">Estimated</div>
                  </div>
                )}
              </div>
            </div>
            <p className="purchase-notice">
              ✓ Checking an item marks it as purchased.
            </p>

            {/* Items by Category */}
            {categoryKeys.length === 0 && searchResults.length === 0 && suggestions.length === 0 ? (
              <EmptyState
                icon="🎤"
                title="Your list is empty"
                message="Add items using the button below or try speaking 'Add milk' using the mic"
              />
            ) : (
              categoryKeys.map((category) => (
                <CategoryGroup
                  key={category}
                  category={category}
                  items={groupedItems[category]}
                  onToggle={handleToggleItem}
                  onDelete={handleDeleteItem}
                  onUpdate={handleUpdateItem}
                />
              ))
            )}

            <SearchResultsPanel results={searchResults} />
            <SuggestionsPanel suggestions={suggestions} />

            {/* Manual Add Form */}
            <AddItemForm onAdd={handleAddItem} />
          </>
        )}
      </main>

      {/* Voice Assistant */}
      <VoiceAssistant />

      {/* Unavailable Modal */}
      {modalMessage && (
        <div className="modal-overlay">
          <div className="modal-content glass">
            <div className="modal-header">
              <span className="modal-warning-icon">⚠️</span>
              <h3 className="modal-title">Item Unavailable</h3>
            </div>
            <div className="modal-body">
              <p>{modalMessage}</p>
              <p>No substitute has been added. Choose an alternative only if you want it in your list.</p>
            </div>
            <div className="modal-actions-wrapper">
              <button className="modal-btn" onClick={handleCloseModal}>
                View Alternatives ➔
              </button>
            </div>
          </div>
        </div>
      )}

      {commandAcknowledgement && (
        <div className="modal-overlay">
          <div className="modal-content glass" role="dialog" aria-modal="true" aria-label="Voice command completed">
            <div className="modal-header">
              <span className="modal-warning-icon">✅</span>
              <h3 className="modal-title">Shopping List Updated</h3>
            </div>
            <div className="modal-body">
              <p>{commandAcknowledgement}</p>
            </div>
            <div className="modal-actions-wrapper">
              <button className="modal-btn" onClick={() => setCommandAcknowledgement(null)}>
                OK
              </button>
            </div>
          </div>
        </div>
      )}

      {voiceErrorMessage && (
        <div className="modal-overlay">
          <div className="modal-content glass" role="alertdialog" aria-modal="true" aria-label="Voice command error">
            <div className="modal-header">
              <span className="modal-warning-icon">⚠️</span>
              <h3 className="modal-title">Voice Command Unavailable</h3>
            </div>
            <div className="modal-body">
              <p>{voiceErrorMessage}</p>
            </div>
            <div className="modal-actions-wrapper">
              <button className="modal-btn" onClick={() => setVoiceErrorMessage(null)}>
                OK
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default App;

import { create } from 'zustand';
import { shoppingService } from '../services/shoppingService';

const useShoppingStore = create((set, get) => ({
  // State
  lists: [],
  activeList: null,
  items: [],
  loading: false,
  error: null,
  searchResults: [],
  suggestions: [],
  modalMessage: null,
  commandAcknowledgement: null,
  voiceErrorMessage: null,

  // Actions
  setLoading: (loading) => set({ loading }),
  setError: (error) => set({ error }),
  clearError: () => set({ error: null }),
  setSearchResults: (results) => set({ searchResults: results || [] }),
  setSuggestions: (suggestions) => set({ suggestions: suggestions || [] }),
  setModalMessage: (message) => set({ modalMessage: message }),
  setCommandAcknowledgement: (message) => set({ commandAcknowledgement: message }),
  setVoiceErrorMessage: (message) => set({ voiceErrorMessage: message }),

  // Fetch all lists
  fetchLists: async () => {
    set({ loading: true, error: null });
    try {
      const { data } = await shoppingService.getAllLists();
      set({ lists: data, loading: false });
      // Auto-select first list if none active
      if (data.length > 0 && !get().activeList) {
        get().fetchListById(data[0].id);
      }
    } catch (err) {
      set({ error: err.message, loading: false });
    }
  },

  // Fetch single list with items
  fetchListById: async (id) => {
    set({ loading: true, error: null });
    try {
      const { data } = await shoppingService.getListById(id);
      set({ activeList: data, items: data.items || [], loading: false });
    } catch (err) {
      set({ error: err.message, loading: false });
    }
  },

  // Create new list
  createList: async (name) => {
    set({ loading: true, error: null });
    try {
      const { data } = await shoppingService.createList(name);
      set((state) => ({
        lists: [data, ...state.lists],
        activeList: data,
        items: [],
        loading: false,
      }));
      return data;
    } catch (err) {
      set({ error: err.message, loading: false });
      throw err;
    }
  },

  // Delete list
  deleteList: async (id) => {
    set({ loading: true, error: null });
    try {
      await shoppingService.deleteList(id);
      const newLists = get().lists.filter((l) => l.id !== id);
      set({ lists: newLists, loading: false });
      if (get().activeList?.id === id) {
        if (newLists.length > 0) {
          get().fetchListById(newLists[0].id);
        } else {
          set({ activeList: null, items: [] });
        }
      }
    } catch (err) {
      set({ error: err.message, loading: false });
    }
  },

  // Add item to active list
  addItem: async (item) => {
    const activeList = get().activeList;
    if (!activeList) return;
    set({ error: null });
    try {
      const { data } = await shoppingService.addItem(activeList.id, item);
      set((state) => ({
        items: [...state.items, data],
        activeList: {
          ...state.activeList,
          itemCount: state.activeList.itemCount + 1,
        },
      }));
      return data;
    } catch (err) {
      set({ error: err.message });
      throw err;
    }
  },

  // Update item
  updateItem: async (itemId, updates) => {
    const activeList = get().activeList;
    if (!activeList) return;
    set({ error: null });
    try {
      const { data } = await shoppingService.updateItem(activeList.id, itemId, updates);
      set((state) => ({
        items: state.items.map((i) => (i.id === itemId ? data : i)),
      }));
      return data;
    } catch (err) {
      set({ error: err.message });
      throw err;
    }
  },

  // Delete item
  deleteItem: async (itemId) => {
    const activeList = get().activeList;
    if (!activeList) return;
    set({ error: null });
    try {
      await shoppingService.deleteItem(activeList.id, itemId);
      set((state) => ({
        items: state.items.filter((i) => i.id !== itemId),
        activeList: {
          ...state.activeList,
          itemCount: Math.max(0, state.activeList.itemCount - 1),
        },
      }));
    } catch (err) {
      set({ error: err.message });
    }
  },

  // Toggle item checked
  toggleItem: async (itemId) => {
    const activeList = get().activeList;
    if (!activeList) return;
    try {
      const { data } = await shoppingService.toggleItem(activeList.id, itemId);
      set((state) => ({
        items: state.items.map((i) => (i.id === itemId ? data : i)),
      }));
    } catch (err) {
      set({ error: err.message });
    }
  },

  // Set items directly (used by voice commands) and keep list counts in sync.
  setItems: (items) => set((state) => {
    const updatedItems = items || [];
    const activeListId = state.activeList?.id;

    return {
      items: updatedItems,
      activeList: state.activeList
        ? { ...state.activeList, itemCount: updatedItems.length }
        : null,
      lists: state.lists.map((list) => (
        list.id === activeListId
          ? { ...list, itemCount: updatedItems.length }
          : list
      )),
    };
  }),

  // Get items grouped by category
  getGroupedItems: () => {
    const items = get().items;
    const groups = {};
    items.forEach((item) => {
      const cat = item.category || 'Uncategorized';
      if (!groups[cat]) groups[cat] = [];
      groups[cat].push(item);
    });
    return groups;
  },
}));

export default useShoppingStore;

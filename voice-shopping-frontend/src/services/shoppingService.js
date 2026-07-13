import api from './api';

export const shoppingService = {
  // Lists
  getAllLists: () => api.get('/lists'),
  getListById: (id) => api.get(`/lists/${id}`),
  createList: (name) => api.post('/lists', { name }),
  updateList: (id, name) => api.put(`/lists/${id}`, { name }),
  deleteList: (id) => api.delete(`/lists/${id}`),

  // Items
  getItems: (listId) => api.get(`/lists/${listId}/items`),
  addItem: (listId, item) => api.post(`/lists/${listId}/items`, item),
  updateItem: (listId, itemId, item) => api.put(`/lists/${listId}/items/${itemId}`, item),
  deleteItem: (listId, itemId) => api.delete(`/lists/${listId}/items/${itemId}`),
  toggleItem: (listId, itemId) => api.patch(`/lists/${listId}/items/${itemId}/toggle`),
};

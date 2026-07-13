import api from './api';

export const productService = {
  search: (params) => api.get('/products/search', { params }),
  getCategories: () => api.get('/products/categories'),
  getSeasonal: () => api.get('/products/seasonal'),
};

import client from '../api/client';

export const urlService = {
  createShortUrl: async (data) => {
    // data: { originalUrl, customAlias, title, password, expiresAt, maxClicks }
    const response = await client.post('/urls', data);
    return response.data;
  },

  getUrls: async (page = 0, size = 10, search = '', sort = 'createdAt,desc') => {
    const params = new URLSearchParams({ page, size, sort });
    if (search && search.trim() !== '') {
      params.append('search', search.trim());
    }
    const response = await client.get(`/urls?${params.toString()}`);
    return response.data;
  },

  updateUrl: async (id, data) => {
    const response = await client.put(`/urls/${id}`, data);
    return response.data;
  },

  deleteUrl: async (id) => {
    const response = await client.delete(`/urls/${id}`);
    return response.data;
  }
};

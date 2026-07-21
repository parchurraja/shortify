import client from '../api/client';

export const authService = {
  login: async (email, password) => {
    const response = await client.post('/auth/login', { email, password });
    return response.data;
  },

  register: async (name, email, password) => {
    const response = await client.post('/auth/register', { name, email, password });
    return response.data;
  },

  logout: async () => {
    try {
      await client.post('/auth/logout');
    } catch {
      // Ignore logout API failure and proceed with local cleanup
    } finally {
      localStorage.clear();
    }
  },

  getCurrentUser: () => {
    const userStr = localStorage.getItem('user');
    return userStr ? JSON.parse(userStr) : null;
  }
};

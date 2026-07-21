import client from '../api/client';

export const analyticsService = {
  getDashboardData: async () => {
    const response = await client.get('/analytics/dashboard');
    return response.data;
  }
};

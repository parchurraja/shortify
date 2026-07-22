import React, { createContext, useState, useEffect, useContext } from 'react';
import client from '../api/client';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Initial check of local credentials
    const storedUser = localStorage.getItem('user');
    const token = localStorage.getItem('accessToken');
    const refreshToken = localStorage.getItem('refreshToken');

    if (storedUser && token && refreshToken) {
      setUser(JSON.parse(storedUser));
    } else {
      localStorage.removeItem('accessToken');
      localStorage.removeItem('refreshToken');
      localStorage.removeItem('user');
      setUser(null);
    }
    setLoading(false);
  }, []);

  const login = async (email, password) => {
    try {
      const response = await client.post('/auth/login', { email, password });
      const { accessToken, refreshToken, user: userInfo } = response.data.data;
      
      localStorage.setItem('accessToken', accessToken);
      localStorage.setItem('refreshToken', refreshToken);
      localStorage.setItem('user', JSON.stringify(userInfo));
      
      setUser(userInfo);
      return response.data;
    } catch (error) {
      throw error.response?.data || error;
    }
  };

  const register = async (name, email, password) => {
    try {
      const response = await client.post('/auth/register', { name, email, password });
      localStorage.removeItem('accessToken');
      localStorage.removeItem('refreshToken');
      localStorage.removeItem('user');
      setUser(null);
      return response.data;
    } catch (error) {
      throw error.response?.data || error;
    }
  };

  const logout = async () => {
    try {
      await client.post('/auth/logout');
    } catch (error) {
      console.error('Logout request failed', error);
    } finally {
      localStorage.clear();
      setUser(null);
    }
  };

  const value = {
    user,
    isAuthenticated: !!user,
    loading,
    login,
    register,
    logout,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

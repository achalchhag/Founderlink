import React, { createContext, useContext, useState, useEffect } from 'react';
import { authApi } from '../services/api';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [token, setToken] = useState(() => localStorage.getItem('founderlink_token'));
  const [user, setUser] = useState(null);

  const parseJwt = (jwtToken) => {
    try {
      const base64Url = jwtToken.split('.')[1];
      const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
      const jsonPayload = decodeURIComponent(
        atob(base64)
          .split('')
          .map((c) => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
          .join('')
      );
      return JSON.parse(jsonPayload);
    } catch {
      return null;
    }
  };

  useEffect(() => {
    if (token) {
      localStorage.setItem('founderlink_token', token);
      const claims = parseJwt(token);
      if (claims) {
        setUser({
          id: claims.sub,
          email: claims.email || claims.sub,
          role: claims.role || 'FOUNDER'
        });
      }
    } else {
      localStorage.removeItem('founderlink_token');
      setUser(null);
    }
  }, [token]);

  const login = async (email, password) => {
    const data = await authApi.login({ email, password });
    if (data && data.accessToken) {
      setToken(data.accessToken);
    }
    return data;
  };

  const register = async (fullName, email, password, role = 'FOUNDER') => {
    const data = await authApi.register({ fullName, email, password, role });
    return data;
  };

  const logout = () => {
    setToken(null);
    setUser(null);
    localStorage.removeItem('founderlink_token');
  };

  return (
    <AuthContext.Provider value={{ token, user, isAuthenticated: !!token, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  return useContext(AuthContext);
}

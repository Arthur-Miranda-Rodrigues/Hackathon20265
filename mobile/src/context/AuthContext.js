import React, { createContext, useContext, useEffect, useState } from 'react';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { api } from '../api';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [usuario, setUsuario] = useState(null);
  const [carregando, setCarregando] = useState(true);

  useEffect(() => {
    (async () => {
      const token = await AsyncStorage.getItem('token');
      if (token) {
        try {
          setUsuario(await api.me());
        } catch {
          await AsyncStorage.removeItem('token');
        }
      }
      setCarregando(false);
    })();
  }, []);

  async function login(email, senha) {
    const { token } = await api.login(email, senha);
    await AsyncStorage.setItem('token', token);
    setUsuario(await api.me());
  }

  async function logout() {
    try {
      await api.logout();
    } catch {}
    await AsyncStorage.removeItem('token');
    setUsuario(null);
  }

  async function atualizarUsuario() {
    setUsuario(await api.me());
  }

  return (
    <AuthContext.Provider value={{ usuario, carregando, login, logout, atualizarUsuario, setUsuario }}>
      {children}
    </AuthContext.Provider>
  );
}

export const useAuth = () => useContext(AuthContext);

import React, { createContext, useContext, useEffect, useState, type ReactNode } from 'react';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { api } from '../api';
import type { Usuario } from '../types';

interface AuthContextValue {
  usuario: Usuario | null;
  carregando: boolean;
  visitante: boolean;
  login: (email: string, senha: string) => Promise<void>;
  logout: () => Promise<void>;
  entrarComoVisitante: () => void;
  sairVisitante: () => void;
  atualizarUsuario: () => Promise<void>;
  setUsuario: React.Dispatch<React.SetStateAction<Usuario | null>>;
}

const AuthContext = createContext<AuthContextValue>({} as AuthContextValue);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [usuario, setUsuario] = useState<Usuario | null>(null);
  const [visitante, setVisitante] = useState(false);
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

  async function login(email: string, senha: string) {
    const { token } = await api.login(email, senha);
    await AsyncStorage.setItem('token', token);
    setVisitante(false);
    setUsuario(await api.me());
  }

  async function logout() {
    try {
      await api.logout();
    } catch {}
    await AsyncStorage.removeItem('token');
    setUsuario(null);
    setVisitante(false);
  }

  function entrarComoVisitante() {
    setVisitante(true);
  }

  function sairVisitante() {
    setVisitante(false);
  }

  async function atualizarUsuario() {
    setUsuario(await api.me());
  }

  return (
    <AuthContext.Provider
      value={{
        usuario,
        carregando,
        visitante,
        login,
        logout,
        entrarComoVisitante,
        sairVisitante,
        atualizarUsuario,
        setUsuario,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export const useAuth = () => useContext(AuthContext);

import AsyncStorage from '@react-native-async-storage/async-storage';
import { API_URL } from './config';
import type {
  FiltrosPartida,
  LoginResponse,
  MensagemResponse,
  MinhaPosicao,
  Page,
  Palpite,
  Partida,
  RankingItem,
  Usuario,
} from './types';

async function request<T>(path: string, options: RequestInit = {}): Promise<T> {
  const token = await AsyncStorage.getItem('token');
  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
    ...((options.headers as Record<string, string>) || {}),
  };
  if (token) headers.Authorization = `Bearer ${token}`;

  const res = await fetch(`${API_URL}${path}`, { ...options, headers });
  const text = await res.text();
  const data = text ? JSON.parse(text) : null;

  if (!res.ok) {
    const msg = (data && (data.mensagem || data.erro)) || `Erro ${res.status}`;
    throw new Error(msg);
  }
  return data as T;
}

export const api = {
  // Auth
  cadastro: (nome: string, email: string, senha: string) =>
    request<Usuario>('/api/auth/cadastro', {
      method: 'POST',
      body: JSON.stringify({ nome, email, senha }),
    }),
  login: (email: string, senha: string) =>
    request<LoginResponse>('/api/auth/login', {
      method: 'POST',
      body: JSON.stringify({ email, senha }),
    }),
  recuperarSenha: (email: string) =>
    request<MensagemResponse>('/api/auth/recuperar-senha', {
      method: 'POST',
      body: JSON.stringify({ email }),
    }),
  logout: () => request<void>('/api/auth/logout', { method: 'POST' }),

  // Usuário
  me: () => request<Usuario>('/api/usuarios/me'),
  editarPerfil: (nome: string, avatarUrl: string) =>
    request<Usuario>('/api/usuarios/me', {
      method: 'PUT',
      body: JSON.stringify({ nome, avatarUrl }),
    }),
  excluirConta: () =>
    request<void>('/api/usuarios/me', {
      method: 'DELETE',
      body: JSON.stringify({ confirmacao: true }),
    }),

  // Partidas
  partidas: (filtros: FiltrosPartida = {}) => {
    const qs = new URLSearchParams();
    if (filtros.fase) qs.append('fase', filtros.fase);
    if (filtros.data) qs.append('data', filtros.data);
    if (filtros.status) qs.append('status', filtros.status);
    const q = qs.toString();
    return request<Partida[]>(`/api/partidas${q ? `?${q}` : ''}`);
  },
  partida: (id: number) => request<Partida>(`/api/partidas/${id}`),
  proximas: () => request<Partida[]>('/api/partidas/proximas'),

  // Palpites
  registrarPalpite: (partidaId: number, golsA: number, golsB: number) =>
    request<Palpite>('/api/palpites', {
      method: 'POST',
      body: JSON.stringify({ partidaId, golsA, golsB }),
    }),
  editarPalpite: (id: number, golsA: number, golsB: number) =>
    request<Palpite>(`/api/palpites/${id}`, {
      method: 'PUT',
      body: JSON.stringify({ golsA, golsB }),
    }),
  meusPalpites: () => request<Palpite[]>('/api/palpites/meus'),

  // Ranking
  ranking: (page = 0, size = 50) =>
    request<Page<RankingItem>>(`/api/ranking?page=${page}&size=${size}`),
  minhaPosicao: () => request<MinhaPosicao>('/api/ranking/me'),
};

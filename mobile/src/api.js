import AsyncStorage from '@react-native-async-storage/async-storage';
import { API_URL } from './config';

async function request(path, options = {}) {
  const token = await AsyncStorage.getItem('token');
  const headers = { 'Content-Type': 'application/json', ...(options.headers || {}) };
  if (token) headers.Authorization = `Bearer ${token}`;

  const res = await fetch(`${API_URL}${path}`, { ...options, headers });
  const text = await res.text();
  const data = text ? JSON.parse(text) : null;

  if (!res.ok) {
    const msg = (data && (data.mensagem || data.erro)) || `Erro ${res.status}`;
    throw new Error(msg);
  }
  return data;
}

export const api = {
  // Auth
  cadastro: (nome, email, senha) =>
    request('/api/auth/cadastro', { method: 'POST', body: JSON.stringify({ nome, email, senha }) }),
  login: (email, senha) =>
    request('/api/auth/login', { method: 'POST', body: JSON.stringify({ email, senha }) }),
  recuperarSenha: (email) =>
    request('/api/auth/recuperar-senha', { method: 'POST', body: JSON.stringify({ email }) }),
  logout: () => request('/api/auth/logout', { method: 'POST' }),

  // Usuário
  me: () => request('/api/usuarios/me'),
  editarPerfil: (nome, avatarUrl) =>
    request('/api/usuarios/me', { method: 'PUT', body: JSON.stringify({ nome, avatarUrl }) }),
  excluirConta: () =>
    request('/api/usuarios/me', { method: 'DELETE', body: JSON.stringify({ confirmacao: true }) }),

  // Partidas
  partidas: (filtros = {}) => {
    const qs = new URLSearchParams();
    if (filtros.fase) qs.append('fase', filtros.fase);
    if (filtros.data) qs.append('data', filtros.data);
    if (filtros.status) qs.append('status', filtros.status);
    const q = qs.toString();
    return request(`/api/partidas${q ? `?${q}` : ''}`);
  },
  partida: (id) => request(`/api/partidas/${id}`),
  proximas: () => request('/api/partidas/proximas'),

  // Palpites
  registrarPalpite: (partidaId, golsA, golsB) =>
    request('/api/palpites', { method: 'POST', body: JSON.stringify({ partidaId, golsA, golsB }) }),
  editarPalpite: (id, golsA, golsB) =>
    request(`/api/palpites/${id}`, { method: 'PUT', body: JSON.stringify({ golsA, golsB }) }),
  meusPalpites: () => request('/api/palpites/meus'),

  // Ranking
  ranking: (page = 0, size = 50) => request(`/api/ranking?page=${page}&size=${size}`),
  minhaPosicao: () => request('/api/ranking/me'),
};

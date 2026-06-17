import type { NativeStackScreenProps } from '@react-navigation/native-stack';

export type Fase = 'GRUPOS' | 'OITAVAS' | 'QUARTAS' | 'SEMIFINAL' | 'FINAL';
export type StatusPartida = 'AGENDADA' | 'EM_ANDAMENTO' | 'ENCERRADA';
export type CriterioPontuacao = 'PLACAR_EXATO' | 'VENCEDOR_OU_EMPATE' | 'ERROU';

export interface Usuario {
  id: number;
  nome: string;
  email: string;
  avatarUrl?: string | null;
}

export interface Partida {
  id: number;
  selecaoA: string;
  selecaoB: string;
  fase: Fase;
  grupo?: string | null;
  dataHora: string;
  estadio?: string | null;
  status: StatusPartida;
  golsA?: number | null;
  golsB?: number | null;
}

export interface Palpite {
  id: number;
  partidaId: number;
  partida: string;
  dataHora: string;
  palpiteGolsA: number;
  palpiteGolsB: number;
  resultadoGolsA?: number | null;
  resultadoGolsB?: number | null;
  pontosObtidos?: number | null;
  criterioPontuacao?: CriterioPontuacao | null;
}

export interface RankingItem {
  id: number;
  nome: string;
  pontuacaoTotal: number;
  placaresExatos: number;
}

export interface MinhaPosicao {
  posicao: number;
  pontuacaoTotal: number;
  placaresExatos: number;
}

// Paginação do Spring Data
export interface Page<T> {
  content: T[];
  totalPages: number;
  number: number;
}

export interface LoginResponse {
  token: string;
}

export interface MensagemResponse {
  mensagem?: string;
}

export interface FiltrosPartida {
  fase?: Fase | null;
  data?: string | null;
  status?: StatusPartida | null;
}

export type AuthStackParamList = {
  Login: undefined;
  Cadastro: undefined;
  RecuperarSenha: undefined;
};

export type PartidasStackParamList = {
  Partidas: undefined;
  PartidaDetalhe: { id: number };
};

export type LoginScreenProps = NativeStackScreenProps<AuthStackParamList, 'Login'>;
export type CadastroScreenProps = NativeStackScreenProps<AuthStackParamList, 'Cadastro'>;
export type PartidasScreenProps = NativeStackScreenProps<PartidasStackParamList, 'Partidas'>;
export type PartidaDetalheScreenProps = NativeStackScreenProps<
  PartidasStackParamList,
  'PartidaDetalhe'
>;

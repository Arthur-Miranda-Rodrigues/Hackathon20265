import { StyleSheet } from 'react-native';
import type { CriterioPontuacao, Fase, StatusPartida } from './types';

export const cores = {
  primaria: '#0b6e4f',
  fundo: '#f4f5f7',
  card: '#ffffff',
  texto: '#1c1c1e',
  textoFraco: '#6b7280',
  borda: '#e5e7eb',
  destaque: '#fff7d6',
  erro: '#b00020',
};

export const estilos = StyleSheet.create({
  tela: { flex: 1, backgroundColor: cores.fundo },
  conteudo: { padding: 16 },
  titulo: { fontSize: 20, fontWeight: 'bold', color: cores.texto, marginBottom: 12 },
  card: {
    backgroundColor: cores.card,
    borderRadius: 10,
    padding: 14,
    marginBottom: 10,
    borderWidth: 1,
    borderColor: cores.borda,
  },
  label: { fontSize: 13, color: cores.textoFraco, marginBottom: 4, marginTop: 8 },
  input: {
    backgroundColor: cores.card,
    borderWidth: 1,
    borderColor: cores.borda,
    borderRadius: 8,
    paddingHorizontal: 12,
    paddingVertical: 10,
    fontSize: 15,
  },
  botao: {
    backgroundColor: cores.primaria,
    borderRadius: 8,
    paddingVertical: 13,
    alignItems: 'center',
    marginTop: 16,
  },
  botaoTexto: { color: '#fff', fontWeight: 'bold', fontSize: 15 },
  link: { color: cores.primaria, marginTop: 16, textAlign: 'center' },
  erro: { color: cores.erro, marginTop: 8 },
});

const labelsFase: Record<Fase, string> = {
  GRUPOS: 'Fase de Grupos',
  OITAVAS: 'Oitavas de Final',
  QUARTAS: 'Quartas de Final',
  SEMIFINAL: 'Semifinal',
  FINAL: 'Final',
};

const labelsStatus: Record<StatusPartida, string> = {
  AGENDADA: 'Agendada',
  EM_ANDAMENTO: 'Em andamento',
  ENCERRADA: 'Encerrada',
};

const labelsCriterio: Record<CriterioPontuacao, string> = {
  PLACAR_EXATO: 'Placar exato',
  VENCEDOR_OU_EMPATE: 'Acertou o vencedor/empate',
  ERROU: 'Errou',
};

export const labelFase = (f: string): string => labelsFase[f as Fase] || f;
export const labelStatus = (s: string): string => labelsStatus[s as StatusPartida] || s;
export const labelCriterio = (c: string): string => labelsCriterio[c as CriterioPontuacao] || c;

export function formatarDataHora(iso?: string | null): string {
  if (!iso) return '';
  const d = new Date(iso);
  return d.toLocaleString('pt-BR', {
    day: '2-digit',
    month: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
  });
}

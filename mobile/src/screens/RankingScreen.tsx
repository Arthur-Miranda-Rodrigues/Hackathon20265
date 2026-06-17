import React, { useCallback, useState } from 'react';
import { View, Text, FlatList, TouchableOpacity, ActivityIndicator } from 'react-native';
import { useFocusEffect } from '@react-navigation/native';
import { api } from '../api';
import { useAuth } from '../context/AuthContext';
import { estilos, cores } from '../theme';
import type { MinhaPosicao, Page, RankingItem } from '../types';

export default function RankingScreen() {
  const { usuario } = useAuth();
  const [pagina, setPagina] = useState(0);
  const [dados, setDados] = useState<Page<RankingItem>>({ content: [], totalPages: 1, number: 0 });
  const [minhaPosicao, setMinhaPosicao] = useState<MinhaPosicao | null>(null);
  const [carregando, setCarregando] = useState(true);

  const carregar = useCallback(async (page: number) => {
    setCarregando(true);
    try {
      const [ranking, posicao] = await Promise.all([api.ranking(page, 50), api.minhaPosicao()]);
      setDados(ranking);
      setMinhaPosicao(posicao);
    } catch (e) {
      setDados({ content: [], totalPages: 1, number: 0 });
    } finally {
      setCarregando(false);
    }
  }, []);

  useFocusEffect(
    useCallback(() => {
      carregar(pagina);
    }, [carregar, pagina])
  );

  const inicioPosicao = (dados.number ?? 0) * 50;

  return (
    <View style={estilos.tela}>
      {/* Posição do usuário autenticado (RF-033) */}
      {minhaPosicao && (
        <View style={{ padding: 16, paddingBottom: 0 }}>
          <View style={[estilos.card, { backgroundColor: cores.destaque }]}>
            <Text style={{ fontWeight: 'bold', color: cores.texto }}>
              Sua posição: {minhaPosicao.posicao}º
            </Text>
            <Text style={{ color: cores.textoFraco }}>
              {minhaPosicao.pontuacaoTotal} pts · {minhaPosicao.placaresExatos} placar(es) exato(s)
            </Text>
          </View>
        </View>
      )}

      {carregando ? (
        <ActivityIndicator color={cores.primaria} style={{ marginTop: 24 }} />
      ) : (
        <FlatList
          data={dados.content}
          keyExtractor={(item) => String(item.id)}
          contentContainerStyle={{ padding: 16 }}
          ListHeaderComponent={<Text style={estilos.titulo}>Ranking geral</Text>}
          ListEmptyComponent={<Text style={{ color: cores.textoFraco }}>Ranking vazio.</Text>}
          renderItem={({ item, index }) => {
            const sou = usuario && item.id === usuario.id;
            return (
              <View
                style={[
                  estilos.card,
                  { flexDirection: 'row', alignItems: 'center' },
                  sou && { borderColor: cores.primaria, borderWidth: 2 },
                ]}
              >
                <Text style={{ width: 36, fontWeight: 'bold', color: cores.primaria }}>
                  {inicioPosicao + index + 1}º
                </Text>
                <View style={{ flex: 1 }}>
                  <Text style={{ fontWeight: 'bold', color: cores.texto }}>{item.nome}</Text>
                  <Text style={{ color: cores.textoFraco }}>
                    {item.placaresExatos} placar(es) exato(s)
                  </Text>
                </View>
                <Text style={{ fontWeight: 'bold', color: cores.texto }}>
                  {item.pontuacaoTotal} pts
                </Text>
              </View>
            );
          }}
          ListFooterComponent={
            dados.totalPages > 1 ? (
              <View
                style={{ flexDirection: 'row', justifyContent: 'space-between', marginTop: 8 }}
              >
                <TouchableOpacity
                  disabled={pagina <= 0}
                  onPress={() => setPagina((p) => Math.max(0, p - 1))}
                >
                  <Text style={{ color: pagina <= 0 ? cores.textoFraco : cores.primaria }}>
                    ‹ Anterior
                  </Text>
                </TouchableOpacity>
                <Text style={{ color: cores.textoFraco }}>
                  Página {dados.number + 1} de {dados.totalPages}
                </Text>
                <TouchableOpacity
                  disabled={pagina >= dados.totalPages - 1}
                  onPress={() => setPagina((p) => p + 1)}
                >
                  <Text
                    style={{
                      color: pagina >= dados.totalPages - 1 ? cores.textoFraco : cores.primaria,
                    }}
                  >
                    Próxima ›
                  </Text>
                </TouchableOpacity>
              </View>
            ) : null
          }
        />
      )}
    </View>
  );
}

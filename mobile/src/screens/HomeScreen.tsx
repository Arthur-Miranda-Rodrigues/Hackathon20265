import React, { useCallback, useState } from 'react';
import { View, Text, FlatList, ActivityIndicator, RefreshControl } from 'react-native';
import { useFocusEffect } from '@react-navigation/native';
import { api } from '../api';
import { useAuth } from '../context/AuthContext';
import { estilos, cores, labelFase, formatarDataHora } from '../theme';
import type { Partida } from '../types';

export default function HomeScreen() {
  const { usuario } = useAuth();
  const [partidas, setPartidas] = useState<Partida[]>([]);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState('');

  const carregar = useCallback(async () => {
    setErro('');
    try {
      setPartidas(await api.proximas());
    } catch (e: any) {
      setErro(e.message);
    } finally {
      setCarregando(false);
    }
  }, []);

  useFocusEffect(
    useCallback(() => {
      carregar();
    }, [carregar])
  );

  return (
    <View style={estilos.tela}>
      <View style={estilos.conteudo}>
        <Text style={estilos.titulo}>Olá, {usuario?.nome}!</Text>
        <Text style={{ color: cores.textoFraco, marginBottom: 8 }}>
          Próximas partidas para palpitar
        </Text>
      </View>

      {carregando ? (
        <ActivityIndicator color={cores.primaria} />
      ) : (
        <FlatList
          data={partidas}
          keyExtractor={(item) => String(item.id)}
          contentContainerStyle={{ paddingHorizontal: 16, paddingBottom: 16 }}
          refreshControl={<RefreshControl refreshing={false} onRefresh={carregar} />}
          ListEmptyComponent={
            <Text style={{ color: cores.textoFraco }}>
              {erro || 'Nenhuma partida disponível para palpitar.'}
            </Text>
          }
          renderItem={({ item }) => (
            <View style={estilos.card}>
              <Text style={{ fontWeight: 'bold', fontSize: 16, color: cores.texto }}>
                {item.selecaoA} x {item.selecaoB}
              </Text>
              <Text style={{ color: cores.textoFraco, marginTop: 4 }}>{labelFase(item.fase)}</Text>
              <Text style={{ color: cores.textoFraco }}>{formatarDataHora(item.dataHora)}</Text>
              {!!item.estadio && <Text style={{ color: cores.textoFraco }}>{item.estadio}</Text>}
            </View>
          )}
        />
      )}
    </View>
  );
}

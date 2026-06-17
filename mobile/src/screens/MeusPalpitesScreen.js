import React, { useCallback, useState } from 'react';
import { View, Text, FlatList, ActivityIndicator, RefreshControl } from 'react-native';
import { useFocusEffect } from '@react-navigation/native';
import { api } from '../api';
import { estilos, cores, labelCriterio, formatarDataHora } from '../theme';

export default function MeusPalpitesScreen() {
  const [palpites, setPalpites] = useState([]);
  const [carregando, setCarregando] = useState(true);

  const carregar = useCallback(async () => {
    try {
      setPalpites(await api.meusPalpites());
    } catch (e) {
      setPalpites([]);
    } finally {
      setCarregando(false);
    }
  }, []);

  useFocusEffect(
    useCallback(() => {
      carregar();
    }, [carregar])
  );

  if (carregando) {
    return <ActivityIndicator color={cores.primaria} style={{ marginTop: 24 }} />;
  }

  return (
    <View style={estilos.tela}>
      <FlatList
        data={palpites}
        keyExtractor={(item) => String(item.id)}
        contentContainerStyle={{ padding: 16 }}
        refreshControl={<RefreshControl refreshing={false} onRefresh={carregar} />}
        ListHeaderComponent={<Text style={estilos.titulo}>Meus palpites</Text>}
        ListEmptyComponent={
          <Text style={{ color: cores.textoFraco }}>Você ainda não fez palpites.</Text>
        }
        renderItem={({ item }) => {
          const encerrada = item.resultadoGolsA != null && item.resultadoGolsB != null;
          return (
            <View style={estilos.card}>
              <Text style={{ fontWeight: 'bold', fontSize: 15, color: cores.texto }}>{item.partida}</Text>
              <Text style={{ color: cores.textoFraco, marginTop: 2 }}>
                {formatarDataHora(item.dataHora)}
              </Text>
              <Text style={{ marginTop: 6, color: cores.texto }}>
                Seu palpite: {item.palpiteGolsA} - {item.palpiteGolsB}
              </Text>
              {encerrada && (
                <Text style={{ color: cores.texto }}>
                  Resultado: {item.resultadoGolsA} - {item.resultadoGolsB}
                </Text>
              )}
              {encerrada && (
                <View style={{ marginTop: 6 }}>
                  <Text style={{ fontWeight: 'bold', color: cores.primaria }}>
                    {item.pontosObtidos} ponto(s)
                  </Text>
                  {!!item.criterioPontuacao && (
                    <Text style={{ color: cores.textoFraco }}>{labelCriterio(item.criterioPontuacao)}</Text>
                  )}
                </View>
              )}
            </View>
          );
        }}
      />
    </View>
  );
}

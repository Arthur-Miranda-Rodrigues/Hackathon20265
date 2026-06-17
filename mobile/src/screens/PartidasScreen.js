import React, { useCallback, useState } from 'react';
import {
  View,
  Text,
  FlatList,
  TouchableOpacity,
  ActivityIndicator,
  ScrollView,
} from 'react-native';
import { useFocusEffect } from '@react-navigation/native';
import { api } from '../api';
import { estilos, cores, labelFase, labelStatus, formatarDataHora } from '../theme';

const FASES = ['GRUPOS', 'OITAVAS', 'QUARTAS', 'SEMIFINAL', 'FINAL'];
const STATUS = ['AGENDADA', 'EM_ANDAMENTO', 'ENCERRADA'];

function Filtro({ ativo, texto, onPress }) {
  return (
    <TouchableOpacity
      onPress={onPress}
      style={{
        paddingHorizontal: 12,
        paddingVertical: 6,
        borderRadius: 16,
        marginRight: 8,
        backgroundColor: ativo ? cores.primaria : cores.card,
        borderWidth: 1,
        borderColor: ativo ? cores.primaria : cores.borda,
      }}
    >
      <Text style={{ color: ativo ? '#fff' : cores.texto, fontSize: 13 }}>{texto}</Text>
    </TouchableOpacity>
  );
}

// Agrupa as partidas por fase (RF-010).
function agruparPorFase(partidas) {
  const grupos = {};
  partidas.forEach((p) => {
    const chave = p.fase || 'OUTROS';
    if (!grupos[chave]) grupos[chave] = [];
    grupos[chave].push(p);
  });
  return Object.entries(grupos);
}

export default function PartidasScreen({ navigation }) {
  const [partidas, setPartidas] = useState([]);
  const [carregando, setCarregando] = useState(true);
  const [fase, setFase] = useState(null);
  const [status, setStatus] = useState(null);

  const carregar = useCallback(async () => {
    setCarregando(true);
    try {
      setPartidas(await api.partidas({ fase, status }));
    } catch (e) {
      setPartidas([]);
    } finally {
      setCarregando(false);
    }
  }, [fase, status]);

  useFocusEffect(
    useCallback(() => {
      carregar();
    }, [carregar])
  );

  return (
    <View style={estilos.tela}>
      <View style={{ paddingHorizontal: 16, paddingTop: 12 }}>
        <Text style={estilos.label}>Fase</Text>
        <ScrollView horizontal showsHorizontalScrollIndicator={false} style={{ marginBottom: 6 }}>
          <Filtro texto="Todas" ativo={!fase} onPress={() => setFase(null)} />
          {FASES.map((f) => (
            <Filtro key={f} texto={labelFase(f)} ativo={fase === f} onPress={() => setFase(f)} />
          ))}
        </ScrollView>

        <Text style={estilos.label}>Status</Text>
        <ScrollView horizontal showsHorizontalScrollIndicator={false} style={{ marginBottom: 6 }}>
          <Filtro texto="Todos" ativo={!status} onPress={() => setStatus(null)} />
          {STATUS.map((s) => (
            <Filtro key={s} texto={labelStatus(s)} ativo={status === s} onPress={() => setStatus(s)} />
          ))}
        </ScrollView>
      </View>

      {carregando ? (
        <ActivityIndicator color={cores.primaria} style={{ marginTop: 20 }} />
      ) : (
        <FlatList
          data={agruparPorFase(partidas)}
          keyExtractor={(item) => item[0]}
          contentContainerStyle={{ padding: 16 }}
          ListEmptyComponent={
            <Text style={{ color: cores.textoFraco }}>Nenhuma partida encontrada.</Text>
          }
          renderItem={({ item: [chaveFase, lista] }) => (
            <View>
              <Text style={{ fontWeight: 'bold', color: cores.primaria, marginBottom: 6, marginTop: 4 }}>
                {labelFase(chaveFase)}
              </Text>
              {lista.map((p) => (
                <TouchableOpacity
                  key={p.id}
                  style={estilos.card}
                  onPress={() => navigation.navigate('PartidaDetalhe', { id: p.id })}
                >
                  <View style={{ flexDirection: 'row', justifyContent: 'space-between' }}>
                    <Text style={{ fontWeight: 'bold', fontSize: 15, color: cores.texto }}>
                      {p.selecaoA} x {p.selecaoB}
                    </Text>
                    {p.golsA != null && p.golsB != null && (
                      <Text style={{ fontWeight: 'bold', color: cores.texto }}>
                        {p.golsA} - {p.golsB}
                      </Text>
                    )}
                  </View>
                  <Text style={{ color: cores.textoFraco, marginTop: 4 }}>
                    {formatarDataHora(p.dataHora)} · {labelStatus(p.status)}
                  </Text>
                </TouchableOpacity>
              ))}
            </View>
          )}
        />
      )}
    </View>
  );
}

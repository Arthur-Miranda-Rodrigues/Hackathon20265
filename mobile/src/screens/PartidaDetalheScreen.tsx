import React, { useCallback, useState } from 'react';
import {
  View,
  Text,
  TextInput,
  TouchableOpacity,
  ScrollView,
  ActivityIndicator,
  Alert,
} from 'react-native';
import { useFocusEffect } from '@react-navigation/native';
import { api } from '../api';
import { estilos, cores, labelFase, labelStatus, formatarDataHora } from '../theme';
import type { Palpite, Partida, PartidaDetalheScreenProps } from '../types';

export default function PartidaDetalheScreen({ route }: PartidaDetalheScreenProps) {
  const { id } = route.params;
  const [partida, setPartida] = useState<Partida | null>(null);
  const [palpite, setPalpite] = useState<Palpite | null>(null); // palpite existente do usuário, se houver
  const [golsA, setGolsA] = useState('');
  const [golsB, setGolsB] = useState('');
  const [carregando, setCarregando] = useState(true);
  const [salvando, setSalvando] = useState(false);
  const [erro, setErro] = useState('');

  const carregar = useCallback(async () => {
    setErro('');
    try {
      const p = await api.partida(id);
      setPartida(p);
      const meus = await api.meusPalpites();
      const existente = meus.find((m) => m.partidaId === id);
      if (existente) {
        setPalpite(existente);
        setGolsA(String(existente.palpiteGolsA));
        setGolsB(String(existente.palpiteGolsB));
      }
    } catch (e: any) {
      setErro(e.message);
    } finally {
      setCarregando(false);
    }
  }, [id]);

  useFocusEffect(
    useCallback(() => {
      carregar();
    }, [carregar])
  );

  async function salvar() {
    setErro('');
    const a = parseInt(golsA, 10);
    const b = parseInt(golsB, 10);
    if (isNaN(a) || isNaN(b)) {
      setErro('Informe o placar dos dois times.');
      return;
    }
    setSalvando(true);
    try {
      if (palpite) {
        await api.editarPalpite(palpite.id, a, b);
      } else {
        await api.registrarPalpite(id, a, b);
      }
      Alert.alert('Pronto', 'Palpite salvo com sucesso.');
      carregar();
    } catch (e: any) {
      setErro(e.message);
    } finally {
      setSalvando(false);
    }
  }

  if (carregando) {
    return <ActivityIndicator color={cores.primaria} style={{ marginTop: 24 }} />;
  }

  if (!partida) {
    return (
      <View style={estilos.conteudo}>
        <Text style={estilos.erro}>{erro || 'Partida não encontrada.'}</Text>
      </View>
    );
  }

  const podePalpitar = partida.status === 'AGENDADA';

  return (
    <ScrollView style={estilos.tela} contentContainerStyle={estilos.conteudo}>
      <View style={estilos.card}>
        <Text style={{ fontSize: 18, fontWeight: 'bold', color: cores.texto }}>
          {partida.selecaoA} x {partida.selecaoB}
        </Text>
        <Text style={estilos.label}>
          {labelFase(partida.fase)}
          {partida.grupo ? ` · Grupo ${partida.grupo}` : ''}
        </Text>
        <Text style={{ color: cores.textoFraco }}>{formatarDataHora(partida.dataHora)}</Text>
        {!!partida.estadio && <Text style={{ color: cores.textoFraco }}>{partida.estadio}</Text>}
        <Text style={{ color: cores.textoFraco, marginTop: 4 }}>
          Status: {labelStatus(partida.status)}
        </Text>
        {partida.golsA != null && partida.golsB != null && (
          <Text style={{ fontWeight: 'bold', fontSize: 16, marginTop: 8, color: cores.texto }}>
            Resultado: {partida.golsA} - {partida.golsB}
          </Text>
        )}
      </View>

      <Text style={estilos.titulo}>Seu palpite</Text>

      {podePalpitar ? (
        <View>
          <View
            style={{ flexDirection: 'row', alignItems: 'center', justifyContent: 'space-between' }}
          >
            <View style={{ flex: 1, marginRight: 8 }}>
              <Text style={estilos.label}>{partida.selecaoA}</Text>
              <TextInput
                style={estilos.input}
                keyboardType="number-pad"
                value={golsA}
                onChangeText={setGolsA}
              />
            </View>
            <View style={{ flex: 1, marginLeft: 8 }}>
              <Text style={estilos.label}>{partida.selecaoB}</Text>
              <TextInput
                style={estilos.input}
                keyboardType="number-pad"
                value={golsB}
                onChangeText={setGolsB}
              />
            </View>
          </View>

          {!!erro && <Text style={estilos.erro}>{erro}</Text>}

          <TouchableOpacity style={estilos.botao} onPress={salvar} disabled={salvando}>
            <Text style={estilos.botaoTexto}>
              {salvando ? 'Salvando...' : palpite ? 'Atualizar palpite' : 'Registrar palpite'}
            </Text>
          </TouchableOpacity>
        </View>
      ) : (
        <View style={estilos.card}>
          {palpite ? (
            <Text style={{ color: cores.texto }}>
              Seu palpite: {palpite.palpiteGolsA} - {palpite.palpiteGolsB}
            </Text>
          ) : (
            <Text style={{ color: cores.textoFraco }}>Você não palpitou nesta partida.</Text>
          )}
          <Text style={{ color: cores.textoFraco, marginTop: 6 }}>
            Os palpites estão encerrados para esta partida.
          </Text>
        </View>
      )}
    </ScrollView>
  );
}

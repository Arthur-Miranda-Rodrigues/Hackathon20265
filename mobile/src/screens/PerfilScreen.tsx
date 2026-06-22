import React, { useState } from 'react';
import { Text, TextInput, TouchableOpacity, ScrollView, Alert, Image } from 'react-native';
import * as ImagePicker from 'expo-image-picker';
import { api } from '../api';
import { API_URL } from '../config';
import { useAuth } from '../context/AuthContext';
import { estilos, cores } from '../theme';

export default function PerfilScreen() {
  const { usuario, logout, atualizarUsuario } = useAuth();
  const [nome, setNome] = useState(usuario?.nome || '');
  const [avatarUrl, setAvatarUrl] = useState(usuario?.avatarUrl || '');
  const [erro, setErro] = useState('');
  const [salvando, setSalvando] = useState(false);

  const avatarFonte = avatarUrl
    ? (avatarUrl.startsWith('http') ? avatarUrl : `${API_URL}${avatarUrl}`)
    : '';

  async function salvar() {
    setErro('');
    setSalvando(true);
    try {
      await api.editarPerfil(nome.trim(), avatarUrl.trim());
      await atualizarUsuario();
      Alert.alert('Perfil', 'Dados atualizados.');
    } catch (e: any) {
      setErro(e.message);
    } finally {
      setSalvando(false);
    }
  }

  async function escolherFoto() {
    setErro('');
    const perm = await ImagePicker.requestMediaLibraryPermissionsAsync();
    if (!perm.granted) {
      setErro('Permissão para acessar as fotos foi negada.');
      return;
    }
    const res = await ImagePicker.launchImageLibraryAsync({
      mediaTypes: ImagePicker.MediaTypeOptions.Images,
      quality: 0.7,
    });
    if (res.canceled || !res.assets?.length) return;
    setSalvando(true);
    try {
      const atualizado = await api.uploadAvatar(res.assets[0].uri);
      setAvatarUrl(atualizado.avatarUrl || '');
      await atualizarUsuario();
      Alert.alert('Perfil', 'Foto de perfil atualizada.');
    } catch (e: any) {
      setErro(e.message);
    } finally {
      setSalvando(false);
    }
  }

  function confirmarExclusao() {
    Alert.alert('Excluir conta', 'Esta ação é permanente e remove seus dados. Deseja continuar?', [
      { text: 'Cancelar', style: 'cancel' },
      {
        text: 'Excluir',
        style: 'destructive',
        onPress: async () => {
          try {
            await api.excluirConta();
            await logout();
          } catch (e: any) {
            Alert.alert('Erro', e.message);
          }
        },
      },
    ]);
  }

  return (
    <ScrollView style={estilos.tela} contentContainerStyle={estilos.conteudo}>
      <Text style={estilos.titulo}>Meu perfil</Text>

      {!!avatarFonte && (
        <Image
          source={{ uri: avatarFonte }}
          style={{ width: 80, height: 80, borderRadius: 40, alignSelf: 'center', marginBottom: 8 }}
        />
      )}

      <Text style={{ color: cores.textoFraco, textAlign: 'center' }}>{usuario?.email}</Text>

      <TouchableOpacity
        style={[estilos.botao, { backgroundColor: cores.card, borderWidth: 1, borderColor: cores.borda }]}
        onPress={escolherFoto}
        disabled={salvando}
      >
        <Text style={[estilos.botaoTexto, { color: cores.texto }]}>Escolher foto da galeria</Text>
      </TouchableOpacity>

      <Text style={estilos.label}>Nome de exibição</Text>
      <TextInput style={estilos.input} value={nome} onChangeText={setNome} />

      <Text style={estilos.label}>URL do avatar (opcional)</Text>
      <TextInput
        style={estilos.input}
        autoCapitalize="none"
        value={avatarUrl}
        onChangeText={setAvatarUrl}
        placeholder="https://..."
      />

      {!!erro && <Text style={estilos.erro}>{erro}</Text>}

      <TouchableOpacity style={estilos.botao} onPress={salvar} disabled={salvando}>
        <Text style={estilos.botaoTexto}>{salvando ? 'Salvando...' : 'Salvar perfil'}</Text>
      </TouchableOpacity>

      <TouchableOpacity
        style={[estilos.botao, { backgroundColor: cores.card, borderWidth: 1, borderColor: cores.borda }]}
        onPress={logout}
      >
        <Text style={[estilos.botaoTexto, { color: cores.texto }]}>Sair</Text>
      </TouchableOpacity>

      <TouchableOpacity
        style={[estilos.botao, { backgroundColor: cores.card, borderWidth: 1, borderColor: cores.erro }]}
        onPress={confirmarExclusao}
      >
        <Text style={[estilos.botaoTexto, { color: cores.erro }]}>Excluir minha conta</Text>
      </TouchableOpacity>
    </ScrollView>
  );
}

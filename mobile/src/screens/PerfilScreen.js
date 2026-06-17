import React, { useState } from 'react';
import { View, Text, TextInput, TouchableOpacity, ScrollView, Alert, Image } from 'react-native';
import { api } from '../api';
import { useAuth } from '../context/AuthContext';
import { estilos, cores } from '../theme';

export default function PerfilScreen() {
  const { usuario, logout, atualizarUsuario } = useAuth();
  const [nome, setNome] = useState(usuario?.nome || '');
  const [avatarUrl, setAvatarUrl] = useState(usuario?.avatarUrl || '');
  const [erro, setErro] = useState('');
  const [salvando, setSalvando] = useState(false);

  async function salvar() {
    setErro('');
    setSalvando(true);
    try {
      await api.editarPerfil(nome.trim(), avatarUrl.trim());
      await atualizarUsuario();
      Alert.alert('Perfil', 'Dados atualizados.');
    } catch (e) {
      setErro(e.message);
    } finally {
      setSalvando(false);
    }
  }

  function confirmarExclusao() {
    Alert.alert(
      'Excluir conta',
      'Esta ação é permanente e remove seus dados. Deseja continuar?',
      [
        { text: 'Cancelar', style: 'cancel' },
        {
          text: 'Excluir',
          style: 'destructive',
          onPress: async () => {
            try {
              await api.excluirConta();
              await logout();
            } catch (e) {
              Alert.alert('Erro', e.message);
            }
          },
        },
      ]
    );
  }

  return (
    <ScrollView style={estilos.tela} contentContainerStyle={estilos.conteudo}>
      <Text style={estilos.titulo}>Meu perfil</Text>

      {!!avatarUrl && (
        <Image
          source={{ uri: avatarUrl }}
          style={{ width: 80, height: 80, borderRadius: 40, alignSelf: 'center', marginBottom: 8 }}
        />
      )}

      <Text style={{ color: cores.textoFraco, textAlign: 'center' }}>{usuario?.email}</Text>

      <Text style={estilos.label}>Nome de exibição</Text>
      <TextInput style={estilos.input} value={nome} onChangeText={setNome} />

      <Text style={estilos.label}>URL do avatar</Text>
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

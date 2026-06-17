import React, { useState } from 'react';
import { Text, TextInput, TouchableOpacity, ScrollView, Alert } from 'react-native';
import { api } from '../api';
import { estilos } from '../theme';
import type { CadastroScreenProps } from '../types';

export default function CadastroScreen({ navigation }: CadastroScreenProps) {
  const [nome, setNome] = useState('');
  const [email, setEmail] = useState('');
  const [senha, setSenha] = useState('');
  const [erro, setErro] = useState('');
  const [carregando, setCarregando] = useState(false);

  async function cadastrar() {
    setErro('');
    setCarregando(true);
    try {
      await api.cadastro(nome.trim(), email.trim(), senha);
      Alert.alert('Conta criada', 'Faça login para continuar.');
      navigation.navigate('Login');
    } catch (e: any) {
      setErro(e.message);
    } finally {
      setCarregando(false);
    }
  }

  return (
    <ScrollView style={estilos.tela} contentContainerStyle={estilos.conteudo}>
      <Text style={estilos.titulo}>Criar conta</Text>

      <Text style={estilos.label}>Nome</Text>
      <TextInput style={estilos.input} value={nome} onChangeText={setNome} />

      <Text style={estilos.label}>E-mail</Text>
      <TextInput
        style={estilos.input}
        autoCapitalize="none"
        keyboardType="email-address"
        value={email}
        onChangeText={setEmail}
      />

      <Text style={estilos.label}>Senha</Text>
      <TextInput style={estilos.input} secureTextEntry value={senha} onChangeText={setSenha} />

      {!!erro && <Text style={estilos.erro}>{erro}</Text>}

      <TouchableOpacity style={estilos.botao} onPress={cadastrar} disabled={carregando}>
        <Text style={estilos.botaoTexto}>{carregando ? 'Cadastrando...' : 'Cadastrar'}</Text>
      </TouchableOpacity>
    </ScrollView>
  );
}

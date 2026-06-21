import React, { useState } from 'react';
import { Text, TextInput, TouchableOpacity, ScrollView } from 'react-native';
import { useAuth } from '../context/AuthContext';
import { estilos } from '../theme';
import type { LoginScreenProps } from '../types';

export default function LoginScreen({ navigation }: LoginScreenProps) {
  const { login, entrarComoVisitante } = useAuth();
  const [email, setEmail] = useState('');
  const [senha, setSenha] = useState('');
  const [erro, setErro] = useState('');
  const [carregando, setCarregando] = useState(false);

  async function entrar() {
    setErro('');
    setCarregando(true);
    try {
      await login(email.trim(), senha);
    } catch (e: any) {
      setErro(e.message);
    } finally {
      setCarregando(false);
    }
  }

  return (
    <ScrollView style={estilos.tela} contentContainerStyle={estilos.conteudo}>
      <Text style={estilos.titulo}>Entrar</Text>

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

      <TouchableOpacity style={estilos.botao} onPress={entrar} disabled={carregando}>
        <Text style={estilos.botaoTexto}>{carregando ? 'Entrando...' : 'Entrar'}</Text>
      </TouchableOpacity>

      <TouchableOpacity onPress={() => navigation.navigate('Cadastro')}>
        <Text style={estilos.link}>Criar uma conta</Text>
      </TouchableOpacity>
      <TouchableOpacity onPress={() => navigation.navigate('RecuperarSenha')}>
        <Text style={estilos.link}>Esqueci minha senha</Text>
      </TouchableOpacity>
      <TouchableOpacity onPress={entrarComoVisitante}>
        <Text style={estilos.link}>Explorar sem login</Text>
      </TouchableOpacity>
    </ScrollView>
  );
}

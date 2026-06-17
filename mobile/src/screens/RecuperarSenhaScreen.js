import React, { useState } from 'react';
import { View, Text, TextInput, TouchableOpacity, ScrollView } from 'react-native';
import { api } from '../api';
import { estilos } from '../theme';

export default function RecuperarSenhaScreen() {
  const [email, setEmail] = useState('');
  const [mensagem, setMensagem] = useState('');
  const [erro, setErro] = useState('');
  const [carregando, setCarregando] = useState(false);

  async function recuperar() {
    setErro('');
    setMensagem('');
    setCarregando(true);
    try {
      const res = await api.recuperarSenha(email.trim());
      setMensagem(res.mensagem || 'Token de recuperação enviado.');
    } catch (e) {
      setErro(e.message);
    } finally {
      setCarregando(false);
    }
  }

  return (
    <ScrollView style={estilos.tela} contentContainerStyle={estilos.conteudo}>
      <Text style={estilos.titulo}>Recuperar senha</Text>

      <Text style={estilos.label}>E-mail cadastrado</Text>
      <TextInput
        style={estilos.input}
        autoCapitalize="none"
        keyboardType="email-address"
        value={email}
        onChangeText={setEmail}
      />

      {!!mensagem && <Text style={{ color: '#0b6e4f', marginTop: 12 }}>{mensagem}</Text>}
      {!!erro && <Text style={estilos.erro}>{erro}</Text>}

      <TouchableOpacity style={estilos.botao} onPress={recuperar} disabled={carregando}>
        <Text style={estilos.botaoTexto}>{carregando ? 'Enviando...' : 'Enviar'}</Text>
      </TouchableOpacity>
    </ScrollView>
  );
}

import React, { useState } from 'react';
import { Text, TextInput, TouchableOpacity, ScrollView, ActivityIndicator, View } from 'react-native';
import { api } from '../api';
import { estilos } from '../theme';

export default function RecuperarSenhaScreen() {
  const [email, setEmail] = useState('');
  const [token, setToken] = useState('');
  const [novaSenha, setNovaSenha] = useState('');
  const [confirmarSenha, setConfirmarSenha] = useState('');

  const [tokenEnviado, setTokenEnviado] = useState(false); // Controla a transição de etapas
  const [mensagem, setMensagem] = useState('');
  const [erro, setErro] = useState('');
  const [carregando, setCarregando] = useState(false);

  // Validação simples de e-mail no front-end
  function validarEmail(emailInformado: string) {
    const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return regex.test(emailInformado);
  }

  // 1ª Etapa: Solicitar o Token por e-mail
  async function solicitarToken() {
    const emailFormatado = email.trim();

    if (!emailFormatado) {
      setErro('Por favor, informe o seu e-mail.');
      return;
    }

    if (!validarEmail(emailFormatado)) {
      setErro('Por favor, insira um e-mail válido.');
      return;
    }

    setErro('');
    setMensagem('');
    setCarregando(true);

    try {
      const res = await api.recuperarSenha(emailFormatado);
      setMensagem(res.mensagem || 'Token de recuperação enviado com sucesso.');
      setTokenEnviado(true); // Exibe os campos da nova senha
    } catch (e: any) {
      setErro(e.message);
    } finally {
      setCarregando(false);
    }
  }

  // 2ª Etapa: Enviar a Nova Senha e o Token digitado
  async function redefinirSenha() {
    const tokenFormatado = token.trim();
    const emailFormatado = email.trim();

    if (!tokenFormatado) {
      setErro('Por favor, insira o token que você recebeu.');
      return;
    }

    if (!novaSenha) {
      setErro('Por favor, digite sua nova senha.');
      return;
    }

    if (novaSenha.length < 6) {
      setErro('A senha deve conter no mínimo 6 caracteres.');
      return;
    }

    if (novaSenha !== confirmarSenha) {
      setErro('As senhas não coincidem.');
      return;
    }

    setErro('');
    setMensagem('');
    setCarregando(true);

    try {
      // Envia os dados para o endpoint mapeado na sua api
      const res = await api.redefinirSenha(emailFormatado, tokenFormatado, novaSenha);
      setMensagem(res.mensagem || 'Sua senha foi redefinida com sucesso!');
      
      // Limpa os campos de senha por segurança
      setToken('');
      setNovaSenha('');
      setConfirmarSenha('');
    } catch (e: any) {
      setErro(e.message);
    } finally {
      setCarregando(false);
    }
  }

  return (
    <ScrollView style={estilos.tela} contentContainerStyle={estilos.conteudo}>
      <Text style={estilos.titulo}>Recuperar senha</Text>

      {/* Campo de E-mail (Sempre visível, mas bloqueado após o token ser enviado) */}
      <Text style={estilos.label}>E-mail cadastrado</Text>
      <TextInput
        style={[estilos.input, tokenEnviado && { backgroundColor: '#e2e8f0', color: '#64748b' }]}
        autoCapitalize="none"
        autoCorrect={false}
        keyboardType="email-address"
        placeholder="exemplo@email.com"
        placeholderTextColor="#999"
        value={email}
        onChangeText={(text) => {
          setEmail(text);
          if (erro) setErro('');
        }}
        editable={!carregando && !tokenEnviado}
      />

      {/* Se o token já foi enviado, renderiza os campos para digitar a nova senha */}
      {tokenEnviado && (
        <View style={{ width: '100%' }}>
          <Text style={estilos.label}>Token enviado</Text>
          <TextInput
            style={estilos.input}
            autoCapitalize="none"
            autoCorrect={false}
            placeholder="Digite ou cole o token"
            placeholderTextColor="#999"
            value={token}
            onChangeText={(text) => {
              setToken(text);
              if (erro) setErro('');
            }}
            editable={!carregando}
          />

          <Text style={estilos.label}>Nova Senha</Text>
          <TextInput
            style={estilos.input}
            secureTextEntry
            autoCapitalize="none"
            autoCorrect={false}
            placeholder="No mínimo 6 caracteres"
            placeholderTextColor="#999"
            value={novaSenha}
            onChangeText={(text) => {
              setNovaSenha(text);
              if (erro) setErro('');
            }}
            editable={!carregando}
          />

          <Text style={estilos.label}>Confirmar Nova Senha</Text>
          <TextInput
            style={estilos.input}
            secureTextEntry
            autoCapitalize="none"
            autoCorrect={false}
            placeholder="Repita a nova senha"
            placeholderTextColor="#999"
            value={confirmarSenha}
            onChangeText={(text) => {
              setConfirmarSenha(text);
              if (erro) setErro('');
            }}
            editable={!carregando}
          />
        </View>
      )}

      {/* Mensagens de feedback */}
      {!!mensagem && <Text style={{ color: '#0b6e4f', marginTop: 12, fontWeight: '600' }}>{mensagem}</Text>}
      {!!erro && <Text style={estilos.erro}>{erro}</Text>}

      {/* Botão Dinâmico que muda o texto e a ação dependendo da etapa */}
      <TouchableOpacity 
        style={[estilos.botao, carregando && { opacity: 0.6 }, { marginTop: 20 }]} 
        onPress={tokenEnviado ? redefinirSenha : solicitarToken} 
        disabled={carregando}
      >
        {carregando ? (
          <ActivityIndicator color="#FFF" />
        ) : (
          <Text style={estilos.botaoTexto}>
            {tokenEnviado ? 'Alterar Senha' : 'Enviar'}
          </Text>
        )}
      </TouchableOpacity>

      {/* Link auxiliar caso o usuário queira corrigir o e-mail digitado */}
      {tokenEnviado && !carregando && (
        <TouchableOpacity 
          onPress={() => {
            setTokenEnviado(false);
            setMensagem('');
            setErro('');
          }} 
          style={{ marginTop: 16 }}
        >
          <Text style={{ color: '#666', fontSize: 13, textDecorationLine: 'underline', textAlign: 'center' }}>
            Alterar e-mail informado
          </Text>
        </TouchableOpacity>
      )}
    </ScrollView>
  );
}
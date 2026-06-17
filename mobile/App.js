import React from 'react';
import { ActivityIndicator, View } from 'react-native';
import { StatusBar } from 'expo-status-bar';
import { NavigationContainer } from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';

import { AuthProvider, useAuth } from './src/context/AuthContext';
import { cores } from './src/theme';

import LoginScreen from './src/screens/LoginScreen';
import CadastroScreen from './src/screens/CadastroScreen';
import RecuperarSenhaScreen from './src/screens/RecuperarSenhaScreen';
import HomeScreen from './src/screens/HomeScreen';
import PartidasScreen from './src/screens/PartidasScreen';
import PartidaDetalheScreen from './src/screens/PartidaDetalheScreen';
import MeusPalpitesScreen from './src/screens/MeusPalpitesScreen';
import RankingScreen from './src/screens/RankingScreen';
import PerfilScreen from './src/screens/PerfilScreen';

const Stack = createNativeStackNavigator();
const Tab = createBottomTabNavigator();

const telaCabecalho = {
  headerStyle: { backgroundColor: cores.primaria },
  headerTintColor: '#fff',
};

function PartidasStack() {
  return (
    <Stack.Navigator screenOptions={telaCabecalho}>
      <Stack.Screen name="Partidas" component={PartidasScreen} />
      <Stack.Screen name="PartidaDetalhe" component={PartidaDetalheScreen} options={{ title: 'Detalhe da partida' }} />
    </Stack.Navigator>
  );
}

function AppTabs() {
  return (
    <Tab.Navigator
      screenOptions={{
        ...telaCabecalho,
        tabBarActiveTintColor: cores.primaria,
      }}
    >
      <Tab.Screen name="Início" component={HomeScreen} />
      <Tab.Screen name="Jogos" component={PartidasStack} options={{ headerShown: false }} />
      <Tab.Screen name="Meus Palpites" component={MeusPalpitesScreen} />
      <Tab.Screen name="Ranking" component={RankingScreen} />
      <Tab.Screen name="Perfil" component={PerfilScreen} />
    </Tab.Navigator>
  );
}

function AuthStack() {
  return (
    <Stack.Navigator screenOptions={telaCabecalho}>
      <Stack.Screen name="Login" component={LoginScreen} options={{ title: 'Bolão Copa 2026' }} />
      <Stack.Screen name="Cadastro" component={CadastroScreen} options={{ title: 'Criar conta' }} />
      <Stack.Screen name="RecuperarSenha" component={RecuperarSenhaScreen} options={{ title: 'Recuperar senha' }} />
    </Stack.Navigator>
  );
}

function Rotas() {
  const { usuario, carregando } = useAuth();

  if (carregando) {
    return (
      <View style={{ flex: 1, justifyContent: 'center', alignItems: 'center', backgroundColor: cores.fundo }}>
        <ActivityIndicator size="large" color={cores.primaria} />
      </View>
    );
  }

  return (
    <NavigationContainer>
      {usuario ? <AppTabs /> : <AuthStack />}
    </NavigationContainer>
  );
}

export default function App() {
  return (
    <AuthProvider>
      <StatusBar style="light" />
      <Rotas />
    </AuthProvider>
  );
}

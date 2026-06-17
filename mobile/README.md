# Bolão Copa 2026 — App Mobile (React Native / Expo)

Artefato 2 do Hackathon: app mobile que consome a API REST do backend Spring Boot.

## Stack

- React Native (Expo SDK 51)
- React Navigation (stack + tabs)
- AsyncStorage (token JWT)

## Configuração

Antes de rodar, ajuste o endereço do backend em [`src/config.js`](src/config.js):

```js
export const API_URL = 'http://10.0.2.2:8080'; // emulador Android
```

- Emulador Android: `http://10.0.2.2:8080`
- iOS Simulator: `http://localhost:8080`
- Dispositivo físico (Expo Go): `http://SEU_IP_LOCAL:8080`

## Rodar

```bash
cd mobile
npm install
npm start
```

Abra no Expo Go (QR code), `npm run android` ou `npm run ios`.

## Telas / Requisitos atendidos

- **Login / Cadastro / Recuperar senha** — RF-001, RF-002, RF-003
- **Início** — próximas partidas para palpitar (RF-013)
- **Jogos** — listar por fase, filtrar por fase/status, detalhe da partida (RF-010, RF-011, RF-012)
- **Detalhe da partida** — registrar/editar palpite com bloqueio após início (RF-020, RF-021, RF-022)
- **Meus Palpites** — lista com pontuação e critério (RF-023, RF-024)
- **Ranking** — ranking geral paginado com a posição do usuário (RF-032, RF-033, RF-034)
- **Perfil** — editar nome/avatar, logout e exclusão de conta (RF-004, RF-005, RF-006)

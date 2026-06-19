# Bolão Copa do Mundo 2026 - Java/Spring Boot

Projeto criado a partir da estrutura do `biblioteca-master`, seguindo o escopo do PDF do Hackathon.

## Stack

- Java 21+
- Spring Boot 4.x
- Spring Web MVC
- Spring Data JPA
- Spring Security
- JWT
- MySQL
- Thymeleaf
- Bootstrap

## Banco de dados

Crie ou deixe o Spring criar o banco `bolao_copa` no MySQL.

Ajuste em `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/bolao_copa?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=America/Sao_Paulo
spring.datasource.username=root
spring.datasource.password=
```

## Rodar

```bash
./mvnw spring-boot:run
```

No Windows:

```bash
mvnw.cmd spring-boot:run
```

## Login admin padrão

- E-mail: `admin@bolao.com`
- Senha: `admin123`

## Painel web

- `/login`
- `/dashboard`
- `/selecoes`
- `/partidas`
- `/usuarios`

## API REST

### Auth

`POST /api/auth/cadastro`

```json
{
  "nome": "João",
  "email": "joao@email.com",
  "senha": "123456"
}
```

`POST /api/auth/login`

```json
{
  "email": "joao@email.com",
  "senha": "123456"
}
```

### Partidas públicas

- `GET /api/partidas`
- `GET /api/partidas/{id}`

### Palpites

Usar header:

```http
Authorization: Bearer SEU_TOKEN
```

`POST /api/palpites`

```json
{
  "partidaId": 1,
  "golsA": 2,
  "golsB": 1
}
```

### Ranking

- `GET /api/ranking`
- `GET /api/ranking/me`

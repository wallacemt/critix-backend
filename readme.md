# Critix - Backend

![License](https://img.shields.io/static/v1?label=license&message=MIT&color=orange) &nbsp;
![Cargo version](https://img.shields.io/static/v1?label=cargo&message=v0.1.0&color=yellow) &nbsp;
![Repository size](https://img.shields.io/github/repo-size/wallacemt/squad-17-backend?color=blue) &nbsp;
![Pull request](https://img.shields.io/static/v1?label=PR&message=welcome&color=green)

## Indices

- [`Sobre o Projeto`](#sobre-o-projeto)
- [`Tecnologias Utilizadas`](#tecnologias-utilizadas)
- [`Estrutura do Projeto`](#estrutura-projeto)
- [`Configuração é Execução`](#configuracao-execucao) 
- [`Principais Endpoints`](#endpoints) 
- [`Autenticação é Segurança`](#autenticacao)
- [`WebSockets`](#websocket) 
- [`Banco de Dados`](#database) 
- [`Contribuições`](#contribuicoes) 
- [`Licença`](#license) 

<span id="sobre-o-projeto"></span>

## 📌 Sobre o Projeto
O backend do **Critix** é responsável por fornecer a API REST que alimenta a plataforma de avaliação de filmes e séries. Ele gerencia autenticação, usuários, avaliações e interações, garantindo um serviço seguro e eficiente.


## 🚀 Tecnologias Utilizadas

<div align='center' id="tecnologias-utilizadas">
    <img align='center' height='49' width='49' title='Java' alt='Java' src='https://cdn.jsdelivr.net/gh/devicons/devicon@latest/icons/java/java-original-wordmark.svg' /> &nbsp;
    <img align='center' height='48' width='48' title='Spring' alt='TailSpringwind' src=' https://cdn.jsdelivr.net/gh/devicons/devicon@latest/icons/spring/spring-original.svg' /> &nbsp;
    <img align='center' height='49' width='49' title='JWT' alt='JWT' src='https://cdn.worldvectorlogo.com/logos/jwt-3.svg' /> &nbsp;
   <img align='center' height='49' width='49' title='Dotenv' alt='dotenv' src='https://github.com/bush1D3v/navarro_blog_api/assets/133554156/de030e87-8f12-4b6b-8c75-071bab8526a5' /> &nbsp;
   <img align='center' height='50' width='50' title='Cors' alt='cors' src='https://github.com/bush1D3v/navarro_blog_api/assets/133554156/5dcd815b-e815-453b-9f3f-71e7dbcdf71d' />
   <img align='center' height='60' width='70' title='Swagger' alt='swagger' src='https://github.com/bush1D3v/tsbank_api/assets/133554156/6739401f-d03b-47f8-b01f-88da2a9075d1' />
   <img align='center' height='70' width='70' title='Docker' alt='docker' src='https://cdn.jsdelivr.net/gh/devicons/devicon@latest/icons/docker/docker-original.svg' /> &nbsp;
   <img align='center' height='48' width='48' title='Bcrypt' alt='bcrypt' src='https://github.com/bush1D3v/navarro_blog_api/assets/133554156/8d9137f8-cd85-4629-be08-c639db52088d' /> &nbsp;
    <img align='center' height='48' width='48' title='MySql' alt='MySql' src='https://cdn.jsdelivr.net/gh/devicons/devicon@latest/icons/mysql/mysql-original-wordmark.svg
    ' /> &nbsp;
    <img align='center' height='48' width='48' style="filter: invert(1);" title='WebSocket' alt='WebSocket' src='https://www.svgrepo.com/show/354553/websocket.svg' /> &nbsp;
    <img align='center' height='48' width='48'  title='Postman' alt='Postman' src='
        https://cdn.jsdelivr.net/gh/devicons/devicon@latest/icons/postman/postman-original.svg' /> &nbsp;
    <img align='center' height='48' width='48'  title='Junit' alt='Junit' src='https://cdn.jsdelivr.net/gh/devicons/devicon@latest/icons/junit/junit-original-wordmark.svg' /> &nbsp;
    <img align='center' height='48' width='48'  title='Junit' alt='Junit' src='https://cdn.jsdelivr.net/gh/devicons/devicon@latest/icons/oauth/oauth-original.svg' /> &nbsp;
</div>

<span id="estrutura-projeto"></span>
## 📂 Estrutura do Projeto
```
critix-backend/
│-- src/
│   ├── main/java/br/com/projeto/
│   │   ├── config/
│   │   ├── controle/
│   │   ├── dto/
│   │   ├── infra/
│   │   ├── middleware/
│   │   ├── models/
│   │   ├── repositorio/
│   │   ├── service/
│   │   ├── ultils/
│   ├── main/resources/
│   │   ├── application.properties
│-- test/
│-- docker-compose.yml
│-- Dockerfile
│-- README.md
```

<span id="configuracao-execucao"></span>
## 🛠️ Configuração e Execução

### 1️⃣ Clonar o Repositório

```bash
git clone https://github.com/wallacemt/critix-backend.git
cd critix-backend
```

### 2️⃣ Configurar o Banco de Dados

O projeto utiliza **MySQL via Docker**. Para subir o banco de dados, execute:

```bash
docker-compose up -d
```

Se preferir rodar manualmente, configure as credenciais no `application.properties`.

### 3️⃣ Rodar a Aplicação

```bash
./mvnw spring-boot:run
```

Ou, se estiver usando Java diretamente:

```bash
java -jar target/critix-backend.jar
```


<span id="endpoints"></span>

## 📌 Endpoints Principais

A API está documentada no **Postman**. Você pode importar a coleção utilizando o arquivo `critix-api.json` incluído no projeto.

### 🔑 Autenticação

- `POST /auth/login` - Realiza o login e retorna um token JWT
- `POST /auth/register` - Cria um novo usuário

### 📋 Avaliações (Protegida)

- `GET /reviews` - Lista todas as avaliações do usuário
- `POST /reviews` - Cria uma nova avaliação

### 👤 Usuários (Protegida)

- `GET /user/{id}` - Retorna informações públicas do usuário
- `GET /auth/user` - Retorna dados do usuário autenticado

### 📝 Watchlist (Protegida)

- `GET /watchlist/{status}` - Retorna watchlist do usuario pelo status
- `POST /watchlist` - Adiciona um item ná watchlist

### 🤳 Follower (Protegida)

- `POST /follow/:id` - Segue um usuário pelo ID
- `DEL /auth/user` - Deixa de seguir um usuário

### 🔔 Notifications (Protegida)

- `POST /user/notifications` - Retorna as notificações do usuário
- `DEL user/notifications/:id/seen` - Marca uma notificação como visualizada!


<span id="autenticacao"></span>

## 🔒 Autenticação e Segurança

A API utiliza **JWT** para autenticação. Para acessar rotas protegidas, adicione o token no cabeçalho:

```json
Authorization: Bearer <seu_token>
```

<span id="websocket"></span>

## 🔗 WebSocket (Notificações em Tempo Real)

O Critix possui notificações em tempo real via WebSockets. Para consumir:

- **Endpoint WebSocket:** `ws://localhost:8081/ws`
- **Eventos Suportados:** `NEW_REVIEW`, `NEW_COMMENT`, `LIKE_RECEIVED`, `NEW_FOLLOW`


<span id="database"></span>

## 🗂️ Banco de Dados

### Diagrama Do Banco:

<div align='center'>
<img align='center' height='750' width='800' style="border-radius:1.5rem"  title='Junit' alt='Junit' src='https://res.cloudinary.com/dg9hqvlas/image/upload/v1741207924/diagrama_t7o1ic.png' /> &nbsp;
</div>


<span id="contribuicoes"></span>

## 🛠 Contribuição

Ficou interessado em contribuir? Faça um **fork** do repositório, crie uma **branch**, implemente a melhoria e envie um **pull request**. Toda ajuda é bem-vinda!

1. **Fork the repository.**
2. **Clone your forked repository to your local machine.**
3. **Create a branch for your feature or fix:**

   ```bash
   git checkout -b my-new-feature
   ```

4. **Commit your changes:**

   ```bash
   git commit -m 'Add new feature'
   ```

5. **Push your changes to your fork:**

   ```bash
   git push origin my-new-feature
   ```

6. **Create a Pull Request.**


<span id="license"></span>

## 📜 Licença

Este projeto está sob a licença MIT.

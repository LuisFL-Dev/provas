# Sistema de Provas

Sistema web para professores criarem provas de múltipla escolha e alunos responderem, com correção automática.

Projeto de Estágio Supervisionado – Sistemas de Informação – UNIFACOL

**Autores:** Luis Fernando e Edvanilson Oliveira

## Tecnologias

- **Backend:** Java 21, Spring Boot, Spring Security (JWT), Spring Data JPA, Flyway
- **Banco de dados:** PostgreSQL 16 (Docker)
- **Frontend:** React (Vite), React Router, Axios
- **Testes da API:** Postman (collection na pasta `postman/`)

## Pré-requisitos

- JDK 21
- Docker Desktop
- Node.js 22

## Como rodar

**1. Subir o banco** (na raiz do projeto):

```
docker compose up -d
```

**2. Subir a API** (na pasta `backend`):

```
.\mvnw.cmd spring-boot:run
```

No Linux ou Mac, use `./mvnw spring-boot:run`. A API fica em http://localhost:8080

**3. Subir o frontend** (na pasta `frontend`, em outro terminal):

```
npm install
npm run dev
```

O site fica em http://localhost:5173

## Usuários de teste

| Perfil    | E-mail               | Senha  |
|-----------|----------------------|--------|
| Professor | professor@provas.com | 123456 |
| Aluno     | aluno@provas.com     | 123456 |

Alunos novos podem criar conta pela tela de login.

## Endpoints

| Método | Caminho                        | Acesso    |
|--------|--------------------------------|-----------|
| POST   | /auth/login                    | Público   |
| POST   | /auth/registro                 | Público   |
| GET    | /provas                        | Logado    |
| GET    | /provas/{id}                   | Logado    |
| POST   | /provas                        | Professor |
| DELETE | /provas/{id}                   | Professor |
| POST   | /provas/{id}/respostas         | Aluno     |
| GET    | /provas/{id}/minhas-tentativas | Aluno     |
| GET    | /tentativas/minhas             | Aluno     |

## Regras das provas

- Cada aluno pode fazer a mesma prova no máximo 2 vezes.
- O tempo máximo é de 1 hora. Quando o tempo acaba, a prova é enviada automaticamente e as questões sem resposta contam como erradas.
- A nota vai de 0 a 10: acertos × 10 ÷ total de questões.
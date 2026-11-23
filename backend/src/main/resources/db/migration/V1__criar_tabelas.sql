CREATE TABLE usuarios (
    id      BIGSERIAL PRIMARY KEY,
    nome    VARCHAR(100) NOT NULL,
    email   VARCHAR(150) NOT NULL UNIQUE,
    senha   VARCHAR(255) NOT NULL,
    perfil  VARCHAR(20)  NOT NULL
);

CREATE TABLE provas (
    id         BIGSERIAL PRIMARY KEY,
    titulo     VARCHAR(150) NOT NULL,
    descricao  VARCHAR(1000),
    criada_em  TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE questoes (
    id         BIGSERIAL PRIMARY KEY,
    prova_id   BIGINT NOT NULL REFERENCES provas(id) ON DELETE CASCADE,
    enunciado  VARCHAR(1000) NOT NULL,
    ordem      INT NOT NULL
);

CREATE TABLE alternativas (
    id          BIGSERIAL PRIMARY KEY,
    questao_id  BIGINT NOT NULL REFERENCES questoes(id) ON DELETE CASCADE,
    texto       VARCHAR(500) NOT NULL,
    correta     BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE tentativas (
    id            BIGSERIAL PRIMARY KEY,
    aluno_id      BIGINT NOT NULL REFERENCES usuarios(id),
    prova_id      BIGINT NOT NULL REFERENCES provas(id) ON DELETE CASCADE,
    acertos       INT NOT NULL,
    total         INT NOT NULL,
    nota          NUMERIC(4,2) NOT NULL,
    realizada_em  TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE respostas (
    id              BIGSERIAL PRIMARY KEY,
    tentativa_id    BIGINT NOT NULL REFERENCES tentativas(id) ON DELETE CASCADE,
    questao_id      BIGINT NOT NULL REFERENCES questoes(id) ON DELETE CASCADE,
    alternativa_id  BIGINT NOT NULL REFERENCES alternativas(id) ON DELETE CASCADE,
    correta         BOOLEAN NOT NULL
);
-- Aura AI - schema do banco (PostgreSQL)
-- Execute no DBeaver conectado ao banco aura_ai.
-- Para recriar do zero, descomente o bloco DROP abaixo.

-- DROP TABLE IF EXISTS analise_produto, alerta, analise_ia, transcricao, reuniao, produto_totvs, cliente CASCADE;

CREATE TABLE IF NOT EXISTS cliente (
    id        BIGSERIAL    PRIMARY KEY,
    nome      VARCHAR(150) NOT NULL,
    cnpj      VARCHAR(20)  NOT NULL UNIQUE,
    segmento  VARCHAR(100) NOT NULL,
    email     VARCHAR(150) NOT NULL
);

CREATE TABLE IF NOT EXISTS produto_totvs (
    id         BIGSERIAL    PRIMARY KEY,
    nome       VARCHAR(150) NOT NULL,
    categoria  VARCHAR(100) NOT NULL,
    versao     VARCHAR(50)  NOT NULL,
    descricao  TEXT         NOT NULL
);

CREATE TABLE IF NOT EXISTS reuniao (
    id           BIGSERIAL    PRIMARY KEY,
    titulo       VARCHAR(150) NOT NULL,
    data_hora    TIMESTAMP    NOT NULL,
    duracao_min  INT          NOT NULL CONSTRAINT ck_reuniao_duracao CHECK (duracao_min > 0),
    responsavel  VARCHAR(150) NOT NULL,
    status       VARCHAR(30)  NOT NULL CONSTRAINT ck_reuniao_status CHECK (status IN ('PENDENTE', 'PROCESSADA')),
    cliente_id   BIGINT       NOT NULL,
    CONSTRAINT fk_reuniao_cliente FOREIGN KEY (cliente_id) REFERENCES cliente (id)
);

CREATE TABLE IF NOT EXISTS transcricao (
    id            BIGSERIAL   PRIMARY KEY,
    texto_bruto   TEXT        NOT NULL,
    qtd_palavras  INT         NOT NULL,
    idioma        VARCHAR(20) NOT NULL,
    reuniao_id    BIGINT      NOT NULL UNIQUE,
    CONSTRAINT fk_transcricao_reuniao FOREIGN KEY (reuniao_id) REFERENCES reuniao (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS analise_ia (
    id            BIGSERIAL    PRIMARY KEY,
    sentimento    VARCHAR(30),
    risco_churn   NUMERIC(4,2) CONSTRAINT ck_analise_churn CHECK (risco_churn BETWEEN 0 AND 1),
    oportunidade  NUMERIC(4,2) CONSTRAINT ck_analise_oportunidade CHECK (oportunidade BETWEEN 0 AND 1),
    persona       VARCHAR(100),
    budget        NUMERIC(12,2),
    concorrente   VARCHAR(100),
    reuniao_id    BIGINT       NOT NULL,
    CONSTRAINT fk_analise_reuniao FOREIGN KEY (reuniao_id) REFERENCES reuniao (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS alerta (
    id          BIGSERIAL   PRIMARY KEY,
    tipo        VARCHAR(50) NOT NULL,
    descricao   TEXT        NOT NULL,
    prioridade  VARCHAR(30) NOT NULL,
    status      VARCHAR(30) NOT NULL,
    analise_id  BIGINT      NOT NULL,
    CONSTRAINT fk_alerta_analise FOREIGN KEY (analise_id) REFERENCES analise_ia (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS analise_produto (
    analise_id   BIGINT       NOT NULL,
    produto_id   BIGINT       NOT NULL,
    contexto     TEXT         NOT NULL,
    relevancia   NUMERIC(4,2) NOT NULL CONSTRAINT ck_analise_produto_relevancia CHECK (relevancia BETWEEN 0 AND 1),
    tipo_mencao  VARCHAR(50)  NOT NULL,
    PRIMARY KEY (analise_id, produto_id),
    CONSTRAINT fk_analise_produto_analise FOREIGN KEY (analise_id) REFERENCES analise_ia (id) ON DELETE CASCADE,
    CONSTRAINT fk_analise_produto_produto FOREIGN KEY (produto_id) REFERENCES produto_totvs (id)
);

-- O PostgreSQL não cria índice automático nas chaves estrangeiras
CREATE INDEX IF NOT EXISTS idx_reuniao_cliente         ON reuniao (cliente_id);
CREATE INDEX IF NOT EXISTS idx_analise_ia_reuniao      ON analise_ia (reuniao_id);
CREATE INDEX IF NOT EXISTS idx_alerta_analise          ON alerta (analise_id);
CREATE INDEX IF NOT EXISTS idx_analise_produto_produto ON analise_produto (produto_id);

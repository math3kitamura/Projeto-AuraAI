-- Aplica as correções ao banco que JÁ existe (criado antes desta versão).
-- Rode uma vez no DBeaver. Se algo falhar, o ROLLBACK é automático.
-- Antes: SELECT DISTINCT status FROM reuniao;  (só pode haver PENDENTE e PROCESSADA)

BEGIN;

ALTER TABLE transcricao
    DROP CONSTRAINT fk_transcricao_reuniao,
    ADD CONSTRAINT fk_transcricao_reuniao FOREIGN KEY (reuniao_id) REFERENCES reuniao (id) ON DELETE CASCADE;

ALTER TABLE analise_ia
    DROP CONSTRAINT fk_analise_reuniao,
    ADD CONSTRAINT fk_analise_reuniao FOREIGN KEY (reuniao_id) REFERENCES reuniao (id) ON DELETE CASCADE;

ALTER TABLE alerta
    DROP CONSTRAINT fk_alerta_analise,
    ADD CONSTRAINT fk_alerta_analise FOREIGN KEY (analise_id) REFERENCES analise_ia (id) ON DELETE CASCADE;

ALTER TABLE analise_produto
    DROP CONSTRAINT fk_analise_produto_analise,
    ADD CONSTRAINT fk_analise_produto_analise FOREIGN KEY (analise_id) REFERENCES analise_ia (id) ON DELETE CASCADE;

CREATE INDEX IF NOT EXISTS idx_reuniao_cliente         ON reuniao (cliente_id);
CREATE INDEX IF NOT EXISTS idx_analise_ia_reuniao      ON analise_ia (reuniao_id);
CREATE INDEX IF NOT EXISTS idx_alerta_analise          ON alerta (analise_id);
CREATE INDEX IF NOT EXISTS idx_analise_produto_produto ON analise_produto (produto_id);

ALTER TABLE reuniao
    ADD CONSTRAINT ck_reuniao_duracao CHECK (duracao_min > 0),
    ADD CONSTRAINT ck_reuniao_status  CHECK (status IN ('PENDENTE', 'PROCESSADA'));

ALTER TABLE analise_ia
    ADD CONSTRAINT ck_analise_churn        CHECK (risco_churn BETWEEN 0 AND 1),
    ADD CONSTRAINT ck_analise_oportunidade CHECK (oportunidade BETWEEN 0 AND 1);

ALTER TABLE analise_produto
    ADD CONSTRAINT ck_analise_produto_relevancia CHECK (relevancia BETWEEN 0 AND 1);

COMMIT;

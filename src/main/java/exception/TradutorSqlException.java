package exception;

import java.sql.SQLException;

/**
 * Converte os erros do PostgreSQL (pelo SQLState) em exceções da aplicação,
 * evitando expor mensagens técnicas ao usuário.
 */
public final class TradutorSqlException {

    private static final String VIOLACAO_FK = "23503";
    private static final String VIOLACAO_UNICA = "23505";
    private static final String VIOLACAO_CHECK = "23514";
    private static final String TEXTO_LONGO_DEMAIS = "22001";

    private TradutorSqlException() {
    }

    public static AuraAIException traduzir(
            SQLException e,
            String mensagemConflito
    ) {

        String estado = e.getSQLState();

        if (VIOLACAO_FK.equals(estado) || VIOLACAO_UNICA.equals(estado)) {
            return new ConflitoException(
                    mensagemConflito != null
                            ? mensagemConflito
                            : "Operação não permitida: há registros duplicados ou vinculados.",
                    e
            );
        }

        if (VIOLACAO_CHECK.equals(estado)) {
            return new RegraNegocioException(
                    "Os dados informados violam uma regra de integridade do banco."
            );
        }

        if (TEXTO_LONGO_DEMAIS.equals(estado)) {
            return new RegraNegocioException(
                    "Um dos campos excede o tamanho máximo permitido."
            );
        }

        return new ErroPersistenciaException(
                "Erro ao acessar o banco de dados.",
                e
        );
    }
}

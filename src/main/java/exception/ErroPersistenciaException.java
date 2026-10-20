package exception;

/** Falha inesperada no acesso ao banco de dados (HTTP 500). */
public class ErroPersistenciaException extends AuraAIException {

    public ErroPersistenciaException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}

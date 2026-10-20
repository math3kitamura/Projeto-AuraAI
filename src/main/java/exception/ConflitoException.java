package exception;

/** Conflito com o estado atual dos dados, como duplicidade ou vínculo existente (HTTP 409). */
public class ConflitoException extends AuraAIException {

    public ConflitoException(String mensagem) {
        super(mensagem);
    }

    public ConflitoException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}

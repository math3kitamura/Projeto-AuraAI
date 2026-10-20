package br.com.fiap.auraai.exception;

/**
 * Exceção base da aplicação. As subclasses indicam o tipo do erro,
 * que a API converte no código HTTP correspondente.
 */
public class AuraAIException extends RuntimeException {

    public AuraAIException(String mensagem) {
        super(mensagem);
    }

    public AuraAIException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}

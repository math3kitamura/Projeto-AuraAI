package br.com.fiap.auraai.exception;

/** Requisição válida, mas que viola uma regra de negócio (HTTP 422). */
public class RegraNegocioException extends AuraAIException {

    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}

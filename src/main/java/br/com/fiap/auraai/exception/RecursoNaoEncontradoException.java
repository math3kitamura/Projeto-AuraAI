package br.com.fiap.auraai.exception;

/** Recurso inexistente (HTTP 404). */
public class RecursoNaoEncontradoException extends AuraAIException {

    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}

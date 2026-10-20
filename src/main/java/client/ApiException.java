package client;

import exception.AuraAIException;

/**
 * Erro ao se comunicar com a API REST. Quando a API respondeu,
 * guarda o código HTTP; quando não foi possível conectar, status = 0.
 */
public class ApiException extends AuraAIException {

    private final int status;

    public ApiException(String mensagem, int status) {
        super(mensagem);
        this.status = status;
    }

    public ApiException(String mensagem, Throwable causa) {
        super(mensagem, causa);
        this.status = 0;
    }

    public int getStatus() {
        return status;
    }
}

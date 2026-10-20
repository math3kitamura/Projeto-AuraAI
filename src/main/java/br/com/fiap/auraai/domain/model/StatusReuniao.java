package br.com.fiap.auraai.domain.model;

public enum StatusReuniao {

    PENDENTE,
    PROCESSADA;

    /** Converte o texto vindo do banco ou da API, ignorando caixa e espaços. */
    public static StatusReuniao deTexto(String texto) {

        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException(
                    "O status da reunião não pode ficar em branco."
            );
        }

        try {
            return valueOf(texto.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Status de reunião inválido: " + texto + "."
            );
        }
    }
}

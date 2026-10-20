package br.com.fiap.auraai.domain.model;

public enum Sentimento {

    POSITIVO,
    NEGATIVO,
    NEUTRO;

    /** Converte o texto vindo do banco ou da API; texto vazio resulta em null. */
    public static Sentimento deTexto(String texto) {

        if (texto == null || texto.isBlank()) {
            return null;
        }

        try {
            return valueOf(texto.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Sentimento inválido: " + texto + "."
            );
        }
    }
}

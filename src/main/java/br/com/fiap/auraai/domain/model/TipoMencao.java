package br.com.fiap.auraai.domain.model;

public enum TipoMencao {

    /** Produto que o cliente já utiliza. */
    ATUAL,

    /** Produto de interesse do cliente. */
    POTENCIAL;

    public static TipoMencao deTexto(String texto) {

        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException(
                    "O tipo de menção não pode ficar em branco."
            );
        }

        try {
            return valueOf(texto.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Tipo de menção inválido: " + texto + "."
            );
        }
    }
}

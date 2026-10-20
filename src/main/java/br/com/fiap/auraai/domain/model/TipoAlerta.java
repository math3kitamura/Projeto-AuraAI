package br.com.fiap.auraai.domain.model;

public enum TipoAlerta {

    CHURN_RISK,
    UPSELL,
    CONCORRENTE;

    public static TipoAlerta deTexto(String texto) {

        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException(
                    "O tipo do alerta não pode ficar em branco."
            );
        }

        try {
            return valueOf(texto.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Tipo de alerta inválido: " + texto + "."
            );
        }
    }
}

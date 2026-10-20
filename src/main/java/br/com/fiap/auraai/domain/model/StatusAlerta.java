package br.com.fiap.auraai.domain.model;

public enum StatusAlerta {

    ABERTO,
    RESOLVIDO;

    public static StatusAlerta deTexto(String texto) {

        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException(
                    "O status do alerta não pode ficar em branco."
            );
        }

        try {
            return valueOf(texto.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Status de alerta inválido: " + texto + "."
            );
        }
    }
}

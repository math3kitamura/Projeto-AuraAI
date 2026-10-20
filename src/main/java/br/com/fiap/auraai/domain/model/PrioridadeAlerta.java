package br.com.fiap.auraai.domain.model;

public enum PrioridadeAlerta {

    ALTA,
    MEDIA,
    BAIXA;

    public static PrioridadeAlerta deTexto(String texto) {

        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException(
                    "A prioridade do alerta não pode ficar em branco."
            );
        }

        try {
            return valueOf(texto.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Prioridade de alerta inválida: " + texto + "."
            );
        }
    }
}

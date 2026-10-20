package br.com.fiap.auraai.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record ReuniaoRequest(

        @NotBlank(message = "O título da reunião é obrigatório.")
        @Size(max = 150, message = "O título deve ter no máximo 150 caracteres.")
        String titulo,

        @NotNull(message = "A data e hora da reunião são obrigatórias.")
        LocalDateTime dataHora,

        @Min(value = 1, message = "A duração deve ser maior que zero.")
        int duracaoMin,

        @NotBlank(message = "O responsável é obrigatório.")
        @Size(max = 150, message = "O responsável deve ter no máximo 150 caracteres.")
        String responsavel,

        @NotNull(message = "O cliente é obrigatório.")
        Long clienteId,

        /** Opcional. Na atualização, se omitido, mantém o status atual. */
        @Pattern(
                regexp = "PENDENTE|PROCESSADA",
                message = "Status inválido. Use PENDENTE ou PROCESSADA."
        )
        String status
) {
}

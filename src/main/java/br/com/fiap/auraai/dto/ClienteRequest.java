package br.com.fiap.auraai.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ClienteRequest(

        @NotBlank(message = "O nome do cliente é obrigatório.")
        @Size(max = 150, message = "O nome deve ter no máximo 150 caracteres.")
        String nome,

        @NotBlank(message = "O CNPJ é obrigatório.")
        @Pattern(
                regexp = "\\d{2}\\.?\\d{3}\\.?\\d{3}/?\\d{4}-?\\d{2}",
                message = "CNPJ inválido. Use o formato 00.000.000/0000-00."
        )
        String cnpj,

        @NotBlank(message = "O segmento é obrigatório.")
        @Size(max = 100, message = "O segmento deve ter no máximo 100 caracteres.")
        String segmento,

        @NotBlank(message = "O e-mail é obrigatório.")
        @Email(message = "E-mail inválido.")
        @Size(max = 150, message = "O e-mail deve ter no máximo 150 caracteres.")
        String email
) {
}

package dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProdutoRequest(

        @NotBlank(message = "O nome do produto é obrigatório.")
        @Size(max = 150, message = "O nome deve ter no máximo 150 caracteres.")
        String nome,

        @NotBlank(message = "A categoria é obrigatória.")
        @Size(max = 100, message = "A categoria deve ter no máximo 100 caracteres.")
        String categoria,

        @NotBlank(message = "A versão é obrigatória.")
        @Size(max = 50, message = "A versão deve ter no máximo 50 caracteres.")
        String versao,

        @NotBlank(message = "A descrição é obrigatória.")
        String descricao
) {
}

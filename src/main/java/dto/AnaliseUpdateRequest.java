package dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/** Ajuste manual dos dados comerciais de uma análise já gerada. */
public record AnaliseUpdateRequest(

        @NotBlank(message = "A persona é obrigatória.")
        @Size(max = 100, message = "A persona deve ter no máximo 100 caracteres.")
        String persona,

        @NotNull(message = "O budget é obrigatório.")
        @DecimalMin(value = "0.0", message = "O budget deve ser maior ou igual a zero.")
        BigDecimal budget,

        @Size(max = 100, message = "O concorrente deve ter no máximo 100 caracteres.")
        String concorrente
) {
}

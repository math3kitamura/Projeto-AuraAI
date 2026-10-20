package dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TranscricaoRequest(

        @NotBlank(message = "O texto da transcrição é obrigatório.")
        String textoBruto,

        @NotBlank(message = "O idioma é obrigatório.")
        @Size(max = 20, message = "O idioma deve ter no máximo 20 caracteres.")
        String idioma
) {
}

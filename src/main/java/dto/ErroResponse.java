package dto;

import java.time.LocalDateTime;

/** Corpo padrão de todas as respostas de erro da API. */
public record ErroResponse(
        LocalDateTime timestamp,
        int status,
        String erro,
        String mensagem,
        String caminho
) {
}

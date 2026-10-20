package dto;

import domain.model.Alerta;

public record AlertaResponse(
        Long id,
        String tipo,
        String descricao,
        String prioridade,
        String status
) {

    public static AlertaResponse de(Alerta alerta) {

        return new AlertaResponse(
                alerta.getId(),
                alerta.getTipo(),
                alerta.getDescricao(),
                alerta.getPrioridade(),
                alerta.getStatus()
        );
    }
}

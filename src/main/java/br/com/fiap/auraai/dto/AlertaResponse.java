package br.com.fiap.auraai.dto;

import br.com.fiap.auraai.domain.model.Alerta;

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
                alerta.getTipo().name(),
                alerta.getDescricao(),
                alerta.getPrioridade().name(),
                alerta.getStatus().name()
        );
    }
}

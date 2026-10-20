package br.com.fiap.auraai.dto;

import br.com.fiap.auraai.domain.model.Reuniao;

import java.time.LocalDateTime;

public record ReuniaoResponse(
        Long id,
        String titulo,
        LocalDateTime dataHora,
        int duracaoMin,
        String responsavel,
        String status,
        ClienteResponse cliente
) {

    public static ReuniaoResponse de(Reuniao reuniao) {

        return new ReuniaoResponse(
                reuniao.getId(),
                reuniao.getTitulo(),
                reuniao.getDataHora(),
                reuniao.getDuracaoMin(),
                reuniao.getResponsavel(),
                reuniao.getStatus().name(),
                ClienteResponse.de(reuniao.getCliente())
        );
    }
}

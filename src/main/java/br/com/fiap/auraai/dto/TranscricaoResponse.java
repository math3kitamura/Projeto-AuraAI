package br.com.fiap.auraai.dto;

import br.com.fiap.auraai.domain.model.Transcricao;

public record TranscricaoResponse(
        Long id,
        String textoBruto,
        int qtdPalavras,
        String idioma,
        Long reuniaoId
) {

    public static TranscricaoResponse de(Transcricao transcricao) {

        return new TranscricaoResponse(
                transcricao.getId(),
                transcricao.getTextoBruto(),
                transcricao.getQtdPalavras(),
                transcricao.getIdioma(),
                transcricao.getReuniao().getId()
        );
    }
}

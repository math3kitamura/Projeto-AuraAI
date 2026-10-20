package br.com.fiap.auraai.dto;

import br.com.fiap.auraai.domain.model.AnaliseIA;

import java.math.BigDecimal;
import java.util.List;

public record AnaliseResponse(
        Long id,
        String sentimento,
        double riscoChurn,
        double oportunidade,
        String persona,
        BigDecimal budget,
        String concorrente,
        ReuniaoResponse reuniao,
        List<AlertaResponse> alertas,
        List<ProdutoIdentificadoResponse> produtosIdentificados
) {

    public static AnaliseResponse de(AnaliseIA analise) {

        return new AnaliseResponse(
                analise.getId(),
                analise.getSentimento() == null
                        ? null
                        : analise.getSentimento().name(),
                analise.getRiscoChurn(),
                analise.getOportunidade(),
                analise.getPersona(),
                analise.getBudget(),
                analise.getConcorrente(),
                ReuniaoResponse.de(analise.getReuniao()),
                analise.getAlertas()
                        .stream()
                        .map(AlertaResponse::de)
                        .toList(),
                analise.getProdutosIdentificados()
                        .stream()
                        .map(ProdutoIdentificadoResponse::de)
                        .toList()
        );
    }
}

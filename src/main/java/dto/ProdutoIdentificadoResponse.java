package dto;

import domain.model.AnaliseProduto;

public record ProdutoIdentificadoResponse(
        ProdutoResponse produto,
        String contexto,
        double relevancia,
        String tipoMencao
) {

    public static ProdutoIdentificadoResponse de(AnaliseProduto item) {

        return new ProdutoIdentificadoResponse(
                ProdutoResponse.de(item.getProduto()),
                item.getContexto(),
                item.getRelevancia(),
                item.getTipoMencao()
        );
    }
}

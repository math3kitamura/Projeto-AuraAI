package br.com.fiap.auraai.dto;

import br.com.fiap.auraai.domain.model.ProdutoTOTVS;

public record ProdutoResponse(
        Long id,
        String nome,
        String categoria,
        String versao,
        String descricao
) {

    public static ProdutoResponse de(ProdutoTOTVS produto) {

        return new ProdutoResponse(
                produto.getId(),
                produto.getNome(),
                produto.getCategoria(),
                produto.getVersao(),
                produto.getDescricao()
        );
    }
}

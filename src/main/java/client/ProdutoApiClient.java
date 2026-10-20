package client;

import domain.model.ProdutoTOTVS;
import dto.ProdutoRequest;
import dto.ProdutoResponse;

import java.util.List;

public class ProdutoApiClient extends ApiClientBase {

    public ProdutoTOTVS cadastrar(
            String nome,
            String categoria,
            String versao,
            String descricao
    ) {

        ProdutoResponse resposta = post(
                "/produtos",
                new ProdutoRequest(nome, categoria, versao, descricao),
                ProdutoResponse.class
        );

        return Conversor.paraProduto(resposta);
    }

    /** Retorna null se o produto não existir. */
    public ProdutoTOTVS buscarPorId(Long id) {

        ProdutoResponse resposta = ouNulo(
                () -> get("/produtos/" + id, ProdutoResponse.class)
        );

        return resposta == null ? null : Conversor.paraProduto(resposta);
    }

    public List<ProdutoTOTVS> listarTodos() {

        return getLista("/produtos", ProdutoResponse.class)
                .stream()
                .map(Conversor::paraProduto)
                .toList();
    }

    public void atualizar(
            Long id,
            String nome,
            String categoria,
            String versao,
            String descricao
    ) {

        put(
                "/produtos/" + id,
                new ProdutoRequest(nome, categoria, versao, descricao),
                ProdutoResponse.class
        );
    }

    public void excluir(Long id) {
        delete("/produtos/" + id);
    }
}

package service;

import dao.ProdutoDAO;
import domain.model.ProdutoTOTVS;
import exception.RecursoNaoEncontradoException;
import exception.TradutorSqlException;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class ProdutoService {

    private final ProdutoDAO produtoDAO;

    public ProdutoService(ProdutoDAO produtoDAO) {
        this.produtoDAO = produtoDAO;
    }

    public ProdutoTOTVS cadastrar(
            String nome,
            String categoria,
            String versao,
            String descricao
    ) {

        ProdutoTOTVS produto =
                new ProdutoTOTVS(nome, categoria, versao, descricao);

        try {
            produtoDAO.inserir(produto);
        } catch (SQLException e) {
            throw TradutorSqlException.traduzir(e, null);
        }

        return produto;
    }

    public ProdutoTOTVS buscarPorId(Long id) {

        try {
            ProdutoTOTVS produto = produtoDAO.buscarPorId(id);

            if (produto == null) {
                throw new RecursoNaoEncontradoException(
                        "Produto não encontrado."
                );
            }

            return produto;

        } catch (SQLException e) {
            throw TradutorSqlException.traduzir(e, null);
        }
    }

    public List<ProdutoTOTVS> listarTodos() {

        try {
            return produtoDAO.listarTodos();
        } catch (SQLException e) {
            throw TradutorSqlException.traduzir(e, null);
        }
    }

    public ProdutoTOTVS atualizar(
            Long id,
            String nome,
            String categoria,
            String versao,
            String descricao
    ) {

        buscarPorId(id);

        ProdutoTOTVS produto =
                new ProdutoTOTVS(id, nome, categoria, versao, descricao);

        try {
            produtoDAO.atualizar(produto);
        } catch (SQLException e) {
            throw TradutorSqlException.traduzir(e, null);
        }

        return produto;
    }

    public void excluir(Long id) {

        buscarPorId(id);

        try {
            produtoDAO.excluir(id);
        } catch (SQLException e) {
            throw TradutorSqlException.traduzir(
                    e,
                    "Não é possível excluir: o produto foi identificado em análises."
            );
        }
    }
}

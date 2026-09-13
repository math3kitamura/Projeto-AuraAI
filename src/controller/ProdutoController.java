package controller;

import dao.ProdutoDAO;
import dao.ProdutoDAOImpl;
import domain.model.ProdutoTOTVS;

import java.sql.SQLException;
import java.util.List;

public class ProdutoController {

    private final ProdutoDAO produtoDAO;

    public ProdutoController() {
        this.produtoDAO = new ProdutoDAOImpl();
    }

    public ProdutoTOTVS cadastrar(
            String nome,
            String categoria,
            String versao,
            String descricao
    ) throws SQLException {

        ProdutoTOTVS produto =
                new ProdutoTOTVS(
                        nome,
                        categoria,
                        versao,
                        descricao
                );

        produtoDAO.inserir(produto);

        return produto;
    }

    public ProdutoTOTVS buscarPorId(Long id)
            throws SQLException {

        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    "Informe um ID válido."
            );
        }

        return produtoDAO.buscarPorId(id);
    }

    public List<ProdutoTOTVS> listarTodos()
            throws SQLException {

        return produtoDAO.listarTodos();
    }

    public void atualizar(
            Long id,
            String nome,
            String categoria,
            String versao,
            String descricao
    ) throws SQLException {

        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    "Informe um ID válido."
            );
        }

        ProdutoTOTVS produto =
                new ProdutoTOTVS(
                        id,
                        nome,
                        categoria,
                        versao,
                        descricao
                );

        produtoDAO.atualizar(produto);
    }

    public void excluir(Long id)
            throws SQLException {

        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    "Informe um ID válido."
            );
        }

        produtoDAO.excluir(id);
    }
}

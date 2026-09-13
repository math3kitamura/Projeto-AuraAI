package dao;

import domain.model.ProdutoTOTVS;

import java.sql.SQLException;
import java.util.List;

public interface ProdutoDAO {

    void inserir(ProdutoTOTVS produto) throws SQLException;

    ProdutoTOTVS buscarPorId(Long id) throws SQLException;

    List<ProdutoTOTVS> listarTodos() throws SQLException;

    void atualizar(ProdutoTOTVS produto) throws SQLException;

    void excluir(Long id) throws SQLException;
}
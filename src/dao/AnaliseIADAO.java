package dao;

import domain.service.AnaliseIA;

import java.sql.SQLException;
import java.util.List;

public interface AnaliseIADAO {

    void inserir(AnaliseIA analise)
            throws SQLException;

    AnaliseIA buscarPorId(Long id)
            throws SQLException;

    List<AnaliseIA> listarTodos()
            throws SQLException;

    void atualizar(AnaliseIA analise)
            throws SQLException;

    void excluir(Long id)
            throws SQLException;
}
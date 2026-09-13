package dao;

import domain.model.Cliente;

import java.sql.SQLException;
import java.util.List;

public interface ClienteDAO {

    void inserir(Cliente cliente) throws SQLException;

    Cliente buscarPorId(Long id) throws SQLException;

    List<Cliente> listarTodos() throws SQLException;

    void atualizar(Cliente cliente) throws SQLException;

    void excluir(Long id) throws SQLException;
}
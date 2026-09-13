package controller;

import dao.ClienteDAO;
import dao.ClienteDAOImpl;
import domain.model.Cliente;

import java.sql.SQLException;
import java.util.List;

public class ClienteController {

    private final ClienteDAO clienteDAO;

    public ClienteController() {
        this.clienteDAO = new ClienteDAOImpl();
    }

    public Cliente cadastrar(
            String nome,
            String cnpj,
            String segmento,
            String email
    ) throws SQLException {

        Cliente cliente = new Cliente(
                nome,
                cnpj,
                segmento,
                email
        );

        clienteDAO.inserir(cliente);

        return cliente;
    }

    public Cliente buscarPorId(Long id)
            throws SQLException {

        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    "Informe um ID válido."
            );
        }

        return clienteDAO.buscarPorId(id);
    }

    public List<Cliente> listarTodos()
            throws SQLException {

        return clienteDAO.listarTodos();
    }

    public void atualizar(
            Long id,
            String nome,
            String cnpj,
            String segmento,
            String email
    ) throws SQLException {

        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    "Informe um ID válido."
            );
        }

        Cliente cliente = new Cliente(
                id,
                nome,
                cnpj,
                segmento,
                email
        );

        clienteDAO.atualizar(cliente);
    }

    public void excluir(Long id)
            throws SQLException {

        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    "Informe um ID válido."
            );
        }

        clienteDAO.excluir(id);
    }
}
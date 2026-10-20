package service;

import dao.ClienteDAO;
import domain.model.Cliente;
import exception.RecursoNaoEncontradoException;
import exception.TradutorSqlException;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class ClienteService {

    private static final String MSG_CNPJ_DUPLICADO =
            "Já existe um cliente cadastrado com este CNPJ.";

    private final ClienteDAO clienteDAO;

    public ClienteService(ClienteDAO clienteDAO) {
        this.clienteDAO = clienteDAO;
    }

    public Cliente cadastrar(
            String nome,
            String cnpj,
            String segmento,
            String email
    ) {

        Cliente cliente = new Cliente(nome, cnpj, segmento, email);

        try {
            clienteDAO.inserir(cliente);
        } catch (SQLException e) {
            throw TradutorSqlException.traduzir(e, MSG_CNPJ_DUPLICADO);
        }

        return cliente;
    }

    public Cliente buscarPorId(Long id) {

        try {
            Cliente cliente = clienteDAO.buscarPorId(id);

            if (cliente == null) {
                throw new RecursoNaoEncontradoException(
                        "Cliente não encontrado."
                );
            }

            return cliente;

        } catch (SQLException e) {
            throw TradutorSqlException.traduzir(e, null);
        }
    }

    public List<Cliente> listarTodos() {

        try {
            return clienteDAO.listarTodos();
        } catch (SQLException e) {
            throw TradutorSqlException.traduzir(e, null);
        }
    }

    public Cliente atualizar(
            Long id,
            String nome,
            String cnpj,
            String segmento,
            String email
    ) {

        buscarPorId(id);

        Cliente cliente = new Cliente(id, nome, cnpj, segmento, email);

        try {
            clienteDAO.atualizar(cliente);
        } catch (SQLException e) {
            throw TradutorSqlException.traduzir(e, MSG_CNPJ_DUPLICADO);
        }

        return cliente;
    }

    public void excluir(Long id) {

        buscarPorId(id);

        try {
            clienteDAO.excluir(id);
        } catch (SQLException e) {
            throw TradutorSqlException.traduzir(
                    e,
                    "Não é possível excluir: o cliente possui reuniões vinculadas."
            );
        }
    }
}

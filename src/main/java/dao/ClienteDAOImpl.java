package dao;

import org.springframework.stereotype.Repository;
import domain.model.Cliente;
import factory.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ClienteDAOImpl implements ClienteDAO {

    @Override
    public void inserir(Cliente cliente) throws SQLException {

        String sql = """
                INSERT INTO cliente
                (nome, cnpj, segmento, email)
                VALUES (?, ?, ?, ?)
                """;

        try (
                Connection conexao =
                        ConnectionFactory
                                .getInstance()
                                .getConnection();

                PreparedStatement stmt =
                        conexao.prepareStatement(
                                sql,
                                Statement.RETURN_GENERATED_KEYS
                        )
        ) {

            stmt.setString(
                    1,
                    cliente.getNome()
            );

            stmt.setString(
                    2,
                    cliente.getCnpj()
            );

            stmt.setString(
                    3,
                    cliente.getSegmento()
            );

            stmt.setString(
                    4,
                    cliente.getEmail()
            );

            stmt.executeUpdate();

            try (
                    ResultSet rs =
                            stmt.getGeneratedKeys()
            ) {

                if (rs.next()) {

                    cliente.setId(
                            rs.getLong(1)
                    );
                }
            }
        }
    }

    @Override
    public Cliente buscarPorId(Long id)
            throws SQLException {

        String sql = """
                SELECT
                    id,
                    nome,
                    cnpj,
                    segmento,
                    email
                FROM cliente
                WHERE id = ?
                """;

        try (
                Connection conexao =
                        ConnectionFactory
                                .getInstance()
                                .getConnection();

                PreparedStatement stmt =
                        conexao.prepareStatement(sql)
        ) {

            stmt.setLong(1, id);

            try (
                    ResultSet rs =
                            stmt.executeQuery()
            ) {

                if (rs.next()) {

                    return criarCliente(rs);
                }
            }
        }

        return null;
    }

    @Override
    public List<Cliente> listarTodos()
            throws SQLException {

        String sql = """
                SELECT
                    id,
                    nome,
                    cnpj,
                    segmento,
                    email
                FROM cliente
                ORDER BY id
                """;

        List<Cliente> clientes =
                new ArrayList<>();

        try (
                Connection conexao =
                        ConnectionFactory
                                .getInstance()
                                .getConnection();

                PreparedStatement stmt =
                        conexao.prepareStatement(sql);

                ResultSet rs =
                        stmt.executeQuery()
        ) {

            while (rs.next()) {

                Cliente cliente =
                        criarCliente(rs);

                clientes.add(cliente);
            }
        }

        return clientes;
    }

    @Override
    public void atualizar(Cliente cliente)
            throws SQLException {

        if (cliente.getId() == null) {

            throw new IllegalArgumentException(
                    "O cliente precisa possuir ID para ser atualizado."
            );
        }

        String sql = """
                UPDATE cliente
                SET
                    nome = ?,
                    cnpj = ?,
                    segmento = ?,
                    email = ?
                WHERE id = ?
                """;

        try (
                Connection conexao =
                        ConnectionFactory
                                .getInstance()
                                .getConnection();

                PreparedStatement stmt =
                        conexao.prepareStatement(sql)
        ) {

            stmt.setString(
                    1,
                    cliente.getNome()
            );

            stmt.setString(
                    2,
                    cliente.getCnpj()
            );

            stmt.setString(
                    3,
                    cliente.getSegmento()
            );

            stmt.setString(
                    4,
                    cliente.getEmail()
            );

            stmt.setLong(
                    5,
                    cliente.getId()
            );

            stmt.executeUpdate();
        }
    }

    @Override
    public void excluir(Long id)
            throws SQLException {

        String sql = """
                DELETE FROM cliente
                WHERE id = ?
                """;

        try (
                Connection conexao =
                        ConnectionFactory
                                .getInstance()
                                .getConnection();

                PreparedStatement stmt =
                        conexao.prepareStatement(sql)
        ) {

            stmt.setLong(1, id);

            stmt.executeUpdate();
        }
    }

    private Cliente criarCliente(ResultSet rs)
            throws SQLException {

        return new Cliente(
                rs.getLong("id"),
                rs.getString("nome"),
                rs.getString("cnpj"),
                rs.getString("segmento"),
                rs.getString("email")
        );
    }
}

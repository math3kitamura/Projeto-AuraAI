package dao;

import org.springframework.stereotype.Repository;
import domain.model.ProdutoTOTVS;
import factory.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ProdutoDAOImpl implements ProdutoDAO {

    @Override
    public void inserir(ProdutoTOTVS produto)
            throws SQLException {

        String sql = """
                INSERT INTO produto_totvs
                (nome, categoria, versao, descricao)
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
                    produto.getNome()
            );

            stmt.setString(
                    2,
                    produto.getCategoria()
            );

            stmt.setString(
                    3,
                    produto.getVersao()
            );

            stmt.setString(
                    4,
                    produto.getDescricao()
            );

            stmt.executeUpdate();

            try (
                    ResultSet rs =
                            stmt.getGeneratedKeys()
            ) {

                if (rs.next()) {

                    produto.setId(
                            rs.getLong(1)
                    );
                }
            }
        }
    }

    @Override
    public ProdutoTOTVS buscarPorId(Long id)
            throws SQLException {

        String sql = """
                SELECT
                    id,
                    nome,
                    categoria,
                    versao,
                    descricao
                FROM produto_totvs
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
                    return criarProduto(rs);
                }
            }
        }

        return null;
    }

    @Override
    public List<ProdutoTOTVS> listarTodos()
            throws SQLException {

        String sql = """
                SELECT
                    id,
                    nome,
                    categoria,
                    versao,
                    descricao
                FROM produto_totvs
                ORDER BY id
                """;

        List<ProdutoTOTVS> produtos =
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

                ProdutoTOTVS produto =
                        criarProduto(rs);

                produtos.add(produto);
            }
        }

        return produtos;
    }

    @Override
    public void atualizar(ProdutoTOTVS produto)
            throws SQLException {

        if (produto.getId() == null) {

            throw new IllegalArgumentException(
                    "O produto precisa possuir ID para ser atualizado."
            );
        }

        String sql = """
                UPDATE produto_totvs
                SET
                    nome = ?,
                    categoria = ?,
                    versao = ?,
                    descricao = ?
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
                    produto.getNome()
            );

            stmt.setString(
                    2,
                    produto.getCategoria()
            );

            stmt.setString(
                    3,
                    produto.getVersao()
            );

            stmt.setString(
                    4,
                    produto.getDescricao()
            );

            stmt.setLong(
                    5,
                    produto.getId()
            );

            stmt.executeUpdate();
        }
    }

    @Override
    public void excluir(Long id)
            throws SQLException {

        String sql = """
                DELETE FROM produto_totvs
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

    private ProdutoTOTVS criarProduto(ResultSet rs)
            throws SQLException {

        return new ProdutoTOTVS(
                rs.getLong("id"),
                rs.getString("nome"),
                rs.getString("categoria"),
                rs.getString("versao"),
                rs.getString("descricao")
        );
    }
}
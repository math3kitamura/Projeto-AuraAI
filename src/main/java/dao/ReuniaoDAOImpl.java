package dao;

import org.springframework.stereotype.Repository;
import domain.model.Cliente;
import domain.model.Reuniao;
import domain.model.Transcricao;
import factory.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ReuniaoDAOImpl implements ReuniaoDAO {

    @Override
    public void inserir(Reuniao reuniao)
            throws SQLException {

        if (reuniao.getCliente() == null) {
            throw new IllegalArgumentException(
                    "A reunião precisa possuir um cliente."
            );
        }

        if (reuniao.getCliente().getId() == null) {
            throw new IllegalArgumentException(
                    "O cliente precisa estar cadastrado no banco antes da reunião."
            );
        }

        String sql = """
                INSERT INTO reuniao
                (
                    titulo,
                    data_hora,
                    duracao_min,
                    responsavel,
                    status,
                    cliente_id
                )
                VALUES (?, ?, ?, ?, ?, ?)
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
                    reuniao.getTitulo()
            );

            stmt.setTimestamp(
                    2,
                    Timestamp.valueOf(
                            reuniao.getDataHora()
                    )
            );

            stmt.setInt(
                    3,
                    reuniao.getDuracaoMin()
            );

            stmt.setString(
                    4,
                    reuniao.getResponsavel()
            );

            stmt.setString(
                    5,
                    reuniao.getStatus()
            );

            stmt.setLong(
                    6,
                    reuniao.getCliente().getId()
            );

            stmt.executeUpdate();

            try (
                    ResultSet rs =
                            stmt.getGeneratedKeys()
            ) {

                if (rs.next()) {

                    reuniao.setId(
                            rs.getLong(1)
                    );
                }
            }
        }
    }

    @Override
    public Reuniao buscarPorId(Long id)
            throws SQLException {

        try (
                Connection conexao =
                        ConnectionFactory
                                .getInstance()
                                .getConnection()
        ) {

            return buscarPorId(conexao, id);
        }
    }

    /**
     * Versão que reaproveita uma conexão já aberta, evitando abrir
     * várias conexões quando outro DAO precisa carregar a reunião.
     */
    public Reuniao buscarPorId(Connection conexao, Long id)
            throws SQLException {

        String sql = """
                SELECT
                    r.id AS reuniao_id,
                    r.titulo,
                    r.data_hora,
                    r.duracao_min,
                    r.responsavel,
                    r.status,

                    c.id AS cliente_id,
                    c.nome,
                    c.cnpj,
                    c.segmento,
                    c.email

                FROM reuniao r

                INNER JOIN cliente c
                    ON c.id = r.cliente_id

                WHERE r.id = ?
                """;

        try (
                PreparedStatement stmt =
                        conexao.prepareStatement(sql)
        ) {

            stmt.setLong(1, id);

            try (
                    ResultSet rs =
                            stmt.executeQuery()
            ) {

                if (rs.next()) {
                    return criarReuniao(rs);
                }
            }
        }

        return null;
    }

    @Override
    public List<Reuniao> listarTodos()
            throws SQLException {

        String sql = """
                SELECT
                    r.id AS reuniao_id,
                    r.titulo,
                    r.data_hora,
                    r.duracao_min,
                    r.responsavel,
                    r.status,

                    c.id AS cliente_id,
                    c.nome,
                    c.cnpj,
                    c.segmento,
                    c.email

                FROM reuniao r

                INNER JOIN cliente c
                    ON c.id = r.cliente_id

                ORDER BY r.id
                """;

        List<Reuniao> reunioes =
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

                Reuniao reuniao =
                        criarReuniao(rs);

                reunioes.add(reuniao);
            }
        }

        return reunioes;
    }

    @Override
    public void atualizar(Reuniao reuniao)
            throws SQLException {

        if (reuniao.getId() == null) {
            throw new IllegalArgumentException(
                    "A reunião precisa possuir ID para ser atualizada."
            );
        }

        if (
                reuniao.getCliente() == null
                        || reuniao.getCliente().getId() == null
        ) {
            throw new IllegalArgumentException(
                    "A reunião precisa possuir um cliente cadastrado."
            );
        }

        String sql = """
                UPDATE reuniao
                SET
                    titulo = ?,
                    data_hora = ?,
                    duracao_min = ?,
                    responsavel = ?,
                    status = ?,
                    cliente_id = ?
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
                    reuniao.getTitulo()
            );

            stmt.setTimestamp(
                    2,
                    Timestamp.valueOf(
                            reuniao.getDataHora()
                    )
            );

            stmt.setInt(
                    3,
                    reuniao.getDuracaoMin()
            );

            stmt.setString(
                    4,
                    reuniao.getResponsavel()
            );

            stmt.setString(
                    5,
                    reuniao.getStatus()
            );

            stmt.setLong(
                    6,
                    reuniao.getCliente().getId()
            );

            stmt.setLong(
                    7,
                    reuniao.getId()
            );

            stmt.executeUpdate();
        }
    }

    @Override
    public void excluir(Long id)
            throws SQLException {

        String sqlTranscricao = """
                DELETE FROM transcricao
                WHERE reuniao_id = ?
                """;

        String sqlReuniao = """
                DELETE FROM reuniao
                WHERE id = ?
                """;

        try (
                Connection conexao =
                        ConnectionFactory
                                .getInstance()
                                .getConnection()
        ) {

            conexao.setAutoCommit(false);

            try (
                    PreparedStatement stmtTranscricao =
                            conexao.prepareStatement(
                                    sqlTranscricao
                            );

                    PreparedStatement stmtReuniao =
                            conexao.prepareStatement(
                                    sqlReuniao
                            )
            ) {

                stmtTranscricao.setLong(1, id);
                stmtTranscricao.executeUpdate();

                stmtReuniao.setLong(1, id);
                stmtReuniao.executeUpdate();

                conexao.commit();

            } catch (SQLException e) {

                conexao.rollback();
                throw e;

            } finally {

                conexao.setAutoCommit(true);
            }
        }
    }

    @Override
    public void inserirTranscricao(
            Transcricao transcricao
    ) throws SQLException {

        if (transcricao.getReuniao() == null) {

            throw new IllegalArgumentException(
                    "A transcrição precisa possuir uma reunião."
            );
        }

        if (
                transcricao
                        .getReuniao()
                        .getId() == null
        ) {

            throw new IllegalArgumentException(
                    "A reunião precisa estar cadastrada antes da transcrição."
            );
        }

        String sql = """
                INSERT INTO transcricao
                (
                    texto_bruto,
                    qtd_palavras,
                    idioma,
                    reuniao_id
                )
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
                    transcricao.getTextoBruto()
            );

            stmt.setInt(
                    2,
                    transcricao.getQtdPalavras()
            );

            stmt.setString(
                    3,
                    transcricao.getIdioma()
            );

            stmt.setLong(
                    4,
                    transcricao
                            .getReuniao()
                            .getId()
            );

            stmt.executeUpdate();

            try (
                    ResultSet rs =
                            stmt.getGeneratedKeys()
            ) {

                if (rs.next()) {

                    transcricao.setId(
                            rs.getLong(1)
                    );
                }
            }
        }
    }

    @Override
    public Transcricao buscarTranscricaoPorReuniao(
            Long reuniaoId
    ) throws SQLException {

        String sql = """
                SELECT
                    id,
                    texto_bruto,
                    idioma
                FROM transcricao
                WHERE reuniao_id = ?
                """;

        try (
                Connection conexao =
                        ConnectionFactory
                                .getInstance()
                                .getConnection()
        ) {

            Reuniao reuniao =
                    buscarPorId(conexao, reuniaoId);

            if (reuniao == null) {
                return null;
            }

            return buscarTranscricao(
                    conexao,
                    reuniao,
                    sql
            );
        }
    }

    private Transcricao buscarTranscricao(
            Connection conexao,
            Reuniao reuniao,
            String sql
    ) throws SQLException {

        Long reuniaoId = reuniao.getId();

        try (
                PreparedStatement stmt =
                        conexao.prepareStatement(sql)
        ) {

            stmt.setLong(
                    1,
                    reuniaoId
            );

            try (
                    ResultSet rs =
                            stmt.executeQuery()
            ) {

                if (rs.next()) {

                    return new Transcricao(
                            rs.getLong("id"),
                            rs.getString(
                                    "texto_bruto"
                            ),
                            rs.getString(
                                    "idioma"
                            ),
                            reuniao
                    );
                }
            }
        }

        return null;
    }

    private Reuniao criarReuniao(
            ResultSet rs
    ) throws SQLException {

        Cliente cliente =
                new Cliente(
                        rs.getLong(
                                "cliente_id"
                        ),
                        rs.getString(
                                "nome"
                        ),
                        rs.getString(
                                "cnpj"
                        ),
                        rs.getString(
                                "segmento"
                        ),
                        rs.getString(
                                "email"
                        )
                );

        return new Reuniao(
                rs.getLong(
                        "reuniao_id"
                ),
                rs.getString(
                        "titulo"
                ),
                rs.getTimestamp(
                        "data_hora"
                ).toLocalDateTime(),
                rs.getInt(
                        "duracao_min"
                ),
                rs.getString(
                        "responsavel"
                ),
                rs.getString(
                        "status"
                ),
                cliente
        );
    }
}

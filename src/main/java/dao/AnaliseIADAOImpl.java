package dao;

import org.springframework.stereotype.Repository;
import domain.model.Alerta;
import domain.model.AnaliseProduto;
import domain.model.ProdutoTOTVS;
import domain.model.Reuniao;
import domain.model.AnaliseIA;
import factory.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.List;

@Repository
public class AnaliseIADAOImpl
        implements AnaliseIADAO {

    @Override
    public void inserir(AnaliseIA analise)
            throws SQLException {

        validarAnaliseParaPersistencia(analise);

        String sql = """
                INSERT INTO analise_ia
                (
                    sentimento,
                    risco_churn,
                    oportunidade,
                    persona,
                    budget,
                    concorrente,
                    reuniao_id
                )
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (
                Connection conexao =
                        ConnectionFactory
                                .getInstance()
                                .getConnection()
        ) {

            conexao.setAutoCommit(false);

            try (
                    PreparedStatement stmt =
                            conexao.prepareStatement(
                                    sql,
                                    Statement.RETURN_GENERATED_KEYS
                            )
            ) {

                stmt.setString(
                        1,
                        analise.getSentimento()
                );

                stmt.setDouble(
                        2,
                        analise.getRiscoChurn()
                );

                stmt.setDouble(
                        3,
                        analise.getOportunidade()
                );

                stmt.setString(
                        4,
                        analise.getPersona()
                );

                stmt.setBigDecimal(
                        5,
                        analise.getBudget()
                );

                stmt.setString(
                        6,
                        analise.getConcorrente()
                );

                stmt.setLong(
                        7,
                        analise
                                .getReuniao()
                                .getId()
                );

                stmt.executeUpdate();

                try (
                        ResultSet rs =
                                stmt.getGeneratedKeys()
                ) {

                    if (rs.next()) {

                        analise.setId(
                                rs.getLong(1)
                        );
                    }
                }

                // Salva os alertas gerados
                for (
                        Alerta alerta :
                        analise.getAlertas()
                ) {

                    inserirAlerta(
                            conexao,
                            analise.getId(),
                            alerta
                    );
                }

                // Salva os produtos identificados
                for (
                        AnaliseProduto produto :
                        analise.getProdutosIdentificados()
                ) {

                    inserirProdutoIdentificado(
                            conexao,
                            analise.getId(),
                            produto
                    );
                }

                // Na mesma transação, marca a reunião como processada
                // para que análise e status nunca fiquem divergentes.
                marcarReuniaoComoProcessada(
                        conexao,
                        analise.getReuniao().getId()
                );

                conexao.commit();

            } catch (SQLException | RuntimeException e) {

                conexao.rollback();

                throw e;

            } finally {

                conexao.setAutoCommit(true);
            }
        }
    }

    @Override
    public AnaliseIA buscarPorId(Long id)
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

    private AnaliseIA buscarPorId(
            Connection conexao,
            Long id
    ) throws SQLException {

        String sql = """
                SELECT
                    id,
                    sentimento,
                    risco_churn,
                    oportunidade,
                    persona,
                    budget,
                    concorrente,
                    reuniao_id
                FROM analise_ia
                WHERE id = ?
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

                if (!rs.next()) {
                    return null;
                }

                AnaliseIA analise =
                        criarAnalise(conexao, rs);

                carregarAlertas(
                        conexao,
                        analise
                );

                carregarProdutos(
                        conexao,
                        analise
                );

                return analise;
            }
        }
    }

    @Override
    public List<AnaliseIA> listarTodos()
            throws SQLException {

        String sql = """
                SELECT id
                FROM analise_ia
                ORDER BY id
                """;

        List<AnaliseIA> analises =
                new ArrayList<>();

        // Uma única conexão para toda a listagem
        try (
                Connection conexao =
                        ConnectionFactory
                                .getInstance()
                                .getConnection()
        ) {

            List<Long> ids =
                    new ArrayList<>();

            try (
                    PreparedStatement stmt =
                            conexao.prepareStatement(sql);

                    ResultSet rs =
                            stmt.executeQuery()
            ) {

                while (rs.next()) {

                    ids.add(
                            rs.getLong("id")
                    );
                }
            }

            for (Long id : ids) {

                AnaliseIA analise =
                        buscarPorId(conexao, id);

                if (analise != null) {
                    analises.add(analise);
                }
            }
        }

        return analises;
    }

    @Override
    public void atualizar(AnaliseIA analise)
            throws SQLException {

        if (analise.getId() == null) {

            throw new IllegalArgumentException(
                    "A análise precisa possuir ID para ser atualizada."
            );
        }

        validarAnaliseParaPersistencia(
                analise
        );

        String sql = """
                UPDATE analise_ia
                SET
                    sentimento = ?,
                    risco_churn = ?,
                    oportunidade = ?,
                    persona = ?,
                    budget = ?,
                    concorrente = ?,
                    reuniao_id = ?
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
                    PreparedStatement stmt =
                            conexao.prepareStatement(sql)
            ) {

                stmt.setString(
                        1,
                        analise.getSentimento()
                );

                stmt.setDouble(
                        2,
                        analise.getRiscoChurn()
                );

                stmt.setDouble(
                        3,
                        analise.getOportunidade()
                );

                stmt.setString(
                        4,
                        analise.getPersona()
                );

                stmt.setBigDecimal(
                        5,
                        analise.getBudget()
                );

                stmt.setString(
                        6,
                        analise.getConcorrente()
                );

                stmt.setLong(
                        7,
                        analise
                                .getReuniao()
                                .getId()
                );

                stmt.setLong(
                        8,
                        analise.getId()
                );

                stmt.executeUpdate();

                /*
                 * Como alertas e produtos são resultados
                 * da análise, recriamos os relacionamentos.
                 */

                excluirAlertas(
                        conexao,
                        analise.getId()
                );

                excluirProdutosIdentificados(
                        conexao,
                        analise.getId()
                );

                for (
                        Alerta alerta :
                        analise.getAlertas()
                ) {

                    inserirAlerta(
                            conexao,
                            analise.getId(),
                            alerta
                    );
                }

                for (
                        AnaliseProduto produto :
                        analise.getProdutosIdentificados()
                ) {

                    inserirProdutoIdentificado(
                            conexao,
                            analise.getId(),
                            produto
                    );
                }

                conexao.commit();

            } catch (SQLException | RuntimeException e) {

                conexao.rollback();

                throw e;

            } finally {

                conexao.setAutoCommit(true);
            }
        }
    }

    @Override
    public void excluir(Long id)
            throws SQLException {

        String sqlAnalise = """
                DELETE FROM analise_ia
                WHERE id = ?
                """;

        try (
                Connection conexao =
                        ConnectionFactory
                                .getInstance()
                                .getConnection()
        ) {

            conexao.setAutoCommit(false);

            try {

                excluirAlertas(
                        conexao,
                        id
                );

                excluirProdutosIdentificados(
                        conexao,
                        id
                );

                try (
                        PreparedStatement stmt =
                                conexao.prepareStatement(
                                        sqlAnalise
                                )
                ) {

                    stmt.setLong(
                            1,
                            id
                    );

                    stmt.executeUpdate();
                }

                conexao.commit();

            } catch (SQLException e) {

                conexao.rollback();
                throw e;

            } finally {

                conexao.setAutoCommit(true);
            }
        }
    }

    /*
     * =====================================
     * MÉTODOS AUXILIARES
     * =====================================
     */

    private void inserirAlerta(
            Connection conexao,
            Long analiseId,
            Alerta alerta
    ) throws SQLException {

        String sql = """
                INSERT INTO alerta
                (
                    tipo,
                    descricao,
                    prioridade,
                    status,
                    analise_id
                )
                VALUES (?, ?, ?, ?, ?)
                """;

        try (
                PreparedStatement stmt =
                        conexao.prepareStatement(
                                sql,
                                Statement.RETURN_GENERATED_KEYS
                        )
        ) {

            stmt.setString(
                    1,
                    alerta.getTipo()
            );

            stmt.setString(
                    2,
                    alerta.getDescricao()
            );

            stmt.setString(
                    3,
                    alerta.getPrioridade()
            );

            stmt.setString(
                    4,
                    alerta.getStatus()
            );

            stmt.setLong(
                    5,
                    analiseId
            );

            stmt.executeUpdate();

            try (
                    ResultSet rs =
                            stmt.getGeneratedKeys()
            ) {

                if (rs.next()) {

                    alerta.setId(
                            rs.getLong(1)
                    );
                }
            }
        }
    }

    private void inserirProdutoIdentificado(
            Connection conexao,
            Long analiseId,
            AnaliseProduto analiseProduto
    ) throws SQLException {

        ProdutoTOTVS produto =
                analiseProduto.getProduto();

        if (produto.getId() == null) {

            throw new IllegalArgumentException(
                    "O produto identificado precisa estar cadastrado no banco."
            );
        }

        String sql = """
                INSERT INTO analise_produto
                (
                    analise_id,
                    produto_id,
                    contexto,
                    relevancia,
                    tipo_mencao
                )
                VALUES (?, ?, ?, ?, ?)
                """;

        try (
                PreparedStatement stmt =
                        conexao.prepareStatement(sql)
        ) {

            stmt.setLong(
                    1,
                    analiseId
            );

            stmt.setLong(
                    2,
                    produto.getId()
            );

            stmt.setString(
                    3,
                    analiseProduto.getContexto()
            );

            stmt.setDouble(
                    4,
                    analiseProduto.getRelevancia()
            );

            stmt.setString(
                    5,
                    analiseProduto.getTipoMencao()
            );

            stmt.executeUpdate();
        }
    }

    private void carregarAlertas(
            Connection conexao,
            AnaliseIA analise
    ) throws SQLException {

        String sql = """
                SELECT
                    id,
                    tipo,
                    descricao,
                    prioridade,
                    status
                FROM alerta
                WHERE analise_id = ?
                ORDER BY id
                """;

        try (
                PreparedStatement stmt =
                        conexao.prepareStatement(sql)
        ) {

            stmt.setLong(
                    1,
                    analise.getId()
            );

            try (
                    ResultSet rs =
                            stmt.executeQuery()
            ) {

                while (rs.next()) {

                    Alerta alerta =
                            new Alerta(
                                    rs.getLong("id"),
                                    rs.getString("tipo"),
                                    rs.getString("descricao"),
                                    rs.getString("prioridade"),
                                    rs.getString("status")
                            );

                    analise.adicionarAlerta(
                            alerta
                    );
                }
            }
        }
    }

    private void carregarProdutos(
            Connection conexao,
            AnaliseIA analise
    ) throws SQLException {

        String sql = """
                SELECT
                    p.id,
                    p.nome,
                    p.categoria,
                    p.versao,
                    p.descricao,

                    ap.contexto,
                    ap.relevancia,
                    ap.tipo_mencao

                FROM analise_produto ap

                INNER JOIN produto_totvs p
                    ON p.id = ap.produto_id

                WHERE ap.analise_id = ?
                """;

        try (
                PreparedStatement stmt =
                        conexao.prepareStatement(sql)
        ) {

            stmt.setLong(
                    1,
                    analise.getId()
            );

            try (
                    ResultSet rs =
                            stmt.executeQuery()
            ) {

                while (rs.next()) {

                    ProdutoTOTVS produto =
                            new ProdutoTOTVS(
                                    rs.getLong("id"),
                                    rs.getString("nome"),
                                    rs.getString("categoria"),
                                    rs.getString("versao"),
                                    rs.getString("descricao")
                            );

                    AnaliseProduto analiseProduto =
                            new AnaliseProduto(
                                    produto,
                                    rs.getString("contexto"),
                                    rs.getDouble("relevancia"),
                                    rs.getString("tipo_mencao")
                            );

                    analise.adicionarProdutoIdentificado(
                            analiseProduto
                    );
                }
            }
        }
    }

    private void excluirAlertas(
            Connection conexao,
            Long analiseId
    ) throws SQLException {

        String sql = """
                DELETE FROM alerta
                WHERE analise_id = ?
                """;

        try (
                PreparedStatement stmt =
                        conexao.prepareStatement(sql)
        ) {

            stmt.setLong(
                    1,
                    analiseId
            );

            stmt.executeUpdate();
        }
    }

    private void excluirProdutosIdentificados(
            Connection conexao,
            Long analiseId
    ) throws SQLException {

        String sql = """
                DELETE FROM analise_produto
                WHERE analise_id = ?
                """;

        try (
                PreparedStatement stmt =
                        conexao.prepareStatement(sql)
        ) {

            stmt.setLong(
                    1,
                    analiseId
            );

            stmt.executeUpdate();
        }
    }

    private void marcarReuniaoComoProcessada(
            Connection conexao,
            Long reuniaoId
    ) throws SQLException {

        String sql = """
                UPDATE reuniao
                SET status = 'PROCESSADA'
                WHERE id = ?
                """;

        try (
                PreparedStatement stmt =
                        conexao.prepareStatement(sql)
        ) {

            stmt.setLong(1, reuniaoId);

            stmt.executeUpdate();
        }
    }

    private AnaliseIA criarAnalise(
            Connection conexao,
            ResultSet rs
    ) throws SQLException {

        Long reuniaoId =
                rs.getLong(
                        "reuniao_id"
                );

        Reuniao reuniao =
                new ReuniaoDAOImpl().buscarPorId(
                        conexao,
                        reuniaoId
                );

        return new AnaliseIA(
                rs.getLong("id"),
                reuniao,
                rs.getString("sentimento"),
                rs.getDouble("risco_churn"),
                rs.getDouble("oportunidade"),
                rs.getString("persona"),
                rs.getBigDecimal("budget"),
                rs.getString("concorrente")
        );
    }

    private void validarAnaliseParaPersistencia(
            AnaliseIA analise
    ) {

        if (analise == null) {

            throw new IllegalArgumentException(
                    "A análise não pode ser nula."
            );
        }

        if (
                analise.getReuniao() == null
                        || analise.getReuniao().getId() == null
        ) {

            throw new IllegalArgumentException(
                    "A reunião precisa estar cadastrada no banco antes da análise."
            );
        }

        if (analise.getSentimento() == null) {

            throw new IllegalArgumentException(
                    "Execute analisar() antes de salvar a análise."
            );
        }
    }
}

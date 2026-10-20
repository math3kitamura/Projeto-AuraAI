package factory;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Factory + Singleton responsável pelas conexões JDBC com o PostgreSQL.
 *
 * A configuração é lida, nesta ordem, de:
 *  1. variáveis de ambiente DB_URL, DB_USER e DB_PASSWORD;
 *  2. arquivo db.properties na pasta de execução (ignorado pelo Git).
 */
public class ConnectionFactory {

    private static final String URL_PADRAO =
            "jdbc:postgresql://localhost:5432/aura_ai";

    private static final String USUARIO_PADRAO = "postgres";

    private static final String ARQUIVO_CONFIG = "db.properties";

    private static ConnectionFactory instancia;

    private final String url;
    private final String usuario;
    private final String senha;

    private ConnectionFactory() {

        Properties arquivo = carregarArquivo();

        this.url = obter("DB_URL", "db.url", arquivo, URL_PADRAO);
        this.usuario = obter("DB_USER", "db.user", arquivo, USUARIO_PADRAO);
        this.senha = obter("DB_PASSWORD", "db.password", arquivo, null);
    }

    public static synchronized ConnectionFactory getInstance() {

        if (instancia == null) {
            instancia = new ConnectionFactory();
        }

        return instancia;
    }

    public Connection getConnection() throws SQLException {

        if (senha == null) {
            throw new SQLException(
                    "Senha do banco não configurada. Defina a variável de ambiente "
                            + "DB_PASSWORD ou crie o arquivo " + ARQUIVO_CONFIG
                            + " com db.password=..."
            );
        }

        return DriverManager.getConnection(url, usuario, senha);
    }

    private static Properties carregarArquivo() {

        Properties propriedades = new Properties();

        try (FileInputStream entrada = new FileInputStream(ARQUIVO_CONFIG)) {
            propriedades.load(entrada);
        } catch (IOException e) {
            // Arquivo opcional: sem ele, valem as variáveis de ambiente.
        }

        return propriedades;
    }

    private static String obter(
            String variavel,
            String chave,
            Properties arquivo,
            String padrao
    ) {

        String valor = System.getenv(variavel);

        if (valor == null || valor.isBlank()) {
            valor = arquivo.getProperty(chave, padrao);
        }

        return valor;
    }
}

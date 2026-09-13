package factory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {

    private static ConnectionFactory instancia;

    private static final String URL =
            "jdbc:postgresql://localhost:5432/aura_ai";

    private static final String USUARIO =
            "postgres";

    private static final String SENHA =
            "SUA_SENHa";

    private ConnectionFactory() {
    }

    public static ConnectionFactory getInstance() {

        if (instancia == null) {
            instancia = new ConnectionFactory();
        }

        return instancia;
    }

    public Connection getConnection() throws SQLException {

        return DriverManager.getConnection(
                URL,
                USUARIO,
                SENHA
        );
    }
}

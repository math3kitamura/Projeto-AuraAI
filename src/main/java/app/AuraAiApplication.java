package app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * Ponto de entrada da API REST. O acesso ao banco é feito pelos DAOs
 * (JDBC + ConnectionFactory), por isso o DataSource do Spring não é usado.
 */
@SpringBootApplication(
        scanBasePackages = {
                "controller",
                "service",
                "dao",
                "exception"
        },
        exclude = DataSourceAutoConfiguration.class
)
public class AuraAiApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuraAiApplication.class, args);
    }
}

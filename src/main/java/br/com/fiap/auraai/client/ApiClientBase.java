package br.com.fiap.auraai.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import br.com.fiap.auraai.dto.ErroResponse;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

/**
 * Base dos clientes HTTP usados pelo front-end Swing para consumir a API.
 * A URL da API vem da variável de ambiente AURA_API_URL
 * (padrão: http://localhost:8080).
 */
abstract class ApiClientBase {

    private static final String URL_PADRAO = "http://localhost:8080";

    private static final HttpClient HTTP =
            HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(5))
                    .build();

    private static final ObjectMapper MAPPER =
            new ObjectMapper()
                    .registerModule(new JavaTimeModule())
                    .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                    .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    private final String baseUrl;

    protected ApiClientBase() {

        String url = System.getenv("AURA_API_URL");

        this.baseUrl =
                url == null || url.isBlank()
                        ? URL_PADRAO
                        : url.replaceAll("/+$", "");
    }

    protected <T> T get(String caminho, Class<T> tipo) {
        return converter(enviar("GET", caminho, null), tipo);
    }

    protected <T> List<T> getLista(String caminho, Class<T> tipo) {

        String corpo = enviar("GET", caminho, null);

        try {
            return MAPPER.readValue(
                    corpo,
                    MAPPER.getTypeFactory()
                            .constructCollectionType(List.class, tipo)
            );
        } catch (JsonProcessingException e) {
            throw new ApiException("Resposta inválida da API.", e);
        }
    }

    protected <T> T post(String caminho, Object corpo, Class<T> tipo) {
        return converter(enviar("POST", caminho, corpo), tipo);
    }

    protected <T> T put(String caminho, Object corpo, Class<T> tipo) {
        return converter(enviar("PUT", caminho, corpo), tipo);
    }

    protected void delete(String caminho) {
        enviar("DELETE", caminho, null);
    }

    /** Retorna o corpo da resposta ou lança ApiException em caso de erro. */
    private String enviar(String metodo, String caminho, Object corpo) {

        try {
            HttpRequest.Builder requisicao =
                    HttpRequest.newBuilder(URI.create(baseUrl + caminho))
                            .timeout(Duration.ofSeconds(30))
                            .header("Accept", "application/json");

            if (corpo == null) {
                requisicao.method(
                        metodo,
                        HttpRequest.BodyPublishers.noBody()
                );
            } else {
                requisicao
                        .header("Content-Type", "application/json")
                        .method(
                                metodo,
                                HttpRequest.BodyPublishers.ofString(
                                        MAPPER.writeValueAsString(corpo)
                                )
                        );
            }

            HttpResponse<String> resposta =
                    HTTP.send(
                            requisicao.build(),
                            HttpResponse.BodyHandlers.ofString()
                    );

            if (resposta.statusCode() >= 200 && resposta.statusCode() < 300) {
                return resposta.body();
            }

            throw new ApiException(
                    extrairMensagem(resposta),
                    resposta.statusCode()
            );

        } catch (IOException e) {
            throw new ApiException(
                    "Não foi possível conectar à API em " + baseUrl
                            + ". Verifique se o servidor está em execução.",
                    e
            );

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

            throw new ApiException("A requisição foi interrompida.", e);
        }
    }

    private String extrairMensagem(HttpResponse<String> resposta) {

        try {
            ErroResponse erro =
                    MAPPER.readValue(resposta.body(), ErroResponse.class);

            if (erro.mensagem() != null && !erro.mensagem().isBlank()) {
                return erro.mensagem();
            }
        } catch (IOException | RuntimeException e) {
            // Corpo fora do padrão: usa a mensagem genérica abaixo.
        }

        return "A API respondeu com erro HTTP " + resposta.statusCode() + ".";
    }

    private <T> T converter(String corpo, Class<T> tipo) {

        try {
            return MAPPER.readValue(corpo, tipo);
        } catch (JsonProcessingException e) {
            throw new ApiException("Resposta inválida da API.", e);
        }
    }

    /** Executa a chamada e devolve null se a API responder 404. */
    protected <T> T ouNulo(java.util.function.Supplier<T> chamada) {

        try {
            return chamada.get();
        } catch (ApiException e) {
            if (e.getStatus() == 404) {
                return null;
            }

            throw e;
        }
    }
}

package br.com.fiap.auraai.client;

import br.com.fiap.auraai.domain.model.Cliente;
import br.com.fiap.auraai.dto.ClienteRequest;
import br.com.fiap.auraai.dto.ClienteResponse;

import java.util.List;

public class ClienteApiClient extends ApiClientBase {

    public Cliente cadastrar(
            String nome,
            String cnpj,
            String segmento,
            String email
    ) {

        ClienteResponse resposta = post(
                "/clientes",
                new ClienteRequest(nome, cnpj, segmento, email),
                ClienteResponse.class
        );

        return Conversor.paraCliente(resposta);
    }

    /** Retorna null se o cliente não existir. */
    public Cliente buscarPorId(Long id) {

        ClienteResponse resposta = ouNulo(
                () -> get("/clientes/" + id, ClienteResponse.class)
        );

        return resposta == null ? null : Conversor.paraCliente(resposta);
    }

    public List<Cliente> listarTodos() {

        return getLista("/clientes", ClienteResponse.class)
                .stream()
                .map(Conversor::paraCliente)
                .toList();
    }

    public void atualizar(
            Long id,
            String nome,
            String cnpj,
            String segmento,
            String email
    ) {

        put(
                "/clientes/" + id,
                new ClienteRequest(nome, cnpj, segmento, email),
                ClienteResponse.class
        );
    }

    public void excluir(Long id) {
        delete("/clientes/" + id);
    }
}

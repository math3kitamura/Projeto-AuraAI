package client;

import domain.model.AnaliseIA;
import dto.AnaliseResponse;

import java.util.List;

public class AnaliseApiClient extends ApiClientBase {

    /** Executa a análise da IA sobre a transcrição da reunião. */
    public AnaliseIA analisarReuniao(Long reuniaoId) {

        AnaliseResponse resposta = post(
                "/reunioes/" + reuniaoId + "/analises",
                null,
                AnaliseResponse.class
        );

        return Conversor.paraAnalise(resposta);
    }

    /** Retorna null se a análise não existir. */
    public AnaliseIA buscarPorId(Long id) {

        AnaliseResponse resposta = ouNulo(
                () -> get("/analises/" + id, AnaliseResponse.class)
        );

        return resposta == null ? null : Conversor.paraAnalise(resposta);
    }

    public List<AnaliseIA> listarTodos() {

        return getLista("/analises", AnaliseResponse.class)
                .stream()
                .map(Conversor::paraAnalise)
                .toList();
    }

    public void excluir(Long id) {
        delete("/analises/" + id);
    }
}

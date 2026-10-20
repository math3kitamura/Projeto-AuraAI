package br.com.fiap.auraai.controller;

import br.com.fiap.auraai.domain.model.AnaliseIA;
import br.com.fiap.auraai.dto.AlertaResponse;
import br.com.fiap.auraai.dto.AnaliseResponse;
import br.com.fiap.auraai.dto.AnaliseUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseStatus;
import br.com.fiap.auraai.service.AnaliseService;

import java.util.List;

@RestController
public class AnaliseController {

    private final AnaliseService analiseService;

    public AnaliseController(AnaliseService analiseService) {
        this.analiseService = analiseService;
    }

    /** Executa a análise da IA sobre a transcrição da reunião. */
    @PostMapping("/reunioes/{reuniaoId}/analises")
    @ResponseStatus(HttpStatus.CREATED)
    public AnaliseResponse analisar(@PathVariable Long reuniaoId) {

        return AnaliseResponse.de(
                analiseService.analisarReuniao(reuniaoId)
        );
    }

    @GetMapping("/analises")
    public List<AnaliseResponse> listar() {

        return analiseService
                .listarTodos()
                .stream()
                .map(AnaliseResponse::de)
                .toList();
    }

    @GetMapping("/analises/{id}")
    public AnaliseResponse buscar(@PathVariable Long id) {

        return AnaliseResponse.de(
                analiseService.buscarPorId(id)
        );
    }

    @GetMapping("/analises/{id}/alertas")
    public List<AlertaResponse> listarAlertas(@PathVariable Long id) {

        return analiseService
                .buscarPorId(id)
                .getAlertas()
                .stream()
                .map(AlertaResponse::de)
                .toList();
    }

    @PatchMapping("/analises/{analiseId}/alertas/{alertaId}/resolver")
    public AlertaResponse resolverAlerta(
            @PathVariable Long analiseId,
            @PathVariable Long alertaId
    ) {

        return AlertaResponse.de(
                analiseService.resolverAlerta(analiseId, alertaId)
        );
    }

    @PutMapping("/analises/{id}")
    public AnaliseResponse atualizar(
            @PathVariable Long id,
            @Valid @RequestBody AnaliseUpdateRequest request
    ) {

        AnaliseIA analise = analiseService.atualizarDadosComerciais(
                id,
                request.persona(),
                request.budget(),
                request.concorrente()
        );

        return AnaliseResponse.de(analise);
    }

    @DeleteMapping("/analises/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        analiseService.excluir(id);
    }
}

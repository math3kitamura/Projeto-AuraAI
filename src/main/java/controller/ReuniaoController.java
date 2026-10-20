package controller;

import domain.model.Reuniao;
import domain.model.Transcricao;
import dto.ReuniaoRequest;
import dto.ReuniaoResponse;
import dto.TranscricaoRequest;
import dto.TranscricaoResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import service.ReuniaoService;

import java.util.List;

@RestController
@RequestMapping("/reunioes")
public class ReuniaoController {

    private final ReuniaoService reuniaoService;

    public ReuniaoController(ReuniaoService reuniaoService) {
        this.reuniaoService = reuniaoService;
    }

    @GetMapping
    public List<ReuniaoResponse> listar() {

        return reuniaoService
                .listarTodos()
                .stream()
                .map(ReuniaoResponse::de)
                .toList();
    }

    @GetMapping("/{id}")
    public ReuniaoResponse buscar(@PathVariable Long id) {

        return ReuniaoResponse.de(
                reuniaoService.buscarPorId(id)
        );
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReuniaoResponse cadastrar(
            @Valid @RequestBody ReuniaoRequest request
    ) {

        Reuniao reuniao = reuniaoService.cadastrar(
                request.titulo(),
                request.dataHora(),
                request.duracaoMin(),
                request.responsavel(),
                request.clienteId()
        );

        return ReuniaoResponse.de(reuniao);
    }

    @PutMapping("/{id}")
    public ReuniaoResponse atualizar(
            @PathVariable Long id,
            @Valid @RequestBody ReuniaoRequest request
    ) {

        Reuniao reuniao = reuniaoService.atualizar(
                id,
                request.titulo(),
                request.dataHora(),
                request.duracaoMin(),
                request.responsavel(),
                request.status(),
                request.clienteId()
        );

        return ReuniaoResponse.de(reuniao);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        reuniaoService.excluir(id);
    }

    @GetMapping("/{id}/transcricao")
    public TranscricaoResponse buscarTranscricao(@PathVariable Long id) {

        return TranscricaoResponse.de(
                reuniaoService.buscarTranscricao(id)
        );
    }

    @PostMapping("/{id}/transcricao")
    @ResponseStatus(HttpStatus.CREATED)
    public TranscricaoResponse cadastrarTranscricao(
            @PathVariable Long id,
            @Valid @RequestBody TranscricaoRequest request
    ) {

        Transcricao transcricao = reuniaoService.cadastrarTranscricao(
                id,
                request.textoBruto(),
                request.idioma()
        );

        return TranscricaoResponse.de(transcricao);
    }
}

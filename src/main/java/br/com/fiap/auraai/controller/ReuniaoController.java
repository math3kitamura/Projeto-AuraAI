package br.com.fiap.auraai.controller;

import br.com.fiap.auraai.domain.model.Reuniao;
import br.com.fiap.auraai.domain.model.Transcricao;
import br.com.fiap.auraai.dto.ReuniaoRequest;
import br.com.fiap.auraai.dto.ReuniaoResponse;
import br.com.fiap.auraai.dto.TranscricaoRequest;
import br.com.fiap.auraai.dto.TranscricaoResponse;
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
import br.com.fiap.auraai.service.ReuniaoService;

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

    @PutMapping("/{id}/transcricao")
    public TranscricaoResponse atualizarTranscricao(
            @PathVariable Long id,
            @Valid @RequestBody TranscricaoRequest request
    ) {

        Transcricao transcricao = reuniaoService.atualizarTranscricao(
                id,
                request.textoBruto(),
                request.idioma()
        );

        return TranscricaoResponse.de(transcricao);
    }

    @DeleteMapping("/{id}/transcricao")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluirTranscricao(@PathVariable Long id) {
        reuniaoService.excluirTranscricao(id);
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

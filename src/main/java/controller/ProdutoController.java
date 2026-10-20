package controller;

import domain.model.ProdutoTOTVS;
import dto.ProdutoRequest;
import dto.ProdutoResponse;
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
import service.ProdutoService;

import java.util.List;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @GetMapping
    public List<ProdutoResponse> listar() {

        return produtoService
                .listarTodos()
                .stream()
                .map(ProdutoResponse::de)
                .toList();
    }

    @GetMapping("/{id}")
    public ProdutoResponse buscar(@PathVariable Long id) {

        return ProdutoResponse.de(
                produtoService.buscarPorId(id)
        );
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProdutoResponse cadastrar(
            @Valid @RequestBody ProdutoRequest request
    ) {

        ProdutoTOTVS produto = produtoService.cadastrar(
                request.nome(),
                request.categoria(),
                request.versao(),
                request.descricao()
        );

        return ProdutoResponse.de(produto);
    }

    @PutMapping("/{id}")
    public ProdutoResponse atualizar(
            @PathVariable Long id,
            @Valid @RequestBody ProdutoRequest request
    ) {

        ProdutoTOTVS produto = produtoService.atualizar(
                id,
                request.nome(),
                request.categoria(),
                request.versao(),
                request.descricao()
        );

        return ProdutoResponse.de(produto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        produtoService.excluir(id);
    }
}

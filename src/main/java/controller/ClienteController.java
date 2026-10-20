package controller;

import domain.model.Cliente;
import dto.ClienteRequest;
import dto.ClienteResponse;
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
import service.ClienteService;

import java.util.List;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    public List<ClienteResponse> listar() {

        return clienteService
                .listarTodos()
                .stream()
                .map(ClienteResponse::de)
                .toList();
    }

    @GetMapping("/{id}")
    public ClienteResponse buscar(@PathVariable Long id) {

        return ClienteResponse.de(
                clienteService.buscarPorId(id)
        );
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClienteResponse cadastrar(
            @Valid @RequestBody ClienteRequest request
    ) {

        Cliente cliente = clienteService.cadastrar(
                request.nome(),
                request.cnpj(),
                request.segmento(),
                request.email()
        );

        return ClienteResponse.de(cliente);
    }

    @PutMapping("/{id}")
    public ClienteResponse atualizar(
            @PathVariable Long id,
            @Valid @RequestBody ClienteRequest request
    ) {

        Cliente cliente = clienteService.atualizar(
                id,
                request.nome(),
                request.cnpj(),
                request.segmento(),
                request.email()
        );

        return ClienteResponse.de(cliente);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        clienteService.excluir(id);
    }
}

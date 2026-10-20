package br.com.fiap.auraai.controller;

import br.com.fiap.auraai.AuraAiApplication;
import br.com.fiap.auraai.domain.model.Cliente;
import br.com.fiap.auraai.exception.ConflitoException;
import br.com.fiap.auraai.exception.GlobalExceptionHandler;
import br.com.fiap.auraai.exception.RecursoNaoEncontradoException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import br.com.fiap.auraai.service.ClienteService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ClienteController.class)
@ContextConfiguration(classes = AuraAiApplication.class)
@Import(GlobalExceptionHandler.class)
class ClienteControllerTest {

    private static final String JSON_VALIDO = """
            {"nome":"Armor Solutions","cnpj":"12.345.678/0001-90",
             "segmento":"Jogos","email":"contato@armor.com"}
            """;

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private ClienteService clienteService;

    private Cliente cliente() {
        return new Cliente(
                1L,
                "Armor Solutions",
                "12.345.678/0001-90",
                "Jogos",
                "contato@armor.com"
        );
    }

    @Test
    void listarRetorna200() throws Exception {

        when(clienteService.listarTodos()).thenReturn(List.of(cliente()));

        mvc.perform(get("/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Armor Solutions"));
    }

    @Test
    void buscarInexistenteRetorna404() throws Exception {

        when(clienteService.buscarPorId(99L))
                .thenThrow(new RecursoNaoEncontradoException("Cliente não encontrado."));

        mvc.perform(get("/clientes/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.mensagem").value("Cliente não encontrado."));
    }

    @Test
    void cadastrarValidoRetorna201() throws Exception {

        when(clienteService.cadastrar(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(cliente());

        mvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_VALIDO))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void cadastrarSemNomeRetorna400() throws Exception {

        mvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome":"","cnpj":"12.345.678/0001-90",
                                 "segmento":"Jogos","email":"a@b.com"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("O nome do cliente é obrigatório."));
    }

    @Test
    void cadastrarCnpjDuplicadoRetorna409() throws Exception {

        when(clienteService.cadastrar(anyString(), anyString(), anyString(), anyString()))
                .thenThrow(new ConflitoException("Já existe um cliente cadastrado com este CNPJ."));

        mvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_VALIDO))
                .andExpect(status().isConflict());
    }

    @Test
    void excluirRetorna204() throws Exception {

        mvc.perform(delete("/clientes/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void excluirComReunioesRetorna409() throws Exception {

        doThrow(new ConflitoException("O cliente possui reuniões vinculadas."))
                .when(clienteService).excluir(anyLong());

        mvc.perform(delete("/clientes/1"))
                .andExpect(status().isConflict());
    }

    @Test
    void idNaoNumericoRetorna400() throws Exception {

        mvc.perform(get("/clientes/abc"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void jsonMalformadoRetorna400() throws Exception {

        mvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{nome"))
                .andExpect(status().isBadRequest());
    }
}

package br.com.fiap.auraai.controller;

import br.com.fiap.auraai.AuraAiApplication;
import br.com.fiap.auraai.domain.model.ProdutoTOTVS;
import br.com.fiap.auraai.exception.ConflitoException;
import br.com.fiap.auraai.exception.RecursoNaoEncontradoException;
import br.com.fiap.auraai.service.ProdutoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProdutoController.class)
@ContextConfiguration(classes = AuraAiApplication.class)
class ProdutoControllerTest {

    private static final String JSON_VALIDO = """
            {"nome":"Protheus","categoria":"ERP","versao":"12.1",
             "descricao":"Sistema integrado de gestão."}
            """;

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private ProdutoService produtoService;

    private ProdutoTOTVS produto() {
        return new ProdutoTOTVS(1L, "Protheus", "ERP", "12.1", "Sistema integrado de gestão.");
    }

    @Test
    void listarRetorna200() throws Exception {

        when(produtoService.listarTodos()).thenReturn(List.of(produto()));

        mvc.perform(get("/produtos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Protheus"));
    }

    @Test
    void buscarInexistenteRetorna404() throws Exception {

        when(produtoService.buscarPorId(9L))
                .thenThrow(new RecursoNaoEncontradoException("Produto não encontrado."));

        mvc.perform(get("/produtos/9"))
                .andExpect(status().isNotFound());
    }

    @Test
    void cadastrarValidoRetorna201() throws Exception {

        when(produtoService.cadastrar(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(produto());

        mvc.perform(post("/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_VALIDO))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void cadastrarSemCategoriaRetorna400() throws Exception {

        mvc.perform(post("/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome":"Protheus","categoria":"","versao":"12.1","descricao":"x"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("A categoria é obrigatória."));
    }

    @Test
    void atualizarRetorna200() throws Exception {

        when(produtoService.atualizar(anyLong(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(produto());

        mvc.perform(put("/produtos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_VALIDO))
                .andExpect(status().isOk());
    }

    @Test
    void excluirRetorna204() throws Exception {

        mvc.perform(delete("/produtos/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void excluirProdutoEmUsoRetorna409() throws Exception {

        doThrow(new ConflitoException("O produto foi identificado em análises."))
                .when(produtoService).excluir(anyLong());

        mvc.perform(delete("/produtos/1"))
                .andExpect(status().isConflict());
    }
}

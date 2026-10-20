package br.com.fiap.auraai.controller;

import br.com.fiap.auraai.AuraAiApplication;
import br.com.fiap.auraai.domain.model.Cliente;
import br.com.fiap.auraai.domain.model.Reuniao;
import br.com.fiap.auraai.domain.model.StatusReuniao;
import br.com.fiap.auraai.domain.model.Transcricao;
import br.com.fiap.auraai.exception.ConflitoException;
import br.com.fiap.auraai.exception.RecursoNaoEncontradoException;
import br.com.fiap.auraai.service.ReuniaoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReuniaoController.class)
@ContextConfiguration(classes = AuraAiApplication.class)
class ReuniaoControllerTest {

    private static final String JSON_VALIDO = """
            {"titulo":"Kickoff","dataHora":"2026-10-20T14:30:00","duracaoMin":60,
             "responsavel":"Matheus","clienteId":1}
            """;

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private ReuniaoService reuniaoService;

    private Reuniao reuniao() {

        Cliente cliente = new Cliente(
                1L, "Armor Solutions", "12.345.678/0001-90", "Jogos", "a@b.com"
        );

        return new Reuniao(
                5L,
                "Kickoff",
                LocalDateTime.of(2026, 10, 20, 14, 30),
                60,
                "Matheus",
                StatusReuniao.PENDENTE,
                cliente
        );
    }

    @Test
    void listarRetorna200() throws Exception {

        when(reuniaoService.listarTodos()).thenReturn(List.of(reuniao()));

        mvc.perform(get("/reunioes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("PENDENTE"))
                .andExpect(jsonPath("$[0].cliente.nome").value("Armor Solutions"));
    }

    @Test
    void cadastrarValidoRetorna201() throws Exception {

        when(reuniaoService.cadastrar(anyString(), any(), anyInt(), anyString(), anyLong()))
                .thenReturn(reuniao());

        mvc.perform(post("/reunioes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_VALIDO))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(5));
    }

    @Test
    void cadastrarComDuracaoInvalidaRetorna400() throws Exception {

        mvc.perform(post("/reunioes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"titulo":"Kickoff","dataHora":"2026-10-20T14:30:00",
                                 "duracaoMin":0,"responsavel":"Matheus","clienteId":1}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("A duração deve ser maior que zero."));
    }

    @Test
    void cadastrarComClienteInexistenteRetorna404() throws Exception {

        when(reuniaoService.cadastrar(anyString(), any(), anyInt(), anyString(), anyLong()))
                .thenThrow(new RecursoNaoEncontradoException("Cliente não encontrado."));

        mvc.perform(post("/reunioes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_VALIDO))
                .andExpect(status().isNotFound());
    }

    @Test
    void atualizarComStatusInvalidoRetorna400() throws Exception {

        mvc.perform(put("/reunioes/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"titulo":"Kickoff","dataHora":"2026-10-20T14:30:00",
                                 "duracaoMin":60,"responsavel":"Matheus","clienteId":1,
                                 "status":"XYZ"}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void excluirRetorna204() throws Exception {

        mvc.perform(delete("/reunioes/5"))
                .andExpect(status().isNoContent());
    }

    @Test
    void buscarTranscricaoInexistenteRetorna404() throws Exception {

        when(reuniaoService.buscarTranscricao(5L))
                .thenThrow(new RecursoNaoEncontradoException("A reunião não possui transcrição."));

        mvc.perform(get("/reunioes/5/transcricao"))
                .andExpect(status().isNotFound());
    }

    @Test
    void cadastrarTranscricaoRetorna201() throws Exception {

        Transcricao transcricao =
                new Transcricao(7L, "O cliente quer contratar.", "pt-BR", reuniao());

        when(reuniaoService.cadastrarTranscricao(eq(5L), anyString(), anyString()))
                .thenReturn(transcricao);

        mvc.perform(post("/reunioes/5/transcricao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"textoBruto":"O cliente quer contratar.","idioma":"pt-BR"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.qtdPalavras").value(4))
                .andExpect(jsonPath("$.reuniaoId").value(5));
    }

    @Test
    void cadastrarSegundaTranscricaoRetorna409() throws Exception {

        when(reuniaoService.cadastrarTranscricao(eq(5L), anyString(), anyString()))
                .thenThrow(new ConflitoException("A reunião já possui uma transcrição."));

        mvc.perform(post("/reunioes/5/transcricao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"textoBruto":"texto","idioma":"pt-BR"}
                                """))
                .andExpect(status().isConflict());
    }

    @Test
    void atualizarTranscricaoRetorna200() throws Exception {

        Transcricao transcricao =
                new Transcricao(7L, "Novo texto aqui.", "pt-BR", reuniao());

        when(reuniaoService.atualizarTranscricao(eq(5L), anyString(), anyString()))
                .thenReturn(transcricao);

        mvc.perform(put("/reunioes/5/transcricao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"textoBruto":"Novo texto aqui.","idioma":"pt-BR"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.textoBruto").value("Novo texto aqui."));
    }

    @Test
    void excluirTranscricaoRetorna204() throws Exception {

        mvc.perform(delete("/reunioes/5/transcricao"))
                .andExpect(status().isNoContent());
    }

    @Test
    void excluirTranscricaoInexistenteRetorna404() throws Exception {

        doThrow(new RecursoNaoEncontradoException("A reunião não possui transcrição."))
                .when(reuniaoService).excluirTranscricao(anyLong());

        mvc.perform(delete("/reunioes/5/transcricao"))
                .andExpect(status().isNotFound());
    }
}

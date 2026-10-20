package br.com.fiap.auraai.controller;

import br.com.fiap.auraai.AuraAiApplication;
import br.com.fiap.auraai.domain.model.Alerta;
import br.com.fiap.auraai.domain.model.AnaliseIA;
import br.com.fiap.auraai.domain.model.Cliente;
import br.com.fiap.auraai.domain.model.PrioridadeAlerta;
import br.com.fiap.auraai.domain.model.Reuniao;
import br.com.fiap.auraai.domain.model.Sentimento;
import br.com.fiap.auraai.domain.model.StatusAlerta;
import br.com.fiap.auraai.domain.model.StatusReuniao;
import br.com.fiap.auraai.domain.model.TipoAlerta;
import br.com.fiap.auraai.exception.RecursoNaoEncontradoException;
import br.com.fiap.auraai.exception.RegraNegocioException;
import br.com.fiap.auraai.service.AnaliseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AnaliseController.class)
@ContextConfiguration(classes = AuraAiApplication.class)
class AnaliseControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private AnaliseService analiseService;

    private AnaliseIA analise() {

        Cliente cliente = new Cliente(
                1L, "Armor Solutions", "12.345.678/0001-90", "Jogos", "a@b.com"
        );

        Reuniao reuniao = new Reuniao(
                5L,
                "Kickoff",
                LocalDateTime.of(2026, 10, 20, 14, 30),
                60,
                "Matheus",
                StatusReuniao.PROCESSADA,
                cliente
        );

        AnaliseIA analise = new AnaliseIA(
                9L,
                reuniao,
                Sentimento.POSITIVO,
                0.15,
                0.90,
                "Decisor",
                new BigDecimal("50000.00"),
                "SAP"
        );

        analise.adicionarAlerta(
                new Alerta(
                        3L,
                        TipoAlerta.UPSELL,
                        "Oportunidade comercial.",
                        PrioridadeAlerta.ALTA,
                        StatusAlerta.ABERTO
                )
        );

        return analise;
    }

    @Test
    void analisarReuniaoRetorna201() throws Exception {

        when(analiseService.analisarReuniao(5L)).thenReturn(analise());

        mvc.perform(post("/reunioes/5/analises"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sentimento").value("POSITIVO"))
                .andExpect(jsonPath("$.alertas[0].tipo").value("UPSELL"))
                .andExpect(jsonPath("$.reuniao.cliente.nome").value("Armor Solutions"));
    }

    @Test
    void analisarReuniaoSemTranscricaoRetorna422() throws Exception {

        when(analiseService.analisarReuniao(5L))
                .thenThrow(new RegraNegocioException("A reunião não possui transcrição."));

        mvc.perform(post("/reunioes/5/analises"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.mensagem").value("A reunião não possui transcrição."));
    }

    @Test
    void listarRetorna200() throws Exception {

        when(analiseService.listarTodos()).thenReturn(List.of(analise()));

        mvc.perform(get("/analises"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(9));
    }

    @Test
    void buscarInexistenteRetorna404() throws Exception {

        when(analiseService.buscarPorId(99L))
                .thenThrow(new RecursoNaoEncontradoException("Análise não encontrada."));

        mvc.perform(get("/analises/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void listarAlertasRetorna200() throws Exception {

        when(analiseService.buscarPorId(9L)).thenReturn(analise());

        mvc.perform(get("/analises/9/alertas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].prioridade").value("ALTA"));
    }

    @Test
    void resolverAlertaRetorna200() throws Exception {

        Alerta resolvido = new Alerta(
                3L,
                TipoAlerta.UPSELL,
                "Oportunidade comercial.",
                PrioridadeAlerta.ALTA,
                StatusAlerta.RESOLVIDO
        );

        when(analiseService.resolverAlerta(9L, 3L)).thenReturn(resolvido);

        mvc.perform(patch("/analises/9/alertas/3/resolver"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RESOLVIDO"));
    }

    @Test
    void resolverAlertaInexistenteRetorna404() throws Exception {

        when(analiseService.resolverAlerta(9L, 77L))
                .thenThrow(new RecursoNaoEncontradoException("Alerta não encontrado nesta análise."));

        mvc.perform(patch("/analises/9/alertas/77/resolver"))
                .andExpect(status().isNotFound());
    }

    @Test
    void atualizarRetorna200() throws Exception {

        when(analiseService.atualizarDadosComerciais(anyLong(), anyString(), any(), any()))
                .thenReturn(analise());

        mvc.perform(put("/analises/9")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"persona":"Decisor","budget":75000.00,"concorrente":"SAP"}
                                """))
                .andExpect(status().isOk());
    }

    @Test
    void atualizarComBudgetNegativoRetorna400() throws Exception {

        mvc.perform(put("/analises/9")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"persona":"Decisor","budget":-1,"concorrente":"SAP"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("O budget deve ser maior ou igual a zero."));
    }

    @Test
    void excluirRetorna204() throws Exception {

        mvc.perform(delete("/analises/9"))
                .andExpect(status().isNoContent());
    }
}

package br.com.fiap.auraai.service;

import br.com.fiap.auraai.dao.AnaliseIADAO;
import br.com.fiap.auraai.dao.ProdutoDAO;
import br.com.fiap.auraai.dao.ReuniaoDAO;
import br.com.fiap.auraai.domain.model.Alerta;
import br.com.fiap.auraai.domain.model.AnaliseIA;
import br.com.fiap.auraai.domain.model.Cliente;
import br.com.fiap.auraai.domain.model.PrioridadeAlerta;
import br.com.fiap.auraai.domain.model.ProdutoTOTVS;
import br.com.fiap.auraai.domain.model.Reuniao;
import br.com.fiap.auraai.domain.model.Sentimento;
import br.com.fiap.auraai.domain.model.StatusAlerta;
import br.com.fiap.auraai.domain.model.StatusReuniao;
import br.com.fiap.auraai.domain.model.TipoAlerta;
import br.com.fiap.auraai.domain.model.Transcricao;
import br.com.fiap.auraai.exception.RecursoNaoEncontradoException;
import br.com.fiap.auraai.exception.RegraNegocioException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnaliseServiceTest {

    @Mock
    private AnaliseIADAO analiseDAO;

    @Mock
    private ReuniaoDAO reuniaoDAO;

    @Mock
    private ProdutoDAO produtoDAO;

    @Mock
    private ReuniaoService reuniaoService;

    private AnaliseService service;

    private Reuniao reuniao;

    @BeforeEach
    void preparar() {

        service = new AnaliseService(analiseDAO, reuniaoDAO, produtoDAO, reuniaoService);

        Cliente cliente = new Cliente(1L, "Armor", "12.345.678/0001-90", "Jogos", "a@b.com");

        reuniao = new Reuniao(
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
    void analisarSemTranscricaoLancaRegraDeNegocio() throws SQLException {

        when(reuniaoService.buscarPorId(5L)).thenReturn(reuniao);
        when(reuniaoDAO.buscarTranscricaoPorReuniao(5L)).thenReturn(null);

        assertThrows(
                RegraNegocioException.class,
                () -> service.analisarReuniao(5L)
        );

        verify(analiseDAO, never()).inserir(any());
    }

    @Test
    void analisarGravaAnaliseEMarcaReuniaoComoProcessada() throws SQLException {

        Transcricao transcricao = new Transcricao(
                7L,
                "O cliente demonstrou interesse em contratar o Protheus e avalia o SAP.",
                "pt-BR",
                reuniao
        );

        ProdutoTOTVS protheus =
                new ProdutoTOTVS(1L, "Protheus", "ERP", "12.1", "Sistema de gestão.");

        when(reuniaoService.buscarPorId(5L)).thenReturn(reuniao);
        when(reuniaoDAO.buscarTranscricaoPorReuniao(5L)).thenReturn(transcricao);
        when(produtoDAO.listarTodos()).thenReturn(List.of(protheus));

        AnaliseIA resultado = service.analisarReuniao(5L);

        ArgumentCaptor<AnaliseIA> captor = ArgumentCaptor.forClass(AnaliseIA.class);
        verify(analiseDAO).inserir(captor.capture());

        assertEquals(Sentimento.POSITIVO, captor.getValue().getSentimento());
        assertEquals(1, resultado.getProdutosIdentificados().size());
        assertEquals(StatusReuniao.PROCESSADA, reuniao.getStatus());
    }

    @Test
    void buscarAnaliseInexistenteLancaNaoEncontrado() throws SQLException {

        when(analiseDAO.buscarPorId(9L)).thenReturn(null);

        assertThrows(
                RecursoNaoEncontradoException.class,
                () -> service.buscarPorId(9L)
        );
    }

    @Test
    void resolverAlertaPersisteONovoStatus() throws SQLException {

        AnaliseIA analise = new AnaliseIA(
                9L, reuniao, Sentimento.POSITIVO, 0.15, 0.90,
                "Decisor", new BigDecimal("50000.00"), null
        );

        analise.adicionarAlerta(
                new Alerta(3L, TipoAlerta.UPSELL, "Oportunidade.",
                        PrioridadeAlerta.ALTA, StatusAlerta.ABERTO)
        );

        when(analiseDAO.buscarPorId(9L)).thenReturn(analise);

        Alerta resolvido = service.resolverAlerta(9L, 3L);

        assertEquals(StatusAlerta.RESOLVIDO, resolvido.getStatus());
        verify(analiseDAO).atualizarStatusAlerta(3L, StatusAlerta.RESOLVIDO);
    }

    @Test
    void resolverAlertaDeOutraAnaliseLancaNaoEncontrado() throws SQLException {

        AnaliseIA analise = new AnaliseIA(
                9L, reuniao, Sentimento.NEUTRO, 0.3, 0.4,
                "Decisor", new BigDecimal("50000.00"), null
        );

        when(analiseDAO.buscarPorId(9L)).thenReturn(analise);

        assertThrows(
                RecursoNaoEncontradoException.class,
                () -> service.resolverAlerta(9L, 77L)
        );

        verify(analiseDAO, never()).atualizarStatusAlerta(any(), any());
    }

    @Test
    void atualizarDadosComerciaisGravaAAnalise() throws SQLException {

        AnaliseIA analise = new AnaliseIA(
                9L, reuniao, Sentimento.POSITIVO, 0.15, 0.90,
                "Decisor", new BigDecimal("50000.00"), "SAP"
        );

        when(analiseDAO.buscarPorId(9L)).thenReturn(analise);

        AnaliseIA atualizada = service.atualizarDadosComerciais(
                9L, "Influenciador", new BigDecimal("80000.00"), " "
        );

        assertEquals("Influenciador", atualizada.getPersona());
        assertEquals(new BigDecimal("80000.00"), atualizada.getBudget());
        assertTrue(atualizada.getConcorrente() == null);
        verify(analiseDAO).atualizarDadosComerciais(atualizada);
    }

    @Test
    void atualizarComBudgetNegativoLancaIllegalArgument() throws SQLException {

        AnaliseIA analise = new AnaliseIA(
                9L, reuniao, Sentimento.POSITIVO, 0.15, 0.90,
                "Decisor", new BigDecimal("50000.00"), null
        );

        when(analiseDAO.buscarPorId(9L)).thenReturn(analise);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.atualizarDadosComerciais(9L, "Decisor", new BigDecimal("-1"), null)
        );

        verify(analiseDAO, never()).atualizarDadosComerciais(any());
    }
}

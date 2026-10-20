package br.com.fiap.auraai.service;

import br.com.fiap.auraai.dao.ReuniaoDAO;
import br.com.fiap.auraai.domain.model.Cliente;
import br.com.fiap.auraai.domain.model.Reuniao;
import br.com.fiap.auraai.domain.model.StatusReuniao;
import br.com.fiap.auraai.domain.model.Transcricao;
import br.com.fiap.auraai.exception.ConflitoException;
import br.com.fiap.auraai.exception.RecursoNaoEncontradoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReuniaoServiceTest {

    @Mock
    private ReuniaoDAO reuniaoDAO;

    @Mock
    private ClienteService clienteService;

    private ReuniaoService service;

    private Cliente cliente;
    private Reuniao reuniao;

    @BeforeEach
    void preparar() {

        service = new ReuniaoService(reuniaoDAO, clienteService);

        cliente = new Cliente(1L, "Armor", "12.345.678/0001-90", "Jogos", "a@b.com");

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
    void cadastrarComClienteInexistentePropagaNaoEncontrado() {

        when(clienteService.buscarPorId(1L))
                .thenThrow(new RecursoNaoEncontradoException("Cliente não encontrado."));

        assertThrows(
                RecursoNaoEncontradoException.class,
                () -> service.cadastrar("Kickoff", LocalDateTime.now(), 60, "Matheus", 1L)
        );
    }

    @Test
    void cadastrarComDuracaoInvalidaLancaIllegalArgument() {

        when(clienteService.buscarPorId(1L)).thenReturn(cliente);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.cadastrar("Kickoff", LocalDateTime.now(), 0, "Matheus", 1L)
        );
    }

    @Test
    void segundaTranscricaoLancaConflitoSemInserir() throws SQLException {

        Transcricao existente = new Transcricao(7L, "texto", "pt-BR", reuniao);

        when(reuniaoDAO.buscarPorId(5L)).thenReturn(reuniao);
        when(reuniaoDAO.buscarTranscricaoPorReuniao(5L)).thenReturn(existente);

        assertThrows(
                ConflitoException.class,
                () -> service.cadastrarTranscricao(5L, "outro texto", "pt-BR")
        );

        verify(reuniaoDAO, never()).inserirTranscricao(any());
    }

    @Test
    void primeiraTranscricaoEhInserida() throws SQLException {

        when(reuniaoDAO.buscarPorId(5L)).thenReturn(reuniao);
        when(reuniaoDAO.buscarTranscricaoPorReuniao(5L)).thenReturn(null);

        Transcricao criada =
                service.cadastrarTranscricao(5L, "O cliente quer contratar.", "pt-BR");

        assertEquals(4, criada.getQtdPalavras());
        verify(reuniaoDAO).inserirTranscricao(criada);
    }

    @Test
    void atualizarComStatusInvalidoLancaIllegalArgument() throws SQLException {

        when(reuniaoDAO.buscarPorId(5L)).thenReturn(reuniao);
        when(clienteService.buscarPorId(1L)).thenReturn(cliente);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.atualizar(
                        5L, "Kickoff", LocalDateTime.now(), 60, "Matheus", "XYZ", 1L
                )
        );
    }

    @Test
    void atualizarSemStatusMantemOStatusAtual() throws SQLException {

        when(reuniaoDAO.buscarPorId(5L)).thenReturn(reuniao);
        when(clienteService.buscarPorId(1L)).thenReturn(cliente);

        Reuniao atualizada = service.atualizar(
                5L, "Novo título", LocalDateTime.now(), 90, "Matheus", null, 1L
        );

        assertEquals(StatusReuniao.PENDENTE, atualizada.getStatus());
        assertEquals("Novo título", atualizada.getTitulo());
    }

    @Test
    void atualizarTranscricaoRecalculaQuantidadeDePalavras() throws SQLException {

        Transcricao existente = new Transcricao(7L, "texto curto", "pt-BR", reuniao);

        when(reuniaoDAO.buscarPorId(5L)).thenReturn(reuniao);
        when(reuniaoDAO.buscarTranscricaoPorReuniao(5L)).thenReturn(existente);

        Transcricao atualizada =
                service.atualizarTranscricao(5L, "um texto bem mais longo agora", "pt-BR");

        assertEquals(6, atualizada.getQtdPalavras());
        verify(reuniaoDAO).atualizarTranscricao(atualizada);
    }

    @Test
    void excluirTranscricaoInexistenteLancaNaoEncontrado() throws SQLException {

        when(reuniaoDAO.buscarPorId(5L)).thenReturn(reuniao);
        when(reuniaoDAO.buscarTranscricaoPorReuniao(5L)).thenReturn(null);

        assertThrows(
                RecursoNaoEncontradoException.class,
                () -> service.excluirTranscricao(5L)
        );

        verify(reuniaoDAO, never()).excluirTranscricao(any());
    }
}

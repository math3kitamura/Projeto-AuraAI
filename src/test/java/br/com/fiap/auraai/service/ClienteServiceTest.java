package br.com.fiap.auraai.service;

import br.com.fiap.auraai.dao.ClienteDAO;
import br.com.fiap.auraai.domain.model.Cliente;
import br.com.fiap.auraai.exception.ConflitoException;
import br.com.fiap.auraai.exception.ErroPersistenciaException;
import br.com.fiap.auraai.exception.RecursoNaoEncontradoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteDAO clienteDAO;

    @InjectMocks
    private ClienteService service;

    private Cliente cliente() {
        return new Cliente(1L, "Armor", "12.345.678/0001-90", "Jogos", "a@b.com");
    }

    @Test
    void cadastrarComCnpjDuplicadoLancaConflito() throws SQLException {

        doThrow(new SQLException("duplicate key", "23505"))
                .when(clienteDAO).inserir(any());

        ConflitoException erro = assertThrows(
                ConflitoException.class,
                () -> service.cadastrar("Armor", "12.345.678/0001-90", "Jogos", "a@b.com")
        );

        assertEquals("Já existe um cliente cadastrado com este CNPJ.", erro.getMessage());
    }

    @Test
    void cadastrarComNomeEmBrancoLancaIllegalArgument() {

        assertThrows(
                IllegalArgumentException.class,
                () -> service.cadastrar(" ", "12.345.678/0001-90", "Jogos", "a@b.com")
        );
    }

    @Test
    void buscarInexistenteLancaNaoEncontrado() throws SQLException {

        when(clienteDAO.buscarPorId(9L)).thenReturn(null);

        assertThrows(
                RecursoNaoEncontradoException.class,
                () -> service.buscarPorId(9L)
        );
    }

    @Test
    void excluirClienteComReunioesLancaConflito() throws SQLException {

        when(clienteDAO.buscarPorId(1L)).thenReturn(cliente());
        doThrow(new SQLException("fk violation", "23503"))
                .when(clienteDAO).excluir(1L);

        ConflitoException erro = assertThrows(
                ConflitoException.class,
                () -> service.excluir(1L)
        );

        assertEquals(
                "Não é possível excluir: o cliente possui reuniões vinculadas.",
                erro.getMessage()
        );
    }

    @Test
    void excluirClienteInexistenteNaoChamaODao() throws SQLException {

        when(clienteDAO.buscarPorId(9L)).thenReturn(null);

        assertThrows(
                RecursoNaoEncontradoException.class,
                () -> service.excluir(9L)
        );
    }

    @Test
    void falhaGenericaDoBancoViraErroDePersistencia() throws SQLException {

        when(clienteDAO.listarTodos()).thenThrow(new SQLException("conexão recusada", "08001"));

        assertThrows(
                ErroPersistenciaException.class,
                () -> service.listarTodos()
        );
    }

    @Test
    void atualizarGravaOClienteExistente() throws SQLException {

        when(clienteDAO.buscarPorId(1L)).thenReturn(cliente());

        Cliente atualizado =
                service.atualizar(1L, "Armor Games", "12.345.678/0001-90", "Jogos", "a@b.com");

        assertEquals("Armor Games", atualizado.getNome());
        verify(clienteDAO).atualizar(atualizado);
    }
}

package service;

import dao.ReuniaoDAO;
import domain.model.Cliente;
import domain.model.Reuniao;
import domain.model.Transcricao;
import exception.ConflitoException;
import exception.RecursoNaoEncontradoException;
import exception.TradutorSqlException;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReuniaoService {

    private static final String MSG_TRANSCRICAO_EXISTENTE =
            "A reunião já possui uma transcrição.";

    private final ReuniaoDAO reuniaoDAO;
    private final ClienteService clienteService;

    public ReuniaoService(
            ReuniaoDAO reuniaoDAO,
            ClienteService clienteService
    ) {
        this.reuniaoDAO = reuniaoDAO;
        this.clienteService = clienteService;
    }

    public Reuniao cadastrar(
            String titulo,
            LocalDateTime dataHora,
            int duracaoMin,
            String responsavel,
            Long clienteId
    ) {

        Cliente cliente = clienteService.buscarPorId(clienteId);

        Reuniao reuniao =
                new Reuniao(titulo, dataHora, duracaoMin, responsavel, cliente);

        try {
            reuniaoDAO.inserir(reuniao);
        } catch (SQLException e) {
            throw TradutorSqlException.traduzir(e, null);
        }

        return reuniao;
    }

    public Reuniao buscarPorId(Long id) {

        try {
            Reuniao reuniao = reuniaoDAO.buscarPorId(id);

            if (reuniao == null) {
                throw new RecursoNaoEncontradoException(
                        "Reunião não encontrada."
                );
            }

            return reuniao;

        } catch (SQLException e) {
            throw TradutorSqlException.traduzir(e, null);
        }
    }

    public List<Reuniao> listarTodos() {

        try {
            return reuniaoDAO.listarTodos();
        } catch (SQLException e) {
            throw TradutorSqlException.traduzir(e, null);
        }
    }

    public Reuniao atualizar(
            Long id,
            String titulo,
            LocalDateTime dataHora,
            int duracaoMin,
            String responsavel,
            String status,
            Long clienteId
    ) {

        Reuniao atual = buscarPorId(id);
        Cliente cliente = clienteService.buscarPorId(clienteId);

        Reuniao reuniao = new Reuniao(
                id,
                titulo,
                dataHora,
                duracaoMin,
                responsavel,
                status == null || status.isBlank()
                        ? atual.getStatus()
                        : status,
                cliente
        );

        try {
            reuniaoDAO.atualizar(reuniao);
        } catch (SQLException e) {
            throw TradutorSqlException.traduzir(e, null);
        }

        return reuniao;
    }

    public void excluir(Long id) {

        buscarPorId(id);

        try {
            reuniaoDAO.excluir(id);
        } catch (SQLException e) {
            throw TradutorSqlException.traduzir(e, null);
        }
    }

    public Transcricao cadastrarTranscricao(
            Long reuniaoId,
            String texto,
            String idioma
    ) {

        Reuniao reuniao = buscarPorId(reuniaoId);

        try {
            if (reuniaoDAO.buscarTranscricaoPorReuniao(reuniaoId) != null) {
                throw new ConflitoException(MSG_TRANSCRICAO_EXISTENTE);
            }

            Transcricao transcricao =
                    new Transcricao(texto, idioma, reuniao);

            reuniaoDAO.inserirTranscricao(transcricao);

            reuniao.adicionarTranscricao(transcricao);

            return transcricao;

        } catch (SQLException e) {
            throw TradutorSqlException.traduzir(
                    e,
                    MSG_TRANSCRICAO_EXISTENTE
            );
        }
    }

    public Transcricao buscarTranscricao(Long reuniaoId) {

        buscarPorId(reuniaoId);

        try {
            Transcricao transcricao =
                    reuniaoDAO.buscarTranscricaoPorReuniao(reuniaoId);

            if (transcricao == null) {
                throw new RecursoNaoEncontradoException(
                        "A reunião não possui transcrição."
                );
            }

            return transcricao;

        } catch (SQLException e) {
            throw TradutorSqlException.traduzir(e, null);
        }
    }
}

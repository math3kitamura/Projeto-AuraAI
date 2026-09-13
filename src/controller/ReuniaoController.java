package controller;

import dao.ClienteDAO;
import dao.ClienteDAOImpl;
import dao.ReuniaoDAO;
import dao.ReuniaoDAOImpl;

import domain.model.Cliente;
import domain.model.Reuniao;
import domain.model.Transcricao;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class ReuniaoController {

    private final ReuniaoDAO reuniaoDAO;
    private final ClienteDAO clienteDAO;

    public ReuniaoController() {

        this.reuniaoDAO =
                new ReuniaoDAOImpl();

        this.clienteDAO =
                new ClienteDAOImpl();
    }

    public Reuniao cadastrar(
            String titulo,
            LocalDateTime dataHora,
            int duracaoMin,
            String responsavel,
            Long clienteId
    ) throws SQLException {

        Cliente cliente =
                clienteDAO.buscarPorId(
                        clienteId
                );

        if (cliente == null) {
            throw new IllegalArgumentException(
                    "Cliente não encontrado."
            );
        }

        Reuniao reuniao =
                new Reuniao(
                        titulo,
                        dataHora,
                        duracaoMin,
                        responsavel,
                        cliente
                );

        reuniaoDAO.inserir(reuniao);

        return reuniao;
    }

    public Transcricao cadastrarTranscricao(
            String texto,
            String idioma,
            Long reuniaoId
    ) throws SQLException {

        Reuniao reuniao =
                reuniaoDAO.buscarPorId(
                        reuniaoId
                );

        if (reuniao == null) {
            throw new IllegalArgumentException(
                    "Reunião não encontrada."
            );
        }

        Transcricao transcricao =
                new Transcricao(
                        texto,
                        idioma,
                        reuniao
                );

        reuniaoDAO.inserirTranscricao(
                transcricao
        );

        reuniao.adicionarTranscricao(
                transcricao
        );

        return transcricao;
    }

    public Reuniao buscarPorId(Long id)
            throws SQLException {

        return reuniaoDAO.buscarPorId(id);
    }

    public List<Reuniao> listarTodos()
            throws SQLException {

        return reuniaoDAO.listarTodos();
    }

    public void atualizar(Reuniao reuniao)
            throws SQLException {

        reuniaoDAO.atualizar(reuniao);
    }

    public void excluir(Long id)
            throws SQLException {

        reuniaoDAO.excluir(id);
    }

    public Transcricao buscarTranscricao(
            Long reuniaoId
    ) throws SQLException {

        return reuniaoDAO
                .buscarTranscricaoPorReuniao(
                        reuniaoId
                );
    }
}

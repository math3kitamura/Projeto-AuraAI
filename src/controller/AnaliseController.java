package controller;

import dao.AnaliseIADAO;
import dao.AnaliseIADAOImpl;
import dao.ProdutoDAO;
import dao.ProdutoDAOImpl;
import dao.ReuniaoDAO;
import dao.ReuniaoDAOImpl;

import domain.model.ProdutoTOTVS;
import domain.model.Reuniao;
import domain.model.Transcricao;

import domain.service.AnaliseIA;

import java.sql.SQLException;
import java.util.List;

public class AnaliseController {

    private final AnaliseIADAO analiseDAO;
    private final ReuniaoDAO reuniaoDAO;
    private final ProdutoDAO produtoDAO;

    public AnaliseController() {

        this.analiseDAO =
                new AnaliseIADAOImpl();

        this.reuniaoDAO =
                new ReuniaoDAOImpl();

        this.produtoDAO =
                new ProdutoDAOImpl();
    }

    public AnaliseIA analisarReuniao(
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
                reuniaoDAO
                        .buscarTranscricaoPorReuniao(
                                reuniaoId
                        );

        if (transcricao == null) {
            throw new IllegalArgumentException(
                    "A reunião não possui transcrição."
            );
        }

        reuniao.adicionarTranscricao(
                transcricao
        );

        AnaliseIA analise =
                new AnaliseIA(
                        reuniao
                );

        analise.analisar();

        List<ProdutoTOTVS> produtos =
                produtoDAO.listarTodos();

        for (ProdutoTOTVS produto : produtos) {

            analise.mapearProduto(
                    produto
            );
        }

        analiseDAO.inserir(
                analise
        );

        reuniao.adicionarAnalise(
                analise
        );

        reuniaoDAO.atualizar(
                reuniao
        );

        return analise;
    }

    public AnaliseIA buscarPorId(Long id)
            throws SQLException {

        return analiseDAO.buscarPorId(id);
    }

    public List<AnaliseIA> listarTodos()
            throws SQLException {

        return analiseDAO.listarTodos();
    }

    public void atualizar(
            AnaliseIA analise
    ) throws SQLException {

        analiseDAO.atualizar(
                analise
        );
    }

    public void excluir(Long id)
            throws SQLException {

        analiseDAO.excluir(id);
    }
}
package br.com.fiap.auraai.service;

import br.com.fiap.auraai.dao.AnaliseIADAO;
import br.com.fiap.auraai.dao.ProdutoDAO;
import br.com.fiap.auraai.dao.ReuniaoDAO;
import br.com.fiap.auraai.domain.model.Alerta;
import br.com.fiap.auraai.domain.model.AnaliseIA;
import br.com.fiap.auraai.domain.model.ProdutoTOTVS;
import br.com.fiap.auraai.domain.model.Reuniao;
import br.com.fiap.auraai.domain.model.Transcricao;
import br.com.fiap.auraai.exception.RecursoNaoEncontradoException;
import br.com.fiap.auraai.exception.RegraNegocioException;
import br.com.fiap.auraai.exception.TradutorSqlException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

@Service
public class AnaliseService {

    private final AnaliseIADAO analiseDAO;
    private final ReuniaoDAO reuniaoDAO;
    private final ProdutoDAO produtoDAO;
    private final ReuniaoService reuniaoService;

    public AnaliseService(
            AnaliseIADAO analiseDAO,
            ReuniaoDAO reuniaoDAO,
            ProdutoDAO produtoDAO,
            ReuniaoService reuniaoService
    ) {
        this.analiseDAO = analiseDAO;
        this.reuniaoDAO = reuniaoDAO;
        this.produtoDAO = produtoDAO;
        this.reuniaoService = reuniaoService;
    }

    public AnaliseIA analisarReuniao(Long reuniaoId) {

        Reuniao reuniao = reuniaoService.buscarPorId(reuniaoId);

        try {
            Transcricao transcricao =
                    reuniaoDAO.buscarTranscricaoPorReuniao(reuniaoId);

            if (transcricao == null) {
                throw new RegraNegocioException(
                        "A reunião não possui transcrição."
                );
            }

            reuniao.adicionarTranscricao(transcricao);

            AnaliseIA analise = new AnaliseIA(reuniao);

            analise.analisar();

            for (ProdutoTOTVS produto : produtoDAO.listarTodos()) {
                analise.mapearProduto(produto);
            }

            // Grava a análise e marca a reunião como PROCESSADA
            // na mesma transação.
            analiseDAO.inserir(analise);

            reuniao.adicionarAnalise(analise);

            return analise;

        } catch (SQLException e) {
            throw TradutorSqlException.traduzir(e, null);
        }
    }

    public AnaliseIA buscarPorId(Long id) {

        try {
            AnaliseIA analise = analiseDAO.buscarPorId(id);

            if (analise == null) {
                throw new RecursoNaoEncontradoException(
                        "Análise não encontrada."
                );
            }

            return analise;

        } catch (SQLException e) {
            throw TradutorSqlException.traduzir(e, null);
        }
    }

    public List<AnaliseIA> listarTodos() {

        try {
            return analiseDAO.listarTodos();
        } catch (SQLException e) {
            throw TradutorSqlException.traduzir(e, null);
        }
    }

    public AnaliseIA atualizarDadosComerciais(
            Long id,
            String persona,
            BigDecimal budget,
            String concorrente
    ) {

        AnaliseIA analise = buscarPorId(id);

        analise.atualizarDadosComerciais(persona, budget, concorrente);

        try {
            analiseDAO.atualizarDadosComerciais(analise);
        } catch (SQLException e) {
            throw TradutorSqlException.traduzir(e, null);
        }

        return analise;
    }

    public Alerta resolverAlerta(Long analiseId, Long alertaId) {

        AnaliseIA analise = buscarPorId(analiseId);

        Alerta alerta = analise.getAlertas()
                .stream()
                .filter(a -> alertaId.equals(a.getId()))
                .findFirst()
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Alerta não encontrado nesta análise."
                ));

        alerta.resolver();

        try {
            analiseDAO.atualizarStatusAlerta(
                    alertaId,
                    alerta.getStatus()
            );
        } catch (SQLException e) {
            throw TradutorSqlException.traduzir(e, null);
        }

        return alerta;
    }

    public void excluir(Long id) {

        buscarPorId(id);

        try {
            analiseDAO.excluir(id);
        } catch (SQLException e) {
            throw TradutorSqlException.traduzir(e, null);
        }
    }
}

package br.com.fiap.auraai.dao;

import br.com.fiap.auraai.domain.model.AnaliseIA;
import br.com.fiap.auraai.domain.model.StatusAlerta;

import java.sql.SQLException;
import java.util.List;

public interface AnaliseIADAO {

    void inserir(AnaliseIA analise)
            throws SQLException;

    AnaliseIA buscarPorId(Long id)
            throws SQLException;

    List<AnaliseIA> listarTodos()
            throws SQLException;

    void atualizar(AnaliseIA analise)
            throws SQLException;

    void excluir(Long id)
            throws SQLException;

    void atualizarStatusAlerta(Long alertaId, StatusAlerta status)
            throws SQLException;

    /** Atualiza só persona, budget e concorrente, preservando alertas e produtos. */
    void atualizarDadosComerciais(AnaliseIA analise)
            throws SQLException;
}
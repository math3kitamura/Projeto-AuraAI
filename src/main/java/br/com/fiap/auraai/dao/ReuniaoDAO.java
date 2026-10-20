package br.com.fiap.auraai.dao;

import br.com.fiap.auraai.domain.model.Reuniao;
import br.com.fiap.auraai.domain.model.Transcricao;

import java.sql.SQLException;
import java.util.List;

public interface ReuniaoDAO {

    void inserir(Reuniao reuniao) throws SQLException;

    Reuniao buscarPorId(Long id) throws SQLException;

    List<Reuniao> listarTodos() throws SQLException;

    void atualizar(Reuniao reuniao) throws SQLException;

    void excluir(Long id) throws SQLException;

    void inserirTranscricao(Transcricao transcricao) throws SQLException;

    Transcricao buscarTranscricaoPorReuniao(Long reuniaoId)
            throws SQLException;

    void atualizarTranscricao(Transcricao transcricao) throws SQLException;

    void excluirTranscricao(Long reuniaoId) throws SQLException;
}

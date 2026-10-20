package br.com.fiap.auraai.client;

import br.com.fiap.auraai.domain.model.Reuniao;
import br.com.fiap.auraai.domain.model.Transcricao;
import br.com.fiap.auraai.dto.ReuniaoRequest;
import br.com.fiap.auraai.dto.ReuniaoResponse;
import br.com.fiap.auraai.dto.TranscricaoRequest;
import br.com.fiap.auraai.dto.TranscricaoResponse;

import java.time.LocalDateTime;
import java.util.List;

public class ReuniaoApiClient extends ApiClientBase {

    public Reuniao cadastrar(
            String titulo,
            LocalDateTime dataHora,
            int duracaoMin,
            String responsavel,
            Long clienteId
    ) {

        ReuniaoResponse resposta = post(
                "/reunioes",
                new ReuniaoRequest(
                        titulo,
                        dataHora,
                        duracaoMin,
                        responsavel,
                        clienteId,
                        null
                ),
                ReuniaoResponse.class
        );

        return Conversor.paraReuniao(resposta);
    }

    /** Retorna null se a reunião não existir. */
    public Reuniao buscarPorId(Long id) {

        ReuniaoResponse resposta = ouNulo(
                () -> get("/reunioes/" + id, ReuniaoResponse.class)
        );

        return resposta == null ? null : Conversor.paraReuniao(resposta);
    }

    public List<Reuniao> listarTodos() {

        return getLista("/reunioes", ReuniaoResponse.class)
                .stream()
                .map(Conversor::paraReuniao)
                .toList();
    }

    public void atualizar(Reuniao reuniao) {

        put(
                "/reunioes/" + reuniao.getId(),
                new ReuniaoRequest(
                        reuniao.getTitulo(),
                        reuniao.getDataHora(),
                        reuniao.getDuracaoMin(),
                        reuniao.getResponsavel(),
                        reuniao.getCliente().getId(),
                        reuniao.getStatus().name()
                ),
                ReuniaoResponse.class
        );
    }

    public void excluir(Long id) {
        delete("/reunioes/" + id);
    }

    public Transcricao cadastrarTranscricao(
            String texto,
            String idioma,
            Long reuniaoId
    ) {

        TranscricaoResponse resposta = post(
                "/reunioes/" + reuniaoId + "/transcricao",
                new TranscricaoRequest(texto, idioma),
                TranscricaoResponse.class
        );

        return Conversor.paraTranscricao(
                resposta,
                buscarPorId(reuniaoId)
        );
    }

    /** Retorna null se a reunião não tiver transcrição. */
    public Transcricao buscarTranscricao(Long reuniaoId) {

        TranscricaoResponse resposta = ouNulo(
                () -> get(
                        "/reunioes/" + reuniaoId + "/transcricao",
                        TranscricaoResponse.class
                )
        );

        if (resposta == null) {
            return null;
        }

        return Conversor.paraTranscricao(
                resposta,
                buscarPorId(reuniaoId)
        );
    }
}

package client;

import domain.model.Alerta;
import domain.model.AnaliseIA;
import domain.model.AnaliseProduto;
import domain.model.Cliente;
import domain.model.ProdutoTOTVS;
import domain.model.Reuniao;
import domain.model.Transcricao;
import dto.AlertaResponse;
import dto.AnaliseResponse;
import dto.ClienteResponse;
import dto.ProdutoIdentificadoResponse;
import dto.ProdutoResponse;
import dto.ReuniaoResponse;
import dto.TranscricaoResponse;

/**
 * Converte os DTOs recebidos da API nos objetos de domínio
 * usados pelas telas Swing.
 */
final class Conversor {

    private Conversor() {
    }

    static Cliente paraCliente(ClienteResponse dto) {

        return new Cliente(
                dto.id(),
                dto.nome(),
                dto.cnpj(),
                dto.segmento(),
                dto.email()
        );
    }

    static ProdutoTOTVS paraProduto(ProdutoResponse dto) {

        return new ProdutoTOTVS(
                dto.id(),
                dto.nome(),
                dto.categoria(),
                dto.versao(),
                dto.descricao()
        );
    }

    static Reuniao paraReuniao(ReuniaoResponse dto) {

        return new Reuniao(
                dto.id(),
                dto.titulo(),
                dto.dataHora(),
                dto.duracaoMin(),
                dto.responsavel(),
                dto.status(),
                paraCliente(dto.cliente())
        );
    }

    static Transcricao paraTranscricao(
            TranscricaoResponse dto,
            Reuniao reuniao
    ) {

        return new Transcricao(
                dto.id(),
                dto.textoBruto(),
                dto.idioma(),
                reuniao
        );
    }

    static AnaliseIA paraAnalise(AnaliseResponse dto) {

        AnaliseIA analise = new AnaliseIA(
                dto.id(),
                paraReuniao(dto.reuniao()),
                dto.sentimento(),
                dto.riscoChurn(),
                dto.oportunidade(),
                dto.persona(),
                dto.budget(),
                dto.concorrente()
        );

        for (AlertaResponse alerta : dto.alertas()) {

            analise.adicionarAlerta(
                    new Alerta(
                            alerta.id(),
                            alerta.tipo(),
                            alerta.descricao(),
                            alerta.prioridade(),
                            alerta.status()
                    )
            );
        }

        for (ProdutoIdentificadoResponse item : dto.produtosIdentificados()) {

            analise.adicionarProdutoIdentificado(
                    new AnaliseProduto(
                            paraProduto(item.produto()),
                            item.contexto(),
                            item.relevancia(),
                            item.tipoMencao()
                    )
            );
        }

        return analise;
    }
}

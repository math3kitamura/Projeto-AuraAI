package br.com.fiap.auraai.client;

import br.com.fiap.auraai.domain.model.Alerta;
import br.com.fiap.auraai.domain.model.AnaliseIA;
import br.com.fiap.auraai.domain.model.AnaliseProduto;
import br.com.fiap.auraai.domain.model.Cliente;
import br.com.fiap.auraai.domain.model.PrioridadeAlerta;
import br.com.fiap.auraai.domain.model.ProdutoTOTVS;
import br.com.fiap.auraai.domain.model.Reuniao;
import br.com.fiap.auraai.domain.model.Sentimento;
import br.com.fiap.auraai.domain.model.StatusAlerta;
import br.com.fiap.auraai.domain.model.StatusReuniao;
import br.com.fiap.auraai.domain.model.TipoAlerta;
import br.com.fiap.auraai.domain.model.TipoMencao;
import br.com.fiap.auraai.domain.model.Transcricao;
import br.com.fiap.auraai.dto.AlertaResponse;
import br.com.fiap.auraai.dto.AnaliseResponse;
import br.com.fiap.auraai.dto.ClienteResponse;
import br.com.fiap.auraai.dto.ProdutoIdentificadoResponse;
import br.com.fiap.auraai.dto.ProdutoResponse;
import br.com.fiap.auraai.dto.ReuniaoResponse;
import br.com.fiap.auraai.dto.TranscricaoResponse;

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
                StatusReuniao.deTexto(dto.status()),
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
                Sentimento.deTexto(dto.sentimento()),
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
                            TipoAlerta.deTexto(alerta.tipo()),
                            alerta.descricao(),
                            PrioridadeAlerta.deTexto(alerta.prioridade()),
                            StatusAlerta.deTexto(alerta.status())
                    )
            );
        }

        for (ProdutoIdentificadoResponse item : dto.produtosIdentificados()) {

            analise.adicionarProdutoIdentificado(
                    new AnaliseProduto(
                            paraProduto(item.produto()),
                            item.contexto(),
                            item.relevancia(),
                            TipoMencao.deTexto(item.tipoMencao())
                    )
            );
        }

        return analise;
    }
}

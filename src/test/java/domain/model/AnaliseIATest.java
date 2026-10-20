package domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AnaliseIATest {

    private Cliente cliente;

    @BeforeEach
    void criarCliente() {
        cliente = new Cliente(
                "Armor Solutions",
                "12.345.678/0001-90",
                "Desenvolvimento de Jogos",
                "contato@armorsolutions.com"
        );
    }

    private AnaliseIA analiseComTexto(String texto) {

        Reuniao reuniao = new Reuniao(
                "Reunião de teste",
                LocalDateTime.now(),
                60,
                "Matheus",
                cliente
        );

        reuniao.adicionarTranscricao(
                new Transcricao(texto, "pt-BR", reuniao)
        );

        return new AnaliseIA(reuniao);
    }

    private boolean possuiAlerta(AnaliseIA analise, String tipo) {

        return analise.getAlertas()
                .stream()
                .anyMatch(a -> a.getTipo().equalsIgnoreCase(tipo));
    }

    @Test
    @DisplayName("Cenário comercial positivo: sentimento, churn, oportunidade, concorrente e alertas")
    void cenarioPositivo() {

        AnaliseIA analise = analiseComTexto(
                "A Armor Solutions demonstrou interesse em contratar "
                        + "o Protheus para melhorar a gestão da empresa. "
                        + "A organização pretende expandir suas operações "
                        + "e também informou que está avaliando o SAP."
        );

        assertEquals("POSITIVO", analise.identificarSentimento());
        assertEquals(0.15, analise.calcularRiscoChurn(), 0.0001);
        assertEquals(0.90, analise.calcularOportunidade(), 0.0001);
        assertEquals("SAP", analise.identificarConcorrente());

        analise.analisar();

        assertTrue(possuiAlerta(analise, "UPSELL"));
        assertTrue(possuiAlerta(analise, "CONCORRENTE"));
    }

    @Test
    @DisplayName("Cenário de risco de churn gera alerta CHURN_RISK")
    void cenarioRiscoDeChurn() {

        AnaliseIA analise = analiseComTexto(
                "O cliente está insatisfeito com o serviço, "
                        + "relatou diversos problemas e informou "
                        + "que está pensando em cancelar o contrato."
        );

        assertEquals("NEGATIVO", analise.identificarSentimento());
        assertEquals(0.85, analise.calcularRiscoChurn(), 0.0001);
        assertEquals(0.25, analise.calcularOportunidade(), 0.0001);
        assertNull(analise.identificarConcorrente());

        analise.analisar();

        assertTrue(possuiAlerta(analise, "CHURN_RISK"));
    }

    @Test
    @DisplayName("Produto mencionado é identificado uma única vez")
    void produtoNaoDeveDuplicar() {

        AnaliseIA analise = analiseComTexto(
                "O cliente quer contratar o Protheus."
        );

        ProdutoTOTVS protheus = new ProdutoTOTVS(
                "Protheus",
                "ERP",
                "12.1",
                "Sistema integrado de gestão empresarial."
        );

        analise.mapearProduto(protheus);
        analise.mapearProduto(protheus);

        assertEquals(1, analise.getProdutosIdentificados().size());
    }

    @Test
    @DisplayName("Analisar sem transcrição é rejeitado")
    void analisarSemTranscricao() {

        Reuniao reuniao = new Reuniao(
                "Sem transcrição",
                LocalDateTime.now(),
                30,
                "João",
                cliente
        );

        AnaliseIA analise = new AnaliseIA(reuniao);

        assertThrows(IllegalStateException.class, analise::analisar);
    }
}

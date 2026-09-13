package test;

import domain.model.Cliente;
import domain.model.ProdutoTOTVS;
import domain.model.Reuniao;
import domain.model.Transcricao;
import domain.service.AnaliseIA;

import java.time.LocalDateTime;

public class TesteAuraAI {

    private static int testesAprovados = 0;
    private static int testesReprovados = 0;

    public static void main(String[] args) {

        System.out.println("==========================================");
        System.out.println("TESTES DAS REGRAS DE NEGÓCIO - AURA AI");
        System.out.println("==========================================");

        testarCenarioPositivo();

        testarCenarioNegativo();

        exibirResultadoFinal();
    }

    private static void testarCenarioPositivo() {

        System.out.println(
                "\n--- TESTE 1: CENÁRIO COMERCIAL POSITIVO ---"
        );

        Cliente cliente = new Cliente(
                "Armor Solutions",
                "12.345.678/0001-90",
                "Desenvolvimento de Jogos",
                "contato@armorsolutions.com"
        );

        Reuniao reuniao = new Reuniao(
                "Projeto TOTVS - Armor Solutions",
                LocalDateTime.now(),
                60,
                "Matheus",
                cliente
        );

        Transcricao transcricao = new Transcricao(
                "A Armor Solutions demonstrou interesse em contratar "
                        + "o Protheus para melhorar a gestão da empresa. "
                        + "A organização pretende expandir suas operações "
                        + "e também informou que está avaliando o SAP.",
                "pt-BR",
                reuniao
        );

        reuniao.adicionarTranscricao(
                transcricao
        );

        AnaliseIA analise =
                new AnaliseIA(
                        reuniao
                );


        String sentimento =
                analise.identificarSentimento();

        validarTexto(
                "Identificação de sentimento",
                "POSITIVO",
                sentimento
        );


        double riscoChurn =
                analise.calcularRiscoChurn();

        validarNumero(
                "Cálculo do risco de churn",
                0.15,
                riscoChurn
        );

        double oportunidade =
                analise.calcularOportunidade();

        validarNumero(
                "Cálculo da oportunidade comercial",
                0.90,
                oportunidade
        );

        String concorrente =
                analise.identificarConcorrente();

        validarTexto(
                "Identificação de concorrente",
                "SAP",
                concorrente
        );

        analise.analisar();

        validarBooleano(
                "Geração do alerta UPSELL",
                possuiAlerta(
                        analise,
                        "UPSELL"
                )
        );

        validarBooleano(
                "Geração do alerta CONCORRENTE",
                possuiAlerta(
                        analise,
                        "CONCORRENTE"
                )
        );

        ProdutoTOTVS protheus =
                new ProdutoTOTVS(
                        "Protheus",
                        "ERP",
                        "12.1",
                        "Sistema integrado de gestão empresarial."
                );

        analise.mapearProduto(
                protheus
        );

        validarNumeroInteiro(
                "Identificação do produto Protheus",
                1,
                analise
                        .getProdutosIdentificados()
                        .size()
        );


        // Chamamos novamente para verificar
        // se o sistema evita produto duplicado.

        analise.mapearProduto(
                protheus
        );

        validarNumeroInteiro(
                "Produto não deve ser duplicado",
                1,
                analise
                        .getProdutosIdentificados()
                        .size()
        );
    }

    private static void testarCenarioNegativo() {

        System.out.println(
                "\n--- TESTE 2: CENÁRIO DE RISCO DE CHURN ---"
        );

        Cliente cliente = new Cliente(
                "Empresa Cliente Teste",
                "98.765.432/0001-10",
                "Tecnologia",
                "cliente@teste.com"
        );

        Reuniao reuniao = new Reuniao(
                "Reunião de acompanhamento",
                LocalDateTime.now(),
                45,
                "João",
                cliente
        );

        Transcricao transcricao = new Transcricao(
                "O cliente está insatisfeito com o serviço, "
                        + "relatou diversos problemas e informou "
                        + "que está pensando em cancelar o contrato.",
                "pt-BR",
                reuniao
        );

        reuniao.adicionarTranscricao(
                transcricao
        );

        AnaliseIA analise =
                new AnaliseIA(
                        reuniao
                );


        // Sentimento

        validarTexto(
                "Sentimento negativo",
                "NEGATIVO",
                analise.identificarSentimento()
        );


        // Churn

        validarNumero(
                "Alto risco de churn",
                0.85,
                analise.calcularRiscoChurn()
        );


        // Oportunidade

        validarNumero(
                "Baixa oportunidade comercial",
                0.25,
                analise.calcularOportunidade()
        );


        // Concorrente

        validarTexto(
                "Nenhum concorrente identificado",
                null,
                analise.identificarConcorrente()
        );


        analise.analisar();


        // Alerta esperado

        validarBooleano(
                "Geração do alerta CHURN_RISK",
                possuiAlerta(
                        analise,
                        "CHURN_RISK"
                )
        );
    }

    private static boolean possuiAlerta(
            AnaliseIA analise,
            String tipo
    ) {

        return analise
                .getAlertas()
                .stream()
                .anyMatch(
                        alerta ->
                                alerta
                                        .getTipo()
                                        .equalsIgnoreCase(
                                                tipo
                                        )
                );
    }

    private static void validarTexto(
            String nomeTeste,
            String esperado,
            String obtido
    ) {

        boolean aprovado;

        if (esperado == null) {

            aprovado =
                    obtido == null;

        } else {

            aprovado =
                    esperado.equals(
                            obtido
                    );
        }

        registrarResultado(
                nomeTeste,
                aprovado,
                String.valueOf(esperado),
                String.valueOf(obtido)
        );
    }


    private static void validarNumero(
            String nomeTeste,
            double esperado,
            double obtido
    ) {

        boolean aprovado =
                Math.abs(
                        esperado - obtido
                ) < 0.0001;

        registrarResultado(
                nomeTeste,
                aprovado,
                String.valueOf(esperado),
                String.valueOf(obtido)
        );
    }


    private static void validarNumeroInteiro(
            String nomeTeste,
            int esperado,
            int obtido
    ) {

        registrarResultado(
                nomeTeste,
                esperado == obtido,
                String.valueOf(esperado),
                String.valueOf(obtido)
        );
    }

    private static void validarBooleano(
            String nomeTeste,
            boolean resultado
    ) {

        registrarResultado(
                nomeTeste,
                resultado,
                "true",
                String.valueOf(resultado)
        );
    }


    private static void registrarResultado(
            String nomeTeste,
            boolean aprovado,
            String esperado,
            String obtido
    ) {

        if (aprovado) {

            testesAprovados++;

            System.out.println(
                    "[APROVADO] "
                            + nomeTeste
            );

        } else {

            testesReprovados++;

            System.out.println(
                    "[REPROVADO] "
                            + nomeTeste
            );

            System.out.println(
                    "   Esperado: "
                            + esperado
            );

            System.out.println(
                    "   Obtido: "
                            + obtido
            );
        }
    }


    private static void exibirResultadoFinal() {

        System.out.println(
                "\n=========================================="
        );

        System.out.println(
                "RESULTADO DOS TESTES"
        );

        System.out.println(
                "=========================================="
        );

        System.out.println(
                "Testes aprovados: "
                        + testesAprovados
        );

        System.out.println(
                "Testes reprovados: "
                        + testesReprovados
        );


        if (testesReprovados == 0) {

            System.out.println(
                    "\nTODOS OS TESTES FORAM EXECUTADOS COM SUCESSO!"
            );

        } else {

            System.out.println(
                    "\nEXISTEM TESTES QUE PRECISAM SER REVISADOS."
            );
        }

        System.out.println(
                "=========================================="
        );
    }
}

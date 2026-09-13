package domain.service;

import domain.model.Alerta;
import domain.model.AnaliseProduto;
import domain.model.EntidadeBase;
import domain.model.ProdutoTOTVS;
import domain.model.Reuniao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AnaliseIA extends EntidadeBase {

    private String sentimento;
    private double riscoChurn;
    private double oportunidade;
    private String persona;
    private BigDecimal budget;
    private String concorrente;

    private Reuniao reuniao;

    private List<Alerta> alertas;
    private List<AnaliseProduto> produtosIdentificados;

    public AnaliseIA(Reuniao reuniao) {
        super();

        validarReuniao(reuniao);

        this.reuniao = reuniao;
        this.alertas = new ArrayList<>();
        this.produtosIdentificados = new ArrayList<>();
    }

    public AnaliseIA(Long id, Reuniao reuniao) {
        super(id);

        validarReuniao(reuniao);

        this.reuniao = reuniao;
        this.alertas = new ArrayList<>();
        this.produtosIdentificados = new ArrayList<>();
    }

    public AnaliseIA(
            Long id,
            Reuniao reuniao,
            String sentimento,
            double riscoChurn,
            double oportunidade,
            String persona,
            BigDecimal budget,
            String concorrente
    ) {
        super(id);

        validarReuniao(reuniao);

        this.reuniao = reuniao;
        this.sentimento = sentimento;
        this.riscoChurn = riscoChurn;
        this.oportunidade = oportunidade;
        this.persona = persona;
        this.budget = budget;
        this.concorrente = concorrente;

        this.alertas = new ArrayList<>();
        this.produtosIdentificados = new ArrayList<>();
    }

    private void validarReuniao(Reuniao reuniao) {

        if (reuniao == null) {
            throw new IllegalArgumentException(
                    "A análise precisa estar vinculada a uma reunião."
            );
        }
    }

    public void analisar() {

        if (reuniao.getTranscricao() == null) {
            throw new IllegalStateException(
                    "A reunião precisa ter uma transcrição antes da análise."
            );
        }

        alertas.clear();

        this.sentimento = identificarSentimento();
        this.riscoChurn = calcularRiscoChurn();
        this.oportunidade = calcularOportunidade();
        this.concorrente = identificarConcorrente();

        identificarPersona();
        estimarBudget();
        gerarAlertas();
    }

    public String identificarSentimento() {

        String texto = obterTexto();

        if (
                texto.contains("insatisfeito")
                        || texto.contains("problema")
                        || texto.contains("cancelar")
        ) {
            return "NEGATIVO";
        }

        if (
                texto.contains("interesse")
                        || texto.contains("contratar")
                        || texto.contains("expandir")
        ) {
            return "POSITIVO";
        }

        return "NEUTRO";
    }


    public double calcularRiscoChurn() {

        String texto = obterTexto();

        if (
                texto.contains("insatisfeito")
                        || texto.contains("problema")
                        || texto.contains("cancelar")
        ) {
            return 0.85;
        }

        if (
                texto.contains("interesse")
                        || texto.contains("contratar")
                        || texto.contains("expandir")
        ) {
            return 0.15;
        }

        return 0.30;
    }

    public double calcularOportunidade() {

        String texto = obterTexto();

        if (
                texto.contains("interesse")
                        || texto.contains("contratar")
                        || texto.contains("expandir")
        ) {
            return 0.90;
        }

        if (
                texto.contains("insatisfeito")
                        || texto.contains("problema")
                        || texto.contains("cancelar")
        ) {
            return 0.25;
        }

        return 0.40;
    }

    public String identificarConcorrente() {

        String texto = obterTexto();

        if (texto.contains("sap")) {
            return "SAP";
        }

        if (texto.contains("senior")) {
            return "Senior";
        }

        return null;
    }

    private void identificarPersona() {
        this.persona = "Decisor";
    }

    private void estimarBudget() {
        this.budget = new BigDecimal("50000.00");
    }

    private void gerarAlertas() {

        if (riscoChurn >= 0.80) {

            gerarAlerta(
                    "CHURN_RISK",
                    "Cliente demonstrou risco de cancelamento.",
                    "ALTA"
            );
        }

        if (oportunidade >= 0.80) {

            gerarAlerta(
                    "UPSELL",
                    "Cliente demonstrou oportunidade comercial.",
                    "ALTA"
            );
        }

        if (concorrente != null) {

            gerarAlerta(
                    "CONCORRENTE",
                    "Concorrente mencionado na reunião: "
                            + concorrente,
                    "MEDIA"
            );
        }
    }

    public void mapearProduto(ProdutoTOTVS produto) {

        if (produto == null) {
            throw new IllegalArgumentException(
                    "O produto não pode ser nulo."
            );
        }

        String texto = obterTexto();

        if (texto.contains(produto.getNome().toLowerCase())) {

            boolean jaIdentificado =
                    produtosIdentificados
                            .stream()
                            .anyMatch(
                                    p -> p.getProduto()
                                            .getNome()
                                            .equalsIgnoreCase(
                                                    produto.getNome()
                                            )
                            );

            if (!jaIdentificado) {

                AnaliseProduto analiseProduto =
                        new AnaliseProduto(
                                produto,
                                "Produto mencionado durante a reunião comercial.",
                                0.90,
                                "ATUAL"
                        );

                produtosIdentificados.add(analiseProduto);
            }
        }
    }

    private void gerarAlerta(
            String tipo,
            String descricao,
            String prioridade
    ) {

        Alerta alerta = new Alerta(
                tipo,
                descricao,
                prioridade
        );

        adicionarAlerta(alerta);
    }

    private String obterTexto() {

        return reuniao
                .getTranscricao()
                .getTextoBruto()
                .toLowerCase();
    }

    public void adicionarAlerta(Alerta alerta) {

        if (alerta == null) {
            throw new IllegalArgumentException(
                    "O alerta não pode ser nulo."
            );
        }

        alertas.add(alerta);
    }

    public void adicionarProdutoIdentificado(
            AnaliseProduto analiseProduto
    ) {

        if (analiseProduto == null) {
            throw new IllegalArgumentException(
                    "A análise de produto não pode ser nula."
            );
        }

        produtosIdentificados.add(analiseProduto);
    }

    public String getSentimento() {
        return sentimento;
    }

    public double getRiscoChurn() {
        return riscoChurn;
    }

    public double getOportunidade() {
        return oportunidade;
    }

    public String getPersona() {
        return persona;
    }

    public BigDecimal getBudget() {
        return budget;
    }

    public String getConcorrente() {
        return concorrente;
    }

    public Reuniao getReuniao() {
        return reuniao;
    }

    public List<Alerta> getAlertas() {
        return alertas;
    }

    public List<AnaliseProduto> getProdutosIdentificados() {
        return produtosIdentificados;
    }

    @Override
    public void exibirResumo() {

        System.out.println("Sentimento: " + sentimento);
        System.out.println("Risco de Churn: " + riscoChurn);
        System.out.println("Oportunidade: " + oportunidade);
        System.out.println("Persona: " + persona);
        System.out.println("Budget estimado: R$ " + budget);

        if (concorrente != null) {
            System.out.println(
                    "Concorrente identificado: "
                            + concorrente
            );
        }
    }
}
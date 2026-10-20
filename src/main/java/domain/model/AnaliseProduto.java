package domain.model;

public class AnaliseProduto {

    private ProdutoTOTVS produto;
    private String contexto;
    private double relevancia;
    private String tipoMencao;

    public AnaliseProduto(
            ProdutoTOTVS produto,
            String contexto,
            double relevancia,
            String tipoMencao
    ) {

        setProduto(produto);
        setContexto(contexto);
        setRelevancia(relevancia);
        setTipoMencao(tipoMencao);
    }

    public ProdutoTOTVS getProduto() {
        return produto;
    }

    public void setProduto(ProdutoTOTVS produto) {

        if (produto == null) {
            throw new IllegalArgumentException(
                    "O produto da análise não pode ser nulo."
            );
        }

        this.produto = produto;
    }

    public String getContexto() {
        return contexto;
    }

    public void setContexto(String contexto) {

        if (contexto == null || contexto.isBlank()) {
            throw new IllegalArgumentException(
                    "O contexto da menção não pode ficar em branco."
            );
        }

        this.contexto = contexto.trim();
    }

    public double getRelevancia() {
        return relevancia;
    }

    public void setRelevancia(double relevancia) {

        if (relevancia < 0 || relevancia > 1) {
            throw new IllegalArgumentException(
                    "A relevância deve estar entre 0 e 1."
            );
        }

        this.relevancia = relevancia;
    }

    public String getTipoMencao() {
        return tipoMencao;
    }

    public void setTipoMencao(String tipoMencao) {

        if (tipoMencao == null || tipoMencao.isBlank()) {
            throw new IllegalArgumentException(
                    "O tipo de menção não pode ficar em branco."
            );
        }

        this.tipoMencao = tipoMencao
                .trim()
                .toUpperCase();
    }

    public void exibirResumo() {

        System.out.println(
                "Produto identificado: "
                        + produto.getNome()
        );

        System.out.println(
                "Tipo de menção: "
                        + tipoMencao
        );

        System.out.println(
                "Relevância: "
                        + relevancia
        );

        System.out.println(
                "Contexto: "
                        + contexto
        );
    }
}
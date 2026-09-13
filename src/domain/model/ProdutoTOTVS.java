package domain.model;

public class ProdutoTOTVS extends EntidadeBase {

    private String nome;
    private String categoria;
    private String versao;
    private String descricao;

    public ProdutoTOTVS(
            String nome,
            String categoria,
            String versao,
            String descricao
    ) {
        super();

        setNome(nome);
        setCategoria(categoria);
        setVersao(versao);
        setDescricao(descricao);
    }
    public ProdutoTOTVS(
            Long id,
            String nome,
            String categoria,
            String versao,
            String descricao
    ) {
        super(id);

        setNome(nome);
        setCategoria(categoria);
        setVersao(versao);
        setDescricao(descricao);
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {

        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException(
                    "O nome do produto não pode ficar em branco."
            );
        }

        this.nome = nome.trim();
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {

        if (categoria == null || categoria.isBlank()) {
            throw new IllegalArgumentException(
                    "A categoria do produto não pode ficar em branco."
            );
        }

        this.categoria = categoria.trim();
    }

    public String getVersao() {
        return versao;
    }

    public void setVersao(String versao) {

        if (versao == null || versao.isBlank()) {
            throw new IllegalArgumentException(
                    "A versão do produto não pode ficar em branco."
            );
        }

        this.versao = versao.trim();
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {

        if (descricao == null || descricao.isBlank()) {
            throw new IllegalArgumentException(
                    "A descrição do produto não pode ficar em branco."
            );
        }

        this.descricao = descricao.trim();
    }

    @Override
    public void exibirResumo() {

        System.out.println("Produto TOTVS: " + nome);
        System.out.println("Categoria: " + categoria);
        System.out.println("Versão: " + versao);
        System.out.println("Descrição: " + descricao);
    }
}
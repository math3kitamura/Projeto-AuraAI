package domain.model;

public class Alerta extends EntidadeBase {

    private String tipo;
    private String descricao;
    private String prioridade;
    private String status;

    public Alerta(
            String tipo,
            String descricao,
            String prioridade
    ) {
        super();

        setTipo(tipo);
        setDescricao(descricao);
        setPrioridade(prioridade);

        this.status = "ABERTO";
    }

    public Alerta(
            Long id,
            String tipo,
            String descricao,
            String prioridade
    ) {
        super(id);

        setTipo(tipo);
        setDescricao(descricao);
        setPrioridade(prioridade);

        this.status = "ABERTO";
    }

    public Alerta(
            Long id,
            String tipo,
            String descricao,
            String prioridade,
            String status
    ) {
        super(id);

        setTipo(tipo);
        setDescricao(descricao);
        setPrioridade(prioridade);
        setStatus(status);
    }

    public void disparar() {
        System.out.println("ALERTA GERADO!");
        System.out.println("Tipo: " + tipo);
        System.out.println("Prioridade: " + prioridade);
        System.out.println("Descrição: " + descricao);
    }

    public void resolver() {
        this.status = "RESOLVIDO";
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {

        if (tipo == null || tipo.isBlank()) {
            throw new IllegalArgumentException(
                    "O tipo do alerta não pode ficar em branco."
            );
        }

        this.tipo = tipo.trim().toUpperCase();
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {

        if (descricao == null || descricao.isBlank()) {
            throw new IllegalArgumentException(
                    "A descrição do alerta não pode ficar em branco."
            );
        }

        this.descricao = descricao.trim();
    }

    public String getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(String prioridade) {

        if (prioridade == null || prioridade.isBlank()) {
            throw new IllegalArgumentException(
                    "A prioridade do alerta não pode ficar em branco."
            );
        }

        this.prioridade = prioridade.trim().toUpperCase();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {

        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException(
                    "O status do alerta não pode ficar em branco."
            );
        }

        this.status = status.trim().toUpperCase();
    }

    @Override
    public void exibirResumo() {

        System.out.println("Alerta: " + tipo);
        System.out.println("Prioridade: " + prioridade);
        System.out.println("Status: " + status);
        System.out.println("Descrição: " + descricao);
    }
}
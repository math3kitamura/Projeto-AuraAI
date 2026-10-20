package br.com.fiap.auraai.domain.model;

public class Alerta extends EntidadeBase {

    private TipoAlerta tipo;
    private String descricao;
    private PrioridadeAlerta prioridade;
    private StatusAlerta status;

    // Novo alerta: ainda sem ID, nasce ABERTO
    public Alerta(
            TipoAlerta tipo,
            String descricao,
            PrioridadeAlerta prioridade
    ) {
        super();

        setTipo(tipo);
        setDescricao(descricao);
        setPrioridade(prioridade);

        this.status = StatusAlerta.ABERTO;
    }

    public Alerta(
            Long id,
            TipoAlerta tipo,
            String descricao,
            PrioridadeAlerta prioridade
    ) {
        super(id);

        setTipo(tipo);
        setDescricao(descricao);
        setPrioridade(prioridade);

        this.status = StatusAlerta.ABERTO;
    }

    // Alerta que já existe no banco
    public Alerta(
            Long id,
            TipoAlerta tipo,
            String descricao,
            PrioridadeAlerta prioridade,
            StatusAlerta status
    ) {
        super(id);

        setTipo(tipo);
        setDescricao(descricao);
        setPrioridade(prioridade);
        setStatus(status);
    }

    public void resolver() {
        this.status = StatusAlerta.RESOLVIDO;
    }

    public TipoAlerta getTipo() {
        return tipo;
    }

    public void setTipo(TipoAlerta tipo) {

        if (tipo == null) {
            throw new IllegalArgumentException(
                    "O tipo do alerta não pode ficar em branco."
            );
        }

        this.tipo = tipo;
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

    public PrioridadeAlerta getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(PrioridadeAlerta prioridade) {

        if (prioridade == null) {
            throw new IllegalArgumentException(
                    "A prioridade do alerta não pode ficar em branco."
            );
        }

        this.prioridade = prioridade;
    }

    public StatusAlerta getStatus() {
        return status;
    }

    public void setStatus(StatusAlerta status) {

        if (status == null) {
            throw new IllegalArgumentException(
                    "O status do alerta não pode ficar em branco."
            );
        }

        this.status = status;
    }

    @Override
    public void exibirResumo() {

        System.out.println("Alerta: " + tipo);
        System.out.println("Prioridade: " + prioridade);
        System.out.println("Status: " + status);
        System.out.println("Descrição: " + descricao);
    }
}

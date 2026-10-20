package br.com.fiap.auraai.domain.model;

public abstract class EntidadeBase {

    private Long id;

    public EntidadeBase() {
    }

    public EntidadeBase(Long id) {
        setId(id);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    "O ID deve ser maior que zero."
            );
        }

        this.id = id;
    }

    public abstract void exibirResumo();
}
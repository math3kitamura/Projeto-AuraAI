package br.com.fiap.auraai.domain.model;

import java.util.ArrayList;
import java.util.List;

public class Cliente extends EntidadeBase {

    private String nome;
    private String cnpj;
    private String segmento;
    private String email;

    private List<Reuniao> reunioes;

    // Usado para cadastrar um NOVO cliente
    public Cliente(
            String nome,
            String cnpj,
            String segmento,
            String email
    ) {
        super();

        setNome(nome);
        setCnpj(cnpj);
        setSegmento(segmento);
        setEmail(email);

        this.reunioes = new ArrayList<>();
    }

    // Usado quando o cliente JÁ EXISTE no banco
    public Cliente(
            Long id,
            String nome,
            String cnpj,
            String segmento,
            String email
    ) {
        super(id);

        setNome(nome);
        setCnpj(cnpj);
        setSegmento(segmento);
        setEmail(email);

        this.reunioes = new ArrayList<>();
    }

    public void adicionarReuniao(Reuniao reuniao) {

        if (reuniao == null) {
            throw new IllegalArgumentException(
                    "A reunião não pode ser nula."
            );
        }

        reunioes.add(reuniao);
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {

        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException(
                    "O nome do cliente não pode ficar em branco."
            );
        }

        this.nome = nome.trim();
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {

        if (cnpj == null || cnpj.isBlank()) {
            throw new IllegalArgumentException(
                    "O CNPJ não pode ficar em branco."
            );
        }

        this.cnpj = cnpj.trim();
    }

    public String getSegmento() {
        return segmento;
    }

    public void setSegmento(String segmento) {

        if (segmento == null || segmento.isBlank()) {
            throw new IllegalArgumentException(
                    "O segmento não pode ficar em branco."
            );
        }

        this.segmento = segmento.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException(
                    "O e-mail não pode ficar em branco."
            );
        }

        this.email = email.trim();
    }

    public List<Reuniao> getReunioes() {
        return reunioes;
    }

    @Override
    public void exibirResumo() {

        System.out.println("Cliente: " + nome);
        System.out.println("CNPJ: " + cnpj);
        System.out.println("Segmento: " + segmento);
        System.out.println("E-mail: " + email);
    }
}
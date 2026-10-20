package br.com.fiap.auraai.domain.model;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Reuniao extends EntidadeBase {

    private String titulo;
    private LocalDateTime dataHora;
    private int duracaoMin;
    private String responsavel;
    private StatusReuniao status;
    private Cliente cliente;
    private Transcricao transcricao;
    private List<AnaliseIA> analises;

    public Reuniao(
            String titulo,
            LocalDateTime dataHora,
            int duracaoMin,
            String responsavel,
            Cliente cliente
    ) {
        super();

        setTitulo(titulo);
        setDataHora(dataHora);
        setDuracaoMin(duracaoMin);
        setResponsavel(responsavel);
        setCliente(cliente);

        this.status = StatusReuniao.PENDENTE;
        this.analises = new ArrayList<>();
    }


    public Reuniao(
            Long id,
            String titulo,
            LocalDateTime dataHora,
            int duracaoMin,
            String responsavel,
            Cliente cliente
    ) {
        super(id);

        setTitulo(titulo);
        setDataHora(dataHora);
        setDuracaoMin(duracaoMin);
        setResponsavel(responsavel);
        setCliente(cliente);

        this.status = StatusReuniao.PENDENTE;
        this.analises = new ArrayList<>();
    }

    public Reuniao(
            Long id,
            String titulo,
            LocalDateTime dataHora,
            int duracaoMin,
            String responsavel,
            StatusReuniao status,
            Cliente cliente
    ) {
        super(id);

        setTitulo(titulo);
        setDataHora(dataHora);
        setDuracaoMin(duracaoMin);
        setResponsavel(responsavel);
        setStatus(status);
        setCliente(cliente);

        this.analises = new ArrayList<>();
    }

    public void adicionarTranscricao(Transcricao transcricao) {

        if (transcricao == null) {
            throw new IllegalArgumentException(
                    "A transcrição não pode ser nula."
            );
        }

        this.transcricao = transcricao;
    }

    public void adicionarAnalise(AnaliseIA analise) {

        if (analise == null) {
            throw new IllegalArgumentException(
                    "A análise não pode ser nula."
            );
        }

        analises.add(analise);

        this.status = StatusReuniao.PROCESSADA;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {

        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException(
                    "O título da reunião não pode ficar em branco."
            );
        }

        this.titulo = titulo.trim();
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {

        if (dataHora == null) {
            throw new IllegalArgumentException(
                    "A data e hora da reunião são obrigatórias."
            );
        }

        this.dataHora = dataHora;
    }

    public int getDuracaoMin() {
        return duracaoMin;
    }

    public void setDuracaoMin(int duracaoMin) {

        if (duracaoMin <= 0) {
            throw new IllegalArgumentException(
                    "A duração da reunião deve ser maior que zero."
            );
        }

        this.duracaoMin = duracaoMin;
    }

    public String getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(String responsavel) {

        if (responsavel == null || responsavel.isBlank()) {
            throw new IllegalArgumentException(
                    "O responsável pela reunião não pode ficar em branco."
            );
        }

        this.responsavel = responsavel.trim();
    }

    public StatusReuniao getStatus() {
        return status;
    }

    public void setStatus(StatusReuniao status) {

        if (status == null) {
            throw new IllegalArgumentException(
                    "O status da reunião não pode ficar em branco."
            );
        }

        this.status = status;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {

        if (cliente == null) {
            throw new IllegalArgumentException(
                    "A reunião precisa estar vinculada a um cliente."
            );
        }

        this.cliente = cliente;
    }

    public Transcricao getTranscricao() {
        return transcricao;
    }

    public List<AnaliseIA> getAnalises() {
        return analises;
    }

    @Override
    public void exibirResumo() {

        System.out.println("Reunião: " + titulo);
        System.out.println("Cliente: " + cliente.getNome());
        System.out.println("Responsável: " + responsavel);
        System.out.println("Data/Hora: " + dataHora);
        System.out.println("Duração: " + duracaoMin + " minutos");
        System.out.println("Status: " + status);
    }
}
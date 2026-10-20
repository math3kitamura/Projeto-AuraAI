package br.com.fiap.auraai.view;

import br.com.fiap.auraai.client.AnaliseApiClient;
import br.com.fiap.auraai.client.ReuniaoApiClient;

import br.com.fiap.auraai.domain.model.Alerta;
import br.com.fiap.auraai.domain.model.AnaliseProduto;
import br.com.fiap.auraai.domain.model.Reuniao;

import br.com.fiap.auraai.domain.model.AnaliseIA;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import br.com.fiap.auraai.client.ApiException;
import java.util.List;

public class TelaAnalise extends JFrame {

    private final AnaliseApiClient analiseApi;
    private final ReuniaoApiClient reuniaoApi;

    private JComboBox<Reuniao> cmbReuniao;

    private JButton btnAnalisar;
    private JButton btnAtualizarLista;
    private JButton btnExcluirAnalise;
    private JButton btnLimpar;

    private JTextField txtSentimento;
    private JTextField txtRiscoChurn;
    private JTextField txtOportunidade;
    private JTextField txtPersona;
    private JTextField txtBudget;
    private JTextField txtConcorrente;

    private JTable tabelaAnalises;
    private JTable tabelaAlertas;
    private JTable tabelaProdutos;

    private DefaultTableModel modeloAnalises;
    private DefaultTableModel modeloAlertas;
    private DefaultTableModel modeloProdutos;

    private Long analiseSelecionadaId;


    public TelaAnalise() {

        analiseApi =
                new AnaliseApiClient();

        reuniaoApi =
                new ReuniaoApiClient();

        configurarJanela();

        criarComponentes();

        configurarEventos();

        carregarReunioes();

        carregarAnalises();
    }

    private void configurarJanela() {

        setTitle(
                "Aura AI - Análise Inteligente"
        );

        setSize(
                1100,
                750
        );

        setDefaultCloseOperation(
                JFrame.DISPOSE_ON_CLOSE
        );

        setLocationRelativeTo(
                null
        );

        setResizable(
                false
        );
    }


    private void criarComponentes() {

        JPanel painelPrincipal =
                new JPanel(
                        new BorderLayout(
                                10,
                                10
                        )
                );

        painelPrincipal.setBorder(
                BorderFactory.createEmptyBorder(
                        15,
                        15,
                        15,
                        15
                )
        );


        JLabel titulo =
                new JLabel(
                        "Aura AI - Análise de Reuniões",
                        SwingConstants.CENTER
                );

        titulo.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        24
                )
        );

        painelPrincipal.add(
                titulo,
                BorderLayout.NORTH
        );


        JPanel painelSelecao =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT
                        )
                );

        painelSelecao.setBorder(
                BorderFactory.createTitledBorder(
                        "Executar Nova Análise"
                )
        );


        cmbReuniao =
                new JComboBox<>();


        cmbReuniao.setPreferredSize(
                new Dimension(
                        600,
                        30
                )
        );

        cmbReuniao.setRenderer(

                new DefaultListCellRenderer() {

                    @Override
                    public Component getListCellRendererComponent(
                            JList<?> list,
                            Object value,
                            int index,
                            boolean isSelected,
                            boolean cellHasFocus
                    ) {

                        super.getListCellRendererComponent(
                                list,
                                value,
                                index,
                                isSelected,
                                cellHasFocus
                        );

                        if (value instanceof Reuniao) {

                            Reuniao reuniao =
                                    (Reuniao) value;

                            setText(
                                    reuniao.getId()
                                            + " - "
                                            + reuniao.getTitulo()
                                            + " | "
                                            + reuniao
                                            .getCliente()
                                            .getNome()
                                            + " | "
                                            + reuniao.getStatus()
                            );
                        }

                        return this;
                    }
                }
        );


        btnAnalisar =
                new JButton(
                        "Analisar Reunião"
                );


        btnAtualizarLista =
                new JButton(
                        "Atualizar"
                );


        painelSelecao.add(
                new JLabel(
                        "Reunião:"
                )
        );

        painelSelecao.add(
                cmbReuniao
        );

        painelSelecao.add(
                btnAnalisar
        );

        painelSelecao.add(
                btnAtualizarLista
        );


        JPanel painelResultado =
                new JPanel(
                        new GridLayout(
                                3,
                                4,
                                10,
                                10
                        )
                );

        painelResultado.setBorder(
                BorderFactory.createTitledBorder(
                        "Resultado da Aura AI"
                )
        );


        txtSentimento =
                criarCampoSomenteLeitura();

        txtRiscoChurn =
                criarCampoSomenteLeitura();

        txtOportunidade =
                criarCampoSomenteLeitura();

        txtPersona =
                criarCampoSomenteLeitura();

        txtBudget =
                criarCampoSomenteLeitura();

        txtConcorrente =
                criarCampoSomenteLeitura();


        painelResultado.add(
                new JLabel(
                        "Sentimento:"
                )
        );

        painelResultado.add(
                txtSentimento
        );


        painelResultado.add(
                new JLabel(
                        "Risco de Churn:"
                )
        );

        painelResultado.add(
                txtRiscoChurn
        );


        painelResultado.add(
                new JLabel(
                        "Oportunidade:"
                )
        );

        painelResultado.add(
                txtOportunidade
        );


        painelResultado.add(
                new JLabel(
                        "Persona:"
                )
        );

        painelResultado.add(
                txtPersona
        );


        painelResultado.add(
                new JLabel(
                        "Budget:"
                )
        );

        painelResultado.add(
                txtBudget
        );


        painelResultado.add(
                new JLabel(
                        "Concorrente:"
                )
        );

        painelResultado.add(
                txtConcorrente
        );


        modeloAlertas =
                new DefaultTableModel(

                        new Object[]{
                                "ID",
                                "Tipo",
                                "Prioridade",
                                "Status",
                                "Descrição"
                        },

                        0
                ) {

                    @Override
                    public boolean isCellEditable(
                            int row,
                            int column
                    ) {

                        return false;
                    }
                };


        tabelaAlertas =
                new JTable(
                        modeloAlertas
                );


        JScrollPane scrollAlertas =
                new JScrollPane(
                        tabelaAlertas
                );


        modeloProdutos =
                new DefaultTableModel(

                        new Object[]{
                                "ID",
                                "Produto",
                                "Categoria",
                                "Relevância",
                                "Tipo de Menção",
                                "Contexto"
                        },

                        0
                ) {

                    @Override
                    public boolean isCellEditable(
                            int row,
                            int column
                    ) {

                        return false;
                    }
                };


        tabelaProdutos =
                new JTable(
                        modeloProdutos
                );


        JScrollPane scrollProdutos =
                new JScrollPane(
                        tabelaProdutos
                );


        JTabbedPane abasResultado =
                new JTabbedPane();


        abasResultado.addTab(
                "Alertas",
                scrollAlertas
        );


        abasResultado.addTab(
                "Produtos Identificados",
                scrollProdutos
        );


        JPanel painelAnalise =
                new JPanel(
                        new BorderLayout(
                                10,
                                10
                        )
                );


        painelAnalise.add(
                painelResultado,
                BorderLayout.NORTH
        );


        painelAnalise.add(
                abasResultado,
                BorderLayout.CENTER
        );


        modeloAnalises =
                new DefaultTableModel(

                        new Object[]{
                                "ID",
                                "Reunião",
                                "Cliente",
                                "Sentimento",
                                "Risco Churn",
                                "Oportunidade",
                                "Concorrente"
                        },

                        0
                ) {

                    @Override
                    public boolean isCellEditable(
                            int row,
                            int column
                    ) {

                        return false;
                    }
                };


        tabelaAnalises =
                new JTable(
                        modeloAnalises
                );


        tabelaAnalises.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );


        JScrollPane scrollAnalises =
                new JScrollPane(
                        tabelaAnalises
                );


        scrollAnalises.setBorder(
                BorderFactory.createTitledBorder(
                        "Histórico de Análises"
                )
        );


        JPanel painelBotoes =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT
                        )
                );


        btnExcluirAnalise =
                new JButton(
                        "Excluir Análise"
                );


        btnLimpar =
                new JButton(
                        "Limpar"
                );


        painelBotoes.add(
                btnLimpar
        );

        painelBotoes.add(
                btnExcluirAnalise
        );


        JPanel painelCentro =
                new JPanel(
                        new GridLayout(
                                2,
                                1,
                                10,
                                10
                        )
                );


        painelCentro.add(
                painelAnalise
        );


        painelCentro.add(
                scrollAnalises
        );


        JPanel painelConteudo =
                new JPanel(
                        new BorderLayout(
                                10,
                                10
                        )
                );


        painelConteudo.add(
                painelSelecao,
                BorderLayout.NORTH
        );


        painelConteudo.add(
                painelCentro,
                BorderLayout.CENTER
        );


        painelConteudo.add(
                painelBotoes,
                BorderLayout.SOUTH
        );


        painelPrincipal.add(
                painelConteudo,
                BorderLayout.CENTER
        );


        add(
                painelPrincipal
        );
    }


    private void configurarEventos() {

        btnAnalisar.addActionListener(
                e -> analisarReuniao()
        );


        btnAtualizarLista.addActionListener(

                e -> {

                    carregarReunioes();

                    carregarAnalises();
                }
        );


        btnExcluirAnalise.addActionListener(
                e -> excluirAnalise()
        );


        btnLimpar.addActionListener(
                e -> limparResultado()
        );


        tabelaAnalises
                .getSelectionModel()
                .addListSelectionListener(

                        e -> {

                            if (!e.getValueIsAdjusting()) {

                                selecionarAnalise();
                            }
                        }
                );
    }


    private void analisarReuniao() {

        Reuniao reuniao =
                (Reuniao)
                        cmbReuniao
                                .getSelectedItem();


        if (reuniao == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Selecione uma reunião.",
                    "Atenção",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }


        try {

            AnaliseIA analise =
                    analiseApi
                            .analisarReuniao(
                                    reuniao.getId()
                            );


            exibirAnalise(
                    analise
            );


            JOptionPane.showMessageDialog(
                    this,
                    "Análise concluída com sucesso!\n"
                            + "ID da análise: "
                            + analise.getId(),
                    "Aura AI",
                    JOptionPane.INFORMATION_MESSAGE
            );


            carregarAnalises();

            carregarReunioes();


        } catch (
                ApiException
                | IllegalArgumentException
                | IllegalStateException e
        ) {

            mostrarErro(
                    e.getMessage()
            );
        }
    }

    private void exibirAnalise(
            AnaliseIA analise
    ) {

        analiseSelecionadaId =
                analise.getId();


        txtSentimento.setText(
                valorOuTraco(
                        analise.getSentimento() == null
                                ? null
                                : analise.getSentimento().name()
                )
        );


        txtRiscoChurn.setText(
                formatarPercentual(
                        analise.getRiscoChurn()
                )
        );


        txtOportunidade.setText(
                formatarPercentual(
                        analise.getOportunidade()
                )
        );


        txtPersona.setText(
                valorOuTraco(
                        analise.getPersona()
                )
        );


        if (analise.getBudget() != null) {

            txtBudget.setText(
                    "R$ "
                            + analise
                            .getBudget()
                            .toPlainString()
            );

        } else {

            txtBudget.setText(
                    "-"
            );
        }


        txtConcorrente.setText(
                valorOuTraco(
                        analise.getConcorrente()
                )
        );


        carregarAlertas(
                analise
        );


        carregarProdutosIdentificados(
                analise
        );
    }

    private void carregarAlertas(
            AnaliseIA analise
    ) {

        modeloAlertas.setRowCount(
                0
        );


        for (
                Alerta alerta :
                analise.getAlertas()
        ) {

            modeloAlertas.addRow(

                    new Object[]{
                            alerta.getId(),
                            alerta.getTipo(),
                            alerta.getPrioridade(),
                            alerta.getStatus(),
                            alerta.getDescricao()
                    }
            );
        }
    }


    private void carregarProdutosIdentificados(
            AnaliseIA analise
    ) {

        modeloProdutos.setRowCount(
                0
        );


        for (
                AnaliseProduto analiseProduto :
                analise
                        .getProdutosIdentificados()
        ) {

            modeloProdutos.addRow(

                    new Object[]{
                            analiseProduto
                                    .getProduto()
                                    .getId(),

                            analiseProduto
                                    .getProduto()
                                    .getNome(),

                            analiseProduto
                                    .getProduto()
                                    .getCategoria(),

                            formatarPercentual(
                                    analiseProduto
                                            .getRelevancia()
                            ),

                            analiseProduto
                                    .getTipoMencao(),

                            analiseProduto
                                    .getContexto()
                    }
            );
        }
    }

    private void carregarAnalises() {

        try {

            List<AnaliseIA> analises =
                    analiseApi
                            .listarTodos();


            modeloAnalises.setRowCount(
                    0
            );


            for (
                    AnaliseIA analise :
                    analises
            ) {

                String nomeCliente =
                        "-";


                String tituloReuniao =
                        "-";


                if (
                        analise.getReuniao()
                                != null
                ) {

                    tituloReuniao =
                            analise
                                    .getReuniao()
                                    .getTitulo();


                    if (
                            analise
                                    .getReuniao()
                                    .getCliente()
                                    != null
                    ) {

                        nomeCliente =
                                analise
                                        .getReuniao()
                                        .getCliente()
                                        .getNome();
                    }
                }


                modeloAnalises.addRow(

                        new Object[]{
                                analise.getId(),

                                tituloReuniao,

                                nomeCliente,

                                analise.getSentimento(),

                                formatarPercentual(
                                        analise
                                                .getRiscoChurn()
                                ),

                                formatarPercentual(
                                        analise
                                                .getOportunidade()
                                ),

                                valorOuTraco(
                                        analise
                                                .getConcorrente()
                                )
                        }
                );
            }


        } catch (ApiException e) {

            mostrarErro(
                    "Erro ao carregar análises.\n"
                            + e.getMessage()
            );
        }
    }


    private void carregarReunioes() {

        try {

            List<Reuniao> reunioes =
                    reuniaoApi
                            .listarTodos();


            DefaultComboBoxModel<Reuniao> modelo =
                    new DefaultComboBoxModel<>();


            for (
                    Reuniao reuniao :
                    reunioes
            ) {

                modelo.addElement(
                        reuniao
                );
            }


            cmbReuniao.setModel(
                    modelo
            );


        } catch (ApiException e) {

            mostrarErro(
                    "Erro ao carregar reuniões.\n"
                            + e.getMessage()
            );
        }
    }

    private void selecionarAnalise() {

        int linha =
                tabelaAnalises
                        .getSelectedRow();


        if (linha == -1) {
            return;
        }


        Long id =
                ((Number)
                        modeloAnalises
                                .getValueAt(
                                        linha,
                                        0
                                ))
                        .longValue();


        try {

            AnaliseIA analise =
                    analiseApi
                            .buscarPorId(
                                    id
                            );


            if (analise != null) {

                exibirAnalise(
                        analise
                );
            }


        } catch (ApiException e) {

            mostrarErro(
                    "Erro ao carregar análise.\n"
                            + e.getMessage()
            );
        }
    }


    private void excluirAnalise() {

        if (analiseSelecionadaId == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Selecione uma análise no histórico.",
                    "Atenção",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }


        int resposta =
                JOptionPane.showConfirmDialog(
                        this,
                        "Deseja realmente excluir esta análise?",
                        "Confirmar exclusão",
                        JOptionPane.YES_NO_OPTION
                );


        if (
                resposta
                        != JOptionPane.YES_OPTION
        ) {

            return;
        }


        try {

            analiseApi.excluir(
                    analiseSelecionadaId
            );


            JOptionPane.showMessageDialog(
                    this,
                    "Análise excluída com sucesso!",
                    "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE
            );


            limparResultado();

            carregarAnalises();


        } catch (ApiException e) {

            mostrarErro(
                    "Erro ao excluir análise.\n"
                            + e.getMessage()
            );
        }
    }

    private void limparResultado() {

        analiseSelecionadaId =
                null;


        txtSentimento.setText(
                ""
        );

        txtRiscoChurn.setText(
                ""
        );

        txtOportunidade.setText(
                ""
        );

        txtPersona.setText(
                ""
        );

        txtBudget.setText(
                ""
        );

        txtConcorrente.setText(
                ""
        );


        modeloAlertas.setRowCount(
                0
        );

        modeloProdutos.setRowCount(
                0
        );


        tabelaAnalises
                .clearSelection();
    }


    private JTextField criarCampoSomenteLeitura() {

        JTextField campo =
                new JTextField();


        campo.setEditable(
                false
        );


        return campo;
    }


    private String formatarPercentual(
            double valor
    ) {

        return String.format(
                "%.0f%%",
                valor * 100
        );
    }


    private String valorOuTraco(
            String valor
    ) {

        if (
                valor == null
                        || valor.isBlank()
        ) {

            return "-";
        }

        return valor;
    }


    private void mostrarErro(
            String mensagem
    ) {

        JOptionPane.showMessageDialog(
                this,
                mensagem,
                "Erro",
                JOptionPane.ERROR_MESSAGE
        );
    }
}

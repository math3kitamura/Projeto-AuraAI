package view;

import client.ClienteApiClient;
import client.ReuniaoApiClient;

import domain.model.Cliente;
import domain.model.Reuniao;
import domain.model.Transcricao;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import client.ApiException;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import java.util.Date;
import java.util.List;

public class TelaReuniao extends JFrame {

    private JTextField txtTitulo;
    private JTextField txtResponsavel;

    private JSpinner spnDataHora;
    private JSpinner spnDuracao;

    private JComboBox<Cliente> cmbCliente;
    private JComboBox<String> cmbIdioma;

    private JTextArea txtTranscricao;

    private JButton btnCadastrar;
    private JButton btnAtualizar;
    private JButton btnExcluir;
    private JButton btnLimpar;
    private JButton btnSalvarTranscricao;

    private JTable tabelaReunioes;
    private DefaultTableModel modeloTabela;

    private final ReuniaoApiClient reuniaoApi;
    private final ClienteApiClient clienteApi;

    private List<Reuniao> reunioesCarregadas;

    private Reuniao reuniaoSelecionada;

    private final DateTimeFormatter formatadorData =
            DateTimeFormatter.ofPattern(
                    "dd/MM/yyyy HH:mm"
            );

    public TelaReuniao() {

        reuniaoApi =
                new ReuniaoApiClient();

        clienteApi =
                new ClienteApiClient();

        configurarJanela();
        criarComponentes();
        configurarEventos();

        carregarClientes();
        carregarReunioes();
    }

    private void configurarJanela() {

        setTitle(
                "Aura AI - Reuniões"
        );

        setSize(
                1000,
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

        // =====================================
        // TÍTULO
        // =====================================

        JLabel titulo =
                new JLabel(
                        "Gerenciamento de Reuniões",
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

        JPanel painelFormulario =
                new JPanel(
                        new GridBagLayout()
                );

        painelFormulario.setBorder(
                BorderFactory.createTitledBorder(
                        "Dados da Reunião"
                )
        );

        GridBagConstraints gbc =
                new GridBagConstraints();

        gbc.insets =
                new Insets(
                        5,
                        5,
                        5,
                        5
                );

        gbc.fill =
                GridBagConstraints.HORIZONTAL;

        cmbCliente =
                new JComboBox<>();

        cmbCliente.setRenderer(
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

                        if (value instanceof Cliente) {

                            Cliente cliente =
                                    (Cliente) value;

                            setText(
                                    cliente.getId()
                                            + " - "
                                            + cliente.getNome()
                            );
                        }

                        return this;
                    }
                }
        );

        txtTitulo =
                new JTextField(
                        25
                );


        txtResponsavel =
                new JTextField(
                        25
                );

        spnDataHora =
                new JSpinner(
                        new SpinnerDateModel()
                );

        JSpinner.DateEditor editorData =
                new JSpinner.DateEditor(
                        spnDataHora,
                        "dd/MM/yyyy HH:mm"
                );

        spnDataHora.setEditor(
                editorData
        );


        spnDuracao =
                new JSpinner(
                        new SpinnerNumberModel(
                                30,
                                1,
                                1440,
                                5
                        )
                );

        gbc.gridx = 0;
        gbc.gridy = 0;

        painelFormulario.add(
                new JLabel("Cliente:"),
                gbc
        );

        gbc.gridx = 1;

        painelFormulario.add(
                cmbCliente,
                gbc
        );

        gbc.gridx = 0;
        gbc.gridy = 1;

        painelFormulario.add(
                new JLabel("Título:"),
                gbc
        );

        gbc.gridx = 1;

        painelFormulario.add(
                txtTitulo,
                gbc
        );

        gbc.gridx = 0;
        gbc.gridy = 2;

        painelFormulario.add(
                new JLabel("Responsável:"),
                gbc
        );

        gbc.gridx = 1;

        painelFormulario.add(
                txtResponsavel,
                gbc
        );


        gbc.gridx = 0;
        gbc.gridy = 3;

        painelFormulario.add(
                new JLabel("Data/Hora:"),
                gbc
        );

        gbc.gridx = 1;

        painelFormulario.add(
                spnDataHora,
                gbc
        );


        gbc.gridx = 0;
        gbc.gridy = 4;

        painelFormulario.add(
                new JLabel("Duração (min):"),
                gbc
        );

        gbc.gridx = 1;

        painelFormulario.add(
                spnDuracao,
                gbc
        );


        JPanel painelBotoes =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.CENTER,
                                10,
                                5
                        )
                );

        btnCadastrar =
                new JButton(
                        "Cadastrar"
                );

        btnAtualizar =
                new JButton(
                        "Atualizar"
                );

        btnExcluir =
                new JButton(
                        "Excluir"
                );

        btnLimpar =
                new JButton(
                        "Limpar"
                );

        painelBotoes.add(
                btnCadastrar
        );

        painelBotoes.add(
                btnAtualizar
        );

        painelBotoes.add(
                btnExcluir
        );

        painelBotoes.add(
                btnLimpar
        );

        JPanel painelCadastro =
                new JPanel(
                        new BorderLayout(
                                5,
                                5
                        )
                );

        painelCadastro.add(
                painelFormulario,
                BorderLayout.CENTER
        );

        painelCadastro.add(
                painelBotoes,
                BorderLayout.SOUTH
        );


        modeloTabela =
                new DefaultTableModel(
                        new Object[]{
                                "ID",
                                "Título",
                                "Data/Hora",
                                "Duração",
                                "Responsável",
                                "Cliente",
                                "Status"
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

        tabelaReunioes =
                new JTable(
                        modeloTabela
                );

        tabelaReunioes.setSelectionMode(
                ListSelectionModel
                        .SINGLE_SELECTION
        );

        JScrollPane scrollTabela =
                new JScrollPane(
                        tabelaReunioes
                );

        scrollTabela.setBorder(
                BorderFactory.createTitledBorder(
                        "Reuniões Cadastradas"
                )
        );

        JPanel painelTranscricao =
                new JPanel(
                        new BorderLayout(
                                5,
                                5
                        )
                );

        painelTranscricao.setBorder(
                BorderFactory.createTitledBorder(
                        "Transcrição da Reunião"
                )
        );

        txtTranscricao =
                new JTextArea(
                        6,
                        50
                );

        txtTranscricao.setLineWrap(
                true
        );

        txtTranscricao.setWrapStyleWord(
                true
        );

        JScrollPane scrollTranscricao =
                new JScrollPane(
                        txtTranscricao
                );

        cmbIdioma =
                new JComboBox<>(
                        new String[]{
                                "pt-BR",
                                "en-US",
                                "es-ES"
                        }
                );

        btnSalvarTranscricao =
                new JButton(
                        "Salvar Transcrição"
                );

        btnSalvarTranscricao.setEnabled(
                false
        );

        JPanel painelTranscricaoOpcoes =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT
                        )
                );

        painelTranscricaoOpcoes.add(
                new JLabel("Idioma:")
        );

        painelTranscricaoOpcoes.add(
                cmbIdioma
        );

        painelTranscricaoOpcoes.add(
                btnSalvarTranscricao
        );

        painelTranscricao.add(
                scrollTranscricao,
                BorderLayout.CENTER
        );

        painelTranscricao.add(
                painelTranscricaoOpcoes,
                BorderLayout.SOUTH
        );


        JPanel painelCentro =
                new JPanel(
                        new BorderLayout(
                                10,
                                10
                        )
                );

        painelCentro.add(
                painelCadastro,
                BorderLayout.NORTH
        );

        painelCentro.add(
                scrollTabela,
                BorderLayout.CENTER
        );

        painelCentro.add(
                painelTranscricao,
                BorderLayout.SOUTH
        );

        painelPrincipal.add(
                painelCentro,
                BorderLayout.CENTER
        );

        add(
                painelPrincipal
        );
    }


    private void configurarEventos() {

        btnCadastrar.addActionListener(
                e -> cadastrarReuniao()
        );

        btnAtualizar.addActionListener(
                e -> atualizarReuniao()
        );

        btnExcluir.addActionListener(
                e -> excluirReuniao()
        );

        btnLimpar.addActionListener(
                e -> limparCampos()
        );

        btnSalvarTranscricao.addActionListener(
                e -> salvarTranscricao()
        );

        tabelaReunioes
                .getSelectionModel()
                .addListSelectionListener(
                        e -> {

                            if (!e.getValueIsAdjusting()) {

                                selecionarReuniao();
                            }
                        }
                );
    }


    private void cadastrarReuniao() {

        try {

            Cliente cliente =
                    (Cliente)
                            cmbCliente
                                    .getSelectedItem();

            if (cliente == null) {

                throw new IllegalArgumentException(
                        "Selecione um cliente."
                );
            }

            Reuniao reuniao =
                    reuniaoApi.cadastrar(
                            txtTitulo.getText(),
                            obterDataHora(),
                            (Integer)
                                    spnDuracao
                                            .getValue(),
                            txtResponsavel.getText(),
                            cliente.getId()
                    );

            JOptionPane.showMessageDialog(
                    this,
                    "Reunião cadastrada com sucesso!\n"
                            + "ID: "
                            + reuniao.getId(),
                    "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE
            );

            carregarReunioes();

            limparCampos();

        } catch (
                IllegalArgumentException
                | ApiException e
        ) {

            mostrarErro(
                    e.getMessage()
            );
        }
    }


    private void atualizarReuniao() {

        if (reuniaoSelecionada == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Selecione uma reunião na tabela.",
                    "Atenção",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        try {

            Cliente cliente =
                    (Cliente)
                            cmbCliente
                                    .getSelectedItem();

            if (cliente == null) {

                throw new IllegalArgumentException(
                        "Selecione um cliente."
                );
            }

            reuniaoSelecionada.setTitulo(
                    txtTitulo.getText()
            );

            reuniaoSelecionada.setResponsavel(
                    txtResponsavel.getText()
            );

            reuniaoSelecionada.setDataHora(
                    obterDataHora()
            );

            reuniaoSelecionada.setDuracaoMin(
                    (Integer)
                            spnDuracao
                                    .getValue()
            );

            reuniaoSelecionada.setCliente(
                    cliente
            );

            reuniaoApi.atualizar(
                    reuniaoSelecionada
            );

            JOptionPane.showMessageDialog(
                    this,
                    "Reunião atualizada com sucesso!",
                    "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE
            );

            carregarReunioes();

            limparCampos();

        } catch (
                IllegalArgumentException
                | ApiException e
        ) {

            mostrarErro(
                    e.getMessage()
            );
        }
    }

    private void excluirReuniao() {

        if (reuniaoSelecionada == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Selecione uma reunião na tabela.",
                    "Atenção",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        int resposta =
                JOptionPane.showConfirmDialog(
                        this,
                        "Deseja realmente excluir esta reunião?",
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

            reuniaoApi.excluir(
                    reuniaoSelecionada.getId()
            );

            JOptionPane.showMessageDialog(
                    this,
                    "Reunião excluída com sucesso!",
                    "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE
            );

            carregarReunioes();

            limparCampos();

        } catch (ApiException e) {

            mostrarErro(
                    "Não foi possível excluir a reunião.\n"
                            + e.getMessage()
            );
        }
    }

    private void salvarTranscricao() {

        if (reuniaoSelecionada == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Selecione uma reunião.",
                    "Atenção",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        try {

            if (
                    txtTranscricao
                            .getText()
                            .isBlank()
            ) {

                throw new IllegalArgumentException(
                        "Digite a transcrição da reunião."
                );
            }

            Transcricao existente =
                    reuniaoApi
                            .buscarTranscricao(
                                    reuniaoSelecionada
                                            .getId()
                            );

            if (existente != null) {

                JOptionPane.showMessageDialog(
                        this,
                        "Esta reunião já possui uma transcrição.",
                        "Atenção",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            String idioma =
                    (String)
                            cmbIdioma
                                    .getSelectedItem();

            Transcricao transcricao =
                    reuniaoApi
                            .cadastrarTranscricao(
                                    txtTranscricao
                                            .getText(),
                                    idioma,
                                    reuniaoSelecionada
                                            .getId()
                            );

            JOptionPane.showMessageDialog(
                    this,
                    "Transcrição salva com sucesso!\n"
                            + "ID: "
                            + transcricao.getId()
                            + "\nPalavras: "
                            + transcricao.getQtdPalavras(),
                    "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE
            );

            carregarTranscricao(
                    reuniaoSelecionada.getId()
            );

        } catch (
                IllegalArgumentException
                | ApiException e
        ) {

            mostrarErro(
                    e.getMessage()
            );
        }
    }

    private void carregarClientes() {

        try {

            List<Cliente> clientes =
                    clienteApi
                            .listarTodos();

            DefaultComboBoxModel<Cliente> modelo =
                    new DefaultComboBoxModel<>();

            for (Cliente cliente : clientes) {

                modelo.addElement(
                        cliente
                );
            }

            cmbCliente.setModel(
                    modelo
            );

        } catch (ApiException e) {

            mostrarErro(
                    "Erro ao carregar clientes.\n"
                            + e.getMessage()
            );
        }
    }

    private void carregarReunioes() {

        try {

            reunioesCarregadas =
                    reuniaoApi
                            .listarTodos();

            modeloTabela.setRowCount(
                    0
            );

            for (
                    Reuniao reuniao :
                    reunioesCarregadas
            ) {

                modeloTabela.addRow(
                        new Object[]{
                                reuniao.getId(),
                                reuniao.getTitulo(),
                                reuniao
                                        .getDataHora()
                                        .format(
                                        formatadorData
                                ),
                                reuniao.getDuracaoMin(),
                                reuniao.getResponsavel(),
                                reuniao
                                        .getCliente()
                                        .getNome(),
                                reuniao.getStatus()
                        }
                );
            }

        } catch (ApiException e) {

            mostrarErro(
                    "Erro ao carregar reuniões.\n"
                            + e.getMessage()
            );
        }
    }

    private void selecionarReuniao() {

        int linha =
                tabelaReunioes
                        .getSelectedRow();

        if (linha == -1) {
            return;
        }

        Long id =
                ((Number)
                        modeloTabela
                                .getValueAt(
                                        linha,
                                        0
                                ))
                        .longValue();

        reuniaoSelecionada =
                encontrarReuniaoPorId(
                        id
                );

        if (reuniaoSelecionada == null) {
            return;
        }

        txtTitulo.setText(
                reuniaoSelecionada
                        .getTitulo()
        );

        txtResponsavel.setText(
                reuniaoSelecionada
                        .getResponsavel()
        );

        spnDuracao.setValue(
                reuniaoSelecionada
                        .getDuracaoMin()
        );

        spnDataHora.setValue(
                converterParaDate(
                        reuniaoSelecionada
                                .getDataHora()
                )
        );

        selecionarClienteCombo(
                reuniaoSelecionada
                        .getCliente()
                        .getId()
        );

        carregarTranscricao(
                reuniaoSelecionada
                        .getId()
        );
    }

    private void carregarTranscricao(
            Long reuniaoId
    ) {

        try {

            Transcricao transcricao =
                    reuniaoApi
                            .buscarTranscricao(
                                    reuniaoId
                            );

            if (transcricao == null) {

                txtTranscricao.setText(
                        ""
                );

                txtTranscricao.setEditable(
                        true
                );

                btnSalvarTranscricao.setEnabled(
                        true
                );

                cmbIdioma.setEnabled(
                        true
                );

            } else {

                txtTranscricao.setText(
                        transcricao
                                .getTextoBruto()
                );

                cmbIdioma.setSelectedItem(
                        transcricao
                                .getIdioma()
                );

                txtTranscricao.setEditable(
                        false
                );

                btnSalvarTranscricao.setEnabled(
                        false
                );

                cmbIdioma.setEnabled(
                        false
                );
            }

        } catch (ApiException e) {

            mostrarErro(
                    "Erro ao carregar transcrição.\n"
                            + e.getMessage()
            );
        }
    }

    private Reuniao encontrarReuniaoPorId(
            Long id
    ) {

        if (reunioesCarregadas == null) {
            return null;
        }

        for (
                Reuniao reuniao :
                reunioesCarregadas
        ) {

            if (
                    reuniao
                            .getId()
                            .equals(id)
            ) {

                return reuniao;
            }
        }

        return null;
    }

    private void selecionarClienteCombo(
            Long clienteId
    ) {

        for (
                int i = 0;
                i < cmbCliente.getItemCount();
                i++
        ) {

            Cliente cliente =
                    cmbCliente.getItemAt(i);

            if (
                    cliente
                            .getId()
                            .equals(clienteId)
            ) {

                cmbCliente.setSelectedIndex(
                        i
                );

                break;
            }
        }
    }

    private LocalDateTime obterDataHora() {

        Date data =
                (Date)
                        spnDataHora
                                .getValue();

        return LocalDateTime.ofInstant(
                data.toInstant(),
                ZoneId.systemDefault()
        );
    }

    private Date converterParaDate(
            LocalDateTime dataHora
    ) {

        return Date.from(
                dataHora
                        .atZone(
                                ZoneId.systemDefault()
                        )
                        .toInstant()
        );
    }

    private void limparCampos() {

        txtTitulo.setText(
                ""
        );

        txtResponsavel.setText(
                ""
        );

        spnDuracao.setValue(
                30
        );

        spnDataHora.setValue(
                new Date()
        );

        txtTranscricao.setText(
                ""
        );

        txtTranscricao.setEditable(
                false
        );

        btnSalvarTranscricao.setEnabled(
                false
        );

        cmbIdioma.setEnabled(
                true
        );

        cmbIdioma.setSelectedItem(
                "pt-BR"
        );

        reuniaoSelecionada =
                null;

        tabelaReunioes
                .clearSelection();

        txtTitulo.requestFocus();
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
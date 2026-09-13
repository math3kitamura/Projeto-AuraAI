package view;

import controller.ClienteController;
import domain.model.Cliente;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class TelaCliente extends JFrame {

    private JTextField txtNome;
    private JTextField txtCnpj;
    private JTextField txtSegmento;
    private JTextField txtEmail;

    private JButton btnCadastrar;
    private JButton btnAtualizar;
    private JButton btnExcluir;
    private JButton btnLimpar;

    private JTable tabelaClientes;
    private DefaultTableModel modeloTabela;

    private final ClienteController clienteController;

    private Long clienteSelecionadoId;

    public TelaCliente() {

        clienteController =
                new ClienteController();

        configurarJanela();
        criarComponentes();
        configurarEventos();

        carregarClientes();
    }

    private void configurarJanela() {

        setTitle("Aura AI - Clientes");

        setSize(850, 600);

        setDefaultCloseOperation(
                JFrame.DISPOSE_ON_CLOSE
        );

        setLocationRelativeTo(null);

        setResizable(false);
    }

    private void criarComponentes() {

        JPanel painelPrincipal =
                new JPanel(
                        new BorderLayout(10, 10)
                );

        painelPrincipal.setBorder(
                BorderFactory.createEmptyBorder(
                        20,
                        20,
                        20,
                        20
                )
        );

        JLabel titulo =
                new JLabel(
                        "Gerenciamento de Clientes",
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
                        new GridLayout(
                                4,
                                2,
                                10,
                                10
                        )
                );

        painelFormulario.setBorder(
                BorderFactory.createTitledBorder(
                        "Dados do Cliente"
                )
        );

        txtNome =
                new JTextField();

        txtCnpj =
                new JTextField();

        txtSegmento =
                new JTextField();

        txtEmail =
                new JTextField();

        painelFormulario.add(
                new JLabel("Nome:")
        );

        painelFormulario.add(
                txtNome
        );

        painelFormulario.add(
                new JLabel("CNPJ:")
        );

        painelFormulario.add(
                txtCnpj
        );

        painelFormulario.add(
                new JLabel("Segmento:")
        );

        painelFormulario.add(
                txtSegmento
        );

        painelFormulario.add(
                new JLabel("E-mail:")
        );

        painelFormulario.add(
                txtEmail
        );

        JPanel painelBotoes =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.CENTER,
                                10,
                                10
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

        JPanel painelSuperior =
                new JPanel(
                        new BorderLayout(
                                10,
                                10
                        )
                );

        painelSuperior.add(
                painelFormulario,
                BorderLayout.CENTER
        );

        painelSuperior.add(
                painelBotoes,
                BorderLayout.SOUTH
        );

        modeloTabela =
                new DefaultTableModel(
                        new Object[]{
                                "ID",
                                "Nome",
                                "CNPJ",
                                "Segmento",
                                "E-mail"
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

        tabelaClientes =
                new JTable(
                        modeloTabela
                );

        tabelaClientes.setSelectionMode(
                ListSelectionModel
                        .SINGLE_SELECTION
        );

        JScrollPane scrollPane =
                new JScrollPane(
                        tabelaClientes
                );

        scrollPane.setBorder(
                BorderFactory.createTitledBorder(
                        "Clientes Cadastrados"
                )
        );

        JPanel painelCentro =
                new JPanel(
                        new BorderLayout(
                                10,
                                10
                        )
                );

        painelCentro.add(
                painelSuperior,
                BorderLayout.NORTH
        );

        painelCentro.add(
                scrollPane,
                BorderLayout.CENTER
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
                e -> cadastrarCliente()
        );

        btnAtualizar.addActionListener(
                e -> atualizarCliente()
        );

        btnExcluir.addActionListener(
                e -> excluirCliente()
        );

        btnLimpar.addActionListener(
                e -> limparCampos()
        );

        tabelaClientes
                .getSelectionModel()
                .addListSelectionListener(
                        e -> {

                            if (!e.getValueIsAdjusting()) {

                                selecionarCliente();
                            }
                        }
                );
    }

    private void cadastrarCliente() {

        try {

            String nome =
                    txtNome.getText();

            String cnpj =
                    txtCnpj.getText();

            String segmento =
                    txtSegmento.getText();

            String email =
                    txtEmail.getText();

            Cliente cliente =
                    clienteController.cadastrar(
                            nome,
                            cnpj,
                            segmento,
                            email
                    );

            JOptionPane.showMessageDialog(
                    this,
                    "Cliente cadastrado com sucesso!\n"
                            + "ID: "
                            + cliente.getId(),
                    "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE
            );

            carregarClientes();

            limparCampos();

        } catch (
                IllegalArgumentException
                | SQLException e
        ) {

            mostrarErro(
                    e.getMessage()
            );
        }
    }

    private void atualizarCliente() {

        if (clienteSelecionadoId == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Selecione um cliente na tabela.",
                    "Atenção",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        try {

            clienteController.atualizar(
                    clienteSelecionadoId,
                    txtNome.getText(),
                    txtCnpj.getText(),
                    txtSegmento.getText(),
                    txtEmail.getText()
            );

            JOptionPane.showMessageDialog(
                    this,
                    "Cliente atualizado com sucesso!",
                    "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE
            );

            carregarClientes();

            limparCampos();

        } catch (
                IllegalArgumentException
                | SQLException e
        ) {

            mostrarErro(
                    e.getMessage()
            );
        }
    }

    private void excluirCliente() {

        if (clienteSelecionadoId == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Selecione um cliente na tabela.",
                    "Atenção",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        int resposta =
                JOptionPane.showConfirmDialog(
                        this,
                        "Deseja realmente excluir este cliente?",
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

            clienteController.excluir(
                    clienteSelecionadoId
            );

            JOptionPane.showMessageDialog(
                    this,
                    "Cliente excluído com sucesso!",
                    "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE
            );

            carregarClientes();

            limparCampos();

        } catch (SQLException e) {

            mostrarErro(
                    "Não foi possível excluir o cliente.\n"
                            + e.getMessage()
            );
        }
    }

    private void carregarClientes() {

        try {

            List<Cliente> clientes =
                    clienteController
                            .listarTodos();

            modeloTabela.setRowCount(
                    0
            );

            for (Cliente cliente : clientes) {

                modeloTabela.addRow(
                        new Object[]{
                                cliente.getId(),
                                cliente.getNome(),
                                cliente.getCnpj(),
                                cliente.getSegmento(),
                                cliente.getEmail()
                        }
                );
            }

        } catch (SQLException e) {

            mostrarErro(
                    "Erro ao carregar clientes.\n"
                            + e.getMessage()
            );
        }
    }

    private void selecionarCliente() {

        int linha =
                tabelaClientes
                        .getSelectedRow();

        if (linha == -1) {
            return;
        }

        clienteSelecionadoId =
                ((Number)
                        modeloTabela.getValueAt(
                                linha,
                                0
                        ))
                        .longValue();

        txtNome.setText(
                modeloTabela
                        .getValueAt(
                                linha,
                                1
                        )
                        .toString()
        );

        txtCnpj.setText(
                modeloTabela
                        .getValueAt(
                                linha,
                                2
                        )
                        .toString()
        );

        txtSegmento.setText(
                modeloTabela
                        .getValueAt(
                                linha,
                                3
                        )
                        .toString()
        );

        txtEmail.setText(
                modeloTabela
                        .getValueAt(
                                linha,
                                4
                        )
                        .toString()
        );
    }

    private void limparCampos() {

        txtNome.setText("");
        txtCnpj.setText("");
        txtSegmento.setText("");
        txtEmail.setText("");

        clienteSelecionadoId =
                null;

        tabelaClientes
                .clearSelection();

        txtNome.requestFocus();
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

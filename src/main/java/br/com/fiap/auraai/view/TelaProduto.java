package br.com.fiap.auraai.view;

import br.com.fiap.auraai.client.ProdutoApiClient;
import br.com.fiap.auraai.domain.model.ProdutoTOTVS;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import br.com.fiap.auraai.client.ApiException;
import java.util.List;

public class TelaProduto extends JFrame {

    private JTextField txtNome;
    private JTextField txtCategoria;
    private JTextField txtVersao;
    private JTextArea txtDescricao;

    private JButton btnCadastrar;
    private JButton btnAtualizar;
    private JButton btnExcluir;
    private JButton btnLimpar;

    private JTable tabelaProdutos;
    private DefaultTableModel modeloTabela;

    private final ProdutoApiClient produtoApi;

    private Long produtoSelecionadoId;

    public TelaProduto() {

        produtoApi =
                new ProdutoApiClient();

        configurarJanela();
        criarComponentes();
        configurarEventos();

        carregarProdutos();
    }

    private void configurarJanela() {

        setTitle("Aura AI - Produtos TOTVS");

        setSize(900, 650);

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
                        "Gerenciamento de Produtos TOTVS",
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
                        "Dados do Produto"
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

        txtNome =
                new JTextField(25);

        txtCategoria =
                new JTextField(25);

        txtVersao =
                new JTextField(25);

        txtDescricao =
                new JTextArea(
                        4,
                        25
                );

        txtDescricao.setLineWrap(true);
        txtDescricao.setWrapStyleWord(true);

        JScrollPane scrollDescricao =
                new JScrollPane(
                        txtDescricao
                );

        // NOME

        gbc.gridx = 0;
        gbc.gridy = 0;

        painelFormulario.add(
                new JLabel("Nome:"),
                gbc
        );

        gbc.gridx = 1;

        painelFormulario.add(
                txtNome,
                gbc
        );

        // CATEGORIA

        gbc.gridx = 0;
        gbc.gridy = 1;

        painelFormulario.add(
                new JLabel("Categoria:"),
                gbc
        );

        gbc.gridx = 1;

        painelFormulario.add(
                txtCategoria,
                gbc
        );

        // VERSÃO

        gbc.gridx = 0;
        gbc.gridy = 2;

        painelFormulario.add(
                new JLabel("Versão:"),
                gbc
        );

        gbc.gridx = 1;

        painelFormulario.add(
                txtVersao,
                gbc
        );

        // DESCRIÇÃO

        gbc.gridx = 0;
        gbc.gridy = 3;

        gbc.anchor =
                GridBagConstraints.NORTH;

        painelFormulario.add(
                new JLabel("Descrição:"),
                gbc
        );

        gbc.gridx = 1;

        painelFormulario.add(
                scrollDescricao,
                gbc
        );

        // ==========================
        // BOTÕES
        // ==========================

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

        // ==========================
        // TABELA
        // ==========================

        modeloTabela =
                new DefaultTableModel(
                        new Object[]{
                                "ID",
                                "Nome",
                                "Categoria",
                                "Versão",
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

        tabelaProdutos =
                new JTable(
                        modeloTabela
                );

        tabelaProdutos.setSelectionMode(
                ListSelectionModel
                        .SINGLE_SELECTION
        );

        JScrollPane scrollTabela =
                new JScrollPane(
                        tabelaProdutos
                );

        scrollTabela.setBorder(
                BorderFactory.createTitledBorder(
                        "Produtos Cadastrados"
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
                scrollTabela,
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
                e -> cadastrarProduto()
        );

        btnAtualizar.addActionListener(
                e -> atualizarProduto()
        );

        btnExcluir.addActionListener(
                e -> excluirProduto()
        );

        btnLimpar.addActionListener(
                e -> limparCampos()
        );

        tabelaProdutos
                .getSelectionModel()
                .addListSelectionListener(
                        e -> {

                            if (!e.getValueIsAdjusting()) {

                                selecionarProduto();
                            }
                        }
                );
    }

    private void cadastrarProduto() {

        try {

            ProdutoTOTVS produto =
                    produtoApi.cadastrar(
                            txtNome.getText(),
                            txtCategoria.getText(),
                            txtVersao.getText(),
                            txtDescricao.getText()
                    );

            JOptionPane.showMessageDialog(
                    this,
                    "Produto cadastrado com sucesso!\n"
                            + "ID: "
                            + produto.getId(),
                    "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE
            );

            carregarProdutos();

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

    private void atualizarProduto() {

        if (produtoSelecionadoId == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Selecione um produto na tabela.",
                    "Atenção",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        try {

            produtoApi.atualizar(
                    produtoSelecionadoId,
                    txtNome.getText(),
                    txtCategoria.getText(),
                    txtVersao.getText(),
                    txtDescricao.getText()
            );

            JOptionPane.showMessageDialog(
                    this,
                    "Produto atualizado com sucesso!",
                    "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE
            );

            carregarProdutos();

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

    private void excluirProduto() {

        if (produtoSelecionadoId == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Selecione um produto na tabela.",
                    "Atenção",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        int resposta =
                JOptionPane.showConfirmDialog(
                        this,
                        "Deseja realmente excluir este produto?",
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

            produtoApi.excluir(
                    produtoSelecionadoId
            );

            JOptionPane.showMessageDialog(
                    this,
                    "Produto excluído com sucesso!",
                    "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE
            );

            carregarProdutos();

            limparCampos();

        } catch (ApiException e) {

            mostrarErro(
                    "Não foi possível excluir o produto.\n"
                            + e.getMessage()
            );
        }
    }

    private void carregarProdutos() {

        try {

            List<ProdutoTOTVS> produtos =
                    produtoApi
                            .listarTodos();

            modeloTabela.setRowCount(
                    0
            );

            for (
                    ProdutoTOTVS produto :
                    produtos
            ) {

                modeloTabela.addRow(
                        new Object[]{
                                produto.getId(),
                                produto.getNome(),
                                produto.getCategoria(),
                                produto.getVersao(),
                                produto.getDescricao()
                        }
                );
            }

        } catch (ApiException e) {

            mostrarErro(
                    "Erro ao carregar produtos.\n"
                            + e.getMessage()
            );
        }
    }


    private void selecionarProduto() {

        int linha =
                tabelaProdutos
                        .getSelectedRow();

        if (linha == -1) {
            return;
        }

        produtoSelecionadoId =
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

        txtCategoria.setText(
                modeloTabela
                        .getValueAt(
                                linha,
                                2
                        )
                        .toString()
        );

        txtVersao.setText(
                modeloTabela
                        .getValueAt(
                                linha,
                                3
                        )
                        .toString()
        );

        txtDescricao.setText(
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
        txtCategoria.setText("");
        txtVersao.setText("");
        txtDescricao.setText("");

        produtoSelecionadoId =
                null;

        tabelaProdutos
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

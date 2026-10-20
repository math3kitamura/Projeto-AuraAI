package view;

import javax.swing.*;
import java.awt.*;

public class TelaPrincipal extends JFrame {

    private JButton btnClientes;
    private JButton btnProdutos;
    private JButton btnReunioes;
    private JButton btnAnalise;
    private JButton btnSair;

    public TelaPrincipal() {

        configurarJanela();
        criarComponentes();
        configurarEventos();
    }

    private void configurarJanela() {

        setTitle("Aura AI");

        setSize(500, 400);

        setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE
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
                        "AURA AI",
                        SwingConstants.CENTER
                );

        titulo.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        26
                )
        );

        JLabel subtitulo =
                new JLabel(
                        "Inteligência Conversacional",
                        SwingConstants.CENTER
                );

        JPanel painelTitulo =
                new JPanel(
                        new GridLayout(2, 1)
                );

        painelTitulo.add(titulo);
        painelTitulo.add(subtitulo);

        painelPrincipal.add(
                painelTitulo,
                BorderLayout.NORTH
        );

        JPanel painelBotoes =
                new JPanel(
                        new GridLayout(
                                5,
                                1,
                                10,
                                10
                        )
                );

        btnClientes =
                new JButton(
                        "Clientes"
                );

        btnProdutos =
                new JButton(
                        "Produtos TOTVS"
                );

        btnReunioes =
                new JButton(
                        "Reuniões"
                );

        btnAnalise =
                new JButton(
                        "Análise IA"
                );

        btnSair =
                new JButton(
                        "Sair"
                );

        painelBotoes.add(
                btnClientes
        );

        painelBotoes.add(
                btnProdutos
        );

        painelBotoes.add(
                btnReunioes
        );

        painelBotoes.add(
                btnAnalise
        );

        painelBotoes.add(
                btnSair
        );

        painelPrincipal.add(
                painelBotoes,
                BorderLayout.CENTER
        );

        add(painelPrincipal);
    }

    private void configurarEventos() {

        btnClientes.addActionListener(
                e -> {

                    TelaCliente telaCliente =
                            new TelaCliente();

                    telaCliente.setVisible(true);
                }
        );

        btnProdutos.addActionListener(
                e -> {

                    TelaProduto telaProduto =
                            new TelaProduto();

                    telaProduto.setVisible(true);
                }
        );

        btnReunioes.addActionListener(
                e -> {

                    TelaReuniao telaReuniao =
                            new TelaReuniao();

                    telaReuniao.setVisible(
                            true
                    );
                }
        );

        btnAnalise.addActionListener(
                e -> {

                    TelaAnalise telaAnalise =
                            new TelaAnalise();

                    telaAnalise.setVisible(
                            true
                    );
                }
        );

        btnSair.addActionListener(
                e -> System.exit(0)
        );
    }
}
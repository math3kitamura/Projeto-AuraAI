package domain.model;

public class Transcricao extends EntidadeBase {

    private String textoBruto;
    private int qtdPalavras;
    private String idioma;
    private Reuniao reuniao;

    // Construtor para uma NOVA transcrição
    // Ainda não possui ID porque não foi salva no banco
    public Transcricao(
            String textoBruto,
            String idioma,
            Reuniao reuniao
    ) {
        super();

        setTextoBruto(textoBruto);
        setIdioma(idioma);
        setReuniao(reuniao);

        this.qtdPalavras = calcularQuantidadePalavras();
    }

    // Construtor para manter compatibilidade com a Main atual
    // Também será útil quando o objeto vier do banco
    public Transcricao(
            Long id,
            String textoBruto,
            String idioma,
            Reuniao reuniao
    ) {
        super(id);

        setTextoBruto(textoBruto);
        setIdioma(idioma);
        setReuniao(reuniao);

        this.qtdPalavras = calcularQuantidadePalavras();
    }

    private int calcularQuantidadePalavras() {
        return textoBruto.trim().split("\\s+").length;
    }

    public String getTextoBruto() {
        return textoBruto;
    }

    public void setTextoBruto(String textoBruto) {

        if (textoBruto == null || textoBruto.isBlank()) {
            throw new IllegalArgumentException(
                    "O texto da transcrição não pode ficar em branco."
            );
        }

        this.textoBruto = textoBruto.trim();

        // Sempre que o texto for alterado,
        // recalculamos a quantidade de palavras
        this.qtdPalavras = calcularQuantidadePalavras();
    }

    public int getQtdPalavras() {
        return qtdPalavras;
    }

    public String getIdioma() {
        return idioma;
    }

    public void setIdioma(String idioma) {

        if (idioma == null || idioma.isBlank()) {
            throw new IllegalArgumentException(
                    "O idioma da transcrição não pode ficar em branco."
            );
        }

        this.idioma = idioma.trim();
    }

    public Reuniao getReuniao() {
        return reuniao;
    }

    public void setReuniao(Reuniao reuniao) {

        if (reuniao == null) {
            throw new IllegalArgumentException(
                    "A transcrição precisa estar vinculada a uma reunião."
            );
        }

        this.reuniao = reuniao;
    }

    public void processar() {
        System.out.println(
                "Transcrição processada com "
                        + qtdPalavras
                        + " palavras."
        );
    }

    @Override
    public void exibirResumo() {

        System.out.println("Idioma: " + idioma);
        System.out.println(
                "Quantidade de palavras: "
                        + qtdPalavras
        );
    }
}
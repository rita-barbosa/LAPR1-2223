import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.lang.Runtime;

public class The_Project {
    static final int NUM_DE_ARGUMENTOS = 9;
    static String[] LINHA_COMANDO_CORRETA = {"input_file.csv", "-m", "", "-p", "", "-t", "", "-d", ""};
    static final String[] LEGENDA_DA_TABELA = {"dia", "S", "I", "R", "N"};
    static final int TIPOS_DE_POPULACOES = 4;
    static final int NUMERO_DE_K_RK4 = 4;
    static final int CONSTANTE_NULA = 0;
    static final double METADE = (double) 1 / 2;
    static final double CONSTANTE_MEDIA_RK4 = ((double) 1 / 6);
    static Scanner read = new Scanner(System.in);
    static String EXTENSAO_FICHEIRO_CSV = ".csv";
    static String EXTENSAO_FICHEIRO_TXT = ".txt";
    static String EXTENSAO_FICHEIRO_PNG = ".png";

    static final NumberFormat numFormat = new DecimalFormat("0.##E0");

    public static void main(String[] args) throws IOException {
        double h = -1;
        int numPopulacao = 0;
        int numDias = 0;
        String ficheiroParametros = "";
        boolean correctCMD = true;

        switch (args.length) {
            case 0:
                System.out.printf("%nESTUDO DA PROPAGAÇÃO DE UMA NOTÍCIA FALSA.%n Introduza a seguinte informação:%n");
                executarModoInterativo(h, numPopulacao, numDias, ficheiroParametros, METADE, CONSTANTE_NULA, CONSTANTE_MEDIA_RK4, TIPOS_DE_POPULACOES, NUMERO_DE_K_RK4, LEGENDA_DA_TABELA, EXTENSAO_FICHEIRO_TXT, EXTENSAO_FICHEIRO_CSV, EXTENSAO_FICHEIRO_PNG);
                break;
            case NUM_DE_ARGUMENTOS:
                correctCMD = verificarLinhaDeComandos(args, LINHA_COMANDO_CORRETA, EXTENSAO_FICHEIRO_CSV);
                if (!correctCMD) {
                    erroMensagem();
                } else {
                    executarModoNaoInterativo(args, EXTENSAO_FICHEIRO_PNG, EXTENSAO_FICHEIRO_TXT, EXTENSAO_FICHEIRO_CSV, LEGENDA_DA_TABELA, CONSTANTE_MEDIA_RK4, METADE, CONSTANTE_NULA, TIPOS_DE_POPULACOES, NUMERO_DE_K_RK4);
                }
                break;
            default:
                erroMensagem();
                break;

        }
    }

    private static void executarModoInterativo(double h, int numPopulacao, int numDias, String ficheiroParametros,
                                               double constanteMetade, int constanteNula, double constanteMediaDosK, int tiposPopulacaoes, int numeroDeKs, String[] arrLegendaTabelas, String extensaoFicheiroTxt, String extensaoFicheiroCsv, String extensaoFicheiroPng) throws IOException {
        int[] arrTamanhoDaMatrizParametros = new int[2];

        ficheiroParametros = lerNomeFicheiroParametros(ficheiroParametros);
        determinarNumeroDeColunasELinhasDaMatrizDeParametros(ficheiroParametros, arrTamanhoDaMatrizParametros);
        String[] arrNomesPessoasAnalisar = new String[arrTamanhoDaMatrizParametros[0]];
        double[][] matrizParametros = new double[arrTamanhoDaMatrizParametros[0]][arrTamanhoDaMatrizParametros[1]];
        preencherMatrizDosParametros(ficheiroParametros, arrNomesPessoasAnalisar, matrizParametros);
        h = lerValorH(h);
        numPopulacao = lerValorNumeroPopulacao(numPopulacao);
        numDias = lerValorNumeroDias(numDias);

        menu(ficheiroParametros, matrizParametros, h, numPopulacao, numDias, arrNomesPessoasAnalisar, constanteMetade, constanteNula, constanteMediaDosK, tiposPopulacaoes, numeroDeKs, arrLegendaTabelas, extensaoFicheiroTxt, extensaoFicheiroCsv, extensaoFicheiroPng);
    }

    private static int mostrarOpcoesMenu() {
        System.out.printf("%nQual funcionalidade deseja efetuar?%n");
        System.out.println(" 1: Modelo de SIR através do método de EULER");
        System.out.println(" 2: Modelo de SIR através do método de RUNGE-KUNTA DE 4ªORDEM.");
        System.out.println(" 3: Análise Comparativa");
        System.out.println(" 4: Executar todas as funcionalidades.");
        System.out.println(" 5: Voltar a inserir dados iniciais.");
        System.out.println(" 0: Terminar programa.");
        return verificarValor();
    }

    private static int verificarValor() {
        int numero = -1;
        do {
            try {
                numero = read.nextInt();
            } catch (InputMismatchException exception) {
                System.out.println("ERRO: Inseriu uma letra ou símbolo. ");
            }
            read.nextLine();
        } while (numero == -1);
        return numero;
    }

    private static void
    menu(String ficheiroParametros, double[][] matrizParametros, double h, int numPopulacao, int numDias, String[] arrNomesPessoasAnalisar,
         double constanteMetade, int constanteNula, double constanteMediaDosK, int numeroTiposPopulacaoes, int numeroDeKsParaRK, String[] arrLegendaTabelas, String extensaoFicheiroTxt, String extensaoFicheiroCsv, String extensaoFicheiroPng) throws IOException {
        int steps = (int) (numDias / h);
        int avanco = (int) (1 / h);
        int pessoaAnalisar;
        int metodoUsado = 0;
        String nomeFicheiroOutput = "";
        double[][] matrizValoresEuler = new double[numeroTiposPopulacaoes][steps + 1];
        double[][] matrizValoresRK4 = new double[numeroTiposPopulacaoes][steps + 1];
        double[][] matrizK = new double[3][numeroDeKsParaRK];
        atribuirValoresIniciaisMatrizValores(matrizValoresRK4, numPopulacao);

        switch (mostrarOpcoesMenu()) {
            case 1:
                metodoUsado = 1;
                pessoaAnalisar = lerEscolhaPessoaAnalisar(arrNomesPessoasAnalisar);
                calculoMetodoEuler(matrizValoresEuler, numPopulacao, h, steps, matrizParametros[pessoaAnalisar][2], matrizParametros[pessoaAnalisar][1], matrizParametros[pessoaAnalisar][3], matrizParametros[pessoaAnalisar][0]);
                nomeFicheiroOutput = fazerNomeFicheiroComExtensao(h, numPopulacao, numDias, arrNomesPessoasAnalisar[pessoaAnalisar], metodoUsado, extensaoFicheiroCsv);
                preencherFicheiroCSV(nomeFicheiroOutput, avanco, arrLegendaTabelas, matrizValoresEuler, numDias);
                metodoParaGrafico(metodoUsado, nomeFicheiroOutput, h, numPopulacao, numDias, arrNomesPessoasAnalisar[pessoaAnalisar], extensaoFicheiroPng);
                perguntarSeUtilizadorQuerTerminarPrograma(ficheiroParametros, matrizParametros, h, numPopulacao, numDias, arrNomesPessoasAnalisar, constanteMetade, constanteNula, constanteMediaDosK, numeroTiposPopulacaoes, numeroDeKsParaRK, arrLegendaTabelas, extensaoFicheiroTxt, extensaoFicheiroCsv, extensaoFicheiroPng);
                break;
            case 2:
                metodoUsado = 2;
                pessoaAnalisar = lerEscolhaPessoaAnalisar(arrNomesPessoasAnalisar);
                calculoMetodoRungeKutta(matrizK, matrizValoresRK4, h, steps, matrizParametros[pessoaAnalisar][2], matrizParametros[pessoaAnalisar][1], matrizParametros[pessoaAnalisar][3], matrizParametros[pessoaAnalisar][0], constanteMediaDosK, constanteMetade, constanteNula, numPopulacao);
                nomeFicheiroOutput = fazerNomeFicheiroComExtensao(h, numPopulacao, numDias, arrNomesPessoasAnalisar[pessoaAnalisar], metodoUsado, extensaoFicheiroCsv);
                preencherFicheiroCSV(nomeFicheiroOutput, avanco, arrLegendaTabelas, matrizValoresRK4, numDias);
                metodoParaGrafico(metodoUsado, nomeFicheiroOutput, h, numPopulacao, numDias, arrNomesPessoasAnalisar[pessoaAnalisar], extensaoFicheiroPng);
                perguntarSeUtilizadorQuerTerminarPrograma(ficheiroParametros, matrizParametros, h, numPopulacao, numDias, arrNomesPessoasAnalisar, constanteMetade, constanteNula, constanteMediaDosK, numeroTiposPopulacaoes, numeroDeKsParaRK, arrLegendaTabelas, extensaoFicheiroTxt, extensaoFicheiroCsv, extensaoFicheiroPng);
                break;
            case 3:
                menuAnaliseComparativa(arrNomesPessoasAnalisar, arrLegendaTabelas, matrizValoresEuler, matrizK, matrizValoresRK4, numPopulacao, numDias, h, steps, matrizParametros, constanteMediaDosK, constanteMetade, constanteNula, avanco, extensaoFicheiroTxt);
                perguntarSeUtilizadorQuerTerminarPrograma(ficheiroParametros, matrizParametros, h, numPopulacao, numDias, arrNomesPessoasAnalisar, constanteMetade, constanteNula, constanteMediaDosK, numeroTiposPopulacaoes, numeroDeKsParaRK, arrLegendaTabelas, extensaoFicheiroTxt, extensaoFicheiroCsv, extensaoFicheiroPng);
                break;
            case 4:
                pessoaAnalisar = lerEscolhaPessoaAnalisar(arrNomesPessoasAnalisar);
                for (metodoUsado = 1; metodoUsado <= 2; metodoUsado++) {
                    executarTodasFuncionalidades(metodoUsado, avanco, steps, pessoaAnalisar, matrizValoresEuler, matrizValoresRK4, matrizK, matrizParametros, h, numPopulacao, numDias, arrNomesPessoasAnalisar, constanteMetade, constanteNula, constanteMediaDosK, arrLegendaTabelas, extensaoFicheiroTxt, extensaoFicheiroCsv, extensaoFicheiroPng);
                }
                perguntarSeUtilizadorQuerTerminarPrograma(ficheiroParametros, matrizParametros, h, numPopulacao, numDias, arrNomesPessoasAnalisar, constanteMetade, constanteNula, constanteMediaDosK, numeroTiposPopulacaoes, numeroDeKsParaRK, arrLegendaTabelas, extensaoFicheiroTxt, extensaoFicheiroCsv, extensaoFicheiroPng);
                break;
            case 5:
                h = -1;
                numDias = 0;
                numPopulacao = 0;
                h = lerValorH(h);
                numPopulacao = lerValorNumeroPopulacao(numPopulacao);
                numDias = lerValorNumeroDias(numDias);
                menu(ficheiroParametros, matrizParametros, h, numPopulacao, numDias, arrNomesPessoasAnalisar, constanteMetade, constanteNula, constanteMediaDosK, numeroTiposPopulacaoes, numeroDeKsParaRK, arrLegendaTabelas, extensaoFicheiroTxt, extensaoFicheiroCsv, extensaoFicheiroPng);
                break;
            case 0:
                System.exit(0);
                break;
            default:
                System.out.println("ERRO: A opção inserida é inválida.");
                menu(ficheiroParametros, matrizParametros, h, numPopulacao, numDias, arrNomesPessoasAnalisar, constanteMetade, constanteNula, constanteMediaDosK, numeroTiposPopulacaoes, numeroDeKsParaRK, arrLegendaTabelas, extensaoFicheiroTxt, extensaoFicheiroCsv, extensaoFicheiroPng);
                break;
        }
    }

    private static void executarTodasFuncionalidades(int metodoUsado, int avanco, int steps, int pessoaAnalisar, double[][] matrizValoresEuler,
                                                     double[][] matrizValoresRK4, double[][] matrizK, double[][] matrizParametros, double h, int numPopulacao, int numDias,
                                                     String[] arrNomesPessoasAnalisar, double constanteMetade, int constanteNula, double constanteMediaDosK,
                                                     String[] arrLegendaTabelas, String extensaoFicheiroTxt, String extensaoFicheiroCsv, String extensaoFicheiroPng) throws IOException {

        if (metodoUsado == 1) {
            calculoMetodoEuler(matrizValoresEuler, numPopulacao, h, steps, matrizParametros[pessoaAnalisar][2], matrizParametros[pessoaAnalisar][1], matrizParametros[pessoaAnalisar][3], matrizParametros[pessoaAnalisar][0]);
        } else {
            calculoMetodoRungeKutta(matrizK, matrizValoresRK4, h, steps, matrizParametros[pessoaAnalisar][2], matrizParametros[pessoaAnalisar][1], matrizParametros[pessoaAnalisar][3], matrizParametros[pessoaAnalisar][0], constanteMediaDosK, constanteMetade, constanteNula, numPopulacao);
        }
        String nomeFicheiroOutput = fazerNomeFicheiroComExtensao(h, numPopulacao, numDias, arrNomesPessoasAnalisar[pessoaAnalisar], metodoUsado, extensaoFicheiroCsv);
        preencherFicheiroCSV(nomeFicheiroOutput, avanco, arrLegendaTabelas, matrizValoresEuler, numDias);
        preencherFicheiroTxt(matrizValoresEuler, matrizK, matrizParametros, h, steps, arrNomesPessoasAnalisar, arrLegendaTabelas, avanco, "analise", extensaoFicheiroTxt, numPopulacao, numDias, metodoUsado, constanteMediaDosK, constanteMetade, constanteNula);
        metodoParaGrafico(metodoUsado, nomeFicheiroOutput, h, numPopulacao, numDias, arrNomesPessoasAnalisar[pessoaAnalisar], extensaoFicheiroPng);
    }


    private static void perguntarSeUtilizadorQuerTerminarPrograma(String ficheiroParametros, double[][] matrizParametros, double h,
                                                                  int numPopulacao, int numDias, String[] arrNomesPessoasAnalisar,
                                                                  double constanteMetade, int constanteNula, double constanteMediaDosK,
                                                                  int tiposPopulacaoes, int numeroDeKs, String[] arrLegendaTabelas, String extensaoFicheiroTxt, String extensaoFicheiroCsv, String extensaoFicheiroPng) throws IOException {

        switch (mostrarOpcaoSaidaOuContinuacaoPrograma()) {
            case 1:
                menu(ficheiroParametros, matrizParametros, h, numPopulacao, numDias, arrNomesPessoasAnalisar, constanteMetade, constanteNula, constanteMediaDosK, tiposPopulacaoes, numeroDeKs, arrLegendaTabelas, extensaoFicheiroTxt, extensaoFicheiroCsv, extensaoFicheiroPng);
                break;
            case 0:
                System.exit(0);
                break;
            default:
                System.out.printf("A opção introduzida é inválida.%n");
                perguntarSeUtilizadorQuerTerminarPrograma(ficheiroParametros, matrizParametros, h, numPopulacao, numDias, arrNomesPessoasAnalisar, constanteMetade, constanteNula, constanteMediaDosK, tiposPopulacaoes, numeroDeKs, arrLegendaTabelas, extensaoFicheiroTxt, extensaoFicheiroCsv, extensaoFicheiroPng);
                break;
        }
    }

    private static int mostrarOpcaoSaidaOuContinuacaoPrograma() {
        System.out.printf("%nSelecione a opção desejada%n");
        System.out.printf("  1: Voltar menu.%n");
        System.out.printf("  0: Terminar o programa.%n");
        return verificarValor();
    }


    private static String lerNomeFicheiroParametros(String nomeFicheiro) {
        String[] nomeETipoFicheiro;
        File ficheiro;
        do {
            System.out.printf("%nInsira o nome do ficheiro [Exemplo: nomeficheiro.csv].%n");
            nomeFicheiro = read.nextLine();
            nomeETipoFicheiro = nomeFicheiro.split("\\.");
            ficheiro = new File(nomeFicheiro);
            verificarNomeFicheiro(ficheiro, nomeETipoFicheiro);
        } while (!isFileExists(ficheiro) || !nomeETipoFicheiro[1].equals("csv"));

        return nomeFicheiro;
    }


    private static boolean isFileExists(File ficheiro) {
        return ficheiro.exists();
    }


    private static void verificarNomeFicheiro(File ficheiro, String[] nomeETipoFicheiro) throws ArrayIndexOutOfBoundsException {
        if (!isFileExists(ficheiro)) {
            System.out.printf("ERRO: O ficheiro não existe.%n");
        } else {
            try {
                if (!nomeETipoFicheiro[1].equals("csv") || nomeETipoFicheiro.length > 2) {
                    System.out.printf("ERRO: A extensão do ficheiro introduzido é inválida.%n");
                }
            } catch (ArrayIndexOutOfBoundsException ex) {
                System.out.printf("ERRO: O nome do ficheiro não apresenta extensão.%n");
            }
        }
    }

    private static void determinarNumeroDeColunasELinhasDaMatrizDeParametros(String ficheiroParametros, int[] matrixPropriedades) throws FileNotFoundException {
        Scanner lerDoFicheiro = new Scanner(new File(ficheiroParametros));
        int linhas = 0;
        String[] numParametros = lerDoFicheiro.nextLine().split(";");
        int colunas = numParametros.length;
        while (lerDoFicheiro.hasNextLine()) {
            linhas++;
            lerDoFicheiro.nextLine();
        }
        matrixPropriedades[0] = linhas;
        matrixPropriedades[1] = colunas - 1;
        lerDoFicheiro.close();
    }

    private static void preencherMatrizDosParametros(String ficheiro_Input, String[] nomesInfo, double[][] numerosInfo) throws FileNotFoundException {
        Scanner read = new Scanner(new File(ficheiro_Input));
        int numLinha = 0;
        read.nextLine();
        while (read.hasNextLine() && numLinha < nomesInfo.length) {
            String[] arrLinha = read.nextLine().split(";");
            nomesInfo[numLinha] = arrLinha[0];
            for (int i = 1; i < arrLinha.length; i++) {
                String linha = arrLinha[i].replace(",", ".");
                numerosInfo[numLinha][i - 1] = Double.parseDouble(linha);
            }
            numLinha++;
        }
        read.close();
    }


    private static double lerValorH(double h) {
        while (h <= 0 || h > 1) {
            System.out.printf("%nIntroduza o valor do passo de integração [0 < h <= 1].%n");
            String valorH = read.nextLine();
            h = converterStringParaValorDouble(valorH);
            if (h <= 0 || h > 1) {
                System.out.printf("ERRO: O valor introduzido é inválido.%n");
            }
        }
        return h;
    }

    private static double converterStringParaValorDouble(String valor) {
        valor = valor.replaceAll(",", ".");
        return Double.parseDouble(valor);
    }


    private static int lerValorNumeroDias(int numDias) {
        while (numDias <= 0) {
            System.out.printf("%nIntroduza o número dias.%n");
            numDias = read.nextInt();
            if (numDias <= 0) {
                System.out.printf("ERRO: O valor introduzido é inválido.%n");
            }
        }
        return numDias;
    }


    private static int lerValorNumeroPopulacao(int numPopulacao) {
        while (numPopulacao <= 0) {
            System.out.printf("%nIntroduza o número total da população.%n");
            numPopulacao = read.nextInt();
            if (numPopulacao <= 0) {
                System.out.printf("ERRO: O valor introduzido é inválido.%n");
            }
        }
        return numPopulacao;
    }

    private static int lerEscolhaPessoaAnalisar(String[] arrNomesPessoasAnalisar) {
        int pessoaAnalisar;
        System.out.printf("%nEscolha uma das seguintes pessoas para analisar:%n");
        for (int i = 0; i < arrNomesPessoasAnalisar.length; i++) {
            System.out.printf("%d: %s%n", i, arrNomesPessoasAnalisar[i]);
        }
        pessoaAnalisar = read.nextInt();
        return pessoaAnalisar;
    }

    private static int perguntarOpcaoDeAnalise() {
        System.out.printf("%nPara qual método pretende obter a análise comparativa?%n");
        System.out.println(" 1: Método de EULER.");
        System.out.println(" 2: Método de RUNGE-KUNTA DE 4ªORDEM.");
        return verificarValor();
    }

    private static void menuAnaliseComparativa(String[] arrNomesPessoasAnalisar, String[] arrLegendaTabelas, double[][] matrizValoresEuler,
                                               double[][] matrizK, double[][] matrizValoresRK4, int numPopulacao, int numDias, double h,
                                               int steps, double[][] matrizParametros, double constanteMediaDosK, double constanteMetade, int constanteNula, int avanco, String extensaoFicheiroTxt) throws FileNotFoundException {
        String nomeFicheiroOutput = "analise";
        int opcaoMenu = perguntarOpcaoDeAnalise();
        switch (opcaoMenu) {
            case 1:
                preencherFicheiroTxt(matrizValoresEuler, matrizK, matrizParametros, h, steps, arrNomesPessoasAnalisar, arrLegendaTabelas, avanco, nomeFicheiroOutput, extensaoFicheiroTxt, numPopulacao, numDias, opcaoMenu, constanteMediaDosK, constanteMetade, constanteNula);
                break;
            case 2:
                preencherFicheiroTxt(matrizValoresRK4, matrizK, matrizParametros, h, steps, arrNomesPessoasAnalisar, arrLegendaTabelas, avanco, nomeFicheiroOutput, extensaoFicheiroTxt, numPopulacao, numDias, opcaoMenu, constanteMediaDosK, constanteMetade, constanteNula);
                break;
            default:
                System.out.println("ERRO: A opção inserida é inválida.");
                menuAnaliseComparativa(arrNomesPessoasAnalisar, arrLegendaTabelas, matrizValoresEuler, matrizK, matrizValoresRK4, numPopulacao, numDias, h, steps, matrizParametros, constanteMediaDosK, constanteMetade, constanteNula, avanco, extensaoFicheiroTxt);
                break;
        }
    }

    private static void preencherFicheiroTxt(double[][] matrizValores, double[][] matrizK, double[][] matrizParametros, double h, int steps, String[] arrNomesPessoasAnalisar,
                                             String[] arrLegendaTabelas, int avanco, String nomeFicheiroOutput, String extensaoFicheiroTxt,
                                             int numPopulacao, int numDias, int opcaoMetodo, double constanteMediaDosK, double constanteMetade, int constanteNula) throws FileNotFoundException {
        PrintWriter outputTabelaEuler = new PrintWriter(fazerNomeFicheiroComExtensao(h, numPopulacao, numDias, nomeFicheiroOutput, opcaoMetodo, extensaoFicheiroTxt));
        for (int i = 0; i < arrNomesPessoasAnalisar.length; i++) {
            fazerCalculoPopulacoesAnaliseComparativa(i, opcaoMetodo, matrizK, matrizValores, h, steps, matrizParametros, constanteMediaDosK, constanteMetade, constanteNula, numPopulacao);
            fazerTabelaAnaliseComparativa(outputTabelaEuler, arrNomesPessoasAnalisar[i], arrLegendaTabelas, matrizValores, numDias, avanco);
        }
        outputTabelaEuler.close();

    }

    private static void fazerTabelaAnaliseComparativa(PrintWriter out, String pessoaAnalisar, String[] arrLegendaTabelas,
                                                      double[][] matrizValores, int numDias, int avanco) {

        out.printf(">----- %s -----<", pessoaAnalisar);
        out.printf("%n--------------------------------------------------------------------");
        out.printf("%n%-4s %15s %15s %15s %15s%n", arrLegendaTabelas[0], arrLegendaTabelas[1], arrLegendaTabelas[2], arrLegendaTabelas[3], arrLegendaTabelas[4]);
        for (int contadorDias = 0; contadorDias <= numDias; contadorDias++) {
            int j = contadorDias * avanco;
            if (matrizValores[0][j] < 0.0001) {
                out.printf("%-4d %15s %15.3f %15.3f %15.3f%n", contadorDias, numFormat.format(matrizValores[0][j]), matrizValores[1][j], matrizValores[2][j], matrizValores[3][j]);
            } else {
                out.printf("%-4d %15.5f %15.3f %15.3f %15.3f%n", contadorDias, matrizValores[0][j], matrizValores[1][j], matrizValores[2][j], matrizValores[3][j]);
            }
        }
        out.println();
    }

    private static void fazerCalculoPopulacoesAnaliseComparativa(int i, int opcaoMetodo, double[][] matrizK, double[][] matrizValores, double h, int steps, double[][] matrizParametros, double constanteMediaDosK, double constanteMetade, int constanteNula, int numPopulacao) {
        if (opcaoMetodo == 1) {
            calculoMetodoEuler(matrizValores, numPopulacao, h, steps, matrizParametros[i][2], matrizParametros[i][1], matrizParametros[i][3], matrizParametros[i][0]);
        } else {
            calculoMetodoRungeKutta(matrizK, matrizValores, h, steps, matrizParametros[i][2], matrizParametros[i][1], matrizParametros[i][3], matrizParametros[i][0], constanteMediaDosK, constanteMetade, constanteNula, numPopulacao);
        }
    }


    private static void atribuirValoresIniciaisMatrizValores(double[][] matriz, int numPopulacao) {
        matriz[0][0] = numPopulacao - 1; // VALOR INICIAL DE S
        matriz[1][0] = 1; // VALOR INICIAL DE I
        matriz[2][0] = 0; // VALOR INICIAL DE R
        matriz[3][0] = numPopulacao;
    }

    private static void executarModoNaoInterativo(String[] args, String extensaoFicheiroPng, String extensaoFicheiroTxt, String extensaoFicheiroCsv, String[] constanteLegendaTabela,
                                                  double constanteMediaRK4, double constanteMetade, int constanteNula, int numeroTiposPopulacoes, int numeroDeKParaRK) throws IOException {
        // ===================================================================== variaveis do codigo
        String ficheiroParametros = args[0];
        int metodo = Integer.parseInt(args[2]);
        double h =converterStringParaValorDouble(args[4]);
        int numPopulacao = Integer.parseInt(args[6]);
        int numDias = Integer.parseInt(args[8]);
        // ===================================================================== variaveis dos parametros
        int[] matrixPropriedades = new int[2];
        determinarNumeroDeColunasELinhasDaMatrizDeParametros(ficheiroParametros, matrixPropriedades);
        String[] nomesInfo = new String[matrixPropriedades[0]];
        double[][] matrizParametros = new double[matrixPropriedades[0]][matrixPropriedades[1]];
        preencherMatrizDosParametros(ficheiroParametros, nomesInfo, matrizParametros);
        // ===================================================================== variaveis de Euler
        int steps = (int) (numDias / h);
        double[][] arrEulerMatrix = new double[numeroTiposPopulacoes][steps + 1];
        // ===================================================================== variaveis de RK4
        double[][] matrizValoresRK4 = new double[numeroTiposPopulacoes][steps + 1];
        double[][] matrizK = new double[3][numeroDeKParaRK];
        // ===================================================================== variaveis para ficheiro análise
        File ficheiroTabela = new File(fazerNomeFicheiroComExtensao(h, numPopulacao, numDias, "analise", metodo, extensaoFicheiroTxt));
        PrintWriter tabela = new PrintWriter(ficheiroTabela);
        // ===================================================================== distinção dos processos entre métodos
        System.out.println("Os seguintes ficheiros foram criados e guardados no diretório do projeto:");
        System.out.println(ficheiroTabela.getName());
        if (metodo == 1) {
            seguirProcessoEuler(extensaoFicheiroPng, metodo, extensaoFicheiroCsv, matrizParametros, arrEulerMatrix, numPopulacao, h,
                    steps, tabela, nomesInfo, numDias, constanteLegendaTabela);
        } else {
            seguirProcessoRungeKutta(extensaoFicheiroPng, metodo, extensaoFicheiroCsv, matrizParametros, matrizK, matrizValoresRK4,
                    constanteMediaRK4, constanteMetade, constanteNula, numPopulacao, h, steps, tabela, nomesInfo, numDias, constanteLegendaTabela);
        }
        tabela.close();
    }

    private static boolean verificarLinhaDeComandos(String[] args, String[] linhaCMDCorreta, String extensaoDesejada) {
        boolean cmdCorreta = true;

        for (int i = 0; i < args.length && cmdCorreta; i++) {
            if (args[i] == null) {
                cmdCorreta = false;
            } else {
                switch (i) {
                    case 0:
                        cmdCorreta = verExtensao(args[i], extensaoDesejada);
                        if (cmdCorreta) {
                            File ficheiro = new File(args[i]);
                            if (!ficheiro.exists()) {
                                System.out.println("O ficheiro CSV dos parâmetros (input) não existe.");
                                cmdCorreta = false;
                            }
                        }
                        break;
                    case 1, 3, 5, 7:
                        cmdCorreta = verExpressaoIgual(args[i], linhaCMDCorreta[i]);
                        break;
                    case 2, 6, 8:
                        cmdCorreta = verStringInteger(args[i]);
                        if (cmdCorreta) {
                            int numero = 0;
                            switch (i) {
                                case 2:
                                    numero = Integer.parseInt(args[i]);
                                    if (numero != 1 && numero != 2) {
                                        cmdCorreta = false;
                                    }
                                    break;
                                case 6, 8:
                                    numero = Integer.parseInt(args[i]);
                                    if (numero <= 0) {
                                        cmdCorreta = false;
                                    }
                                    break;
                            }
                        }
                        break;
                    case 4:
                        double numero;
                        cmdCorreta = verStringDouble(args[i]);
                        if (cmdCorreta) {
                            numero = converterStringParaValorDouble(args[i]);
                            if (numero <= 0 || numero > 1) {
                                cmdCorreta = false;
                            }
                        }
                        break;
                }
            }
        }
        return cmdCorreta;
    }

    private static boolean verExtensao(String arg, String extensaoDesejada) {
        boolean yesCSV = false;

        int index = arg.lastIndexOf('.');
        String extension = arg.substring(index);
        if (extension.equals(extensaoDesejada)) {
            yesCSV = true;
        }
        return yesCSV;
    }

    private static boolean verExpressaoIgual(String arg, String s) {
        boolean stringIgual = false;
        if (arg.equals(s)) {
            stringIgual = true;
        }
        return stringIgual;
    }

    private static boolean verStringInteger(String arg) {
        boolean notNumber = true;
        try {
            int number = Integer.parseInt(arg);
        } catch (NumberFormatException e) {
            notNumber = false;
        }
        return notNumber;
    }

    private static boolean verStringDouble(String arg) {
        boolean notNumber = true;
        arg = arg.replaceAll(",", ".");

        try {
            double number = Double.parseDouble(arg);
        } catch (NumberFormatException e) {
            notNumber = false;
        }
        return notNumber;
    }

    private static void preencherFicheirosCSVeTabelaMNI(PrintWriter tabela, String ficheiroCSV, String pessoa, double[][] matrizValores,
                                                        int numDias, String[] legendaDaTabela, double h) throws FileNotFoundException {
        int avanco = (int) (1 / h);
        fazerTabelaAnaliseComparativa(tabela, pessoa, legendaDaTabela, matrizValores, numDias, avanco);
        preencherFicheiroCSV(ficheiroCSV, avanco, legendaDaTabela, matrizValores, numDias);
    }

    private static void erroMensagem() {
        System.out.println("A linha de comando introduzida é inválida.");
        System.out.println("O seu comando deve seguir o seguinte formato:");
        System.out.println("java -jar <nomeAplicação.jar> <ficheiro_parâmetros.csv> -m X -p Y -t Z -d K");
        System.out.println("  (X) representa o método a usar (1-Euler ou 2-Runge Kutta de 4ª ordem).");
        System.out.println("  (Y) representa o passo de integração (0 < Y <= 1).");
        System.out.println("  (Z) representa o tamanho da população em estudo N (Z > 0).");
        System.out.println("  (K) representa o número de dias a considerar para a análise (K > 0).");
    }

    private static void seguirProcessoEuler(String extensaoGrafico, int metodoUsado, String extensaoDesejada, double[][] matrizParametros,
                                            double[][] arrEulerMatrix, int numPopulacao, double h, int steps, PrintWriter tabela,
                                            String[] nomesInfo, int numDias, String[] legenda_da_tabela) throws IOException {
        for (int i = 0; i < nomesInfo.length; i++) {
            String nomeFicheiroCSV = fazerNomeFicheiroComExtensao(h, numPopulacao, numDias, nomesInfo[i], metodoUsado, extensaoDesejada);
            System.out.println(nomeFicheiroCSV);
            calculoMetodoEuler(arrEulerMatrix, numPopulacao, h, steps, matrizParametros[i][2], matrizParametros[i][1], matrizParametros[i][3], matrizParametros[i][0]);
            preencherFicheirosCSVeTabelaMNI(tabela, nomeFicheiroCSV, nomesInfo[i], arrEulerMatrix, numDias, legenda_da_tabela, h);
            System.out.println("grafico" + fazerNomeFicheiroComExtensao(h, numPopulacao, numDias, nomesInfo[i], metodoUsado, extensaoGrafico));
            metodoParaGrafico(metodoUsado, nomeFicheiroCSV, h, numPopulacao, numDias, nomesInfo[i], extensaoGrafico);
        }
    }

    private static void calculoMetodoEuler(double[][] matrizValoresEuler, int numPopulacao, double h, int steps, double ro, double gama,
                                           double alfa, double beta) {
        atribuirValoresIniciaisMatrizValores(matrizValoresEuler, numPopulacao);
        for (int e = 0; e < steps; e++) {
            matrizValoresEuler[0][e + 1] = matrizValoresEuler[0][e] + h * ((-beta) * matrizValoresEuler[0][e] * matrizValoresEuler[1][e]);
            matrizValoresEuler[1][e + 1] = matrizValoresEuler[1][e] + h * ((ro * beta * matrizValoresEuler[0][e] * matrizValoresEuler[1][e]) + ((-gama) * matrizValoresEuler[1][e]) + (alfa * matrizValoresEuler[2][e]));
            matrizValoresEuler[2][e + 1] = matrizValoresEuler[2][e] + h * ((gama * matrizValoresEuler[1][e]) + ((-alfa) * matrizValoresEuler[2][e]) + ((1 - ro) * beta * matrizValoresEuler[0][e] * matrizValoresEuler[1][e]));
            matrizValoresEuler[3][e + 1] = matrizValoresEuler[0][e + 1] + matrizValoresEuler[1][e + 1] + matrizValoresEuler[2][e + 1];
        }
    }

    private static void seguirProcessoRungeKutta(String extensaoGrafico, int metodoUsado, String extensaoCSV, double[][] matrizParametros,
                                                 double[][] matrizK, double[][] matrizValoresRK4, double constanteMediaRk4,
                                                 double constanteMetade, int constanteNula, int numPopulacao, double h, int steps, PrintWriter tabela,
                                                 String[] nomesInfo, int numDias, String[] legendaDaTabela) throws IOException {
        for (int i = 0; i < nomesInfo.length; i++) {
            String nomeFicheiroCSV = fazerNomeFicheiroComExtensao(h, numPopulacao, numDias, nomesInfo[i], metodoUsado, extensaoCSV);
            System.out.println(nomeFicheiroCSV);
            calculoMetodoRungeKutta(matrizK, matrizValoresRK4, h, steps, matrizParametros[i][2], matrizParametros[i][1], matrizParametros[i][3], matrizParametros[i][0], constanteMediaRk4, constanteMetade, constanteNula, numPopulacao);
            preencherFicheirosCSVeTabelaMNI(tabela, nomeFicheiroCSV, nomesInfo[i], matrizValoresRK4, numDias, legendaDaTabela, h);
            System.out.println("grafico" + fazerNomeFicheiroComExtensao(h, numPopulacao, numDias, nomesInfo[i], metodoUsado, extensaoGrafico));
            metodoParaGrafico(metodoUsado, nomeFicheiroCSV, h, numPopulacao, numDias, nomesInfo[i], extensaoGrafico);
        }
    }

    private static void calculoMetodoRungeKutta(double[][] matrizK, double[][] matrizValoresRK4, double h,
                                                int steps, double ro, double gama, double alfa, double beta,
                                                double constanteMedia, double constanteMetade, int constanteNula, int numPopulacao) {
        atribuirValoresIniciaisMatrizValores(matrizValoresRK4, numPopulacao);
        for (int i = 0; i < steps; i++) {
            calculoDoKI(beta, alfa, ro, gama, matrizK, matrizValoresRK4, h, i, constanteNula);
            calculoDoKII(beta, alfa, ro, gama, matrizK, matrizValoresRK4, constanteMetade, h, i);
            calculoDoKIII(beta, alfa, ro, gama, matrizK, matrizValoresRK4, constanteMetade, h, i);
            calculoDoKIIII(beta, alfa, ro, gama, matrizK, matrizValoresRK4, h, i);
            calculoProximosValoresPopulacoes(matrizK, matrizValoresRK4, i, constanteMedia);
        }
    }

    private static void calculoProximosValoresPopulacoes(double[][] matrizK, double[][] matrizValoresRK4, int i, double constanteMedia) {
        for (int j = 0; j < matrizValoresRK4.length - 1; j++) {
            matrizValoresRK4[j][i + 1] = matrizValoresRK4[j][i] + ((matrizK[j][0] + (2 * matrizK[j][1]) + (2 * matrizK[j][2]) + matrizK[j][3]) * constanteMedia);
        }
        matrizValoresRK4[3][i + 1] = matrizValoresRK4[0][i + 1] + matrizValoresRK4[1][i + 1] + matrizValoresRK4[2][i + 1];
    }

    private static void calculoDoKIIII(double beta, double alfa, double ro, double gama, double[][] matrizK, double[][] matrizValoresRK4, double h, int i) {
        matrizK[0][3] = h * calculoEquacaoSuscetiveis(beta, matrizValoresRK4, (matrizK[0][2]), (matrizK[1][2]), i);
        matrizK[1][3] = h * calculoEquacaoInfetados(beta, ro, gama, alfa, matrizValoresRK4, (matrizK[0][2]), (matrizK[1][2]), (matrizK[2][2]), i);
        matrizK[2][3] = h * calculoEquacaoRecuperados(beta, ro, gama, alfa, matrizValoresRK4, (matrizK[0][2]), (matrizK[1][2]), (matrizK[2][2]), i);
    }

    private static void calculoDoKIII(double beta, double alfa, double ro, double gama, double[][] matrizK, double[][] matrizValoresRK4, double metade, double h, int i) {
        matrizK[0][2] = h * calculoEquacaoSuscetiveis(beta, matrizValoresRK4, (matrizK[0][1] * metade), (matrizK[1][1] * metade), i);
        matrizK[1][2] = h * calculoEquacaoInfetados(beta, ro, gama, alfa, matrizValoresRK4, (matrizK[0][1] * metade), (matrizK[1][1] * metade), (matrizK[2][1] * metade), i);
        matrizK[2][2] = h * calculoEquacaoRecuperados(beta, ro, gama, alfa, matrizValoresRK4, (matrizK[0][1] * metade), (matrizK[1][1] * metade), (matrizK[2][1] * metade), i);
    }

    private static void calculoDoKII(double beta, double alfa, double ro, double gama, double[][] matrizK, double[][] matrizValoresRK4, double metade, double h, int i) {
        matrizK[0][1] = h * calculoEquacaoSuscetiveis(beta, matrizValoresRK4, (matrizK[0][0] * metade), (matrizK[1][0] * metade), i);
        matrizK[1][1] = h * calculoEquacaoInfetados(beta, ro, gama, alfa, matrizValoresRK4, (matrizK[0][0] * metade), (matrizK[1][0] * metade), (matrizK[2][0] * metade), i);
        matrizK[2][1] = h * calculoEquacaoRecuperados(beta, ro, gama, alfa, matrizValoresRK4, (matrizK[0][0] * metade), (matrizK[1][0] * metade), (matrizK[2][0] * metade), i);
    }

    private static void calculoDoKI(double beta, double alfa, double ro, double gama, double[][] matrizK, double[][] matrizValoresRK4, double h, int i, int constanteNula) {
        matrizK[0][0] = h * calculoEquacaoSuscetiveis(beta, matrizValoresRK4, constanteNula, constanteNula, i);
        matrizK[1][0] = h * calculoEquacaoInfetados(beta, ro, gama, alfa, matrizValoresRK4, constanteNula, constanteNula, constanteNula, i);
        matrizK[2][0] = h * calculoEquacaoRecuperados(beta, ro, gama, alfa, matrizValoresRK4, constanteNula, constanteNula, constanteNula, i);
    }

    private static double calculoEquacaoRecuperados(double beta, double ro, double gama, double alfa, double[][] matrizValoresRK4, double kS, double kI, double kR, int i) {
        return ((gama * (matrizValoresRK4[1][i] + kI)) + ((-alfa) * (matrizValoresRK4[2][i] + kR)) + ((1 + (-ro)) * beta * (matrizValoresRK4[0][i] + kS) * (matrizValoresRK4[1][i] + kI)));
    }

    private static double calculoEquacaoInfetados(double beta, double ro, double gama, double alfa, double[][] matrizValoresRK4, double kS, double kI, double kR, int i) {
        return ((ro * beta * (matrizValoresRK4[0][i] + kS) * (matrizValoresRK4[1][i] + kI)) + ((-gama) * (matrizValoresRK4[1][i] + kI)) + (alfa * (matrizValoresRK4[2][i] + kR)));
    }

    private static double calculoEquacaoSuscetiveis(double beta, double[][] matrizValoresRK4, double kS, double kI, int i) {
        return ((-beta) * (matrizValoresRK4[0][i] + kS) * (matrizValoresRK4[1][i] + kI));
    }


    private static String fazerNomeFicheiroComExtensao(double h, int numPopulacao, int numDias, String pessoa, int metodoUsado, String extensao) {
        String nomeFicheiro;
        String hString;
        if (h == 1) {
            hString = "1";
        } else {
            hString = Double.toString(h).replace(".", "");
        }

        nomeFicheiro = pessoa + "m" + metodoUsado + "p" + hString + "t" + numPopulacao + "d" + numDias + extensao;
        return nomeFicheiro;
    }

    private static void preencherFicheiroCSV(String nomeficheiro, int avanco, String[] legenda_da_tabela,
                                             double[][] matrizValores, int numDias) throws FileNotFoundException {
        PrintWriter out = new PrintWriter(new File(nomeficheiro));
        String juntarArrays = String.join(";", legenda_da_tabela);
        out.printf(juntarArrays);
        out.println();
        for (int contadorDias = 0; contadorDias <= numDias; contadorDias++) {
            int j = contadorDias * avanco;
            if (matrizValores[0][j] < 0.0001) {
                out.printf("%d;%s;%f;%f;%f%n", contadorDias, numFormat.format(matrizValores[0][j]), matrizValores[1][j], matrizValores[2][j], matrizValores[3][j]);
            } else {
                out.printf("%d;%f;%f;%f;%f%n", contadorDias, matrizValores[0][j], matrizValores[1][j], matrizValores[2][j], matrizValores[3][j]);
            }
        }
        out.close();
    }


    private static void metodoParaGrafico(int escolhaMetodo, String nomeFicheiroOutput, double h, int numPopulacao, int numDias, String pessoaAnalisar, String extensaoGrafico) throws IOException {
        switch (escolhaMetodo) {
            case 1:
                String labelsEuler = "set xlabel 'Número de dias'; set ylabel 'População' ; set title 'Distribuição da falsa notícia (Euler)'";
                String nomeGraficoEuler = "grafico" + fazerNomeFicheiroComExtensao(h, numPopulacao, numDias, pessoaAnalisar, escolhaMetodo, extensaoGrafico);
                fazerGrafico(labelsEuler, nomeFicheiroOutput, nomeGraficoEuler);
                break;

            case 2:
                String labelsRungeKutta = "set xlabel 'Número de dias'; set ylabel 'População' ; set title 'Distribuição da falsa notícia (Runge-Kutta)'";
                String nomeGraficoRK4 = "grafico" + fazerNomeFicheiroComExtensao(h, numPopulacao, numDias, pessoaAnalisar, escolhaMetodo, extensaoGrafico);
                fazerGrafico(labelsRungeKutta, nomeFicheiroOutput, nomeGraficoRK4);
                break;
        }
    }


    private static void fazerGrafico(String labels, String nomeFicheiro, String nomeGrafico) throws IOException {
        String terminal = "pngcairo";
        String separator = "set datafile separator ';'";
        String codigognuplot = "plot '" + nomeFicheiro + "' using 1:2 title 'S' with l lw 2, '" + nomeFicheiro + "' using 1:3 title 'I' with l lw 2, '" + nomeFicheiro + "' using 1:4 title 'R' with l lw 2";
        String comand = "gnuplot -e \"" + labels + "; " + separator + "; set terminal " + terminal + "; set output '" + nomeGrafico + "'; " + codigognuplot + "\"";

        Runtime rt = Runtime.getRuntime();
        rt.exec(comand);

    }
}
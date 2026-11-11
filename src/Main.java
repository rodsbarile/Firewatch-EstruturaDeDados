import estrutura.ListaEncadeada;
import model.OcorrenciaIncendio;
import ordenacao.Bubble;
import ordenacao.Insertion;
import ordenacao.Merge;
import ordenacao.Quick;
import ordenacao.Selection;

private static final Scanner entrada = new Scanner(System.in);

void main() {

    try {

        GerenciarOcorrencias gerenciador =
                new GerenciarOcorrencias();

        int opcao;

        do {

            exibirMenu();

            opcao = lerInteiro();

            try {

                switch (opcao) {

                    case 1:
                        cadastrar(gerenciador);
                        break;

                    case 2:
                        consultar(gerenciador);
                        break;

                    case 3:
                        listar(gerenciador);
                        break;

                    case 4:
                        atualizar(gerenciador);
                        break;

                    case 5:
                        excluir(gerenciador);
                        break;

                    case 6:
                        demonstrarFIFO(gerenciador);
                        break;

                    case 7:
                        demonstrarLIFO(gerenciador);
                        break;

                    case 8:
                        listarCidades(gerenciador);
                        break;

                    case 9:
                        ordenar(gerenciador);
                        break;

                    case 0:
                        IO.println(
                                "Encerrando FireWatch..."
                        );
                        break;

                    default:
                        IO.println("Opção inválida.");
                }

            } catch (IOException | IllegalArgumentException e) {

                IO.println(
                        "Não foi possível realizar a operação: "
                                + e.getMessage()
                );
            }

        } while (opcao != 0);

    } catch (IOException e) {

        IO.println(
                "Erro ao carregar os registros: " + e.getMessage()
        );
    }
}

// MENU PRINCIPAL

private static void exibirMenu() {

    IO.println("\n========== FireWatch ==========");
    IO.println("1 - Cadastrar ocorrência");
    IO.println("2 - Consultar ocorrência");
    IO.println("3 - Listar ocorrências");
    IO.println("4 - Atualizar ocorrência");
    IO.println("5 - Excluir ocorrência");
    IO.println("6 - Demonstrar FIFO");
    IO.println("7 - Demonstrar LIFO");
    IO.println("8 - Listar Hash de cidades");
    IO.println("9 - Ordenar ocorrências");
    IO.println("0 - Sair");
    IO.print("\nEscolha: ");
}

// CREATE

private static void cadastrar(
        GerenciarOcorrencias gerenciador
) throws IOException {

    IO.println("\n=== NOVA OCORRÊNCIA ===");

    OcorrenciaIncendio ocorrencia = lerOcorrencia(null);

    boolean cadastrada = gerenciador.cadastrar(ocorrencia);

    if (cadastrada) {

        IO.println(
                "Ocorrência cadastrada e salva com sucesso!"
        );

    } else {

        IO.println(
                "Já existe uma ocorrência com esse ID."
        );
    }
}

// READ - CONSULTA POR ID

private static void consultar(
        GerenciarOcorrencias gerenciador
) {

    IO.print("Informe o ID: ");

    int id = lerInteiro();

    OcorrenciaIncendio ocorrencia =
            gerenciador.consultar(id);

    if (ocorrencia == null) {

        IO.println("Ocorrência não encontrada.");

    } else {

        IO.println("\n=== OCORRÊNCIA ===");
        IO.println(ocorrencia);
    }
}

// READ - LISTAGEM COMPLETA

private static void listar(
        GerenciarOcorrencias gerenciador
) {

    List<OcorrenciaIncendio> ocorrencias =
            gerenciador.listarTodas();

    IO.println("\n=== OCORRÊNCIAS CADASTRADAS ===");

    if (ocorrencias.isEmpty()) {

        IO.println("Nenhuma ocorrência cadastrada.");
        return;
    }

    for (OcorrenciaIncendio ocorrencia : ocorrencias) {

        IO.println(ocorrencia);
        IO.println("------------------------------");
    }

    IO.println(
            "Total: " + gerenciador.getTotalOcorrencias()
    );
}

// UPDATE

private static void atualizar(
        GerenciarOcorrencias gerenciador
) throws IOException {

    IO.print("Informe o ID da ocorrência: ");

    int id = lerInteiro();

    if (gerenciador.consultar(id) == null) {

        IO.println("Ocorrência não encontrada.");
        return;
    }

    IO.println("\n=== NOVOS DADOS ===");

    OcorrenciaIncendio nova = lerOcorrencia(id);

    boolean atualizada = gerenciador.atualizar(nova);

    if (atualizada) {

        IO.println(
                "Ocorrência atualizada e salva com sucesso!"
        );

    } else {

        IO.println("Não foi possível atualizar a ocorrência.");
    }
}

// DELETE

private static void excluir(
        GerenciarOcorrencias gerenciador
) throws IOException {

    IO.print("Informe o ID da ocorrência: ");

    int id = lerInteiro();

    boolean removida = gerenciador.excluir(id);

    if (removida) {

        IO.println(
                "Ocorrência excluída e arquivo atualizado!"
        );

    } else {

        IO.println("Ocorrência não encontrada.");
    }
}

// FIFO - PRIMEIRO A ENTRAR, PRIMEIRO A SAIR

private static void demonstrarFIFO(
        GerenciarOcorrencias gerenciador
) {

    ListaEncadeada fila =
            gerenciador.criarCopiaProcessamento();

    IO.println("\n=== PROCESSAMENTO FIFO ===");
    IO.println(
            "Primeiro a entrar, primeiro a sair.\n"
    );

    if (fila.estaVazia()) {

        IO.println("Não existem ocorrências para processar.");
        return;
    }

    while (!fila.estaVazia()) {

        OcorrenciaIncendio ocorrencia =
                fila.removerInicio();

        IO.println(
                "Processando ocorrência: "
                        + ocorrencia.getId()
                        + " - "
                        + ocorrencia.getMunicipio()
        );
    }
}

// LIFO - ÚLTIMO A ENTRAR, PRIMEIRO A SAIR

private static void demonstrarLIFO(
        GerenciarOcorrencias gerenciador
) {

    ListaEncadeada pilha =
            gerenciador.criarCopiaProcessamento();

    IO.println("\n=== PROCESSAMENTO LIFO ===");
    IO.println(
            "Último a entrar, primeiro a sair.\n"
    );

    if (pilha.estaVazia()) {

        IO.println("Não existem ocorrências para processar.");
        return;
    }

    while (!pilha.estaVazia()) {

        OcorrenciaIncendio ocorrencia =
                pilha.removerFim();

        IO.println(
                "Processando ocorrência: "
                        + ocorrencia.getId()
                        + " - "
                        + ocorrencia.getMunicipio()
        );
    }
}

// HASH DE CIDADES

private static void listarCidades(
        GerenciarOcorrencias gerenciador
) {

    List<String> cidades = gerenciador.listarCidades();

    IO.println("\n=== HASH DE CIDADES ===");

    if (cidades.isEmpty()) {

        IO.println("Nenhuma cidade cadastrada.");
        return;
    }

    for (String cidade : cidades) {

        IO.println(cidade);
    }

    IO.println(
            "Total de localidades: "
                    + gerenciador.getTotalCidades()
    );
}

// ORDENAÇÃO POR ID

private static void ordenar(
        GerenciarOcorrencias gerenciador
) throws IOException {

    IO.println("\n=== ORDENAÇÃO DE OCORRÊNCIAS ===");
    IO.println("1 - Bubble Sort");
    IO.println("2 - Selection Sort");
    IO.println("3 - Quick Sort");
    IO.println("4 - Merge Sort");
    IO.println("5 - Insertion Sort");
    IO.println("6 - Testar todos os métodos");
    IO.print("Escolha o método: ");

    int metodo = lerInteiro();
    List<OcorrenciaIncendio> ocorrencias =
            gerenciador.listarTodas();

    if (metodo == 6) {
        IO.println("\n=== TESTE DE TODOS OS MÉTODOS ===");

        for (int metodoAtual = 1; metodoAtual <= 5; metodoAtual++) {
            executarOrdenacao(ocorrencias, metodoAtual);
        }

        IO.println("Todos os métodos foram executados com sucesso.");
        return;
    }

    executarOrdenacao(ocorrencias, metodo);
}

private static void executarOrdenacao(
        List<OcorrenciaIncendio> ocorrencias,
        int metodo
) throws IOException {

    long inicio = System.nanoTime();
    List<OcorrenciaIncendio> ordenadas;
    String nomeArquivo;
    String nomeMetodo;

    switch (metodo) {
        case 1:
            ordenadas = Bubble.ordenar(ocorrencias);
            nomeArquivo = "bubble_sort.txt";
            nomeMetodo = "Bubble Sort";
            break;

        case 2:
            ordenadas = Selection.ordenar(ocorrencias);
            nomeArquivo = "selection_sort.txt";
            nomeMetodo = "Selection Sort";
            break;

        case 3:
            ordenadas = Quick.ordenar(ocorrencias);
            nomeArquivo = "quick_sort.txt";
            nomeMetodo = "Quick Sort";
            break;

        case 4:
            ordenadas = Merge.ordenar(ocorrencias);
            nomeArquivo = "merge_sort.txt";
            nomeMetodo = "Merge Sort";
            break;

        case 5:
            ordenadas = Insertion.ordenar(ocorrencias);
            nomeArquivo = "insertion_sort.txt";
            nomeMetodo = "Insertion Sort";
            break;

        default:
            IO.println("Método de ordenação inválido.");
            return;
    }

    long tempoTotalNanos = System.nanoTime() - inicio;
    GerenciarArquivo arquivo = new GerenciarArquivo();
    arquivo.escreverSaida(nomeArquivo, ordenadas);
    arquivo.escreverRelatorio(
            nomeArquivo.replace(".txt", "_relatorio.txt"),
            "Método: " + nomeMetodo + System.lineSeparator()
                    + "Quantidade de registros: " + ocorrencias.size()
                    + System.lineSeparator()
                    + "Tempo total de execução: "
                    + tempoTotalNanos + " ns"
                    + System.lineSeparator()
                    + "Tempo total de execução: "
                    + (tempoTotalNanos / 1_000_000.0) + " ms"
    );

    IO.println(
            "Ocorrências ordenadas com sucesso em dados\\"
                    + nomeArquivo
                    + " e relatório de desempenho em dados\\"
                    + nomeArquivo.replace(".txt", "_relatorio.txt")
    );
}

// LEITURA DOS DADOS DE UMA OCORRÊNCIA.
// null significa que será solicitado um novo ID.
// Um ID informado significa que ele será mantido no UPDATE.

private static OcorrenciaIncendio lerOcorrencia(Integer idFixo) {

    int id;

    if (idFixo == null) {

        IO.print("ID: ");
        id = lerInteiro();

    } else {

        id = idFixo;
    }

    LocalDateTime data = lerDataHora();

    IO.print("Município: ");
    String municipio = entrada.nextLine().trim();

    IO.print("UF: ");
    String uf = entrada.nextLine().trim();

    IO.print("País: ");
    String pais = entrada.nextLine().trim();

    IO.print("Tipo de área: ");
    String tipoArea = entrada.nextLine().trim();

    int nivelRisco = lerNivelRisco();

    IO.print("Status: ");
    String status = entrada.nextLine().trim();

    return new OcorrenciaIncendio(
            id,
            data,
            municipio,
            uf,
            pais,
            tipoArea,
            nivelRisco,
            status
    );
}

// DATA E HORA

private static LocalDateTime lerDataHora() {

    while (true) {

        IO.print(
                "Data e hora (AAAA-MM-DDTHH:MM): "
        );

        String texto = entrada.nextLine().trim();

        try {

            return LocalDateTime.parse(texto);

        } catch (DateTimeParseException e) {

            IO.println(
                    "Data inválida. Exemplo: 2026-11-11T14:30"
            );
        }
    }
}

// NÍVEL DE RISCO

private static int lerNivelRisco() {

    while (true) {

        IO.print("Nível de risco (1 a 5): ");

        int nivel = lerInteiro();

        if (nivel >= 1 && nivel <= 5) {
            return nivel;
        }

        IO.println(
                "O nível de risco deve estar entre 1 e 5."
        );
    }
}

// LEITURA DE NÚMEROS INTEIROS

private static int lerInteiro() {

    while (true) {

        try {

            return Integer.parseInt(
                    entrada.nextLine().trim()
            );

        } catch (NumberFormatException e) {

            IO.print(
                    "Valor inválido. Digite um número inteiro: "
            );
        }
    }
}

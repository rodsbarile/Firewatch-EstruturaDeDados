import estrutura.ListaEncadeada;
import hash.HashPaises;
import model.OcorrenciaIncendio;
import ordenacao.Bubble;
import ordenacao.Heap;
import ordenacao.Insertion;
import ordenacao.Merge;
import ordenacao.Quick;
import ordenacao.Selection;

private static final Scanner entrada = new Scanner(System.in);

void main() {

    try {

        GerenciarOcorrencias gerenciador =
                new GerenciarOcorrencias();
        HashPaises hashPaises = new HashPaises();

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
                        ordenar(gerenciador);
                        break;

                    case 3:
                        pesquisarPorId(gerenciador);
                        break;

                    case 4:
                        listar(gerenciador);
                        break;

                    case 5:
                        atualizar(gerenciador);
                        break;

                    case 6:
                        removerComLIFO(gerenciador);
                        break;

                    case 7:
                        removerComFIFO(gerenciador);
                        break;

                    case 8:
                        listarCidades(gerenciador);
                        break;

                    case 9:
                        gerenciarPaises(hashPaises);
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
    IO.println("2 - Ordenar ocorrências");
    IO.println("3 - Pesquisar por ID");
    IO.println("4 - Listar ocorrências");
    IO.println("5 - Atualizar ocorrência");
    IO.println("6 - Remover com LIFO");
    IO.println("7 - Remover com FIFO");
    IO.println("8 - Listar Hash de cidades");
    IO.println("9 - Gerenciar países e fronteiras");
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

// PESQUISA POR ID

private static void pesquisarPorId(
        GerenciarOcorrencias gerenciador
) throws IOException {

    IO.println("\n=== PESQUISA POR ID ===");
    IO.println("1 - Pesquisa linear");
    IO.println("2 - Pesquisa binária");
    IO.print("Escolha o método: ");

    int metodo = lerInteiro();

    if (metodo != 1 && metodo != 2) {
        IO.println("Método de pesquisa inválido.");
        return;
    }

    IO.println("\n=== LISTA PARA PESQUISA ===");
    IO.println("1 - Lista desordenada (entrada.txt)");
    IO.println("2 - Lista ordenada por ID");
    IO.print("Escolha a lista: ");

    int tipoLista = lerInteiro();

    if (tipoLista != 1 && tipoLista != 2) {
        IO.println("Opção de lista inválida.");
        return;
    }

    if (metodo == 2 && tipoLista == 1) {
        IO.println(
                "A pesquisa binária só pode utilizar a lista ordenada."
        );
        return;
    }

    List<OcorrenciaIncendio> ocorrencias =
            gerenciador.listarTodas();

    if (tipoLista == 2) {
        ocorrencias = Quick.ordenar(ocorrencias);
    }

    IO.print("\nInforme o ID: ");
    int id = lerInteiro();

    long inicio = System.nanoTime();
    OcorrenciaIncendio ocorrencia;

    if (metodo == 1) {
        ocorrencia = pesquisarLinear(ocorrencias, id);
    } else {
        ocorrencia = pesquisarBinaria(ocorrencias, id);
    }

    long tempoTotalNanos = System.nanoTime() - inicio;
    String nomeMetodo =
            metodo == 1 ? "Pesquisa Linear" : "Pesquisa Binária";
    String nomeLista =
            tipoLista == 1 ? "Desordenada" : "Ordenada";

    GerenciarArquivo arquivo = new GerenciarArquivo();
    String nomeRelatorio = metodo == 1
            ? "pesquisa_linear_relatorio.txt"
            : "pesquisa_binaria_relatorio.txt";

    arquivo.escreverRelatorio(
            nomeRelatorio,
            "Método: " + nomeMetodo + System.lineSeparator()
                    + "Lista utilizada: " + nomeLista
                    + System.lineSeparator()
                    + "Quantidade de registros: "
                    + ocorrencias.size()
                    + System.lineSeparator()
                    + "Tempo total de execução: "
                    + tempoTotalNanos + " ns"
                    + System.lineSeparator()
                    + "Tempo total de execução: "
                    + (tempoTotalNanos / 1_000_000.0) + " ms"
    );

    if (ocorrencia == null) {
        IO.println("Ocorrência não encontrada.");
    } else {
        IO.println("\n=== OCORRÊNCIA ENCONTRADA ===");
        IO.println(ocorrencia);
    }

    IO.println(
            "Relatório de pesquisa gerado em dados\\relatorio\\"
                    + nomeRelatorio
    );
}

private static OcorrenciaIncendio pesquisarLinear(
        List<OcorrenciaIncendio> ocorrencias,
        int id
) {
    for (OcorrenciaIncendio ocorrencia : ocorrencias) {
        if (ocorrencia.getId() == id) {
            return ocorrencia;
        }
    }

    return null;
}

private static OcorrenciaIncendio pesquisarBinaria(
        List<OcorrenciaIncendio> ocorrencias,
        int id
) {
    int inicio = 0;
    int fim = ocorrencias.size() - 1;

    while (inicio <= fim) {
        int meio = inicio + (fim - inicio) / 2;
        OcorrenciaIncendio ocorrencia = ocorrencias.get(meio);

        if (ocorrencia.getId() == id) {
            return ocorrencia;
        }

        if (ocorrencia.getId() < id) {
            inicio = meio + 1;
        } else {
            fim = meio - 1;
        }
    }

    return null;
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

// DELETE COM LIFO - ÚLTIMO A ENTRAR, PRIMEIRO A SAIR

private static void removerComLIFO(
        GerenciarOcorrencias gerenciador
) throws IOException {

    ListaEncadeada pilha =
            gerenciador.criarCopiaProcessamento();

    OcorrenciaIncendio ocorrencia = pilha.consultarFim();

    if (ocorrencia == null) {
        IO.println("Não existem ocorrências para remover.");
        return;
    }

    if (!confirmarRemocao(ocorrencia)) {
        IO.println("Remoção cancelada.");
        return;
    }

    gerenciador.excluir(ocorrencia.getId());

    IO.println("Ocorrência removida com LIFO e arquivo atualizado!");
}

// DELETE COM FIFO - PRIMEIRO A ENTRAR, PRIMEIRO A SAIR

private static void removerComFIFO(
        GerenciarOcorrencias gerenciador
) throws IOException {

    ListaEncadeada fila =
            gerenciador.criarCopiaProcessamento();

    OcorrenciaIncendio ocorrencia = fila.consultarInicio();

    if (ocorrencia == null) {
        IO.println("Não existem ocorrências para remover.");
        return;
    }

    if (!confirmarRemocao(ocorrencia)) {
        IO.println("Remoção cancelada.");
        return;
    }

    gerenciador.excluir(ocorrencia.getId());

    IO.println("Ocorrência removida com FIFO e arquivo atualizado!");
}

private static boolean confirmarRemocao(
        OcorrenciaIncendio ocorrencia
) {
    IO.println("\n=== CONFIRMAÇÃO DE REMOÇÃO ===");
    IO.println("Registro que será excluído:");
    IO.println(ocorrencia);

    while (true) {
        IO.print("Confirma a exclusão? (S/N): ");
        String resposta = entrada.nextLine().trim();

        if (resposta.equalsIgnoreCase("S")) {
            return true;
        }

        if (resposta.equalsIgnoreCase("N")) {
            return false;
        }

        IO.println("Resposta inválida. Digite S para sim ou N para não.");
    }
}

// HASH DE PAÍSES E FRONTEIRAS

private static void gerenciarPaises(
        HashPaises hashPaises
) {
    int opcao;

    do {
        IO.println("\n=== PAÍSES E FRONTEIRAS ===");
        IO.println("1 - Cadastrar país");
        IO.println("2 - Cadastrar fronteira");
        IO.println("3 - Listar países");
        IO.println("4 - Listar fronteiras de um país");
        IO.println("5 - Listar países e suas fronteiras");
        IO.println("6 - Adicionar países e fronteiras de teste");
        IO.println("0 - Voltar");
        IO.print("Escolha: ");

        opcao = lerInteiro();

        switch (opcao) {
            case 1:
                IO.print("Nome do país: ");
                String pais = entrada.nextLine().trim();

                if (hashPaises.inserirPais(pais)) {
                    IO.println("País cadastrado com sucesso.");
                } else {
                    IO.println("Esse país já está cadastrado.");
                }
                break;

            case 2:
                IO.print("Primeiro país: ");
                String primeiro = entrada.nextLine().trim();
                IO.print("Segundo país: ");
                String segundo = entrada.nextLine().trim();

                if (hashPaises.inserirFronteira(primeiro, segundo)) {
                    IO.println("Fronteira cadastrada nos dois países.");
                } else {
                    IO.println("Essa fronteira já estava cadastrada.");
                }
                break;

            case 3:
                listarPaises(hashPaises);
                break;

            case 4:
                IO.print("País: ");
                listarFronteiras(hashPaises, entrada.nextLine().trim());
                break;

            case 5:
                listarPaisesComFronteiras(hashPaises);
                break;

            case 6:
                hashPaises.inserirDadosTeste();
                IO.println(
                        "Países e fronteiras de teste adicionados."
                );
                break;

            case 0:
                break;

            default:
                IO.println("Opção inválida.");
        }
    } while (opcao != 0);
}

private static void listarPaises(HashPaises hashPaises) {
    List<String> paises = hashPaises.listarPaises();

    IO.println("\n=== PAÍSES CADASTRADOS ===");
    if (paises.isEmpty()) {
        IO.println("Nenhum país cadastrado.");
        return;
    }

    for (String pais : paises) {
        IO.println(pais);
    }
}

private static void listarFronteiras(
        HashPaises hashPaises,
        String pais
) {
    List<String> fronteiras = hashPaises.listarFronteiras(pais);

    IO.println("\n=== FRONTEIRAS DE " + pais + " ===");
    if (fronteiras.isEmpty()) {
        IO.println("Esse país não possui fronteiras cadastradas.");
        return;
    }

    for (String fronteira : fronteiras) {
        IO.println(fronteira);
    }
}

private static void listarPaisesComFronteiras(
        HashPaises hashPaises
) {
    List<String> paises =
            hashPaises.listarPaisesComFronteiras();

    IO.println("\n=== PAÍSES E FRONTEIRAS ===");
    if (paises.isEmpty()) {
        IO.println("Nenhum país cadastrado.");
        return;
    }

    for (String pais : paises) {
        IO.println(pais);
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
    IO.println("6 - Heap Sort");
    IO.println("7 - Testar todos os métodos");
    IO.print("Escolha o método: ");

    int metodo = lerInteiro();
    List<OcorrenciaIncendio> ocorrencias =
            gerenciador.listarTodas();

    if (metodo == 7) {
        IO.println("\n=== TESTE DE TODOS OS MÉTODOS ===");

        for (int metodoAtual = 1; metodoAtual <= 6; metodoAtual++) {
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

        case 6:
            ordenadas = Heap.ordenar(ocorrencias);
            nomeArquivo = "heap_sort.txt";
            nomeMetodo = "Heap Sort";
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

        id = lerId();

    } else {

        id = idFixo;
    }

    LocalDateTime data = lerDataHora();

    String municipio = lerTextoObrigatorio("Município");
    String uf = lerUf();
    String pais = lerTextoObrigatorio("País");
    String tipoArea = lerOpcao(
            "Tipo de área",
            "FLORESTA",
            "MATA",
            "PASTAGEM",
            "AREA_RURAL",
            "AREA_PROTEGIDA"
    );

    int nivelRisco = lerNivelRisco();

    String status = lerOpcao(
            "Status",
            "REGISTRADA",
            "EM_ATENDIMENTO",
            "CONTROLADA",
            "ENCERRADA"
    );

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

private static int lerId() {
    while (true) {
        IO.print("ID: ");
        int id = lerInteiro();

        if (id > 0) {
            return id;
        }

        IO.println("O ID deve ser um número inteiro positivo.");
    }
}

private static String lerTextoObrigatorio(String campo) {
    while (true) {
        IO.print(campo + ": ");
        String valor = entrada.nextLine().trim();

        if (!valor.isEmpty() && !valor.contains("|")) {
            return valor;
        }

        IO.println(
                campo + " é obrigatório e não pode conter o caractere |."
        );
    }
}

private static String lerUf() {
    while (true) {
        IO.print("UF: ");
        String uf = entrada.nextLine().trim().toUpperCase(Locale.ROOT);

        if (uf.matches("[A-Z]{2}")) {
            return uf;
        }

        IO.println("A UF deve conter exatamente duas letras.");
    }
}

private static String lerOpcao(
        String campo,
        String... opcoes
) {
    while (true) {
        IO.print(campo + ": ");
        String valor = entrada.nextLine().trim()
                .toUpperCase(Locale.ROOT);

        for (String opcao : opcoes) {
            if (opcao.equals(valor)) {
                return valor;
            }
        }

        IO.println(
                campo + " inválido. Opções aceitas: "
                        + String.join(", ", opcoes)
        );
    }
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

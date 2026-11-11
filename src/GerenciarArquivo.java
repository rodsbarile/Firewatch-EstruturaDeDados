
import model.OcorrenciaIncendio;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GerenciarArquivo {

    private final Path caminhoArquivo;

    // Inicializa utilizando dados/entrada.txt.
    public GerenciarArquivo() {
        this(resolverCaminhoPadrao());
    }

    // Permite informar outro caminho, útil para testes.
    public GerenciarArquivo(Path caminhoArquivo) {

        if (caminhoArquivo == null) {
            throw new IllegalArgumentException("Caminho inválido.");
        }

        this.caminhoArquivo = caminhoArquivo
                .toAbsolutePath()
                .normalize();
    }

    // Resolve o caminho correto do arquivo.
    private static Path resolverCaminhoPadrao() {

        Path diretorio = Path.of(
                System.getProperty("user.dir")
        ).toAbsolutePath().normalize();

        // Caso a execução esteja na raiz do projeto.
        return diretorio.resolve("dados").resolve("entrada.txt");
    }

    // Cria a pasta e o arquivo, caso ainda não existam.
    private void criarSeNaoExistir() throws IOException {

        Path pasta = caminhoArquivo.getParent();

        if (pasta != null) {
            Files.createDirectories(pasta);
        }

        if (Files.notExists(caminhoArquivo)) {
            Files.createFile(caminhoArquivo);
        }
    }

    // LÊ TODOS OS REGISTROS DO ARQUIVO.
    public List<OcorrenciaIncendio> lerOcorrencias()
            throws IOException {

        criarSeNaoExistir();

        List<OcorrenciaIncendio> ocorrencias = new ArrayList<>();

        try (BufferedReader leitor = Files.newBufferedReader(
                caminhoArquivo,
                StandardCharsets.UTF_8
        )) {

            String linha;
            int numeroLinha = 0;

            while ((linha = leitor.readLine()) != null) {

                numeroLinha++;

                if (linha.isBlank()) {
                    continue;
                }

                try {

                    OcorrenciaIncendio ocorrencia =
                            OcorrenciaIncendio.de_linha_arquivo(linha);

                    ocorrencias.add(ocorrencia);

                } catch (RuntimeException e) {

                    throw new IOException(
                            "Erro na linha " + numeroLinha
                                    + ": " + e.getMessage(),
                            e
                    );
                }
            }
        }

        return ocorrencias;
    }

    // SALVA TODOS OS REGISTROS NO ARQUIVO PRINCIPAL.
    // Sobrescreve o conteúdo anterior, evitando duplicações.
    public void salvarOcorrencias(
            Iterable<OcorrenciaIncendio> ocorrencias
    ) throws IOException {

        if (ocorrencias == null) {
            throw new IllegalArgumentException("Lista inválida.");
        }

        List<String> linhas = converterParaLinhas(ocorrencias);

        criarSeNaoExistir();

        gravarAtomico(caminhoArquivo, linhas);
    }

    // ESCREVE O RESULTADO DE UMA ORDENAÇÃO EM OUTRO ARQUIVO.
    // Exemplo: escreverSaida("merge_sort", listaOrdenada);
    public void escreverSaida(
            String nomeArquivo,
            Iterable<OcorrenciaIncendio> ocorrencias
    ) throws IOException {

        if (nomeArquivo == null || nomeArquivo.isBlank()) {
            throw new IllegalArgumentException(
                    "Nome do arquivo inválido."
            );
        }

        if (ocorrencias == null) {
            throw new IllegalArgumentException("Lista inválida.");
        }

        List<String> linhas = converterParaLinhas(ocorrencias);

        Path pastaSaida = caminhoArquivo
                .getParent()
                .resolve("saida")
                .normalize();

        String nome = nomeArquivo.trim();

        Path componente = Path.of(nome);

        // Impede que o nome informado acesse outras pastas.
        if (componente.isAbsolute()
                || componente.getNameCount() != 1
                || nome.equals(".")
                || nome.equals("..")) {

            throw new IllegalArgumentException(
                    "Informe apenas o nome do arquivo."
            );
        }

        if (!nome.toLowerCase(Locale.ROOT).endsWith(".txt")) {
            nome += ".txt";
        }

        Files.createDirectories(pastaSaida);

        Path destino = pastaSaida.resolve(nome).normalize();

        if (!destino.getParent().equals(pastaSaida)) {
            throw new IllegalArgumentException(
                    "Caminho de saída inválido."
            );
        }

        gravarAtomico(destino, linhas);
    }

    // CONVERTE AS OCORRÊNCIAS PARA O FORMATO DO TXT.
    private List<String> converterParaLinhas(
            Iterable<OcorrenciaIncendio> ocorrencias
    ) {

        List<String> linhas = new ArrayList<>();

        for (OcorrenciaIncendio ocorrencia : ocorrencias) {

            if (ocorrencia == null) {
                throw new IllegalArgumentException(
                        "A lista possui uma ocorrência inválida."
                );
            }

            linhas.add(ocorrencia.para_linha_arquivo());
        }

        return linhas;
    }

    // GRAVA EM UM TEMPORÁRIO E DEPOIS SUBSTITUI O ARQUIVO.
    private void gravarAtomico(
            Path destino,
            List<String> linhas
    ) throws IOException {

        Path pasta = destino.getParent();

        if (pasta != null) {
            Files.createDirectories(pasta);
        }

        Path temporario = destino.resolveSibling(
                destino.getFileName().toString() + ".tmp"
        );

        try {

            Files.write(
                    temporario,
                    linhas,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE
            );

            try {

                Files.move(
                        temporario,
                        destino,
                        StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE
                );

            } catch (AtomicMoveNotSupportedException e) {

                Files.move(
                        temporario,
                        destino,
                        StandardCopyOption.REPLACE_EXISTING
                );
            }

        } finally {

            Files.deleteIfExists(temporario);
        }
    }

    // RETORNA O CAMINHO DO ARQUIVO PRINCIPAL.
    public Path getCaminhoArquivo() {
        return caminhoArquivo;
    }
}

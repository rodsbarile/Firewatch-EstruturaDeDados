
package hash;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HashCidades {

    private static final int TAMANHO = 26;

    // Cada posição representa uma letra do alfabeto
    private final NoCidade[] tabela;

    private int quantidade;

    // Nó da lista encadeada interna de cada posição
    private static class NoCidade {

        String municipio;
        String uf;
        String municipioNormalizado;
        NoCidade proximo;

        public NoCidade(String municipio, String uf) {

            this.municipio = municipio.trim();
            this.uf = uf.trim().toUpperCase(Locale.ROOT);

            this.municipioNormalizado =
                    normalizar(this.municipio);

            this.proximo = null;
        }
    }

    public HashCidades() {

        tabela = new NoCidade[TAMANHO];
        quantidade = 0;
    }

    // REMOVE ACENTOS E PADRONIZA O TEXTO
    private static String normalizar(String texto) {

        String normalizado = Normalizer.normalize(
                texto.trim(),
                Normalizer.Form.NFD
        );

        return normalizado
                .replaceAll("\\p{M}", "")
                .toUpperCase(Locale.ROOT);
    }

    // DETERMINA EM QUAL ÍNDICE A CIDADE SERÁ ARMAZENADA
    public int hash(String municipio) {

        if (municipio == null || municipio.isBlank()) {
            throw new IllegalArgumentException(
                    "Nome do município inválido."
            );
        }

        String nome = normalizar(municipio);

        char primeiraLetra = nome.charAt(0);

        if (primeiraLetra < 'A' || primeiraLetra > 'Z') {
            throw new IllegalArgumentException(
                    "O município deve começar com uma letra de A até Z."
            );
        }

        return primeiraLetra - 'A';
    }

    // COMPARA PRIMEIRO O MUNICÍPIO E DEPOIS A UF
    private int comparar(
            String municipioA,
            String ufA,
            String municipioB,
            String ufB
    ) {

        int comparacao = normalizar(municipioA)
                .compareTo(normalizar(municipioB));

        if (comparacao != 0) {
            return comparacao;
        }

        return normalizar(ufA).compareTo(normalizar(ufB));
    }

    // INSERE A CIDADE NA POSIÇÃO CORRETA DO BUCKET
    public boolean inserir(String municipio, String uf) {

        if (municipio == null || municipio.isBlank()
                || uf == null || uf.isBlank()) {

            throw new IllegalArgumentException(
                    "Município e UF são obrigatórios."
            );
        }

        if (municipio.contains("|") || uf.contains("|")) {
            throw new IllegalArgumentException(
                    "Os campos não podem conter o caractere |."
            );
        }

        if (!uf.trim().matches("[a-zA-Z]{2}")) {
            throw new IllegalArgumentException(
                    "UF deve conter duas letras."
            );
        }

        int indice = hash(municipio);

        NoCidade novo = new NoCidade(municipio, uf);

        NoCidade atual = tabela[indice];
        NoCidade anterior = null;

        // Percorre até encontrar a posição correta
        while (atual != null &&
                comparar(
                        atual.municipio,
                        atual.uf,
                        novo.municipio,
                        novo.uf
                ) < 0) {

            anterior = atual;
            atual = atual.proximo;
        }

        // Verifica se a cidade já existe
        if (atual != null &&
                comparar(
                        atual.municipio,
                        atual.uf,
                        novo.municipio,
                        novo.uf
                ) == 0) {

            return false;
        }

        // Insere no início do bucket
        if (anterior == null) {

            novo.proximo = tabela[indice];
            tabela[indice] = novo;

        } else {

            novo.proximo = atual;
            anterior.proximo = novo;
        }

        quantidade++;

        return true;
    }

    // PESQUISA UMA CIDADE POR MUNICÍPIO E UF
    public boolean buscar(String municipio, String uf) {

        int indice = hash(municipio);

        if (uf == null || uf.isBlank()) {
            throw new IllegalArgumentException("UF inválida.");
        }

        NoCidade atual = tabela[indice];

        while (atual != null) {

            int comparacao = comparar(
                    atual.municipio,
                    atual.uf,
                    municipio,
                    uf
            );

            if (comparacao == 0) {
                return true;
            }

            // Como o bucket está ordenado,
            // não precisamos percorrer o restante
            if (comparacao > 0) {
                return false;
            }

            atual = atual.proximo;
        }

        return false;
    }

    // REMOVE UMA CIDADE PELO MUNICÍPIO E UF
    public boolean remover(String municipio, String uf) {

        int indice = hash(municipio);

        if (uf == null || uf.isBlank()) {
            throw new IllegalArgumentException("UF inválida.");
        }

        NoCidade atual = tabela[indice];
        NoCidade anterior = null;

        while (atual != null) {

            int comparacao = comparar(
                    atual.municipio,
                    atual.uf,
                    municipio,
                    uf
            );

            if (comparacao == 0) {

                if (anterior == null) {
                    tabela[indice] = atual.proximo;
                } else {
                    anterior.proximo = atual.proximo;
                }

                quantidade--;

                return true;
            }

            if (comparacao > 0) {
                return false;
            }

            anterior = atual;
            atual = atual.proximo;
        }

        return false;
    }

    // RETORNA TODOS OS ÍNDICES DA HASH EM ORDEM
    public List<String> listar() {

        List<String> resultado = new ArrayList<>();

        for (int i = 0; i < TAMANHO; i++) {

            char letra = (char) ('A' + i);

            NoCidade atual = tabela[i];

            if (atual == null) {

                resultado.add(
                        "[" + i + " - " + letra + "] -> Vazio"
                );

                continue;
            }

            StringBuilder linha = new StringBuilder();

            linha.append("[").append(i)
                    .append(" - ").append(letra)
                    .append("] -> ");

            while (atual != null) {

                linha.append(atual.municipio)
                        .append(" - ")
                        .append(atual.uf);

                atual = atual.proximo;

                if (atual != null) {
                    linha.append(" -> ");
                }
            }

            resultado.add(linha.toString());
        }

        return resultado;
    }

    // QUANTIDADE DE LOCALIDADES ARMAZENADAS
    public int getQuantidade() {
        return quantidade;
    }
}

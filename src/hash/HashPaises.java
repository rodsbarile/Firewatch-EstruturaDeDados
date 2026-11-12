package hash;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HashPaises {

    private static final int TAMANHO = 26;
    private final NoPais[] tabela = new NoPais[TAMANHO];
    private int quantidade;

    private static class NoPais {
        String nome;
        String nomeNormalizado;
        List<String> fronteiras = new ArrayList<>();
        NoPais proximo;

        NoPais(String nome) {
            this.nome = nome.trim();
            this.nomeNormalizado = normalizar(this.nome);
        }
    }

    private static String normalizar(String texto) {
        return Normalizer.normalize(
                        texto.trim(),
                        Normalizer.Form.NFD
                )
                .replaceAll("\\p{M}", "")
                .toUpperCase(Locale.ROOT);
    }

    public int hash(String nome) {
        validarNome(nome);

        char primeiraLetra = normalizar(nome).charAt(0);
        if (primeiraLetra < 'A' || primeiraLetra > 'Z') {
            throw new IllegalArgumentException(
                    "O país deve começar com uma letra de A até Z."
            );
        }

        return primeiraLetra - 'A';
    }

    public boolean inserirPais(String nome) {
        int indice = hash(nome);
        String nomeNormalizado = normalizar(nome);
        NoPais atual = tabela[indice];
        NoPais anterior = null;

        while (atual != null
                && atual.nomeNormalizado.compareTo(nomeNormalizado) < 0) {
            anterior = atual;
            atual = atual.proximo;
        }

        if (atual != null
                && atual.nomeNormalizado.equals(nomeNormalizado)) {
            return false;
        }

        NoPais novo = new NoPais(nome);
        novo.proximo = atual;
        if (anterior == null) {
            tabela[indice] = novo;
        } else {
            anterior.proximo = novo;
        }

        quantidade++;
        return true;
    }

    public boolean inserirFronteira(String primeiro, String segundo) {
        validarNome(primeiro);
        validarNome(segundo);

        if (normalizar(primeiro).equals(normalizar(segundo))) {
            throw new IllegalArgumentException(
                    "Um país não pode fazer fronteira consigo mesmo."
            );
        }

        NoPais paisA = encontrar(primeiro);
        NoPais paisB = encontrar(segundo);

        if (paisA == null || paisB == null) {
            throw new IllegalArgumentException(
                    "Cadastre os dois países antes de criar a fronteira."
            );
        }

        boolean inserida = adicionarFronteira(paisA, paisB.nome);
        adicionarFronteira(paisB, paisA.nome);
        return inserida;
    }

    private boolean adicionarFronteira(NoPais pais, String fronteira) {
        String normalizada = normalizar(fronteira);
        for (String existente : pais.fronteiras) {
            if (normalizar(existente).equals(normalizada)) {
                return false;
            }
        }

        int indice = 0;
        while (indice < pais.fronteiras.size()
                && normalizar(pais.fronteiras.get(indice))
                .compareTo(normalizada) < 0) {
            indice++;
        }

        pais.fronteiras.add(indice, fronteira);
        return true;
    }

    public List<String> listarPaises() {
        List<String> resultado = new ArrayList<>();
        for (int indice = 0; indice < TAMANHO; indice++) {
            for (NoPais atual = tabela[indice];
                 atual != null;
                 atual = atual.proximo) {
                resultado.add(atual.nome);
            }
        }
        return resultado;
    }

    public List<String> listarFronteiras(String nome) {
        NoPais pais = encontrar(nome);
        if (pais == null) {
            throw new IllegalArgumentException("País não cadastrado.");
        }
        return new ArrayList<>(pais.fronteiras);
    }

    public List<String> listarPaisesComFronteiras() {
        List<String> resultado = new ArrayList<>();

        for (String pais : listarPaises()) {
            NoPais registro = encontrar(pais);
            StringBuilder linha = new StringBuilder();

            linha.append("[")
                    .append(pais)
                    .append("] -> ");

            if (registro.fronteiras.isEmpty()) {
                linha.append("Nenhuma fronteira cadastrada");
            } else {
                for (int indice = 0;
                     indice < registro.fronteiras.size();
                     indice++) {
                    if (indice > 0) {
                        linha.append(", ");
                    }
                    linha.append(registro.fronteiras.get(indice));
                }
            }

            resultado.add(linha.toString());
        }

        return resultado;
    }

    public void inserirDadosTeste() {
        String[] paises = {
                "Brasil",
                "Argentina",
                "Bolívia",
                "Colômbia",
                "Guiana",
                "Paraguai",
                "Peru",
                "Suriname",
                "Uruguai",
                "Venezuela",
                "França"
        };

        for (String pais : paises) {
            inserirPais(pais);
        }

        String[] fronteiras = {
                "Argentina",
                "Bolívia",
                "Colômbia",
                "Guiana",
                "Paraguai",
                "Peru",
                "Suriname",
                "Uruguai",
                "Venezuela",
                "França"
        };

        for (String fronteira : fronteiras) {
            inserirFronteira("Brasil", fronteira);
        }
    }

    public int getQuantidade() {
        return quantidade;
    }

    private NoPais encontrar(String nome) {
        int indice = hash(nome);
        String normalizado = normalizar(nome);

        for (NoPais atual = tabela[indice];
             atual != null;
             atual = atual.proximo) {
            if (atual.nomeNormalizado.equals(normalizado)) {
                return atual;
            }
        }
        return null;
    }

    private void validarNome(String nome) {
        if (nome == null || nome.isBlank() || nome.contains("|")) {
            throw new IllegalArgumentException("Nome de país inválido.");
        }
    }
}

package ordenacao;

import model.OcorrenciaIncendio;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class Merge {

    private Merge() {
    }

    public static List<OcorrenciaIncendio> ordenar(
            List<OcorrenciaIncendio> ocorrencias
    ) {
        Objects.requireNonNull(ocorrencias);

        List<OcorrenciaIncendio> resultado =
                new ArrayList<>(ocorrencias);

        ordenar(resultado, 0, resultado.size() - 1);
        return resultado;
    }

    private static void ordenar(
            List<OcorrenciaIncendio> ocorrencias,
            int inicio,
            int fim
    ) {
        if (inicio >= fim) {
            return;
        }

        int meio = inicio + (fim - inicio) / 2;
        ordenar(ocorrencias, inicio, meio);
        ordenar(ocorrencias, meio + 1, fim);
        intercalar(ocorrencias, inicio, meio, fim);
    }

    private static void intercalar(
            List<OcorrenciaIncendio> ocorrencias,
            int inicio,
            int meio,
            int fim
    ) {
        List<OcorrenciaIncendio> temporaria = new ArrayList<>();
        int esquerda = inicio;
        int direita = meio + 1;

        while (esquerda <= meio && direita <= fim) {
            if (ocorrencias.get(esquerda).getId()
                    <= ocorrencias.get(direita).getId()) {
                temporaria.add(ocorrencias.get(esquerda++));
            } else {
                temporaria.add(ocorrencias.get(direita++));
            }
        }

        while (esquerda <= meio) {
            temporaria.add(ocorrencias.get(esquerda++));
        }

        while (direita <= fim) {
            temporaria.add(ocorrencias.get(direita++));
        }

        for (int indice = 0; indice < temporaria.size(); indice++) {
            ocorrencias.set(inicio + indice, temporaria.get(indice));
        }
    }
}

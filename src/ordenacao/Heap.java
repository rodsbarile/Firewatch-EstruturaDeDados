package ordenacao;

import model.OcorrenciaIncendio;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class Heap {

    private Heap() {
    }

    public static List<OcorrenciaIncendio> ordenar(
            List<OcorrenciaIncendio> ocorrencias
    ) {
        Objects.requireNonNull(ocorrencias);

        List<OcorrenciaIncendio> resultado =
                new ArrayList<>(ocorrencias);

        for (int indice = resultado.size() / 2 - 1;
             indice >= 0;
             indice--) {
            ajustarHeap(resultado, resultado.size(), indice);
        }

        for (int fim = resultado.size() - 1; fim > 0; fim--) {
            trocar(resultado, 0, fim);
            ajustarHeap(resultado, fim, 0);
        }

        return resultado;
    }

    private static void ajustarHeap(
            List<OcorrenciaIncendio> ocorrencias,
            int tamanho,
            int raiz
    ) {
        int maior = raiz;
        int esquerdo = 2 * raiz + 1;
        int direito = 2 * raiz + 2;

        if (esquerdo < tamanho
                && ocorrencias.get(esquerdo).getId()
                > ocorrencias.get(maior).getId()) {
            maior = esquerdo;
        }

        if (direito < tamanho
                && ocorrencias.get(direito).getId()
                > ocorrencias.get(maior).getId()) {
            maior = direito;
        }

        if (maior != raiz) {
            trocar(ocorrencias, raiz, maior);
            ajustarHeap(ocorrencias, tamanho, maior);
        }
    }

    private static void trocar(
            List<OcorrenciaIncendio> ocorrencias,
            int primeiro,
            int segundo
    ) {
        OcorrenciaIncendio temporaria = ocorrencias.get(primeiro);
        ocorrencias.set(primeiro, ocorrencias.get(segundo));
        ocorrencias.set(segundo, temporaria);
    }
}

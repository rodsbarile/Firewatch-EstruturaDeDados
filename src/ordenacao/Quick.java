package ordenacao;

import model.OcorrenciaIncendio;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class Quick {

    private Quick() {
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

        int pivo = particionar(ocorrencias, inicio, fim);
        ordenar(ocorrencias, inicio, pivo - 1);
        ordenar(ocorrencias, pivo + 1, fim);
    }

    private static int particionar(
            List<OcorrenciaIncendio> ocorrencias,
            int inicio,
            int fim
    ) {
        int idPivo = ocorrencias.get(fim).getId();
        int menor = inicio;

        for (int indice = inicio; indice < fim; indice++) {
            if (ocorrencias.get(indice).getId() <= idPivo) {
                trocar(ocorrencias, menor, indice);
                menor++;
            }
        }

        trocar(ocorrencias, menor, fim);
        return menor;
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

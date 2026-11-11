package ordenacao;

import model.OcorrenciaIncendio;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class Insertion {

    private Insertion() {
    }

    public static List<OcorrenciaIncendio> ordenar(
            List<OcorrenciaIncendio> ocorrencias
    ) {
        Objects.requireNonNull(ocorrencias);

        List<OcorrenciaIncendio> resultado =
                new ArrayList<>(ocorrencias);

        for (int indice = 1; indice < resultado.size(); indice++) {
            OcorrenciaIncendio atual = resultado.get(indice);
            int posicao = indice - 1;

            while (posicao >= 0
                    && resultado.get(posicao).getId() > atual.getId()) {
                resultado.set(posicao + 1, resultado.get(posicao));
                posicao--;
            }

            resultado.set(posicao + 1, atual);
        }

        return resultado;
    }
}

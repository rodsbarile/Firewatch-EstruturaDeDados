package ordenacao;

import model.OcorrenciaIncendio;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class Bubble {

    private Bubble() {
    }

    public static List<OcorrenciaIncendio> ordenar(
            List<OcorrenciaIncendio> ocorrencias
    ) {
        Objects.requireNonNull(ocorrencias);

        List<OcorrenciaIncendio> resultado =
                new ArrayList<>(ocorrencias);

        for (int fim = resultado.size() - 1; fim > 0; fim--) {
            boolean trocou = false;

            for (int indice = 0; indice < fim; indice++) {
                if (resultado.get(indice).getId()
                        > resultado.get(indice + 1).getId()) {

                    OcorrenciaIncendio temporaria = resultado.get(indice);
                    resultado.set(indice, resultado.get(indice + 1));
                    resultado.set(indice + 1, temporaria);
                    trocou = true;
                }
            }

            if (!trocou) {
                break;
            }
        }

        return resultado;
    }
}

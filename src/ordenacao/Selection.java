package ordenacao;

import model.OcorrenciaIncendio;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class Selection {

    private Selection() {
    }

    public static List<OcorrenciaIncendio> ordenar(
            List<OcorrenciaIncendio> ocorrencias
    ) {
        Objects.requireNonNull(ocorrencias);

        List<OcorrenciaIncendio> resultado =
                new ArrayList<>(ocorrencias);

        for (int inicio = 0; inicio < resultado.size() - 1; inicio++) {
            int menor = inicio;

            for (int indice = inicio + 1;
                 indice < resultado.size();
                 indice++) {

                if (resultado.get(indice).getId()
                        < resultado.get(menor).getId()) {
                    menor = indice;
                }
            }

            if (menor != inicio) {
                OcorrenciaIncendio temporaria = resultado.get(inicio);
                resultado.set(inicio, resultado.get(menor));
                resultado.set(menor, temporaria);
            }
        }

        return resultado;
    }
}

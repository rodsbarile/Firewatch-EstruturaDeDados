
package model;

import java.time.LocalDateTime;
import java.util.Locale;

public class OcorrenciaIncendio {

    private final int id;
    private final LocalDateTime data_hora_registro;
    private final String municipio;
    private final String uf;
    private final String pais;
    private final String tipo_area;
    private final int nivel_risco;
    private final String status;

    public OcorrenciaIncendio(
            int id,
            LocalDateTime data_hora_registro,
            String municipio,
            String uf,
            String pais,
            String tipo_area,
            int nivel_risco,
            String status
    ) {

        if (id <= 0) {
            throw new IllegalArgumentException("ID inválido.");
        }

        if (data_hora_registro == null) {
            throw new IllegalArgumentException("Data inválida.");
        }

        if (nivel_risco < 1 || nivel_risco > 5) {
            throw new IllegalArgumentException("Risco deve estar entre 1 e 5.");
        }

        this.id = id;
        this.data_hora_registro = data_hora_registro;
        this.municipio = validar_texto(municipio, "municipio");
        this.uf = validar_texto(uf, "uf").toUpperCase(Locale.ROOT);
        this.pais = validar_texto(pais, "pais");

        this.tipo_area = validar_opcao(
                tipo_area,
                "tipo_area",
                "FLORESTA",
                "MATA",
                "PASTAGEM",
                "AREA_RURAL",
                "AREA_PROTEGIDA"
        );

        this.nivel_risco = nivel_risco;

        this.status = validar_opcao(
                status,
                "status",
                "REGISTRADA",
                "EM_ATENDIMENTO",
                "CONTROLADA",
                "ENCERRADA"
        );

        if (!this.uf.matches("[A-Z]{2}")) {
            throw new IllegalArgumentException("UF inválida.");
        }
    }

    private static String validar_texto(String valor, String campo) {

        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(campo + " inválido.");
        }

        valor = valor.trim();

        if (valor.contains("|")) {
            throw new IllegalArgumentException(
                    campo + " não pode conter o caractere |."
            );
        }

        return valor;
    }

    private static String validar_opcao(
            String valor,
            String campo,
            String... opcoes
    ) {

        valor = validar_texto(valor, campo).toUpperCase(Locale.ROOT);

        for (String opcao : opcoes) {
            if (opcao.equals(valor)) {
                return valor;
            }
        }

        throw new IllegalArgumentException(campo + " inválido.");
    }

    // Converte o objeto em uma linha do arquivo
    public String para_linha_arquivo() {

        return String.join("|",
                String.valueOf(id),
                data_hora_registro.toString(),
                municipio,
                uf,
                pais,
                tipo_area,
                String.valueOf(nivel_risco),
                status
        );
    }

    // Reconstrói um objeto a partir de uma linha do arquivo
    public static OcorrenciaIncendio de_linha_arquivo(String linha) {

        String[] campos = linha.split("\\|", -1);

        if (campos.length != 8) {
            throw new IllegalArgumentException(
                    "Registro inválido: esperados 8 campos."
            );
        }

        try {
            return new OcorrenciaIncendio(
                    Integer.parseInt(campos[0]),
                    LocalDateTime.parse(campos[1]),
                    campos[2],
                    campos[3],
                    campos[4],
                    campos[5],
                    Integer.parseInt(campos[6]),
                    campos[7]
            );

        } catch (
                NumberFormatException |
                java.time.format.DateTimeParseException e
        ) {
            throw new IllegalArgumentException(
                    "Erro na conversão dos dados.", e
            );
        }
    }

    public int getId() {
        return id;
    }

    public String getMunicipio() {
        return municipio;
    }

    public String getUf() {
        return uf;
    }

    public String getPais() {
        return pais;
    }

    public LocalDateTime getData_hora_registro() {
        return data_hora_registro;
    }

    public String getTipo_area() {
        return tipo_area;
    }

    public int getNivel_risco() {
        return nivel_risco;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {

        return "ID: " + id +
                " | Data: " + data_hora_registro +
                " | Município: " + municipio +
                " | UF: " + uf +
                " | País: " + pais +
                " | Área: " + tipo_area +
                " | Risco: " + nivel_risco +
                " | Status: " + status;
    }
}


package estrutura;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

import model.OcorrenciaIncendio;

public class ListaEncadeada implements Iterable<OcorrenciaIncendio> {

    // Nó interno: não precisa de um arquivo No.java separado.
    private static class No {
        OcorrenciaIncendio ocorrencia;
        No anterior;
        No proximo;

        No(OcorrenciaIncendio ocorrencia) {
            this.ocorrencia = ocorrencia;
        }
    }

    private No inicio;
    private No fim;
    private int tamanho;

    public ListaEncadeada() {
        inicio = null;
        fim = null;
        tamanho = 0;
    }

    // INSERÇÃO

    public void inserirInicio(OcorrenciaIncendio ocorrencia) {
        Objects.requireNonNull(ocorrencia);

        No novo = new No(ocorrencia);

        if (estaVazia()) {
            inicio = novo;
            fim = novo;
        } else {
            novo.proximo = inicio;
            inicio.anterior = novo;
            inicio = novo;
        }

        tamanho++;
    }

    public void inserirFim(OcorrenciaIncendio ocorrencia) {
        Objects.requireNonNull(ocorrencia);

        No novo = new No(ocorrencia);

        if (estaVazia()) {
            inicio = novo;
            fim = novo;
        } else {
            fim.proximo = novo;
            novo.anterior = fim;
            fim = novo;
        }

        tamanho++;
    }

    // CONSULTA

    public OcorrenciaIncendio buscarPorId(int id) {
        for (No atual = inicio; atual != null; atual = atual.proximo) {
            if (atual.ocorrencia.getId() == id) {
                return atual.ocorrencia;
            }
        }

        return null;
    }

    public OcorrenciaIncendio consultarInicio() {
        return inicio == null ? null : inicio.ocorrencia;
    }

    public OcorrenciaIncendio consultarFim() {
        return fim == null ? null : fim.ocorrencia;
    }

    // ATUALIZAÇÃO

    public boolean atualizar(OcorrenciaIncendio novaOcorrencia) {
        Objects.requireNonNull(novaOcorrencia);

        for (No atual = inicio; atual != null; atual = atual.proximo) {
            if (atual.ocorrencia.getId() == novaOcorrencia.getId()) {
                atual.ocorrencia = novaOcorrencia;
                return true;
            }
        }

        return false;
    }

    // REMOÇÃO

    public OcorrenciaIncendio removerInicio() {
        if (estaVazia()) {
            return null;
        }

        OcorrenciaIncendio removida = inicio.ocorrencia;

        if (inicio == fim) {
            inicio = null;
            fim = null;
        } else {
            inicio = inicio.proximo;
            inicio.anterior = null;
        }

        tamanho--;
        return removida;
    }

    public OcorrenciaIncendio removerFim() {
        if (estaVazia()) {
            return null;
        }

        OcorrenciaIncendio removida = fim.ocorrencia;

        if (inicio == fim) {
            inicio = null;
            fim = null;
        } else {
            fim = fim.anterior;
            fim.proximo = null;
        }

        tamanho--;
        return removida;
    }

    public OcorrenciaIncendio removerPorId(int id) {
        for (No atual = inicio; atual != null; atual = atual.proximo) {
            if (atual.ocorrencia.getId() == id) {

                if (atual == inicio) {
                    return removerInicio();
                }

                if (atual == fim) {
                    return removerFim();
                }

                atual.anterior.proximo = atual.proximo;
                atual.proximo.anterior = atual.anterior;

                tamanho--;
                return atual.ocorrencia;
            }
        }

        return null;
    }

    // OUTRAS OPERAÇÕES

    public boolean existeLocalidade(String municipio, String uf) {
        String municipioBusca = normalizar(municipio);
        String ufBusca = normalizar(uf);

        for (OcorrenciaIncendio ocorrencia : this) {
            if (normalizar(ocorrencia.getMunicipio()).equals(municipioBusca)
                    && normalizar(ocorrencia.getUf()).equals(ufBusca)) {
                return true;
            }
        }

        return false;
    }

    public boolean estaVazia() {
        return tamanho == 0;
    }

    public int getTamanho() {
        return tamanho;
    }

    public void limpar() {
        inicio = null;
        fim = null;
        tamanho = 0;
    }

    // RETORNA UMA LISTA PARA CONSULTAS E EXIBIÇÃO

    public List<OcorrenciaIncendio> listar() {
        List<OcorrenciaIncendio> resultado = new ArrayList<>();

        for (OcorrenciaIncendio ocorrencia : this) {
            resultado.add(ocorrencia);
        }

        return resultado;
    }

    // CRIA UMA CÓPIA INDEPENDENTE DA ESTRUTURA

    public ListaEncadeada copiar() {
        ListaEncadeada copia = new ListaEncadeada();

        for (OcorrenciaIncendio ocorrencia : this) {
            copia.inserirFim(ocorrencia);
        }

        return copia;
    }

    // ITERADOR PARA PERCORRER A LISTA COM FOR-EACH

    @Override
    public Iterator<OcorrenciaIncendio> iterator() {
        return new Iterator<OcorrenciaIncendio>() {

            private No atual = inicio;

            @Override
            public boolean hasNext() {
                return atual != null;
            }

            @Override
            public OcorrenciaIncendio next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }

                OcorrenciaIncendio ocorrencia = atual.ocorrencia;
                atual = atual.proximo;

                return ocorrencia;
            }
        };
    }

    private String normalizar(String texto) {
        if (texto == null) {
            return "";
        }

        return Normalizer.normalize(texto.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toUpperCase();
    }
}

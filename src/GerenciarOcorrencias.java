
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import estrutura.ListaEncadeada;
import hash.HashCidades;
import model.OcorrenciaIncendio;

public class GerenciarOcorrencias {

    private ListaEncadeada lista;
    private HashCidades hashCidades;
    private final GerenciarArquivo arquivo;

    public GerenciarOcorrencias() throws IOException {
        this(new GerenciarArquivo());
    }

    public GerenciarOcorrencias(GerenciarArquivo arquivo)
            throws IOException {

        this.arquivo = Objects.requireNonNull(arquivo);

        carregarDados();
    }

    // CARREGAMENTO INICIAL

    private void carregarDados() throws IOException {

        ListaEncadeada novaLista = new ListaEncadeada();

        List<OcorrenciaIncendio> ocorrencias =
                arquivo.lerOcorrencias();

        for (OcorrenciaIncendio ocorrencia : ocorrencias) {

            if (novaLista.buscarPorId(ocorrencia.getId()) != null) {
                throw new IOException(
                        "ID duplicado encontrado no arquivo: "
                                + ocorrencia.getId()
                );
            }

            novaLista.inserirFim(ocorrencia);
        }

        HashCidades novaHash = construirHash(novaLista);

        lista = novaLista;
        hashCidades = novaHash;
    }

    // CADASTRO

    public boolean cadastrar(OcorrenciaIncendio ocorrencia)
            throws IOException {

        Objects.requireNonNull(ocorrencia);

        if (lista.buscarPorId(ocorrencia.getId()) != null) {
            return false;
        }

        ListaEncadeada novaLista = lista.copiar();

        novaLista.inserirFim(ocorrencia);

        confirmarAlteracao(novaLista);

        return true;
    }

    // CONSULTA

    public OcorrenciaIncendio consultar(int id) {
        return lista.buscarPorId(id);
    }

    public List<OcorrenciaIncendio> listarTodas() {
        return lista.listar();
    }

    // ATUALIZAÇÃO

    public boolean atualizar(OcorrenciaIncendio ocorrencia)
            throws IOException {

        Objects.requireNonNull(ocorrencia);

        ListaEncadeada novaLista = lista.copiar();

        if (!novaLista.atualizar(ocorrencia)) {
            return false;
        }

        confirmarAlteracao(novaLista);

        return true;
    }

    // EXCLUSÃO

    public boolean excluir(int id) throws IOException {

        ListaEncadeada novaLista = lista.copiar();

        OcorrenciaIncendio removida = novaLista.removerPorId(id);

        if (removida == null) {
            return false;
        }

        confirmarAlteracao(novaLista);

        return true;
    }

    // HASH DE CIDADES

    private HashCidades construirHash(ListaEncadeada listaBase) {

        HashCidades novaHash = new HashCidades();

        for (OcorrenciaIncendio ocorrencia : listaBase) {

            // A Hash não duplica município + UF.
            novaHash.inserir(
                    ocorrencia.getMunicipio(),
                    ocorrencia.getUf()
            );
        }

        return novaHash;
    }

    public List<String> listarCidades() {
        return hashCidades.listar();
    }

    public int getTotalCidades() {
        return hashCidades.getQuantidade();
    }

    // CONFIRMAÇÃO DAS ALTERAÇÕES

    private void confirmarAlteracao(ListaEncadeada novaLista)
            throws IOException {

        HashCidades novaHash = construirHash(novaLista);

        // Se ocorrer erro aqui, a lista principal não é alterada.
        arquivo.salvarOcorrencias(novaLista);

        // Atualiza as estruturas em memória somente após salvar.
        lista = novaLista;
        hashCidades = novaHash;
    }

    // PROCESSAMENTO FIFO / LIFO

    public ListaEncadeada criarCopiaProcessamento() {
        return lista.copiar();
    }

    // INFORMAÇÕES GERAIS

    public int getTotalOcorrencias() {
        return lista.getTamanho();
    }
}

#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""Gera arquivos de entrada para o projeto de ocorrências de incêndio.

Formato de cada linha (compatível com OcorrenciaIncendio.java):
id|data_hora_registro|municipio|uf|pais|tipo_area|nivel_risco|status

O script utiliza a API de Localidades do IBGE para obter municípios e UFs.
Não exige bibliotecas externas: utiliza apenas a biblioteca padrão do Python.
"""

import argparse
import json
import random
import sys
from concurrent.futures import ThreadPoolExecutor
from datetime import datetime, timedelta
from pathlib import Path
from urllib.error import HTTPError, URLError
from urllib.request import Request, urlopen
import gzip


URL_IBGE_ESTADOS = (
    "https://servicodados.ibge.gov.br/api/v1/localidades/estados?orderBy=nome"
)
URL_IBGE_MUNICIPIOS = (
    "https://servicodados.ibge.gov.br/api/v1/localidades/estados/{}"
    "/municipios?orderBy=nome"
)

# O cache evita baixar a lista completa de municípios em toda execução.
ARQUIVO_CACHE = Path(__file__).resolve().parent / "municipios_ibge.json"

TIPOS_AREA = [
    "FLORESTA",
    "MATA",
    "PASTAGEM",
    "AREA_RURAL",
    "AREA_PROTEGIDA",
]

STATUS = [
    "REGISTRADA",
    "EM_ATENDIMENTO",
    "CONTROLADA",
    "ENCERRADA",
]


# -----------------------------------------------------------------------------
# Busca e cache dos municípios brasileiros
# -----------------------------------------------------------------------------

def requisitar_json(url):
    """Faz uma requisição HTTP e retorna o conteúdo JSON."""
    requisicao = Request(
        url,
        headers={
            "Accept": "application/json",
            "User-Agent": "GeradorEntradaOcorrencias/1.0",
        },
    )

    with urlopen(requisicao, timeout=30) as resposta:
        conteudo = resposta.read()
        if resposta.headers.get("Content-Encoding", "").lower() == "gzip":
            conteudo = gzip.decompress(conteudo)
        return json.loads(conteudo.decode("utf-8"))


def buscar_municipios_de_uf(estado):
    """Busca os municípios de um estado e associa cada um à sua sigla."""
    url = URL_IBGE_MUNICIPIOS.format(estado["id"])
    municipios_estado = requisitar_json(url)

    return [
        {
            "municipio": item["nome"].strip(),
            "uf": estado["sigla"].upper(),
        }
        for item in municipios_estado
        if item.get("nome") and "|" not in item["nome"]
    ]


def carregar_cache():
    """Carrega a lista de municípios salva anteriormente, se existir."""
    with ARQUIVO_CACHE.open("r", encoding="utf-8") as arquivo:
        municipios = json.load(arquivo)

    if not isinstance(municipios, list) or not municipios:
        raise ValueError("O cache de municípios está vazio ou inválido.")

    # Confere se os campos necessários estão presentes.
    for item in municipios:
        if not item.get("municipio") or not item.get("uf"):
            raise ValueError("O cache de municípios possui registros inválidos.")

    return municipios


def salvar_cache(municipios):
    """Salva a lista obtida do IBGE para reutilização futura."""
    with ARQUIVO_CACHE.open("w", encoding="utf-8") as arquivo:
        json.dump(municipios, arquivo, ensure_ascii=False, indent=2)


def obter_municipios(atualizar=False):
    """Obtém municípios do IBGE; usa o cache caso a API esteja indisponível."""
    if ARQUIVO_CACHE.exists() and not atualizar:
        try:
            municipios = carregar_cache()
            print(f"Lista de municípios carregada do cache ({len(municipios)} registros).")
            return municipios
        except (OSError, ValueError, json.JSONDecodeError) as erro:
            print(f"Cache inválido ({erro}). Tentando consultar o IBGE...")

    print("Consultando a API do IBGE para obter estados e municípios...")

    try:
        estados = requisitar_json(URL_IBGE_ESTADOS)
        if not isinstance(estados, list) or not estados:
            raise ValueError("A API do IBGE não retornou a lista de estados esperada.")

        # Consulta os municípios de vários estados em paralelo para agilizar.
        municipios = []
        with ThreadPoolExecutor(max_workers=6) as executor:
            resultados = executor.map(buscar_municipios_de_uf, estados)
            for municipios_estado in resultados:
                municipios.extend(municipios_estado)

        if not municipios:
            raise ValueError("A API do IBGE não retornou municípios.")

        salvar_cache(municipios)
        print(f"{len(municipios)} municípios carregados e salvos no cache.")
        return municipios

    except (HTTPError, URLError, TimeoutError, json.JSONDecodeError, KeyError, ValueError) as erro:
        # Se a atualização falhar, tenta aproveitar um cache anterior.
        if ARQUIVO_CACHE.exists():
            try:
                municipios = carregar_cache()
                print("Não foi possível atualizar pelo IBGE. Usando o cache existente.")
                return municipios
            except (OSError, ValueError, json.JSONDecodeError):
                pass

        raise RuntimeError(
            "Não foi possível obter os municípios do IBGE e não existe um cache válido.\n"
            "Verifique sua conexão com a internet e execute o script novamente.\n"
            f"Detalhe: {erro}"
        ) from erro


# -----------------------------------------------------------------------------
# Geração dos registros
# -----------------------------------------------------------------------------

def gerar_ids(quantidade, ordem, rng):
    """Gera IDs únicos em ordem crescente, decrescente ou aleatória."""
    if ordem == "crescente":
        return list(range(1, quantidade + 1))

    if ordem == "decrescente":
        return list(range(quantidade, 0, -1))

    ids = list(range(1, quantidade + 1))
    rng.shuffle(ids)
    return ids


def subtrair_cinco_anos(data):
    """Subtrai cinco anos sem falhar em datas como 29 de fevereiro."""
    try:
        return data.replace(year=data.year - 5)
    except ValueError:
        return data.replace(year=data.year - 5, day=28)


def gerar_data_aleatoria(rng, data_final):
    """Retorna uma data/hora aleatória entre cinco anos atrás e agora."""
    data_inicial = subtrair_cinco_anos(data_final)
    intervalo_segundos = int((data_final - data_inicial).total_seconds())
    deslocamento = rng.randint(0, intervalo_segundos)
    data = data_inicial + timedelta(seconds=deslocamento)

    # O formato é aceito por LocalDateTime.parse() no Java.
    return data.isoformat(timespec="seconds")


def gerar_linhas(quantidade, ordem, municipios, rng):
    """Monta as linhas no mesmo formato do método para_linha_arquivo()."""
    ids = gerar_ids(quantidade, ordem, rng)
    agora = datetime.now().replace(microsecond=0)
    linhas = []

    for identificador in ids:
        localidade = rng.choice(municipios)

        campos = [
            str(identificador),
            gerar_data_aleatoria(rng, agora),
            localidade["municipio"],
            localidade["uf"],
            "Brasil",
            rng.choice(TIPOS_AREA),
            str(rng.randint(1, 5)),
            rng.choice(STATUS),
        ]

        # Impede a geração de uma linha incompatível com split("|").
        if len(campos) != 8 or any("|" in campo for campo in campos):
            raise ValueError("Foi encontrado um campo inválido durante a geração.")

        linhas.append("|".join(campos))

    return linhas


# -----------------------------------------------------------------------------
# Configuração e execução
# -----------------------------------------------------------------------------

def perguntar_quantidade():
    while True:
        resposta = input("Quantidade de ocorrências: ").strip()
        try:
            quantidade = int(resposta)
            if quantidade > 0:
                return quantidade
        except ValueError:
            pass
        print("Digite um número inteiro maior que zero.")


def perguntar_ordem():
    print("\nComo deseja ordenar os IDs?")
    print("1 - Crescente  (1, 2, 3, ...)")
    print("2 - Decrescente (..., 3, 2, 1)")
    print("3 - Aleatória  (IDs embaralhados)")

    opcoes = {
        "1": "crescente",
        "2": "decrescente",
        "3": "aleatoria",
        "c": "crescente",
        "d": "decrescente",
        "a": "aleatoria",
    }

    while True:
        resposta = input("Escolha uma opção [1/2/3]: ").strip().lower()
        if resposta in opcoes:
            return opcoes[resposta]
        print("Opção inválida. Escolha 1, 2 ou 3.")


def criar_argumentos():
    parser = argparse.ArgumentParser(
        description="Gera um arquivo de entrada de ocorrências de incêndio."
    )
    parser.add_argument(
        "-n", "--quantidade",
        type=int,
        help="Quantidade de ocorrências a gerar.",
    )
    parser.add_argument(
        "-o", "--ordem",
        choices=["crescente", "decrescente", "aleatoria"],
        help="Ordem dos IDs: crescente, decrescente ou aleatoria.",
    )
    parser.add_argument(
        "-s", "--saida",
        help="Caminho do arquivo de saída (padrão: entrada.txt ao lado do script).",
    )
    parser.add_argument(
        "--seed",
        type=int,
        help="Semente aleatória para reproduzir a mesma geração.",
    )
    parser.add_argument(
        "--atualizar-municipios",
        action="store_true",
        help="Ignora o cache e baixa novamente os municípios do IBGE.",
    )
    return parser


def main():
    parser = criar_argumentos()
    args = parser.parse_args()

    quantidade = args.quantidade if args.quantidade is not None else perguntar_quantidade()
    if quantidade <= 0:
        parser.error("a quantidade precisa ser maior que zero")

    ordem = args.ordem if args.ordem is not None else perguntar_ordem()

    # Por padrão, salva entrada.txt na mesma pasta deste script.
    caminho_saida = (
        Path(args.saida).expanduser()
        if args.saida
        else Path(__file__).resolve().parent / "entrada.txt"
    )

    rng = random.Random(args.seed)

    try:
        municipios = obter_municipios(atualizar=args.atualizar_municipios)
        linhas = gerar_linhas(quantidade, ordem, municipios, rng)

        caminho_saida.parent.mkdir(parents=True, exist_ok=True)
        with caminho_saida.open("w", encoding="utf-8", newline="\n") as arquivo:
            arquivo.write("\n".join(linhas) + "\n")

        print("\nArquivo gerado com sucesso!")
        print(f"Registros: {quantidade}")
        print(f"Ordem dos IDs: {ordem}")
        print(f"Arquivo: {caminho_saida.resolve()}")

    except (RuntimeError, OSError, ValueError) as erro:
        print(f"\nErro: {erro}", file=sys.stderr)
        sys.exit(1)


if __name__ == "__main__":
    main()

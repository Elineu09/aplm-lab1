package org.eli.consola

import org.eli.modelo.Sessao
import org.eli.modelo.TipoMovimento
import org.eli.servico.GestorUtilizadores
import org.eli.servico.Orcamento

/**
 * Interpreta os comandos escritos na consola e chama o serviço correspondente.
 * A saída é feita através de [escrever] para que a classe possa ser testada.
 */
class Consola(
    private val gestor: GestorUtilizadores = GestorUtilizadores(),
    private val escrever: (String) -> Unit = ::println,
) {
    private var sessao: Sessao? = null

    /** Executa uma linha de comando. Devolve false quando o programa deve terminar. */
    fun executar(linha: String): Boolean {
        val partes = linha.trim().split(Regex("\\s+"))
        val comando = partes[0]
        val args = partes.drop(1)

        try {
            when (comando) {
                "" -> {}
                "s" -> return false
                "u" -> iniciarSessao(args)
                "exit" -> terminarSessao()
                in COMANDOS_ORCAMENTO -> executarNoOrcamento(comando, args)
                else -> escrever("Comando invalido: $comando")
            }
        } catch (e: IllegalArgumentException) {
            escrever("Erro: ${e.message}")
        }
        return true
    }

    private fun iniciarSessao(args: List<String>) {
        validarArgumentos(args, 2)
        require(sessao == null) { "Já existe uma sessão iniciada; use exit primeiro" }
        sessao = gestor.iniciarSessao(email = args[0], password = args[1])
    }

    private fun terminarSessao() {
        require(sessao != null) { "Não existe sessão iniciada" }
        sessao = null
    }

    private fun executarNoOrcamento(comando: String, args: List<String>) {
        val orcamento = requireNotNull(sessao) { "Inicie sessão primeiro: u email password" }.utilizador.orcamento
        when (comando) {
            "i" -> inserir(orcamento, args)
            "l" -> listar(orcamento, args)
            "r" -> remover(orcamento, args)
            "b" -> balanco(orcamento, args)
            "m" -> valorMinimo(orcamento, args)
            "v" -> verificar(orcamento, args)
            "w" -> comportavel(orcamento, args)
        }
    }

    // i valor data periodicidade tipo descricao
    private fun inserir(orcamento: Orcamento, args: List<String>) {
        validarArgumentos(args, 5)
        orcamento.inserir(
            valor = args[0].paraInteiro("valor"),
            data = args[1].paraInteiro("data"),
            periodicidade = args[2].paraInteiro("periodicidade"),
            tipo = TipoMovimento.deCodigo(args[3]),
            descricao = args[4],
        )
    }

    // l data
    private fun listar(orcamento: Orcamento, args: List<String>) {
        validarArgumentos(args, 1)
        orcamento.listar(args[0].paraInteiro("data"))
            .forEachIndexed { indice, movimento -> escrever("$indice $movimento") }
    }

    // r data indice
    private fun remover(orcamento: Orcamento, args: List<String>) {
        validarArgumentos(args, 2)
        orcamento.remover(args[0].paraInteiro("data"), args[1].paraInteiro("índice"))
    }

    // b data
    private fun balanco(orcamento: Orcamento, args: List<String>) {
        validarArgumentos(args, 1)
        escrever(orcamento.balanco(args[0].paraInteiro("data")).toString())
    }

    // m valor
    private fun valorMinimo(orcamento: Orcamento, args: List<String>) {
        validarArgumentos(args, 1)
        orcamento.valorMinimo = args[0].paraInteiro("valor mínimo")
    }

    // v
    private fun verificar(orcamento: Orcamento, args: List<String>) {
        validarArgumentos(args, 0)
        escrever(if (orcamento.valido()) "Valido" else "Invalido")
    }

    // w data valor
    private fun comportavel(orcamento: Orcamento, args: List<String>) {
        validarArgumentos(args, 2)
        val data = orcamento.primeiraDataComportavel(args[0].paraInteiro("data"), args[1].paraInteiro("valor"))
        escrever(data?.toString() ?: "Incomportavel")
    }

    private fun validarArgumentos(args: List<String>, quantidade: Int) =
        require(args.size == quantidade) { "O comando espera $quantidade argumento(s)" }

    private fun String.paraInteiro(campo: String): Int =
        requireNotNull(toIntOrNull()) { "O campo $campo deve ser um número inteiro" }

    companion object {
        private val COMANDOS_ORCAMENTO = setOf("i", "l", "r", "b", "m", "v", "w")
    }
}

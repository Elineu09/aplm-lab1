package org.eli.servico

import org.eli.modelo.Movimento
import org.eli.modelo.Movimento.Companion.PRIMEIRA_DATA
import org.eli.modelo.Movimento.Companion.ULTIMA_DATA
import org.eli.modelo.TipoMovimento

/** Orçamento pessoal: conjunto de movimentos e valor mínimo do balanço. */
class Orcamento {
    // Uma lista de movimentos por data; a ordem de inserção dá o índice usado em listar/remover.
    private val movimentosPorData = List(ULTIMA_DATA + 1) { mutableListOf<Movimento>() }

    var valorMinimo = 0

    /** Insere um movimento e, se periodicidade > 0, as suas repetições até à última data. */
    fun inserir(valor: Int, data: Int, periodicidade: Int, tipo: TipoMovimento, descricao: String) {
        require(periodicidade >= 0) { "A periodicidade não pode ser negativa" }
        validarData(data)

        val datas = if (periodicidade == 0) listOf(data) else (data..ULTIMA_DATA step periodicidade).toList()
        require(datas.none { movimentosPorData[it].size >= MAX_MOVIMENTOS_POR_DATA }) {
            "Cada data só pode ter $MAX_MOVIMENTOS_POR_DATA movimentos"
        }

        for (d in datas) {
            movimentosPorData[d].add(Movimento(valor, d, tipo, descricao))
        }
    }

    fun listar(data: Int): List<Movimento> {
        validarData(data)
        return movimentosPorData[data].toList()
    }

    fun remover(data: Int, indice: Int) {
        validarData(data)
        val movimentos = movimentosPorData[data]
        require(indice in movimentos.indices) { "Não existe movimento com índice $indice na data $data" }
        movimentos.removeAt(indice)
    }

    fun balanco(data: Int): Int {
        validarData(data)
        return balancosAcumulados()[data]
    }

    fun valido(): Boolean = balancosAcumulados().all { it >= valorMinimo }

    /** Primeira data, a partir de [data], em que a despesa não deixa o balanço abaixo do mínimo; null se nunca. */
    fun primeiraDataComportavel(data: Int, valor: Int): Int? {
        validarData(data)
        require(valor > 0) { "O valor da despesa deve ser maior que zero" }

        val balancos = balancosAcumulados()
        // A despesa feita no dia d reduz o balanço de todos os dias a partir de d.
        return (data..ULTIMA_DATA).firstOrNull { d ->
            (d..ULTIMA_DATA).all { dia -> balancos[dia] - valor >= valorMinimo }
        }
    }

    /** Balanço de cada data, incluindo todos os movimentos até essa data. */
    private fun balancosAcumulados(): IntArray {
        val balancos = IntArray(ULTIMA_DATA + 1)
        var acumulado = 0
        for (data in balancos.indices) {
            acumulado += movimentosPorData[data].sumOf { it.valorComSinal }
            balancos[data] = acumulado
        }
        return balancos
    }

    private fun validarData(data: Int) =
        require(data in PRIMEIRA_DATA..ULTIMA_DATA) { "A data deve estar entre $PRIMEIRA_DATA e $ULTIMA_DATA" }

    companion object {
        const val MAX_MOVIMENTOS_POR_DATA = 100
    }
}

package org.eli.modelo

//Um movimento de dinheiro numa data.

data class Movimento(
    val valor: Int,
    val data: Int,
    val tipo: TipoMovimento,
    val descricao: String,
) {
    init {
        require(valor > 0) { "O valor deve ser maior que zero" }
        require(data in PRIMEIRA_DATA..ULTIMA_DATA) { "A data deve estar entre $PRIMEIRA_DATA e $ULTIMA_DATA" }
        require(descricao.isNotBlank()) { "A descrição é obrigatória" }
        require(descricao.length <= MAX_DESCRICAO) { "A descrição não pode ter mais de $MAX_DESCRICAO caracteres" }
    }

    // Valor com sinal: positivo para receitas, negativo para despesas.
    val valorComSinal: Int
        get() = if (tipo == TipoMovimento.RECEITA) valor else -valor

    override fun toString() = "$valor ${tipo.codigo} $descricao"

    companion object {
        const val PRIMEIRA_DATA = 0
        const val ULTIMA_DATA = 999
        const val MAX_DESCRICAO = 60
    }
}

package org.eli.modelo

//Tipo de um movimento: receita ('r') ou despesa ('d').

enum class TipoMovimento(val codigo: Char) {
    RECEITA('r'),
    DESPESA('d');

    companion object {
        fun deCodigo(codigo: String): TipoMovimento =
            requireNotNull(entries.find { it.codigo.toString() == codigo }) {
                "O tipo deve ser r (receita) ou d (despesa)"
            }
    }
}

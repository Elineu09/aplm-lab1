package org.eli

import org.eli.consola.Consola

fun main() {
    val consola = Consola()
    while (true) {
        val linha = readlnOrNull() ?: break // fim da entrada
        if (!consola.executar(linha)) break
    }
}

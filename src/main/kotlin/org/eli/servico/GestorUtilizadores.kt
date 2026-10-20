package org.eli.servico

import org.eli.modelo.Sessao
import org.eli.modelo.Utilizador

/** Guarda os utilizadores (o email é a chave, sem duplicados) e trata do início de sessão. */
class GestorUtilizadores {
    private val utilizadores = mutableMapOf<String, Utilizador>()

    /** Inicia sessão; se o email ainda não existe, cria primeiro o utilizador. */
    fun iniciarSessao(email: String, password: String): Sessao {
        val utilizador = utilizadores.getOrPut(email) { Utilizador(email, password) }
        require(utilizador.passwordCorrecta(password)) { "Password incorrecta" }
        return utilizador.novaSessao()
    }
}

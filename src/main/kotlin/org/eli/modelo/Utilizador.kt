package org.eli.modelo

import org.eli.servico.Orcamento
import java.security.MessageDigest

/** Utilizador identificado pelo email. Cada utilizador tem o seu orçamento e as suas sessões. */
class Utilizador(val email: String, password: String) {
    // Guarda-se apenas o hash da password, nunca o texto original.
    private val passwordHash = hash(password)

    val orcamento = Orcamento()

    private val _sessoes = mutableListOf<Sessao>()
    val sessoes: List<Sessao> get() = _sessoes

    init {
        require('@' in email) { "Email inválido" }
        require(password.isNotBlank()) { "A password é obrigatória" }
    }

    fun passwordCorrecta(password: String) = hash(password) == passwordHash

    fun novaSessao(): Sessao = Sessao(this).also { _sessoes.add(it) }

    private fun hash(texto: String): String =
        MessageDigest.getInstance("SHA-256")
            .digest(texto.toByteArray())
            .joinToString("") { "%02x".format(it) }
}

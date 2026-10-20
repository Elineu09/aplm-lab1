package org.eli.modelo

import java.time.LocalDateTime
import java.util.UUID

/** Sessão iniciada por um utilizador: identificada por um token único e a data/hora de início. */
class Sessao(val utilizador: Utilizador) {
    val token: String = UUID.randomUUID().toString()
    val dataHora: LocalDateTime = LocalDateTime.now()
}

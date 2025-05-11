package ua.pp.lumivoid.iwtcms.ktor.api.requests

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import ua.pp.lumivoid.iwtcms.ktor.cookie.UserSession
import ua.pp.lumivoid.iwtcms.ktor.tables.Users

object CheckLoginG: Request() {
    override val PATH = "/api/checkLogin"

    override val request: Routing.() -> Unit = {
        get(PATH) {
            val session = call.sessions.get<UserSession>()

            @Suppress("SENSELESS_COMPARISON") // idk why
            if (session == null || session.name == null || session.id == null) {
                call.respondText("Not logged in", status = HttpStatusCode.Unauthorized)
                return@get
            }

            newSuspendedTransaction  {
                try {
                    Users.selectAll()
                        .where { Users.username eq session.name }
                        .andWhere { Users.uniqueId eq session.id }.first()
                } catch(_: NoSuchElementException) {
                    call.respondText("Not logged in", status = HttpStatusCode.Unauthorized)
                    return@newSuspendedTransaction
                }

                call.respondText(session.name)
            }
        }
    }
}
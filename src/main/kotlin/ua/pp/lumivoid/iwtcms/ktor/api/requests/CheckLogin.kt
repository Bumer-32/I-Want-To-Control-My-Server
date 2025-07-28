package ua.pp.lumivoid.iwtcms.ktor.api.requests

import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respondText
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import ua.pp.lumivoid.iwtcms.ktor.cookie.UserSession
import ua.pp.lumivoid.iwtcms.ktor.tables.UsersTable

object CheckLogin : Request() {
    override val path = "/api/checkLogin"

    override val request: Routing.() -> Unit = {
        get(path) {
            val session = call.sessions.get<UserSession>()

            if (session == null) {
                call.respondText("Not logged in", status = HttpStatusCode.Unauthorized)
                return@get
            }

            newSuspendedTransaction {
                try {
                    UsersTable
                        .selectAll()
                        .where { (UsersTable.username eq session.name) and (UsersTable.uniqueId eq session.id) }
                        .first()
                } catch (_: NoSuchElementException) {
                    call.respondText("Not logged in", status = HttpStatusCode.Unauthorized)
                    return@newSuspendedTransaction
                }

                call.respondText(session.name)
            }
        }
    }
}

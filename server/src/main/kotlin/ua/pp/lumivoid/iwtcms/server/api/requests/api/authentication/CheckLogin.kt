package ua.pp.lumivoid.iwtcms.server.api.requests.api.authentication

import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import kotlinx.coroutines.flow.first
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import ua.pp.lumivoid.iwtcms.server.api.Request
import ua.pp.lumivoid.iwtcms.server.cookie.UserSession
import ua.pp.lumivoid.iwtcms.server.tables.UsersTable

internal object CheckLogin : Request() {
    override val path = "/api/authentication/checkLogin"

    override val request: Routing.() -> Unit = {
        get(path) {
            val session = call.sessions.get<UserSession>()

            if (session == null) {
                call.respond(HttpStatusCode.Unauthorized, "Not logged in")
                return@get
            }

            suspendTransaction {
                try {
                    UsersTable
                        .selectAll()
                        .where { (UsersTable.username eq session.name) and (UsersTable.uniqueId eq session.id) }
                        .first()
                } catch (_: NoSuchElementException) {
                    call.respond(HttpStatusCode.Unauthorized, "Not logged in")
                    return@suspendTransaction
                }

                call.respond(session.name)
            }
        }
    }
}
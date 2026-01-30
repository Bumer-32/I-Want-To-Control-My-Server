package ua.pp.lumivoid.iwtcms.server.api.requests.api.authentication

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import ua.pp.lumivoid.iwtcms.server.api.Request
import ua.pp.lumivoid.iwtcms.server.cookie.UserSession
import ua.pp.lumivoid.iwtcms.server.tables.UserEntity
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

            val user = withContext(Dispatchers.IO) {
                transaction {
                    UserEntity.find { (UsersTable.username eq session.name) and (UsersTable.uniqueId eq session.id) }.firstOrNull()
                }
            }

            if (user == null) call.respond(HttpStatusCode.Unauthorized, "Not logged in")
            else call.respond(session.name)
        }
    }
}
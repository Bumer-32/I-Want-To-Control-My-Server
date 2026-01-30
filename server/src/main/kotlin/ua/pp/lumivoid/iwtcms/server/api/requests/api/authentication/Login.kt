package ua.pp.lumivoid.iwtcms.server.api.requests.api.authentication

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import org.apache.commons.codec.digest.DigestUtils
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import ua.pp.lumivoid.iwtcms.server.api.Request
import ua.pp.lumivoid.iwtcms.server.cookie.UserSession
import ua.pp.lumivoid.iwtcms.server.tables.UserEntity
import ua.pp.lumivoid.iwtcms.server.tables.UsersTable

internal object Login : Request() {
    override val path = "/api/authentication/login"

    override val request: Routing.() -> Unit = {
        post(path) {
            val payload = call.receive<LoginPayload>()

            val user = withContext(Dispatchers.IO) {
                transaction {
                    UserEntity
                        .find { (UsersTable.username eq payload.username) }
                        .firstOrNull()
                }
            }

            if (user == null) {
                call.respond(HttpStatusCode.Unauthorized, "Login failed")
                return@post
            }

            if (DigestUtils.sha256Hex(payload.password + user.salt) == user.passwordHash) {
                call.sessions.set(UserSession(user.username, user.uniqueId))
                call.respond("Login successful")
            } else {
                call.respond(HttpStatusCode.Unauthorized, "Login failed")
            }
        }
    }

    @Serializable
    data class LoginPayload(
        val username: String,
        val password: String,
    )
}
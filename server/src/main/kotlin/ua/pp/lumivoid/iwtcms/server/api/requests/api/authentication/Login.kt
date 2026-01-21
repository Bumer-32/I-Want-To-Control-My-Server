package ua.pp.lumivoid.iwtcms.server.api.requests.api.authentication

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import kotlinx.coroutines.flow.first
import kotlinx.serialization.Serializable
import org.apache.commons.codec.digest.DigestUtils
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import ua.pp.lumivoid.iwtcms.server.api.Request
import ua.pp.lumivoid.iwtcms.server.cookie.UserSession
import ua.pp.lumivoid.iwtcms.server.tables.UsersTable

internal object Login : Request() {
    override val path = "/api/authentication/login"

    override val request: Routing.() -> Unit = {
        post(path) {
            val payload = call.receive<LoginPayload>()

            suspendTransaction {
                try {
                    val user: ResultRow = UsersTable
                        .selectAll()
                        .where { (UsersTable.username eq payload.username) }
                        .first()
                    val salt = user[UsersTable.salt]

                    if (DigestUtils.sha256Hex(payload.password + salt) == user[UsersTable.passwordHash]) {
                        call.sessions.set(UserSession(user[UsersTable.username], user[UsersTable.uniqueId]))
                        call.respond("Login successful")
                    } else {
                        call.respond(HttpStatusCode.Unauthorized, "Login failed")
                    }
                } catch (_: NoSuchElementException) {
                    call.respond(HttpStatusCode.Unauthorized, "Login failed")
                }
            }
        }
    }

    @Serializable
    data class LoginPayload(
        val username: String,
        val password: String,
    )
}
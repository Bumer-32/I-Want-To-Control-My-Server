package ua.pp.lumivoid.iwtcms.ktor.api.requests

import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respondText
import io.ktor.server.routing.Routing
import io.ktor.server.routing.post
import io.ktor.server.sessions.sessions
import io.ktor.server.sessions.set
import kotlinx.serialization.Serializable
import org.apache.commons.codec.digest.DigestUtils
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import ua.pp.lumivoid.iwtcms.ktor.cookie.UserSession
import ua.pp.lumivoid.iwtcms.ktor.tables.UsersTable

object Login : Request() {
    override val path = "/api/login"

    override val request: Routing.() -> Unit = {
        post(path) {
            val payload = call.receive<LoginPayload>()

            newSuspendedTransaction {
                try {
                    val user: ResultRow = UsersTable
                                            .selectAll()
                                            .where { (UsersTable.username eq payload.username) }
                                            .first()
                    val salt = user[UsersTable.salt]

                    if (DigestUtils.sha256Hex(payload.password + salt) == user[UsersTable.passwordHash]) {
                        call.sessions.set(UserSession(user[UsersTable.username], user[UsersTable.uniqueId]))
                        call.respondText("Login successful")
                    } else {
                        call.respondText("Login failed", status = HttpStatusCode.Unauthorized)
                    }
                } catch (_: NoSuchElementException) {
                    call.respondText("Login failed", status = HttpStatusCode.Unauthorized)
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
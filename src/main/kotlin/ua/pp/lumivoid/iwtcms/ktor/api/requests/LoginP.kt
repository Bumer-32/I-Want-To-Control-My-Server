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
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import ua.pp.lumivoid.iwtcms.ktor.cookie.UserSession
import ua.pp.lumivoid.iwtcms.ktor.tables.Users

object LoginP : Request() {
    override val path = "/api/login"

    override val request: Routing.() -> Unit = {
        post(path) {
            val payload = call.receive<LoginPayload>()

            newSuspendedTransaction {
                val user: ResultRow =
                    try {
                        Users
                            .selectAll()
                            .where { Users.username eq payload.username }
                            .andWhere { Users.passwordHash eq DigestUtils.sha256Hex(payload.password) }
                            .first()
                    } catch (_: NoSuchElementException) {
                        call.respondText("Login failed", status = HttpStatusCode.Unauthorized)
                        return@newSuspendedTransaction
                    }

                call.sessions.set(UserSession(name = user[Users.username], id = user[Users.uniqueId]))
                call.respondText("Login successful")
            }
        }
    }
}

@Serializable
data class LoginPayload(
    val username: String,
    val password: String,
)

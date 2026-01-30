package ua.pp.lumivoid.iwtcms.server.api.requests.api.player

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import ua.pp.lumivoid.iwtcms.server.IWTCMS
import ua.pp.lumivoid.iwtcms.server.api.Request
import ua.pp.lumivoid.iwtcms.server.api.doAuth
import ua.pp.lumivoid.iwtcms.server.api.requests.api.user.PermissionsList
import ua.pp.lumivoid.iwtcms.server.cookie.UserSession
import ua.pp.lumivoid.iwtcms.server.tables.UserEntity
import ua.pp.lumivoid.iwtcms.server.tables.UsersTable

@Suppress("DuplicatedCode")
internal object Kill : Request() {
    override val path = "/api/player/kill"

    override val request: Routing.() -> Unit = {
        post(path) {
            doAuth(
                call = call,
                permission = PermissionsList.Permission.USERS_MANAGE.value,
                success = {
                    val session = call.sessions.get<UserSession>()
                    val payload = call.receive<KillPayload>()
                    val iwtcms = IWTCMS.instance

                    if (payload.username == null && payload.uuid == null) {
                        call.respond(HttpStatusCode.BadRequest, "Username or UUID is required")
                        return@doAuth
                    }

                    val player = iwtcms.players().find { it.name == payload.username || it.uuid == payload.uuid }

                    if (player == null) {
                        call.respond(HttpStatusCode.NotFound, "Player not found")
                        return@doAuth
                    }

                    val source = withContext(Dispatchers.IO) {
                        transaction {
                            UserEntity
                                .find { (UsersTable.username eq session!!.name) and (UsersTable.uniqueId eq session.id) }
                                .first().username
                        }
                    }

                    iwtcms.kill(player)

                    logger.info("Iwtcms user $source successfully killed player ${player.name}")
                    call.respond("Player ${player.name} killed")
                },
            )
        }
    }

    @Serializable
    data class KillPayload(
        val username: String? = null,
        val uuid: String? = null,
    )
}
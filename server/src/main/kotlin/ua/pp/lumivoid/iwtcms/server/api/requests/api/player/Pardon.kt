package ua.pp.lumivoid.iwtcms.server.api.requests.api.player

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import kotlinx.coroutines.flow.first
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import ua.pp.lumivoid.iwtcms.server.IWTCMS
import ua.pp.lumivoid.iwtcms.server.api.Request
import ua.pp.lumivoid.iwtcms.server.api.doAuth
import ua.pp.lumivoid.iwtcms.server.api.requests.api.user.PermissionsList
import ua.pp.lumivoid.iwtcms.server.cookie.UserSession
import ua.pp.lumivoid.iwtcms.server.tables.UsersTable

@Suppress("DuplicatedCode")
internal object Pardon : Request() {
    override val path = "/api/player/pardon"

    override val request: Routing.() -> Unit = {
        post(path) {
            doAuth(
                call = call,
                permission = PermissionsList.Permission.USERS_MANAGE.value,
                success = {
                    val session = call.sessions.get<UserSession>()
                    val payload = call.receive<PardonPayload>()
                    val iwtcms = IWTCMS.instance

                    if (payload.username == null) {
                        call.respond(HttpStatusCode.BadRequest, "Username is required")
                        return@doAuth
                    }

                    val bannedPlayer = iwtcms.bans().find { it.player.name == payload.username }?.player

                    if (bannedPlayer == null) {
                        call.respond(HttpStatusCode.Conflict, "Player not banned")
                        return@doAuth
                    }

                    val source = suspendTransaction {
                        UsersTable
                            .selectAll()
                            .where { (UsersTable.username eq session!!.name) and (UsersTable.uniqueId eq session.id) }
                            .first()[UsersTable.username]
                    }

                    iwtcms.pardon(bannedPlayer)

                    logger.info("Iwtcms user $source successfully unbanned player ${payload.username}")
                    call.respond("Player ${payload.username} has been successfully unbanned")
                    return@doAuth
                },
            )
        }
    }

    @Serializable
    data class PardonPayload(
        val username: String? = null,
    )
}
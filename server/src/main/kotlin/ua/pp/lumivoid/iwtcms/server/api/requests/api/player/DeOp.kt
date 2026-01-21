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
internal object DeOp : Request() {
    override val path = "/api/player/deOp"

    override val request: Routing.() -> Unit = {
        post(path) {
            val session = call.sessions.get<UserSession>()
            val payload = call.receive<DeOpData>()
            val iwtcms = IWTCMS.instance

            doAuth(
                call = call,
                permission = PermissionsList.Permission.USERS_MANAGE.value,
                success = {
                    if (payload.username == null && payload.uuid == null) {
                        call.respond(HttpStatusCode.BadRequest, "Username or UUID is required")
                        return@doAuth
                    }

                    val player = iwtcms.players().find { it.name == payload.username || it.uuid == payload.uuid }

                    if (player == null) {
                        call.respond(HttpStatusCode.NotFound, "Player not found")
                        return@doAuth
                    }

                    if (!iwtcms.isOp(player)) {
                        call.respond(HttpStatusCode.Conflict, "Player already is not an operator")
                        return@doAuth
                    }

                    val source = suspendTransaction {
                        UsersTable
                            .selectAll()
                            .where { (UsersTable.username eq session!!.name) and (UsersTable.uniqueId eq session.id) }
                            .first()[UsersTable.username]
                    }

                    iwtcms.deop(player)

                    logger.info("Iwtcms user $source successfully removed operator privileges on player ${player.name}")
                    call.respond("Player ${player.name} are no longer a server operator")
                },
            )
        }
    }

    @Serializable
    private data class DeOpData(
        val username: String? = null,
        val uuid: String? = null,
    )
}
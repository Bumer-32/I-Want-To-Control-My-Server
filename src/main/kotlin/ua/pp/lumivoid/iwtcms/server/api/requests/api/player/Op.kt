package ua.pp.lumivoid.iwtcms.server.api.requests.api.player

import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.post
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import kotlinx.coroutines.flow.first
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import ua.pp.lumivoid.iwtcms.server.api.doAuth
import ua.pp.lumivoid.iwtcms.server.api.Request
import ua.pp.lumivoid.iwtcms.server.api.requests.api.user.PermissionsList
import ua.pp.lumivoid.iwtcms.server.cookie.UserSession
import ua.pp.lumivoid.iwtcms.server.tables.UsersTable
import ua.pp.lumivoid.iwtcms.util.MinecraftServerHandler

@Suppress("DuplicatedCode")
object Op : Request() {
    override val path = "/api/player/op"

    override val request: Routing.() -> Unit = {
        post(path) {
            val session = call.sessions.get<UserSession>()
            val payload = call.receive<OpData>()
            val playerManager = MinecraftServerHandler.server!!.playerManager

            doAuth(
                call = call,
                permission = PermissionsList.Permission.USERS_MANAGE.value,
                success = {
                    if (payload.username == null && payload.uuid == null) {
                        call.respond(HttpStatusCode.BadRequest, "Username or UUID is required")
                        return@doAuth
                    }

                    val player = playerManager.playerList.find { it.name.string == payload.username || it.uuidAsString == payload.uuid }

                    if (player == null) {
                        call.respond(HttpStatusCode.NotFound, "Player not found")
                        return@doAuth
                    }

                    if (playerManager.isOperator(player.gameProfile)) {
                        call.respond(HttpStatusCode.Conflict, "Player already is an operator")
                        return@doAuth
                    }

                    val source = suspendTransaction {
                        UsersTable
                            .selectAll()
                            .where { (UsersTable.username eq session!!.name) and (UsersTable.uniqueId eq session.id) }
                            .first()[UsersTable.username]
                    }

                    playerManager.addToOperators(player.gameProfile)

                    logger.info("Iwtcms user $source successfully granted operator privileges to player ${player.name.string}")
                    call.respond("Player ${player.name.string} now a server operator")
                },
            )
        }
    }

    @Serializable
    private data class OpData(
        val username: String? = null,
        val uuid: String? = null,
    )
}
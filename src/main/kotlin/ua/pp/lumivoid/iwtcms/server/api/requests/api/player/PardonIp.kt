package ua.pp.lumivoid.iwtcms.server.api.requests.api.player

import com.google.common.net.InetAddresses
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
import ua.pp.lumivoid.iwtcms.server.api.Request
import ua.pp.lumivoid.iwtcms.server.api.doAuth
import ua.pp.lumivoid.iwtcms.server.api.requests.api.user.PermissionsList
import ua.pp.lumivoid.iwtcms.server.cookie.UserSession
import ua.pp.lumivoid.iwtcms.server.tables.UsersTable
import ua.pp.lumivoid.iwtcms.util.MinecraftServerHandler

object PardonIp : Request() {
    override val path = "/api/player/pardonIp"

    override val request: Routing.() -> Unit = {
        post(path) {
            val session = call.sessions.get<UserSession>()
            val payload = call.receive<PardonIpData>()
            val playerManager = MinecraftServerHandler.server!!.playerManager

            doAuth(
                call = call,
                permission = PermissionsList.Permission.USERS_MANAGE.value,
                success = {
                    if (!InetAddresses.isInetAddress(payload.ip)) {
                        call.respond(HttpStatusCode.BadRequest, "Invalid IP address")
                    }

                    if (!playerManager.ipBanList.isBanned(payload.ip)) {
                        call.respond(HttpStatusCode.Conflict, "Such ip are not banned")
                        return@doAuth
                    }

                    val source = suspendTransaction {
                        UsersTable
                            .selectAll()
                            .where { (UsersTable.username eq session!!.name) and (UsersTable.uniqueId eq session.id) }
                            .first()[UsersTable.username]
                    }

                    playerManager.ipBanList.remove(payload.ip)

                    logger.info("Iwtcms user $source successfully unbanned ip ${payload.ip}")
                    call.respond("Ip ${payload.ip} has been successfully unbanned")
                },
            )
        }
    }

    @Serializable
    private data class PardonIpData(val ip: String)
}
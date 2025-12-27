package ua.pp.lumivoid.iwtcms.server.api.requests.api.player

import com.google.common.net.InetAddresses
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.post
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import kotlinx.coroutines.flow.first
import kotlinx.serialization.Serializable
import net.minecraft.network.chat.Component
import net.minecraft.server.players.IpBanListEntry
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import ua.pp.lumivoid.iwtcms.server.IWTCMS
import ua.pp.lumivoid.iwtcms.server.api.doAuth
import ua.pp.lumivoid.iwtcms.server.api.Request
import ua.pp.lumivoid.iwtcms.server.api.requests.api.user.PermissionsList
import ua.pp.lumivoid.iwtcms.server.cookie.UserSession
import ua.pp.lumivoid.iwtcms.server.tables.UsersTable
import java.util.Date
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.time.toJavaInstant

internal object BanIp : Request() {
    override val path = "/api/player/banIp"

    @OptIn(ExperimentalTime::class)
    override val request: Routing.() -> Unit = {
        post(path) {
            val session = call.sessions.get<UserSession>()
            val payload = call.receive<BanIpData>()
            val playerList = IWTCMS.instance.getMinecraftServer().playerList

            doAuth(
                call = call,
                permission = PermissionsList.Permission.USERS_MANAGE.value,
                success = {
                    val ip = if (InetAddresses.isInetAddress(payload.ip.toString())) {
                        payload.ip!!
                    } else {
                        if (payload.username == null && payload.uuid == null) {
                            call.respond(HttpStatusCode.BadRequest, "No any targets found, please specify username, uuid or ip")
                            return@doAuth
                        }

                        val player = playerList.players.find { it.name.string == payload.username || it.stringUUID == payload.uuid }

                        if (player == null) {
                            call.respond(HttpStatusCode.NotFound, "Player not found")
                            return@doAuth
                        }

                        player.ipAddress
                    }

                    if (playerList.ipBans.isBanned(ip)) {
                        call.respond(HttpStatusCode.Conflict, "Such ip already banned")
                        return@doAuth
                    }

                    val source = suspendTransaction {
                        UsersTable
                            .selectAll()
                            .where { (UsersTable.username eq session!!.name) and (UsersTable.uniqueId eq session.id) }
                            .first()[UsersTable.username]
                    }

                    playerList.ipBans.add(
                        IpBanListEntry(
                            ip,
                            Date.from(Clock.System.now().toJavaInstant()),
                            "$source (using iwtcms)",
                            if (payload.expireDate != null) Date.from(payload.expireDate.toJavaInstant()) else null,
                            payload.reason
                        )
                    )

                    // don't forget to kick
                    // btw we if we ban by ip there's can be multiple players from 1 ip, so we need to kick them all, not only 1 player
                    playerList.getPlayersWithAddress(ip).forEach { player ->
                        player.connection.disconnect(Component.translatable("multiplayer.disconnect.ip_banned"))
                    }

                    logger.info("Iwtcms user $source successfully banned ip $ip")
                    call.respond("Ip $ip has been successfully banned")
                },
            )
        }
    }

    @Serializable
    private data class BanIpData @OptIn(ExperimentalTime::class) constructor(
        val username: String? = null,
        val uuid: String? = null,
        val ip: String? = null,
        val expireDate: Instant? = null,
        val reason: String? = null,
    )
}
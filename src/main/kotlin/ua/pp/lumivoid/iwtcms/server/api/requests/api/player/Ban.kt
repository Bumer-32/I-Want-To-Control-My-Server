package ua.pp.lumivoid.iwtcms.server.api.requests.api.player

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import kotlinx.coroutines.flow.first
import kotlinx.serialization.Serializable
import net.minecraft.server.BannedPlayerEntry
import net.minecraft.text.Text
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
import java.util.*
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.time.toJavaInstant

@Suppress("DuplicatedCode")
object Ban : Request() {
    override val path = "/api/player/ban"

    @OptIn(ExperimentalTime::class)
    override val request: Routing.() -> Unit = {
        post(path) {
            val session = call.sessions.get<UserSession>()
            val payload = call.receive<BanData>()
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

                    if (playerManager.userBanList.contains(player.gameProfile)) {
                        call.respond(HttpStatusCode.Conflict, "Player already banned")
                        return@doAuth
                    }

                    val source = suspendTransaction {
                        UsersTable
                            .selectAll()
                            .where { (UsersTable.username eq session!!.name) and (UsersTable.uniqueId eq session.id) }
                            .first()[UsersTable.username]
                    }

                   playerManager.userBanList.add(
                        BannedPlayerEntry(
                            player.gameProfile,
                            Date.from(Clock.System.now().toJavaInstant()),
                            "$source (using iwtcms)",
                            if (payload.expireDate != null) Date.from(payload.expireDate.toJavaInstant()) else null,
                            payload.reason
                        )
                   )

                    // don't forget to kick
                    player.networkHandler.disconnect(Text.translatable("multiplayer.disconnect.banned"))

                    logger.info("Iwtcms user $source successfully banned player ${player.name.string}")
                    call.respond("Player ${player.name.string} has been successfully banned")
                },
            )
        }
    }

    @Serializable
    private data class BanData @OptIn(ExperimentalTime::class) constructor(
        val username: String? = null,
        val uuid: String? = null,
        val expireDate: Instant? = null,
        val reason: String? = null,
    )
}
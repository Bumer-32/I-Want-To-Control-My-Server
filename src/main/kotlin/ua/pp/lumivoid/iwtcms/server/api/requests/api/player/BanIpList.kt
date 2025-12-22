package ua.pp.lumivoid.iwtcms.server.api.requests.api.player

import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import ua.pp.lumivoid.iwtcms.server.api.doAuth
import ua.pp.lumivoid.iwtcms.server.api.Request
import ua.pp.lumivoid.iwtcms.server.api.requests.api.user.PermissionsList
import ua.pp.lumivoid.iwtcms.util.MinecraftServerHandler
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

object BanIpList : Request() {
    override val path = "/api/player/banIpList"

    @OptIn(ExperimentalTime::class)
    override val request: Routing.() -> Unit = {
        get(path) {
            doAuth(
                call = call,
                permission = PermissionsList.Permission.PLAYERS_MANAGE.value,
                success = {
                    val banned = mutableListOf<BanIp>()
                    MinecraftServerHandler.server!!.playerManager.ipBanList.values().forEach {
                        banned.add(
                            BanIp(
                                target = it.toText().string,
                                reason = it.reason,
                                expireDate = if (it.expiryDate?.time != null) Instant.fromEpochMilliseconds(it.expiryDate!!.time) else null,
                                creationDate = if (it.creationDate?.time != null) Instant.fromEpochMilliseconds(it.creationDate!!.time) else null
                            )
                        )
                    }
                    call.respond(Json.encodeToString(banned))
                },
            )
        }
    }

    @Serializable
    private data class BanIp @OptIn(ExperimentalTime::class) constructor(
        val target: String,
        val reason: String,
        val expireDate: Instant?,
        val creationDate: Instant?,
    )
}
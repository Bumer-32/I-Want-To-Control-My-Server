package ua.pp.lumivoid.iwtcms.server.api.requests.api.player

import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import ua.pp.lumivoid.iwtcms.server.IWTCMS
import ua.pp.lumivoid.iwtcms.server.api.Request
import ua.pp.lumivoid.iwtcms.server.api.doAuth
import ua.pp.lumivoid.iwtcms.server.api.requests.api.user.PermissionsList
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

internal object BanIpList : Request() {
    override val path = "/api/player/banIpList"

    @OptIn(ExperimentalTime::class)
    override val request: Routing.() -> Unit = {
        get(path) {
            doAuth(
                call = call,
                permission = PermissionsList.Permission.PLAYERS_MANAGE.value,
                success = {
                    val banned = mutableListOf<BanIp>()
                    IWTCMS.instance.getMinecraftServer().playerList.ipBans.entries.forEach {
                        banned.add(
                            BanIp(
                                target = it.displayName.string,
                                reason = it.reason,
                                expireDate = if (it.expires?.time != null) Instant.fromEpochMilliseconds(it.expires!!.time) else null,
                                creationDate = if (it.created?.time != null) Instant.fromEpochMilliseconds(it.created!!.time) else null
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
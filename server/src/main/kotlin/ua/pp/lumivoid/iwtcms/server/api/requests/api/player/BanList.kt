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

internal object BanList : Request() {
    override val path = "/api/player/banList"

    @OptIn(ExperimentalTime::class)
    override val request: Routing.() -> Unit = {
        get(path) {
            doAuth(
                call = call,
                permission = PermissionsList.Permission.PLAYERS_MANAGE.value,
                success = {
                    val banned = mutableListOf<Ban>()
                    IWTCMS.instance.bans().forEach {
                        banned.add(
                            Ban(
                                target = it.player.name,
                                reason = it.reason,
                                expireDate = if (it.expireDate?.time != null) Instant.fromEpochMilliseconds(it.expireDate.time) else null,
                                creationDate = if (it.creationDate?.time != null) Instant.fromEpochMilliseconds(it.creationDate.time) else null
                            )
                        )
                    }
                    call.respond(Json.encodeToString(banned))
                },
            )
        }
    }

    @Serializable
    private data class Ban @OptIn(ExperimentalTime::class) constructor(
        val target: String,
        val reason: String?,
        val expireDate: Instant?,
        val creationDate: Instant?,
    )
}
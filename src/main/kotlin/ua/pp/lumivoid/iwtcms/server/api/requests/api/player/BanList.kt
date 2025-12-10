package ua.pp.lumivoid.iwtcms.server.api.requests.api.player

import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import kotlinx.serialization.json.Json
import ua.pp.lumivoid.iwtcms.server.api.doAuth
import ua.pp.lumivoid.iwtcms.server.api.Request
import ua.pp.lumivoid.iwtcms.server.api.requests.api.user.PermissionsList
import ua.pp.lumivoid.iwtcms.util.MinecraftServerHandler

object BanList : Request() {
    override val path = "/api/player/banList"

    override val request: Routing.() -> Unit = {
        get(path) {
            doAuth(
                call = call,
                permission = PermissionsList.Permission.PLAYERS_MANAGE.value,
                success = {
                    val banned = MinecraftServerHandler.server!!.playerManager.ipBanList.values().toList()
                    call.respond(Json.encodeToString(banned))
                },
            )
        }
    }
}
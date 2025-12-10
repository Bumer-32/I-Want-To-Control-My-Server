package ua.pp.lumivoid.iwtcms.server.api.requests.api.player

import io.ktor.server.routing.Routing
import io.ktor.server.routing.post
import ua.pp.lumivoid.iwtcms.server.api.doAuth
import ua.pp.lumivoid.iwtcms.server.api.Request
import ua.pp.lumivoid.iwtcms.server.api.requests.api.user.PermissionsList

object Kick : Request() {
    override val path = "/api/player/kick"

    override val request: Routing.() -> Unit = {
        post(path) {
            doAuth(
                call = call,
                permission = PermissionsList.Permission.PLAYERS_MANAGE.value,
                success = {
                },
            )
        }
    }
}
package ua.pp.lumivoid.iwtcms.ktor.api.requests

import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.post
import io.ktor.server.sessions.clear
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import ua.pp.lumivoid.iwtcms.ktor.cookie.UserSession

object Logout : Request() {
    override val path = "/api/logout"

    override val request: Routing.() -> Unit = {
        post(path) {
            val session = call.sessions.get<UserSession>()
            if (session == null) {
                call.respond("Not logged in")
                return@post
            }

            call.sessions.clear(UserSession::class)
            call.respond("Logged out")
        }
    }
}

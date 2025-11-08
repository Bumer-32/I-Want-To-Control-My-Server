package ua.pp.lumivoid.iwtcms.server.api.requests.api.authentication

import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.post
import io.ktor.server.sessions.clear
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import ua.pp.lumivoid.iwtcms.server.api.Request
import ua.pp.lumivoid.iwtcms.server.cookie.UserSession

object Logout : Request() {
    override val path = "/api/authentication/logout"

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
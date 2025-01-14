package ua.pp.lumivoid.iwtcms.ktor.api.requests

import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respondText
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import ua.pp.lumivoid.iwtcms.ktor.cookie.UserSession
import ua.pp.lumivoid.iwtcms.util.Config

object CheckLoginG: Request() {
    override val PATH = "/api/checkLogin"

    override val request: Routing.() -> Unit = {
        get(PATH) {
            val session = call.sessions.get<UserSession>()
            if (session == null) {
                call.respondText("Not logged in", status = HttpStatusCode.Unauthorized)
            }

            if (Config.readConfig().users.find { it.username == session?.name && it.id == session.id } != null) {
                call.respondText(session!!.name)
            } else {
                call.respondText("Not logged in", status = HttpStatusCode.Unauthorized)
            }
        }
    }
}
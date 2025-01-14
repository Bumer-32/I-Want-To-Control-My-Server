package ua.pp.lumivoid.iwtcms.ktor.api.requests

import io.ktor.http.ContentType
import io.ktor.server.response.respondText
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import ua.pp.lumivoid.iwtcms.util.Config

object IsAuthEnabledG: Request() {
    override val PATH = "/api/isAuthEnabled"

    override val request: Routing.() -> Unit = {
        get(PATH) {
            call.respondText(Config.readConfig().useAuthentication.toString(), ContentType.Text.Plain)
        }
    }
}
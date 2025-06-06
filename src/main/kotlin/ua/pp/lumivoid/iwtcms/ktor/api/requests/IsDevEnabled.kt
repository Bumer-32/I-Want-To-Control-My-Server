package ua.pp.lumivoid.iwtcms.ktor.api.requests

import io.ktor.http.ContentType
import io.ktor.server.response.respondText
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import ua.pp.lumivoid.iwtcms.ktor.util.Config

object IsDevEnabled : Request() {
    override val path = "/api/isDevEnabled"

    override val request: Routing.() -> Unit = {
        get(path) {
            call.respondText(Config.readConfig().devMode.toString(), ContentType.Text.Plain)
        }
    }
}

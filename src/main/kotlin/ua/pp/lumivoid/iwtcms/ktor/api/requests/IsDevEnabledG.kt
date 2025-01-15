package ua.pp.lumivoid.iwtcms.ktor.api.requests

import io.ktor.http.ContentType
import io.ktor.server.response.respondText
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import ua.pp.lumivoid.iwtcms.ktor.util.Config

object IsDevEnabledG: Request() {
    override val PATH = "/api/isDevEnabled"

    override val request: Routing.() -> Unit = {
        get(PATH) {
            call.respondText(Config.readConfig().devMode.toString(), ContentType.Text.Plain)
        }
    }
}
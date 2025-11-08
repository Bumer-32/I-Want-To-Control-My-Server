package ua.pp.lumivoid.iwtcms.server.api.requests.api

import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import ua.pp.lumivoid.iwtcms.server.api.Request
import ua.pp.lumivoid.iwtcms.server.util.Config

object IsDevEnabled : Request() {
    override val path = "/api/isDevEnabled"

    override val request: Routing.() -> Unit = {
        get(path) {
            call.respond(Config.readConfig().devMode.toString())
        }
    }
}
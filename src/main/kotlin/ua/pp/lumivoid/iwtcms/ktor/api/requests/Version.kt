package ua.pp.lumivoid.iwtcms.ktor.api.requests

import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import ua.pp.lumivoid.iwtcms.Constants

object Version : Request() {
    override val path = "/api/version"

    override val request: Routing.() -> Unit = {
        get(path) {
            call.respond(Constants.MOD_VERSION)
        }
    }
}

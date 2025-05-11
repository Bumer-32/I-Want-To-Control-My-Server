package ua.pp.lumivoid.iwtcms.ktor.api.requests

import io.ktor.http.ContentType
import io.ktor.server.response.respondText
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import ua.pp.lumivoid.iwtcms.Constants

object VersionG : Request() {
    override val path = "/api/version"

    override val request: Routing.() -> Unit = {
        get(path) {
            call.respondText(Constants.MOD_VERSION, ContentType.Text.Plain)
        }
    }
}

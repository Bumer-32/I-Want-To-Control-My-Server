package ua.pp.lumivoid.iwtcms.server.api.requests

import io.ktor.server.http.content.staticResources
import io.ktor.server.routing.Routing
import ua.pp.lumivoid.iwtcms.server.api.Request

object Files : Request() {
    override val path = "/files"

    override val request: Routing.() -> Unit = {
        staticResources(path, "files")
    }
}

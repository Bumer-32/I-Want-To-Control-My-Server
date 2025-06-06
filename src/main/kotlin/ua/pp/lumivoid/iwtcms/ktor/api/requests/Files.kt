package ua.pp.lumivoid.iwtcms.ktor.api.requests

import io.ktor.server.http.content.staticResources
import io.ktor.server.routing.Routing

object Files : Request() {
    override val path = "/files"

    override val request: Routing.() -> Unit = {
        staticResources(path, "files")
    }
}

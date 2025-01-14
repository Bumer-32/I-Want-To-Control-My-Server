package ua.pp.lumivoid.iwtcms.ktor.api.requests

import io.ktor.server.http.content.staticResources
import io.ktor.server.routing.Routing

object FilesG: Request() {
    override val PATH =  "/files"

    override val request: Routing.() -> Unit = {
        staticResources(PATH, "files")
    }
}
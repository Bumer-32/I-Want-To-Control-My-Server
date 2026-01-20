package ua.pp.lumivoid.iwtcms.server.api.requests

import io.ktor.server.http.content.*
import io.ktor.server.routing.*
import ua.pp.lumivoid.iwtcms.server.api.Request
import ua.pp.lumivoid.iwtcms.server.util.Config

internal object Main : Request() {
    override val path = "/"

    override val request: Routing.() -> Unit = {
        val webRoot = if (Config.readConfig().enableIWTCMSControlPanel) "web" else "static"
        staticResources(path, webRoot, "index.html")
    }
}

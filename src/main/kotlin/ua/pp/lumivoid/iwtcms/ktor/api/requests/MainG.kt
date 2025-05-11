package ua.pp.lumivoid.iwtcms.ktor.api.requests

import io.ktor.server.http.content.staticResources
import io.ktor.server.routing.Routing
import ua.pp.lumivoid.iwtcms.ktor.util.Config

object MainG : Request() {
    override val path = "/"

    override val request: Routing.() -> Unit = {
        if (Config.readConfig().enableIWTCMSControlPanel) {
            staticResources(path, "web", index = "index.html")
        } else {
            staticResources(path, "disabledWeb", index = "index.html")
        }
    }
}

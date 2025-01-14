package ua.pp.lumivoid.iwtcms.ktor.api.requests

import io.ktor.http.ContentType
import io.ktor.server.response.respondText
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import ua.pp.lumivoid.iwtcms.util.Config

object MCSettingsGP: Request() {
    override val PATH = "/api/mcSettings"

    override val request: Routing.() -> Unit = {
        get(PATH) {
            call.respondText(Config.readConfig().devMode.toString(), ContentType.Text.Plain)
        }

        post (PATH) {

        }
    }
}
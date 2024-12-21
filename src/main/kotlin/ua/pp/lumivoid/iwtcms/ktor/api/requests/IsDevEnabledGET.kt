package ua.pp.lumivoid.iwtcms.ktor.api.requests

import io.ktor.http.ContentType
import io.ktor.server.response.respondText
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import ua.pp.lumivoid.iwtcms.Constants
import ua.pp.lumivoid.iwtcms.ktor.api.requests.ApiListGET.registerAPI
import ua.pp.lumivoid.iwtcms.util.Config

object IsDevEnabledGET: Request() {
    override val logger = Constants.EMBEDDED_SERVER_LOGGER
    override val PATH = "/api/isDevEnabled"

    override val request: Routing.() -> Unit = {
        logger.info("Initializing $PATH request")
        registerAPI("IsDevEnabledGET",  PATH)

        get(PATH) {
            call.respondText(Config.readConfig().devMode.toString(), ContentType.Text.Plain)
        }
    }
}
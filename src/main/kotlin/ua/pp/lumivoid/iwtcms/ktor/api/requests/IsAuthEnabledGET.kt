package ua.pp.lumivoid.iwtcms.ktor.api.requests

import io.ktor.http.ContentType
import io.ktor.server.response.respondText
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import ua.pp.lumivoid.iwtcms.Constants
import ua.pp.lumivoid.iwtcms.ktor.api.requests.ApiListGET.registerAPI
import ua.pp.lumivoid.iwtcms.util.Config

object IsAuthEnabledGET: Request() {
    override val logger = Constants.EMBEDDED_SERVER_LOGGER
    override val PATH = "/api/isAuthEnabled"

    private val logs = mutableListOf<String>()

    override val request: Routing.() -> Unit = {
        logger.info("Initializing $PATH request")
        registerAPI("IsAuthEnabledGET",  PATH)

        get(PATH) {
            call.respondText(Config.readConfig().useAuthentication.toString(), ContentType.Text.Plain)
        }
    }
}
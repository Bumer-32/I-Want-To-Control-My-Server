package ua.pp.lumivoid.iwtcms.ktor.api.requests

import io.ktor.server.http.content.staticResources
import io.ktor.server.routing.Routing
import ua.pp.lumivoid.iwtcms.Constants
import ua.pp.lumivoid.iwtcms.ktor.api.requests.ApiListGET.registerAPI

object MainGET: Request() {
    override val logger = Constants.EMBEDDED_SERVER_LOGGER
    override val PATH =  "/"

    override val request: Routing.() -> Unit = {
        logger.info("Initializing $PATH request")
        registerAPI("MainGET", PATH)

        staticResources(PATH, "web", index = "index.html")
    }
}
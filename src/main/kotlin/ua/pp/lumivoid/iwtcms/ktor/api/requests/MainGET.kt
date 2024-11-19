package ua.pp.lumivoid.iwtcms.ktor.api.requests

import io.ktor.server.http.content.staticResources
import io.ktor.server.routing.Routing
import ua.pp.lumivoid.iwtcms.Constants

object MainGET {
    private val logger = Constants.EMBEDDED_SERVER_LOGGER

    private const val PATH =  "/"

    val request: Routing.() -> Unit = {
        logger.info("Initializing $PATH request")
        staticResources(PATH, "web", index = "index.html")
    }
}
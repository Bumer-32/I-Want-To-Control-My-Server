package ua.pp.lumivoid.iwtcms.ktor.api.requests

import io.ktor.server.http.content.staticResources
import io.ktor.server.routing.Routing
import ua.pp.lumivoid.iwtcms.Constants
import ua.pp.lumivoid.iwtcms.ktor.api.requests.ApiListGET.registerAPI

object FilesGET: Request() {
    override val logger = Constants.EMBEDDED_SERVER_LOGGER
    override val PATH =  "/files"

    override val request: Routing.() -> Unit = {
        logger.info("Initializing $PATH request")
        registerAPI("FilesGET", PATH)

        staticResources(PATH, "files")
    }
}
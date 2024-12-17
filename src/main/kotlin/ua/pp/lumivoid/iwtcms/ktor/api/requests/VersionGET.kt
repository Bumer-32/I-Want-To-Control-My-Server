package ua.pp.lumivoid.iwtcms.ktor.api.requests

import io.ktor.http.ContentType
import io.ktor.server.response.respondText
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import ua.pp.lumivoid.iwtcms.Constants
import ua.pp.lumivoid.iwtcms.ktor.api.requests.ApiListGET.registerAPI

object VersionGET: Request() {
    override val logger = Constants.EMBEDDED_SERVER_LOGGER
    override val PATH = "/api/iwtcmsVersion"

    override val request: Routing.() -> Unit = {
        logger.info("Initializing $PATH request")
        registerAPI("VersionGET",  PATH)

        get(PATH) {
            call.respondText(Constants.MOD_VERSION, ContentType.Text.Plain)
        }
    }
}
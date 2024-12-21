package ua.pp.lumivoid.iwtcms.ktor.api.dev

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readRawBytes
import io.ktor.http.contentType
import io.ktor.server.request.uri
import io.ktor.server.response.respondBytes
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import ua.pp.lumivoid.iwtcms.Constants
import ua.pp.lumivoid.iwtcms.ktor.api.requests.Request
import ua.pp.lumivoid.iwtcms.util.Config


object DevRequests: Request() {
    override val logger = Constants.EMBEDDED_SERVER_LOGGER
    override val PATH =  "/{...}"

    // TODO crashing if launching with built jar, needs to add includes

    override val request: Routing.() -> Unit = {
        logger.info("Initializing $PATH request")
        get(PATH) {
            val httpClient = HttpClient()
            val response: HttpResponse = httpClient.get("${Config.readConfig().proxyUrl}${call.request.uri}")
            call.respondBytes(
                bytes = response.readRawBytes(),
                contentType = response.contentType(),
                status = response.status
            )
        }
    }
}
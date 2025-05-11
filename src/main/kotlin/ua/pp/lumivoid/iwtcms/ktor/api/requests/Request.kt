package ua.pp.lumivoid.iwtcms.ktor.api.requests

import io.ktor.server.routing.Routing
import io.ktor.server.routing.RoutingRoot
import ua.pp.lumivoid.iwtcms.Constants
import ua.pp.lumivoid.iwtcms.ktor.api.requests.ApiListG.registerAPI

abstract class Request {
    protected val logger = Constants.EMBEDDED_SERVER_LOGGER

    protected abstract val path: String
    protected abstract val request: Routing.() -> Unit

    fun register(routing: RoutingRoot) {
        logger.info("Initializing ${this.javaClass.simpleName} request: $path")
        registerAPI(this.javaClass.simpleName, path)
        request.invoke(routing)
    }
}

package ua.pp.lumivoid.iwtcms.server.api

import io.ktor.server.routing.Routing
import io.ktor.server.routing.RoutingRoot
import ua.pp.lumivoid.iwtcms.server.Constants

internal abstract class Request {
    protected val logger = Constants.LOGGER

    protected abstract val path: String
    protected abstract val request: Routing.() -> Unit

    fun register(routing: RoutingRoot) {
        logger.info("Initializing ${this.javaClass.simpleName} request: $path")
        request.invoke(routing)
    }
}
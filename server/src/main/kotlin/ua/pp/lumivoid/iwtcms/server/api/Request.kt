package ua.pp.lumivoid.iwtcms.server.api

import io.ktor.server.routing.*
import ua.pp.lumivoid.iwtcms.server.Constants

internal abstract class Request {
    protected val logger = Constants.LOGGER

    abstract val path: String
    protected abstract val request: Routing.() -> Unit

    fun register(routing: RoutingRoot) {
        logger.info("Initializing ${this.javaClass.simpleName} request: $path")
        request.invoke(routing)
    }
}
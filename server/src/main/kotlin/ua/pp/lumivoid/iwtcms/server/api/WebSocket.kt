package ua.pp.lumivoid.iwtcms.server.api

import io.ktor.server.routing.Routing
import io.ktor.server.routing.RoutingRoot
import ua.pp.lumivoid.iwtcms.server.Constants

internal abstract class WebSocket {
    protected val logger = Constants.LOGGER

    protected abstract val path: String
    abstract val ws: Routing.() -> Unit

    fun register(routing: RoutingRoot) {
        logger.info("Initializing ${this.javaClass.simpleName} websocket: $path")
        ws.invoke(routing)
    }
}
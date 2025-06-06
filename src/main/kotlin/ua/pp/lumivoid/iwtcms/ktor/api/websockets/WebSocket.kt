package ua.pp.lumivoid.iwtcms.ktor.api.websockets

import io.ktor.server.routing.Routing
import io.ktor.server.routing.RoutingRoot
import ua.pp.lumivoid.iwtcms.Constants

abstract class WebSocket {
    protected val logger = Constants.EMBEDDED_SERVER_LOGGER

    protected abstract var wsInterface: WebSocketBaseInterface?
    protected abstract val path: String
    abstract val ws: Routing.() -> Unit

    abstract fun asWs(): WebSocketBaseInterface?

    fun register(routing: RoutingRoot) {
        logger.info("Initializing ${this.javaClass.simpleName} websocket: $path")
        ws.invoke(routing)
    }
}

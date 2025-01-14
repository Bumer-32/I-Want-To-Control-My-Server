package ua.pp.lumivoid.iwtcms.ktor.api.websockets

import io.ktor.server.routing.Routing
import io.ktor.server.routing.RoutingRoot
import ua.pp.lumivoid.iwtcms.Constants
import ua.pp.lumivoid.iwtcms.ktor.api.requests.ApiListG.registerAPI

abstract class WebSocket {
    protected val logger = Constants.EMBEDDED_SERVER_LOGGER

    protected abstract var WSinterface: WebSocketBaseInterface?
    protected abstract val PATH: String
    abstract val ws: Routing.() -> Unit
    abstract fun asWs(): WebSocketBaseInterface?

    fun register(routing: RoutingRoot) {
        logger.info("Initializing ${this.javaClass.simpleName} websocket: $PATH")
        registerAPI(this.javaClass.simpleName, PATH)
        ws.invoke(routing)
    }
}
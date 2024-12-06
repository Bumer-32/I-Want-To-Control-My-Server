package ua.pp.lumivoid.iwtcms.ktor.api.websockets

import io.ktor.server.routing.Routing
import org.slf4j.Logger

abstract class WS {
    protected abstract val logger: Logger
    protected abstract var WSinterface: WebSocketBaseInterface?
    protected abstract val PATH: String
    abstract val ws: Routing.() -> Unit
    abstract fun asWs(): WebSocketBaseInterface?
}
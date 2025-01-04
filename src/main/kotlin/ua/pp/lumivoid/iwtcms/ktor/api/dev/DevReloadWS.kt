@file:Suppress("DuplicatedCode")

package ua.pp.lumivoid.iwtcms.ktor.api.dev

import io.ktor.server.routing.Routing
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.CloseReason
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import ua.pp.lumivoid.iwtcms.Constants
import ua.pp.lumivoid.iwtcms.ktor.api.websockets.WebSocket
import ua.pp.lumivoid.iwtcms.ktor.api.websockets.WebSocketBaseInterface

object DevReloadWS: WebSocket() {
    override val logger = Constants.EMBEDDED_SERVER_LOGGER
    override var WSinterface: WebSocketBaseInterface? = null
    override val PATH = "/dev/reloadWS"

    override val ws: Routing.() -> Unit = {
        logger.info("Initializing $PATH websocket")

        webSocket(PATH) {
            send(Frame.Text("Connected to $PATH"))

            var running = true

            WSinterface = object : WebSocketBaseInterface {
                override fun sendMessage(message: String) {
                    launch {
                        send(Frame.Text(message))
                    }
                }
                override fun shutdown() {
                    logger.info("Сlosing $PATH websocket")
                    running = false
                    runBlocking { close(CloseReason(CloseReason.Codes.NORMAL, "shutting down server")) }
                }
            }

            runCatching {
                incoming.consumeEach { frame ->
                    if (frame is Frame.Text) {
                        // send on every message
                        send(Frame.Text("reload"))
                    }
                }
            }.onFailure { exception ->
                logger.error("WebSocket exception: $exception")
            }
        }
    }

    override fun asWs(): WebSocketBaseInterface? = WSinterface
}


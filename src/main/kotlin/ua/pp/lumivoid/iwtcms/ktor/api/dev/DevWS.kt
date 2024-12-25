package ua.pp.lumivoid.iwtcms.ktor.api.dev

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.http.HttpMethod
import io.ktor.server.request.uri
import io.ktor.server.routing.Routing
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.CloseReason
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import ua.pp.lumivoid.iwtcms.Constants
import ua.pp.lumivoid.iwtcms.ktor.api.websockets.WS
import ua.pp.lumivoid.iwtcms.ktor.api.websockets.WebSocketBaseInterface
import ua.pp.lumivoid.iwtcms.util.Config

object DevWS: WS() {
    override val logger = Constants.EMBEDDED_SERVER_LOGGER
    override var WSinterface: WebSocketBaseInterface? = null
    override val PATH = "/{...}"

    override val ws: Routing.() -> Unit = {
        logger.info("Initializing $PATH websocket")

        webSocket(PATH) {
            var running = true

            val client = Client(this@DevWS, call.request.uri)

            WSinterface = object : WebSocketBaseInterface {
                override fun sendMessage(message: String) {
                    runBlocking { send(Frame.Text(message)) }
                }
                override fun shutdown() {
                    running = false
                    client.asWs()?.shutdown()
                    runBlocking { close(CloseReason(CloseReason.Codes.NORMAL, "Client requested shutdown")) }
                }
            }

            runCatching {
                incoming.consumeEach { frame ->
                    if (frame is Frame.Text) {
                        client.asWs()?.sendMessage(frame.readText())
                    }
                }
            }.onFailure { exception ->
                logger.error("WebSocket exception: $exception")
            }

        }
    }

    override fun asWs(): WebSocketBaseInterface? = WSinterface

    private class Client(parent: DevWS, uri: String) {
        private var WSinterface: WebSocketBaseInterface? = null

        init {
            CoroutineScope(Dispatchers.Default).launch {
                val client = HttpClient(CIO) {
                    install(WebSockets)
                }

                client.webSocket(
                    method = HttpMethod.Get,
                    host = Config.readConfig().proxyWsUrl,
                    port = Config.readConfig().proxyWsPort,
                    path = uri
                ) {
                    var running = true

                    WSinterface = object : WebSocketBaseInterface {
                        override fun sendMessage(message: String) {
                            runBlocking { send(Frame.Text(message)) }
                        }
                        override fun shutdown() {
                            running = false
                            runBlocking { close(CloseReason(CloseReason.Codes.NORMAL, "Client requested shutdown")) }
                        }
                    }

                    while (running) {
                        val message = incoming.receive() as? Frame.Text
                        parent.asWs()?.sendMessage(message?.readText() ?: "")
                    }
                }
            }
        }

        fun asWs(): WebSocketBaseInterface? = WSinterface
    }
}


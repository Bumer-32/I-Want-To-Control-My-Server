package ua.pp.lumivoid.iwtcms.ktor.api.websockets

import io.ktor.http.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import ua.pp.lumivoid.iwtcms.Constants
import ua.pp.lumivoid.iwtcms.ktor.api.UserAuthentication
import ua.pp.lumivoid.iwtcms.ktor.api.requests.ApiListGET.registerAPI
import ua.pp.lumivoid.iwtcms.util.MinecraftServerHandler

object ConsoleWS: WS() {
    override val logger = Constants.EMBEDDED_SERVER_LOGGER
    override var WSinterface: WebSocketBaseInterface? = null
    override val PATH = "/ws/console" // why console? because we use this socket same as console, receive logs and send commands

    override val ws: Routing.() -> Unit = {
        logger.info("Initializing $PATH websocket")
        registerAPI("ConsoleWS", PATH)

        webSocket(PATH) {
            val status = UserAuthentication.doAuth(
                call = call,
                permit = "read real time logs",
                success = {},
                unauthorized = {
                    logger.debug("Unauthorized user tried to connect to $PATH websocket")
                    runBlocking{ close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Unauthorized")) } },
                forbidden = {
                    logger.debug("Forbidden user tried to connect to $PATH websocket")
                    runBlocking{ close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Forbidden")) }
                }
            )

            if (status != HttpStatusCode.OK) return@webSocket

            // and ws

            send(Frame.Text("Connected to $PATH"))

            WSinterface = object : WebSocketBaseInterface {
                override fun sendMessage(message: String) {
                    launch {
                        send(Frame.Text(message))
                    }
                }
                override fun shutdown() {
                    logger.info("Сlosing $PATH websocket")
                    runBlocking { close(CloseReason(CloseReason.Codes.NORMAL, "shutting down server")) }
                }
            }

            var allowExecution = false

            UserAuthentication.doAuth(
                call = call,
                permit = "execute commands",
                success = { allowExecution = true },
                unauthorized = { allowExecution = false },
                forbidden = { allowExecution = false }
            )

            if (allowExecution) {
                runCatching {
                    incoming.consumeEach { frame ->
                        if (frame is Frame.Text) {
                            val receivedText = frame.readText()
                            logger.info("Launching command: $receivedText")

                            try {
                                if (MinecraftServerHandler.server != null) {
                                    MinecraftServerHandler.server!!.commandManager.executeWithPrefix(
                                        MinecraftServerHandler.server!!.commandSource,
                                        receivedText
                                    )
                                }
                            } catch (e: Exception) {
                                e.stackTrace.forEach { logger.error(it.toString()) }
                            }
                        }
                    }
                }.onFailure { exception ->
                    logger.error("WebSocket exception: ${exception}")
                }
            }
        }
    }

    override fun asWs(): WebSocketBaseInterface? = WSinterface
}
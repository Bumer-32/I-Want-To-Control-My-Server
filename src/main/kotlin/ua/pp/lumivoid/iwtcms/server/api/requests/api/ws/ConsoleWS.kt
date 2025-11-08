package ua.pp.lumivoid.iwtcms.server.api.requests.api.ws

import io.ktor.http.HttpStatusCode
import io.ktor.server.routing.Routing
import io.ktor.server.routing.RoutingCall
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.CloseReason
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.channels.consumeEach
import ua.pp.lumivoid.iwtcms.server.api.WebSocket
import ua.pp.lumivoid.iwtcms.server.api.WebSocketBaseInterface
import ua.pp.lumivoid.iwtcms.server.api.requests.api.user.PermissionsList
import ua.pp.lumivoid.iwtcms.server.api.doAuth
import ua.pp.lumivoid.iwtcms.util.MinecraftServerHandler

object ConsoleWS : WebSocket() {
    override var wsInterface: WebSocketBaseInterface? = null
    override val path = "/ws/console" // why console? because we use this socket same as console, receive logs and send commands

    override val ws: Routing.() -> Unit = {
        webSocket(path) {
            val status = doAuth(
                call = call as RoutingCall,
                permission = PermissionsList.Permission.LOGS_READ.value,
                success = {},
                unauthorized = {
                    logger.debug("Unauthorized user tried to connect to $path websocket")
                    close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Unauthorized"))
                },
                forbidden = {
                    logger.debug("Forbidden user tried to connect to $path websocket")
                    close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Forbidden"))
                },
            )

            if (status != HttpStatusCode.OK) return@webSocket

            // and ws

            send(Frame.Text("Connected to $path"))

            wsInterface =
                object : WebSocketBaseInterface {
                    override suspend fun sendMessage(message: String) {
                        send(Frame.Text(message))
                    }

                    override suspend fun shutdown() {
                        logger.info("Closing $path websocket")
                        close(CloseReason(CloseReason.Codes.NORMAL, "shutting down server"))
                    }
                }

            var allowExecution = false

            doAuth(
                call = call as RoutingCall,
                permission = PermissionsList.Permission.COMMANDS_EXECUTE.value,
                success = { allowExecution = true },
                unauthorized = { allowExecution = false },
                forbidden = { allowExecution = false },
            )

            runCatching {
                incoming.consumeEach { frame ->
                    if (frame is Frame.Text && allowExecution) {
                        val receivedText = frame.readText()
                        logger.info("Launching command: $receivedText")

                        try {
                            if (MinecraftServerHandler.server != null) {
                                MinecraftServerHandler.server!!.commandManager.executeWithPrefix(
                                    MinecraftServerHandler.server!!.commandSource,
                                    receivedText,
                                )
                            }
                        } catch (e: Exception) {
                            e.stackTrace.forEach { logger.error(it.toString()) }
                        }
                    }
                }
            }.onFailure { exception ->
                logger.error("WebSocket exception: $exception")
            }
        }
    }

    override fun asWs(): WebSocketBaseInterface? = wsInterface
}

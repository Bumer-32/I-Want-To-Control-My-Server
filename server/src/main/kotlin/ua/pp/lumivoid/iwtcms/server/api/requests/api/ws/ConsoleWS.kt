package ua.pp.lumivoid.iwtcms.server.api.requests.api.ws

import io.ktor.http.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import ua.pp.lumivoid.iwtcms.server.IWTCMS
import ua.pp.lumivoid.iwtcms.server.api.WebSocket
import ua.pp.lumivoid.iwtcms.server.api.doAuth
import ua.pp.lumivoid.iwtcms.server.api.requests.api.user.PermissionsList

internal object ConsoleWS : WebSocket() {
    override val path = "/ws/console" // why console? because we use this socket same as console, receive logs and send commands

    private val messageResponseFlow = MutableSharedFlow<String>()
    private val sharedFlow = messageResponseFlow.asSharedFlow()

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

            if (status != HttpStatusCode.OK) {
                close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Auth Error"))
                return@webSocket
            }

            // and ws

            // Send
            val job = launch {
                sharedFlow.collect { message ->
                    send(Frame.Text(message))
                }
            }

            // Receive
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
                            val server = IWTCMS.instance.getMinecraftServer()
                            server.commands.performPrefixedCommand(
                                server.createCommandSourceStack(),
                                receivedText,
                            )
                        } catch (e: Exception) {
                            e.stackTrace.forEach { logger.error(it.toString()) }
                        }
                    }
                }
            }.onFailure { exception ->
                logger.error("WebSocket exception: $exception")
            }.also {
                job.cancel()
            }
        }
    }

    suspend fun send(message: String) {
        messageResponseFlow.emit(message)
    }
}

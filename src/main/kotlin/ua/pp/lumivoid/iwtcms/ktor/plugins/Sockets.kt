package ua.pp.lumivoid.iwtcms.ktor.plugins

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import ua.pp.lumivoid.iwtcms.Constants
import ua.pp.lumivoid.iwtcms.util.MinecraftServerHandler
import kotlin.time.Duration.Companion.seconds

private val logger = Constants.EMBEDDED_SERVER_LOGGER

private val message = MutableSharedFlow<String>(
    replay = 100,
    extraBufferCapacity = 50,
    onBufferOverflow = BufferOverflow.SUSPEND
)

fun Application.configureSockets() {
    install(WebSockets) {
        pingPeriod = 15.seconds
        timeout = 15.seconds
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    routing {
        webSocket("/ws/console") { // why console? because we use this socket same as console, receive logs and send commands
            send(Frame.Text("Connected to iwtcms logs"))

            val job = launch {
                message.asSharedFlow().collect {
                    send(Frame.Text(it))
                }
            }

            runCatching {
                incoming.consumeEach { frame ->
                    if (frame is Frame.Text) {
                        val receivedText = frame.readText()
                        logger.info("Launching command: $receivedText")

                        try {
                            if (MinecraftServerHandler.server != null) {
                                MinecraftServerHandler.server!!.commandManager.executeWithPrefix(MinecraftServerHandler.server!!.commandSource, receivedText)
                            }
                        } catch (e: Exception) {
                            e.stackTrace.forEach { logger.error(it.toString()) }
                        }
                    }
                }
            }.onFailure { exception ->
                logger.error("WebSocket exception: ${exception.localizedMessage}")
            }.also {
                job.cancel()
            }
        }
    }
}

fun broadcastIwtcmsMainWebSocket(data: String) {
    CoroutineScope(Dispatchers.Default).launch {
        message.emit(data)
    }
}

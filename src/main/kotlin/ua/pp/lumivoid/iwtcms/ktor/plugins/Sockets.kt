package ua.pp.lumivoid.iwtcms.ktor.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.pingPeriod
import io.ktor.server.websocket.timeout
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import ua.pp.lumivoid.iwtcms.Constants
import ua.pp.lumivoid.iwtcms.requests.RequestHandler
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
                        RequestHandler.processRequest(receivedText)
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

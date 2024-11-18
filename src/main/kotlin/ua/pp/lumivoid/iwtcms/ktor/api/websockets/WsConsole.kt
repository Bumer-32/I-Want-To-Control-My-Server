package ua.pp.lumivoid.iwtcms.ktor.api

import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import ua.pp.lumivoid.iwtcms.Constants
import ua.pp.lumivoid.iwtcms.util.MinecraftServerHandler

interface WsConsole {
    fun sendMessage(message: String) {}
    fun shutdown() {}
}

object WsConsoleImpl {
    private val logger = Constants.EMBEDDED_SERVER_LOGGER

    private var wsConsole: WsConsole? = null

    val ws: Routing.() -> Unit = {
        logger.info("Initializing /ws/console websocket")
        webSocket("/ws/console") { // why console? because we use this socket same as console, receive logs and send commands
            send(Frame.Text("Connected to iwtcms logs"))

            wsConsole = object : WsConsole {
                override fun sendMessage(message: String) {
                    launch {
                        send(Frame.Text(message))
                    }
                }
                override fun shutdown() {
                    logger.info("Сlosing /ws/console websocket")
                    runBlocking { close(CloseReason(CloseReason.Codes.NORMAL, "shutting down server")) }
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
            }
        }
    }

    fun asWsConsole(): WsConsole? = wsConsole
}
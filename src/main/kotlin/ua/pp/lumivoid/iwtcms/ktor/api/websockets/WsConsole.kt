package ua.pp.lumivoid.iwtcms.ktor.api.websockets

import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respondText
import io.ktor.server.routing.*
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import ua.pp.lumivoid.iwtcms.Constants
import ua.pp.lumivoid.iwtcms.ktor.api.User
import ua.pp.lumivoid.iwtcms.ktor.cookie.UserSession
import ua.pp.lumivoid.iwtcms.util.Config
import ua.pp.lumivoid.iwtcms.util.MinecraftServerHandler

interface WsConsole {
    fun sendMessage(message: String) {}
    fun shutdown() {}
}

object WsConsoleImpl {
    private val logger = Constants.EMBEDDED_SERVER_LOGGER

    private var wsConsole: WsConsole? = null

    private const val PATH = "/ws/console/"

    val ws: Routing.() -> Unit = {
        logger.info("Initializing $PATH websocket")
        webSocket(PATH) { // why console? because we use this socket same as console, receive logs and send commands

            //authentication

            var user: User? = null

            if (Config.readConfig().useAuthentication) {

                val session = call.sessions.get<UserSession>()

                if (Config.readConfig().users.any {user = it; it.id == session?.id}) {
                    if (user!!.permits["read real time logs"] == false) {
                        close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Forbidden"))
                        return@webSocket
                    }
                } else {
                    close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Unauthorized"))
                    return@webSocket
                }
            }

            // and ws

            send(Frame.Text("Connected to iwtcms logs"))

            wsConsole = object : WsConsole {
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

            if ((Config.readConfig().useAuthentication && user!!.permits.get("execute commands") == true) || !Config.readConfig().useAuthentication) {
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
                    logger.error("WebSocket exception: ${exception.localizedMessage}")
                }
            }
        }
    }

    fun asWsConsole(): WsConsole? = wsConsole
}
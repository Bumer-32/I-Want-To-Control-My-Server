package ua.pp.lumivoid.iwtcms.ktor.api.websockets

import io.ktor.http.HttpStatusCode
import io.ktor.server.routing.Routing
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.CloseReason
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ua.pp.lumivoid.iwtcms.Constants
import ua.pp.lumivoid.iwtcms.ktor.api.UserAuthentication
import ua.pp.lumivoid.iwtcms.ktor.api.requests.ApiListGET.registerAPI
import ua.pp.lumivoid.iwtcms.util.Config
import ua.pp.lumivoid.iwtcms.util.ServerStats

object ServerStatsWS: WS() {
    override val logger = Constants.EMBEDDED_SERVER_LOGGER
    override var WSinterface: WebSocketBaseInterface? = null
    override val PATH = "/ws/serverStats"

    private val json = Json { prettyPrint = true }

    override val ws: Routing.() -> Unit = {
        logger.info("Initializing $PATH websocket")
        registerAPI("ServerStatsWS", PATH)

        webSocket(PATH) {
            val status = UserAuthentication.doAuth(
                call = call,
                permit = "access to server stats",
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

            val job = launch {
                while (running) {
                    val stats = ServerStats.getServerStats()
                    val statsJson = json.encodeToString(stats)
                    send(Frame.Text(statsJson))
                    delay(Config.readConfig().statisticsPeriod.toLong())
                }
            }

            runCatching {
                incoming.consumeEach { frame ->
                    if (frame is Frame.Text) {
                        // send on avery message
                        val stats = ServerStats.getServerStats()
                        val statsJson = json.encodeToString(stats)
                        send(Frame.Text(statsJson))
                    }
                }
            }.onFailure { exception ->
                logger.error("WebSocket exception: ${exception}")
            }.also {
                job.cancel()
            }
        }
    }

    override fun asWs(): WebSocketBaseInterface? = WSinterface
}
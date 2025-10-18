package ua.pp.lumivoid.iwtcms.ktor.api.websockets

import io.ktor.http.HttpStatusCode
import io.ktor.server.routing.Routing
import io.ktor.server.routing.RoutingCall
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.CloseReason
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import ua.pp.lumivoid.iwtcms.ktor.api.requests.PermissionsList
import ua.pp.lumivoid.iwtcms.ktor.api.doAuth
import ua.pp.lumivoid.iwtcms.ktor.util.Config
import ua.pp.lumivoid.iwtcms.util.ServerStats

object ServerStatsWS : WebSocket() {
    override var wsInterface: WebSocketBaseInterface? = null
    override val path = "/ws/serverStats"

    private val json = Json { prettyPrint = true }

    override val ws: Routing.() -> Unit = {
        webSocket(path) {
            val status =
                doAuth(
                    call = call as RoutingCall,
                    permission = PermissionsList.Permission.SERVER_STATS_READ.value,
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

            var running = true

            wsInterface =
                object : WebSocketBaseInterface {
                    override suspend fun sendMessage(message: String) {
                        send(Frame.Text(message))
                    }

                    override suspend fun shutdown() {
                        logger.info("Closing $path websocket")
                        running = false
                        close(CloseReason(CloseReason.Codes.NORMAL, "shutting down server"))
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
                        // send on every message
                        val stats = ServerStats.getServerStats()
                        val statsJson = json.encodeToString(stats)
                        send(Frame.Text(statsJson))
                    }
                }
            }.onFailure { exception ->
                logger.error("WebSocket exception: $exception")
            }.also {
                job.cancel()
            }
        }
    }

    override fun asWs(): WebSocketBaseInterface? = wsInterface
}

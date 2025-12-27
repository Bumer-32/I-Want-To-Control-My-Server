package ua.pp.lumivoid.iwtcms.server.api.requests.api.ws

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
import ua.pp.lumivoid.iwtcms.server.api.WebSocket
import ua.pp.lumivoid.iwtcms.server.api.requests.api.user.PermissionsList
import ua.pp.lumivoid.iwtcms.server.api.doAuth
import ua.pp.lumivoid.iwtcms.server.util.Config
import ua.pp.lumivoid.iwtcms.server.util.ServerStats

internal object ServerStatsWS : WebSocket() {
    override val path = "/ws/serverStats"

    private val sendPeriod = Config.readConfig().statisticsPeriod.toLong()

        override val ws: Routing.() -> Unit = {
        webSocket(path) {
            val status = doAuth(
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

            if (status != HttpStatusCode.OK) {
                close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Auth Error"))
                return@webSocket
            }

            // and ws

            send(Frame.Text("Connected to $path"))

            // Send
            val job = launch {
                while (true) {
                    val stats = ServerStats.getServerStats()
                    val statsJson = Json.encodeToString(stats)
                    send(Frame.Text(statsJson))
                    delay(sendPeriod)
                }
            }

            // Receive
            runCatching {
                incoming.consumeEach { frame ->
                    if (frame is Frame.Text) {
                        // send on every message
                        val stats = ServerStats.getServerStats()
                        val statsJson = Json.encodeToString(stats)
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
}

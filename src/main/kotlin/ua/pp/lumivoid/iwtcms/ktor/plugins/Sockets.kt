package ua.pp.lumivoid.iwtcms.ktor.plugins

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import ua.pp.lumivoid.iwtcms.ktor.api.WsConsoleImpl
import kotlin.time.Duration.Companion.seconds

fun Application.configureSockets() {
    install(WebSockets) {
        pingPeriod = 15.seconds
        timeout = 15.seconds
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }


    WsConsoleImpl.ws.invoke(routing {})
}

package ua.pp.lumivoid.iwtcms.ktor.plugins

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import ua.pp.lumivoid.iwtcms.ktor.api.WsConsoleImpl
import ua.pp.lumivoid.iwtcms.ktor.api.requests.LogsHistoryPage
import ua.pp.lumivoid.iwtcms.ktor.api.requests.MainPage
import kotlin.time.Duration.Companion.seconds

fun Application.configureRouting() {
    install(WebSockets) {
        pingPeriod = 15.seconds
        timeout = 15.seconds
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    val r = routing {}

    MainPage.request.invoke(r)
    LogsHistoryPage.request.invoke(r)

    WsConsoleImpl.ws.invoke(r)
}

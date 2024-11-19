package ua.pp.lumivoid.iwtcms.ktor.plugins

import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.websocket.*
import ua.pp.lumivoid.iwtcms.ktor.api.websockets.WsConsoleImpl
import ua.pp.lumivoid.iwtcms.ktor.api.requests.LoginPOST
import ua.pp.lumivoid.iwtcms.ktor.api.requests.LogsHistoryGET
import ua.pp.lumivoid.iwtcms.ktor.api.requests.MainGET
import ua.pp.lumivoid.iwtcms.ktor.cookie.UserSession
import kotlin.time.Duration.Companion.seconds

fun Application.configureRouting() {
    install(WebSockets) {
        pingPeriod = 15.seconds
        timeout = 15.seconds
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    install(Sessions) {
        cookie<UserSession>("USER_SESSION") {
            cookie.httpOnly = true
            cookie.secure = true
        }
    }

    install(ContentNegotiation) {
        json()
    }

    val r = routing {
    }

    MainGET.request.invoke(r)
    LogsHistoryGET.request.invoke(r)
    LoginPOST.request.invoke(r)

    WsConsoleImpl.ws.invoke(r)
}
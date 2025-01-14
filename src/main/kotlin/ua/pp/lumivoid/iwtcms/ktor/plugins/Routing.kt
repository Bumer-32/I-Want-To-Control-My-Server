package ua.pp.lumivoid.iwtcms.ktor.plugins

import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.plugins.statuspages.statusFile
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.websocket.*
import ua.pp.lumivoid.iwtcms.Constants
import ua.pp.lumivoid.iwtcms.ktor.api.dev.DevReloadWS
import ua.pp.lumivoid.iwtcms.ktor.api.requests.ApiListG
import ua.pp.lumivoid.iwtcms.ktor.api.requests.CheckLoginG
import ua.pp.lumivoid.iwtcms.ktor.api.requests.FilesG
import ua.pp.lumivoid.iwtcms.ktor.api.requests.IsAuthEnabledG
import ua.pp.lumivoid.iwtcms.ktor.api.requests.IsDevEnabledG
import ua.pp.lumivoid.iwtcms.ktor.api.requests.LoginP
import ua.pp.lumivoid.iwtcms.ktor.api.requests.LogoutP
import ua.pp.lumivoid.iwtcms.ktor.api.requests.LogsHistoryG
import ua.pp.lumivoid.iwtcms.ktor.api.requests.MainG
import ua.pp.lumivoid.iwtcms.ktor.api.requests.PermitsG
import ua.pp.lumivoid.iwtcms.ktor.api.requests.VersionG
import ua.pp.lumivoid.iwtcms.ktor.api.websockets.ConsoleWS
import ua.pp.lumivoid.iwtcms.ktor.api.websockets.ServerStatsWS
import ua.pp.lumivoid.iwtcms.ktor.cookie.UserSession
import ua.pp.lumivoid.iwtcms.util.Config
import kotlin.time.Duration.Companion.seconds

fun Application.configureRouting() {
    val logger = Constants.EMBEDDED_SERVER_LOGGER

    install(WebSockets) {
        pingPeriod = 15.seconds
        timeout = 15.seconds
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    install(Sessions) {
        cookie<UserSession>("USER_SESSION") {
            cookie.httpOnly = true
            cookie.secure = Config.readConfig().useSSL
            //cookie.sameSite = "None"
        }
    }

    install(ContentNegotiation) {
        json()
    }

    install(StatusPages) {
        statusFile(HttpStatusCode.NotFound, filePattern = "/web/404.html")
    }

    install(CORS) {
        anyHost()
        allowHeader(HttpHeaders.ContentType)
        allowCredentials = true
    }

    val r = routing {
    }

    logger.info("-=-=-=-=-=-=-=-=-=- Registering routes -=-=-=-=-=-=-=-=-=-")

    MainG.register(r)
    LogsHistoryG.register(r)
    LoginP.register(r)
    ApiListG.register(r)
    PermitsG.register(r)
    IsAuthEnabledG.register(r)
    FilesG.register(r)
    VersionG.register(r)
    CheckLoginG.register(r)
    IsDevEnabledG.register(r)
    LogoutP.register(r)

    ConsoleWS.register(r)
    ServerStatsWS.register(r)

    if (Config.readConfig().devMode) {
        DevReloadWS.register(r)
    }

    logger.info("-=-=-=-=-=-=-=-=-=- Routes registered -=-=-=-=-=-=-=-=-=-")
}
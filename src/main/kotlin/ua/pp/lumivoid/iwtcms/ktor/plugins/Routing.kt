package ua.pp.lumivoid.iwtcms.ktor.plugins

import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.plugins.statuspages.statusFile
import io.ktor.server.routing.routing
import io.ktor.server.sessions.Sessions
import io.ktor.server.sessions.cookie
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.pingPeriod
import io.ktor.server.websocket.timeout
import ua.pp.lumivoid.iwtcms.Constants
import ua.pp.lumivoid.iwtcms.ktor.api.requests.ApiListG
import ua.pp.lumivoid.iwtcms.ktor.api.requests.CheckLoginG
import ua.pp.lumivoid.iwtcms.ktor.api.requests.Configs
import ua.pp.lumivoid.iwtcms.ktor.api.requests.CreateUserP
import ua.pp.lumivoid.iwtcms.ktor.api.requests.DeleteUserP
import ua.pp.lumivoid.iwtcms.ktor.api.requests.EditPermissionsP
import ua.pp.lumivoid.iwtcms.ktor.api.requests.FilesG
import ua.pp.lumivoid.iwtcms.ktor.api.requests.IsAllowedG
import ua.pp.lumivoid.iwtcms.ktor.api.requests.IsDevEnabledG
import ua.pp.lumivoid.iwtcms.ktor.api.requests.LoginP
import ua.pp.lumivoid.iwtcms.ktor.api.requests.LogoutP
import ua.pp.lumivoid.iwtcms.ktor.api.requests.LogsHistoryG
import ua.pp.lumivoid.iwtcms.ktor.api.requests.MainG
import ua.pp.lumivoid.iwtcms.ktor.api.requests.VersionG
import ua.pp.lumivoid.iwtcms.ktor.api.websockets.ConsoleWS
import ua.pp.lumivoid.iwtcms.ktor.api.websockets.ServerStatsWS
import ua.pp.lumivoid.iwtcms.ktor.cookie.UserSession
import ua.pp.lumivoid.iwtcms.ktor.util.Config
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
            // cookie.sameSite = "None"
        }
    }

    install(ContentNegotiation) {
        json()
    }

    install(StatusPages) {
        statusFile(HttpStatusCode.NotFound, filePattern = "/web/404.html")

        exception<Throwable> { call, cause ->
            cause.stackTrace.forEach { logger.error(it.toString()) }
        }
    }

    if (Config.readConfig().devMode) {
        install(CORS) {
            anyHost()
        }
    }

    val r = routing {}

    logger.info("-=-=-=-=-=-=-=-=-=- Registering routes -=-=-=-=-=-=-=-=-=-")

    MainG.register(r)
    LogsHistoryG.register(r)
    LoginP.register(r)
    ApiListG.register(r)
    IsAllowedG.register(r)
    FilesG.register(r)
    VersionG.register(r)
    CheckLoginG.register(r)
    IsDevEnabledG.register(r)
    LogoutP.register(r)
    Configs.register(r)
    CreateUserP.register(r)
    DeleteUserP.register(r)
    EditPermissionsP.register(r)

    ConsoleWS.register(r)
    ServerStatsWS.register(r)

    logger.info("-=-=-=-=-=-=-=-=-=- Routes registered -=-=-=-=-=-=-=-=-=-")
}

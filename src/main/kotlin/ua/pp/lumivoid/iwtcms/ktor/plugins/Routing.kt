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
import ua.pp.lumivoid.iwtcms.ktor.api.requests.CheckLogin
import ua.pp.lumivoid.iwtcms.ktor.api.requests.Configs
import ua.pp.lumivoid.iwtcms.ktor.api.requests.CreateUser
import ua.pp.lumivoid.iwtcms.ktor.api.requests.DeleteUser
import ua.pp.lumivoid.iwtcms.ktor.api.requests.EditPermissions
import ua.pp.lumivoid.iwtcms.ktor.api.requests.Files
import ua.pp.lumivoid.iwtcms.ktor.api.requests.IsAllowed
import ua.pp.lumivoid.iwtcms.ktor.api.requests.IsDevEnabled
import ua.pp.lumivoid.iwtcms.ktor.api.requests.Login
import ua.pp.lumivoid.iwtcms.ktor.api.requests.Logout
import ua.pp.lumivoid.iwtcms.ktor.api.requests.LogsHistory
import ua.pp.lumivoid.iwtcms.ktor.api.requests.Main
import ua.pp.lumivoid.iwtcms.ktor.api.requests.UsersList
import ua.pp.lumivoid.iwtcms.ktor.api.requests.Version
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

    Main.register(r)
    Files.register(r)
    Version.register(r)
    Login.register(r)
    CheckLogin.register(r)
    Logout.register(r)
    IsAllowed.register(r)
    LogsHistory.register(r)
    IsDevEnabled.register(r)
    Configs.register(r)
    UsersList.register(r)
    CreateUser.register(r)
    DeleteUser.register(r)
    EditPermissions.register(r)

    ConsoleWS.register(r)
    ServerStatsWS.register(r)

    logger.info("-=-=-=-=-=-=-=-=-=- Routes registered -=-=-=-=-=-=-=-=-=-")
}

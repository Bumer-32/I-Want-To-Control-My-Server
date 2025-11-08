package ua.pp.lumivoid.iwtcms.server

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
import ua.pp.lumivoid.iwtcms.server.api.requests.api.authentication.CheckLogin
import ua.pp.lumivoid.iwtcms.server.api.requests.api.Configs
import ua.pp.lumivoid.iwtcms.server.api.requests.api.user.CreateUser
import ua.pp.lumivoid.iwtcms.server.api.requests.api.user.DeleteUser
import ua.pp.lumivoid.iwtcms.server.api.requests.api.user.EditPermissions
import ua.pp.lumivoid.iwtcms.server.api.requests.Files
import ua.pp.lumivoid.iwtcms.server.api.requests.api.user.IsAllowed
import ua.pp.lumivoid.iwtcms.server.api.requests.api.IsDevEnabled
import ua.pp.lumivoid.iwtcms.server.api.requests.api.authentication.Login
import ua.pp.lumivoid.iwtcms.server.api.requests.api.authentication.Logout
import ua.pp.lumivoid.iwtcms.server.api.requests.api.LogsHistory
import ua.pp.lumivoid.iwtcms.server.api.requests.Main
import ua.pp.lumivoid.iwtcms.server.api.requests.api.user.PermissionsList
import ua.pp.lumivoid.iwtcms.server.api.requests.api.user.UsersList
import ua.pp.lumivoid.iwtcms.server.api.requests.api.Version
import ua.pp.lumivoid.iwtcms.server.api.requests.api.ws.ConsoleWS
import ua.pp.lumivoid.iwtcms.server.api.requests.api.ws.ServerStatsWS
import ua.pp.lumivoid.iwtcms.server.cookie.UserSession
import ua.pp.lumivoid.iwtcms.server.util.Config
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

        exception<Throwable> { _, cause ->
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

    // * /api/authentication
    CheckLogin.register(r)
    Login.register(r)
    Logout.register(r)

    // * /api/user
    CreateUser.register(r)
    DeleteUser.register(r)
    EditPermissions.register(r)
    IsAllowed.register(r)
    PermissionsList.register(r)
    UsersList.register(r)

    // * /api/ws
    ConsoleWS.register(r)
    ServerStatsWS.register(r)

    // * /api
    Configs.register(r)
    IsDevEnabled.register(r)
    LogsHistory.register(r)
    Version.register(r)

    // * /
    Files.register(r)
    Main.register(r)



    logger.info("-=-=-=-=-=-=-=-=-=- Routes registered -=-=-=-=-=-=-=-=-=-")
}

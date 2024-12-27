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
import ua.pp.lumivoid.iwtcms.ktor.api.dev.DevReloadWS
import ua.pp.lumivoid.iwtcms.ktor.api.requests.ApiListGET
import ua.pp.lumivoid.iwtcms.ktor.api.requests.CheckLoginGET
import ua.pp.lumivoid.iwtcms.ktor.api.requests.FilesGET
import ua.pp.lumivoid.iwtcms.ktor.api.requests.IsAuthEnabledGET
import ua.pp.lumivoid.iwtcms.ktor.api.requests.IsDevEnabledGET
import ua.pp.lumivoid.iwtcms.ktor.api.requests.LoginPOST
import ua.pp.lumivoid.iwtcms.ktor.api.requests.LogoutPOST
import ua.pp.lumivoid.iwtcms.ktor.api.requests.LogsHistoryGET
import ua.pp.lumivoid.iwtcms.ktor.api.requests.MainGET
import ua.pp.lumivoid.iwtcms.ktor.api.requests.PermitsGET
import ua.pp.lumivoid.iwtcms.ktor.api.requests.VersionGET
import ua.pp.lumivoid.iwtcms.ktor.api.websockets.ConsoleWS
import ua.pp.lumivoid.iwtcms.ktor.api.websockets.ServerStatsWS
import ua.pp.lumivoid.iwtcms.ktor.cookie.UserSession
import ua.pp.lumivoid.iwtcms.util.Config
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

    MainGET.request.invoke(r)
    LogsHistoryGET.request.invoke(r)
    LoginPOST.request.invoke(r)
    ApiListGET.request.invoke(r)
    PermitsGET.request.invoke(r)
    IsAuthEnabledGET.request.invoke(r)
    FilesGET.request.invoke(r)
    VersionGET.request.invoke(r)
    CheckLoginGET.request.invoke(r)
    IsDevEnabledGET.request.invoke(r)
    LogoutPOST.request.invoke(r)

    ConsoleWS.ws.invoke(r)
    ServerStatsWS.ws.invoke(r)

    if (Config.readConfig().devMode) {
        DevReloadWS.ws.invoke(r)
    }
}
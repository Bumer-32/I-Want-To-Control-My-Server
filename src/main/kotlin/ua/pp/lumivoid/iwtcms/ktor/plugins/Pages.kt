package ua.pp.lumivoid.iwtcms.ktor.plugins

import io.ktor.server.application.*
import io.ktor.server.routing.*
import ua.pp.lumivoid.iwtcms.ktor.api.requests.LogsHistoryPage
import ua.pp.lumivoid.iwtcms.ktor.api.requests.MainPage

fun Application.configureRouting() {
    MainPage.request.invoke(routing {})
    LogsHistoryPage.request.invoke(routing {})
}
package ua.pp.lumivoid.iwtcms.ktor.api.requests

import io.ktor.http.ContentType
import io.ktor.server.response.respondText
import io.ktor.server.routing.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ua.pp.lumivoid.iwtcms.Constants

object LogsHistoryPage {
    private val logger = Constants.EMBEDDED_SERVER_LOGGER
    private val json = Json { prettyPrint = true }

    private val logs = mutableListOf<String>()

    val request: Routing.() -> Unit = {
        logger.info("Initializing /api/logsHistory/ request")
        get("/api/logsHistory/") {
            val response = json.encodeToString(logs)

            call.respondText(response, contentType = ContentType.Text.Plain)
        }
    }

    fun addLog(log: String) {
        logs.add(log)
    }
}
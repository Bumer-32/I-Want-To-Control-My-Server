package ua.pp.lumivoid.iwtcms.ktor.api.requests

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ua.pp.lumivoid.iwtcms.Constants
import ua.pp.lumivoid.iwtcms.ktor.api.UserAuthentication
import ua.pp.lumivoid.iwtcms.ktor.api.requests.ApiListGET.registerAPI

object LogsHistoryGET {
    private val logger = Constants.EMBEDDED_SERVER_LOGGER
    private val json = Json { prettyPrint = true }

    private val logs = mutableListOf<String>()

    private const val PATH = "/api/logsHistory"

    val request: Routing.() -> Unit = {
        logger.info("Initializing $PATH request")
        registerAPI("LogsHistoryGET",  PATH)

        get(PATH) {
            UserAuthentication.doAuth(
                call = call,
                permit = "read logs history",
                success = {
                    val response = json.encodeToString(logs)
                    runBlocking { call.respondText(response, contentType = ContentType.Text.Plain) }
                },
                forbidden = { runBlocking { call.respondText("Forbidden", status = HttpStatusCode.Forbidden) } }
            )
        }
    }

    fun addLog(log: String) {
        logs.add(log)
    }
}
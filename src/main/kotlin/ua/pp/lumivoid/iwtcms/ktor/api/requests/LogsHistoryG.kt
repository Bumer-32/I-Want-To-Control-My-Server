package ua.pp.lumivoid.iwtcms.ktor.api.requests

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ua.pp.lumivoid.iwtcms.ktor.api.UserAuthentication

object LogsHistoryG: Request() {
    override val PATH = "/api/logsHistory"

    private val json = Json { prettyPrint = true }
    private val logs = mutableListOf<String>()

    override val request: Routing.() -> Unit = {
        get(PATH) {
            UserAuthentication.doAuth(
                call = call,
                permit = "read logs history",
                success = {
                    val response = json.encodeToString(logs)
                    runBlocking { call.respondText(response, contentType = ContentType.Text.Plain) }
                }
            )
        }
    }

    fun addLog(log: String) {
        logs.add(log)
    }
}
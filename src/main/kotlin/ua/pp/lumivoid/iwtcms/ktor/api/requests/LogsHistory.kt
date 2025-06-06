package ua.pp.lumivoid.iwtcms.ktor.api.requests

import io.ktor.http.ContentType
import io.ktor.server.response.respondText
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import ua.pp.lumivoid.iwtcms.ktor.api.doAuth

object LogsHistory : Request() {
    override val path = "/api/logsHistory"

    private val json = Json { prettyPrint = true }
    private val logs = mutableListOf<String>()

    override val request: Routing.() -> Unit = {
        get(path) {
            doAuth(
                call = call,
                permission = "read logs history",
                success = {
                    val response = json.encodeToString(logs)
                    runBlocking { call.respondText(response, contentType = ContentType.Text.Plain) }
                },
            )
        }
    }

    fun addLog(log: String) {
        logs.add(log)
    }
}

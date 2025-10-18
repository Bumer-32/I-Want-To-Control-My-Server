package ua.pp.lumivoid.iwtcms.ktor.api.requests

import io.ktor.server.response.*
import io.ktor.server.routing.*
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
                permission = PermissionsList.Permission.LOGS_READ.value,
                success = {
                    val response = json.encodeToString(logs)
                    call.respond(response)
                },
            )
        }
    }

    fun addLog(log: String) {
        logs.add(log)
    }
}

package ua.pp.lumivoid.iwtcms.server.api.requests.api

import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import kotlinx.serialization.json.Json
import ua.pp.lumivoid.iwtcms.server.api.doAuth
import ua.pp.lumivoid.iwtcms.server.api.Request
import ua.pp.lumivoid.iwtcms.server.api.requests.api.user.PermissionsList

internal object LogsHistory : Request() {
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
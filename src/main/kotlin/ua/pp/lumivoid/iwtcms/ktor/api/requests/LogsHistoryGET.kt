package ua.pp.lumivoid.iwtcms.ktor.api.requests

import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respondText
import io.ktor.server.routing.*
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ua.pp.lumivoid.iwtcms.Constants
import ua.pp.lumivoid.iwtcms.ktor.api.User
import ua.pp.lumivoid.iwtcms.ktor.cookie.UserSession
import ua.pp.lumivoid.iwtcms.util.Config

object LogsHistoryGET {
    private val logger = Constants.EMBEDDED_SERVER_LOGGER
    private val json = Json { prettyPrint = true }

    private val logs = mutableListOf<String>()

    private const val PATH = "/api/logsHistory/"

    val request: Routing.() -> Unit = {
        logger.info("Initializing $PATH request")
        get(PATH) {
            if (Config.readConfig().useAuthentication) {

                val session = call.sessions.get<UserSession>()

                var user: User? = null

                if (Config.readConfig().users.any {user = it; it.id == session?.id}) {
                    if (user!!.permits["read logs history"] == false) {
                        call.respondText("Forbidden", status = HttpStatusCode.Forbidden)
                        return@get
                    }
                } else {
                    call.respondText("Unauthorized", status = HttpStatusCode.Unauthorized)
                    return@get
                }
            }

            val response = json.encodeToString(logs)
            call.respondText(response, contentType = ContentType.Text.Plain)
        }
    }

    fun addLog(log: String) {
        logs.add(log)
    }
}
package ua.pp.lumivoid.iwtcms.ktor.api.requests

import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respondText
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ua.pp.lumivoid.iwtcms.ktor.api.User
import ua.pp.lumivoid.iwtcms.ktor.util.Config

object PermitsG: Request() {
    override val PATH = "/api/permits/{username}"

    private val json = Json { prettyPrint = true }

    override val request: Routing.() -> Unit = {
        get(PATH) {
            val user: User? = Config.readConfig().users.find { it.username == call.parameters["username"] }
            if (user != null) {
                val response = json.encodeToString(user.permits)
                call.respondText(response, contentType = ContentType.Text.Plain)
            } else {
                val anonymousUser: User? = Config.readConfig().users.find { it.username == "anonymous" }
                if (anonymousUser != null) {
                    val response = json.encodeToString(anonymousUser.permits)
                    call.respondText(response, contentType = ContentType.Text.Plain)
                } else {
                    call.respondText("No user found", contentType = ContentType.Text.Plain, status = HttpStatusCode.NotFound)
                }
            }
        }
    }
}
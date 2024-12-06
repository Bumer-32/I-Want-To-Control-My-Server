package ua.pp.lumivoid.iwtcms.ktor.api.requests

import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respondText
import io.ktor.server.routing.Routing
import io.ktor.server.routing.post
import io.ktor.server.sessions.sessions
import io.ktor.server.sessions.set
import kotlinx.serialization.Serializable
import ua.pp.lumivoid.iwtcms.Constants
import ua.pp.lumivoid.iwtcms.ktor.api.User
import ua.pp.lumivoid.iwtcms.ktor.api.requests.ApiListGET.registerAPI
import ua.pp.lumivoid.iwtcms.ktor.cookie.UserSession
import ua.pp.lumivoid.iwtcms.util.Config

object LoginPOST: Request() {
    override val logger = Constants.EMBEDDED_SERVER_LOGGER
    override val PATH = "/api/login"

    override val request: Routing.() -> Unit = {
        logger.info("Initializing $PATH request")

        registerAPI("LoginPOST", PATH)

        post(PATH) {
            val payload = call.receive<LoginPayload>()

            var user: User? = Config.readConfig().users.find { it.username == payload.username && it.password == payload.password }

            if (user != null) {
                call.sessions.set(UserSession(user.username, user.id))

                call.respondText("Login successful")
            } else {
                call.respondText("Login failed", status = HttpStatusCode.Unauthorized)
            }
        }
    }
}

@Serializable
data class LoginPayload(
    val username: String,
    val password: String
)
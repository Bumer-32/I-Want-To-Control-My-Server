package ua.pp.lumivoid.iwtcms.ktor.api.requests

import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import ua.pp.lumivoid.iwtcms.ktor.api.UserAuthentication.doAuth

object IsAllowedG : Request() {
    override val path = "/api/isAllowed"

    override val request: Routing.() -> Unit = {
        get(path) {
            val payload = call.receive<IsAllowedPayload>()

            doAuth(
                call = call,
                permission = payload.permission,
                success = {
                    runBlocking { call.respond("allowed") }
                },
            )
        }
    }
}

@Serializable
data class IsAllowedPayload(
    val permission: String,
)

package ua.pp.lumivoid.iwtcms.ktor.api.requests

import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import kotlinx.coroutines.runBlocking
import ua.pp.lumivoid.iwtcms.ktor.api.doAuth

object IsAllowed : Request() {
    override val path = "/api/isAllowed/{permission}"

    override val request: Routing.() -> Unit = {
        get(path) {
            if (call.parameters["permission"] == null) {
                call.respond(HttpStatusCode.Unauthorized)
                return@get
            }

            doAuth(
                call = call,
                permission = call.parameters["permission"]!!,
                success = {
                    runBlocking { call.respond("allowed") }
                },
            )
        }
    }
}

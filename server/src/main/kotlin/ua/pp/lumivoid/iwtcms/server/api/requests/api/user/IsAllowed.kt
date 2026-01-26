package ua.pp.lumivoid.iwtcms.server.api.requests.api.user

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ua.pp.lumivoid.iwtcms.server.api.Request
import ua.pp.lumivoid.iwtcms.server.api.doAuth

internal object IsAllowed : Request() {
    override val path = "/api/user/isAllowed/{permission}"

    override val request: Routing.() -> Unit = {
        get(path) {
            if (call.parameters["permission"] == null) {
                call.respond(HttpStatusCode.NotFound, "Permission not found")
                return@get
            }

            doAuth(
                call = call,
                permission = call.parameters["permission"]!!,
                success = {
                    call.respond("allowed")
                },
            )
        }
    }
}
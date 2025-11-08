package ua.pp.lumivoid.iwtcms.server.api.requests.api.user

import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import ua.pp.lumivoid.iwtcms.server.api.doAuth
import ua.pp.lumivoid.iwtcms.server.api.Request

object IsAllowed : Request() {
    override val path = "/api/user/isAllowed/{permission}"

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
                    call.respond("allowed")
                },
            )
        }
    }
}
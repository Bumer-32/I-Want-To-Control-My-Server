package ua.pp.lumivoid.iwtcms.ktor.api

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respondText
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import kotlinx.coroutines.runBlocking
import ua.pp.lumivoid.iwtcms.ktor.cookie.UserSession
import ua.pp.lumivoid.iwtcms.util.Config

object UserAuthentication {

    /*
    * checks if auth enabled and user are logged in (user has cookies)
    * launch success unit, unauthorized unit or forbidden unit
    * if auth disabled launch success unit
    * also works for anonymous
    * to set up permits look at config
    *
    * returns HTTP status:
    * */
    @Suppress("t")
    fun doAuth(
        call: ApplicationCall, permit: String,
        success: () -> Unit, unauthorized: () -> Unit = {
            runBlocking { call.respondText("Unauthorized", status = HttpStatusCode.Unauthorized) }
        },
        forbidden: () -> Unit = {
            runBlocking{ call.respondText("Forbidden", status = HttpStatusCode.Forbidden) }
        }
    ) : HttpStatusCode {
        if (!Config.readConfig().useAuthentication) {
            success()
            return HttpStatusCode.OK
        }

        val session = call.sessions.get<UserSession>()
        val anonymousUser: User? = Config.readConfig().users.find { it.username == "anonymous" }

        if (session == null && anonymousUser != null) {
            if (anonymousUser.permits[permit] == true) {
                success()
                return HttpStatusCode.OK
            } else {
                forbidden()
                return HttpStatusCode.Forbidden
            }
        }

        val user: User? = Config.readConfig().users.find { it.username == session?.name && it.id == session.id }

        if (user != null) {
            println("yyyyy")
            if (user.permits[permit] == true) {
                success()
                return HttpStatusCode.OK
            } else if (user.permits[permit] == false) {
                forbidden()
                return HttpStatusCode.Forbidden
            } else {
                // if permit == null try anonymous
                if (anonymousUser?.permits[permit] == true) {
                    success()
                    return HttpStatusCode.OK
                } else if (user.permits[permit] == false) {
                    forbidden()
                    return HttpStatusCode.Forbidden
                } else {
                    // if even anonymous not found - forbidden
                    forbidden()
                    return HttpStatusCode.Forbidden
                }
            }
        }

        unauthorized()
        return HttpStatusCode.Unauthorized
    }
}
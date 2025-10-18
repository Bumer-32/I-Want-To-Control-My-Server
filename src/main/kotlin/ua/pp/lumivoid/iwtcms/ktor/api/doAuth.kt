package ua.pp.lumivoid.iwtcms.ktor.api

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import kotlinx.coroutines.flow.singleOrNull
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import ua.pp.lumivoid.iwtcms.ktor.cookie.UserSession
import ua.pp.lumivoid.iwtcms.ktor.tables.UserPermissionsTable
import ua.pp.lumivoid.iwtcms.ktor.tables.UsersTable

/*
 * checks if auth enabled and user are logged in (user has cookies)
 * launch success unit, unauthorized unit or forbidden unit
 *
 * returns HTTP status
 * */
suspend fun doAuth(
    call: RoutingCall,
    permission: String,
    success: suspend () -> Unit,
    unauthorized: suspend () -> Unit = {
        call.respond(HttpStatusCode.Unauthorized, "Unauthorized")
    },
    forbidden: suspend () -> Unit = {
        call.respond(HttpStatusCode.Forbidden, "Forbidden")
    },
): HttpStatusCode {
    val session = call.sessions.get<UserSession>()

    if (session == null) {
        unauthorized()
        return HttpStatusCode.Unauthorized
    }

    val status = suspendTransaction {
        val user = UsersTable.selectAll()
            .where { (UsersTable.username eq session.name) and (UsersTable.uniqueId eq session.id) }
            .singleOrNull()

        if (user == null) {
            return@suspendTransaction HttpStatusCode.Unauthorized
        }

        if (user[UsersTable.admin]) {
            return@suspendTransaction HttpStatusCode.OK
        }

        val found = UserPermissionsTable.selectAll()
            .where { (UserPermissionsTable.userId eq user[UsersTable.id]) and (UserPermissionsTable.permissionName eq permission) }
            .singleOrNull()

        if (found != null) {
            return@suspendTransaction HttpStatusCode.OK
        } else {
            return@suspendTransaction HttpStatusCode.Forbidden
        }
    }

    when (status) {
        HttpStatusCode.Unauthorized -> unauthorized()
        HttpStatusCode.Forbidden -> forbidden()
        HttpStatusCode.OK -> success()
    }

    return status
}

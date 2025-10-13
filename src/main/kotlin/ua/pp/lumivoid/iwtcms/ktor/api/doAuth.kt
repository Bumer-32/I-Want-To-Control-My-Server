package ua.pp.lumivoid.iwtcms.ktor.api

import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.RoutingCall
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import org.jetbrains.exposed.v1.core.ResultRow
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

    val user = suspendTransaction  {
        UsersTable
            .selectAll()
            .where { (UsersTable.username eq session.name) and (UsersTable.uniqueId eq session.id) }
            .firstOrNull()
    }

    if (user == null) {
        unauthorized()
        return HttpStatusCode.Unauthorized
    }

    if (user[UsersTable.admin]) {
        success()
        return HttpStatusCode.OK
    }

    val permission: ResultRow =
        try {
            UserPermissionsTable
                .selectAll()
                .where { (UserPermissionsTable.userId eq user[UsersTable.id]) and (UserPermissionsTable.permissionName eq permission) }
                .first()
        } catch (_: NoSuchElementException) {
            forbidden()
            return HttpStatusCode.Forbidden
        }

    if (permission[UserPermissionsTable.permissionState]) {
        success()
        return HttpStatusCode.OK
    } else {
        forbidden()
        return HttpStatusCode.Forbidden
    }
}

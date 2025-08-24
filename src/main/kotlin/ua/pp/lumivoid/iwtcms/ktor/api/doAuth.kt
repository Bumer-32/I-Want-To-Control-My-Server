package ua.pp.lumivoid.iwtcms.ktor.api

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.sessions.*
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
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
    call: ApplicationCall,
    permission: String,
    success: suspend () -> Unit,
    unauthorized: suspend () -> Unit = {
        call.respondText("Unauthorized", status = HttpStatusCode.Unauthorized)
    },
    forbidden: suspend () -> Unit = {
        call.respondText("Forbidden", status = HttpStatusCode.Forbidden)
    },
): HttpStatusCode {
    val session = call.sessions.get<UserSession>()

    if (session == null) {
        unauthorized()
        return HttpStatusCode.Unauthorized
    }

    val user = newSuspendedTransaction {
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

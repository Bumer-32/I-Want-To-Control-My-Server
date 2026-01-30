package ua.pp.lumivoid.iwtcms.server.api

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import ua.pp.lumivoid.iwtcms.server.cookie.UserSession
import ua.pp.lumivoid.iwtcms.server.tables.UserEntity
import ua.pp.lumivoid.iwtcms.server.tables.UserPermissionEntity
import ua.pp.lumivoid.iwtcms.server.tables.UserPermissionsTable
import ua.pp.lumivoid.iwtcms.server.tables.UsersTable

/*
 * checks if auth enabled and user is logged in (user has cookies)
 * launch success unit, unauthorized unit or forbidden unit
 *
 * returns HTTP status
 * */
internal suspend fun doAuth(
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

    val status = withContext(Dispatchers.IO) {
        transaction {
            val user = UserEntity.find { (UsersTable.username eq session.name) and (UsersTable.uniqueId eq session.id) }.singleOrNull()

            if (user == null) {
                return@transaction HttpStatusCode.Unauthorized
            }

            if (user.admin) {
                return@transaction HttpStatusCode.OK
            }

            val found = UserPermissionEntity
                .find { (UserPermissionsTable.user eq user.id) and (UserPermissionsTable.permissionName eq permission) }
                .singleOrNull()

            if (found != null) {
                return@transaction HttpStatusCode.OK
            } else {
                return@transaction HttpStatusCode.Forbidden
            }
        }
    }

    when (status) {
        HttpStatusCode.Unauthorized -> unauthorized()
        HttpStatusCode.Forbidden -> forbidden()
        HttpStatusCode.OK -> success()
    }

    return status
}

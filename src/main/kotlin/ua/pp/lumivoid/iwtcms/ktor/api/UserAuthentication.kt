package ua.pp.lumivoid.iwtcms.ktor.api

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respondText
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import ua.pp.lumivoid.iwtcms.ktor.cookie.UserSession
import ua.pp.lumivoid.iwtcms.ktor.tables.UserPermissions
import ua.pp.lumivoid.iwtcms.ktor.tables.Users

object UserAuthentication {
    /*
     * checks if auth enabled and user are logged in (user has cookies)
     * launch success unit, unauthorized unit or forbidden unit
     *
     * returns HTTP status
     * */
    suspend fun doAuth(
        call: ApplicationCall,
        permission: String,
        success: () -> Unit,
        unauthorized: () -> Unit = {
            runBlocking { call.respondText("Unauthorized", status = HttpStatusCode.Unauthorized) }
        },
        forbidden: () -> Unit = {
            runBlocking { call.respondText("Forbidden", status = HttpStatusCode.Forbidden) }
        },
    ): HttpStatusCode {
        val session = call.sessions.get<UserSession>()

        if (session == null) {
            unauthorized()
            return HttpStatusCode.Unauthorized
        }

        return newSuspendedTransaction {
            val user: ResultRow =
                try {
                    Users
                        .selectAll()
                        .where { Users.username eq session.name }
                        .andWhere { Users.uniqueId eq session.id }
                        .first()
                } catch (_: NoSuchElementException) {
                    unauthorized()
                    return@newSuspendedTransaction HttpStatusCode.Unauthorized
                }

            if (user[Users.admin]) {
                success()
                return@newSuspendedTransaction HttpStatusCode.OK
            }

            val permission: ResultRow =
                try {
                    UserPermissions
                        .selectAll()
                        .where { UserPermissions.userId eq user[Users.id] }
                        .andWhere { UserPermissions.permissionName eq permission }
                        .first()
                } catch (_: NoSuchElementException) {
                    forbidden()
                    return@newSuspendedTransaction HttpStatusCode.Forbidden
                }

            if (permission[UserPermissions.permissionState]) {
                success()
                return@newSuspendedTransaction HttpStatusCode.OK
            } else {
                forbidden()
                return@newSuspendedTransaction HttpStatusCode.Forbidden
            }
        }
    }
}

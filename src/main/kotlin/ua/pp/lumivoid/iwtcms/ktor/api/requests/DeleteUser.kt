package ua.pp.lumivoid.iwtcms.ktor.api.requests

import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respondText
import io.ktor.server.routing.Routing
import io.ktor.server.routing.post
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import ua.pp.lumivoid.iwtcms.ktor.api.PermissionsList
import ua.pp.lumivoid.iwtcms.ktor.api.doAuth
import ua.pp.lumivoid.iwtcms.ktor.tables.UserPermissionsTable
import ua.pp.lumivoid.iwtcms.ktor.tables.UsersTable

object DeleteUser : Request() {
    override val path = "/api/deleteUser"

    override val request: Routing.() -> Unit = {
        post(path) {
            val payload = call.receive<DeleteUserPayload>()

            doAuth(
                call = call,
                permission = PermissionsList.Permission.USERS_MANAGE.value,
                success = {
                    val success = newSuspendedTransaction {
                        val user = UsersTable
                                            .selectAll()
                                            .where { UsersTable.username eq payload.username }
                                            .firstOrNull()

                        if (user == null) return@newSuspendedTransaction false

                        UsersTable.deleteWhere { UsersTable.username eq payload.username }
                        UserPermissionsTable.deleteWhere { UserPermissionsTable.userId eq userId }
                        true
                    }

                    if (success) call.respondText("User deleted")
                    else call.respondText("User not found", status = HttpStatusCode.NotFound)
                },
            )
        }
    }

    @Serializable
    data class DeleteUserPayload(
        val username: String,
    )
}
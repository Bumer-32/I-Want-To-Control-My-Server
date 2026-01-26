package ua.pp.lumivoid.iwtcms.server.api.requests.api.user

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.deleteWhere
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import ua.pp.lumivoid.iwtcms.server.api.Request
import ua.pp.lumivoid.iwtcms.server.api.doAuth
import ua.pp.lumivoid.iwtcms.server.tables.UsersTable

internal object DeleteUser : Request() {
    override val path = "/api/user/deleteUser"

    override val request: Routing.() -> Unit = {
        delete(path) {
            doAuth(
                call = call,
                permission = PermissionsList.Permission.USERS_MANAGE.value,
                success = {
                    val payload = call.receive<DeleteUserPayload>()

                    val success = suspendTransaction {
                        val user = UsersTable.selectAll()
                            .where { UsersTable.username eq payload.username }
                            .singleOrNull()

                        if (user == null) return@suspendTransaction false

                        UsersTable.deleteWhere { UsersTable.username eq payload.username }
                        true
                    }

                    if (success) call.respond("User deleted")
                    else call.respond(HttpStatusCode.NotFound, "User not found")
                },
            )
        }
    }

    @Serializable
    data class DeleteUserPayload(
        val username: String,
    )
}
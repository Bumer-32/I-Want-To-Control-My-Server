package ua.pp.lumivoid.iwtcms.ktor.api.requests

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
import ua.pp.lumivoid.iwtcms.ktor.api.doAuth
import ua.pp.lumivoid.iwtcms.ktor.tables.UsersTable

object DeleteUser : Request() {
    override val path = "/api/deleteUser"

    override val request: Routing.() -> Unit = {
        delete (path) {
            val payload = call.receive<DeleteUserPayload>()

            doAuth(
                call = call,
                permission = PermissionsList.Permission.USERS_MANAGE.value,
                success = {
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
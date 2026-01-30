package ua.pp.lumivoid.iwtcms.server.api.requests.api.user

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import ua.pp.lumivoid.iwtcms.server.api.Request
import ua.pp.lumivoid.iwtcms.server.api.doAuth
import ua.pp.lumivoid.iwtcms.server.tables.UserEntity
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

                    val success = withContext(Dispatchers.IO) {
                        transaction {
                            UserEntity.find { UsersTable.username eq payload.username }.firstOrNull()?.let { it.delete(); true } ?: false
                        }
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
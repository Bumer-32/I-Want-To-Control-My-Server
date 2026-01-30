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
import ua.pp.lumivoid.iwtcms.server.tables.UserPermissionEntity
import ua.pp.lumivoid.iwtcms.server.tables.UsersTable

internal object EditPermissions : Request() {
    override val path = "/api/user/editPermissions"

    override val request: Routing.() -> Unit = {
        put(path) {
            doAuth(
                call = call,
                permission = PermissionsList.Permission.USERS_MANAGE.value,
                success = {
                    val payload = call.receive<EditPermissionsPayload>()

                    val status = withContext(Dispatchers.IO) {
                        transaction {
                            val user = UserEntity.find { UsersTable.username eq payload.username }.singleOrNull() ?: return@transaction HttpStatusCode.NotFound

                            user.admin = payload.admin

                            user.permissions.forEach { it.delete() }

                            payload.permissions.forEach { permission ->
                                UserPermissionEntity.new {
                                    this.user = user
                                    this.permissionName = permission
                                }
                            }

                            HttpStatusCode.OK
                        }
                    }

                    when (status) {
                        HttpStatusCode.NotFound -> call.respond(HttpStatusCode.NotFound, "User not found")
                        HttpStatusCode.OK -> call.respond(HttpStatusCode.OK, "User permissions updated")
                    }
                }
            )
        }
    }

    @Serializable
    data class EditPermissionsPayload(
        val username: String,
        val admin: Boolean,
        val permissions: List<String>,
    )
}
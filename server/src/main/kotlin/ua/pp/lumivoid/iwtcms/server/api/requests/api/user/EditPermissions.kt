package ua.pp.lumivoid.iwtcms.server.api.requests.api.user

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.deleteWhere
import org.jetbrains.exposed.v1.r2dbc.insert
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import ua.pp.lumivoid.iwtcms.server.api.Request
import ua.pp.lumivoid.iwtcms.server.api.doAuth
import ua.pp.lumivoid.iwtcms.server.tables.UserPermissionsTable
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

                    suspendTransaction {
                        val user = UsersTable.selectAll()
                            .where { UsersTable.username eq payload.username }.singleOrNull()

                        if (user == null) {
                            call.respond(HttpStatusCode.NotFound, "User not found")
                            return@suspendTransaction
                        }

                        UserPermissionsTable.deleteWhere { UserPermissionsTable.userId eq user[UsersTable.id] }

                        payload.permissions.forEach { permission ->
                            UserPermissionsTable.insert {
                                it[UserPermissionsTable.userId] = user[UsersTable.id]
                                it[UserPermissionsTable.permissionName] = permission
                            }
                        }

                        call.respond(HttpStatusCode.OK, "User permissions updated")
                    }
                }
            )
        }
    }

    @Serializable
    data class EditPermissionsPayload(
        val username: String,
        val permissions: List<String>,
    )
}
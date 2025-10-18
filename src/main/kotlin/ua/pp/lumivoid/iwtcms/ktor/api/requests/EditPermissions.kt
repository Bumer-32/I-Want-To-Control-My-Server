package ua.pp.lumivoid.iwtcms.ktor.api.requests

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.flow.single
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.deleteWhere
import org.jetbrains.exposed.v1.r2dbc.insert
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import ua.pp.lumivoid.iwtcms.ktor.api.doAuth
import ua.pp.lumivoid.iwtcms.ktor.tables.UserPermissionsTable
import ua.pp.lumivoid.iwtcms.ktor.tables.UsersTable

object EditPermissions : Request() {
    override val path = "/api/editPermissions"

    override val request: Routing.() -> Unit = {
        put(path) {
            val payload = call.receive<EditPermissionPayload>()

            doAuth(
                call = call,
                permission = PermissionsList.Permission.USERS_MANAGE.value,
                success = {
                    suspendTransaction {
                        val user = UsersTable.selectAll()
                            .where { UsersTable.username eq payload.username }.single()

                        UserPermissionsTable.deleteWhere { userId eq user[UsersTable.id] }

                        payload.permissions.forEach { permission ->
                            UserPermissionsTable.insert {
                                it[UserPermissionsTable.userId] = user[UsersTable.id]
                                it[UserPermissionsTable.permissionName] = permission
                            }
                        }
                    }

                    call.respond(HttpStatusCode.OK, "User permissions updated")
                }
            )
        }
    }

    @Serializable
    private data class EditPermissionPayload(
        val username: String,
        val permissions: List<String>,
    )
}
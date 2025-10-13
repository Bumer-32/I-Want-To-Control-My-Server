package ua.pp.lumivoid.iwtcms.ktor.api.requests

import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.post
import kotlinx.coroutines.flow.first
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import org.jetbrains.exposed.v1.r2dbc.update
import ua.pp.lumivoid.iwtcms.ktor.api.PermissionsList
import ua.pp.lumivoid.iwtcms.ktor.api.doAuth
import ua.pp.lumivoid.iwtcms.ktor.tables.UserPermissionsTable
import ua.pp.lumivoid.iwtcms.ktor.tables.UsersTable

object EditPermissions : Request() {
    override val path = "/api/editPermissions"

    override val request: Routing.() -> Unit = {
        post(path) {
            val payload = call.receive<EditPermissionPayload>()

            doAuth(
                call = call,
                permission = PermissionsList.Permission.USERS_MANAGE.value,
                success = {
                    val result: TransactionState = suspendTransaction {
                        val userId: Int = try {
                            UsersTable
                                .selectAll()
                                .where { UsersTable.username eq payload.username }
                                .first()[UsersTable.id]
                        } catch (_: NoSuchElementException) {
                            return@suspendTransaction TransactionState.USER_NOT_FOUND
                        }

                        try {
                            payload.permissions.forEach { (key, value) ->
                                UserPermissionsTable.update({ (UserPermissionsTable.userId eq userId) and (UserPermissionsTable.permissionName eq key) }) {
                                    it[UserPermissionsTable.permissionState] = value
                                }
                            }
                            return@suspendTransaction TransactionState.SUCCESS
                        } catch (_: NoSuchElementException) {
                            return@suspendTransaction TransactionState.PERMISSION_NOT_FOUND
                        }
                    }


                    when (result) {
                        TransactionState.PERMISSION_NOT_FOUND -> call.respond(HttpStatusCode.NotFound, "Permission not found")
                        TransactionState.USER_NOT_FOUND -> call.respond(HttpStatusCode.NotFound, "User not found")
                        TransactionState.SUCCESS -> call.respond(HttpStatusCode.OK, "User permissions updated")
                    }
                }
            )
        }
    }

    @Serializable
    data class EditPermissionPayload(
        val username: String,
        val permissions: Map<String, Boolean>,
    )

    private enum class TransactionState {
        USER_NOT_FOUND, PERMISSION_NOT_FOUND, SUCCESS
    }

}
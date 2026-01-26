package ua.pp.lumivoid.iwtcms.server.api.requests.api.user

import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import ua.pp.lumivoid.iwtcms.server.api.Request
import ua.pp.lumivoid.iwtcms.server.api.doAuth
import ua.pp.lumivoid.iwtcms.server.tables.UserPermissionsTable
import ua.pp.lumivoid.iwtcms.server.tables.UsersTable

internal object UsersList: Request() {
    override val path = "/api/user/usersList"
    private val json = Json { prettyPrint = true }

    override val request: Routing.() -> Unit = {
        get(path) {
            doAuth(
                call = call,
                permission = PermissionsList.Permission.USERS_MANAGE.value,
                success = {
                    val usersList = suspendTransaction {
                        val usersList = mutableListOf<UsersListPayload>()

                        UsersTable.selectAll().collect { user: ResultRow ->
                            val permissions = UserPermissionsTable.selectAll()
                                .where { UserPermissionsTable.userId eq user[UsersTable.id] }
                                .map { it[UserPermissionsTable.permissionName] }
                                .toList()

                            usersList.add(
                                UsersListPayload(
                                    id = user[UsersTable.id],
                                    username = user[UsersTable.username],
                                    admin = user[UsersTable.admin],
                                    permissions = permissions
                                )
                            )
                        }

                        return@suspendTransaction usersList
                    }
                    call.respond(json.encodeToString(usersList))
                },
            )
        }
    }

    @Serializable
    data class UsersListPayload(
        val id: Int,
        val username: String,
        val admin: Boolean,
        val permissions: List<String>
    )
}
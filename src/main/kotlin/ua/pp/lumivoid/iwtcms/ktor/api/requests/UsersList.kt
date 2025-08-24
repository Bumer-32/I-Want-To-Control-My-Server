package ua.pp.lumivoid.iwtcms.ktor.api.requests

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import ua.pp.lumivoid.iwtcms.ktor.api.PermissionsList
import ua.pp.lumivoid.iwtcms.ktor.api.doAuth
import ua.pp.lumivoid.iwtcms.ktor.tables.UserPermissionsTable
import ua.pp.lumivoid.iwtcms.ktor.tables.UsersTable

object UsersList: Request() {
    override val path = "/api/usersList"
    private val json = Json { prettyPrint = true }
    private val falsePermissionsMap = mutableMapOf<String, Boolean>()

    init {
        PermissionsList.getPermissionsList().forEach { permission ->
            falsePermissionsMap[permission] = false
        }
    }

    override val request: Routing.() -> Unit = {
        get(path) {
            doAuth(
                call = call,
                permission = PermissionsList.Permission.USERS_MANAGE.value,
                success = {
                    val usersList = newSuspendedTransaction {
                        val usersList = mutableListOf<UsersListData>()
                        UsersTable.selectAll().forEach { user: ResultRow ->
                            val permissions = UserPermissionsTable.selectAll().where { UserPermissionsTable.userId eq user[UsersTable.id] }
                            val permissionsMap = falsePermissionsMap

                            permissions.forEach { permission: ResultRow ->
                                permissionsMap[permission[UserPermissionsTable.permissionName]] = permission[UserPermissionsTable.permissionState]
                            }

                            usersList.add(UsersListData(id = user[UsersTable.id], username = user[UsersTable.username], admin = user[UsersTable.admin], permissions = permissionsMap))
                        }

                        return@newSuspendedTransaction usersList
                    }
                    call.respondText(json.encodeToString(usersList), contentType = ContentType.Text.Plain)
                },
            )
        }
    }

    @Serializable
    data class UsersListData(
        val id: Int,
        val username: String,
        val admin: Boolean,
        val permissions: MutableMap<String, Boolean>
    )
}
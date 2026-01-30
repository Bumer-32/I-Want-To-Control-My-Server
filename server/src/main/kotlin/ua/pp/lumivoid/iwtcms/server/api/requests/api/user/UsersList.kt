package ua.pp.lumivoid.iwtcms.server.api.requests.api.user

import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import ua.pp.lumivoid.iwtcms.server.api.Request
import ua.pp.lumivoid.iwtcms.server.api.doAuth
import ua.pp.lumivoid.iwtcms.server.tables.UserEntity

internal object UsersList: Request() {
    override val path = "/api/user/usersList"
    private val json = Json { prettyPrint = true }

    override val request: Routing.() -> Unit = {
        get(path) {
            doAuth(
                call = call,
                permission = PermissionsList.Permission.USERS_MANAGE.value,
                success = {
                    val usersList = withContext(Dispatchers.IO) {
                        transaction {
                            UserEntity.all().toList().map { UsersListPayload(it.id.value, it.username, it.admin, it.permissions.map { p -> p.permissionName }) }
                        }
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
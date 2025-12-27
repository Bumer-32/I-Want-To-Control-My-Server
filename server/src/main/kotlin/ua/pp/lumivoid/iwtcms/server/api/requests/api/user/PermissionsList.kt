package ua.pp.lumivoid.iwtcms.server.api.requests.api.user

import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import kotlinx.serialization.json.Json
import ua.pp.lumivoid.iwtcms.server.api.doAuth
import ua.pp.lumivoid.iwtcms.server.api.Request

internal object PermissionsList: Request() {
    override val path = "/api/user/permissionsList"
    private val json = Json { prettyPrint = true }

    private var permissions: MutableList<String> = mutableListOf()

    init {
        permissions = Permission.entries.map { it.value }.toMutableList()
    }

    override val request: Routing.() -> Unit = {
        get(path) {
            doAuth(
                call = call,
                permission = Permission.USERS_MANAGE.value,
                success = {
                    call.respond(json.encodeToString(permissions))
                }
            )
        }
    }

    fun createPermission(permission: String) {
        permissions.add(permission)
    }

    enum class Permission(val value: String) {
        LOGS_READ("logs.read"),
        COMMANDS_EXECUTE("commands.execute"),
        SERVER_STATS_READ("server.stats.read"),
        PLAYERS_MANAGE("players.manage"),
        USERS_MANAGE("users.manage"),
    }
}
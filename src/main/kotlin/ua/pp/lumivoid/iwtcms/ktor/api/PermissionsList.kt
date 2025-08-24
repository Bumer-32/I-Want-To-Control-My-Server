package ua.pp.lumivoid.iwtcms.ktor.api

import ua.pp.lumivoid.iwtcms.Constants

object PermissionsList {
    private var permissions: MutableList<String> = mutableListOf()

    init {
        permissions = Permission.entries.map { it.value }.toMutableList()
    }

    fun getPermissionsList(): List<String> {
        return permissions
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
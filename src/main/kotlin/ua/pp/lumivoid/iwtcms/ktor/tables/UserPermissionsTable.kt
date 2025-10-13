package ua.pp.lumivoid.iwtcms.ktor.tables

import org.jetbrains.exposed.v1.core.Table

object UserPermissionsTable : Table("user_permissions") {
    val userId = integer("user_id")
    val permissionName = varchar("permission_name", 128)
    val permissionState = bool("permission_state")

    init {
        uniqueIndex(userId, permissionName)
    }
}

package ua.pp.lumivoid.iwtcms.server.tables

import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.Table

internal object UserPermissionsTable: Table("user_permissions") {
    val id = integer("id").autoIncrement()
    val userId = reference("user_id", UsersTable.id, onDelete = ReferenceOption.CASCADE)
    val permissionName = varchar("permission_name", 312)

    override val primaryKey = PrimaryKey(id)

    init {
        uniqueIndex(userId, permissionName) // user can't have 2 similar permissions
    }
}

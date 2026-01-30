package ua.pp.lumivoid.iwtcms.server.tables

import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass

internal object UserPermissionsTable: IntIdTable("user_permissions") {
    val user = reference("user_id", UsersTable, onDelete = ReferenceOption.CASCADE)
    val permissionName = varchar("permission_name", 312)

    init {
        uniqueIndex(user, permissionName) // user can't have 2 similar permissions
    }
}

internal class UserPermissionEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserPermissionEntity>(UserPermissionsTable)

    var user by UserEntity referencedOn UserPermissionsTable.user
    var permissionName by UserPermissionsTable.permissionName
}

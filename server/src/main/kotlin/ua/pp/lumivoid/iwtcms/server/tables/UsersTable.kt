package ua.pp.lumivoid.iwtcms.server.tables

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass

internal object UsersTable: IntIdTable("users") {
    val username = varchar("username", 128).uniqueIndex()
    val passwordHash = char("password_hash", 64)
    val salt = char("salt", 32).uniqueIndex()
    val uniqueId = char("unique_id", 64).uniqueIndex()
    val admin = bool("admin").default(false)
}

internal class UserEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserEntity>(UsersTable)

    var username by UsersTable.username
    var passwordHash by UsersTable.passwordHash
    var salt by UsersTable.salt
    var uniqueId by UsersTable.uniqueId
    var admin by UsersTable.admin

    val permissions by UserPermissionEntity referrersOn UserPermissionsTable.user
}
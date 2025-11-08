package ua.pp.lumivoid.iwtcms.server.tables

import org.jetbrains.exposed.v1.core.Table

object UsersTable : Table("users") {
    val id = integer("id").autoIncrement()
    val username = varchar("username", 128).uniqueIndex()
    val passwordHash = char("password_hash", 64)
    val salt = char("salt", 32).uniqueIndex()
    val uniqueId = char("unique_id", 64).uniqueIndex()
    val admin = bool("admin").default(false)

    override val primaryKey = PrimaryKey(id)
}
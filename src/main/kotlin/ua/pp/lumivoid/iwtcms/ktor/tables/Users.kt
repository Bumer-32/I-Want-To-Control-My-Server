package ua.pp.lumivoid.iwtcms.ktor.tables

import org.jetbrains.exposed.sql.Table

object Users : Table("users") {
    val id = integer("id").autoIncrement()
    val username = varchar("username", 128).uniqueIndex()
    val passwordHash = char("password_hash", 64)
    val salt = char("salt", 32).uniqueIndex()
    val uniqueId = char("unique_id", 64).uniqueIndex()
    val admin = bool("admin").default(false)
}

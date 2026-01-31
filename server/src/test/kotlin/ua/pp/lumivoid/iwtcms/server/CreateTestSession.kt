package ua.pp.lumivoid.iwtcms.server

import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import ua.pp.lumivoid.iwtcms.server.cookie.UserSession
import ua.pp.lumivoid.iwtcms.server.tables.UserEntity
import ua.pp.lumivoid.iwtcms.server.tables.UsersTable

internal fun createTestSession() = transaction {
    val id = UserEntity.find { UsersTable.username eq "admin" }.single().uniqueId
    UserSession("admin", id)
}
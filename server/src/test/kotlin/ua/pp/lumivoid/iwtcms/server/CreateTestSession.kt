package ua.pp.lumivoid.iwtcms.server

import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import ua.pp.lumivoid.iwtcms.server.tables.UserEntity
import ua.pp.lumivoid.iwtcms.server.tables.UsersTable

internal fun createTestSession() = transaction {
    UserEntity.find { UsersTable.username eq "admin" }.single().uniqueId
}
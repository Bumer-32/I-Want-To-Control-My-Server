package ua.pp.lumivoid.iwtcms.server

import kotlinx.coroutines.flow.single
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import ua.pp.lumivoid.iwtcms.server.cookie.UserSession
import ua.pp.lumivoid.iwtcms.server.tables.UsersTable

internal fun createTestSession() = runBlocking {
    val adminUniqueId = suspendTransaction {
        UsersTable
            .selectAll()
            .where { UsersTable.username eq "admin" }
            .single()[UsersTable.uniqueId]
    }

    return@runBlocking UserSession("admin", adminUniqueId)
}
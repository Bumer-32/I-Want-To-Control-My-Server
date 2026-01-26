package ua.pp.lumivoid.iwtcms.server

import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import org.jetbrains.exposed.v1.r2dbc.update
import ua.pp.lumivoid.iwtcms.server.tables.UsersTable

internal fun verifyTestUserRole(admin: Boolean) = runBlocking {
    suspendTransaction {
        UsersTable.update({ UsersTable.username eq "admin" }) {
            it[UsersTable.admin] = admin
        }
    }
}
package ua.pp.lumivoid.iwtcms.server

import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.v1.core.StdOutSqlLogger
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabase
import org.jetbrains.exposed.v1.r2dbc.SchemaUtils
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import ua.pp.lumivoid.iwtcms.server.api.requests.api.user.CreateUser
import ua.pp.lumivoid.iwtcms.server.tables.UserPermissionsTable
import ua.pp.lumivoid.iwtcms.server.tables.UsersTable

fun connectToTestDb() {
    val url = "r2dbc:h2:mem:///test;MODE=MYSQL;DB_CLOSE_DELAY=-1"

    R2dbcDatabase.connect(url = url, driver = "h2")

    runBlocking {
        suspendTransaction {
            addLogger(StdOutSqlLogger)

            SchemaUtils.create(
                UsersTable,
                UserPermissionsTable
            )

            CreateUser.create("admin", "iwtcms", true, emptyList())
        }
    }
}
